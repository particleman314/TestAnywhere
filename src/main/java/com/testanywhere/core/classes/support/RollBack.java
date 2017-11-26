package com.testanywhere.core.classes.support;

import com.testanywhere.core.utilities.class_support.BaseClass;
import com.testanywhere.core.utilities.class_support.Cast;
import com.testanywhere.core.utilities.exceptions.ObjectCreationException;
import com.testanywhere.core.utilities.logging.*;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

public class RollBack<T> extends OutputDisplay
{
	public static final int DEFAULT_ROLLBACK_SIZE = 2;
	private static final int INFINITE_CAPACITY = -1;

	private List<T> history;
	private int     capacity;

	public RollBack() 
	{
		super();
		this.setLog(this);
		
		this.history  = new LinkedList<>();
		this.capacity = RollBack.DEFAULT_ROLLBACK_SIZE;
	}
	
	@SafeVarargs
	public RollBack(final T... inputs)
	{
		this();
		this.push(inputs);
	}
	
	public RollBack(final T input)
	{
		this();
		this.push(input);
	}

	@SuppressWarnings("unchecked")
	public RollBack(final RollBack rb ) throws ObjectCreationException
	{
		this();
		if ( rb == null ) throw new ObjectCreationException(RollBack.class);

		this.history = copy(rb.history);
		this.capacity = rb.capacity;
	}

	@Override
	public String toString() 
	{
		return "CAP:" + this.getCapacity() + "|" + this.getHistory();
	}
	
	@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
	@Override
	public boolean equals( final Object other )
	{
		if ( other == null ) return false;
		
		RollBack<T> otherRB = Cast.cast(other);
		if ( otherRB == null ) return false;
		
		if ( this.getNumberItems() != otherRB.getNumberItems() ) return false;
		
		for ( int loop = 0; loop < otherRB.getNumberItems(); ++loop )
			if ( ! this.get(loop).equals(otherRB.get(loop)) ) return false;
		return true;
	}

	@Override
	public boolean isNull() { return this.getHistory().isEmpty(); }

	@Override
	public void buildObjectOutput( int numTabs )
	{
		if ( numTabs < 0 ) numTabs = 0;
		Tabbing tabEnvironment = new Tabbing(numTabs);
		DisplayManager dm = this.getDM();

		String outerSpacer = tabEnvironment.getSpacer();
		if ( this.history.isEmpty() )
			dm.append(outerSpacer + "History [" + Object.class.getSimpleName() + "] :", DisplayType.TEXTTYPES.LABEL);
		else
			dm.append(outerSpacer + "History [" + this.history.get(0).getClass().getSimpleName() + "] :", DisplayType.TEXTTYPES.LABEL);

		tabEnvironment.increment();
		String innerSpacer = tabEnvironment.getSpacer();

		if ( this.getHistory().isEmpty() )
		{
			dm.append(innerSpacer + "Data : " + BaseClass.checkIsNull(null));
		}
		else {
			StringBuilder sb = new StringBuilder();
			sb.append(innerSpacer + "Data : ( ");

			int numStoredElements = this.getNumberItems();
			for (int count = 0; count < numStoredElements; count++) {
				sb.append(this.getHistory().get(count).toString());
				if (numStoredElements != 1 && count != (numStoredElements - 1)) sb.append(",");
			}
			sb.append(" )");
			dm.append(sb.toString());
		}
		if ( this.getCapacity() == RollBack.INFINITE_CAPACITY )
			dm.append(innerSpacer + "Capacity size : + " + TextManager.specializeName("UNBOUNDED"));
		else
			dm.append(innerSpacer + "Capacity size : " + this.getCapacity());
	}

	public void allowInfiniteCapacity()
	{
		this.capacity = RollBack.INFINITE_CAPACITY;
	}

	public int getCapacity()
	{ 
		return this.capacity;
	}
	
	public int getNumberItems()
	{
		return this.getHistory().size();
	}

	public void setCapacity( final int capacity )
	{
		if ( capacity < 1 ) return;
		this.capacity = capacity;
		while ( this.getNumberItems() > capacity ) this.unroll();
	}
	
	public void clear() 
	{
		this.history.clear();
	}
	
	public void unroll()
	{
		if ( this.getNumberItems() > 0 ) this.unroll(1);
	}
	
	public void unroll( int numBack ) 
	{
		if ( numBack <= 0 ) return;
		int listSize = this.getNumberItems();
		if ( listSize - numBack < 0 ) { numBack = listSize; }

		String msg;
		if ( this.capacity == RollBack.INFINITE_CAPACITY )
			msg = "Number to unroll : " + numBack + " -- History size : " + listSize + " -- Capacity : <UNBOUNDED>";
		else
			msg = "Number to unroll : " + numBack + " -- History size : " + listSize + " -- Capacity : " + this.getCapacity();

		this.info(msg);

		if ( numBack == listSize )
		{
			this.getHistory().clear();
			T element = get(numBack);
			this.push(element);
		} 
		else
		{
			for ( int count = 0; count < numBack; count++ ) ((LinkedList<T>) this.history).removeLast();
		}
	}
	
	public void incrementCapacity() 
	{
		this.incrementCapacity(1);
	}
	
	public void incrementCapacity( final int more )
	{
		this.capacity += more;
		this.validateCapacity();
	}

	public void decrementCapacity()
	{
		this.decrementCapacity(1);
	}
	
	public void decrementCapacity( final int less )
	{
		this.capacity -= less;
		this.validateCapacity();
	}
	
	public void pushList(final T[] inputs)
	{
		for ( T item : inputs ) push(item);
	}
	
	@SafeVarargs
	public final void push(@SuppressWarnings("unchecked") final T... inputs)
	{
		for ( T item : inputs ) push(item);
	}
	
	public void push(final T input)
	{
		if ( input == null ) return;
		if ( this.getHistory() == null ) this.history = new LinkedList<>();
		this.getHistory().add(input);
		if ( this.getCapacity() != INFINITE_CAPACITY )
			if ( this.getNumberItems() > this.getCapacity() ) ((LinkedList<T>) this.getHistory()).removeFirst();
	}
	
	public T get( final int index )
	{
		int listSize = this.getNumberItems();
		int storage  = this.getCapacity();
		
		if ( index < 1 ) return null;
		if ( index > listSize ) return null;
		if ( index > storage ) return null;
		
		this.debug("Index : " + index + " -- History size : " + listSize + " -- Capacity : " + storage);
		return this.getHistory().get(listSize - index);
	}
	
	public T latest()
	{
		try 
		{
			return ((LinkedList<T>) this.getHistory()).getLast();
		} 
		catch ( NoSuchElementException nse )
		{
			return null;
		}
	}
	
	public T earliest()
	{
		try 
		{
			return ((LinkedList<T>) this.getHistory()).getFirst();
		} 
		catch ( NoSuchElementException nse )
		{
			return null;
		}
	}

	private List<T> getHistory()
	{
		return this.history;
	}

	private void validateCapacity()
	{
		if ( this.getCapacity() < 1 ) this.capacity = RollBack.DEFAULT_ROLLBACK_SIZE;
	}

	private RollBack(final int capacity, final Collection<T> inputs)
	{
		this();
		this.capacity = capacity;
		for ( T obj : inputs )
			this.push(obj);
	}
}