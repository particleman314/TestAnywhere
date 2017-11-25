package com.testanywhere.core.utilities.reflection.converters;

import com.testanywhere.core.utilities.reflection.converters.api.Converter;

/**
 * Short passthrough
 * @see NumberConverter for more details
 */
public class ShortConverter implements Converter<Short>
{
    public ShortConverter()
    {
        super();
    }

    public Short convert(Object value) {
        return NumberConverter.convertToType(Short.class, value);
    }
}
