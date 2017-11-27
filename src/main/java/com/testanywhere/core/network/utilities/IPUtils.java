package com.testanywhere.core.network.utilities;

import com.testanywhere.core.network.IPConstants;
import com.testanywhere.core.network.classes.IP;
import com.testanywhere.core.utilities.logging.LogConfiguration;
import com.testanywhere.core.utilities.logging.TextManager;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.*;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.Enumeration;

public class IPUtils 
{
	public static Logger logger;
	public static InetAddress localHost = null;

	static
	{
		IPUtils.logger = Logger.getLogger("IPUtils");
		LogConfiguration.configure();
	}

	public static void resetLocalHost()
	{
		IPUtils.localHost = null;
		IPUtils.searchLocalHostIP();
	}
	
	public static boolean validIPAddress( final String ipAddr )
	{
		return IPUtils.validIPV4Address(ipAddr) || IPUtils.validIPV6Address(ipAddr);
	}
	
	private static boolean validIPV4Address(final String ipAddr)
	{
		if ( ! TextManager.validString(ipAddr) || ipAddr.equals(IPConstants.NOADDRESS)) return false;
		
		InetAddressValidator checker = InetAddressValidator.getInstance();
		
		return checker.isValidInet4Address(ipAddr);
	}
	
	private static boolean validIPV6Address(final String ipAddr)
	{
		if ( ! TextManager.validString(ipAddr) || ipAddr.equals("::")) return false;
		
		InetAddressValidator checker = InetAddressValidator.getInstance();
		
		return checker.isValidInet6Address(ipAddr);
	}
	
	public static boolean hasValidPort( final int port )
	{
		return ( port >= IPConstants.MIN_PORT && port <= IPConstants.MAX_PORT );
	}
	
	public static String reverseLookup( final String IPorHost ) throws UnknownHostException
	{
		short status = IPConstants.HOSTID_UNKNOWN;
		
		if ( IPorHost.contains(":") && ! IPorHost.contains(".") ) status = IPConstants.HOSTID_AS_IPV6;
		else if ( IPorHost.matches(IPConstants.IPv4Pattern.pattern()) ) status = IPConstants.HOSTID_AS_IPV4;
		else if ( IPorHost.contains(".") ) status = IPConstants.HOSTID_AS_FQDN;
		else if ( TextManager.validString( IPorHost ) ) status = IPConstants.HOSTID_AS_SHORTNAME;
		
		if ( status != IPConstants.HOSTID_UNKNOWN )
		{
			switch ( status )
			{
				case IPConstants.HOSTID_AS_IPV6 : case IPConstants.HOSTID_AS_IPV4 :
				{
					return new IP(IPorHost).getInetAddress().getCanonicalHostName();
				}
				case IPConstants.HOSTID_AS_FQDN : case IPConstants.HOSTID_AS_SHORTNAME :
				{
					return InetAddress.getByName(IPorHost).getHostAddress();
				}
			}
		}
		return IPorHost;
	}
	
	public static String hostAsIP( final String host ) throws UnknownHostException
	{
		if ( host.contains(":") && ! host.contains(".") ) return host;
		else if ( host.matches(IPConstants.IPv4Pattern.pattern()))  return host;
		return InetAddress.getByName(host).getHostAddress();
	}
	
	public static InetAddress searchLocalHostIP()
	{
		// iterate over the network interfaces known to java
		
		if ( IPUtils.localHost != null ) return IPUtils.localHost;
		
		try 
		{
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			IPUtils.logger.debug("Number of interfaces = " + Collections.list(interfaces).size());
			
			for ( NetworkInterface interface_ : Collections.list(interfaces) ) 
			{
				IPUtils.logger.info("Testing network interface --> " + interface_.getDisplayName());
				// we shouldn't care about loopback addresses
				if ( interface_.isLoopback() )
					continue;

				// if you don't expect the interface to be up you can skip this
				// though it would question the usability of the rest of the code
				if ( ! interface_.isUp() )
					continue;

				// iterate over the addresses associated with the interface
				Enumeration<InetAddress> addresses = interface_.getInetAddresses();
				for ( InetAddress address : Collections.list(addresses) ) 
				{
					IPUtils.logger.debug("Testing InetAddress --> " + address.getHostAddress());
					// look only for ipv4 addresses
					if ( address instanceof Inet6Address )
						continue;

					try 
					{
						// use a timeout big enough for your needs
						if ( !address.isReachable(IPConstants.SOCKET_TIMEOUT) ) continue;
					} 
					catch ( IOException ex ) 
					{
						IPUtils.logger.debug("Unable to connect to " + address);
						continue;
					}
					
					// java 7's try-with-resources statement, so that
					// we close the socket immediately after use
					try ( SocketChannel socket = SocketChannel.open() ) 
					{
						// again, use a big enough timeout
						socket.socket().setSoTimeout(IPConstants.SOCKET_TIMEOUT);

						// bind the socket to your local interface
						socket.bind(new InetSocketAddress(address, IPConstants.SOCKET_DEFAULT_BIND_PORT));

						// try to connect to *somewhere*
						//socket.connect(new InetSocketAddress("www.google.com", 80));
						//socket.close();
					}
					catch ( IOException ex )
					{
						continue;
					}

					IPUtils.localHost = address;
					return IPUtils.localHost;
				}
			}
		} 
		catch ( SocketException se ) 
		{
			IPUtils.logger.debug("Socket exception encountered : " + se.getLocalizedMessage());
		}
		try 
		{
			IPUtils.localHost = InetAddress.getLocalHost();
			return IPUtils.localHost;
		} 
		catch (UnknownHostException e)
		{
			return null;
		}
	}
}