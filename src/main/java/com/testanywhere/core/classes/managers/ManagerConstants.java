package com.testanywhere.core.classes.managers;

import com.testanywhere.core.utilities.class_support.functional_support.ConstantsInterface;

public class ManagerConstants implements ConstantsInterface
{
    static
    {
        ManagerConstants.getInstance();
    }

    public static final int PARALLEL = 1;

    private static boolean isInitialized = false;

    public static ManagerConstants getInstance()
    {
        if ( ! ManagerConstants.isInitialized )
        {
            ManagerConstants.isInitialized = true;
            ManagerConstants.initialize();
        }
        return ManagerConstantsHolder.INSTANCE;
    }

    @Override
    public void reset()
    {
        ManagerConstants.initialize();
    }

    private static class ManagerConstantsHolder
    {
        public static final ManagerConstants INSTANCE = new ManagerConstants();
    }

    private static void initialize()
    {}
}
