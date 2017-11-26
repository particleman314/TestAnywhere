package com.testanywhere.core.os.classes.processes;

import com.testanywhere.core.utilities.class_support.BaseClass;
import com.testanywhere.core.utilities.logging.DisplayManager;
import com.testanywhere.core.utilities.logging.DisplayType;
import com.testanywhere.core.utilities.logging.Tabbing;
import org.apache.commons.lang3.StringUtils;

public class ProcessTableWindowsItem extends ProcessTableItem
{
	private int    process_id;
	private String imageName;
	private String sessionName;
	private int    sessionID;
	private String memory;
	
	public ProcessTableWindowsItem()
	{
		super();
		this.initialize();
	}
	
	@Override
	public String toString()
	{
		return StringUtils.join(new String[]{String.valueOf(this.getPID()), this.getImageName(), this.getSession(), String.valueOf(this.getSessionID()), this.getMemory()}, " ");
	}

	@Override
	public boolean isNull() { return false; }

	@Override
	public void buildObjectOutput( int numTabs )
	{
		if ( numTabs < 0 ) numTabs = 0;
		Tabbing tabEnvironment = new Tabbing(numTabs);
		DisplayManager dm = this.getDM();

		String outerSpacer = tabEnvironment.getSpacer();
		dm.append(outerSpacer + "Process Table Entry :", DisplayType.TEXTTYPES.LABEL);

		tabEnvironment.increment();
		String innerSpacer = tabEnvironment.getSpacer();

		dm.append(innerSpacer + "Process ID    : " + BaseClass.checkIsNull(this.getPID()));
		dm.append(innerSpacer + "Image         : " + BaseClass.checkIsNull(this.getImageName()));
		dm.append(innerSpacer + "Session Name  : " + BaseClass.checkIsNull(this.getSession()));
		dm.append(innerSpacer + "Session ID    : " + BaseClass.checkIsNull(this.getSessionID()));
		dm.append(innerSpacer + "Memory        : " + BaseClass.checkIsNull(this.getMemory()));
	}

	public int getPID()
	{
		return this.process_id;
	}
	
	public String getImageName()
	{
		return this.imageName;
	}
	
	public String getSession()
	{
		return this.sessionName;
	}
	
	public int getSessionID()
	{
		return this.sessionID;
	}
	
	public String getMemory()
	{
		return this.memory;
	}
	
	public void setPID( int pid )
	{
		this.process_id = pid;
	}
	
	public void setImageName( String image )
	{
		this.imageName = image;
	}
	
	public void setSession( String session )
	{
		this.sessionName = session;
	}
	
	public void setSessionID( int sessionID )
	{
		this.sessionID = sessionID;
	}
	
	public void setMemory( String memory )
	{
		this.memory = memory;
	}
	
	private void initialize()
	{
		this.process_id  = ProcessTable.IMVALID_ENTRY;
		this.sessionID   = ProcessTable.IMVALID_ENTRY;
		this.imageName   = null;
		this.sessionName = null;
		this.memory      = null;
	}
}
