package com.testanywhere.core.machines.utilities;

import com.testanywhere.core.machines.connections.ConnectionClient;
import com.testanywhere.core.utilities.logging.LogConfiguration;
import com.testanywhere.core.utilities.logging.MultiOutputStream;
import org.apache.log4j.Logger;

import java.io.OutputStream;
import java.io.PrintStream;

public class ConnectionUtils
{
	public static Logger logger;

	static
	{
		ConnectionUtils.logger = Logger.getLogger("ConnectionUtils");
		LogConfiguration.configure();
	}

	public static void displayConnection(MultiOutputStream mos, int numTabs, ConnectionClient cc)
	{}

	public static void displayConnection(OutputStream os, int numTabs, ConnectionClient cc)
	{
		if ( os == null ) os = System.out;
		final PrintStream ps = new PrintStream(os);
			
		ps.println(cc.getPrettyPrint(numTabs));
	}

	/*public static JIString[] convertToWMICommand( Action a )
	{
		CmdAction cmdA = Cast.cast(a);
		JIString cmdComps[] = new JIString[cmdA.getCmdOptions().getCommandOptions().size() + 1];
		cmdComps[0] = new JIString(cmdA.getCommand());
		
		Collection<String> opts = cmdA.getCmdOptions().getCommandOptions();
		for ( int i = 0; i < opts.size(); ++i )
		{
			cmdComps[i + 1] = new JIString(((ArrayList<String>) opts).get(i));
		}
		return cmdComps;
	}*/
}
