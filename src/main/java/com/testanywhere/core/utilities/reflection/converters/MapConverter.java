package com.testanywhere.core.utilities.reflection.converters;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

import com.testanywhere.core.utilities.reflection.ConstructorUtils;
import com.testanywhere.core.utilities.reflection.FieldUtils;
import com.testanywhere.core.utilities.reflection.ClassFields.FieldsFilter;
import com.testanywhere.core.utilities.reflection.converters.api.InterfaceConverter;
import com.testanywhere.core.utilities.reflection.map.ArrayOrderedMap;

/**
 * Map converter to handle converting things into maps,
 * Can handle simple cases by stuffing them into the map with the key "data"
 */
@SuppressWarnings("unchecked")
public class MapConverter implements InterfaceConverter<Map>
{
    public MapConverter()
    {
        super();
    }

    public Map convert(Object value) {
        return this.convertInterface(value, ArrayOrderedMap.class);
    }

    public Map convertInterface(Object value, Class<? extends Map> implementationType)
    {
        Map convert = null;
        Class<?> fromType = value.getClass();
        Object toConvert = value;
        if (implementationType == null || implementationType.isInterface()) implementationType = ArrayOrderedMap.class;

        convert = (Map<String, Object>) this.getConstructorUtils().constructClass(implementationType);
        if ( ConstructorUtils.isClassArray(fromType) )
        {
            // from array
            int length = Array.getLength(toConvert);
            for (int i = 0; i < length; i++) {
                Object aVal = Array.get(toConvert, i);
                convert.put(i+"", aVal);
            }
        }
        else if ( ConstructorUtils.isClassCollection(fromType) )
        {
            // from collection
            int i = 0;
            for (Object object : (Collection) value) {
                convert.put(i+"", object);
                i++;
            }
        }
        else if ( ConstructorUtils.isClassMap(fromType) )
        {
            // from map - this is a case where we are going from one map type to another
            convert.putAll((Map)value);
        }
        else
        {
            // from scalar
            if (ConstructorUtils.isClassSimple(fromType))
                convert.put("data", toConvert);
            else
                convert = this.getFieldUtils().getFieldValues(value, FieldsFilter.COMPLETE, false);
        }
        return convert;
    }

    private ConstructorUtils getConstructorUtils() {
        return ConstructorUtils.getInstance();
    }

    private FieldUtils getFieldUtils() {
        return FieldUtils.getInstance();
    }
}
