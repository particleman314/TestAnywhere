package com.testanywhere.core.machines.actions.support_future;

import com.testanywhere.core.machines.actions.Action;
import com.testanywhere.core.machines.exceptions.RemoteCallException;
import com.testanywhere.core.machines.actions.support_future.RemoteCallInterface;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public abstract class AbstractRemoteCall<T> implements RemoteCallInterface<T>
{
    private final ExecutorService executor;

    public AbstractRemoteCall(ExecutorService executor) 
    {
        this.executor = executor;
    }

    // note, final so it cannot be overridden in a sub class.
    // note, action is final so it can be passed to the callable.
    public final Future<T> executeAsynchronous(final Action action)
    {
        Callable<T> task = new Callable<T>() {

            @Override
            public T call() throws RemoteCallException
            {
                return executeSynchronous(action);
            }

        };

        return executor.submit(task);
    }
}