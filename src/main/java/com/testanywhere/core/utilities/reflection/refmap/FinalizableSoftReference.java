package com.testanywhere.core.utilities.reflection.refmap;

import java.lang.ref.SoftReference;

/**
 * Soft reference with a {@code finalizeReferent()} method which a background
 * thread invokes after the garbage collector reclaims the referent. This is a
 * simpler alternative to using a {@link java.lang.ref.ReferenceQueue}.
 *
 */
public abstract class FinalizableSoftReference<T> extends SoftReference<T>
    implements FinalizableReference {

  /**
   * Consructs a new finalizable soft reference.
   *
   * @param referent to softly reference
   * @param queue that should finalize the referent
   */
  protected FinalizableSoftReference(T referent,
      FinalizableReferenceQueue queue) {
    super(referent, queue.queue);
  }
}
