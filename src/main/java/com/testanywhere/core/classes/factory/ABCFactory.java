package com.testanywhere.core.classes.factory;

import com.testanywhere.core.classes.class_support.CompartmentObject;
import com.testanywhere.core.classes.class_support.GeneralizedConstructor;
import com.testanywhere.core.classes.class_support.ParameterizedObject;
import com.testanywhere.core.classes.loaders.ABCClassloader;
import com.testanywhere.core.classes.support.map.DynamicMap;
import com.testanywhere.core.utilities.class_support.Cast;
import com.testanywhere.core.utilities.logging.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public abstract class ABCFactory extends OutputDisplay implements FactoryInterface
{
	private String      factoryName;
	private Class<?>    factoryType;
	private ClassLoader classLoader;
	private boolean     initialized;

	private DynamicMap<String> factoryObjectMap;
	private Map<String, String> factoryNameMap;

	protected ABCFactory()
	{
		super();
		this.__initialize();
	}

	@Override
	public void buildObjectOutput( int numTabs )
	{
		if ( numTabs < 0 ) numTabs = 0;
		Tabbing tabEnvironment = new Tabbing(numTabs);
		DisplayManager dm = this.getDM();

		String outerSpacer = tabEnvironment.getSpacer();
		dm.append(outerSpacer + "Factory :", DisplayType.TEXTTYPES.LABEL);

		if ( this.getFactoryType() != null )
		{
			tabEnvironment.increment();
			String innerSpacer = tabEnvironment.getSpacer();

			dm.append(innerSpacer + "Name : " + this.getFactoryName());
			dm.append(innerSpacer + "Type : " + this.getFactoryType());
		}

		ClassLoader ABCLoader = this.getLoader();
		if ( ABCLoader != null ) {
			ABCClassloader loader = Cast.cast(ABCLoader);
			if ( loader != null ) {
				loader.buildObjectOutput(tabEnvironment.numberTabs());
				dm.addFormatLines(loader.getDM().getLines());
			}
		}

		String innerSpacer = tabEnvironment.getSpacer();
		if ( ! this.getFactoryMap().isEmpty() )
		{
			DynamicMap<String> currentMap = this.getFactoryMap();
			dm.append(innerSpacer + "Object Map : ", DisplayType.TEXTTYPES.LABEL);
			tabEnvironment.increment();
			String subInnerSpacer = tabEnvironment.getSpacer();
			for ( String s : currentMap.getMap().keySet() )
			{
				Object val = currentMap.get(s);
				String name = val.toString();

				try {
					Method m = val.getClass().getMethod("getName", (Class<?>[]) null);
					if (m != null)
						name = Cast.cast(m.invoke(val, (Object[]) null));
				} catch ( NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored)
				{}

				dm.append(subInnerSpacer + s + " : [ " + name + ", " + val + " ]");
			}
			tabEnvironment.decrement();
		}

		if ( ! this.getFactoryNameMap().isEmpty() )
		{
			Map<String, String> currentMap = this.getFactoryNameMap();
			dm.append(innerSpacer + "Object Name Map : ", DisplayType.TEXTTYPES.LABEL);
			tabEnvironment.increment();
			String subInnerSpacer = tabEnvironment.getSpacer();
			for ( String s : currentMap.keySet() )
			{
				dm.append(subInnerSpacer + s + " : " + currentMap.get(s));
			}
			tabEnvironment.decrement();
		}

	}

	@Override
	public abstract Object instantiate( final String type, final Collection<CompartmentObject<?>> alo );

	@Override
	public abstract Object instantiate( final Class<?> type, final Collection<CompartmentObject<?>> alo );

	public abstract void initializeFactory();
	
	@Override
	public void reset()
	{
		FactoryUtils.resetFactory(this);
	}
	
	@Override
	public Class<?>[] findClassType(final String pluginType) throws NullPointerException
	{
		return FactoryUtils.findClassType(this, pluginType);
	}

	@Override
	public ClassLoader getLoader() 
	{
		if ( this.classLoader != null ) return this.classLoader;
		return null;
	}
	
	@Override
	public String getFactoryName() 
	{
		if ( this.factoryName != null ) return this.factoryName;
		return null;
	}

	@Override
	public Class<?> getFactoryType() 
	{
		if ( this.factoryType != null ) return this.factoryType;
		return null;
	}
	
	@Override
	public void setLoader( final ClassLoader classLoader )
	{
		this.classLoader = classLoader;
	}

	@Override
	public void setFactoryName( final String name )
	{
		this.factoryName = name;
	}

	@Override
	public void setFactoryType( final Class<?> classType )
	{
		this.factoryType = classType;
	}

	@Override
	public boolean isNull() { return false; }

	public void updateFactoryClasses( final String subsystem, final String keyword )
	{
		if ( ! TextManager.validString(subsystem) ) return;
		String pluginFullPath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
		Map<String, String> foundFactoryClasses = FactoryUtils.findClassTypes(pluginFullPath, subsystem, keyword);
		if ( foundFactoryClasses != null && foundFactoryClasses.size() > 0 )
			for ( String s : foundFactoryClasses.keySet() )
				this.factoryNameMap.put(s, foundFactoryClasses.get(s));
	}

	public abstract void findFactoryClasses();

	public DynamicMap<String> getFactoryMap()
	{
		return this.factoryObjectMap;
	}

	public Map<String, String> getFactoryNameMap()
	{
		return this.factoryNameMap;
	}

	protected void setInitialized(final boolean isInit)
	{
		this.initialized = isInit;
	}

	protected boolean isInitialized()
	{
		return this.initialized;
	}
	
	void instantiateLoader(final Class<?> classType) throws FactoryException
	{
		if ( classType == null ) throw new FactoryException();
		
		Class<?>[] constructorParamTypes = null;
		Object[] constructorObjs         = null;

		Constructor<?> constructor;
		try 
		{
			constructor = classType.getConstructor(constructorParamTypes);
			Object instanceOfLoader;
			try 
			{
				instanceOfLoader = constructor.newInstance(constructorObjs);
			} 
			catch (InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException e) 
			{
				throw new FactoryException(e.getLocalizedMessage());
			}

			ABCClassloader loader = Cast.cast(instanceOfLoader);
			if ( loader != null )
			{
				if ( ! loader.isInitialized() ) loader.initializeLoader(null);
				this.setLoader(loader);
			}
		} 
		catch (NoSuchMethodException | SecurityException e) 
		{
			throw new FactoryException(e.getLocalizedMessage());
		}
	}
	
	protected Object instantiatedObjectFromLoader( final ABCClassloader pluginLoader, final String pluginType, final Class<?> myPluginClass, Collection<CompartmentObject<?>> alo ) throws ReflectiveOperationException
	{
		Class<?>[] constructorParamTypes = null;
		Object[] constructorObjs = null;
		if ( alo != null ) constructorObjs = ParameterizedObject.getParameterizedObjectsAsArray(alo);
		
		ArrayList<GeneralizedConstructor> gc = pluginLoader.getClassConstructors(pluginType);
	
		if ( gc != null ) {
			ParameterizedObject p = FactoryUtils.findMatchingConstructor(gc, alo);
			if ( p == null ) throw new ReflectiveOperationException("Could not find requested signature as a constructor for class " + TextManager.specializeName(pluginType));
			if ( p.getClassTypes() != null ) {
				ArrayList<Class<?>> classArray = p.getClassTypes();
				constructorParamTypes = new Class<?>[classArray.size()];
				p.getClassTypes().toArray(constructorParamTypes);
			}
			if ( alo == null && p.getObjectTypes() != null ) {
				ArrayList<Object> objArray = p.getObjectTypes();
				constructorObjs = new Object[objArray.size()];
				p.getObjectTypes().toArray(constructorObjs);
			}
		}
	
	
		Constructor<?> constructor = myPluginClass.getConstructor(constructorParamTypes);
		if ( gc == null )
			return constructor.newInstance();
		return constructor.newInstance(constructorObjs);
	}

	private void __initialize()
	{
		this.factoryName = null;
		this.factoryType = null;
		this.classLoader = null;
		this.initialized = false;

		this.factoryObjectMap = new DynamicMap<>();
		this.factoryNameMap   = new TreeMap<>();
	}
}