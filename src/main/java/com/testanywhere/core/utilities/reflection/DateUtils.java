package com.testanywhere.core.utilities.reflection;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils
{
    public static String makeDateRFC2822(Date date, Locale l)
    {
        if ( l == null ) l = Locale.US;
        SimpleDateFormat df = new SimpleDateFormat("EEE', 'dd' 'MMM' 'yyyy' 'HH:mm:ss' 'Z", l);
        return df.format(date);
    }

    public static String makeDateRFC2822(Date date)
    {
        return DateUtils.makeDateRFC2822(date, null);
    }

    public static String makeDateISO8601(Date date)
    {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        String result = df.format(date);
        // convert YYYYMMDDTHH:mm:ss+HH00 into YYYYMMDDTHH:mm:ss+HH:00
        return result.substring(0, result.length() - 2) + ":" + result.substring(result.length() - 2);
    }

    private DateUtils() {}
}
