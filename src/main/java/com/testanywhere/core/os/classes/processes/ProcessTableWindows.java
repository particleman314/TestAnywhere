package com.testanywhere.core.os.classes.processes;

import com.testanywhere.core.os.classes.support.machines.BaseMachineConstants;
import com.testanywhere.core.utilities.exceptions.ObjectCreationException;
import com.testanywhere.core.utilities.logging.DisplayManager;
import com.testanywhere.core.utilities.logging.DisplayType;
import com.testanywhere.core.utilities.logging.Tabbing;
import com.testanywhere.core.utilities.logging.TextManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProcessTableWindows extends ProcessTable
{
	public static final int MAXIMUM_PID = 32767;
	
	public ProcessTableWindows()
	{
		super();
	}

	public ProcessTableWindows( String tableData )
	{
		super(BaseMachineConstants.MACHINE_TYPE_PATTERNS.WINDOWS, tableData);
	}

	public ProcessTableWindows( final ProcessTableWindows unixTable ) throws ObjectCreationException, CloneNotSupportedException
	{
		this();
		if ( unixTable == null ) throw new ObjectCreationException(ProcessTableUnix.class);

		for ( ProcessTableItem pti : unixTable.getProcessTable() )
			this.processTable.add((ProcessTableItem) pti.copy());
	}

	@Override
	public String getType() 
	{
		return BaseMachineConstants.MACHINE_TYPE_PATTERNS.WINDOWS.toString();
	}

	@Override
	public void decodeProcessTable(BaseMachineConstants.MACHINE_TYPE_PATTERNS mt, String tableData)
	{
		if ( ! TextManager.validString(tableData) ) return;
		if ( ! BaseMachineConstants.MACHINE_TYPE_PATTERNS.WINDOWS.isSame(mt) ) return;
		
		String[] lines = tableData.split("\n");
		if ( lines.length == 0 ) return;

		final Pattern pspattern = mt.getProcessTablePattern();

		for ( String l : lines )
		{
			if ( l.startsWith("Image Name") ) { continue; }
			
			Matcher psmatcher = pspattern.matcher(l);
			
			// Need to be able to properly match...
			if ( psmatcher.find() )
			{
				ProcessTableWindowsItem entry = new ProcessTableWindowsItem();
				for ( int loop = 1; loop <= psmatcher.groupCount(); ++loop )
				{
					String field = psmatcher.group(loop);
					switch ( loop )
					{
						case 1:
						{
							entry.setImageName(field);
							break;
						}
						case 2:
						{
							entry.setPID(Integer.valueOf(field));
							break;
						}
						case 3:
						{
							entry.setSession(field);
							break;
						}
						case 4:
						{
							entry.setSessionID(Integer.valueOf(field));
							break;
						}
						case 5:
						{
							entry.setMemory(field);
							break;
						}
					}
				}
				this.getProcessTable().add(entry);
			}
		}
	}

	@Override
	public boolean isNull() { return false; }

	@Override
	public void buildObjectOutput( int numTabs )
	{
		if (numTabs < 0) numTabs = 0;
		Tabbing tabEnvironment = new Tabbing(numTabs);
		DisplayManager dm = this.getDM();

		String outerSpacer = tabEnvironment.getSpacer();
		dm.append(outerSpacer + "ProcessTable (WINDOWS) :", DisplayType.TEXTTYPES.LABEL);

		tabEnvironment.increment();

		for (ProcessTableItem pti : this.getProcessTable())
		{
			pti.buildObjectOutput( tabEnvironment.numberTabs() );
			dm.addFormatLines(pti.getDM().getLines());
		}
	}
}
