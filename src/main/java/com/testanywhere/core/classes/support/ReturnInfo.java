package com.testanywhere.core.classes.support;

import com.testanywhere.core.classes.class_support.CompartmentObject;
import com.testanywhere.core.utilities.classes.Pair;
import com.testanywhere.core.utilities.exceptions.ObjectCreationException;
import com.testanywhere.core.utilities.logging.*;
import com.testanywhere.core.classes.managers.ErrorMsgDBManager;
import com.testanywhere.core.classes.managers.Registrar;
import com.testanywhere.core.classes.utilities.ClassUtils;
import com.testanywhere.core.classes.utilities.ErrorUtils;

public class ReturnInfo extends OutputDisplay
{
	public static final ReturnInfo NO_RESPONSE_NO_ERROR = new ReturnInfo("", ErrorUtils.SUCCESS_ID);
	public static final ReturnInfo NO_RESPONSE_ERROR = new ReturnInfo("", ErrorUtils.GENERIC_FAIL_ID);

	private CompartmentObject<?> returnOutput;
	private int returnCode;
	
	public ReturnInfo()
	{
		super();
		this.returnOutput = null;
		this.returnCode = ErrorUtils.GENERIC_FAIL_ID;
	}
	
	public ReturnInfo(final String text, final int error)
	{
		this();
		this.returnOutput = new CompartmentObject<>(text);
		this.returnCode = error;
	}

	public ReturnInfo( final ReturnInfo ri )
	{
		this();
		if ( ri == null ) throw new ObjectCreationException(ReturnInfo.class);

		try {
			this.returnOutput = ri.returnOutput.copy();
		} catch (CloneNotSupportedException e) {
			throw new ObjectCreationException(ReturnInfo.class);
		}
		this.returnCode = ri.getReturnCode();
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		if ( ! this.getReturn().isEmpty() )
			sb.append("RI:" + this.getReturn());
		else
			sb.append("RI:");
		return sb.toString();
	}

	@Override
	public boolean isNull()
	{
		return false;
	}

	@Override
	public void buildObjectOutput( int numTabs )
	{
		if ( numTabs < 0 ) numTabs = 0;
		Tabbing tabEnvironment = new Tabbing(numTabs);
		DisplayManager dm = this.getDM();

		String outerSpacer = tabEnvironment.getSpacer();
		dm.append(outerSpacer + "Return Information :", DisplayType.TEXTTYPES.LABEL);

		tabEnvironment.increment();
		String innerSpacer = tabEnvironment.getSpacer();
		dm.append(innerSpacer + "Last Output      : " + this.getOutput());
		dm.append(innerSpacer + "Last Error       : " + this.getReturnCode());
	}

	public String getOutput()
	{
		return ClassUtils.convertStringOutput(this.returnOutput);
	}

	public int getReturnCode()
	{
		return this.returnCode;
	}

	public void setOutput( final String text )
	{
		this.returnOutput = new CompartmentObject<>(text);
	}

	public void setReturnCode( final int errorVal )
	{
		this.returnCode = errorVal;
	}

	public Pair<String, Integer> getReturn()
	{
		return new Pair<>(this.getOutput(), this.getReturnCode());
	}

	public String getReturnCodeAsMsg()
	{
		ErrorMsgDBManager eDBM = Registrar.getDefaultManager("ErrorMsgDBManager");
		if ( eDBM == null ) return ErrorMsgDBManager.NO_ENTRY_MSG;

		return eDBM.getErrorMsgFromID(this.getReturnCode());
	}
}