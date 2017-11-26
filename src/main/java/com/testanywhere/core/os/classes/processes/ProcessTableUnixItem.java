package com.testanywhere.core.os.classes.processes;

import com.testanywhere.core.utilities.class_support.BaseClass;
import com.testanywhere.core.utilities.logging.DisplayManager;
import com.testanywhere.core.utilities.logging.DisplayType;
import com.testanywhere.core.utilities.logging.Tabbing;
import org.apache.commons.lang3.StringUtils;

public class ProcessTableUnixItem extends ProcessTableItem
{
	private String user_id;
	private int    process_id;
	private int    parent_process_id;
	private int    cpu_usage;
	private String process_starttime;
	private String TTY;
	private String runtime;
	private String issued_cmd;
	
	public ProcessTableUnixItem()
	{
		super();
		this.initialize();
	}
	
	@Override
	public String toString()
	{
		return StringUtils.join(new String[]{this.getUID(), String.valueOf(this.getPID()), String.valueOf(this.getPPID()), String.valueOf(this.getCPU()), this.getStartTime(), this.getTTY(), this.getRunTime(), this.getCMD()}, " ");
	}

	@Override
	public boolean isNull()
	{
		return false;
	}

	@Override
	public void buildObjectOutput( int numTabs )
	{
		if ( numTabs < 0 ) numTabs = 0;
		Tabbing tabEnvironment = new Tabbing(numTabs);
		DisplayManager dm = this.getDM();

		String outerSpacer = tabEnvironment.getSpacer();
		dm.append(outerSpacer + "Process Table Entry : ", DisplayType.TEXTTYPES.LABEL);

		tabEnvironment.increment();
		String innerSpacer = tabEnvironment.getSpacer();

		dm.append(innerSpacer + "User ID       : " + BaseClass.checkIsNull(this.getUID()));
		dm.append(innerSpacer + "Process ID    : " + BaseClass.checkIsNull(this.getPID()));
		dm.append(innerSpacer + "Parent ID     : " + BaseClass.checkIsNull(this.getPPID()));
		dm.append(innerSpacer + "CPU Usage (%) : " + BaseClass.checkIsNull(this.getCPU()));
		dm.append(innerSpacer + "StartTime     : " + BaseClass.checkIsNull(this.getStartTime()));
		dm.append(innerSpacer + "TTY           : " + BaseClass.checkIsNull(this.getTTY()));
		dm.append(innerSpacer + "RunTime       : " + BaseClass.checkIsNull(this.getRunTime()));
		dm.append(innerSpacer + "CMD           : " + BaseClass.checkIsNull(this.getCMD()));
	}

	public String getUID()
	{
		return this.user_id;
	}
	
	public int getPID()
	{
		return this.process_id;
	}
	
	public int getPPID()
	{
		return this.parent_process_id;
	}
	
	public int getCPU()
	{
		return this.cpu_usage;
	}
	
	public String getStartTime()
	{
		return this.process_starttime;
	}
	
	public String getTTY()
	{
		return this.TTY;
	}
	
	public String getRunTime()
	{
		return this.runtime;
	}
	
	public String getCMD()
	{
		return this.issued_cmd;
	}
	
	public void setUID( String uid )
	{
		this.user_id = uid;
	}
	
	public void setPID( int pid )
	{
		this.process_id = pid;
	}
	
	public void setPPID( int ppid )
	{
		this.parent_process_id = ppid;
	}
	
	public void setCPU( int cpu )
	{
		this.cpu_usage = cpu;
	}
	
	public void setStartTime( String starttime )
	{
		this.process_starttime = starttime;
	}
	
	public void setTTY( String tty )
	{
		this.TTY = tty;
	}
	
	public void setRuntime( String runtime )
	{
		this.runtime = runtime;
	}

	public void setCMD( String cmd )
	{
		this.issued_cmd = cmd;
	}

	private void initialize()
	{
		this.user_id           = null;
		this.process_id        = ProcessTable.IMVALID_ENTRY;
		this.parent_process_id = ProcessTable.IMVALID_ENTRY;
		this.cpu_usage         = ProcessTable.IMVALID_ENTRY;
		this.process_starttime = null;
		this.TTY               = null;
		this.runtime           = null;
		this.issued_cmd        = null;
	}
}
