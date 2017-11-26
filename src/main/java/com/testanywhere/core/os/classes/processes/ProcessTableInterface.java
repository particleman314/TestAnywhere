package com.testanywhere.core.os.classes.processes;

import com.testanywhere.core.os.classes.support.machines.BaseMachineConstants;

public interface ProcessTableInterface 
{
	void decodeProcessTable(BaseMachineConstants.MACHINE_TYPE_PATTERNS mt, String tableData);
	String getType();
	void clear();
}
