package com.testanywhere.core.utilities.reflection.converters;

import java.net.MalformedURLException;
import java.net.URL;

import com.testanywhere.core.utilities.logging.TextManager;
import com.testanywhere.core.utilities.reflection.converters.api.Converter;

/**
 * Converts strings into URLs
 */
public class URLConverter implements Converter<URL>
{
    public URLConverter()
    {
        super();
    }

    public URL convert(Object value)
    {
        URL url = null;
        String s = value.toString();
        if (TextManager.validString(s))
        {
            try
            {
                url = new URL(s);
            }
            catch (MalformedURLException e)
            {
                throw new UnsupportedOperationException("URL convert failure: cannot convert source ("+value+") and type ("+value.getClass()+") to a URL: " + e.getMessage());
            }
        }

        if (url == null) throw new UnsupportedOperationException("URL convert failure: cannot convert source ("+value+") and type ("+value.getClass()+") to a URL");
        return url;
    }
}
