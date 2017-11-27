package com.testanywhere.core.machines.factories;

import com.testanywhere.core.classes.class_support.CompartmentObject;
import com.testanywhere.core.classes.factory.ABCFactory;
import com.testanywhere.core.classes.factory.FactoryUtils;
import com.testanywhere.core.utilities.logging.TextManager;
import com.testanywhere.core.machines.machinetypes.MachineType;
import com.testanywhere.core.machines.loaders.MachineTypeLoader;
import com.testanywhere.core.classes.utilities.ClassUtils;

import java.util.Collection;

public class MachineTypeFactory extends ABCFactory
{
	public MachineTypeFactory() 
	{
		// Allow superclass to be created and initialized
		super();
		
		// Setup logging for this derived class
		this.debug("Instantiating a " + TextManager.specializeName(this.getClass().getSimpleName() + "..."));
		
		// Complete the initialization of the derived class
		this.initializeFactory();
	}

	@Override
	public MachineType instantiate(Class<?> type, Collection<CompartmentObject<?>> alo )
	{
		return FactoryUtils.instantiateObject(this, type, alo);
	}

	@Override
	public MachineType instantiate( String pluginType, Collection<CompartmentObject<?>> alo )
	{
		return FactoryUtils.instantiateObject(this, pluginType, alo);
	}
	
	@Override
	public void initializeFactory() 
	{
		if ( ! super.isInitialized() )
		{
			// This is the location of where we want to begin the initialization process based on the fact
			// a ConnectionLoader is attached to this ConnectionFactory
			MachineTypeLoader pluginLoader = new MachineTypeLoader();
			String parentDir = ClassUtils.getParentClass(this.getClass().getPackage().getName());
			pluginLoader.initializeLoader(parentDir + ".machinetypes.types");
			
			this.setLoader(pluginLoader);
			FactoryUtils.initializeFactorySettings(this, pluginLoader.getClass());
			
			super.setInitialized(true);
		}
	}

	@Override
	public void findFactoryClasses() {
		this.updateFactoryClasses(((MachineTypeLoader) this.getLoader()).getPackageID(), "Type");
	}
}
