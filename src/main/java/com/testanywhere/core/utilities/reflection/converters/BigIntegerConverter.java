package com.testanywhere.core.utilities.reflection.converters;

import com.testanywhere.core.utilities.reflection.converters.api.Converter;

import java.math.BigInteger;

/**
 * BigInteger passthrough
 */
public class BigIntegerConverter implements Converter<BigInteger>
{
    public BigIntegerConverter()
    {
        super();
    }

    public BigInteger convert(Object value) {
        return NumberConverter.convertToType(BigInteger.class, value);
    }
}
