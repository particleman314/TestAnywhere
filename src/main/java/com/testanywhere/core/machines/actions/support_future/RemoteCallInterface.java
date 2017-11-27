package com.nimsoft.actions.support_future;

import com.testanywhere.core.machines.actions.Action;
import com.testanywhere.core.machines.exceptions.RemoteCallException;

import java.util.concurrent.Future;

public interface RemoteCallInterface<T> 
{
    // for synchronous
    T executeSynchronous(final Action action) throws RemoteCallException;

    // for asynchronous
    Future<T> executeAsynchronous(final Action action);
}