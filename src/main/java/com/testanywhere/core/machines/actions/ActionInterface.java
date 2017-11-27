package com.testanywhere.core.machines.actions;

import com.testanywhere.core.classes.support.ReturnInfo;
import com.testanywhere.core.machines.connections.ConnectionClient;

public interface ActionInterface
{
	ReturnInfo execute(ConnectionClient cc);
	void assignResults(Action action) throws CloneNotSupportedException;
}
