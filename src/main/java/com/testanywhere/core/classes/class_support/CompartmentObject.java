package com.testanywhere.core.classes.class_support;

import com.testanywhere.core.utilities.exceptions.ObjectCreationException;
import com.testanywhere.core.utilities.logging.*;
import com.testanywhere.core.utilities.class_support.BaseClass;
import com.testanywhere.core.utilities.class_support.Cast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class CompartmentObject<T> extends OutputDisplay
{
	private Object __underlyingObject;
	private Class<?> __underlyingClass;
	private Type __underlyingType;

	public CompartmentObject()
	{
		super();
		this.__underlyingObject = null;
		this.__underlyingClass = null;
		this.__underlyingType = null;
	}

	public CompartmentObject( final T object )
	{
		super();
		this.__underlyingObject = object;
		if ( object != null ) {
			this.__underlyingClass = object.getClass();
			this.__underlyingType = object.getClass().getComponentType();
		}
	}

	public CompartmentObject( final Class<T> clazz, final Object o )
	{
		super();
		this.__underlyingObject = o;
		this.__underlyingClass = clazz;
		this.__underlyingType = clazz.getComponentType();
	}

	public CompartmentObject( final CompartmentObject<?> obj ) throws ObjectCreationException
	{
		super();
		if ( obj == null ) throw new ObjectCreationException(CompartmentObject.class);
		this.__underlyingObject = copy(obj.getObject());
		this.__underlyingClass = copy(obj.getClassType());
		this.__underlyingType = copy(obj.getType());
	}

	@Override
	public void buildObjectOutput( int numTabs )
	{
		if ( numTabs < 0 ) numTabs = 0;
		Tabbing tabEnvironment = new Tabbing(numTabs);
		DisplayManager dm = this.getDM();

		String outerSpacer = tabEnvironment.getSpacer();
		dm.append(outerSpacer + "Object : ", DisplayType.TEXTTYPES.LABEL);

		tabEnvironment.increment();
		String innerSpacer = tabEnvironment.getSpacer();
		T obj = this.getObject();

		try
		{
			if ( obj == null ) dm.append(innerSpacer + "Object Data : " + BaseClass.checkIsNull(obj));
			else {
				Method m = obj.getClass().getMethod("getPrettyPrint", int.class);
				dm.append((String) m.invoke(obj, tabEnvironment.numberTabs()), DisplayType.TEXTTYPES.LABEL);
			}
		}
		catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e)
		{
			dm.append(innerSpacer + "Class Type              : " + BaseClass.checkIsNull(this.getClassType()));
			dm.append(innerSpacer + "Object Data (as String) : " + BaseClass.checkIsNull(obj));
		}
	}

	@Override
	public boolean equals( final Object o )
	{
		if ( o instanceof CompartmentObject )
		{
			CompartmentObject<T> oT = Cast.cast(o);
			return oT != null && (this.getObject().equals(oT.getObject()) && this.getClassType().equals(oT.getClassType()));
		}
		return false;
	}

	@Override
	public String toString()
	{
		String nullDef = "(null)";

		StringBuilder sb = new StringBuilder();
		if ( this.__underlyingObject != null )
		{
			sb.append("Object : " + TextManager.specializeName(this.getObject().toString()));
			if (this.getClassType() != null)
			{
				sb.append(" " + TextManager.STR_OUTPUTSEPARATOR + " Class : " + TextManager.specializeName(this.getClassType().getName()));
			}
			if (this.getType() != null)
			{
				sb.append(" " + TextManager.STR_OUTPUTSEPARATOR + " Type : " + TextManager.specializeName(this.getType().toString()));
			}
		}
		else
		{
			sb.append("Object : " + TextManager.specializeName(nullDef));
			sb.append(" " + TextManager.STR_OUTPUTSEPARATOR + " Class : " + nullDef);
			sb.append(" " + TextManager.STR_OUTPUTSEPARATOR + " Type  : " + nullDef);
		}

		return sb.toString();
	}

	@Override
	public boolean isNull()
	{
		return this.getObject() == null || this.getClassType() == null;
	}

	@SuppressWarnings("unchecked")
	public T getObject()
	{
		if ( this.__underlyingClass == null ) return null;
		return (T) Cast.safeCast(this.__underlyingObject, this.__underlyingClass);
	}

	public Class<?> getClassType()
	{
		return this.__underlyingClass;
	}

	public Type getType()
	{
		return this.__underlyingType;
	}

	public static<T> boolean isNull( T obj )
	{
		if ( obj == null ) return true;
		if ( obj instanceof CompartmentObject<?> )
		{
			if ( ((CompartmentObject) obj).getObject() == null ) return true;
		}
		return false;
	}
}
