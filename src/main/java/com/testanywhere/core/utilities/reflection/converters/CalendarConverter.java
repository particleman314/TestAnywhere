package com.testanywhere.core.utilities.reflection.converters;

import com.testanywhere.core.utilities.reflection.converters.api.Converter;

import java.text.DateFormat;
import java.util.Calendar;

/**
 * Passthrough to {@link DateConverter} for {@link Calendar}
 * @see DateConverter
 */
public class CalendarConverter extends BaseDateFormatHolder implements Converter<Calendar> {

    public CalendarConverter() {
        super();
    }

    public CalendarConverter(String[] patterns) {
        super(patterns);
    }

    public CalendarConverter(DateFormat[] formats) {
        super(formats);
    }

    public Calendar convert(Object value)
    {
        return DateConverter.convertToType(Calendar.class, value, this.getDateFormats());
    }
}
