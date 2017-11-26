package com.testanywhere.core.classes.support.version;

import com.testanywhere.core.classes.comparators.ComparableSort;
import com.testanywhere.core.classes.support.version.VersionList;

import java.util.Collection;
import java.util.List;

@SuppressWarnings("hiding")
public class VersionSort<Version extends Comparable<Version>> extends ComparableSort<Version>
{
	private List<Version> data;

	public VersionSort( final Collection<Version> inputs )
	{
		this.data = (List<Version>) inputs;
	}

	public VersionSort( final VersionList inputList )
	{
		this.data = (List<Version>) inputList.getVersionList();
	}

	// Selection sort.  If made into class, could allow for plug-in-play for sort algorithm
	public void sort()
	{
		if ( this.data == null || this.data.isEmpty() ) return;
		
		int N = this.data.size();
		if ( N <= 1 ) return;
		
		for ( int i = 0; i < N; ++i ) {
			int min = i;
			for ( int j = i+1; j < N; ++j )
			{
				if ( this.less(this.data.get(j), this.data.get(min)) ) { min = j; }
			}

			if ( i != min )
				this.exchange(this.data, i, min);
		}
	}
	
	public List<Version> getData()
	{
		return this.data;
	}
}
