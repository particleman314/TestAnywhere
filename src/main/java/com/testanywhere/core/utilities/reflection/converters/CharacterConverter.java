package com.testanywhere.core.utilities.reflection.converters;

import com.testanywhere.core.utilities.reflection.converters.api.Converter;

/**
 * Converts objects to a character (will mostly truncate the string value of the object)
 */
public class CharacterConverter implements Converter<Character>
{
    public CharacterConverter()
    {
        super();
    }

    public Character convert(Object value)
    {
        Character c = null;
        if (value instanceof char[])
        {
            char[] ca = (char[]) value;
            if (ca.length > 0) c = ca[0];
        }
        else
        {
            String s = value.toString();
            if (s.length() > 0) c = s.charAt(0);
        }

        if (c == null) throw new UnsupportedOperationException("Character convert failure: cannot convert source ("+value+") and type ("+value.getClass()+") to a char");
        return c;
    }
}
