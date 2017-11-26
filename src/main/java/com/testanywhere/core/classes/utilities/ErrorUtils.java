package com.testanywhere.core.classes.utilities;

import com.testanywhere.core.utilities.logging.LogConfiguration;
import org.apache.log4j.Logger;

public class ErrorUtils
{
    public static int SUCCESS_ID = 0;
    public static int GENERIC_FAIL_ID = 254;

    public static Logger logger;

    static
    {
        ErrorUtils.logger = Logger.getLogger("ErrorUtils");
        LogConfiguration.configure();
        LoadErrors.installErrors((Class<?>) null);
    }
}
