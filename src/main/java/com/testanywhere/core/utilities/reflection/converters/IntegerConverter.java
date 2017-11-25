package com.testanywhere.core.utilities.reflection.converters;

import com.testanywhere.core.utilities.reflection.converters.api.Converter;

/**
 * Integer passthrough
 * @see NumberConverter for more details
 */
public class IntegerConverter implements Converter<Integer>
{
    public IntegerConverter()
    {
        super();
    }

    public Integer convert(Object value) {
        return NumberConverter.convertToType(Integer.class, value);
    }
}
