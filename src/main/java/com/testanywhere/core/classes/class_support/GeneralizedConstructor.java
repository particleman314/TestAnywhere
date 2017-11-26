package com.testanywhere.core.classes.class_support;

import com.testanywhere.core.utilities.class_support.BaseClass;
import com.testanywhere.core.utilities.class_support.Cast;
import com.testanywhere.core.utilities.classes.Pair;
import com.testanywhere.core.utilities.exceptions.ObjectCreationException;
import com.testanywhere.core.utilities.logging.*;

@SuppressWarnings("ExternalizableWithoutPublicNoArgConstructor")
public class GeneralizedConstructor extends OutputDisplay
{
	public static final int NO_SIGNATURE = 0;
	
	private Pair<String, ParameterizedObject> constructor;

	public GeneralizedConstructor( final String constructorName, final ParameterizedObject buildIngreds )
	{
		this(); // private constructor
		this.setConstructorPair(constructorName, buildIngreds);
	}

	public GeneralizedConstructor( final GeneralizedConstructor otherGC )
	{
		this();
		if ( otherGC == null ) throw new ObjectCreationException(GeneralizedConstructor.class);
		try
		{
			this.constructor = otherGC.getConstructor().copy();
		}
		catch (CloneNotSupportedException e)
		{
			throw new ObjectCreationException(GeneralizedConstructor.class);
		}
	}

	@Override
	public String toString()
	{
		if ( this.getConstructor() == null ) return null;
		return this.getConstructor().toString();
	}
	
	@Override
	public boolean equals( final Object other )
	{
		if (other == null) return false;

		GeneralizedConstructor othergc = Cast.cast(other);
		return othergc != null && this.getConstructor().equals(othergc.getConstructor());
	}

	@Override
	public boolean isNull()
	{
		return this.getConstructor().isNull();
	}

	public String getConstructorName()
	{
		if ( this.getConstructor() == null ) return null;
		return this.getConstructor().first();
	}
	
	public ParameterizedObject getParameterizedObject() 
	{
		if ( this.getConstructor() == null ) return null;
		return this.getConstructor().second();
	}

	public int getSignature()
	{
		return this.getSignature(false);
	}

	public int getSignature( final boolean rebuild )
	{
		if ( rebuild )
		{
			if ( this.getConstructor() == null || this.getParameterizedObject() == null )
				return GeneralizedConstructor.NO_SIGNATURE;
		
			return this.getParameterizedObject().getSignature();
		}
		else
		{
			String[] components = this.getConstructorName().split("_");
			if ( components.length < 2 ) return this.getSignature(true);

			return Integer.parseInt(components[1]);
		}
	}
	
	public void setBuildContents(final ParameterizedObject newBuildIngreds)
	{
		this.getConstructor().setR(newBuildIngreds);
	}
	
	public void setConstructorPair(final String constructorName, final ParameterizedObject buildIngreds)
	{
		if ( ! TextManager.validString( constructorName, null ) ) return;
		this.constructor = new Pair<>(constructorName, buildIngreds);
	}
	
	public boolean isEmpty()
	{
		return this.getConstructor() == null || this.getConstructor().isEmpty();
	}

	public void buildObjectOutput(int numTabs)
	{
		if ( numTabs < 0 ) numTabs = 0;
		Tabbing tabEnvironment = new Tabbing(numTabs);
		DisplayManager dm = this.getDM();

		String outerSpacer = tabEnvironment.getSpacer();
		dm.append(outerSpacer + "Generalized Constructor Object :", DisplayType.TEXTTYPES.LABEL );

		tabEnvironment.increment();
		String innerSpacer = tabEnvironment.getSpacer();

		dm.append(innerSpacer + "Name : " + BaseClass.checkIsNull(this.getConstructorName()));
		if ( this.getConstructor() == null || this.getConstructor().isEmpty() || this.isNull() )
		{
			dm.append(innerSpacer + "Constructor Ingredients : " + BaseClass.checkIsNull(this.getConstructor()));
		}
		else
		{
			ParameterizedObject po = this.getParameterizedObject();
			dm.append(innerSpacer + "Constructor Ingredients : " + BaseClass.checkIsNull(po));
			if ( po != null && ! po.isNull() )
			{
				po.buildObjectOutput(tabEnvironment.numberTabs());
				dm.addFormatLines(po.getDM().getLines());
			}
		}
	}

	private GeneralizedConstructor() 
	{
		super();
		this.constructor = null;
	}
	
	@SuppressWarnings("unused")
	private GeneralizedConstructor(final String constructorName)
	{
		this(); // private constructor
		this.setConstructorPair(constructorName, new ParameterizedObject());
	}
	
	private Pair<String, ParameterizedObject> getConstructor() 
	{
		return this.constructor;
	}
}