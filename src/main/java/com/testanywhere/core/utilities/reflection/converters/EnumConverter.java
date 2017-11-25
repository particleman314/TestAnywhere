package com.testanywhere.core.utilities.reflection.converters;

import com.testanywhere.core.utilities.reflection.ClassFields;
import com.testanywhere.core.utilities.reflection.FieldUtils;
import com.testanywhere.core.utilities.reflection.converters.api.InterfaceConverter;

import java.util.Collection;

/**
 * Handles conversions to enums (this is fairly limited to simply converting strings to enums)
 */
@SuppressWarnings("unchecked")
public class EnumConverter implements InterfaceConverter<Enum>
{
    public EnumConverter()
    {
        super();
    }

    public Enum convert(Object value)
    {
        // no default so we cannot handle this case
        throw new IllegalArgumentException("Cannot convert to an enum without having the enum type available");
    }

    public Enum convertInterface(Object value, Class<? extends Enum> implementationType)
    {
        Enum convert = null;
        String name = value.toString(); // can only deal with strings
        // now we try to find the enum field in this enum class
        ClassFields<Enum> cf = (ClassFields<Enum>) this.getFieldUtils().analyzeClass(implementationType);
        Collection<Enum> enumConstants = cf.getClassData().getEnumConstants();
        for (Enum e : enumConstants)
        {
            if (e.name().equals(name)) {
                // found the matching enum
                convert = e;
                break;
            }
        }
        if (convert == null) throw new UnsupportedOperationException("Failure attempting to create enum for name ("+name+") in ("+implementationType+")");
        return convert;
    }

    private FieldUtils getFieldUtils() {
        return FieldUtils.getInstance();
    }
}
