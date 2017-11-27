package com.testanywhere.core.machines.actions;

import com.testanywhere.core.classes.class_support.CompartmentObject;
import com.testanywhere.core.classes.support.ReturnInfo;
import com.testanywhere.core.classes.utilities.ClassUtils;
import com.testanywhere.core.classes.utilities.ErrorUtils;
import com.testanywhere.core.utilities.class_support.BaseClass;
import com.testanywhere.core.utilities.class_support.Cast;
import com.testanywhere.core.utilities.logging.*;

public abstract class Action extends OutputDisplay implements ActionInterface
{
	public static final ReturnInfo NULL_RESULT = new ReturnInfo();

	private ReturnInfo lastResult;
	private boolean executed;
	
	static
	{
		ActionConstants.getInstance();
	}
	
	protected Action()
	{
		super();
		
		this.lastResult = null;
		this.executed = false;
	}
	
	protected Action( Action action ) throws CloneNotSupportedException
	{
		this();
		this.assignResults(action);
	}

	public void assignResults( Action action ) throws CloneNotSupportedException
	{
		if ( action != null )
		{
			this.executed = action.isExecuted();
			this.lastResult = action.getLastResult().copy();
		}
	}

	@Override
	public boolean equals ( Object other ) {
		if (other == null) return false;

		Action a = Cast.cast(other);
		return a != null && this.getLastResult() == a.getLastResult() && this.getLastErrorValue() == a.getLastErrorValue() && this.getLastOutput().equals(a.getLastOutput());

	}

	@Override
	public boolean isNull() { return false; }

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("R:" + this.getLastResult() + TextManager.STR_OUTPUTSEPARATOR);
		sb.append("E:" + TextManager.StringRepOfBool(this.isExecuted(), "yn"));
		return sb.toString();
	}
	
	@Override
	public void buildObjectOutput( int numTabs )
	{
		if ( numTabs < 0 ) numTabs = 0;
		Tabbing tabEnvironment = new Tabbing(numTabs);
		DisplayManager dm = this.getDM();

		String outerSpacer = tabEnvironment.getSpacer();
		dm.append(outerSpacer + "Action :", DisplayType.TEXTTYPES.LABEL);
		
		tabEnvironment.increment();
		String innerSpacer = tabEnvironment.getSpacer();
		dm.append(innerSpacer + "Last Error Value : " + BaseClass.checkIsNull(this.getLastErrorValue()));
		dm.append(innerSpacer + "Last Output : " + BaseClass.checkIsNull(this.getLastOutput()));
		dm.append(innerSpacer + "Was executed : " + TextManager.StringRepOfBool(this.isExecuted(), "yn"));
	}

	public String getLastOutput()
	{
		return this.getLastResult().getOutput();
	}

	public ReturnInfo getLastResult()
	{
		return this.lastResult;
	}

	public int getLastErrorValue()
	{
		return this.getLastResult().getReturnCode();
	}

	// Need to investigate this to determine if this is a "feasible" operation
	public void combineResults( Action action )
	{
		if ( action != null )
		{
			StringBuilder sb = new StringBuilder();

			this.executed = this.isExecuted() || action.isExecuted();

			sb.append(this.getLastOutput() + TextManager.EOL + "===+++===" + TextManager.EOL);
			sb.append(action.getLastOutput());

			this.setActionError(this.getLastErrorValue() & action.getLastErrorValue(), sb.toString());
			sb.setLength(0);
		}
	}

	public void setSuccess()
	{
		this.setActionError(ErrorUtils.SUCCESS_ID, null );
	}

	public void setFailure( int error )
	{
		this.setActionError(error, ClassUtils.convertStringOutput("Error encountered with action"));
	}

	protected void setActionError(int error, String errorString)
	{
		this.lastResult.setOutput(errorString);
		this.lastResult.setReturnCode(error);
		this.executed = true;
	}

	public void setFailure(int error, String errorString)
	{
		this.setActionError(error, ClassUtils.convertStringOutput(errorString));
	}

	protected void setFailure(String errorString)
	{
		this.setActionError(ErrorUtils.GENERIC_FAIL_ID, ClassUtils.convertStringOutput(errorString));
	}

	public void setLastOutput(String output) { this.getLastResult().setOutput(ClassUtils.convertStringOutput(output)); }

	public void setLastOutput(CompartmentObject<?> output) { this.getLastResult().setOutput(ClassUtils.convertStringOutput(output)); }

	protected boolean isExecuted()
	{
		return this.executed;
	}
}
