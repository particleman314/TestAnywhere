package com.testanywhere.core.utilities.reflection.converters;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Simple class to add in support for controlling the formats to
 * use when parsing the various dates
 * 
 */
public class BaseDateFormatHolder
{
    private DateFormat[] formats;
    private String[] patterns;

    public BaseDateFormatHolder()
    {
        super();
    }

    /**
     * @param patterns set the patterns to use when parsing strings to dates
     */
    public BaseDateFormatHolder(String[] patterns)
    {
        this.setPatterns(patterns);
    }

    /**
     * @param formats the formats to use when parsing strings into dates
     */
    public BaseDateFormatHolder(DateFormat[] formats) {
        this.setFormats(formats);
    }

    /**
     * @param formats sets the format objects to use for parsing
     */
    public void setFormats(DateFormat[] formats) {
        this.formats = formats;
    }
    /**
     * @param patterns set the patterns to use for parsing
     */
    public void setPatterns(String[] patterns)
    {
        this.patterns = patterns;
        this.getDateFormats();
    }

    /**
     * @return the array of date formats currently used by this converter
     */
    public DateFormat[] getDateFormats()
    {
        if (this.formats == null)
        {
            Collection<DateFormat> dateFormats = new ArrayList<DateFormat>();
            // add the standard short ones

            dateFormats.add( DateFormat.getDateInstance(DateFormat.SHORT) );
            dateFormats.add( DateFormat.getTimeInstance(DateFormat.SHORT) );
            dateFormats.add( DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT) );

            if ( this.patterns != null)
            {
                for (int i = 0; i < this.patterns.length; i++) dateFormats.add( new SimpleDateFormat(this.patterns[i]) );
            }
            this.formats = dateFormats.toArray(new DateFormat[dateFormats.size()]);
        }
        return this.formats;
    }
}
