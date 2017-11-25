package com.testanywhere.core.utilities.reflection.converters;

import com.testanywhere.core.utilities.reflection.converters.api.Converter;

import java.sql.Time;

/**
 * Passthrough to {@link DateConverter} for {@link Time}
 * @see DateConverter
 */
public class SQLTimeConverter implements Converter<Time>
{
    public SQLTimeConverter()
    {
        super();
    }

    public Time convert(Object value) {
        return DateConverter.convertToType(Time.class, value);
    }
}
