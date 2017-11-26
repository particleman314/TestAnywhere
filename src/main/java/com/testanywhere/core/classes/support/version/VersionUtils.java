package com.testanywhere.core.classes.support.version;

import com.testanywhere.core.utilities.logging.LogConfiguration;
import com.testanywhere.core.utilities.logging.MultiOutputStream;
import org.apache.log4j.Logger;

import java.io.OutputStream;
import java.io.PrintStream;

public class VersionUtils
{
	public static Logger logger;

	static
	{
		VersionUtils.logger = Logger.getLogger("VersionUtils");
		LogConfiguration.configure();
	}

	public static void displayVersion(MultiOutputStream mos, final int numTabs, final Version v )
	{
		if (mos == null)
		{
			mos = new MultiOutputStream();
			mos.addStream(System.out);
		}

		for (OutputStream os : mos.getStreams())
			VersionUtils.displayVersion(os, numTabs, v);
	}

	public static void displayVersion(OutputStream os, int numTabs, final Version v)
	{
		if ( os == null ) os = System.out;
		final PrintStream ps = new PrintStream(os);
		
		if ( numTabs < 0 ) numTabs = 0;
		ps.println(v.getPrettyPrint(numTabs));
		ps.flush();
	}

	private VersionUtils() {}
}