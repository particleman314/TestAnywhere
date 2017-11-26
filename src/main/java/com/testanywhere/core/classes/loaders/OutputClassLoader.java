package com.testanywhere.core.classes.loaders;

import com.testanywhere.core.utilities.class_support.identification.Id;
import com.testanywhere.core.utilities.class_support.identification.IdGenerator;
import com.testanywhere.core.utilities.logging.*;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class OutputClassLoader extends ClassLoader implements OutputDisplayInterface, Id
{
	protected Logger logger;
	protected long id;
	protected String address;
	protected DisplayManager dm;

	static
	{
		LogConfiguration.configure();
	}

	public OutputClassLoader()
	{
		this.id = IdGenerator.generate();
			this.address = "0x" + Long.toHexString(IdGenerator.address(this));

		this.setLog(this);

		this.dm = new DisplayManager();
		this.dm.setReplacementSeparator("-->");
	}

	@Override
	public long id() { return id; }

	@Override
	public String address() { return address; }

	@Override
	public void prettyPrint() 
	{
		this.prettyPrint(0);
	}

	@Override
	public void prettyPrint(final int numTabs)
	{
		this.prettyPrint(System.out, numTabs);
	}

	@Override
	public void prettyPrint(OutputStream os, int numTabs) 
	{
		if ( os == null ) os = System.out;
		PrintStream ps = new PrintStream(os);
		
		if ( numTabs < 0 ) numTabs = 0;
		ps.println(this.getPrettyPrint(numTabs));
		ps.flush();
	}

	@Override
	public void prettyPrint( MultiOutputStream mos, int numTabs )
	{
		if ( mos == null ) {
			mos = new MultiOutputStream();
			mos.addStream(System.out);
		}

		if ( numTabs < 0 ) numTabs = 0;
		for ( OutputStream os : mos.getStreams() )
		{
			PrintStream ps = new PrintStream(os);
			ps.println(this.getPrettyPrint(numTabs));
			ps.flush();
		}
	}

	@Override
	public void log( final String level, final String message )
	{
		if ( TextManager.validString(level) || TextManager.validString(message)) return;
		if ( this.getLog() != null )
		{
			Method[] methods = this.getLog().getClass().getMethods();
			for ( Method m : methods )
			{
				if ( level.equalsIgnoreCase(m.getName()) && m.getParameterTypes().length == 1 )
				{
					try
					{
						m.invoke(this.getLog(), message);
						break;
					}
					catch (IllegalAccessException | IllegalArgumentException
							| InvocationTargetException e)
					{
						System.err.println("Invocation of " + TextManager.specializeName(level) + " messaging failed." + TextManager.EOL);
						System.err.println("Error : " + e);
					}
				}
			}
		}
	}

	// Only method any class needs to define to utilize this interface
	@Override
	public abstract void buildObjectOutput(int numTabs);

	public String getPrettyPrint( int numTabs )
	{
		this.buildObjectOutput(numTabs);
		return this.getDM().forFormattedDisplay();
	}

	public void log( final Level level, final String message )
	{
		this.log(level.toString(), message);
	}

	public void changeLogging( final Level level )
	{
		this.getLog().setLevel(level);
	}

	public void changeLogging( final String level )
	{
		this.getLog().setLevel(Level.toLevel(level));
	}

	public void announce()
	{
		this.info("Starting routine : " + TextManager.specializeName(TextManager.getMethodNameFromStackTrace(2)));
	}

	public void announce( final String level )
	{
		this.announce(Level.toLevel(level));
	}

	public void announce( final Level level )
	{
		this.log(level, "Starting routine : " + TextManager.specializeName(TextManager.getMethodNameFromStackTrace(2)));
	}

	public void warn( final String message )
	{
		this.log(Level.WARN, message);
	}

	public void error( final String message )
	{
		this.log(Level.ERROR, message);
	}

	public void fatal( final String message )
	{
		this.log(Level.FATAL, message);
	}

	public void info( final String message )
	{
		this.log(Level.INFO, message);
	}

	public void trace( final String message )
	{
		this.log(Level.TRACE, message);
	}

	public void debug( final String message )
	{
		this.log(Level.DEBUG, message);
	}

	public void setLog( final Object obj )
	{
		this.logger = Logger.getLogger(obj.getClass());
		this.logger.setLevel(Level.INFO);
	}

	public void replaceLog( final Logger log )
	{
		if ( log != null )
			this.logger = log;
	}

	public Logger getLog()
	{
		return this.logger;
	}

	public void setLog( final Logger logger )
	{
		if ( logger != null ) this.logger = logger;
	}

	public DisplayManager getDM() { return this.dm; }

	private String addException( final Exception e )
	{
		return TextManager.EOL + "Error reported --> [ " + e.getLocalizedMessage() + " ]";
	}
}
