package com.testanywhere.core.utilities.logging;

import com.testanywhere.core.utilities.class_support.BaseClass;
import com.testanywhere.core.utilities.class_support.identification.Id;
import com.testanywhere.core.utilities.class_support.identification.IdGenerator;
import com.testanywhere.core.utilities.class_support.identification.IdHelper;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class OutputDisplay extends BaseClass implements OutputDisplayInterface, Id
{
	protected Logger logger;
	protected transient long id;
	protected transient String address;
	protected DisplayManager dm;

	static
	{
		LogConfiguration.configure();
	}

	public OutputDisplay()
	{
		this.id = IdGenerator.generate();

		//if ( System.getProperty("ALLOW_ADDRESS_MAPPING") != null )
		this.address = "0x" + Long.toHexString(IdGenerator.address(this));
		IdHelper.account(this);

		this.setLog(this);

		this.dm = new DisplayManager();
		this.dm.setReplacementSeparator("-->");
	}

	public void display()
	{
		this.prettyPrint();
	}

	@Override
	public void prettyPrint()
	{
		this.prettyPrint(0);
	}

	@Override
	public void log( final String level, final String message )
	{
		if ( ! TextManager.validString(level) || ! TextManager.validString(message)) return;
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
						System.err.println("Error Message : " + e.getLocalizedMessage());
					}
				}
			}
		}
	}

	@Override
	public long id() { return id; }

	@Override
	public String address() { return address; }

	@Override
	public void prettyPrint( final int numTabs )
	{
		this.prettyPrint(System.out, numTabs);
	}

	@Override
	public void prettyPrint( OutputStream os, int numTabs )
	{
		if ( os == null ) os = System.out;
		PrintStream ps = new PrintStream(os);

		if ( numTabs < 0 ) numTabs = 0;

		String idStr = "[ ID -> <NONE> ]";
		String addrStr = "[ ADDRESS -> <NONE> ]";

		boolean idmap = TextManager.validString(System.getenv("ALLOW_ID_MAPPING")) || TextManager.validString(System.getProperty("ALLOW_ID_MAPPING"));
		boolean addrmap = TextManager.validString(System.getenv("ALLOW_ADDRESS_MAPPING")) || TextManager.validString(System.getProperty("ALLOW_ADDRESS_MAPPING"));

		if ( idmap )
			idStr = "[ ID -> " + this.id() + " ]";

		if ( addrmap )
			addrStr = "[ ADDRESS -> " + this.address() + " ]";

		if ( idmap || addrmap )
			ps.println(idStr + " " + TextManager.STR_OUTPUTSEPARATOR + " " + addrStr);

		this.dm.resetTextBuilder();
		this.dm.resetCachedOutput();

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

	public void setLogLevel( final Level level )
	{
		this.getLog().setLevel(level);
	}

	public void setLogLevel( final String level )
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

	public void warn( final String message, final Exception e )
	{
		String globalMsg = message + addException(e);
		this.warn(globalMsg);
	}

	public void warn( final String message )
	{
		this.log(Level.WARN, message);
	}

	public void error( final String message, final Exception e )
	{
		String globalMsg = message + addException(e);
		this.error(globalMsg);
	}

	public void error( final String message )
	{
		this.log(Level.ERROR, message);
	}

	public void fatal( final String message, final Exception e )
	{
		String globalMsg = message + addException(e);
		this.fatal(globalMsg);
	}

	public void fatal( final String message )
	{
		this.log(Level.FATAL, message);
	}

	public void info( final String message, final Exception e )
	{
		String globalMsg = message + addException(e);
		this.info(globalMsg);
	}

	public void info( final String message )
	{
		this.log(Level.INFO, message);
	}

	public void trace( final String message, final Exception e )
	{
		String globalMsg = message + addException(e);
		this.trace(globalMsg);
	}

	public void trace( final String message )
	{
		this.log(Level.TRACE, message);
	}

	public void debug( final String message, final Exception e )
	{
		String globalMsg = message + addException(e);
		this.debug(globalMsg);
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

	public void setId( long id ) { this.id = id; }

	public void setAddress( String address )
	{
		if ( TextManager.validString(address) && address.startsWith("0x") )
			this.address = address;
	}

	public DisplayManager getDM() { return this.dm; }

	private String addException( final Exception e )
	{
		return TextManager.EOL + "Error reported --> [ " + e.getLocalizedMessage() + " ]";
	}
}
