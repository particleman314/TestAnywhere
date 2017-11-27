package com.testanywhere.core.machines.machinetypes;

import com.testanywhere.core.machines.actions.Action;
import com.testanywhere.core.classes.support.version.Version;
import com.testanywhere.core.machines.connections.ConnectionClient;
import com.testanywhere.core.os.classes.support.process.CommandLine;

public interface MachineTypeInterface
{
	void decodeEnvironment(String data);
	
	String getExecutableExtension();
	boolean is64BitCompliant();
	String getVersion();
	String getMachineClassification();
	String getMachineSpecification();
	String getEnvQueryMethod();
	String getShell();
	String getCommandSeparator();
	String getDirSeparator();
	//public String getServiceManager();

	void findMachineClassification(ConnectionClient cc);
	void findMachineSpecifications(ConnectionClient cc);
	void findMachineVersion(ConnectionClient cc);
	void determineIf64BitCompliant(ConnectionClient cc);
	
	void setMachineSpecifications(String data);
	void setMachineSpecifications(String machineInfo, boolean is64Bit, Version machVer);
	
	Action generateAction(ConnectionClient cc, CommandLine cl);
}
