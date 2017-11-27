package com.testanywhere.core.machines.actions.parallel;

import com.testanywhere.core.utilities.logging.DisplayManager;
import com.testanywhere.core.utilities.logging.DisplayType;
import com.testanywhere.core.utilities.logging.OutputDisplay;
import com.testanywhere.core.utilities.logging.Tabbing;

import java.util.LinkedList;
import java.util.List;

public class Notifier extends OutputDisplay
{
	private List<Observer> observerCollection;
	
	public Notifier()
	{
		super();
		this.observerCollection = new LinkedList<>();
	}

	@Override
	public void buildObjectOutput(int numTabs)
	{
		if ( numTabs < 0 ) numTabs = 0;
		Tabbing tabEnvironment = new Tabbing(numTabs);
		DisplayManager dm = this.getDM();

		String outerSpacer = tabEnvironment.getSpacer();
		dm.append(outerSpacer + "Subscribers :", DisplayType.TEXTTYPES.LABEL );
		
		tabEnvironment.increment();
		String innerSpacer = tabEnvironment.getSpacer();

		int requestedNumTabs = tabEnvironment.numberTabs() + 1;
		for ( Observer o : this.getObservers() )
		{
			if ( o == null ) continue;
			dm.append(innerSpacer + "Observer : ", DisplayType.TEXTTYPES.LABEL);
			o.buildObjectOutput(requestedNumTabs);
			dm.addFormatLines(o.getDM().getLines());
		}
	}

	@Override
	public boolean isNull()
	{
		return false;
	}

	public void clear()
	{
		this.getObservers().clear();
	}

	public void registerObserver(Observer observer)
	{
		List<Observer> obs = this.getObservers();
		if ( observer != null && obs.contains(observer) )
			obs.add(observer);
	}
	
	public <T> void notifyObservers(Event<T> e)
	{
		for ( Observer o : this.getObservers() )
		{
			o.notify(e);
		}
	}
	
	protected List<Observer> getObservers()
	{
		return this.observerCollection;
	}
}
