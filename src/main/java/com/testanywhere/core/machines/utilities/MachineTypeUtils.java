package com.testanywhere.core.machines.utilities;

import com.testanywhere.core.machines.actions.ActionConstants;
import com.testanywhere.core.classes.class_support.CompartmentObject;
import com.testanywhere.core.utilities.logging.LogConfiguration;
import com.testanywhere.core.utilities.logging.TextManager;
import com.testanywhere.core.machines.machinetypes.MachineTypeConstants;
import com.testanywhere.core.machines.machinetypes.UnixTypeConstants;
import com.testanywhere.core.machines.machinetypes.WindowsTypeConstants;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Map;
import java.util.regex.Matcher;

public class MachineTypeUtils
{
	public static Logger logger;

	static
	{
		MachineTypeUtils.logger = Logger.getLogger("MachineTypeUtils");

		MachineTypeConstants.getInstance();
		UnixTypeConstants.getInstance();
		WindowsTypeConstants.getInstance();

		LogConfiguration.configure();
	}

	/*public static void displayRecord(OutputStream os, Integer numTabs, MachineRecord mr)
	{
		if ( os == null ) os = System.out;
		final PrintStream ps = new PrintStream(os);
			
		ps.println(mr.getPrettyPrint(numTabs));
	}*/

	public static String toLocalMachinePathStyle( String s, boolean LvsR )
	{
		if ( LvsR == ActionConstants.LOCAL )
		{
			if ( "\\".equals(File.separator) )
				return s.replaceAll("/", Matcher.quoteReplacement(File.separator));
		}
		return s;
	}

	/*public static NimAddress getNimAddressFromMachineRecord( MachineRecord mr )
	{
		String nimaddr_str = ClassManagement.cast(mr.getDynamicData("NIMADDRESS"));
		NimAddress nimaddr = null;
	
		if ( TextManager.validString(nimaddr_str) )
		{
			if ( NimUtils.isValidNimAddress(nimaddr_str, TopologyConstants.TOPOLOGY_ENTITY_PROBE) )
			{
				nimaddr = new NimAddress(nimaddr_str);
			}
		}
		else
		{
			MachineLookupTable mtlr = Cast.cast(Registrar.getManager("com.nimsoft.machines", "MachineLookupTable"));
			nimaddr = new NimAddress(NimUtils.makeAddress(mtlr.findRecord(mr.getMachineIP().getIP()).getRobotAddress(), "controller"));
		}
		return nimaddr;
	}
	
	public static MachineRecord getMachineRecordFromNimAddress( NimAddress nimaddr )
	{
		if ( nimaddr == null ) { return null; }

		String nimaddr_str = nimaddr.toString();
		if ( ! TextManager.validString(nimaddr_str) || !NimUtils.isValidNimAddress(nimaddr_str, TopologyConstants.TOPOLOGY_ENTITY_PROBE)) { return null; }

		MachineLookupTable mtlr = ClassManagement.cast(Registrar.getManager("com.nimsoft.machines", "MachineLookupTable"));
		if ( mtlr != null )
		{
			return mtlr.findRecord(nimaddr).getMachineRecord();
		}
		return null;
	}

	public static int getPortFromNimAddress( NimAddress nimaddr )
	{
		if ( nimaddr == null || ! nimaddr.isFullAddressPath() ) return IPConstants.NO_PORT;
		String probeID = nimaddr.getProbe();

		MachineLookupTable mtl = ClassManagement.cast(Registrar.getManager("com.nimsoft.machines", "MachineLookupTable"));
		if ( mtl != null )
		{
			MachineLookupTableRecord mtlr = mtl.findRecord(nimaddr);
			return mtlr.getProbePort(probeID);
		}
		return IPConstants.NO_PORT;
	}

	public static boolean determineNMSHome( String puCMD, MachineType mt )
	{
		// Setup data structure for UIM machine (this will set the default location for the NMS homedir)
		mt.addUIMData();
		if ( ! puCMD.startsWith(mt.getUIMData().getNMSHome()) )
		{
			String NMSHome = puCMD.replaceFirst(mt.buildPath("bin", "pu"), "");
			mt.getUIMData().setNMSHome(NMSHome);
			return true;
		}
		return false;
	}*/

	// Return Linux/Unix/Windows
	public static String determineOSVariety( String data )
	{
		String OSFlavorClass = null;
		if ( ! TextManager.validString(data) ) return null;

		data = data.toLowerCase();
		for ( String k : MachineTypeConstants.OSaliases.getMap().keySet() )
		{
			if ( k.toLowerCase().equals(data) )
			{
				OSFlavorClass = MachineTypeConstants.OSaliases.get(k) + "Type";
				break;
			}
		}

		return OSFlavorClass;
	}

	// Return Flavor of Linux
	public static String determineOSLinuxVariety( String data )
	{
		String OSFlavor = null;

		if ( ! TextManager.validString(data) ) return null;

		// Loop over Linux styles next...
		for (Map.Entry<UnixTypeConstants.LINUX_STYLES, CompartmentObject<?>> style : UnixTypeConstants.LINUX_MAP.getMap().entrySet() )
		{
			if ( data.startsWith(style.getKey().getName()) )
			{
				OSFlavor = UnixTypeConstants.LINUXTYPE;
				break;
			}
		}

		return OSFlavor;
	}

	public static String determineOSUnixVariety( String data  )
	{
		String OSFlavor = null;

		if ( ! TextManager.validString(data) ) return null;

		// Loop over Unix styles first...
		for (Map.Entry<String, CompartmentObject<?>> style : MachineTypeConstants.OSaliases.getMap().entrySet() )
		{
			String rep = (String) style.getValue().getObject();
			if ( data.startsWith(style.getKey()) )
			{
				OSFlavor = rep + "Type";
				break;
			}
		}

		return OSFlavor;
	}

	/*public static MachineType determineLocalType()
	{
		// We know it is a local type, so we can check by attempting one type to see if it
		// command which should ONLY exist in Unix.  If not then we make the assumption of
		// Windows
		MachineType mt = new UnixType();
		ConnectionClient cc = new LocalClient(mt);

		CommandLine cmdLine = new CommandLine();
		cmdLine.setCommand("uname");
		cmdLine.addCmdLineEntry("-a");

		// Run uname -a to see if the command succeeds...
		ReturnInfo result = cc.executeCommand(cmdLine);
		if ( result.getReturnCode() != ErrorUtils.SUCCESS_ID ) return mt;
		else
		{
			mt = new WindowsType();
		}
		return mt;
	}*/

	// General means for instantiating a connection type based on MachineRecord and inputs
	// for connection.  This will handle the checking for local or remote connection management
	/*public static ConnectionClient instantiateConnection( MachineRecord mr, ParameterizedObject inputs, boolean local)
	{
		if ( local )
		{
			return MachineUtils.instantiateLocalConnection(inputs);
		}
		else
		{
			return MachineUtils.instantiateRemoteConnection(mr, inputs);
		}
	}

	public static ConnectionClient instantiateLocalConnection(ParameterizedObject inputs)
	{
		return MachineUtils.makeLocalConnection(inputs);
	}*/

	/*public static boolean hasRunningProcess( MachineRecord mr, Integer processID )
	{
		if ( mr == null || processID == null || processID < 0 ) return false;

		ConnectionClient cc = mr.getConnectionManager().getAvailableConnection();
		if ( cc == null ) return false;
		
		MachineType mt = mr.getMachineOS();
		if ( mt == null ) return false;

		ProcessTableAction pta;
		if ( MachineTypeConstants.UNIX.equals(mt.getMachineType()) )
		{
			pta = new ProcessTableUnixAction(cc);
			if ( processID > ProcessTableUnix.MAXIMUM_PID ) return false;
		}
		else
		{
			pta = new ProcessTableWindowsAction(cc);
			if ( processID > ProcessTableWindows.MAXIMUM_PID ) return false;
		}
		
		return pta.hasPID(cc, processID);
	}
	

	public static Pair<Boolean, Integer> hasRunningProcess( MachineRecord mr, String processName, boolean UIMname )
	{
		Pair<Boolean, Integer> result = new Pair<>(false, -1);
		if ( mr == null || ! TextManager.validString(processName) ) return result;

		ConnectionClient cc = mr.getConnectionManager().getAvailableConnection();
		if ( cc == null ) return result;
		
		MachineType mt = mr.getMachineOS();
		if ( mt == null ) return result;

		ProcessTableAction pta;
		if ( MachineTypeConstants.UNIX.equals(mt.getMachineType()) )
		{
			pta = new ProcessTableUnixAction(cc);
		}
		else
		{
			pta = new ProcessTableWindowsAction(cc);
		}
		
		String pName = ( ! UIMname ) ? processName : mt.getUIMProcessDescriptor(processName);
		return new Pair<>(pta.hasProcess(cc, pName), pta.findPIDofProcess(pName));
	}
	
	public static Version getVersion( MachineRecord mr, String probeID, boolean useProbeUtility )
	{
		if ( mr == null || ! TextManager.validString(probeID) ) return null;
		
		ConnectionClient cc = mr.getConnectionManager().getAvailableConnection();
		if ( cc == null ) return null;
		
		MachineType mt = mr.getMachineOS();
		if ( mt == null ) return null;
		
		if ( ! useProbeUtility )
		{
			String subTreePath = null;
			boolean search = false;
			switch ( probeID.toLowerCase() )
			{
				case "robot" :
				{
					probeID = "controller";
					break;
				}
				case "hub" :
				{
					break;
				}
				default :
				{
					subTreePath = "probes";
					search = true;
				}
			}
			Pair<String, String> probeData = NimTools.getNimbusExecutable(probeID, subTreePath, mt);
			if ( search )
			{
				FindAction fa = new FindFileAction(probeData.second());
				fa.setSubTree(probeData.first());
				fa.execute(cc);
				if ( fa.getLastErrorValue() != NimException.OK ) return null;
				
				Filename fn = new Filename(fa.decodeLastOutput(), mt.getDirSeparator(), ".");
				probeData.setL(fn.path());
			}
			
			CmdAction verCmd = new CmdAction();
			verCmd.setCommand(mt.buildPath(probeData.first(), probeData.second()));
			
			CommandOptions verCmdOpts = new CommandOptions("-V");
			verCmd.setCommandOptions(verCmdOpts);
			
			verCmd.execute(cc);
			if ( verCmd.getLastErrorValue() != NimException.OK ) return null;
			
			String vstr = null;

			Pattern vp = Pattern.compile("(\\S*)\\s*\\[Build\\s*(\\S*),(\\S*)\\]");
			Matcher m = vp.matcher(verCmd.decodeLastOutput());
			
			if ( m.find(0) )
			{
				if ( m.groupCount() == 2 )
					vstr = m.group(1);
				else if ( m.groupCount() == 1 )
					vstr = m.group(0);
				
				List<String> vcomps = Arrays.asList(vstr.split("."));
				Version v = new Version(vcomps.get(0) + "." + vcomps.get(1));
				v.setBuildNumber(vcomps.get(2));
				return v;
			}
			else
			{
				return null;
			}
		}
		else
		{
			if ( ! mr.isUIMTopologyElement() ) {
				Credentials UIMcreds = new Credentials(NimConstants.DEFAULT_USERNAME, NimConstants.DEFAULT_PASSWORD);
				MachineTask machTask = new MachineTask(true, false, null, mr.getLoginInfo(), UIMcreds, NimConstants.BASE_PORT_NUMBER);
				if ( ! mr.checkForUIM(machTask) ) return null;
			}
			NimConnection nc = ClassManagement.cast(mr.getDynamicData("UIMCONNECTOR"));
			
			if ( nc == null ) return null;
			
			try 
			{
				PDS input = new PDS();
				input.put("name", probeID);
				
				Pair<PDS, Integer> output = NimUtils.runCallback(nc, NimConstants.BASE_PORT_NUMBER, "probe_list", input);
				if ( output.second() != NimException.OK ) return null;

				// This may need more design to handle probes other than hub and controller.
				String vmm = output.first().getPDS(probeID).getString("pkg_version");
				String vb = output.first().getPDS(probeID).getString("pkg_build");
				
				Version v = new Version(vmm);
				v.setBuildNumber(vb);
				return v;
			} 
			catch (NimException e) 
			{
				return null;
			}
		}
	}
	
	public static String getRobotVersion( MachineRecord mr )
	{
		if ( mr == null ) return null;
		return MachineUtils.getProbeVersion(mr, "controller", mr.isUIMTopologyElement() );
	}

	public static String getRobotVersion( MachineRecord mr, boolean useProbeUtil )
	{
		if ( mr == null ) return null;
		return MachineUtils.getProbeVersion(mr, "controller", useProbeUtil);
	}

	public static String getHubVersion( MachineRecord mr )
	{
		if ( mr == null ) return null;
		return MachineUtils.getProbeVersion(mr, "hub", mr.isUIMTopologyElement() );
	}
	
	public static String getHubVersion( MachineRecord mr, boolean useProbeUtil )
	{
		if ( mr == null ) return null;
		return MachineUtils.getProbeVersion(mr, "hub", useProbeUtil);
	}
	
	public static String getProbeVersion( MachineRecord mr, String probeID, Boolean useProbeUtil )
	{
		if ( mr == null ) return null;
		if ( useProbeUtil == null ) useProbeUtil = true;
		Version v = MachineUtils.getVersion(mr, probeID, useProbeUtil);
		if ( v == null ) return null;
		return v.getVersion();
	}*/
	
	/*protected static ConnectionClient instantiateRemoteConnection( MachineRecord mr, ParameterizedObject inputs )
	{
		ConnectionClient cc = null;
		cc = MachineUtils.makeSSHConnection(mr, inputs);
		if ( cc != null && cc.hasConnection() )
		{
			return cc;
		}

		cc = MachineUtils.makeWMIConnection(mr, inputs);
		if ( cc != null && cc.hasConnection() )
		{
			return cc;
		}

		return null;
	}

	// Base methods to instantiate a type of connection based on MachineRecord and expected
	// connection inputs
	protected static LocalClient makeLocalConnection( ParameterizedObject inputs )
	{
		ConnectionFactory cf = new ConnectionFactory();
		ConnectionClient cc = cf.instantiate("LocalClient", inputs.getParameterization());
		return ClassManagement.cast(cc);
	}

	protected static SSHClient makeSSHConnection( MachineRecord mr, ParameterizedObject inputs )
	{
		ConnectionFactory cf = new ConnectionFactory();
		ConnectionClient cc = cf.instantiate("SSHClient", inputs.getParameterization());

		SSHClient scc = ClassManagement.cast(cc);
		if ( scc == null ) return null;

		scc.setCredentials(mr.getLoginInfo().getCredentials());
		scc.setIP(mr.getLoginInfo().getIPObj());
		scc.connect();
		return scc;
	}

	protected static WMIClient makeWMIConnection( MachineRecord mr, ParameterizedObject inputs )
	{
		ConnectionFactory cf = new ConnectionFactory();
		ConnectionClient cc = cf.instantiate("WMIClient", inputs.getParameterization());

		WMIClient wincc = ClassManagement.cast(cc);
		if ( wincc == null ) return null;

		//wincc.setCredentials(mr.getLoginInfo().getCredentials());
		wincc.connect();
		return wincc;
	}*/
}
