package com.testanywhere.core.machines.connections;

import com.testanywhere.core.machines.actions.Action;
import com.testanywhere.core.classes.support.ReturnInfo;
import com.testanywhere.core.os.classes.support.process.CommandLine;

import java.util.Properties;

public interface ConnectionInterface 
{
	boolean hasConnection();
	boolean connect();
	boolean connect(Properties props);
	
	String getConnectionType();
	ReturnInfo executeAction(Action action);
	ReturnInfo executeCommand(CommandLine cl);
}
