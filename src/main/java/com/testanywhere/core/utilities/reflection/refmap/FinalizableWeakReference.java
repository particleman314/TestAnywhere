package com.testanywhere.core.utilities.reflection.refmap;

import java.lang.ref.WeakReference;

/**
 * Weak reference with a {@code finalizeReferent()} method which a background
 * thread invokes after the garbage collector reclaims the referent. This is a
 * simpler alternative to using a {@link java.lang.ref.ReferenceQueue}.
 *
 */
public abstract class FinalizableWeakReference<T> extends WeakReference<T>
    implements FinalizableReference {

  /**
   * Consructs a new finalizable weak reference.
   *
   * @param referent to weakly reference
   * @param queue that should finalize the referent
   */
  protected FinalizableWeakReference(T referent,
      FinalizableReferenceQueue queue) {
    super(referent, queue.queue);
  }
}
