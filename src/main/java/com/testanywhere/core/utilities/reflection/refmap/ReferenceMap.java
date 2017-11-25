package com.testanywhere.core.utilities.reflection.refmap;

import static com.testanywhere.core.utilities.reflection.refmap.ReferenceType.STRONG;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Concurrent hash map that wraps keys and/or values in soft or weak
 * references. Does not support null keys or values. Uses identity equality
 * for weak and soft keys.
 *
 * <p>The concurrent semantics of {@link ConcurrentHashMap} combined with the
 * fact that the garbage collector can asynchronously reclaim and clean up
 * after keys and values at any time can lead to some racy semantics. For
 * example, {@link #size()} returns an upper bound on the size, i.e. the actual
 * size may be smaller in cases where the key or value has been reclaimed but
 * the map entry has not been cleaned up yet.
 *
 * <p>Another example: If {@link #get(Object)} cannot find an existing entry
 * for a key, it will try to create one. This operation is not atomic. One
 * thread could {@link #put(Object, Object)} a value between the time another
 * thread running {@code get()} checks for an entry and decides to create one.
 * In this case, the newly created value will replace the put value in the
 * map. Also, two threads running {@code get()} concurrently can potentially
 * create duplicate values for a given key.
 *
 * <p>In other words, this class is great for caching but not atomicity.
 * 
 * <p>To determine equality to a key, this implementation uses
 * {@link Object#equals} for strong references, and identity-based equality for
 * soft and weak references. In other words, for a map with weak or soft key
 * references, {@link #get} returns {@code null} when passed an object that
 * equals a map key, but isn't the same instance. This behavior is similar to
 * the way {@link IdentityHashMap} handles key lookups. However, to determine
 * value equality, as occurs when {@link #containsValue} is called, the
 * {@code ReferenceMap} always uses {@code equals}, regardless of the value
 * reference type.
 *
 * <p><b>Note:</b> {@code new ReferenceMap(WEAK, STRONG)} is very nearly a
 * drop-in replacement for {@link WeakHashMap}, but improves upon this by using
 * only identity-based equality for keys. When possible, {@code ReferenceMap}
 * should be preferred over the JDK collection, for its concurrency and greater
 * flexibility.
 * 
 */
@SuppressWarnings("unchecked")
public class ReferenceMap<K, V> implements Map<K, V>, Serializable {

  private static final long serialVersionUID = 0;

  transient ConcurrentMap<Object, Object> delegate;

  final ReferenceType keyReferenceType;
  final ReferenceType valueReferenceType;

  /**
   * Concurrent hash map that wraps keys and/or values based on specified
   * reference types.
   *
   * @param keyReferenceType key reference type
   * @param valueReferenceType value reference type
   */
  public ReferenceMap(ReferenceType keyReferenceType,
      ReferenceType valueReferenceType) {
    ensureNotNull(keyReferenceType, valueReferenceType);

    if (keyReferenceType == ReferenceType.PHANTOM
        || valueReferenceType == ReferenceType.PHANTOM) {
      throw new IllegalArgumentException("Phantom references not supported.");
    }

    this.delegate = new ConcurrentHashMap<Object, Object>();
    this.keyReferenceType = keyReferenceType;
    this.valueReferenceType = valueReferenceType;
  }

  V internalGet(K key) {
    Object valueReference = delegate.get(makeKeyReferenceAware(key));
    return valueReference == null
        ? null
        : (V) dereferenceValue(valueReference);
  }

  public V get(final Object key) {
    ensureNotNull(key);
    return internalGet((K) key);
  }

  V execute(Strategy strategy, K key, V value) {
    ensureNotNull(key, value);
    Object keyReference = referenceKey(key);
    Object valueReference = strategy.execute(
      this,
      keyReference,
      referenceValue(keyReference, value)
    );
    return valueReference == null ? null
        : (V) dereferenceValue(valueReference);
  }

  public V put(K key, V value) {
    return execute(putStrategy(), key, value);
  }

  public V remove(Object key) {
    ensureNotNull(key);
    Object referenceAwareKey = makeKeyReferenceAware(key);
    Object valueReference = delegate.remove(referenceAwareKey);
    return valueReference == null ? null
        : (V) dereferenceValue(valueReference);
  }

  public int size() {
    return delegate.size();
  }

  public boolean isEmpty() {
    return delegate.isEmpty();
  }

  public boolean containsKey(Object key) {
    ensureNotNull(key);
    Object referenceAwareKey = makeKeyReferenceAware(key);
    return delegate.containsKey(referenceAwareKey);
  }

  public boolean containsValue(Object value) {
    ensureNotNull(value);
    for (Object valueReference : delegate.values()) {
      if (value.equals(dereferenceValue(valueReference))) {
        return true;
      }
    }
    return false;
  }

  public void putAll(Map<? extends K, ? extends V> t) {
    for (Map.Entry<? extends K, ? extends V> entry : t.entrySet()) {
      put(entry.getKey(), entry.getValue());
    }
  }

  public void clear() {
    delegate.clear();
  }

  /**
   * Returns an unmodifiable set view of the keys in this map. As this method
   * creates a defensive copy, the performance is O(n).
   */
  public Set<K> keySet() {
    return Collections.unmodifiableSet(
        dereferenceKeySet(delegate.keySet()));
  }

  /**
   * Returns an unmodifiable set view of the values in this map. As this
   * method creates a defensive copy, the performance is O(n).
   */
  public Collection<V> values() {
    return Collections.unmodifiableCollection(
        dereferenceValues(delegate.values()));
  }

  public V putIfAbsent(K key, V value) {
    // TODO (crazybob) if the value has been gc'ed but the entry hasn't been
    // cleaned up yet, this put will fail.
    return execute(putIfAbsentStrategy(), key, value);
  }

  public boolean remove(Object key, Object value) {
    ensureNotNull(key, value);
    Object referenceAwareKey = makeKeyReferenceAware(key);
    Object referenceAwareValue = makeValueReferenceAware(value);
    return delegate.remove(referenceAwareKey, referenceAwareValue);
  }

  public boolean replace(K key, V oldValue, V newValue) {
    ensureNotNull(key, oldValue, newValue);
    Object keyReference = referenceKey(key);

    Object referenceAwareOldValue = makeValueReferenceAware(oldValue);
    return delegate.replace(
      keyReference,
      referenceAwareOldValue,
      referenceValue(keyReference, newValue)
    );
  }

  public V replace(K key, V value) {
    // TODO (crazybob) if the value has been gc'ed but the entry hasn't been
    // cleaned up yet, this will succeed when it probably shouldn't.
    return execute(replaceStrategy(), key, value);
  }

  /**
   * Returns an unmodifiable set view of the entries in this map. As this
   * method creates a defensive copy, the performance is O(n).
   */
  public Set<Map.Entry<K, V>> entrySet() {
    Set<Map.Entry<K, V>> entrySet = new HashSet<Map.Entry<K, V>>();
    for (Map.Entry<Object, Object> entry : delegate.entrySet()) {
      Map.Entry<K, V> dereferenced = dereferenceEntry(entry);
      if (dereferenced != null) {
        entrySet.add(dereferenced);
      }
    }
    return Collections.unmodifiableSet(entrySet);
  }

  /**
   * Dereferences an entry. Returns null if the key or value has been gc'ed.
   */
  Entry dereferenceEntry(Map.Entry<Object, Object> entry) {
    K key = dereferenceKey(entry.getKey()); 
    V value = dereferenceValue(entry.getValue());
    return (key == null || value == null)
        ? null
        : new Entry(key, value);
  }

  /**
   * Creates a reference for a key.
   */
  Object referenceKey(K key) {
    switch (keyReferenceType) {
      case STRONG: return key;
      case SOFT: return new SoftKeyReference(key);
      case WEAK: return new WeakKeyReference(key);
      default: throw new AssertionError();
    }
  }

  /**
   * Converts a reference to a key.
   */
  K dereferenceKey(Object o) {
    return (K) dereference(keyReferenceType, o);
  }

  /**
   * Converts a reference to a value.
   */
  V dereferenceValue(Object o) {
    return (V) dereference(valueReferenceType, o);
  }

  /**
   * Returns the refererent for reference given its reference type.
   */
  Object dereference(ReferenceType referenceType, Object reference) {
    return referenceType == STRONG ? reference : ((Reference) reference).get();
  }

  /**
   * Creates a reference for a value.
   */
  Object referenceValue(Object keyReference, Object value) {
    switch (valueReferenceType) {
      case STRONG: return value;
      case SOFT: return new SoftValueReference(keyReference, value);
      case WEAK: return new WeakValueReference(keyReference, value);
      default: throw new AssertionError();
    }
  }

  /**
   * Dereferences a set of key references.
   */
  Set<K> dereferenceKeySet(Set keyReferences) {
    return keyReferenceType == STRONG
        ? keyReferences
        : dereferenceCollection(keyReferenceType, keyReferences, new HashSet());
  }

  /**
   * Dereferences a collection of value references.
   */
  Collection<V> dereferenceValues(Collection valueReferences) {
    return valueReferenceType == STRONG
        ? valueReferences
        : dereferenceCollection(valueReferenceType, valueReferences,
            new ArrayList(valueReferences.size()));
  }

  /**
   * Wraps key so it can be compared to a referenced key for equality.
   */
  Object makeKeyReferenceAware(Object o) {
    return keyReferenceType == STRONG ? o : new KeyReferenceAwareWrapper(o);
  }

  /**
   * Wraps value so it can be compared to a referenced value for equality.
   */
  Object makeValueReferenceAware(Object o) {
    return valueReferenceType == STRONG ? o : new ReferenceAwareWrapper(o);
  }

  /**
   * Dereferences elements in {@code in} using
   * {@code referenceType} and puts them in {@code out}. Returns
   * {@code out}.
   */
  <T extends Collection<Object>> T dereferenceCollection(
      ReferenceType referenceType, T in, T out) {
    for (Object reference : in) {
      out.add(dereference(referenceType, reference));
    }
    return out;
  }

  static int keyHashCode(Object key) {
    return System.identityHashCode(key);
  }

  /**
   * Tests weak and soft references for identity equality. Compares references
   * to other references and wrappers. If o is a reference, this returns true
   * if r == o or if r and o reference the same non null object. If o is a
   * wrapper, this returns true if r's referent is identical to the wrapped
   * object.
   */
  static boolean referenceEquals(Reference r, Object o) {
    // compare reference to reference.
    if (o instanceof InternalReference) {
      // are they the same reference? used in cleanup.
      if (o == r) {
        return true;
      }

      // do they reference identical values? used in conditional puts.
      Object referent = ((Reference) o).get();
      return referent != null && referent == r.get();
    }

    // is the wrapped object identical to the referent? used in lookups.
    return ((ReferenceAwareWrapper) o).unwrap() == r.get();
  }

  /**
   * Big hack. Used to compare keys and values to referenced keys and values
   * without creating more references.
   */
  static class ReferenceAwareWrapper {

    final Object wrapped;

    ReferenceAwareWrapper(Object wrapped) {
      this.wrapped = wrapped;
    }

    Object unwrap() {
      return wrapped;
    }

    public int hashCode() {
      return wrapped.hashCode();
    }

    public boolean equals(Object obj) {
      // defer to reference's equals() logic.
      return obj == null ? false : obj.equals(this);
    }
  }

  /**
   * Used for keys. Overrides hash code to use identity hash code.
   */
  static class KeyReferenceAwareWrapper extends ReferenceAwareWrapper {

    public KeyReferenceAwareWrapper(Object wrapped) {
      super(wrapped);
    }

    @Override public int hashCode() {
      return System.identityHashCode(wrapped);
    }
    @Override public boolean equals(Object arg0) {
        return super.equals(arg0);
    }
  }
  /**
   * Lazy initialization holder for finalizable reference queue.
   */
  private static class ReferenceQueue {
    private static final FinalizableReferenceQueue instance
        = new FinalizableReferenceQueue();
  }

  /*
   * Marker interface to differentiate external and internal references. Also
   * duplicates finalizeReferent() and Reference.get() for internal use.
   */
  private interface InternalReference {
    void finalizeReferent();
    Object get();
  }

  private class SoftKeyReference extends FinalizableSoftReference<Object>
      implements InternalReference {
    final int hashCode;

    SoftKeyReference(Object key) {
      super(key, ReferenceQueue.instance);
      hashCode = System.identityHashCode(key);
    }
    public void finalizeReferent() {
      delegate.remove(this);
    }
    @Override public int hashCode() {
      return hashCode;
    }
    @Override public boolean equals(Object object) {
      return referenceEquals(this, object);
    }
  }

  private class SoftValueReference extends FinalizableSoftReference<Object>
      implements InternalReference {
    final Object keyReference;

    SoftValueReference(Object keyReference, Object value) {
      super(value, ReferenceQueue.instance);
      this.keyReference = keyReference;
    }
    public void finalizeReferent() {
      delegate.remove(keyReference, this);
    }
    @Override public int hashCode() {
      // It's hard to define a useful hash code, so we're careful not to use it.
      throw new AssertionError("don't hash me");
    }
    @Override public boolean equals(Object obj) {
      return referenceEquals(this, obj);
    }
  }

  /*
   * WeakKeyReference/WeakValueReference are absolutely identical to
   * SoftKeyReference/SoftValueReference except for which classes they extend.
   */

  private class WeakKeyReference extends FinalizableWeakReference<Object>
      implements InternalReference {
    final int hashCode;

    WeakKeyReference(Object key) {
      super(key, ReferenceQueue.instance);
      hashCode = System.identityHashCode(key);
    }
    public void finalizeReferent() {
      delegate.remove(this);
    }
    @Override public int hashCode() {
      return hashCode;
    }
    @Override public boolean equals(Object object) {
      return referenceEquals(this, object);
    }
  }

  private class WeakValueReference extends FinalizableWeakReference<Object>
      implements InternalReference {
    final Object keyReference;

    WeakValueReference(Object keyReference, Object value) {
      super(value, ReferenceQueue.instance);
      this.keyReference = keyReference;
    }
    public void finalizeReferent() {
      delegate.remove(keyReference, this);
    }
    @Override public int hashCode() {
      // It's hard to define a useful hash code, so we're careful not to use it.
      throw new AssertionError("don't hash me");
    }
    @Override public boolean equals(Object obj) {
      return referenceEquals(this, obj);
    }
  }

  protected interface Strategy {
    public Object execute(ReferenceMap map, Object keyReference,
        Object valueReference);
  }

  protected Strategy putStrategy() {
    return PutStrategy.PUT;
  }

  protected Strategy putIfAbsentStrategy() {
    return PutStrategy.PUT_IF_ABSENT;
  }

  protected Strategy replaceStrategy() {
    return PutStrategy.REPLACE;
  }

  protected enum PutStrategy implements Strategy {
    PUT {
      public Object execute(ReferenceMap map, Object keyReference,
          Object valueReference) {
        return map.delegate.put(keyReference, valueReference);
      }
    },

    REPLACE {
      public Object execute(ReferenceMap map, Object keyReference,
          Object valueReference) {
        return map.delegate.replace(keyReference, valueReference);
      }
    },

    PUT_IF_ABSENT {
      public Object execute(ReferenceMap map, Object keyReference,
          Object valueReference) {
        return map.delegate.putIfAbsent(keyReference, valueReference);
      }
    };
  };

  private static PutStrategy defaultPutStrategy;
  public static void setDefaultPutStrategy(PutStrategy defaultPutStrategy) {
      ReferenceMap.defaultPutStrategy = defaultPutStrategy;
  }
  protected PutStrategy getPutStrategy() {
    return defaultPutStrategy;
  }


  class Entry implements Map.Entry<K, V> {

    final K key;
    final V value;

    public Entry(K key, V value) {
      this.key = key;
      this.value = value;
    }

    public K getKey() {
      return this.key;
    }

    public V getValue() {
      return this.value;
    }

    public V setValue(V value) {
      return put(key, value);
    }

    public int hashCode() {
      return key.hashCode() * 31 + value.hashCode();
    }

    public boolean equals(Object o) {
      if (!(o instanceof ReferenceMap.Entry)) {
        return false;
      }

      Entry entry = (Entry) o;
      return key.equals(entry.key) && value.equals(entry.value);
    }

    public String toString() {
      return key + "=" + value;
    }
  }

  static void ensureNotNull(Object o) {
    if (o == null) {
      throw new NullPointerException();
    }
  }

  static void ensureNotNull(Object... array) {
    for (int i = 0; i < array.length; i++) {
      if (array[i] == null) {
        throw new NullPointerException("Argument #" + i + " is null.");
      }
    }
  }

  private void writeObject(ObjectOutputStream out) throws IOException  {
    out.defaultWriteObject();
    out.writeInt(size());
    for (Map.Entry<Object, Object> entry : delegate.entrySet()) {
      Object key = dereferenceKey(entry.getKey());
      Object value = dereferenceValue(entry.getValue());

      // don't persist gc'ed entries.
      if (key != null && value != null) {
        out.writeObject(key);
        out.writeObject(value);
      }
    }
    out.writeObject(null);
  }

  private void readObject(ObjectInputStream in) throws IOException,
      ClassNotFoundException {
    in.defaultReadObject();
    int size = in.readInt();
    this.delegate = new ConcurrentHashMap<Object, Object>(size);
    while (true) {
      K key = (K) in.readObject();
      if (key == null) {
        break;
      }
      V value = (V) in.readObject();
      put(key, value);
    }
  }

}
