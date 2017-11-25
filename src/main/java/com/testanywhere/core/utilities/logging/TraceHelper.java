package com.testanywhere.core.utilities.logging;

import java.lang.reflect.Method;

public class TraceHelper
{
    // save it static to have it available on every call
    private static Method m;

    static
    {
        try
        {
            m = Throwable.class.getDeclaredMethod("getStackTraceElement", int.class);
            m.setAccessible(true);
        }
        catch (Exception ignored)
        {}
    }

    public static String getMethodName()
    {
        return TraceHelper.getMethodName(1);
    }

    public static String getMethodName(final int depth)
    {
        try
        {
            StackTraceElement element = (StackTraceElement) m.invoke( new Throwable(), depth + 1 );
            return element.getMethodName();
        }
        catch (Exception e)
        {
             return null;
        }
    }
}
