package com.testanywhere.core.utilities.reflection.converters;

import com.testanywhere.core.utilities.reflection.converters.api.Converter;

/**
 * Byte passthrough
 * @see NumberConverter for more details
 */
public class ByteConverter implements Converter<Byte>
{
    public ByteConverter()
    {
        super();
    }

    public Byte convert(Object value) {
        return NumberConverter.convertToType(Byte.class, value);
    }

}
