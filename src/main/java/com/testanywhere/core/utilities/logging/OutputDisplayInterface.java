package com.testanywhere.core.utilities.logging;

import java.io.OutputStream;

public interface OutputDisplayInterface 
{
	public void prettyPrint();	
	public void prettyPrint(int numTabs);
	public void prettyPrint(OutputStream os, int numTabs);
	public void prettyPrint(MultiOutputStream mos, int numTabs);
	public void buildObjectOutput(int numTabs);
	//public String getPrettyPrint(int numTabs);
	public void log(final String level, final String message);
}
