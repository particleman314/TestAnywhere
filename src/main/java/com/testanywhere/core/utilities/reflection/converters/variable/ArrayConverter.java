package com.testanywhere.core.utilities.reflection.converters.variable;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

import com.testanywhere.core.utilities.logging.OutputDisplay;
import com.testanywhere.core.utilities.reflection.ConstructorUtils;
import com.testanywhere.core.utilities.reflection.ConversionUtils;
import com.testanywhere.core.utilities.reflection.converters.api.VariableConverter;

/**
 * Handles conversions to arrays from various types (including other arrays),
 * also handles the special case of a comma separated list of strings which
 * it will attempt to convert into an array of strings or whatever was requested
 * 
 */
public class ArrayConverter extends OutputDisplay implements VariableConverter
{
    public ArrayConverter()
    {
        super();
        this.initialize();
    }

    @Override
    public void buildObjectOutput( int numTabs ) {}

    /* (non-Javadoc)
     * @see com.nimsoft.reflection.converters.api.VariableConverter#canConvert(java.lang.Object, java.lang.Class)
     */
    @Override
    public boolean canConvert(Object value, Class<?> toType)
    {
        if ( ConstructorUtils.isClassArray(toType)) return true;
        return false;
    }

    /* (non-Javadoc)
     * @see com.nimsoft.reflection.converters.api.VariableConverter#convert(java.lang.Object, java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T convert(Object value, Class<T> toType)
    {
        T convert = null;
        Class<?> fromType = value.getClass();
        Object toConvert = value;

        Class<?> componentType = toType.getComponentType();
        if ( ConstructorUtils.isClassArray(fromType) )
        {
            // from array - to different type of array
            int length = Array.getLength(toConvert);
            convert = (T) Array.newInstance(componentType, length);
            for (int i = 0; i < length; i++)

            {
                Object object = Array.get(toConvert, i);
                object = getConversionUtils().convert(object, componentType); // convert each value
                Array.set(convert, i, object);
            }
        }
        else if ( ConstructorUtils.isClassCollection(fromType) )
        {
            // from collection
            Collection collection = (Collection) toConvert;
            int length = collection.size();
            convert = (T) Array.newInstance(componentType, length);
            int i = 0;
            for (Object object : collection)
            {
                object = this.getConversionUtils().convert(object, componentType); // convert each value
                Array.set(convert, i, object);
                i++;
            }
        }
        else if ( ConstructorUtils.isClassMap(fromType) )
        {
            // from map
            Map map = (Map) toConvert;
            int length = map.size();
            convert = (T) Array.newInstance(componentType, length);
            int i = 0;
            for (Object object : map.values()) {
                object = this.getConversionUtils().convert(object, componentType); // convert each value
                Array.set(convert, i, object);
                i++;
            }
        }
        else
        {
            // from scalar
            String valueString = toConvert.toString();
            if ("".equals(valueString))
            {
                // empty string becomes empty array
                convert = (T) Array.newInstance(componentType, 0);
            }
            else if (valueString.indexOf(',') > 0)
            {
                // support comma separated string to array
                String[] parts = valueString.split(",");
                convert = (T) Array.newInstance(componentType, parts.length);
                for (int i = 0; i < parts.length; i++)
                {
                    Object object = this.getConversionUtils().convert(parts[i].trim(), componentType); // convert each value
                    Array.set(convert, i, object);
                }
            }
            else
            {
                // just put it in the array
                convert = (T) Array.newInstance(componentType, 1);
                Object object = this.getConversionUtils().convert(toConvert, componentType); // convert each value
                Array.set(convert, 0, object);
            }
        }
        return convert;
    }

    @Override
    public boolean isNull() { return false; }

    private ConversionUtils getConversionUtils() {
        return ConversionUtils.getInstance();
    }

    private void initialize()
    {}
}
