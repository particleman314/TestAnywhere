package com.testanywhere.core.machines.actions.parallel;

import com.testanywhere.core.utilities.logging.OutputDisplay;

public abstract class Observer extends OutputDisplay
{
	protected Observer()
	{
		super();
	}
	
	public abstract <T> void notify( Event<T> e );
}
