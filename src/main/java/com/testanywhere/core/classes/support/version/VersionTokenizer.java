package com.testanywhere.core.classes.support.version;

public class VersionTokenizer 
{
    private final String versionString;
    private final int length;

    private int       position;
    private int       number;
    private String    suffix;

    private String    token;

	@SuppressWarnings("unused")
	private boolean   hasValue;

    public VersionTokenizer(final String versionString)
    {
        this.versionString = versionString;
        this.length = versionString.length();
        this.position = 0;
        this.suffix = "";
        this.number = 0;
        this.hasValue = false;
        this.token = ".";
    }

    public int getNumber()
    {
        return this.number;
    }

    public String getSuffix()
    {
        return this.suffix;
    }

    public String getVersionToken() { return this.token; }

    public void setVersionToken( String token ) { this.token = token; }
    public boolean MoveNext()
    {
    	this.number = 0;
    	this.suffix = "";
    	this.hasValue = false;

        // No more characters
    	
        if (this.position >= this.length)
            return false;

        this.hasValue = true;

        while (this.position < this.length)
        {
            char c = this.versionString.charAt(this.position);
            if (c < '0' || c > '9') break;
            this.number = this.number * 10 + (c - '0');
            this.position++;
        }

        int suffixStart = this.position;

        while (this.position < this.length) 
        {
            char c = this.versionString.charAt(this.position);
            if ( c == this.token.charAt(0) ) break;
            this.position++;
        }

        this.suffix = this.versionString.substring(suffixStart, this.position);

        if (this.position < this.length) this.position++;

        return true;
    }
}