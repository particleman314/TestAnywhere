package com.testanywhere.core.classes.support.version;

import com.testanywhere.core.classes.comparators.types.VersionComparator;
import com.testanywhere.core.utilities.class_support.Cast;
import com.testanywhere.core.utilities.classes.Range;
import com.testanywhere.core.utilities.exceptions.ObjectCreationException;
import com.testanywhere.core.utilities.logging.*;
import org.apache.commons.lang3.StringUtils;

import java.util.StringTokenizer;

public class Version extends OutputDisplay implements Comparable<Version>
{
	private String majorVersion;
	private String minorVersion;
	private String minorMinorVersion;
	private String buildNumber;
	private String version;

	private Range<Integer> versionRange;
	private Integer versionStyle;
	
	private static final VersionComparator sortingComparator = new VersionComparator(VersionConstants.COMPARISON_FUNCTION.EQUALS);
	
	static
	{
		VersionConstants.getInstance(); // Need to initialize
	}
	
	public Version() 
	{
		super();
		this.initialize();
	}
	
    public Version( final String versionID )
    {
    	this();
    	this.version = versionID;
    	this.getVersionInformation();
    }

	public Version ( final Version other ) throws ObjectCreationException
	{
		this();
		if ( other == null ) throw new ObjectCreationException(Version.class);

		this.versionStyle = other.versionStyle;
		this.version = other.version;
		this.getVersionInformation();
	}

	// For sorting algorithm
	@Override
	public int compareTo( final Version otherV )
	{		
		return Version.sortingComparator.compare(this, otherV);
	}

	@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
	@Override
	public boolean equals( final Object other )
	{
		if (other == null) return false;

		Version otherV = Cast.cast(other);
		return otherV != null && this.getVersion().equals(otherV.getVersion());
	}
	
    @Override
    public String toString() 
    {
    	return this.version;
    }

	public int compare( final Object other ) throws VersionException
	{
		return this.compare(other, "equals");
	}

	@Override
	public boolean isNull() { return false; }

	@Override
	public void buildObjectOutput( int numTabs )
	{
		if ( numTabs < 0 ) numTabs = 0;
		Tabbing tabEnvironment = new Tabbing(numTabs);
		DisplayManager dm = this.getDM();

		String outerSpacer = tabEnvironment.getSpacer();

		dm.append(outerSpacer + "Version Record :", DisplayType.TEXTTYPES.LABEL);
		tabEnvironment.increment();

		String innerSpacer = tabEnvironment.getSpacer();

		if ( this.getMajorVersion() != null )
			dm.append(innerSpacer + "Major Version ID : " + this.getMajorVersion());

		if ( this.getMinorVersion() != null && this.versionStyle >= VersionConstants.MINOR )
			dm.append(innerSpacer + "Minor Version ID : " + this.getMinorVersion());

		if ( this.getPatchVersion() != null && this.versionStyle >= VersionConstants.REVISION )
			dm.append(innerSpacer + "Patch Version ID : " + this.getPatchVersion());

		if ( this.getBuildNumber() != null && this.versionStyle >= VersionConstants.BUILD )
			dm.append(innerSpacer + "Build Number ID  : " + this.getBuildNumber());

		for (VersionConstants.VERSION_COMPONENTS vc : VersionConstants.DESIGNATIONS.keySet()) {
			if ( vc.ordinal() == this.versionStyle ) {
				dm.append(innerSpacer + "Version Display Style : " + vc.name());
				break;
			}
		}
	}

	public int compare( final Object other, final String compareType ) throws VersionException
	{
		if ( other == null ) throw new VersionException();
		
		Version otherV = Cast.cast(other);
		if ( otherV == null ) throw new VersionException();
		
		VersionComparator vc = new VersionComparator(VersionConstants.getComparisonType(compareType));
		return vc.compare(this, otherV);
	}

    public void getVersionInformation() 
    {
        StringTokenizer st = new StringTokenizer(this.version, VersionConstants.DEFAULT_VERSION_DELIMITER);

        if ( st.hasMoreTokens() ) this.majorVersion      = st.nextToken();
        if ( st.hasMoreTokens() ) this.minorVersion      = st.nextToken();
        if ( st.hasMoreTokens() ) this.minorMinorVersion = st.nextToken();
        if ( st.hasMoreTokens() ) this.buildNumber       = st.nextToken();
        
        this.__updateZeroPadding(this.minorVersion,      VersionConstants.VERSION_COMPONENTS.MINOR);
        this.__updateZeroPadding(this.minorMinorVersion, VersionConstants.VERSION_COMPONENTS.REVISION);
        this.__updateZeroPadding(this.buildNumber,       VersionConstants.VERSION_COMPONENTS.SUBREVISION);
    }
	
    public String getMajorVersion() 
    {
    	if (this.majorVersion == null) this.getVersionInformation();
    	return this.majorVersion;
    }

    public void setMajorVersion(final String majorVersion)
    {
		if ( ! TextManager.validString(majorVersion ) ) return;
    	this.majorVersion = majorVersion;
    	this.rebuildVersion();
    }

    public String getMinorVersion() 
    {
    	if (this.minorVersion == null) this.getVersionInformation();
    	return this.minorVersion;
    }

    public void setMinorVersion(final String minorVersion)
    {
		if ( ! TextManager.validString(minorVersion ) ) return;
    	this.minorVersion = minorVersion;
    	this.rebuildVersion();
    }

    public String getPatchVersion() 
    {
    	if ( this.minorMinorVersion == null ) this.getVersionInformation();
    	return this.minorMinorVersion;
    }

    public void setPatchVersion(final String minorMinorVersion)
    {
		if ( ! TextManager.validString(minorMinorVersion ) ) return;
    	this.minorMinorVersion = minorMinorVersion;
    	this.rebuildVersion();
    }

    public String getBuildNumber() 
    {
    	if ( this.buildNumber == null ) this.getVersionInformation();
    	return this.buildNumber;
    }

    public void setBuildNumber(final String buildNumber)
    {
		if ( ! TextManager.validString(buildNumber ) ) return;
    	this.buildNumber = buildNumber;
    	this.rebuildVersion();
    }
	
    public void setVersion(final String version)
    {
		if ( ! TextManager.validString(version) ) return;
    	this.version = version;
    	this.getVersionInformation();
    }
	
    public String getVersion() 
    {
    	if ( this.version == null ) this.getVersionInformation();
    	return this.version;
    }
    
    public String getCanonicalVersion() 
    {
    	if ( this.version == null ) this.getVersionInformation();
		String[] components;
		switch ( this.versionStyle )
    	{
    		case VersionConstants.MAJOR:
    		{
    			components = new String[1];
    			components[0] = this.getMajorVersion();
    			break;
    		}
    		case VersionConstants.MINOR:
    		{
    			components = new String[2];
    			components[0] = this.getMajorVersion();
    			components[1] = this.getMinorVersion();
    			break;
    		}
    		case VersionConstants.REVISION:
    		{
    			components = new String[3];
    			components[0] = this.getMajorVersion();
    			components[1] = this.getMinorVersion();
    			components[2] = this.getPatchVersion();
    			break;
    		}
    		default:
    		{
    			components = new String[4];
    			components[0] = this.getMajorVersion();
    			components[1] = this.getMinorVersion();
    			components[2] = this.getPatchVersion();
    			components[3] = this.getBuildNumber();
    		}
    	}
		return StringUtils.join(components, VersionConstants.DEFAULT_VERSION_DELIMITER );
    }
    
    public void setDelimiter( final String formatDelimiter )
    {
    	if ( formatDelimiter == null ) return;
    	VersionConstants.DEFAULT_VERSION_DELIMITER = formatDelimiter;
    }
    
    public void reset() 
    {
    	this.version = "0.0.0.0";
		this.versionStyle = VersionConstants.MINOR;
    	this.getVersionInformation();
    }
    
    public void setStyle( final int style )
    {
    	if ( this.versionRange.outOfBounds(style) ) return;
    	this.versionStyle = style;
    	this.rebuildVersion();
    }
    
    public void rebuildVersion() 
    {
    	StringBuilder sb = new StringBuilder();
    	if ( this.majorVersion != null ) sb.append(this.majorVersion);
    	else sb.append("0");
    	
    	if ( this.minorVersion != null && this.versionStyle > VersionConstants.MINOR ) sb.append(VersionConstants.DEFAULT_VERSION_DELIMITER).append(this.minorVersion);
    	else 
    	{
    		sb.append(VersionConstants.DEFAULT_VERSION_DELIMITER);
    		int expectedSize = VersionConstants.NUMERICAL_SIZES.get(VersionConstants.DESIGNATIONS.get(VersionConstants.VERSION_COMPONENTS.MINOR));
    		sb.append( StringUtils.leftPad("", expectedSize, "0"));
    	}
    	
    	if ( this.minorMinorVersion != null && this.versionStyle > VersionConstants.REVISION ) sb.append(VersionConstants.DEFAULT_VERSION_DELIMITER).append(this.minorMinorVersion);
    	else 
    	{
    		sb.append(VersionConstants.DEFAULT_VERSION_DELIMITER);
    		int expectedSize = VersionConstants.NUMERICAL_SIZES.get(VersionConstants.DESIGNATIONS.get(VersionConstants.VERSION_COMPONENTS.REVISION));
    		sb.append( StringUtils.leftPad("", expectedSize, "0"));
    	}
    	
    	if ( this.buildNumber != null && this.versionStyle > VersionConstants.BUILD ) sb.append(VersionConstants.DEFAULT_VERSION_DELIMITER).append(this.buildNumber);
    	else 
    	{
    		sb.append(VersionConstants.DEFAULT_VERSION_DELIMITER);
    		int expectedSize = VersionConstants.NUMERICAL_SIZES.get(VersionConstants.DESIGNATIONS.get(VersionConstants.VERSION_COMPONENTS.SUBREVISION));
    		sb.append( StringUtils.leftPad("", expectedSize, "0"));
    	}
    	
    	this.version = sb.toString();
    }

	private String padZeros(String data, final Integer requestSize)
	{
		if ( ! TextManager.validString(data) ) return data;
		
    	if ( data.length() < requestSize ) 
    	{
    		data = StringUtils.leftPad(data, requestSize, '0');
    	}
		
    	return data;
	}

	private void __updateZeroPadding(final String data, final VersionConstants.VERSION_COMPONENTS type)
	{
		if ( ! TextManager.validString(data) ) return;
		int size = data.length();
		String key = VersionConstants.DESIGNATIONS.get(type);
		VersionConstants.NUMERICAL_SIZES.put(key, size);
	}

	@SuppressWarnings("unchecked")
	private void initialize()
	{		
		this.majorVersion = null;
		this.minorVersion = null;
		this.minorMinorVersion = null;
		this.buildNumber = null;
		this.version = "0.0.0.0";

		this.versionStyle = VersionConstants.VERSION_COMPONENTS.MINOR.ordinal();
		this.versionRange = new Range(VersionConstants.VERSION_COMPONENTS.MAJOR.ordinal(), VersionConstants.VERSION_COMPONENTS.SUBREVISION.ordinal());
	}
}