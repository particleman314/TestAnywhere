package com.testanywhere.core.classes.managers;

import com.testanywhere.core.classes.factory.ABCFactory;

public interface ManagerInterface
{
	void setManagerName(final String name);
	String getManagerName();
	
	void setManagerType(final Class<?> classType);
	Class<?> getManagerType();

	void configure();
	void initialize();
	void validate();
	
	void reset();
	void cleanup();
	
	boolean isValid();
	boolean hasFactory();
	ABCFactory getFactory();
}