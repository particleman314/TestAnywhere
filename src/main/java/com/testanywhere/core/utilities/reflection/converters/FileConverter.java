package com.testanywhere.core.utilities.reflection.converters;

import java.io.File;

import com.testanywhere.core.utilities.logging.TextManager;
import com.testanywhere.core.utilities.reflection.converters.api.Converter;

/**
 * Simple converter to make {@link File} from strings
 * 
 */
public class FileConverter implements Converter<File>
{
    public FileConverter()
    {
        super();
    }

    public File convert(Object value)
    {
        File f = null;
        String fileName = value.toString();
        if ( TextManager.validString(fileName) ) f = new File(fileName);
        if (f == null) throw new UnsupportedOperationException("File convert failure: cannot convert source ("+value+") and type ("+value.getClass()+") to a File");
        return f;
    }
}
