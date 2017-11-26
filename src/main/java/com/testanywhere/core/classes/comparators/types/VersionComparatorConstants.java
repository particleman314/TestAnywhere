package com.testanywhere.core.classes.comparators.types;

import com.testanywhere.core.classes.comparators.ComparatorConstants;

public class VersionComparatorConstants extends ComparatorConstants
{
	private static boolean isInitialized = false;

	// This ensures it is initialized when import by any class
	static
	{
		VersionComparatorConstants.getInstance();
	}

    public static VersionComparatorConstants getInstance()
    {
    	if ( ! VersionComparatorConstants.isInitialized ) {
    		VersionComparatorConstants.isInitialized = true;
    		VersionComparatorConstants.initialize();
    	}
    	return VersionConstantsHolder.INSTANCE;
    }

	private static class VersionConstantsHolder
	{
    	public static final VersionComparatorConstants INSTANCE = new VersionComparatorConstants();
    }

	protected static void initialize()
	{}

	@Override
	public void reset()
	{
		VersionComparatorConstants.initialize();
	}

	private VersionComparatorConstants() {}
}
