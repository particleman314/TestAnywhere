package com.testanywhere.core.machines.actions.parallel;

import com.testanywhere.core.classes.class_support.ParameterizedObject;
import com.testanywhere.core.utilities.class_support.Cast;
import com.testanywhere.core.utilities.logging.DisplayManager;
import com.testanywhere.core.utilities.logging.DisplayType;
import com.testanywhere.core.utilities.logging.OutputDisplay;
import com.testanywhere.core.utilities.logging.Tabbing;

public class Event<T> extends OutputDisplay
{
	private String subject;
	private Task<T> actionToDo;
	
	public Event()
	{
		super();
		this.subject = null;
		this.actionToDo = null;
	}

	@Override
	public void buildObjectOutput(int numTabs)
	{
		if ( numTabs < 0 ) numTabs = 0;
		Tabbing tabEnvironment = new Tabbing(numTabs);
		DisplayManager dm = this.getDM();

		String outerSpacer = tabEnvironment.getSpacer();
		dm.append(outerSpacer + "Event :", DisplayType.TEXTTYPES.LABEL);
		
		tabEnvironment.increment();
		String innerSpacer = tabEnvironment.getSpacer();
		
		dm.append(innerSpacer + "Subject : " + this.getSubject());
		if ( this.actionToDo != null )
		{
			this.actionToDo.buildObjectOutput(tabEnvironment.numberTabs() + 1);
			dm.addFormatLines(this.actionToDo.getDM().getLines());
		}
	}

	@Override
	public boolean isNull()
	{
		return false;
	}

	public String getSubject()
	{
		return this.subject;
	}
	
	public void setTask(Object object) 
	{
		this.actionToDo = Cast.cast(object);
	}

	public void setSubject( String subject )
	{
		this.subject = subject;
	}
	
	public T execute(ParameterizedObject po) throws Exception
	{
		if ( this.actionToDo != null )
		{
			this.actionToDo.setInputData(po);
			return this.actionToDo.call();
		}
		return null;
	}
}