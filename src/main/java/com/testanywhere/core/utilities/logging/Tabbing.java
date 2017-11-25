package com.testanywhere.core.utilities.logging;

import com.testanywhere.core.utilities.exceptions.ObjectCreationException;
import org.apache.commons.lang3.StringUtils;

public class Tabbing
{
	public static final int NO_MAXIMUM_SIZE = -1;

	public int currentTabLevel;
	public int maximumSize;
	public int tabSize;
	
	public Tabbing()
	{
		this.initialize();
	}
	
	public Tabbing( final int numTabs ) throws ObjectCreationException
	{
	    this();
		if ( numTabs < 0 ) throw new ObjectCreationException("Invalid number of initial tabs requested : " + numTabs);
		this.currentTabLevel = numTabs;
	}
	
	public Tabbing( final int numTabs, final int tabSize ) throws ObjectCreationException
	{
	    this(numTabs);
		if ( tabSize < 0 ) throw new ObjectCreationException("Invalid size of tab requested : " + tabSize );
	    this.changeTabSize(tabSize);
	}
	
	public void changeTabSize( final int tabSize )
	{
		if (tabSize >= 0 ) this.setTabSize(tabSize);
	}
	
	public int getMaximumSize()
	{
		return this.maximumSize;
	}

	public int getTabSize()
	{
		return this.tabSize;
	}

	public void setTabSize( int rqtTabSize )
	{
		if ( rqtTabSize < 0 ) return;
		if ( this.getMaximumSize() > Tabbing.NO_MAXIMUM_SIZE && rqtTabSize > this.getMaximumSize() ) rqtTabSize = this.getMaximumSize();
		this.tabSize = rqtTabSize;
	}

	public String getSpacer() 
	{
	    return Tabbing.computeTabbing(this.numberTabs(), this.getTabSize());
	}
	
	public String getSpacer( final String data )
	{
	    return Tabbing.computeTabbing(this.numberTabs(), this.getTabSize()) + data;
	}
	
	public void setMaximumSize( final int maxTabSize )
	{
		if ( maxTabSize < -1 ) return;
		if ( maxTabSize >= Integer.MAX_VALUE ) return;
		this.maximumSize = maxTabSize;
		this.checkTabLevel();
	}
	
	public void reset() 
	{
	    this.currentTabLevel = 0;
	}
	
	public void decrement() 
	{
	    this.decrement(1);
	}
	
	public void decrement( int subtract ) 
	{
		if ( subtract >= Integer.MAX_VALUE ) return;
		if ( subtract < 0 )
		{
			if ( subtract == Integer.MIN_VALUE )
			{
				subtract = Integer.MIN_VALUE + 1;
			}
			this.increment( Math.abs(subtract) );
		}
		else
		{
			this.currentTabLevel -= subtract;
			this.checkTabLevel();
		}
	}
	
	public void increment() {
		increment(1);
	}
	
	public void increment( int add ) 
	{
		if ( add >= Integer.MAX_VALUE ) return;

		if ( add < 0 )
		{
			this.decrement( Math.abs(add) );
		}
		else
		{
			boolean new_sgn = (Math.signum(this.numberTabs() + add) >= 0);
	    
			if ( ! new_sgn ) return;
			this.currentTabLevel += add;
			this.checkTabLevel();
		}
	}
	
	public int numberTabs() 
	{
		return this.currentTabLevel;
	}

	public static String computeTabbing(final int numTabs)
	{
		return Tabbing.computeTabbing(numTabs, null);
	}

	public static String computeTabbing(final int numTabs, Integer tabSize)
	{
		if ( tabSize == null ) tabSize = 4;
		String tb = "";
		for (int cnt = 0; cnt < numTabs; cnt++)
		{
			tb = tb + StringUtils.repeat(" ", tabSize);
		}
		return tb;
	}

	private void initialize()
	{
		this.currentTabLevel = 0;
		this.maximumSize     = Tabbing.NO_MAXIMUM_SIZE;
		this.tabSize         = 4;
	}

	private void checkTabLevel()
	{
		if ( this.maximumSize < -1 ) this.maximumSize = -1;
		if ( this.currentTabLevel < 0 ) this.currentTabLevel = 0;
		if ( this.maximumSize == -1 ) return;
		
		if ( this.maximumSize < this.currentTabLevel )
		{
			this.currentTabLevel = this.maximumSize;
		}
	}
}