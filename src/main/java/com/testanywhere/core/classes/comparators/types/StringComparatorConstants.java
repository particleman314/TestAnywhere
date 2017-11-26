package com.testanywhere.core.classes.comparators.types;

import com.testanywhere.core.classes.comparators.ComparatorConstants;

public class StringComparatorConstants extends ComparatorConstants
{
    // This ensures it is initialized when import by any class
    static
    {
        StringComparatorConstants.getInstance();
    }

    private static boolean isInitialized = false;

    public static StringComparatorConstants getInstance()
    {
        if ( !StringComparatorConstants.isInitialized )
            StringComparatorConstants.isInitialized = true;
        return StringUtilsComparatorHolder.INSTANCE;
    }

    // Allow a means to reset parameters if they are allowed to change
    @Override
    public void reset()
    {
        StringComparatorConstants.initialize();
    }

    private static class StringUtilsComparatorHolder
    {
    	public static final StringComparatorConstants INSTANCE = new StringComparatorConstants();
    }

    private StringComparatorConstants()
    {}
}
