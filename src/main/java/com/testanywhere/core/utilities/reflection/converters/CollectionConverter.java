package com.testanywhere.core.utilities.reflection.converters;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Vector;

import com.testanywhere.core.utilities.reflection.ConstructorUtils;
import com.testanywhere.core.utilities.reflection.converters.api.InterfaceConverter;

/**
 * Converter for collections (primarily for converting to other types of collections),
 * can also from a scalar to a collection by placing the scalar value into the collection
 */
@SuppressWarnings("unchecked")
public class CollectionConverter implements InterfaceConverter<Collection>
{
    public CollectionConverter()
    {
        super();
    }

    public Collection convert(Object value) {
        return this.convertInterface(value, Vector.class);
    }

    public Collection convertInterface(Object value, Class<? extends Collection> implementationType)
    {
        Collection convert = null;
        Class<?> fromType = value.getClass();
        Object toConvert = value;
        if (implementationType == null) implementationType = Vector.class;
        else if (implementationType.isInterface()) implementationType = ConstructorUtils.getClassFromInterface(implementationType);
        convert = this.getConstructorUtils().constructClass(implementationType);

        if ( ConstructorUtils.isClassArray(fromType) )
        {
            // from array
            int length = Array.getLength(toConvert);
            for (int i = 0; i < length; i++)
            {
                Object aVal = Array.get(toConvert, i);
                convert.add( aVal );
            }
        }
        else if ( ConstructorUtils.isClassCollection(fromType) )
        {
            // from collection - to other type of collection
            Collection<?> collection = (Collection) toConvert;
            convert.addAll(collection);
        }
        else if ( ConstructorUtils.isClassMap(fromType) )
        {
            // from map
            Map map = (Map) toConvert;
            convert.addAll(map.values());
        }
        else
        {
            // from scalar
            convert.add(toConvert);
        }
        return convert;
    }

    private ConstructorUtils getConstructorUtils() {
        return ConstructorUtils.getInstance();
    }
}
