package com.testanywhere.core.utilities.serialization;

import java.util.ArrayList;
import java.util.Collection;

public class SerializeHelper
{
    private Collection<String> skipFields;

    public SerializeHelper()
    {
        super();
        this.initialize();
    }

    public void addSkipField( String skipField )
    {
        if ( this.skipFields.contains(skipField) ) return;
        this.skipFields.add(skipField);
    }

    public void removeSkipField( String skipField )
    {
        if ( this.skipFields.contains(skipField) ) this.skipFields.remove(skipField);
    }

    public Collection<String> getSkipFields() { return this.skipFields; }

    private void initialize()
    {
        this.skipFields = new ArrayList<>();
    }
}
