package com.testanywhere.core.machines.actions.parallel;

import com.testanywhere.core.utilities.logging.*;

import java.util.Map;
import java.util.TreeMap;

public class Dispatcher extends OutputDisplay
{
	Map<String, Notifier> eventDispatch;
	
	public Dispatcher()
	{
		super();
		this.setLog(this);
		this.eventDispatch = new TreeMap<>();
	}

	@Override
	public void buildObjectOutput(int numTabs)
	{
		if ( this.eventDispatch.size() < 1 ) return;
		
		if ( numTabs < 0 ) numTabs = 0;
		Tabbing tabEnvironment = new Tabbing(numTabs);
		DisplayManager dm = this.getDM();

		String outerSpacer = tabEnvironment.getSpacer();
		dm.append(outerSpacer + "Dispatcher :", DisplayType.TEXTTYPES.LABEL );
		
		tabEnvironment.increment();
		String innerSpacer = tabEnvironment.getSpacer();

		int requestedNumTabs = tabEnvironment.numberTabs() + 1;

		for ( String subj : this.eventDispatch.keySet() )
		{
			Notifier n = this.eventDispatch.get(subj);
			if ( n.getObservers().size() < 1 ) continue;
			dm.append(innerSpacer + "Subject : " + TextManager.specializeName(subj));
			n.buildObjectOutput(requestedNumTabs);
			dm.addFormatLines(n.getDM().getLines());
		}
	}

	@Override
	public boolean isNull()
	{
		return false;
	}

	public <T> void dispatch(Event<T> event)
	{
		if ( event == null ) return;
		Notifier n = this.getDispatcher().get(event.getSubject());
		
		if ( n != null )
		{
			n.notifyObservers(event);
		}
	}
	
	public void addDispatcher( String subject )
	{
		if ( this.eventDispatch.containsKey(subject) ) return;
		this.eventDispatch.put(subject, new Notifier());
	}
	
	public void subscribe( String subject, Observer o )
	{
		if ( ! TextManager.validString(subject) || o == null ) return;
		this.addDispatcher(subject);
		this.eventDispatch.get(subject).getObservers().add(o);
	}
	
	private Map<String, Notifier> getDispatcher()
	{
		return this.eventDispatch;
	}
}
