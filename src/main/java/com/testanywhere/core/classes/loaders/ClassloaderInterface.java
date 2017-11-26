package com.testanywhere.core.classes.loaders;

public interface ClassloaderInterface
{
	void setLoaderName(final String name);
	String getLoaderName();
	
	void setLoaderType(final Class<?> classType);
	Class<?> getLoaderType();
	
	Class<?> loadPluginClass(final String className);
	void initializeLoader(final String packageID);
	void reset();
}
