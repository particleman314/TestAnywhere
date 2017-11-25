package com.testanywhere.core.utilities.reflection.converters;

import com.testanywhere.core.utilities.reflection.ClassLoaderUtils;
import com.testanywhere.core.utilities.reflection.converters.api.Converter;

/**
 * Converts a string to a class (this is pretty much the only conversion supported)
 */

public class ClassConverter implements Converter<Class<?>>
{
    public ClassConverter()
    {
        super();
    }

    public Class<?> convert(Object value)
    {
        String className = value.toString();
        Class<?> c = ClassLoaderUtils.getClassFromString(className);
        if (c == null) throw new UnsupportedOperationException("Class convert failure: cannot convert source ("+value+") and type ("+value.getClass()+") to a Class");
        return c;
    }
}
