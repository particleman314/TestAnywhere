package com.testanywhere.core.classes.utilities;

import com.testanywhere.core.utilities.logging.LogConfiguration;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OSUtils
{
    public static Logger logger;

    static
    {
        OSUtils.logger = Logger.getLogger("OSUtils");
        LogConfiguration.configure();
    }

    public static String convertPathToOS( final String input )
    {
        if ( input == null ) return null;

        String inputTrimmed = input.trim();
        if ( inputTrimmed.length() < 1 ) return input;

        String oppositeSeparator;
        if ( OSUtils.isWindows() ) oppositeSeparator = "/";
        else oppositeSeparator = "\\\\";

        // Check for windows style pathing...
        Pattern p = Pattern.compile(oppositeSeparator);
        Matcher m = p.matcher(inputTrimmed);

        if ( m.find() ) inputTrimmed = inputTrimmed.replaceAll(oppositeSeparator, File.separator);
        
        return inputTrimmed;
    }

    public static boolean isWindows()
    {
        String osType = System.getProperty("os.name");
        return osType.toLowerCase().contains("win");
    }
}
