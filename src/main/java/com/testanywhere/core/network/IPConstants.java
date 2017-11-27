package com.testanywhere.core.network;

import com.testanywhere.core.utilities.class_support.functional_support.ConstantsInterface;
import com.testanywhere.core.classes.comparators.ComparatorConstants;

import java.util.regex.Pattern;

public class IPConstants extends ComparatorConstants implements ConstantsInterface
{
    static
    {
        IPConstants.initialize();
    }

    private static final int DEFAULT_MAX_PORT = 65535;

    public static final int NO_PORT = -1;
    public static final int MIN_PORT = 1;
    public static final int MAX_PORT = DEFAULT_MAX_PORT;

    public static final short HOSTID_UNKNOWN = -1;
    public static final short HOSTID_AS_IPV6 = 1;
    public static final short HOSTID_AS_IPV4 = 2;
    public static final short HOSTID_AS_FQDN = 3;
    public static final short HOSTID_AS_SHORTNAME = 4;
    
    public static final String NOADDRESS = "0.0.0.0";
    public static final Pattern IPv4Pattern = Pattern.compile("^.[0-9]{1,3}/..[0-9]{1,3}/..[0-9]{1,3}/..[0-9]{1,3}");

    public static int SOCKET_TIMEOUT = 5000;
    public static int SOCKET_DEFAULT_BIND_PORT = 8080;

    private static boolean isInitialized = false;

    @SuppressWarnings("SameReturnValue")
    public static IPConstants getInstance()
    {
        if ( !IPConstants.isInitialized ) 
        {
        	IPConstants.isInitialized = true;
            IPConstants.initialize();
        }
        return IPConstantsHolder.INSTANCE;
    }

    @SuppressWarnings("EmptyMethod")
    protected static void initialize()
    {}

    public void reset()
    {
        IPConstants.initialize();
    }

    private static class IPConstantsHolder
    {
    	public static final IPConstants INSTANCE = new IPConstants();
    }
}
