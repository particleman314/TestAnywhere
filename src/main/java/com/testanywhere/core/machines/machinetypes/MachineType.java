package com.testanywhere.core.machines.machinetypes;

import com.testanywhere.core.classes.support.cache.Cache;
import com.testanywhere.core.classes.support.version.Version;
import com.testanywhere.core.machines.actions.Action;
import com.testanywhere.core.machines.connections.ConnectionClient;
import com.testanywhere.core.os.classes.support.process.CommandLine;
import com.testanywhere.core.utilities.class_support.BaseClass;
import com.testanywhere.core.utilities.class_support.Cast;
import com.testanywhere.core.utilities.classes.Pair;
import com.testanywhere.core.utilities.logging.*;
import org.apache.commons.lang3.StringUtils;

public abstract class MachineType extends OutputDisplay implements MachineTypeInterface
{
	protected Pair<String, String> machineType;

	protected Cache<String> machineDetails;
	protected Cache<String> commands;

	protected boolean is64BitCompliant;
	protected Version version;

	private Cache<String> cachedData;

	static
	{
		MachineTypeConstants.getInstance(); // Need to initialize
	}
	
	public MachineType()
	{
		super();
		this.initialize();
	}

	@Override
	public void buildObjectOutput(int numTabs)
	{
		if ( numTabs < 0 ) numTabs = 0;
		Tabbing tabEnvironment = new Tabbing(numTabs);
		DisplayManager dm = this.getDM();

		String outerSpacer = tabEnvironment.getSpacer();
		dm.append(outerSpacer + "Machine Type :", DisplayType.TEXTTYPES.LABEL);
		
		tabEnvironment.increment();
		String innerSpacer = tabEnvironment.getSpacer();

		dm.append(innerSpacer + "Machine Type : " + BaseClass.checkIsNull(this.getMachineType()));
		dm.append(innerSpacer + "Machine Classification : " + BaseClass.checkIsNull(this.getMachineClassification()));
		dm.append(innerSpacer + "Machine Specification : " + BaseClass.checkIsNull(this.getMachineSpecification()));
		dm.append(innerSpacer + "Is 64 Bit Compliant : " + TextManager.StringRepOfBool(this.is64BitCompliant(),"yn"));
		dm.append(innerSpacer + "Executable Extension : " + BaseClass.checkIsNull(this.getExecutableExtension()));

		dm.append(innerSpacer + "Version : " + BaseClass.checkIsNull(this.getVersion()));
		dm.append(innerSpacer + "Shell Extension : " + BaseClass.checkIsNull(this.getShell()));
	}

	@Override
	public void setMachineSpecifications( String specifications )
	{
		if ( ! TextManager.validString( specifications ) ) return;
		this.machineDetails.put(MachineTypeConstants.SPECIFICATION_KEY, specifications);
	}

	@Override
	public void setMachineSpecifications( String machineInfo, boolean is64Bit, Version machVer )
	{
		this.setMachineSpecifications(machineInfo);

		this.is64BitCompliant = is64Bit;
		this.version = machVer;
	}

	@Override
	public String getCommandSeparator()
	{
		return Cast.cast(this.machineDetails.get(MachineTypeConstants.CMD_SEPARATOR));
	}

	@Override
	public String getDirSeparator()
	{
		return Cast.cast(this.machineDetails.get(MachineTypeConstants.DIR_SEPARATOR));
	}

	@Override
	public String getMachineClassification()
	{
		return Cast.cast(this.machineDetails.get(MachineTypeConstants.CLASSIFICATION_KEY));
	}

	@Override
	public String getMachineSpecification()
	{
		return Cast.cast(this.machineDetails.get(MachineTypeConstants.SPECIFICATION_KEY));
	}

	@Override
	public String getExecutableExtension() { return Cast.cast(this.machineDetails.get(MachineTypeConstants.EXE_EXTENSION)); }

	@Override
	public String getShell()
	{
		return Cast.cast(this.machineDetails.get(MachineTypeConstants.SHELL));
	}

	@Override
	public String getEnvQueryMethod()
	{
		return this.getCommand("query");
	}

	@Override
	public boolean is64BitCompliant()
	{
		return this.is64BitCompliant;
	}

	@Override
	public String getVersion()
	{
		if ( this.version == null ) return null;
		return this.version.getCanonicalVersion();
	}

	public void setMachineClassification( String classification )
	{
		if ( ! TextManager.validString( classification ) ) return;
		this.machineDetails.put(MachineTypeConstants.CLASSIFICATION_KEY, classification);
	}

	public String getCommand( String requestedCmd ) { return Cast.cast(this.commands.get(requestedCmd)); }

	//public String getActiveProcesses()
	//{
	//	return this.getCommand("taskList");
	//}
	//public String getKillMethod()
	//{
	//	return this.getCommand("kill");
	//}

	public void setMachineType( String type )
	{
		if ( ! TextManager.validString(type) ) return;
		this.machineType.setL(type);
	}

	public Pair<String, String> getMachineType()
	{
		return this.machineType;
	}
	public String getOSMachineType() { return this.machineType.first(); }
	public String getSpecificMachineType() { return this.machineType.second(); }


	/*public void addUIMData()
	{
		if ( this.UIMmachine == null )
		{
			this.UIMmachine = new NimStructure();
			if ( this instanceof UnixType )
			{
				this.getUIMData().setNMSHome(MachineTypeConstants.DEFAULT_UNIX_NMSHOME);
			}
			if ( this instanceof WindowsType )
			{
				WindowsType wt = ClassManagement.cast(this);
				if (wt != null)
				{
					wt.determineBaseNimsoftDirectory();
				}
			}
		}
	}

	public boolean isUIMEnabled() { return ( this.UIMmachine != null ); }

	public NimStructure getUIMData() { return this.UIMmachine; }

	public void setUIMData( NimStructure ns ) { this.UIMmachine = ns; }

	public String getNimSoftHome()
	{
		String NMSHome = null;
		if ( this.isUIMEnabled() )
		{
			NMSHome = this.getUIMData().getNMSHome();
		}
		return NMSHome;
	}

	public void setNimSoftHome( String NMSHome )
	{
		if ( this.isUIMEnabled() )
		{
			if ( ! TextManager.validString(NMSHome) )
			{
				this.removeUIMData();
			}
			else {
				this.getUIMData().setNMSHome(NMSHome);
			}
		}
	}

	public void removeUIMData()
	{
		this.UIMmachine = null;
	}*/

	public String buildPath( String ... pathParts )
	{
		return StringUtils.join(pathParts, this.getDirSeparator());
	}

	public abstract String getTempDir();
	//public abstract String getServiceManager();

	public abstract void determineIf64BitCompliant( ConnectionClient cc );
	public abstract void findMachineVersion( ConnectionClient cc );
	public abstract void findMachineSpecifications( ConnectionClient cc );
	public abstract void findMachineClassification( ConnectionClient cc );

	public abstract void decodeEnvironment( String data );
	//public abstract String getUIMProcessDescriptor( String process_exe );
	//public abstract String getNMSPartialPath();
	public abstract String getProcessTablePattern();

	// MachineType can generate Actions to be executed, but they don't actual run them,
	// They pass the action back to a connection to run...
	public Action executeCommand(ConnectionClient cc, CommandLine cl )
	{
		if ( cl == null || cc == null ) return null;

		Action action = this.generateAction(cc, cl);
		if ( action == null ) return null;
		action.execute(cc);
		return action;
	}
	
	public abstract Action generateAction(ConnectionClient cc, CommandLine cl );
	/*{
		if ( ! cc.isLocal() )
		{
			if ( ! cc.hasConnection() )
			{
				cc.connect();
				if ( ! cc.hasConnection() ) return null;
			}
			
			switch ( cc.getType() )
			{
				case ConnectionConstants.SSH :
				{
					return new SSHCmdAction( command, cmdOpts, workDir );
				}
				case ConnectionConstants.WMI :
				{
					return new WMICmdAction( command, cmdOpts, workDir );
				}
			}
		}
		return new CmdAction( command, cmdOpts, workDir );
	}*/

	private Cache<String> getCache()
	{
		return this.cachedData;
	}

	private void initialize()
	{
		this.machineType      = new Pair<>();
		this.is64BitCompliant = false;
		this.version          = null;

		this.machineDetails   = new Cache<>();
		this.commands         = new Cache<>();
		this.cachedData       = new Cache<>();
	}
}
