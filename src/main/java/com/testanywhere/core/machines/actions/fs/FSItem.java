package com.testanywhere.core.machines.actions.fs;

import com.testanywhere.core.machines.actions.ActionConstants;
import com.testanywhere.core.utilities.class_support.BaseClass;
import com.testanywhere.core.utilities.logging.*;
import com.testanywhere.core.machines.utilities.ActionUtils;
import com.testanywhere.core.utilities.classes.Pair;
import com.testanywhere.core.classes.support.Tristate;
import com.testanywhere.core.machines.exceptions.FSException;
import com.testanywhere.core.machines.utilities.MachineTypeUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class FSItem extends OutputDisplay implements Cloneable
{
	private Pair<String, URL> unit;
	private Tristate unitConnectivity;
	private ActionConstants.FSTYPES fstype;
	
	public FSItem()
	{
		super();
		this.initialize();
	}

	public FSItem( Object src, ActionConstants.FSTYPES type ) throws FSException
	{
		this();
		this.internalizeInputs(src, type);
		this.unitConnectivity.and(false);
	}
	
	public FSItem( Object src, ActionConstants.FSTYPES type, boolean connectType ) throws FSException
	{
		this();
		this.internalizeInputs(src, type);
		this.unitConnectivity.and(connectType);
	}
	
	public FSItem( Object src, ActionConstants.FSTYPES type, boolean connectType, boolean checkSrc ) throws FSException
	{
		this();
		this.internalizeInputs(src, type, checkSrc);
		this.unitConnectivity.and(connectType);
	}
	
	public FSItem( FSItem item )
	{
		this();
		
		if ( item != null )
		{
			this.unit = new Pair<>(item.getSourceInput(), item.getSource());
			this.unitConnectivity = item.unitConnectivity;
		}
	}

	@Override
	public void buildObjectOutput(int numTabs)
	{
		if ( numTabs < 0 ) numTabs = 0;
		Tabbing tabEnvironment = new Tabbing(numTabs);
		DisplayManager dm = this.getDM();

		String outerSpacer = tabEnvironment.getSpacer();
		dm.append(outerSpacer + "FileSystem Element : ", DisplayType.TEXTTYPES.LABEL);

		tabEnvironment.increment();
		String innerSpacer = tabEnvironment.getSpacer();
		if ( ! this.unit.isEmpty() )
			dm.append(innerSpacer + "Source [ String | URL ] : [ " + BaseClass.checkIsNull(this.unit.first()) + " | " + BaseClass.checkIsNull(this.unit.second()) + " ]");
		else
			dm.append(innerSpacer + "Source [ String | URL ] : [ ]");
		dm.append(innerSpacer + "Source Connectivity : " + TextManager.StringRepOfBool(this.getConnectivity(), "tf"));
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		if ( this.unit != null ) {
			if (this.unit.isEmpty())
				sb.append("SRC:[]" + TextManager.STR_OUTPUTSEPARATOR);
			else
				sb.append("SRC:[" + BaseClass.checkIsNull(this.unit.first()) + " <-> " + BaseClass.checkIsNull(this.unit.second()) + "]" + TextManager.STR_OUTPUTSEPARATOR);
			if (this.getUnitConnectivity() != null)
				sb.append("CON:" + TextManager.StringRepOfBool(this.unitConnectivity.getBoolean(), "tf"));
		}
		return sb.toString();
	}

	@Override
	public boolean isNull()
	{
		return false;
	}

	public boolean checkInternalFileState()
	{
		return !(this.unit == null || this.unit.isEmpty());
	}

	public ActionConstants.FSTYPES getFSType()
	{
		return this.fstype;
	}
	
	public String getSourceAsStr()
	{
		if ( this.unit == null || this.unit.isEmpty() ) return null;
		String subPath = this.unit.second().getPath().substring(1);
		return MachineTypeUtils.toLocalMachinePathStyle(subPath, this.getConnectivity());
	}
	
	public String getSourceInput()
	{
		if ( this.unit == null || this.unit.isEmpty() ) return null;
		return this.unit.first();
	}
	
	public URL getSource()
	{
		if ( this.unit == null || this.unit.isEmpty() ) return null;
		return this.unit.second();
	}

	public Tristate getUnitConnectivity()
	{
		return this.unitConnectivity;
	}

	public void setUnitConnectivity( Tristate t )
	{
		this.unitConnectivity = t;
	}

	public boolean getConnectivity()
	{
		try 
		{
			this.unitConnectivity = new Tristate(ActionUtils.setSourceConnectivity( this.unit.second() ));
		} 
		catch (MalformedURLException e) 
		{
			this.unitConnectivity = new Tristate(ActionConstants.LOCAL);
		}
		return this.unitConnectivity.getBoolean();
	}
	
	public void setConnectivity( boolean connectType )
	{
		this.unitConnectivity = new Tristate(connectType);
	}
	
	public void setSource( File fileInput )
	{
		this.setSource(fileInput.getAbsolutePath(), false);
	}
	
	public void setSource( String fileInput, boolean local )
	{
		try 
		{
			this.setSource(ActionUtils.convertToUrl(fileInput, local));
			this.unit.setL(fileInput);
		} 
		catch (MalformedURLException e) 
		{
			this.error("Unable to set the source...");
		}
	}
	
	public void setSource( URL fileInput )
	{
		this.unit.setR(fileInput);
	}
	
	protected void internalizeInputs( Object src, ActionConstants.FSTYPES type ) throws FSException
	{
		this.internalizeInputs(src, type, false);
	}
	
	protected void internalizeInputs(Object src, ActionConstants.FSTYPES type, boolean checkSrc ) throws FSException
	{
		File fsrc = null;
		
		if ( src instanceof String )
		{
			this.__internalString((String) src);
			fsrc = new File((String) src);
		}
		
		else if ( src instanceof File )
		{
			this.__internalFile((File) src);
			fsrc = (File) src;
		}
		
		else if ( src instanceof URL )
		{
			this.__internalURL((URL) src);
			fsrc = new File(((URL) src).getPath());
		}
		
		this.fstype = type;

		if ( checkSrc && fsrc != null )
		{
			// Validate the source location (which is local)
			if ( ! fsrc.exists() || ! fsrc.canRead() ) this.__resetWithException();

			if ( ActionConstants.FSTYPES.DIRECTORY.equals(type) )
				if ( ! fsrc.isDirectory() ) this.__resetWithException();
			else
				if ( ! fsrc.isFile() ) this.__resetWithException();
		}
	}

	private void __resetWithException() throws FSException
	{
		this.__nullify();
		throw new FSException();
	}

	private void __nullify()
	{
		this.unit.set(null, null);
	}
	
	private void __internalString( String src ) throws FSException
	{
		if ( ! TextManager.validString(src) ) throw new FSException();
		
		this.unit.setL(src);
		try
		{
			this.unit.setR(new File(src).toURI().toURL());
		}
		catch (MalformedURLException e)
		{
			this.__nullify();
		}
	}
	
	private void __internalFile( File src ) throws FSException
	{
		if ( src == null ) throw new FSException();

		try
		{
			this.unit.setL(src.getCanonicalPath());
			this.unit.setR(src.toURI().toURL());
		}
		catch (IOException e)
		{
			this.__nullify();
		}
	}

	private void __internalURL( URL src ) throws FSException
	{
		if ( src == null ) throw new FSException();

		this.unit.setL(src.getPath());
		this.unit.setR(src);
	}

	private void initialize()
	{
		this.unit = new Pair<>();
		this.unitConnectivity = new Tristate();
	}
}
