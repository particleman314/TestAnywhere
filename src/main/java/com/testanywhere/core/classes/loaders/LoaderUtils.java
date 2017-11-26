package com.testanywhere.core.classes.loaders;

import com.testanywhere.core.utilities.logging.LogConfiguration;
import com.testanywhere.core.utilities.logging.MultiOutputStream;
import org.apache.log4j.Logger;

import java.io.OutputStream;
import java.io.PrintStream;

public class LoaderUtils 
{
	public static Logger logger;

	static
	{
		LoaderUtils.logger = Logger.getLogger("LoaderUtils");
		LogConfiguration.configure();
	}

	public static void displayLoader(MultiOutputStream mos, final Integer numTabs, final ABCClassloader loader)
	{
		String loaderOutput = LoaderUtils.getLoaderAsString(numTabs, loader);

		if ( loaderOutput != null )
		{
			if (mos == null)
			{
				mos = new MultiOutputStream();
				mos.addStream(System.out);
			}

			for (OutputStream os : mos.getStreams())
			{
				final PrintStream ps = new PrintStream(os);
				ps.println(loaderOutput);
				ps.flush();
			}
		}
	}

	public static void displayLoader(OutputStream os, final Integer numTabs, final ABCClassloader loader)
	{
		String loaderOutput = LoaderUtils.getLoaderAsString(numTabs, loader);
		if ( loaderOutput != null )
		{
			if (os == null) os = System.out;
			final PrintStream ps = new PrintStream(os);
			ps.println(loaderOutput);
			ps.flush();
		}
	}
	
	public static void initializeLoaderSettings( final ABCClassloader genericLoader, final String packageID )
	{
		genericLoader.setLoaderName(genericLoader.getClass().getSimpleName());
		genericLoader.setLoaderType(genericLoader.getClass());
		genericLoader.setPackageID(packageID);
		genericLoader.setValid(true);
	}

	private static String getLoaderAsString( Integer numTabs, final ABCClassloader loader )
	{
		if ( loader == null ) return null;
		if ( numTabs == null || numTabs < 0 ) numTabs = 0;

		return loader.getPrettyPrint(numTabs);
	}
}
