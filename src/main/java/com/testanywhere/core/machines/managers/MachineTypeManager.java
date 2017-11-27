package com.testanywhere.core.machines.managers;

import com.testanywhere.core.machines.factories.MachineTypeFactory;
import com.testanywhere.core.utilities.logging.TextManager;
import com.testanywhere.core.machines.machinetypes.MachineType;
import com.testanywhere.core.machines.machinetypes.MachineTypeConstants;

import com.testanywhere.core.classes.managers.ABCManager;
import com.testanywhere.core.classes.managers.Registrar;

@SuppressWarnings("ExternalizableWithoutPublicNoArgConstructor")
public class MachineTypeManager extends ABCManager
{
	static
	{
		MachineTypeConstants.getInstance();
		Registrar.getInstance().addManagerPath(MachineTypeManager.class.getPackage().getName());
	}
	
    private static class MachineTypeManagerHolder 
    { 
    	public static final MachineTypeManager INSTANCE = new MachineTypeManager();
    }

    public static MachineTypeManager getInstance() 
    {
    	return MachineTypeManagerHolder.INSTANCE;
    }

	private MachineTypeManager() 
	{
		Class<?> clazz = MachineTypeManager.class;

		super.setManagerType(clazz);
		super.setManagerName(clazz.getName());
		super.configure();
		
		this.setFactory( new MachineTypeFactory() );
	}

	@Override
	public void reset() 
	{
		this.getFactory().reset();
		this.setValid(true);
	}

	@Override
	public void cleanup() 
	{}
	
	// Standard API Methods
	public MachineType getType(String type )
	{
		if ( ! TextManager.validString(type) ) return null;
		return this.getFactory().instantiate(type, null);
	}
	
	@Override
	public MachineTypeFactory getFactory()
	{
		return (MachineTypeFactory) super.getFactory();
	}
}