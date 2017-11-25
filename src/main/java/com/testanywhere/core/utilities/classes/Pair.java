package com.testanywhere.core.utilities.classes;

import com.testanywhere.core.utilities.class_support.BaseClass;
import com.testanywhere.core.utilities.class_support.Cast;
import com.testanywhere.core.utilities.exceptions.ObjectCreationException;
import com.testanywhere.core.utilities.logging.*;

public class Pair<L,R> extends OutputDisplay
{
	private L l;
	private R r;

	public Pair()
	{
		super();
		this.initialize();
	}

	public Pair(final L l, final R r)
	{
		this();
		this.l = l;
		this.r = r;
	}

	// Copy constructor
	public Pair( Pair<L, R> otherpair ) throws ObjectCreationException
	{
		this();
		if ( otherpair == null ) throw new ObjectCreationException(Pair.class);

		this.l = this.copy(otherpair.getL());
		this.r = this.copy(otherpair.getR());
	}

	@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
	@Override
	public boolean equals( final Object other )
	{
		if ( other == null ) return false;
		
		Pair<L,R> otherPair = Cast.cast(other);
		if ( otherPair == null ) return false;
		
		L ls_1 = this.first();
		L ls_2 = otherPair.first();
		
		R rs_1 = this.second();
		R rs_2 = otherPair.second();
		
		if ( ( ls_1 == ls_2 && ls_1 == null ) || ( rs_1 == rs_2 && rs_1 == null ) )
		{
			return true;
		} 
		else
		{
			if ( ! this.getL().equals(otherPair.getL()) ) return false;
			if ( ! this.getR().equals(otherPair.getR()) ) return false;
		}
		return true;
	}
	
	@Override
	public String toString() 
	{
		String leftSide  = TextManager.specializeName("null");
		String rightSide = TextManager.specializeName("null");
		
		if ( this.l != null ) leftSide = this.l.toString();
		if ( this.r != null ) rightSide = this.r.toString();
		
		return "Left --> [ " + leftSide + " ] :: Right --> [ " + rightSide + " ]";
	}

	@Override
	public boolean isNull()
	{
		return this.isEmpty();
	}

	@Override
	public void buildObjectOutput(int numTabs)
	{
		if ( numTabs < 0 ) numTabs = 0;
		Tabbing tabEnvironment = new Tabbing(numTabs);
		DisplayManager dm = this.getDM();

		String outerSpacer = tabEnvironment.getSpacer();
		dm.append(outerSpacer + "Pair :", DisplayType.TEXTTYPES.LABEL);

		tabEnvironment.increment();
		String innerSpacer = tabEnvironment.getSpacer();

		dm.append(innerSpacer + "Left : " + BaseClass.checkIsNull(this.getL()));
		dm.append(innerSpacer + "Right : " + BaseClass.checkIsNull(this.getR()));
	}

	public L getL()
	{
		return this.l;
	}
	    
	public R getR()
	{
		return this.r;
	}
	    
	public L first() 
	{
		return this.getL();
	}
	
	public R second() 
	{
		return this.getR();
	}
	
	public void setL(final L l)
	{
		this.l = l;
	}
	    
	public void setR(final R r)
	{
		this.r = r;
	}
	
	public void set(final L l, final R r)
	{
		this.setL(l);
		this.setR(r);
	}

	public void clear()
	{
		this.l = null;
		this.r = null;
	}

	public boolean isEmpty() 
	{
		return this.l == null && this.r == null;
	}

	private void initialize()
	{
		this.l = null;
		this.r = null;
	}
}