package com.testanywhere.core.machines.actions.parallel;

import com.testanywhere.core.classes.class_support.ParameterizedObject;
import com.testanywhere.core.utilities.logging.OutputDisplay;

import java.util.concurrent.Callable;

public abstract class Task<T> extends OutputDisplay implements Callable<T>
{
	private ParameterizedObject inputs;
	
    protected Task()
    {
    	super();
    }

    protected Task( ParameterizedObject po )
    {
    	this();
    	this.inputs = po;
    }
    
    @Override
    public T call() throws Exception { return null; }
    
    public void setInputData( ParameterizedObject po )
    {
    	this.inputs = po;
    }
    
    public ParameterizedObject getInputData()
    {
    	return this.inputs;
    }
}
