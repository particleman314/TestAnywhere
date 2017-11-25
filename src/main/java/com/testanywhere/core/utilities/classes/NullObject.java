package com.testanywhere.core.utilities.classes;

import com.testanywhere.core.utilities.Constants;
import com.testanywhere.core.utilities.class_support.Cast;
import com.testanywhere.core.utilities.logging.DisplayType;
import com.testanywhere.core.utilities.logging.OutputDisplay;
import com.testanywhere.core.utilities.logging.Tabbing;
import com.testanywhere.core.utilities.logging.TextManager;

@SuppressWarnings("SameReturnValue")
public class NullObject extends OutputDisplay
{
	public NullObject()
	{
		super();
	}

	@Override
	public String toString()
	{
		return TextManager.specializeName(Constants.nullRep);
	}
	
	@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
	@Override
	public boolean equals( final Object other )
	{
		if ( other == null ) return true;
		NullObject otherNO = Cast.cast(other);
		return otherNO != null;
	}

	@Override
	public boolean isNull() { return true; }

	@Override
	public void buildObjectOutput(int numTabs)
	{
		if ( numTabs < 0 ) numTabs = 0;
		Tabbing tabEnvironment = new Tabbing(numTabs);

		String outerSpacer = tabEnvironment.getSpacer();
		this.getDM().append(outerSpacer + "Null Object", DisplayType.TEXTTYPES.LABEL);
	}

	public Object getValue()
	{
		return null;
	}
}
