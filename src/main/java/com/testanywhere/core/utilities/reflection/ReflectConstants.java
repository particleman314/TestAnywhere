package com.testanywhere.core.utilities.reflection;

import com.testanywhere.core.utilities.class_support.functional_support.ConstantsInterface;

import java.lang.annotation.Annotation;

public class ReflectConstants implements ConstantsInterface
{
    public static final Class<?> NO_CLASS = null;
    public static final Object NO_OBJECT = null;
    public static final ClassFields<?> NO_FIELDS = null;
    public static final Annotation NO_ANNOTATION = null;

    // This ensures it is initialized when import by any class
    static
    {
        ReflectConstants.getInstance();
    }

    // This makes for a singleton object to keep the only copy
    private static boolean isInitialized = false;

    public static ReflectConstants getInstance()
    {
        if ( ! ReflectConstants.isInitialized ) {
            ReflectConstants.isInitialized = true;
            ReflectConstants.initialize();
        }
        return ReflectConstantsHolder.INSTANCE;
    }

    private static class ReflectConstantsHolder
    {
        public static final ReflectConstants INSTANCE = new ReflectConstants();
    }

    // Allow a means to reset parameters if they are allowed to change
    @Override
    public void reset() {
        ReflectConstants.initialize();
    }

    private static void initialize()
    {}

    private ReflectConstants()
    {}
}
