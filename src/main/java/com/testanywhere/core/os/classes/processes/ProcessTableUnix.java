package com.testanywhere.core.os.classes.processes;

import com.testanywhere.core.os.classes.support.machines.BaseMachineConstants;
import com.testanywhere.core.utilities.exceptions.ObjectCreationException;
import com.testanywhere.core.utilities.logging.DisplayManager;
import com.testanywhere.core.utilities.logging.DisplayType;
import com.testanywhere.core.utilities.logging.Tabbing;
import com.testanywhere.core.utilities.logging.TextManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProcessTableUnix extends ProcessTable
{
	public static final int MAXIMUM_PID = 65535;

	public ProcessTableUnix() {
		super();
	}

	public ProcessTableUnix( final String tableData )
	{
		super(BaseMachineConstants.MACHINE_TYPE_PATTERNS.UNIX, tableData);
	}

	public ProcessTableUnix( final ProcessTableUnix unixTable ) throws ObjectCreationException, CloneNotSupportedException
	{
		this();
		if ( unixTable == null ) throw new ObjectCreationException(ProcessTableUnix.class);

		for ( ProcessTableItem pti : unixTable.getProcessTable() )
			this.processTable.add((ProcessTableItem) pti.copy());
	}

	@Override
	public String getType() {
		return BaseMachineConstants.MACHINE_TYPE_PATTERNS.UNIX.toString();
	}

	@Override
	public void decodeProcessTable(BaseMachineConstants.MACHINE_TYPE_PATTERNS mt, String tableData)
	{
		if (!TextManager.validString(tableData)) return;
		if (!BaseMachineConstants.MACHINE_TYPE_PATTERNS.UNIX.isSame(mt)) return;

		String[] lines = tableData.split("\n");
		if (lines.length == 0) return;

		final Pattern pspattern = mt.getProcessTablePattern();

		for (String l : lines) {
			l = l.trim();
			if (l.startsWith("UID")) {
				continue;
			}

			Matcher psmatcher = pspattern.matcher(l);

			// Need to be able to properly match...
			if (psmatcher.find()) {
				ProcessTableUnixItem entry = new ProcessTableUnixItem();
				for (int loop = 1; loop <= psmatcher.groupCount(); ++loop) {
					String field = psmatcher.group(loop);
					switch (loop) {
						case 1: {
							entry.setUID(field);
							break;
						}
						case 2: {
							entry.setPID(Integer.valueOf(field));
							break;
						}
						case 3: {
							entry.setPPID(Integer.valueOf(field));
							break;
						}
						case 4: {
							entry.setCPU(Integer.valueOf(field));
							break;
						}
						case 5: {
							entry.setStartTime(field);
							break;
						}
						case 6: {
							entry.setTTY(field);
							break;
						}
						case 7: {
							entry.setRuntime(field);
							break;
						}
						case 8: {
							entry.setCMD(field);
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
		dm.append(outerSpacer + "ProcessTable (UNIX) :", DisplayType.TEXTTYPES.LABEL);

		tabEnvironment.increment();

		for (ProcessTableItem pti : this.getProcessTable())
		{
			pti.buildObjectOutput( tabEnvironment.numberTabs() );
			dm.addFormatLines(pti.getDM().getLines());
		}
	}
}
