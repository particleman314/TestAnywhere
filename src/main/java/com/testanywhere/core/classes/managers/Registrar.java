package com.testanywhere.core.classes.managers;

import com.testanywhere.core.utilities.class_support.Cast;
import com.testanywhere.core.utilities.class_support.functional_support.ListFunctions;
import com.testanywhere.core.utilities.logging.*;
import org.apache.log4j.Logger;
import org.xeustechnologies.jcl.JarClassLoader;
import org.xeustechnologies.jcl.context.DefaultContextLoader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

@SuppressWarnings("ExternalizableWithoutPublicNoArgConstructor")
public class Registrar extends OutputDisplay
{
	public static Logger logger;
	public static JarClassLoader jcl;

	static
	{
		Registrar.logger = Logger.getLogger("Registrar");
		Registrar.jcl = new JarClassLoader();
		LogConfiguration.configure();
	}

	private final TreeMap<String, ABCManager> registerManagers;
	private final Set<String> managerPaths2Search;

	private static class RegistrarHolder 
	{ 
    	public static final Registrar INSTANCE = new Registrar();
    }

    public static Registrar getInstance()
    {
    	return RegistrarHolder.INSTANCE;
    }

	@Override
	public boolean isNull() { return false; }

	@Override
	public void buildObjectOutput( int numTabs )
	{
		if ( numTabs < 0 ) numTabs = 0;
		Tabbing tabEnvironment = new Tabbing(numTabs);
		String outerSpacer = tabEnvironment.getSpacer();
		DisplayManager dm = this.getDM();

		dm.append(outerSpacer + "Registrar :", DisplayType.TEXTTYPES.LABEL);

		tabEnvironment.increment();
		String innerSpacer = tabEnvironment.getSpacer();

		dm.append(innerSpacer + "Registered Managers : [ " + this.size() + " ]", DisplayType.TEXTTYPES.LABEL);

		if ( ! this.isEmpty() )
		{
			for ( String s : this.getRegisteredManagers().keySet() )
			{
				ABCManager manager = this.getRegisteredManagers().get(s);
				manager.buildObjectOutput(tabEnvironment.numberTabs());
				dm.addFormatLines(manager.getDM().getLines());
			}
		}

		dm.append(innerSpacer + "Searchable Manager Paths : ", DisplayType.TEXTTYPES.LABEL);
		tabEnvironment.increment();
		dm.addFormatLines(ListFunctions.asNumberedList(this.getManagerPaths(), tabEnvironment));
	}

	public boolean isEmpty()
	{
		return this.getRegisteredManagers().isEmpty();
	}
	
	public int size() 
	{
		return this.getRegisteredManagers().size();
	}

	public void clear()
	{
		this.getRegisteredManagers().clear();
	}

	public boolean addJarPath( final String jarPath )
	{
		if ( ! TextManager.validString(jarPath) ) return false;
		Registrar.jcl.add(jarPath);

		DefaultContextLoader context = new DefaultContextLoader(jcl);
		context.loadContext();

		return true;
	}

	public boolean addManagerPath( final String path )
	{
		if ( ! TextManager.validString(path) ) return false;

		this.managerPaths2Search.add(path);

		Registrar.jcl.add(path);

		DefaultContextLoader context = new DefaultContextLoader(jcl);
		context.loadContext();
		return true;
	}

	public Set<String> getManagerPaths()
	{
		return this.managerPaths2Search;
	}

	public ABCManager registerAndInstantiate(final String managerClassName)
	{
		return this.registerAndInstantiate(null, managerClassName);
	}

	public ABCManager registerAndInstantiate(String managerName, String managerClassName)
	{
		if ( managerClassName == null ) return null;
		
		Class<?> managerClass;
		try 
		{
			if ( TextManager.validString(managerName) )
			{
				managerClassName = managerClassName + "." + managerName;
			}

			managerClass = Registrar.jcl.loadClass(managerClassName, true);
			//Class.forName(managerClassName);
		} 
		catch (ClassNotFoundException e) 
		{
			return null;
		}
		
		if ( managerName == null ) managerName = managerClass.getSimpleName();
		
		Method managerMethod;
		try 
		{
			managerMethod = managerClass.getDeclaredMethod("getInstance", (Class<?>[]) null);
		} 
		catch (NoSuchMethodException | SecurityException e) 
		{
			return null;
		}
		
		try 
		{
			ABCManager managerObj = (ABCManager) managerMethod.invoke(null, (Object[]) null);

			boolean changedName = false;
			String oldManagerName = managerName;

			while ( this.getRegisteredManagers().get(managerName) != null ) 
			{
				managerName = managerName + "_";
				if ( ! changedName ) changedName = true;
			}

			this.assign(managerName, managerObj);
			
			if ( changedName ) 
			{
				Registrar.logger.warn("Changed name of manager from < " + oldManagerName + " > to < " + managerName + " >");
			}
			managerObj.setValid(true);
			return managerObj;
		} 
		catch (IllegalAccessException |
			   IllegalArgumentException |
			   InvocationTargetException e) 
		{
			return null;
		}
	}
	
	public void unregister(final String managerName)
	{
		if ( managerName == null ) return;
				
		if ( this.lookup(managerName) != null ) 
		{
			this.getRegisteredManagers().remove(managerName);
		}
	}
	
	public void register(String managerName, final ABCManager managerSingleton)
	{
		if ( managerName == null || managerSingleton == null ) return;
		
		boolean changedName = false;
		String oldManagerName = managerName;
		
		while ( this.lookup(managerName) != null ) 
		{
			managerName = managerName + "_";
			if ( ! changedName ) changedName = true;
		}
		
		if ( changedName ) 
		{
			Registrar.logger.warn("Changed name of manager from " + TextManager.specializeName(oldManagerName) + " to " + TextManager.specializeName(managerName));
		}
		
		this.assign(managerName, managerSingleton);
	}
	
	public<T extends ABCManager> T lookup(final String managerName)
	{
		if ( managerName == null ) return null;
		if ( this.getRegisteredManagers().containsKey(managerName) ) 
		{
			return Cast.cast(this.getRegisteredManagers().get(managerName));
		}
		return null;
	}
	
	public static<T extends ABCManager> T getManager( final String managerPath, final String managerID )
	{
		Registrar r = Registrar.getInstance();
		ABCManager abcm = r.lookup(managerID);

		if (abcm == null)
		{
			// This is a loader path not a file path...
			if ( ! r.getManagerPaths().contains(managerPath) )
			{
				Registrar.logger.warn("Adding unknown path to list for search " + TextManager.specializeName(managerPath));
				r.addManagerPath(managerPath);
			}
			r.registerAndInstantiate(managerPath + "." + managerID );
			abcm = r.lookup(managerID);
		}
		return Cast.cast(abcm);
	}

	@SuppressWarnings("unchecked")
	public static<T> T getDefaultManager(final String managerID )
	{
		Registrar r = Registrar.getInstance();

		if ( r.getManagerPaths().isEmpty() ) r.addManagerPath("com.nimsoft.managers");
		for ( String lp : r.getManagerPaths() )
		{
			ABCManager m = Registrar.getManager(lp, managerID);
			if (m != null) return (T) m;
		}

		return null;
	}

	private Registrar()
	{
		this.registerManagers = new TreeMap<>();
		this.managerPaths2Search = new TreeSet<>();

		this.managerPaths2Search.add("com.nimsoft.managers");
	}

	private void assign( final String managerName, final ABCManager managerObj )
	{
		if ( managerName == null || managerObj == null ) return;
		
		this.getRegisteredManagers().put(managerName, managerObj);
	}
	
	private TreeMap<String, ABCManager> getRegisteredManagers() 
	{
		return this.registerManagers;
	}
}
