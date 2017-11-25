package com.testanywhere.core.utilites;

import com.testanywhere.core.utilities.class_support.functional_support.ConstantsInterface;

import java.math.BigInteger;

public class Constants implements ConstantsInterface
{
    public static final int MILLISECONDS_PER_SECOND = 1000;

    public static final int KILOBYTES  = 1024;
    public static final int MEGABYTES  = Constants.KILOBYTES * Constants.KILOBYTES;
    public static final int GIGABYTES  = Constants.KILOBYTES * Constants.MEGABYTES;
    public static final long TERABYTES = Constants.KILOBYTES * Constants.GIGABYTES;

    public static final String nullRep = "null";

    // This ensures it is initialized when import by any class
    static
    {
        Constants.getInstance();
    }

    // This makes for a singleton object to keep the only copy
    private static boolean isInitialized = false;

    public static Constants getInstance()
    {
        if ( ! Constants.isInitialized ) {
            Constants.isInitialized = true;
            Constants.initialize();
        }
        return ConstantsHolder.INSTANCE;
    }

    private static class ConstantsHolder
    {
        public static final Constants INSTANCE = new Constants();
    }

    // Allow a means to reset parameters if they are allowed to change
    @Override
    public void reset() {
        Constants.initialize();
    }

    private static void initialize()
    {}

    private Constants()
    {}
}