package com.testanywhere.core.utilities.reflection.converters;

import com.testanywhere.core.utilities.reflection.ConversionUtils;
import com.testanywhere.core.utilities.reflection.converters.api.Converter;

/**
 * Converts objects into booleans (by default this will return Boolean.FALSE)<br/>
 * Special handling:<br/>
 * Numerics greater than 0 are true, less than or equal are false <br/>
 * 
 * <p>Case is ignored when converting values to true or false.</p>
 * <br/>
 * Based on code from commons bean utils but updated for generics and removed complexity and (mis)usage of Throwable<br/>
 */
public class BooleanConverter implements Converter<Boolean>
{
    private String[] trueStrings = {"true", "yes", "y", "on"};
    private String[] falseStrings = {"false", "no", "n", "off"};

    public BooleanConverter()
    {
        super();
    }

    // code derived form the commons beanutils converters

    /**
     * Allows the false strings and true strings to be controlled<br/>
     * By default any object whose string representation is one of the values
     * {"yes", "y", "true", "on", "1"} is converted to Boolean.TRUE, and
     * string representations {"no", "n", "false", "off", "0"} are converted
     * to Boolean.FALSE. The recognised true/false strings can be changed using the constructor
     * in this class and registering it with the {@link ConversionUtils}:
     * <pre>
     *  String[] trueStrings = {"oui", "o"};
     *  String[] falseStrings = {"non", "n"};
     *  Converter bc = new BooleanConverter(trueStrings, falseStrings);
     * </pre>
     * @param trueStrings these strings map to true
     * @param falseStrings these strings map to false
     */
    public BooleanConverter(String[] trueStrings, String[] falseStrings)
    {
        this.falseStrings = BooleanConverter.copyStrings(falseStrings);
        this.trueStrings = BooleanConverter.copyStrings(trueStrings);
    }

    public Boolean convert(Object value)
    {
        Boolean converted = null;
        if (value instanceof Number)
        {
            // numbers
            if (((Number)value).intValue() > 0) converted = Boolean.TRUE;
            else converted = Boolean.FALSE;
        }
        else
        {
            // strings and all others
            String stringValue = value.toString();
            if (stringValue != null)
            {
                stringValue = stringValue.toLowerCase();
                if ("0".equals(stringValue) || "".equals(stringValue)) converted = Boolean.FALSE;
                else if ("1".equals(stringValue)) converted = Boolean.TRUE;
                else
                {
                    for (int i=0; i<trueStrings.length; ++i)
                    {
                        if (trueStrings[i].equals(stringValue)) converted = Boolean.TRUE;
                    }
                    for (int i=0; i<falseStrings.length; ++i)
                    {
                        if (falseStrings[i].equals(stringValue)) converted = Boolean.FALSE;
                    }
                }
            }
        }

        if (converted == null) throw new UnsupportedOperationException("Boolean conversion exception: Cannot convert ("+value+") of type ("+value.getClass()+") into boolean");
        return converted;
    }

    /**
     * This method creates a copy of the provided array, and ensures that
     * all the strings in the newly created array contain only lower-case
     * letters.
     * <p>
     * Using this method to copy string arrays means that changes to the
     * src array do not modify the dst array.
     */
    private static String[] copyStrings(String[] src)
    {
        String[] dst = new String[src.length];
        for(int i=0; i<src.length; ++i) dst[i] = src[i].toLowerCase();
        return dst;
    }
}
