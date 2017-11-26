package com.testanywhere.core.os.utilities;

import com.testanywhere.core.utilities.classes.Pair;
import com.testanywhere.core.utilities.logging.LogConfiguration;
import com.testanywhere.core.utilities.logging.TextManager;
import com.testanywhere.core.os.classes.support.process.CommandLine;
import org.apache.log4j.Logger;

import java.io.*;

public class ProcessUtils 
{
	public static Logger logger;

	static
	{
		ProcessUtils.logger = Logger.getLogger("ProcessUtils");
		LogConfiguration.configure();
	}

	public static Object manageOutputStream( Process p, Object os ) throws Exception
	{
		if ( p == null ) return os;
		return ProcessUtils.manageStream(p.getInputStream(), os);
	}
	
	public static Object manageErrorStream( Process p, Object os ) throws Exception 
	{
		if ( p == null ) return os;
		return ProcessUtils.manageStream(p.getErrorStream(), os);
	}
	
	public static String getProcessOutput( Process p ) throws Exception
	{
		return ProcessUtils.manageOutputStream(p, new StringBuilder()).toString();
	}
	

	/*public static Object execService(ConnectionClient cc, String serviceID, ServiceConstants.SERVICE_ACTION type, String... serviceArgs) throws Exception
	{
		if ( cc == null || ! cc.hasConnection() ) return false;
		if ( ! TextManager.validString(serviceID) ) return false;
		
		String typeID = type.toString();
		
		if ( !TextManager.validString(typeID) ) return false;
		
		// Make Service object the one which makes sense to handle the management
		Service srv;
		if ( MachineTypeConstants.UNIX.equals(cc.getMachineType().getMachineClassification()) )
		{
			srv = new UnixService(serviceID);
		}
		else
		{
			srv = new WindowsService(serviceID);
		}
		
		if ( ServiceConstants.SERVICE_MAP.containsKey(type) )
		{
			return srv.executeService(cc, srv.makeCommand(type.toString(), serviceArgs));
		}
		return null;
	}*/
	
	public static Object manageStream( InputStream ins, Object os ) throws Exception 
	{
		if ( ins == null ) return os;
		if ( os == null ) os = new PrintStream(System.out);
		
		BufferedReader bufR = new BufferedReader(new InputStreamReader(ins));
		String line;
		
		while ( (line = bufR.readLine()) != null ) 
		{
			if ( os instanceof PrintStream ) ((PrintStream) os).println(line);
			if ( os instanceof StringBuilder) ((StringBuilder) os).append(line).append(TextManager.EOL);
		}
		
		if ( os instanceof StringBuilder ) return os;
		return null;
	}

	public static Pair<Process, String> execProcess(String command) throws Exception
	{
		Process process = Runtime.getRuntime().exec(command);
		String output = ProcessUtils.getProcessOutput(process);		
		process.waitFor();
		return new Pair<>(process, output);
	}

	// Local Client connection to ensure process may run
	public static Pair<Process, String> execProcess(CommandLine cmdInfo) throws Exception
	{
		if ( cmdInfo == null ) return null;
		
		String workDir = cmdInfo.getWorkingDirectory();
		
		if ( workDir == null ) return ProcessUtils.execProcess(cmdInfo.useByProcess(false)[0]);

		String[] components = cmdInfo.useByProcess(true);

		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.command(components);
		
		// This is local ONLY
		File workDirPtr = new File(workDir);
		if ( workDirPtr.exists() && workDirPtr.isDirectory() ) processBuilder.directory(workDirPtr.getAbsoluteFile());
			
		Process process = processBuilder.start();
		String output = ProcessUtils.getProcessOutput(process);		
		process.waitFor();
		return new Pair<>(process, output);
	}
}
