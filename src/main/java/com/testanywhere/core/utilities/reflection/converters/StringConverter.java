package com.testanywhere.core.utilities.reflection.converters;

import com.testanywhere.core.utilities.classes.NullObject;
import com.testanywhere.core.utilities.reflection.converters.api.Converter;

/**
 * Converter to handle anything to string (this handles it in the way you would think)
 */
public class StringConverter implements Converter<String>
{
    public StringConverter()
    {
        super();
    }

    public String convert(Object value)
    {
        if (value != null) return value.toString();
        return (new NullObject()).toString();
    }
}
