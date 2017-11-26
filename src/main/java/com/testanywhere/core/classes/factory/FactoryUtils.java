package com.testanywhere.core.classes.factory;

import com.testanywhere.core.classes.class_support.CompartmentObject;
import com.testanywhere.core.classes.class_support.GeneralizedConstructor;
import com.testanywhere.core.classes.class_support.ParameterizedObject;
import com.testanywhere.core.classes.loaders.ABCClassloader;
import com.testanywhere.core.classes.utilities.ClassUtils;
import com.testanywhere.core.classes.utilities.FileDirUtils;
import com.testanywhere.core.utilities.class_support.Cast;
import com.testanywhere.core.utilities.classes.Pair;
import com.testanywhere.core.utilities.logging.LogConfiguration;
import com.testanywhere.core.utilities.logging.MultiOutputStream;
import com.testanywhere.core.utilities.logging.TextManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class FactoryUtils 
{
	public static Logger logger;
	private static final String PLUGIN_DIRECTORY = "com/nimsoft/plugins";

	static
	{
		FactoryUtils.logger = Logger.getLogger("FactoryUtils");
		LogConfiguration.configure();
	}

	public static void displayFactory(MultiOutputStream mos, final Integer numTabs, final ABCFactory factory)
	{
		String factoryOutput = FactoryUtils.getFactoryAsString(numTabs, factory);

		if ( factoryOutput != null )
		{
			if (mos == null)
			{
				mos = new MultiOutputStream();
				mos.addStream(System.out);
			}

			for (OutputStream os : mos.getStreams())
			{
				final PrintStream ps = new PrintStream(os);
				ps.println(factoryOutput);
				ps.flush();
			}
		}
	}

	public static void displayFactory(OutputStream os, final Integer numTabs, final ABCFactory factory)
	{
		String factoryOutput = FactoryUtils.getFactoryAsString(numTabs, factory);
		if ( factoryOutput != null )
		{
			if (os == null) os = System.out;
			final PrintStream ps = new PrintStream(os);
			ps.println(factoryOutput);
			ps.flush();
		}
	}

	public static String cleanupClassName( String pluginType, final ABCClassloader factoryLoader ) throws ClassNotFoundException
	{
		if ( pluginType.endsWith(".class") ) pluginType = pluginType.replaceAll(".class", "");
		String pkgID = factoryLoader.getPackageID();

		if ( TextManager.validString(pkgID) ) pluginType = pkgID + "." + pluginType;
		
		Class<?> pluginClass = Class.forName(pluginType);
		pluginType = pluginClass.getName();
		
		return pluginType;
	}
	
	public static<T> void initializeFactorySettings( final T genericFactory, final Class<?> factoryLoaderClass )
	{
		ABCFactory factory = ((ABCFactory) genericFactory);
		Class<?> factoryClass = factory.getClass();
		
		factory.setFactoryType(factoryClass);
		factory.setFactoryName(factoryClass.getSimpleName());
		factory.debug("Setting up factory information...");
		if ( factory.getLoader() == null )
		{
			try 
			{
				factory.instantiateLoader(factoryLoaderClass);
				factory.info("Attached requested class loader type " + TextManager.specializeName(factoryLoaderClass.toString()));
			} 
			catch (FactoryException e)
			{
				factory.error("Was unable to instantiate requested loader type " + TextManager.specializeName(factoryLoaderClass.toString()));
				factory.error("Setting attached loader to NULL");
				factory.error(e.getLocalizedMessage());
				factory.setLoader(null);
			}
		}
	}
	
	public static<T,S> void resetFactory( final T factory )
	{
		ABCClassloader pluginLoader = Cast.cast(((ABCFactory) factory).getLoader());
		
		if ( pluginLoader != null ) 
		{
			pluginLoader.reset();
			((ABCFactory) factory).debug("Resetting the " + factory.getClass().getSimpleName() + "...");
			((ABCFactory) factory).setInitialized(false);
		}

		((ABCFactory) factory).getFactoryMap().clear();
		((ABCFactory) factory).getFactoryNameMap().clear();
	}
	
	public static<T> Class<?>[] findClassType( final T factory, final String pluginType ) throws NullPointerException
	{
		if ( ((ABCFactory) factory).getLoader() != null )
		{
			Class<?>[] pluginClass = { ((ABCClassloader) ((ABCFactory) factory).getLoader()).findClass(pluginType) };
			if ( pluginClass.length > 0 && pluginClass[0] != null )
			{
				return pluginClass;
			}
		}
		throw new NullPointerException("Unable to find requested class for type " + TextManager.specializeName(pluginType));
	}

	public static<T,S> T instantiateObject( final S factory, final Class<?> type, final Collection<CompartmentObject<?>> alo )
	{
		return FactoryUtils.instantiateObject(factory, type.getSimpleName(), alo);
	}
	
	public static<T,S> T instantiateObject( final S factory, String pluginType, final Collection<CompartmentObject<?>> alo )
	{
		ABCClassloader pluginLoader = (ABCClassloader) ((ABCFactory) factory).getLoader();
		
		if ( pluginLoader != null ) 
		{
			if ( ! pluginLoader.isValid() ) ((ABCFactory) factory).initializeFactory();
		
			try 
			{
				try 
				{
					pluginType = FactoryUtils.cleanupClassName(pluginType, pluginLoader);
				}
				catch (ClassNotFoundException e) 
				{
					return null;
				}
				
				Class<?> myPluginClass = pluginLoader.loadPluginClass(pluginType);
			
				if ( myPluginClass != null ) 
				{
					Object instanceOfPlugin = ((ABCFactory) factory).instantiatedObjectFromLoader(pluginLoader, pluginType, myPluginClass, alo);
				
					return Cast.cast(instanceOfPlugin);
				} 
			} 
			catch (ReflectiveOperationException | SecurityException exception) 
			{	
				try 
				{
					throw new InstantiationException(exception.getLocalizedMessage());
				} 
				catch ( Exception ignored)
				{}
			}
		}
		
		return null;
	}

	public static ParameterizedObject findMatchingConstructor( final ArrayList<GeneralizedConstructor> gc, final Collection<CompartmentObject<?>> alo )
	{
		if ( gc == null ) return null;

		Pair<Integer, Integer> result = ParameterizedObject.buildSignature(alo);
		if ( result == null || result.isEmpty() ) return null;

		int numObjs = result.first();
		int signature = result.second();

		for (GeneralizedConstructor c : gc)
		{
			if (c.getParameterizedObject() == null && numObjs == 0) return c.getParameterizedObject();
			if (c.getParameterizedObject().getNumberElements() == 0 && numObjs == 0) return c.getParameterizedObject();

			int numberInputs = 0;
			if (c.getParameterizedObject().getObjectTypes() != null)
			{
				numberInputs = c.getParameterizedObject().getObjectTypes().size();
			}
			int gcSignature = c.getSignature(false);//c.getParameterizedObject().getSignature();

			if (numberInputs == numObjs && gcSignature == signature) return c.getParameterizedObject();
		}

		return null;

	}

	public static Map<String, String> findClassTypes(final String basePath, final String subsystem, final String kwMatch)
	{
		Map<String,String> foundFactoryClasses = new TreeMap<>();

		if ( !TextManager.validString(basePath) || ! TextManager.validString(subsystem) ) return null;
		boolean canReplace = TextManager.validString(kwMatch);

		File knownTypesDir = new File(FileDirUtils.joinPath(basePath, FactoryUtils.PLUGIN_DIRECTORY, ClassUtils.convertLoaderToFile(subsystem)));
		if ( FileDirUtils.directoryExists(knownTypesDir) )
		{
			File[] files = knownTypesDir.listFiles();
			if ( files != null )
			{
				try {
					URL[] urls = new URL[]{knownTypesDir.toURI().toURL()};
					ClassLoader custom = new URLClassLoader(urls);

					for (File f : files)
					{
						String shortPath = ClassUtils.convertFileToLoader(f.getAbsolutePath().replace(basePath,"").replace(".class",""));
						try
						{
							Class<?> fcl = custom.loadClass(shortPath);
							if (Modifier.isAbstract(fcl.getModifiers())) continue;
						}
						catch (ClassNotFoundException e)
						{
							FactoryUtils.logger.warn("Unable to determine " + TextManager.specializeName(f.getAbsolutePath()) + " as a Java class");
							continue;
						}

						String methodName = f.getName().replace(".class", "");
						String methodType = methodName;
						if (canReplace) methodType = methodName.replaceFirst(kwMatch, "");

						// Skip exceptions which may be defined...
						if (methodType.contains("Exception")) continue;

						foundFactoryClasses.put(methodType, methodName);
					}
				}
				catch ( MalformedURLException ignored)
				{}
			}
		}
		return foundFactoryClasses;
	}

	private static String getFactoryAsString( Integer numTabs, final ABCFactory factory )
	{
		if ( factory == null ) return null;
		if ( numTabs == null || numTabs < 0 ) numTabs = 0;

		return factory.getPrettyPrint(numTabs);
	}
}