package com.testanywhere.core.classes.support.md5;

import com.testanywhere.core.utilities.class_support.functional_support.ConstantsInterface;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

public class MD5Constants implements ConstantsInterface
{
    protected static final Pattern MD5_PATTERN = Pattern.compile("^[A-Fa-f0-9]{0,32}$");
    protected static final String  ZERO        = StringUtils.repeat("0", 32);

    // This ensures it is initialized when import by any class
    static
    {
        MD5Constants.getInstance();
    }

    // This makes for a singleton object to keep the only copy
    private static boolean isInitialized = false;

    public static MD5Constants getInstance()
    {
        if ( ! MD5Constants.isInitialized ) {
            MD5Constants.isInitialized = true;
            MD5Constants.initialize();
        }
        return MD5ConstantsHolder.INSTANCE;
    }

    private static class MD5ConstantsHolder
    {
        public static final MD5Constants INSTANCE = new MD5Constants();
    }

    @Override
    public void reset()
    {
        MD5Constants.initialize();
    }

    private static void initialize()
    {}

    private MD5Constants()
    {}
}
