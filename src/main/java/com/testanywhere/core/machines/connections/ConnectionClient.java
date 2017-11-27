package com.testanywhere.core.machines.connections;

import com.testanywhere.core.classes.class_support.CompartmentObject;
import com.testanywhere.core.classes.support.ReturnInfo;
import com.testanywhere.core.classes.support.map.DynamicMap;
import com.testanywhere.core.classes.utilities.ErrorUtils;
import com.testanywhere.core.machines.actions.Action;
import com.testanywhere.core.machines.actions.ActionSequence;
import com.testanywhere.core.machines.machinetypes.MachineType;
import com.testanywhere.core.os.classes.support.process.CommandLine;
import com.testanywhere.core.utilities.class_support.BaseClass;
import com.testanywhere.core.utilities.class_support.Cast;
import com.testanywhere.core.utilities.logging.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public abstract class ConnectionClient extends OutputDisplay implements ConnectionInterface
{
	protected String connectionType;
	protected MachineType machineInfo;

	protected ActionSequence actions;
	protected ReturnInfo lastConnectionResult;

	private boolean cache;
	private String alias;
	private UUID uuid;

	private DynamicMap<String> environment;
	private DynamicMap<String> storedData;

	private ConnectionState state;

	static
	{
		ConnectionConstants.getInstance(); // Need to initialize
	}

	public ConnectionClient()
	{
		super();
		this.initialize();
	}

	@Override
	public String getConnectionType()
	{
		return this.connectionType;
	}
	
	@Override
    public void buildObjectOutput( int numTabs )
	{
		if ( numTabs < 0 ) numTabs = 0;
		Tabbing tabEnvironment = new Tabbing(numTabs);
		DisplayManager dm = this.getDM();

		String outerSpacer = tabEnvironment.getSpacer();
		dm.append(outerSpacer + "Connection :", DisplayType.TEXTTYPES.LABEL);
		
		tabEnvironment.increment();
		String innerSpacer = tabEnvironment.getSpacer();

		dm.append(innerSpacer + "Connection Type : " + this.getConnectionType());
		dm.append(innerSpacer + "Last Connection Result : " + BaseClass.checkIsNull(this.lastConnectionResult.toString()));
			
		if ( this.getReplay() == null )
		{
			dm.append(innerSpacer + "Number Recorded Actions : 0");
		}
		else
		{
			dm.append(innerSpacer + "Number Recorded Actions : " + this.getReplay().size());
		}

		dm.append(innerSpacer + "UUID : " + BaseClass.checkIsNull(this.getUUIDKey()));
		dm.append(innerSpacer + "Alias : " + BaseClass.checkIsNull(this.getAlias()));
		tabEnvironment.increment();

		this.getState().buildObjectOutput(tabEnvironment.numberTabs());
		dm.addFormatLines(this.getState().getDM().getLines());
		dm.append(innerSpacer + "Recorded Environment :", DisplayType.TEXTTYPES.LABEL);

		this.getEnvironment().buildObjectOutput(tabEnvironment.numberTabs());
		dm.addFormatLines(this.getEnvironment().getDM().getLines());
    }
    
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("T:" + TextManager.specializeName(this.getConnectionType()) + TextManager.STR_OUTPUTSEPARATOR);
		sb.append("ST:" + this.getState().toString() + TextManager.STR_OUTPUTSEPARATOR);
		sb.append("U:" + TextManager.specializeName(this.getUUIDKey().toString()) + TextManager.STR_OUTPUTSEPARATOR);
		sb.append("A:" + TextManager.specializeName(this.getAlias()) + TextManager.STR_OUTPUTSEPARATOR);
		sb.append("AR:" + this.getReplay().toString() + TextManager.STR_OUTPUTSEPARATOR);
		sb.append("M:" + this.getMachineInfo().toString()  + TextManager.STR_OUTPUTSEPARATOR);
		sb.append("CR:" + this.lastConnectionResult.toString() + TextManager.STR_OUTPUTSEPARATOR);
		sb.append("E:" + this.getEnvironment() + TextManager.STR_OUTPUTSEPARATOR);

		return sb.toString();
	}
	
	@Override
	public boolean equals( Object other ) {
		if (other == null) return false;

		ConnectionClient otherC = Cast.cast(other);
		if (otherC == null) return false;

		if (!this.connectionType.equals(otherC.connectionType)) return false;
		return this.getUUIDKey().equals(otherC.getUUIDKey()) && this.getReplay().equals(otherC.getReplay());
	}

	@Override
	public boolean isCopyable()
	{
		return false;
	}

	@Override
	public boolean isAssignable()
	{
		return false;
	}

	public abstract void disconnect();

	public MachineType getMachineInfo() { return this.machineInfo; }

	public ConnectionState getState() { return this.state; }

	public UUID getUUIDKey()
	{
		return this.uuid;
	}
	
	public String getAlias()
	{
		return this.alias;
	}

	public ActionSequence getReplay()
	{
		return this.actions;
	}
	
	public Action getLastAction()
	{
		Collection<?> sequence = this.getReplay().getSequence();
		if ( sequence != null && sequence.size() > 0 ) return (Action) ((ArrayList<?>) sequence).get(sequence.size() - 1);
		return null;
	}

	public String getShell()
	{
		MachineType mt = this.getMachineInfo();
		if ( mt != null ) { return mt.getShell(); }
		return null;
	}
	
	public DynamicMap<String> getEnvironment()
	{
		return this.environment;
	}
	
	@SuppressWarnings("unchecked")
	public<T> T getStoredData( String key )
	{
		if ( ! TextManager.validString(key) ) return null;
		CompartmentObject<?> data = this.getStoredData().getDataPair(key);
		if ( data == null ) return null;
		return (T) Cast.safeCast(data.getObject(), data.getClassType());
	}

	public void setAlias( String alias )
	{
		this.alias = alias;
	}
	
	public void setUUIDKey( UUID key )
	{
		if ( key == null ) key = UUID.randomUUID();
		this.uuid = key;
	}
	
	public void setValid(boolean b) { this.getState().setValid(b); }

	public boolean isValid() { return this.getState().isValid(); }

	public void setLocal(boolean b) { this.getState().setLocal(b); }

	public boolean isLocal() { return this.getState().isLocal(); }

	public void setBusy(boolean b) { this.getState().setBusy(b); }

	public boolean isBusy() { return ( this.getState().isBusy() ); }
	
	public boolean allowCache() { return this.cache; }

	public void clearReplay()
	{
		if ( this.getReplay() == null ) return;
		this.getReplay().clear();
	}
	
	public<T> void storeData( String key, T value )
	{
		if ( ! TextManager.validString(key) ) return;
		this.storedData.put(key, value);
	}
	
	public MachineType transferOSInfo()
	{
		MachineType mt = this.getMachineInfo();
		if ( mt != null ) this.clearOSInfo();
		return mt;
	}

	@Override
	public ReturnInfo executeCommand(CommandLine c)
	{
		Action action = this.getMachineInfo().generateAction(this, c);
		try
		{
			return this.executeAction(action);
		}
		catch (Exception e)
		{
			return new ReturnInfo(e.getLocalizedMessage(), ErrorUtils.GENERIC_FAIL_ID);
		}
	}

	public void clearOSInfo()
	{
		this.getStoredData().removeData(ConnectionConstants.MACHINE_TYPE_KEY);
	}
	
	public void assignOSInfo(MachineType mt)
	{
		this.getStoredData().setData(ConnectionConstants.MACHINE_TYPE_KEY, new CompartmentObject<>(mt));
	}

	private DynamicMap<String> getStoredData()
	{
		return this.storedData;
	}

	private void initialize()
	{
		this.actions              = null;

		this.lastConnectionResult = ReturnInfo.NO_RESPONSE_NO_ERROR;
		this.connectionType       = this.getClass().getSimpleName();

		this.environment          = new DynamicMap<>();
		this.storedData           = new DynamicMap<>();
		this.cache                = false;
		this.uuid                 = null;

		this.state                = new ConnectionState();
	}
}
