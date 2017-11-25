package com.testanywhere.core.utilities.reflection.converters.variable;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

import com.testanywhere.core.utilities.logging.OutputDisplay;
import com.testanywhere.core.utilities.reflection.ArrayUtils;
import com.testanywhere.core.utilities.reflection.ConstructorUtils;
import com.testanywhere.core.utilities.reflection.ConversionUtils;
import com.testanywhere.core.utilities.reflection.DeepUtils;
import com.testanywhere.core.utilities.reflection.converters.api.VariableConverter;

/**
 * This is a special variable converter designed to handle the special case of converting
 * from a non-scalar (collection, array, list, etc.) to a scalar object
 */
public class ScalarConverter extends OutputDisplay implements VariableConverter
{
    public ScalarConverter()
    {
        super();
        this.initialize();
    }

    @Override
    public void buildObjectOutput( int numTabs )
    {}

    /* (non-Javadoc)
     * @see com.nimsoft.reflection.converters.api.VariableConverter#canConvert(java.lang.Object, java.lang.Class)
     */
    @Override
    public boolean canConvert(Object value, Class<?> toType)
    {
        boolean convertible = false;
        if (value != null)
        {
            Class<?> fromType = value.getClass();
            if ( ConstructorUtils.isClassObjectHolder(fromType) )
                if (! ConstructorUtils.isClassObjectHolder(toType) ) convertible = true;
        }
        return convertible;
    }

    /* (non-Javadoc)
     * @see com.nimsoft.reflection.converters.api.VariableConverter#convert(java.lang.Object, java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T convert(Object value, Class<T> toType)
    {
        // we know that fromType is a holder and toType is a scalar
        T convert = null;
        Class<?> fromType = value.getClass();
        Object toConvert = value;

        if ( ConstructorUtils.isClassArray(fromType) )
        {
            // from array
            int length = Array.getLength(toConvert);
            if (length > 0)
            {
                Class<?> componentType = fromType.getComponentType();
                if ( String.class.equals(toType) 
                        && ConstructorUtils.isClassSimple(componentType))
                    return (T) ArrayUtils.arrayToString((Object[]) value);
                else
                    toConvert = Array.get(toConvert, 0);
            }
            else
                toConvert = "";
        }
        else if ( ConstructorUtils.isClassCollection(fromType) )
        {
            // from collection
            Collection<?> collection = (Collection) toConvert;
            int length = collection.size();
            // to scalar
            if (length > 0) toConvert = collection.iterator().next();
            else toConvert = "";
        }
        else if ( ConstructorUtils.isClassMap(fromType) )
        {
            // from map
            Map map = (Map) toConvert;
            int length = map.size();
            // to scalar
            if (length > 0)
            {
                // check if the keys are strings and the toType is non-simple
                boolean stringKeys = false;
                for (Object key : map.keySet())
                {
                   if (String.class.equals(key.getClass())) stringKeys = true;
                   else
                   {
                       stringKeys = false;
                       break;
                   }
                }
                if (stringKeys 
                        && ! ConstructorUtils.isClassSimple(toType) )
                {
                    // this is a bean so populate it with the map data
                    convert = getConstructorUtils().constructClass(toType);
                    this.getDeepUtils().populate(convert, map); // put the values from the map into the object
                    return convert; // EXIT
                }
                else
                    toConvert = map.values().iterator().next();
            }
            else
                toConvert = "";
        }
        else
            throw new IllegalArgumentException("Failure converting to scalar value, the given input does not seem to be an object holder ("+fromType+"): " + value);

        // now convert the object from the holder
        convert = this.getConversionUtils().convert(toConvert, toType);
        return convert;
    }

    @Override
    public boolean isNull() { return false; }

    private ConversionUtils getConversionUtils() {
        return ConversionUtils.getInstance();
    }

    private ConstructorUtils getConstructorUtils() {
        return ConstructorUtils.getInstance();
    }

    private DeepUtils getDeepUtils() {
        return DeepUtils.getInstance();
    }

    private void initialize()
    {}
}
