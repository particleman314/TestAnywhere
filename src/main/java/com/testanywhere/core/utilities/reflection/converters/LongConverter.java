package com.testanywhere.core.utilities.reflection.converters;

import com.testanywhere.core.utilities.reflection.converters.api.Converter;

/**
 * Long passthrough
 * @see NumberConverter for more details
 */
public class LongConverter implements Converter<Long>
{
    public LongConverter()
    {
        super();
    }

    public Long convert(Object value) {
        return NumberConverter.convertToType(Long.class, value);
    }
}
