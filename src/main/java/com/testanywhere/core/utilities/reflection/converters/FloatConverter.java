package com.testanywhere.core.utilities.reflection.converters;

import com.testanywhere.core.utilities.reflection.converters.api.Converter;

/**
 * Float passthrough
 * @see NumberConverter for more details
 */
public class FloatConverter implements Converter<Float>
{
    public FloatConverter()
    {
        super();
    }

    public Float convert(Object value) {
        return NumberConverter.convertToType(Float.class, value);
    }
}
