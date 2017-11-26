package com.testanywhere.core.classes.managers;

import com.testanywhere.core.classes.factory.ABCFactory;
import com.testanywhere.core.utilities.class_support.BaseClass;
import com.testanywhere.core.utilities.logging.*;

public abstract class ABCManager extends OutputDisplay implements ManagerInterface
{
	private String     managerName;
	private Class<?>   managerType;
	private boolean    valid;
	private ABCFactory factory;

	protected ABCManager()
	{
		super();
		this.__initialize();
	}

	protected void setFactory( final ABCFactory factory )
	{
		if ( factory != null )
			this.factory = factory;
	}

	@Override
	public String getManagerName() 
	{
		if ( TextManager.validString(this.managerName) ) return this.managerName;
		return null;
	}

	@Override
	public Class<?> getManagerType() 
	{
		if ( this.managerType != null ) return this.managerType;
		return null;
	}
	
	@Override
	public void setManagerName(final String name)
	{
		if ( TextManager.validString(name) ) this.managerName = name;
	}

	@Override
	public void setManagerType(final Class<?> classType)
	{
		if ( classType != null ) this.managerType = classType;
	}

	public void setValid( final boolean validity )
	{
		this.valid = validity;
	}

	@Override
	public void configure() 
	{
		this.initialize();
	}
	
	@Override
	public void initialize() 
	{
		this.validate();
	}
	
	@Override
	public void validate() 
	{
		this.setValid(true);
	}

	@Override
	public boolean isValid() 
	{
		return this.valid;
	}

	@Override
	public ABCFactory getFactory()
	{
		return this.factory;
	}

	@Override
	public boolean hasFactory()
	{
		return (this.getFactory() != null);
	}

	@Override
	public boolean isNull() { return false; }

	@Override
	public void buildObjectOutput( int numTabs )
	{
		if ( numTabs < 0 ) numTabs = 0;
		Tabbing tabEnvironment = new Tabbing(numTabs);
		DisplayManager dm = this.getDM();

		String outerSpacer = tabEnvironment.getSpacer();
		dm.append(outerSpacer + "Manager (" + BaseClass.checkIsNull(this.getManagerName()) + ") :", DisplayType.TEXTTYPES.LABEL);

		if ( this.getManagerType() != null )
		{
			tabEnvironment.increment();
			String innerSpacer = tabEnvironment.getSpacer();

			dm.append(innerSpacer + "Manager Type : " + this.getManagerType());
			dm.append(innerSpacer + "Is Valid : " + TextManager.StringRepOfBool(this.isValid(), "yn"));
			if ( this.hasFactory() ) {
				this.getFactory().buildObjectOutput(tabEnvironment.numberTabs());
				dm.addFormatLines(this.getFactory().getDM().getLines());
			}
			else
				dm.append(innerSpacer + "Factory : " + BaseClass.checkIsNull(this.getFactory()));
		}
	}

	private void __initialize()
	{
		this.managerName = null;
		this.managerType = null;
		this.valid       = false;
		this.factory     = null;
	}

}
