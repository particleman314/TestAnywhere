package com.testanywhere.core.classes.support.version;

import com.testanywhere.core.utilities.class_support.Cast;
import com.testanywhere.core.utilities.class_support.functional_support.ListFunctions;
import com.testanywhere.core.classes.comparators.ComparatorConstants;
import com.testanywhere.core.classes.comparators.types.VersionComparator;
import com.testanywhere.core.utilities.logging.*;
import com.testanywhere.core.classes.support.version.Version;
import com.testanywhere.core.classes.support.version.VersionConstants;
import com.testanywhere.core.classes.support.version.VersionSort;

import java.util.*;

public class VersionList extends OutputDisplay
{
    private Collection<Version> versions;
		
	public VersionList() 
	{
		super();
		this.versions = new ArrayList<>();
	}
	
    public VersionList( final Collection<Version> versions )
    {
    	this();
		this.versions.addAll(versions);
    }

	@Override
	public void buildObjectOutput( int numTabs )
	{
		if ( numTabs < 0 ) numTabs = 0;
		Tabbing tabEnvironment = new Tabbing(numTabs);
		DisplayManager dm = this.getDM();

		String outerSpacer = tabEnvironment.getSpacer();
		dm.append(outerSpacer + "Version List :", DisplayType.TEXTTYPES.LABEL);

		tabEnvironment.increment();
		dm.addFormatLines(ListFunctions.asNumberedList(this.getVersionList(), tabEnvironment));
	}

	@Override
	public boolean isNull() { return versions == null || versions.isEmpty(); }

	@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
	@Override
	public boolean equals(final Object other)
	{
		if ( other == null ) return false;

		VersionList otherVL = Cast.cast(other);
		if ( otherVL == null ) return false;

		if ( this.size() != otherVL.size() ) return false;
		for ( int loop = 0; loop < this.size(); ++loop )
		{
			if ( ! this.get(loop).equals(otherVL.get(loop)) )
			{
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString()
	{
		return this.versions.toString();
	}

	public void add( final Version v )
	{
		this.getVersionList().add(v);
	}
	
	public int find( final String vs )
	{
		int result = VersionConstants.NOT_FOUND;
		if ( ! TextManager.validString(vs) ) 
		{
			this.error("Attempted to look for version by string but null was input");
			return result;
		}

		Iterator<Version> iter = this.getVersionList().iterator();
		int count = 0;
		while ( iter.hasNext() )
		{
			Version v = iter.next();
			if ( v.getVersion().equals(vs) ) 
			{
				result = count;
				break;
			}
			++count;
		}
		
		return result;
	}
	
	public int find( final Version v )
	{
		if ( v == null )
		{
			this.error("Attempted to look for version but null was input");
			return VersionConstants.NOT_FOUND;
		}
		return this.find(v.getVersion());
	}
	
	public Version getFirst()
	{
		if ( this.isEmpty() ) return null;
		ArrayList<Version> arv = Cast.safeCast(this.getVersionList(), ArrayList.class);
		return arv.get(0);
	}
	
	public Version getLast()
	{
		if ( this.isEmpty() ) return null;
		ArrayList<Version> arv = Cast.safeCast(this.getVersionList(), ArrayList.class);
		return arv.get(arv.size() - 1);
	}
	
	public Version get(final int idx)
	{
		if ( this.isEmpty() ) return null;
		if ( idx < 0 || idx > this.size() - 1 ) return null;
		ArrayList<Version> arv = Cast.safeCast(this.getVersionList(), ArrayList.class);
		return arv.get(idx);
	}
	
	public void remove( final String vstr )
	{
		int vloc = this.find(vstr);
		ArrayList<Version> arv = Cast.safeCast(this.getVersionList(), ArrayList.class);
		if ( vloc != VersionConstants.NOT_FOUND )
		{
			arv.set(vloc, null);
		}
		while( arv.remove(null) );
	}
	
	public void remove( final Version v )
	{
		int vloc = this.find(v);
		ArrayList<Version> arv = Cast.safeCast(this.getVersionList(), ArrayList.class);
		if ( vloc != VersionConstants.NOT_FOUND )
		{
			arv.set(vloc, null);
		}
		while( arv.remove(null) );
	}
	
	public int size() 
	{
		return this.getVersionList().size();
	}
	
	public void sort() 
	{
		new VersionSort(this.getVersionList()).sort();
	}
	
	public boolean isEmpty() 
	{
		return this.getVersionList().isEmpty();
	}
	
	public Collection<Version> getVersionList()
	{
		return this.versions;
	}
	
	public int compare( final Object other, final String compareType )
	{
		if ( other == null ) return VersionConstants.ILLEGAL;
		
		VersionList otherV = Cast.cast(other);
		if ( otherV == null ) return VersionConstants.ILLEGAL;
		
		VersionComparator vc = new VersionComparator(compareType, this.getLog());
		
		if ( this.getVersionList().size() != otherV.getVersionList().size() ) return VersionConstants.ILLEGAL;
		
		VersionSort<Version> vs = new VersionSort<>(this.getVersionList());
		VersionSort<Version> vso = new VersionSort<>(otherV.getVersionList());
		
		for ( int i = 0; i < this.size(); ++i )
		{
			int compareResult = vc.compare(vs.getData().get(i), vso.getData().get(i));
			if ( compareResult != ComparatorConstants.EQUALS )
			{
				return compareResult;
			}
		}
		
		return ComparatorConstants.EQUALS;
	}

    public void clear()
    {
    	this.getVersionList().clear();
    }
}