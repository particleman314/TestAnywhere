package com.testanywhere.core.classes.managers;

import com.testanywhere.core.utilities.logging.LogConfiguration;
import com.testanywhere.core.classes.managers.ABCManager;
import com.testanywhere.core.classes.managers.ManagerConstants;

import org.apache.log4j.Logger;

import java.io.OutputStream;
import java.io.PrintStream;

public class ManagerUtils
{
	public static Logger logger;

	static
	{
		ManagerUtils.logger = Logger.getLogger("ManagerUtils");
		LogConfiguration.configure();
	}

	/*public static void displayManager(MultiOutputStream mos, Integer numTabs, ABCManager manager)
	{
		String managerOutput = ManagerUtils.getManagerAsString(numTabs, manager);

		if ( managerOutput != null )
		{
			if (mos == null)
			{
				mos = new MultiOutputStream();
				mos.addStream(System.out);
			}

			for (OutputStream os : mos.getStreams())
			{
				final PrintStream ps = new PrintStream(os);
				ps.println(managerOutput);
				ps.flush();
			}
		}
	}*/

	public static void displayManager(OutputStream os, final int numTabs, final ABCManager manager)
	{
		String managerOutput = ManagerUtils.getManagerAsString(numTabs, manager);

		if (managerOutput != null)
		{
			if (os == null) os = System.out;
			final PrintStream ps = new PrintStream(os);
			ps.println(manager.getPrettyPrint(numTabs));
			ps.flush();
		}
	}

	public static String getTypesRelativeToManager( final String managerPkgLevel, final int isParallel )
	{
		String modifiedLocation = managerPkgLevel;

		if ( isParallel == ManagerConstants.PARALLEL )
		{
			int startPt = managerPkgLevel.indexOf("manager");
			modifiedLocation = managerPkgLevel.substring(0,startPt);
		}
		return modifiedLocation + ".types";
	}

	private static String getManagerAsString( Integer numTabs, final ABCManager manager )
	{
		if ( manager == null ) return null;
		if ( numTabs == null || numTabs < 0 ) numTabs = 0;

		return manager.getPrettyPrint(numTabs);
	}
}