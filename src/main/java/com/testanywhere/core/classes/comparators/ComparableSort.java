package com.testanywhere.core.classes.comparators;

import java.util.List;

public abstract class ComparableSort<T extends Comparable<T>>
{
	protected boolean less (final T v1, final T v2)
	{
		return v1.compareTo(v2) < 0;
	}

	protected boolean greater (final T v1, final T v2)
	{
		return v1.compareTo(v2) > 0;
	}

	// Global swap function using an array of generic type T
	protected void exchange(final T[] a, final int i, final int j)
	{
		T swap = a[i];
		a[i] = a[j];
		a[j] = swap;
	}

	// Global swap function using a List of generic type T
	protected void exchange(final List<T> a, final int i, final int j)
	{
		T swap = a.get(i);
		a.set(i,a.get(j));
		a.set(j, swap);
	}
}
