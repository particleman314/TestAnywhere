package com.testanywhere.core.encryption.factories;

import com.testanywhere.core.classes.class_support.CompartmentObject;
import com.testanywhere.core.encryption.utilities.EncryptionUtils;
import com.testanywhere.core.encryption.loaders.EncryptionLoader;
import com.testanywhere.core.encryption.plugins.encryption.EncryptionStrategy;
import com.testanywhere.core.classes.factory.ABCFactory;
import com.testanywhere.core.classes.factory.FactoryUtils;
import com.testanywhere.core.utilities.logging.TextManager;
import com.testanywhere.core.classes.utilities.ClassUtils;

import java.util.Collection;

public class EncryptionFactory extends ABCFactory 
{
	public EncryptionFactory() 
	{
		super();
		this.info("Instantiating a " + TextManager.specializeName(this.getClass().getSimpleName()) + "...");
		this.initializeFactory();
	}

	@Override
	public EncryptionStrategy instantiate(Class<?> type, Collection<CompartmentObject<?>> alo )
	{
		return FactoryUtils.instantiateObject(this, type, alo);
	}

	@Override
	public EncryptionStrategy instantiate(String pluginType, Collection<CompartmentObject<?>> alo )
	{
		return FactoryUtils.instantiateObject(this, pluginType, alo);
	}

	@Override
	public void findFactoryClasses()
	{
		this.updateFactoryClasses("encryption",EncryptionUtils.STRATEGYNAME);
	}

	@Override
	public void initializeFactory() 
	{
		if ( ! super.isInitialized() )
		{
			EncryptionLoader pluginLoader = new EncryptionLoader();
			String parentDir = ClassUtils.getParentClass(this.getClass().getPackage().getName());
			pluginLoader.initializeLoader(parentDir + ".plugins.encryption");
			this.setLoader(pluginLoader);

			FactoryUtils.initializeFactorySettings(this, pluginLoader.getClass());
			
			super.setInitialized(true);
		}
	}
}
