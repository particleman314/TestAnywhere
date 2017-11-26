package com.testanywhere.core.classes.factory;

import com.testanywhere.core.classes.class_support.CompartmentObject;

import java.util.Collection;

public interface FactoryInterface
{
	void setLoader(ClassLoader classLoader);
	ClassLoader getLoader();
	
	void setFactoryName(final String name);
	String getFactoryName();
	
	void setFactoryType(final Class<?> classType);
	Class<?> getFactoryType();
	
	Object instantiate(final String type, final Collection<CompartmentObject<?>> alo);
	Object instantiate(final Class<?> type, final Collection<CompartmentObject<?>> alo);
	
	void reset();
	Class<?>[] findClassType(final String pluginType) throws NullPointerException;
}
