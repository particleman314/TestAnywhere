package com.testanywhere.core.utilities.reflection.converters;

import com.testanywhere.core.utilities.reflection.converters.api.Converter;

import java.sql.Timestamp;

/**
 * Passthrough to {@link DateConverter} for {@link Timestamp}
 * @see DateConverter
 */
public class TimestampConverter implements Converter<Timestamp>
{
    public TimestampConverter()
    {
        super();
    }

    public Timestamp convert(Object value) {
        return DateConverter.convertToType(Timestamp.class, value);
    }
}
