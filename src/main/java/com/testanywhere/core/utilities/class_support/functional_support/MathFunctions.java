package com.testanywhere.core.utilities.class_support.functional_support;

import com.testanywhere.core.utilities.logging.LogConfiguration;
import org.apache.log4j.Logger;

public class MathFunctions
{
    public static Logger logger;

    static
    {
        MathFunctions.logger = Logger.getLogger("MathFunctions");
        LogConfiguration.configure();
    }

    public static boolean isOdd( final int value ) { return ((value % 2) != 0); }

    private MathFunctions() {}
}
