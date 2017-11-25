package com.testanywhere.core.utilities.reflection.converters;

import com.testanywhere.core.utilities.reflection.converters.api.Converter;

import java.math.BigDecimal;

/**
 * BigDecimal passthrough
 * @see NumberConverter for more details
 */
public class BigDecimalConverter implements Converter<BigDecimal>
{
    public BigDecimalConverter()
    {
        super();
    }

    public BigDecimal convert(Object value) {
        return NumberConverter.convertToType(BigDecimal.class, value);
    }
}
