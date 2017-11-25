package com.testanywhere.core.utilities.class_support.identification;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class IdGenerator
{
    private static long id = 0;
    private static Unsafe lowLevelAccess;

    static
    {
        IdGenerator.lowLevelAccess = IdGenerator.getUnsafe();
    }

    public static synchronized long generate() { return ++id; }

    public static synchronized long address( Object obj )
    {
        Object helperArray[] = new Object[1];
        helperArray[0]       = obj;
        long addressOfObject = 0L;

        if ( obj == null ) return addressOfObject;

        if ( IdGenerator.lowLevelAccess != null )
        {
            long baseOffset = lowLevelAccess.arrayBaseOffset(Object[].class);
            addressOfObject = lowLevelAccess.getLong(helperArray, baseOffset);
        }
        return addressOfObject;
    }

    private static Unsafe getUnsafe()
    {
        try
        {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            return (Unsafe)f.get(null);
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
