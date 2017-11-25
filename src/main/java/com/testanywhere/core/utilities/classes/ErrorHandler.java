package com.testanywhere.core.utilities.classes;

import java.util.Collection;
import java.util.concurrent.Callable;

public abstract class ErrorHandler implements Callable<String>
{
    private Collection<String> inputs;

    @Override
    public String call() throws Exception
    {
        return null;
    }
}
