package com.testanywhere.core.machines.actions;

import com.testanywhere.core.utilities.class_support.functional_support.ConstantsInterface;

public class ActionConstants implements ConstantsInterface
{
	public static final Boolean LOCAL  = true;
	public static final Boolean REMOTE = false;
	
	public static final String LOCAL_ENTITY  = "file";
	public static final String REMOTE_WEB_ENTITY = "http";
	public static final String REMOTE_FILE_ENTITY = "ftp";
	public static final String REMOTE_WEB_SECURED_ENTITY = "https";

	public static final int LOCAL_TO_LOCAL   = 0;
	public static final int REMOTE_TO_LOCAL  = 1;
	public static final int LOCAL_TO_REMOTE  = 2;
	public static final int REMOTE_TO_REMOTE = 3;

	public static final String FILE_URL  = LOCAL_ENTITY + "://";
	public static final String HTTP_URL  = REMOTE_WEB_ENTITY + "://";
	public static final String HTTPS_URL = HTTP_URL.replace(REMOTE_WEB_ENTITY, REMOTE_WEB_SECURED_ENTITY);
	public static final String FTP_URL   = REMOTE_FILE_ENTITY + "://";

	private static boolean isInitialized = false;

	public enum FSTYPES {
		FILE {
			public String toString() { return "file"; }
		},
		DIRECTORY {
			public String toString() { return "directory"; }
		},
	}

	public static ActionConstants getInstance()
    {
    	if ( ! ActionConstants.isInitialized ) 
    	{
    		ActionConstants.isInitialized = true;
    		ActionConstants.initialize();
    	}
    	return ActionConstantsHolder.INSTANCE;
    }

	@Override
	public void reset()
	{
		ActionConstants.initialize();
	}

	private static class ActionConstantsHolder
	{ 
    	public static final ActionConstants INSTANCE = new ActionConstants();
    }

	private static void initialize() 
	{}
}
