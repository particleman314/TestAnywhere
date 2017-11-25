package com.testanywhere.core.utilities.reflection.converters;

import com.testanywhere.core.utilities.reflection.converters.api.Converter;

/**
 * Double passthrough
 */
public class DoubleConverter implements Converter<Double>
{
    public DoubleConverter()
    {
        super();
    }

    public Double convert(Object value) {
        return NumberConverter.convertToType(Double.class, value);
    }

}
