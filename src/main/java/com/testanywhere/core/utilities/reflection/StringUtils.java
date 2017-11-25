package com.testanywhere.core.utilities.reflection;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class StringUtils {

    public static String makeStringFromInputStream(InputStream stream)
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        StringBuilder sb = new StringBuilder();
        String line = null;

        try
        {
            while ((line = br.readLine()) != null)
            {
                sb.append(line);
                sb.append('\n');
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to get data from stream: " + e.getMessage(), e);
        }
        finally
        {
            try
            {
                br.close();
            }
            catch (IOException e)
            {}
        }
        return sb.toString();
    }

    public static InputStream makeInputStreamFromString(String string)
    {
        InputStream stream = null;
        if (string != null)
        {
            try
            {
                stream = new ByteArrayInputStream(string.getBytes("UTF-8"));
            }
            catch (UnsupportedEncodingException e)
            {}
        }
        return stream;
    }
}
