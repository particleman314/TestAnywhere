package com.testanywhere.core.utilities.reflection.refmap;

/**
 * Implemented by references that have code to run after garbage collection of
 * their referents.
 *
 * @see FinalizableReferenceQueue
 */
public interface FinalizableReference {

  /**
   * Invoked on a background thread after the referent has been garbage
   * collected.
   */
  void finalizeReferent();
}
