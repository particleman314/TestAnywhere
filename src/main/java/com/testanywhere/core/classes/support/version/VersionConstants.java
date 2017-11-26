package com.testanywhere.core.classes.support.version;

import com.testanywhere.core.classes.comparators.ComparatorConstants;

import java.util.TreeMap;

public class VersionConstants extends ComparatorConstants
{
	public static final int NOT_FOUND   = -1;
	public static final int ILLEGAL     = -2;
	
	public static final int DEFAULT_NUMERICAL_SIZE   = 2;
	public static String DEFAULT_VERSION_DELIMITER = ".";
	
	public static final int MAJOR = 0;
	public static final int MINOR = 1;
	public static final int REVISION = 2;
	public static final int BUILD = 3;
	
	public static final TreeMap<String, Integer> NUMERICAL_SIZES         = new TreeMap<>();
	public static final TreeMap<VERSION_COMPONENTS, String> DESIGNATIONS = new TreeMap<>();

	public enum VERSION_COMPONENTS { 
		MAJOR(),
		MINOR(),
		REVISION(),
		SUBREVISION()
	}

	private static boolean isInitialized = false;
	
    public static VersionConstants getInstance() 
    {
    	if ( ! VersionConstants.isInitialized ) {
    		VersionConstants.isInitialized = true;
    		VersionConstants.initialize();
    	}
    	return VersionConstantsHolder.INSTANCE;
    }

	private static class VersionConstantsHolder 
	{ 
    	public static final VersionConstants INSTANCE = new VersionConstants();
    }

	protected static void initialize()
	{
		for (VERSION_COMPONENTS VC : VERSION_COMPONENTS.values())
		{
			VersionConstants.DESIGNATIONS.put(VC, VC.name());
		}

		for ( String s : VersionConstants.DESIGNATIONS.values() ) {
			VersionConstants.NUMERICAL_SIZES.put(s, VersionConstants.DEFAULT_NUMERICAL_SIZE);
		}
	}

	@Override
	public void reset()
	{
		VersionConstants.initialize();
	}

	private VersionConstants() {}
}
