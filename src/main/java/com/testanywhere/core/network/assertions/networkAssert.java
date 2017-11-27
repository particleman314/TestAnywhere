package com.nimsoft.assertions;

import com.testanywhere.core.utilities.logging.TextManager;
import com.testanywhere.core.network.IP;
import com.testanywhere.core.network.IPUtils;
import org.junit.Assert;

public class networkAssert extends Assert
{
    protected networkAssert() {}

    static public void assertValidIP( final String ip )
    {
        boolean result = ( ! TextManager.validString(ip) || ! IPUtils.validIPAddress(ip) );
        if ( result ) fail("Invalid IP detected");
    }

    static public void assertInvalidIP( final String ip )
    {
        boolean result = ( TextManager.validString(ip) && IPUtils.validIPAddress(ip) );
        if ( result ) fail("Valid IP detected");
    }

    static public void assertValidIP( final IP ip )
    {
        if ( ip == null ) fail("Invalid IP detected");
        assertValidIP(ip.getIP());
    }

    static public void assertValidPort( final int port )
    {
        boolean result = ( ! IPUtils.hasValidPort(port) );
        if ( result ) fail("Invalid network port detected");
    }

    static public void assertInvalidPort( final int port )
    {
        boolean result = ( IPUtils.hasValidPort(port) );
        if ( result ) fail("Valid network port detected");
    }

    static public void assertIPIsBound( final IP ip )
    {
        boolean result = ( ip == null || ! ip.isBound() );
        if ( result ) fail("IP is not bound as expected");
    }

    static public void assertIPIsNotBound( final IP ip )
    {
        boolean result = ( ip != null && ip.isBound() );
        if ( result ) fail("IP is bound but was not expected to be so");
    }

}
