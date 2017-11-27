package com.testanywhere.core.machines.factories;

import com.testanywhere.core.classes.class_support.CompartmentObject;
import com.testanywhere.core.machines.connections.ConnectionClient;
import com.testanywhere.core.machines.connections.ConnectionConstants;
import com.testanywhere.core.classes.factory.ABCFactory;
import com.testanywhere.core.classes.factory.FactoryUtils;
import com.testanywhere.core.machines.loaders.ConnectionLoader;
import com.testanywhere.core.utilities.logging.TextManager;
import com.testanywhere.core.classes.utilities.ClassUtils;

import java.util.Collection;

public class ConnectionFactory extends ABCFactory
{
	public ConnectionFactory() 
	{
		super();
		this.info("Instantiating a " + TextManager.specializeName(this.getClass().getSimpleName()) + "...");
		this.initializeFactory();
	}

	public ConnectionClient instantiate(Class<?> type, Collection<CompartmentObject<?>> alo )
	{
		return FactoryUtils.instantiateObject(this, type, alo);
	}
	
	public ConnectionClient instantiate(String pluginType, Collection<CompartmentObject<?>> alo )
	{
		return FactoryUtils.instantiateObject(this, pluginType, alo);
	}
	
	@Override
	public void initializeFactory() 
	{
		if ( ! super.isInitialized() )
		{
			ConnectionLoader pluginLoader = new ConnectionLoader();
			String parentDir = ClassUtils.getParentClass(this.getClass().getPackage().getName());
			pluginLoader.initializeLoader( parentDir + ".connections.types");
			this.setLoader(pluginLoader);

			FactoryUtils.initializeFactorySettings(this, pluginLoader.getClass());
			
			super.setInitialized(true);
		}
	}

	@Override
	public void findFactoryClasses() {
		this.updateFactoryClasses(((ConnectionLoader) this.getLoader()).getPackageID(), ConnectionConstants.CLIENT_CLASS);
	}
}
