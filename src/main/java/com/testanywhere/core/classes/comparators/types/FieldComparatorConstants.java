package com.testanywhere.core.classes.comparators.types;

import com.testanywhere.core.classes.comparators.ComparatorConstants;

public class FieldComparatorConstants extends ComparatorConstants
{
    // This ensures it is initialized when import by any class
    static
    {
        FieldComparatorConstants.getInstance();
    }

    private static boolean isInitialized = false;

    public static FieldComparatorConstants getInstance()
    {
        if ( !FieldComparatorConstants.isInitialized ) FieldComparatorConstants.isInitialized = true;
        return StringUtilsHolder.INSTANCE;
    }

    // Allow a means to reset parameters if they are allowed to change
    @Override
    public  void reset()
    {
        FieldComparatorConstants.initialize();
    }

    private static class StringUtilsHolder
    {
    	public static final FieldComparatorConstants INSTANCE = new FieldComparatorConstants();
    }

    private FieldComparatorConstants()
    {}
}
