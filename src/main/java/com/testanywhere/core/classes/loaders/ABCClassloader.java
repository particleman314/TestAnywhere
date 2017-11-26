package com.testanywhere.core.classes.loaders;

import com.testanywhere.core.classes.class_support.GeneralizedConstructor;
import com.testanywhere.core.classes.class_support.ParameterizedObject;
import com.testanywhere.core.utilities.class_support.ClassWrapper;
import com.testanywhere.core.utilities.class_support.functional_support.ListFunctions;
import com.testanywhere.core.utilities.logging.DisplayManager;
import com.testanywhere.core.utilities.logging.DisplayType;
import com.testanywhere.core.utilities.logging.Tabbing;
import com.testanywhere.core.utilities.logging.TextManager;
import org.xeustechnologies.jcl.JarClassLoader;
import org.xeustechnologies.jcl.context.DefaultContextLoader;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public abstract class ABCClassloader extends OutputClassLoader implements ClassloaderInterface 
{
	private String                                         loaderName;
	private Class<?>                                       loaderType;
	private String                                         packageID;
	private String                                         subPackageID;
	private boolean                                        valid;
	private Map<String, Class<?>>                          constructorClasses;
	private Map<String, ArrayList<GeneralizedConstructor>> constructorObjs;
	private Map<String, String>                            nickName;

	private boolean initialized;

	protected ABCClassloader() 
	{
		super();
		this.__initialize();	
	}
	
	@Override
	public Class<?> loadPluginClass(final String className)
	{
		if ( !TextManager.validString(className) ) return null;
		try 
		{
			return super.loadClass(className);
		} 
		catch (ClassNotFoundException e)
		{
			return null;
		}
	}

	@Override
	public void reset() 
	{
		this.info("Reset the " + this.getClass().getSimpleName() + " Class Loader by setting it invalid...");
		this.clearAllClasses();
	}

	@Override
	public Class<?> getLoaderType() 
	{
		return this.loaderType;
	}
	
	@Override
	public String getLoaderName() 
	{
		return this.loaderName;
	}

	@Override
	public void setLoaderName(final String name) {
		if ( name != null ) this.loaderName = name;
	}

	@Override
	public void setLoaderType(final Class<?> classType)
	{
		if ( classType != null ) this.loaderType = classType;
	}

	@Override
	public void buildObjectOutput( int numTabs )
	{
		if ( numTabs < 0 ) numTabs = 0;
		Tabbing tabEnvironment = new Tabbing(numTabs);
		DisplayManager dm = this.getDM();

		String outerSpacer = tabEnvironment.getSpacer();
		dm.append(outerSpacer + "ClassLoader :", DisplayType.TEXTTYPES.LABEL);

		tabEnvironment.increment();
		String innerSpacer = tabEnvironment.getSpacer();

		dm.append(innerSpacer + "Loader Name : " + this.getLoaderName());
		dm.append(innerSpacer + "Loader Type : " + this.getLoaderType());
		dm.append(innerSpacer + "Package ID  : " + this.getPackageID());
		dm.append(innerSpacer + "Is Valid    : " + TextManager.StringRepOfBool(this.isValid(), "yn"));

		Map<String, Class<?>> ctorClasses = this.getConstructorClassMap();
		if ( ! ctorClasses.keySet().isEmpty() )
		{
			dm.append(innerSpacer + "Constructor Classes :", DisplayType.TEXTTYPES.LABEL);

			tabEnvironment.increment();
			ListFunctions.asNumberedList(ctorClasses.keySet(), tabEnvironment);

			/*for ( String s : ctorClasses.keySet() )
			{
				sb.append(subInnerSpacer + "Name : " + s + " : " + ctorClasses.get(s).toString() + TextManager.EOL);
			}*/
			tabEnvironment.decrement();
		}

		Map<String, ArrayList<GeneralizedConstructor>> ctorObjs = this.getConstructorObjMap();
		if ( ! ctorObjs.keySet().isEmpty() )
		{
			dm.append(innerSpacer + "Constructor Objects :", DisplayType.TEXTTYPES.LABEL);
			tabEnvironment.increment();
			String subInnerSpacer = tabEnvironment.getSpacer();

			for ( String s : ctorObjs.keySet() )
			{
				dm.append(subInnerSpacer + "Name : " + s);
				for ( GeneralizedConstructor g : ctorObjs.get(s) )
				{
					this.getDM().append(g.getPrettyPrint(tabEnvironment.numberTabs()));
				}
			}
			tabEnvironment.decrement();
		}

		Map<String, String> nicks = this.getNickNameMap();
		if ( ! nicks.keySet().isEmpty() )
		{
			dm.append(innerSpacer + "Nickname Map :", DisplayType.TEXTTYPES.LABEL);
			tabEnvironment.increment();
			String subInnerSpacer = tabEnvironment.getSpacer();

			for ( String key : nicks.keySet() )
			{
				dm.append(subInnerSpacer + "<K,V> = " + TextManager.specializeName(key + " , " + nicks.get(key)));
			}
			tabEnvironment.decrement();
		}
	}

	@Override
	public Class<?> loadClass(final String className)
	{
		return this.findClass(className);
	}
	
	@Override
	public Class<?> findClass(final String className)
	{
		this.installCustomClass(className);
		return this.instantiateCustomClass(className);
	}
	
	public String getPackageID() 
	{
		if ( this.subPackageID != null ) return this.packageID + "." + this.subPackageID;
		return this.packageID;
	}
	
	public Set<String> getClassNames() 
	{
		return this.getConstructorClassMap().keySet();
	}
	
	public Map<String, String> getNickNameMap() 
	{
		return this.nickName;
	}
	
	public Map<String, Class<?>> getConstructorClassMap() 
	{
		return this.constructorClasses;
	}
	
	public Map<String, ArrayList<GeneralizedConstructor>> getConstructorObjMap() 
	{
		return this.constructorObjs;
	}

	public ArrayList<GeneralizedConstructor> getClassConstructors( String className )
	{
		className = this.resolveName(className);
		
		if ( this.getConstructorObjMap().containsKey(className) ) return this.getConstructorObjMap().get(className);
		this.installCustomClass(className);
		if ( this.getConstructorObjMap().containsKey(className) ) return this.getConstructorObjMap().get(className);
		return null;
	}

	public void setPackageID (final String packageID)
	{
		this.packageID = packageID;
	}
	
	public void setSubPackageID (final String subPackageID )
	{
		this.subPackageID = subPackageID;
	}
	
	public void setValid(final boolean validity)
	{
		this.valid = validity;
	}
	
	public boolean isValid() 
	{
		return this.valid;
	}
	
	protected Boolean hasClass(final String className)
	{
		if ( className != null ) 
		{
			if ( this.getNickNameMap().containsKey(className) ) return true;
			if ( this.getConstructorClassMap().containsKey(className) ) return true;
		}
		return false;
	}
	
	protected void clearClass(final String className)
	{
		if ( this.hasClass(className) ) 
		{
			try 
			{
				this.getNickNameMap().remove(className);
				this.getConstructorClassMap().remove(className);
			} 
			catch ( UnsupportedOperationException | ClassCastException | NullPointerException ignored)
			{}
		}
	}

	protected void clearAllClasses()
	{
		this.getNickNameMap().clear();
		this.getConstructorClassMap().clear();
		this.getConstructorObjMap().clear();
	}

	public void loadClassesFromJar( final File filepath2Jar )
	{
		if ( ! filepath2Jar.exists() || ! filepath2Jar.isFile() || ! filepath2Jar.canRead() ) return;

		JarClassLoader jcl = new JarClassLoader();
		jcl.add(filepath2Jar.getAbsolutePath());
		DefaultContextLoader context=new DefaultContextLoader(jcl);
		context.loadContext();
	}

	public void loadClassesFromJar( final String path2Jar )
	{
		if ( ! TextManager.validString(path2Jar) ) return;

		File filepath2Jar = new File(path2Jar);
		this.loadClassesFromJar(filepath2Jar);
	}

	public boolean isInitialized()
	{
		return this.initialized;
	}

	protected void setInitialized(final boolean isInit)
	{
		this.initialized = isInit;
	}

	@SuppressWarnings("resource")
	private void installCustomClass(String className)
	{
		String nickName = className.replace(this.getPackageID() + ".", "");
		className       = this.resolveName(className);
		String fullName = className;
		//String fullName = this.getPackageID() + "." + nickName;

		if ( this.getConstructorClassMap().containsKey(fullName) ) return;

		Class<?> result;

		try
		{
			URL [] urls = new URL[] {new File(System.getProperty("user.dir") + File.separator + "").toURI().toURL()};
			ClassLoader custom = new URLClassLoader(urls);

			// Get the class
			result = custom.loadClass(fullName);
			if ( result != null )
			{
				this.getConstructorClassMap().put(fullName, result);
				this.getNickNameMap().put(nickName, fullName);

				// Get the public constructor(s) for this class and begin filling table...
				@SuppressWarnings("rawtypes")
				Constructor[] allConstructors = result.getDeclaredConstructors();
				this.getConstructorObjMap().put(className, null);

				for ( @SuppressWarnings("rawtypes") Constructor ctor : allConstructors)
				{
					ParameterizedObject gcpo_mod = new ParameterizedObject();
					ParameterizedObject gcpo_orig = new ParameterizedObject();
					
					//Determine the types of parameters for each constructor
					Class<?>[] pType = ctor.getParameterTypes();
					List<Boolean> modifiedSignatures = new LinkedList<>();
					for ( Class<?> c : pType ) 
					{
						if ( ClassWrapper.isBaseClassType(c) )
						{
							gcpo_orig.addPairing(c, null);
							gcpo_mod.addPairing(ClassWrapper.convertToWrapperType(c), null);
							modifiedSignatures.add(true);
						}
						else
						{
							gcpo_orig.addPairing(c, null);
							gcpo_mod.addPairing(c, null);
							modifiedSignatures.add(false);
						}
					}

					// Assign a distinct name for each constructor based on number of inputs and type signature
					String gcName = nickName + pType.length + "_" + gcpo_orig.getSignature();

					GeneralizedConstructor gc = new GeneralizedConstructor(gcName, gcpo_orig);
					try
					{
						this.getConstructorObjMap().get(className).add(gc);
						if ( ! gcpo_mod.equals(gcpo_orig) )
						{
							String gcNameMod = nickName + pType.length + "_" + gcpo_mod.getSignature();
							GeneralizedConstructor gcmod = new GeneralizedConstructor(gcNameMod, gcpo_orig);
							this.getConstructorObjMap().get(className).add(gcmod);
						}
					}
					catch ( NullPointerException npe )
					{
						this.getConstructorObjMap().put(className, new ArrayList<GeneralizedConstructor>());
						this.getConstructorObjMap().get(className).add(gc);
						if ( ! gcpo_mod.equals(gcpo_orig) )
						{
							String gcNameMod = nickName + pType.length + "_" + gcpo_mod.getSignature();
							GeneralizedConstructor gcmod = new GeneralizedConstructor(gcNameMod, gcpo_orig);
							this.getConstructorObjMap().get(className).add(gcmod);
						}
					}
				}
			}
		}
		catch ( SecurityException | ClassNotFoundException | NullPointerException | ClassCastException | MalformedURLException e )
		{
			this.getConstructorClassMap().remove(fullName);
			this.getConstructorObjMap().remove(fullName);
			this.getNickNameMap().remove(nickName);
		}
	}

	private Class<?> instantiateCustomClass( String className )
	{
		if ( className != null )
		{
			if ( this.getNickNameMap().containsKey(className) ) className = this.getNickNameMap().get(className);
			if ( this.getConstructorClassMap().containsKey(className) ) return this.getConstructorClassMap().get(className);
		}
		return null;
	}
	
	private String resolveName( String className ) 
	{
		if ( this.getNickNameMap().containsKey(className) ) 
		{
			className = this.getNickNameMap().get(className);
		}
		return className;
	}
	
	private void __initialize()
	{
		this.loaderName   = null;
		this.loaderType   = null;
		this.packageID    = null;
		this.subPackageID = null;
		this.valid        = false;
		this.constructorClasses = Collections.synchronizedMap( new TreeMap<String, Class<?>>() );
		this.constructorObjs    = Collections.synchronizedMap( new TreeMap<String, ArrayList<GeneralizedConstructor>>() );
		this.nickName           = Collections.synchronizedMap( new TreeMap<String, String>() );
		this.initialized  = false;
		
		this.setLog(this);
	}
}