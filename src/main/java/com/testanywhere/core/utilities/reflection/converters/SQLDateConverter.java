package com.testanywhere.core.utilities.reflection.converters;

import com.testanywhere.core.utilities.reflection.converters.api.Converter;

import java.sql.Date;

/**
 * Passthrough to {@link DateConverter} for {@link Date}
 * @see DateConverter
 */
public class SQLDateConverter implements Converter<Date>
{
    public SQLDateConverter()
    {
        super();
    }

    public Date convert(Object value) {
        return DateConverter.convertToType(Date.class, value);
    }
}
