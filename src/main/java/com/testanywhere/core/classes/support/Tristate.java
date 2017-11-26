package com.testanywhere.core.classes.support;

import com.testanywhere.core.utilities.classes.Range;
import com.testanywhere.core.utilities.exceptions.ObjectCreationException;
import com.testanywhere.core.utilities.logging.*;

import java.util.Objects;

public class Tristate extends OutputDisplay
{
	public static final Integer DONT_CARE = -1;
	public static final Integer FAIL      = 0;
	public static final Integer PASS      = 1;

	private int value;

	public Tristate()
	{
		super();
		this.initialize();
	}
	
	public Tristate( final Boolean value )
	{
		this();
		if ( value != null )
			this.value = value ? Tristate.PASS : Tristate.FAIL;
	}
	
	public Tristate( final int value )
	{
		this();
		Range<Integer> r = new Range<>(Tristate.DONT_CARE, Tristate.PASS);
		this.value = r.bound(value);
	}

	public Tristate( final Tristate t ) throws ObjectCreationException
	{
		this();
		if ( t == null ) throw new ObjectCreationException(Tristate.class);

		this.value = t.getValue();
	}

	@Override
	public String toString() 
	{
		if ( this.value == Tristate.DONT_CARE) return "DONT_CARE";
		else if ( this.value == Tristate.FAIL) return "FAIL";
		else return "PASS";
	}

	@Override
	public boolean equals( final Object other )
	{
		boolean result = true;
		
		if ( other == null ) return false;
		if ( other instanceof Boolean ) 
		{
			result = this.compareTo((Boolean) other);
		}
		if ( other instanceof Tristate ) 
		{
			result = this.compareTo((Tristate) other);
		}
		return result;
	}

	@Override
	public boolean isNull() { return false; }

	@Override
	public void buildObjectOutput( int numTabs )
	{
		if ( numTabs < 0 ) numTabs = 0;
		Tabbing tabEnvironment = new Tabbing(numTabs);
		DisplayManager dm = this.getDM();

		String outerSpacer = tabEnvironment.getSpacer();
		dm.append(outerSpacer + "TriState :", DisplayType.TEXTTYPES.LABEL);

		tabEnvironment.increment();
		String innerSpacer = tabEnvironment.getSpacer();

		dm.append(innerSpacer + "TriState Value : " + this.convertToText());
	}

	public Integer getValue()
	{
		return this.value;
	}
	
	public Boolean compareTo(final Tristate obj)
	{
		return this.getValue() != null && this.getValue().equals(obj.getValue());
	}
	
	public Boolean compareTo(final Boolean obj)
	{
		if ( Tristate.DONT_CARE.equals(this.getValue()) ) return false;
		if ( this.getValue() == ( obj ? 1 : 0 ) ) return obj;
		return false;
	}
	
	public void and(Boolean obj) 
	{
		if ( obj == null ) obj = true;
		if ( Tristate.DONT_CARE.equals(this.getValue()) )
		{
			this.value = obj ? Tristate.PASS : Tristate.FAIL;
			return;
		}
		
		if ( Tristate.PASS.equals(this.getValue()) )
		{
			Boolean boolrep = this.getBoolean();
			this.value = (boolrep && obj) ? Tristate.PASS : Tristate.FAIL;
		}
	}
	
	public void or(Boolean obj)
	{
		if ( obj == null ) obj = true;
		if ( Tristate.DONT_CARE.equals(this.getValue()) ) {
			this.value = obj ? Tristate.PASS : Tristate.FAIL;
			return;
		}
		
		if ( Tristate.FAIL.equals(this.getValue()) ) {
			Boolean boolrep = getBoolean();
			this.value = (boolrep || obj) ? Tristate.PASS : Tristate.FAIL;
		}
	}

	public void valueor(final Boolean obj)
	{
		if ( obj == null ) return;
		if ( Tristate.DONT_CARE.equals(this.getValue()) )
		{
			this.value = obj ? Tristate.PASS : Tristate.FAIL;
			return;
		}
		
		if ( Tristate.FAIL.equals(this.getValue()) )
		{
			Boolean boolrep = this.getBoolean();
			this.value = (boolrep || obj) ? Tristate.PASS : Tristate.FAIL;
		}
	}
	
	public Boolean getBoolean() 
	{
		if ( Tristate.DONT_CARE.equals(this.getValue()) ) return null;
		return (!Objects.equals(this.getValue(), Tristate.FAIL));
	}

	private String convertToText()
	{
		if ( Tristate.DONT_CARE.equals(this.getValue()) ) return "DON'T CARE";
		if ( Tristate.FAIL.equals(this.getValue()) ) return "FAIL";
		return "PASS";
	}

	private void initialize()
	{
		this.value = Tristate.DONT_CARE;
	}
}