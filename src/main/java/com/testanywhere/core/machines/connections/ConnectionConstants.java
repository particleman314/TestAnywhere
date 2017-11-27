package com.testanywhere.core.machines.connections;

import com.testanywhere.core.utilities.Constants;
import com.testanywhere.core.utilities.class_support.functional_support.ConstantsInterface;

public class ConnectionConstants implements ConstantsInterface
{
	public static final int DEFAULT_BUSY_SLEEP = 30 * Constants.MILLISECONDS_PER_SECOND;
	public static final int DEFAULT_CONNECTION_TIMEOUT = 30;
	public static int CONNECTION_TIMEOUT = DEFAULT_CONNECTION_TIMEOUT;

	public static final String SSH = "SSH";
	public static final String WMI = "WMI";

	public static final String CLIENT_CLASS = "Client";

	public static final String MACHINE_TYPE_KEY = "MACHINETYPE";

	public enum CONNECTION_STATE {
		CONNECTED("CONNECTED"),
		DISCONNECTED("DISCONNECTED");

		private String state = null;

		CONNECTION_STATE(String s)
		{
			this.setState(s);
		}

		public String getState() {
			return this.state;
		}

		public void setState(final String state) {
			this.state = state;
		}
	}

	public enum CONNECTION_TYPES {
		LOCAL("Local" + ConnectionConstants.CLIENT_CLASS),
		SSH("SSH" + ConnectionConstants.CLIENT_CLASS),
		WMI("WMI" + ConnectionConstants.CLIENT_CLASS);

		private String connType = null;

		CONNECTION_TYPES(String s)
		{
			this.setConnectionType(s);
		}

		public String getConnectionType() {
			return this.connType;
		}

		public void setConnectionType(final String connType) {
			this.connType = connType;
		}
		}

	private static boolean isInitialized = false;

	public static ConnectionConstants getInstance()
	{
		if (!ConnectionConstants.isInitialized)
		{
			ConnectionConstants.isInitialized = true;
			ConnectionConstants.initialize();
		}
		return ConnectionConstantsHolder.INSTANCE;
	}

	@Override
	public void reset() {
		ConnectionConstants.initialize();
	}

	private static class ConnectionConstantsHolder
	{
		public static final ConnectionConstants INSTANCE = new ConnectionConstants();
	}

	private static void initialize()
	{}
}