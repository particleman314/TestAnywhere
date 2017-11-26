package com.testanywhere.core.os.classes.processes;

import com.testanywhere.core.os.classes.support.machines.BaseMachineConstants;
import com.testanywhere.core.utilities.logging.OutputDisplay;
import com.testanywhere.core.utilities.logging.TextManager;

import java.util.ArrayList;
import java.util.Collection;

public abstract class ProcessTable extends OutputDisplay implements ProcessTableInterface
{
	public static final int IMVALID_ENTRY = -1;
	protected Collection<ProcessTableItem> processTable;

	public ProcessTable()
	{
		super();
		this.processTable = new ArrayList<>();
	}
	
	public ProcessTable( BaseMachineConstants.MACHINE_TYPE_PATTERNS mt, String procTable )
	{
		this();

		if ( ! TextManager.validString(procTable) )
			this.decodeProcessTable(mt, procTable);
	}

	@Override
	public abstract String getType();

	@Override
	public abstract void decodeProcessTable(BaseMachineConstants.MACHINE_TYPE_PATTERNS mt, String tableData);
		
	@Override
	public void clear()
	{
		this.getProcessTable().clear();
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		for ( ProcessTableItem pti : this.getProcessTable() )
			sb.append(pti.toString() + TextManager.EOL);
		return sb.toString();
	}

	public Collection<ProcessTableItem> getProcessTable()
	{
		return this.processTable;
	}

	public static ProcessTable decodeOSProcessTable(BaseMachineConstants.MACHINE_TYPE_PATTERNS mt, String tableData)
	{
		if ( BaseMachineConstants.MACHINE_TYPE_PATTERNS.UNIX.isSame(mt) )
			return new ProcessTableUnix(tableData);
		else
			return new ProcessTableWindows(tableData);
	}
}
