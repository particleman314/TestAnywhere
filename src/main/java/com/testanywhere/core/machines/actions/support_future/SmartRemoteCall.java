package com.testanywhere.core.machines.actions.support_future;

import com.testanywhere.core.machines.actions.Action;
import com.testanywhere.core.machines.exceptions.RemoteCallException;

import java.util.concurrent.Executors;

public class SmartRemoteCall extends AbstractRemoteCall<String>
{
    public SmartRemoteCall() 
    {
        super(Executors.newFixedThreadPool(5));
    }

    public String executeSynchronous(Action action) throws RemoteCallException
    {
		return null;
    }
}