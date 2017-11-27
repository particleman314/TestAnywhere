package com.testanywhere.core.machines.machinetypes;

import com.testanywhere.core.classes.support.map.DynamicMap;
import com.testanywhere.core.utilities.class_support.functional_support.ConstantsInterface;

public class MachineTypeConstants implements ConstantsInterface
{
	static
	{
		UnixTypeConstants.getInstance();
		WindowsTypeConstants.getInstance();
	}

	public static final DynamicMap<String> SHELL_MAP = new DynamicMap<>();

	public static final String SPECIFICATION_KEY = "specification";
	public static final String CLASSIFICATION_KEY = "classification";
	public static final String EXE_EXTENSION = "exe_extension";
	public static final String SHELL = "shell";
	public static final String CMD_SEPARATOR = "cmd_separator";
	public static final String DIR_SEPARATOR = "dir_separator";

	//public static final String DEFAULT_UNIX_NMSHOME = "/opt/nimsoft";

	public static final DynamicMap<String> OSaliases = new DynamicMap<>();

	private static boolean isInitialized = false;

	public static MachineTypeConstants getInstance()
    {
    	if ( ! MachineTypeConstants.isInitialized )
    	{
    		MachineTypeConstants.isInitialized = true;
    		MachineTypeConstants.initialize();
    	}
    	return MachineTypeConstantsHolder.INSTANCE;
    }

	@Override
	public void reset()
	{
		MachineTypeConstants.initialize();
	}

	private static class MachineTypeConstantsHolder
	{ 
    	public static final MachineTypeConstants INSTANCE = new MachineTypeConstants();
    }

	private static void initialize()
	{
		MachineTypeConstants.OSaliases.put("Linux", "Linux");
		MachineTypeConstants.OSaliases.put("GNU", "Linux");
		MachineTypeConstants.OSaliases.put("SunOS", "Solaris");
		MachineTypeConstants.OSaliases.put("AIX", "AIX");
		MachineTypeConstants.OSaliases.put("HP-UX", "HPUX");
		MachineTypeConstants.OSaliases.put("zLinux", "zLinux");
		MachineTypeConstants.OSaliases.put("Microsoft", "Windows");

		UnixTypeConstants.installShells();
		WindowsTypeConstants.installShells();
	}
}
