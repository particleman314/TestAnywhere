package com.testanywhere.core.os.classes.support.process;

import com.testanywhere.core.classes.support.Filename;
import com.testanywhere.core.classes.support.RollBack;
import com.testanywhere.core.os.utilities.CommandUtils;
import com.testanywhere.core.utilities.class_support.Cast;
import com.testanywhere.core.utilities.exceptions.ObjectCreationException;
import com.testanywhere.core.utilities.logging.*;
import org.apache.commons.collections4.MultiMap;
import org.apache.commons.collections4.map.MultiValueMap;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

public class CommandLine extends OutputDisplay
{
	protected static final int NO_INDEX = -1;

	private Filename cmdInfo;
	private MultiMap<Integer, CommandOption> cmdOptions;
	private RollBack<CommandOption> cachedLastOption;
	private File workingDirectory;

	public CommandLine()
	{
		super();
		this.initialize();
	}

	public CommandLine( final String ... args )
	{
		this();
		for ( String a : args )
		{
			this.addCmdLineEntries(CommandUtils.generateOptionsFromString(a));
		}
	}

	public CommandLine( final CommandLine cl ) throws ObjectCreationException
	{
		this();
		if ( cl == null ) throw new ObjectCreationException(CommandLine.class);

		this.cmdOptions = copy(cl.cmdOptions);
		this.workingDirectory = copy(cl.workingDirectory);
		try
		{
			this.cachedLastOption = cl.cachedLastOption.copy();
			this.cmdInfo = cl.cmdInfo.copy();
		}
		catch (CloneNotSupportedException e)
		{
			throw new ObjectCreationException(CommandLine.class);
		}
	}

	@Override
	public void buildObjectOutput (int numTabs)
	{
		Tabbing tabEnvironment = new Tabbing(numTabs);
		DisplayManager dm = this.getDM();

		String outerSpacer = tabEnvironment.getSpacer();
		dm.append(outerSpacer + "CommandLine :" , DisplayType.TEXTTYPES.LABEL);
		
		tabEnvironment.increment();

		// Allow for ordered map
		MultiMap<Integer, CommandOption> cmdopts = this.getCommandOptionMap();
		for ( Integer cmdOptIdx : cmdopts.keySet() )
		{
			Collection<CommandOption> opts = Cast.cast(cmdopts.get(cmdOptIdx));
			if (opts != null)
				for (CommandOption co : opts)
				{
					co.buildObjectOutput(tabEnvironment.numberTabs());
					dm.addFormatLines(co.getDM().getLines());
				}
		}

		if ( this.hasWorkingDirectory() )
			dm.append(outerSpacer + "Working Directory : " + this.getWorkingDirectory());
		else
			dm.append(outerSpacer + "Working Directory : <NONE>");

	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		sb.append("CMD:[" + this.getCommand() + "]" + TextManager.STR_OUTPUTSEPARATOR);

		MultiMap<Integer, CommandOption> cmdopts = this.getCommandOptionMap();
		for ( Integer cmdOptIdx : cmdopts.keySet() )
		{
			Collection<CommandOption> opts = Cast.cast(cmdopts.get(cmdOptIdx));
			if (opts != null)
				for (CommandOption co : opts)
					sb.append(co.toString() + TextManager.STR_OUTPUTSEPARATOR);
		}

		if ( this.hasWorkingDirectory() )
			sb.append("WD:[" + this.getWorkingDirectory() +"]" + TextManager.STR_OUTPUTSEPARATOR);
		else
			sb.append("WD:[]" + TextManager.STR_OUTPUTSEPARATOR);

		String result = sb.toString();
		if ( result.length() > 0 )
			result = result.substring(0, result.length() - 1);
		return result;
	}

	@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
	@Override
	public boolean equals( Object other )
	{
		if ( other == null ) return false;
		
		CommandLine cmdLine = Cast.cast(other);
		if ( cmdLine == null ) return false;
		
		if ( cmdLine.getCommandOptionMap().size() != this.getCommandOptionMap().size() ) return false;

		MultiMap<Integer, CommandOption> cmdopts = this.getCommandOptionMap();
		for ( Integer cmdOptIdx : cmdopts.keySet() )
		{
			Collection<CommandOption> opts = Cast.cast(cmdopts.get(cmdOptIdx));
			if (opts != null)
				for (CommandOption co : opts) {
					CommandOption matchedCounterPart = cmdLine.findCommandOption(co.getOption());
					if (matchedCounterPart == null) return false;
					if (!this.matchArguments(co, matchedCounterPart)) return false;
				}
		}
		return true;
	}

	@Override
	public boolean isNull()
	{
		return false;
	}

	public boolean similar( Object other )
	{
		if ( other == null ) return false;

		CommandLine cmdLine = Cast.cast(other);
		if ( cmdLine == null ) return false;

		if ( (cmdLine.size() != this.size()) || (cmdLine.optionSize() != this.optionSize()) ) return false;

		MultiMap<Integer, CommandOption> cmdopts = this.getCommandOptionMap();
		for ( Integer cmdOptIdx : cmdopts.keySet() )
		{
			Collection<CommandOption> opts = Cast.cast(cmdopts.get(cmdOptIdx));
			if (opts != null)
				for (CommandOption co : opts) {
					CommandOption matchedCounterPart = cmdLine.findCommandOption(co.getOption());
					if (matchedCounterPart == null) return false;
				}
		}
		return true;
	}

	public CommandOption findCommandOption( String s )
	{
		if ( ! TextManager.validString(s) ) return null;

		MultiMap<Integer, CommandOption> cmdopts = this.getCommandOptionMap();
		for ( Integer cmdOptIdx : cmdopts.keySet() )
		{
			Collection<CommandOption> opts = Cast.cast(cmdopts.get(cmdOptIdx));
			if (opts != null)
				for (CommandOption co : opts)
					if (s.equals(co.getOption())) return co;
		}

		return null;
	}

	public Integer findCommandOptionIdx( CommandOption requestedCmdOption )
	{
		MultiMap<Integer, CommandOption> cmdopts = this.getCommandOptionMap();
		for (Integer cmdOptIdx : cmdopts.keySet())
		{
			Collection<CommandOption> opts = Cast.cast(cmdopts.get(cmdOptIdx));
			if (opts != null)
				for (CommandOption co : opts)
					if (co.similar(requestedCmdOption)) return cmdOptIdx;

		}
		return CommandLine.NO_INDEX;
	}

	public Collection<CommandOption> getCommandOptions()
	{
		this.combine();

		Collection<CommandOption> finalCmdList = new ArrayList<>();

		MultiMap<Integer, CommandOption> cmdopts = this.getCommandOptionMap();
		for ( Integer cmdOptIdx : cmdopts.keySet() ) {
			Collection<CommandOption> opts = Cast.cast(cmdopts.get(cmdOptIdx));
			if (opts != null)
				finalCmdList.addAll(opts);
		}
		return finalCmdList;
	}

	public void setCommand( String cmdExe )
	{
		if ( ! TextManager.validString(cmdExe) ) return;

		this.cmdInfo = new Filename(cmdExe);
	}

	public void setWorkingDirectory( String dirname )
	{
		//if (! FileDirUtils.directoryExists(dirname) ) return;
		File dirf = new File(dirname);
		this.setWorkingDirectory(dirf);
	}

	public void setWorkingDirectory( File dirname )
	{
		//if (! FileDirUtils.directoryExists(dirname) ) return;
		this.workingDirectory = dirname;
	}

	public String getWorkingDirectory()
	{
		if ( this.workingDirectory == null ) return null;
		return this.workingDirectory.getAbsolutePath();
	}

	public String getCommand()
	{
		if ( this.cmdInfo == null ) return "";
		return this.cmdInfo.getName();
	}

	public boolean hasWorkingDirectory() { return this.getWorkingDirectory() != null; }

	public void setCommandOptions( MultiMap<Integer, CommandOption> cmdoptions )
	{
		this.cmdOptions = cmdoptions;
	}

	public boolean addCmdLineEntries( Collection<CommandOption> coOpts )
	{
		if ( coOpts == null || coOpts.size() < 1 ) return false;
		for ( CommandOption co : coOpts )
			if ( ! this.addCmdLineEntry(co) ) return false;
		return true;
	}

	public boolean addCmdLineEntry( CommandOption co, int location )
	{
		if ( co == null || ! TextManager.validString(co.getOption()) ) return false;
		if ( location < 0 ) location = 0;

		MultiMap<Integer, CommandOption> cmdOpts = this.getCommandOptionMap();
		int matchedIdx = this.findCommandOptionIdx(co);
		if ( matchedIdx == CommandLine.NO_INDEX )
			if ( ! cmdOpts.containsKey(location) ) location = cmdOpts.size();
		else
			location = matchedIdx;

		cmdOpts.put(location, co);
		this.cachedLastOption.push(co);
		return true;
	}

	public boolean addCmdLineEntry( CommandOption co )
	{
		Integer idx = this.findCommandOptionIdx(co);
		Collection<CommandOption> prevCO = null;
		if ( idx != CommandLine.NO_INDEX )
			prevCO = Cast.cast(this.getCommandOptionMap().get(idx));

		if ( prevCO != null )
			return prevCO.add(co);
		else
			return this.addCmdLineEntry(co, this.getCommandOptionMap().size());
	}

	public boolean addCmdLineEntry( String cmdOption ) {
		return TextManager.validString(cmdOption) && this.findCommandOption(cmdOption) == null && this.addCmdLineEntry(new CommandOption(cmdOption));
	}

	public void combine()
	{
		MultiMap<Integer, CommandOption> cmdopts = this.getCommandOptionMap();
	}

	public void expand()
	{
		MultiMap<Integer, CommandOption> cmdopts = this.getCommandOptionMap();
	}

	public boolean hasOptionWithArgument( String option, String arg )
	{
		if ( ! TextManager.validString(arg) ) return false;

		MultiMap<Integer, CommandOption> cmdopts = this.getCommandOptionMap();
		for ( Integer cmdOptIdx : cmdopts.keySet() )
		{
			Collection<CommandOption> opts = Cast.cast(cmdopts.get(cmdOptIdx));
			if (opts != null)
				for (CommandOption co : opts)

					if (co.getOption().equals(option))
					{
						Collection<String> storedOpts = co.getArgument();
						for ( String coStr : storedOpts ) {
							if ( TextManager.validString(coStr) )
								if (coStr.equals(arg)) return true;
						}
					}
		}
		return false;
	}

	public void clear()
	{
		this.getCommandOptionMap().clear();
	}

	public int size()
	{
		return this.getCommandOptions().size();
	}

	public int optionSize()
	{
		int count= 0;
		MultiMap<Integer, CommandOption> cmdopts = this.getCommandOptionMap();
		for ( Integer cmdOptIdx : cmdopts.keySet() )
		{
			Collection<CommandOption> opts = Cast.cast(cmdopts.get(cmdOptIdx));
			if (opts != null) count += opts.size();
		}

		return count;
	}

	public void removeLastCmdLineEntry()
	{
		if ( this.cachedLastOption.getNumberItems() != 0 ) {
			CommandOption lastCmdOptionAdded = this.cachedLastOption.latest();
			Integer lastCmdOptionIdx = this.findCommandOptionIdx(lastCmdOptionAdded);

			if (lastCmdOptionIdx != NO_INDEX)
			{
				this.getCommandOptionMap().removeMapping(lastCmdOptionIdx, lastCmdOptionAdded);
				this.cachedLastOption.unroll();
			}
		}
	}
	
	public String[] useByProcess( boolean asArray )
	{
		String[] components;

		if ( ! asArray ) {
			components = new String[1];

			StringBuilder sb = new StringBuilder();

			sb.append(this.getCommand() + " ");

			MultiMap<Integer, CommandOption> cmdopts = this.getCommandOptionMap();
			for (Integer cmdOptIdx : cmdopts.keySet()) {
				Collection<CommandOption> opts = Cast.cast(cmdopts.get(cmdOptIdx));
				if (opts != null)
					for (CommandOption co : opts) {
						sb.append(co.getOption() + co.getConnector() + " ");
						if (!co.getArgument().isEmpty()) {
							for (String coStr : co.getArgument()) {
								if (TextManager.validString(coStr))
									sb.append(coStr + " ");
							}
						}
					}
			}

			components[0] = sb.toString().trim();
		}
		else
		{
			Collection<String> managedComponents = new ArrayList<>();
			managedComponents.add(this.getCommand());

			MultiMap<Integer, CommandOption> cmdopts = this.getCommandOptionMap();
			for (Integer cmdOptIdx : cmdopts.keySet()) {
				Collection<CommandOption> opts = Cast.cast(cmdopts.get(cmdOptIdx));
				if (opts != null)
					for (CommandOption co : opts)
						for ( String coStr : co.getArgument() ) {
							if (!TextManager.validString(coStr))
								managedComponents.add(co.getArgument() + co.getConnector());
							else
								managedComponents.add(co.getArgument() + co.getConnector() + coStr);
						}
			}
			components = new String[ managedComponents.size() - 1 ];
			components = managedComponents.toArray(components);
		}
		return components;
	}

	public boolean matchOption( CommandOption co1, CommandOption co2 )
	{
		if ( co1 == null || co2 == null ) return false;
		if ( ! TextManager.validString(co1.getOption()) || ! TextManager.validString(co2.getOption()) ) return false;
		return co1.getArgument().equals(co2.getArgument());
	}

	public boolean matchArguments( CommandOption co1, CommandOption co2 )
	{
		if ( co1 == null || co2 == null ) return false;
		if ( ! TextManager.validString(StringUtils.join(co1.getArgument(), " ")) || ! TextManager.validString(StringUtils.join(co2.getArgument(), " ")) ) return false;
		return co1.getArgument().equals(co2.getArgument());
	}

	private MultiMap<Integer, CommandOption> getCommandOptionMap()
	{
		return this.cmdOptions;
	}

	private void initialize()
	{
		this.cmdInfo = null;
		this.cmdOptions = new MultiValueMap<>();
		this.cachedLastOption = new RollBack<>();
		this.workingDirectory = null;

		this.cachedLastOption.allowInfiniteCapacity();
	}
}
