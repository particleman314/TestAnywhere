package com.testanywhere.core.classes.filters;

import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class Filter<T> 
{
	protected abstract boolean passes(T object);

	public Iterator<T> filter(Iterator<T> iterator) 
	{
		return new FilterIterator(iterator);
	}

	public Iterable<T> filter(final Iterable<T> iterable) 
	{
		return new Iterable<T>() 
		{
			public Iterator<T> iterator()
			{
				return filter(iterable.iterator());
			}
		};
	}

	private class FilterIterator implements Iterator<T> 
	{
		private Iterator<T> iterator;
		private T next;

		private FilterIterator(Iterator<T> iterator) 
		{
			this.iterator = iterator;
			this.toNext();
		}

		public boolean hasNext() 
		{
			return this.next != null;
		}

		public T next() 
		{
			if (this.next == null)
				throw new NoSuchElementException();
			T returnValue = this.next;
			this.toNext();
			return returnValue;
		}

		public void remove() 
		{
			throw new UnsupportedOperationException();
		}

		private void toNext()
		{
			this.next = null;
			while (this.iterator.hasNext())
			{
				T item = this.iterator.next();
				if (item != null && passes(item)) 
				{
					this.next = item;
					break;
				}
			}
		}
	}
}