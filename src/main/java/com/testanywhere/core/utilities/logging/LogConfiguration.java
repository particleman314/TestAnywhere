package com.testanywhere.core.utilities.logging;

import org.apache.log4j.BasicConfigurator;

public class LogConfiguration
{
    private static boolean hasConfiguredLogging = false;

    public static void configure()
    {
        if ( ! LogConfiguration.hasConfiguredLogging ) {
            BasicConfigurator.configure();
            LogConfiguration.hasConfiguredLogging = true;
        }
    }

    public static boolean isConfigured()
    {
        return LogConfiguration.hasConfiguredLogging;
    }
}
