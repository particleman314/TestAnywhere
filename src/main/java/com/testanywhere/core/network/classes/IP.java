package com.testanywhere.core.network.classes;

import com.testanywhere.core.network.IPConstants;
import com.testanywhere.core.network.utilities.IPUtils;
import com.testanywhere.core.utilities.class_support.BaseClass;
import com.testanywhere.core.utilities.class_support.Cast;
import com.testanywhere.core.utilities.classes.Pair;
import com.testanywhere.core.utilities.exceptions.ObjectCreationException;
import com.testanywhere.core.utilities.logging.*;

import java.io.Externalizable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class IP extends OutputDisplay implements Comparable<IP>, Externalizable
{
	public static final InetAddress LocalHostIP = IPUtils.searchLocalHostIP();

	private String                 requestedIP;
	private InetAddress            ipObj;
	private int                    port;
	private Pair<Boolean, Boolean> valid;         // This is set if the IP/port requested is valid to allow for binding
	private Socket                 open_socket;

	private static final IPComparator sortingComparator = new IPComparator(IPConstants.COMPARISON_FUNCTION.EQUALS);

	// Constructors
	public IP() 
	{
		super();
		this.initialize();
	}
	
	public IP( final int port )
	{
		this(); // Default to the local host IP (if detected)
		this.setPort(port);
	}
	
	public IP( final String ip )
	{
		this(); // Default to the local host IP (if detected)
		this.setIP(ip);
	}

	public IP( final String ip, final int port )
	{
		this(ip);
		this.setPort(port);
	}

	public IP( final IP ip ) throws ObjectCreationException
	{
		this();
		if ( ip == null ) throw new ObjectCreationException(IP.class);

		this.requestedIP = ip.requestedIP;
		this.ipObj = copy(ip.ipObj);
		this.port = ip.port;
		this.open_socket = copy(ip.open_socket);
		try {
			this.valid = ip.valid.copy();
		} catch (CloneNotSupportedException e) {
			throw new ObjectCreationException(IP.class);
		}
	}

	@Override
	public int compareTo(final IP otherIP)
	{
		return IP.sortingComparator.compare(this, otherIP);
	}

	@Override
	public String toString() 
	{
		if ( this.getInetAddress() != null )
			return this.getIP() + ":" + this.getPort();
		return null;
	}
	
	// Equality operator
	@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
	public boolean equals(final Object other )
	{
		if (other == null) return false;

		IP otherIP = Cast.cast(other);
		boolean IPMatch = (otherIP != null) && (this.getInetAddress().equals(otherIP.getInetAddress())) && this.getPort() == otherIP.getPort();
		return ( IPMatch && otherIP.isValid() == this.isValid()
				         && otherIP.isBound() == this.isBound() );
	}
	
	// Display to console operator
	public String getPrettyPrint( final int numTabs )
	{
		this.buildObjectOutput(numTabs);
		return this.dm.forFormattedDisplay();
	}

	@Override
	public boolean isNull() { return false;}

	@Override
	public void buildObjectOutput( int numTabs )
	{
		if ( numTabs < 0 ) numTabs = 0;
		Tabbing tabEnvironment = new Tabbing(numTabs);
		DisplayManager dm = this.getDM();

		String outerSpacer = tabEnvironment.getSpacer();
		dm.append(outerSpacer + "IP Object:", DisplayType.TEXTTYPES.LABEL);

		tabEnvironment.increment();
		String innerSpacer = tabEnvironment.getSpacer();

		if ( this.getInetAddress() != null )
		{
			dm.append(innerSpacer + "IP : " + this.getIP());
			dm.append(innerSpacer + "Represented Host : " + this.getHostname());

			if ( this.getPort() > -1 )
			{
				dm.append(innerSpacer + "Port ID : " + this.getPort());
				dm.append(innerSpacer + "Is Valid : " + TextManager.StringRepOfBool(this.isValid(), "yn"));
				dm.append(innerSpacer + "Is Bound : " + TextManager.StringRepOfBool(this.isBound(), "yn"));
				if ( this.isBound() )
					dm.append(innerSpacer + "Socket : " + BaseClass.checkIsNull(this.open_socket.toString()));
			}
		}
	}

	// Get/Set methods
	public InetAddress getInetAddress()
	{
		return this.ipObj;
	}
	
	public String getIP() 
	{
		if ( this.getInetAddress() != null )
			return this.getInetAddress().getHostAddress();

		return this.requestedIP;
	}
	
	public int getPort() 
	{
		return this.port;
	}
	
	public String getHostname() 
	{
		if ( this.getInetAddress() != null )
			return this.getInetAddress().getHostName();

		try
		{
			return InetAddress.getByName(this.getIP()).getHostName();
		}
		catch ( UnknownHostException e )
		{
			return null;
		}
	}

	public Socket bind()
	{
		if ( this.isBound() ) return this.open_socket;

		try
		{
			if ( this.isValid() )
			{
				Socket so = new Socket(this.getHostname(), this.getPort());
				this.open_socket = so;
				return so;
			}
		}
		catch (IOException ignored)
		{}
		return null;
	}

	public void close()
	{
		if ( this.open_socket == null ) return;
		try
		{
			this.open_socket.close();
			this.open_socket = null;
		}
		catch ( IOException ignored)
		{}
	}

	public boolean isBound()
	{
		return this.open_socket != null;
	}

	public int getLocalBoundPort()
	{
		if ( this.isBound() )
			return this.open_socket.getLocalPort();

		return IPConstants.NO_PORT;
	}

	public boolean isValid() 
	{
		return ( this.valid.getL() && this.valid.getR() );
	}

	public void reset() 
	{
		this.initialize();
		this.searchLocalHostIP();
		this.setPort(IPConstants.NO_PORT);
	}

	public void setPort( final int portID )
	{
		if ( this.getPort() == portID ) return;
		boolean result = IPUtils.hasValidPort(portID);

		if ( ! result )
			this.port = IPConstants.NO_PORT;
		else
			this.port = portID;

		this.valid.setR(result);
		this.close();
	}

	public void setIP( final String ip )
	{
		if ( this.checkIP(ip) )
		{
			if ( this.getIP().equals(ip) ) return;
			this.requestedIP = ip;
			try
			{
				this.ipObj = InetAddress.getByName(ip);
				this.valid.setL(true);
				this.close();
			}
			catch ( UnknownHostException e )
			{
				this.warn(e.getClass().getName() + " was caught attempting to get " + TextManager.specializeName(ip) + " by name via InetAddress.");
				this.warn(e.getLocalizedMessage());
				this.ipObj = IP.LocalHostIP; // Default to the local host IP (if detected)
			}
		}
	}

	private boolean checkIP(String ip)
	{
		boolean ipv4_or_ipv6 = IPUtils.validIPAddress(ip);
		try
		{
			if (!ipv4_or_ipv6)
			{
				if (!TextManager.validString(ip)) return false;

				ip = InetAddress.getByName(ip).getHostAddress();
			}
		}
		catch (UnknownHostException ignored)
		{}

		return TextManager.validString(ip);
	}

	private void setInetAddress( final InetAddress addr )
	{
		this.ipObj = addr;

		if ( addr == null )
		{
			this.close();
			this.valid.setL(true);
			this.requestedIP = null;
		}
		else
			this.requestedIP = addr.getHostAddress();
	}

	private void searchLocalHostIP()
	{
		if ( this.getInetAddress() == null ) this.setInetAddress(IP.LocalHostIP);
	}

	private void initialize()
	{
		this.ipObj        = IP.LocalHostIP; // Default to the local host IP (if detected)
		this.requestedIP  = this.ipObj.getHostAddress();
		this.port         = IPConstants.NO_PORT;

		this.valid        = new Pair<>(false, false);
		this.open_socket  = null;
	}
}