package com.testanywhere.core.classes.test.identification;

import com.testanywhere.core.utilities.logging.OutputDisplay;

public class TestDesignation extends OutputDisplay {

    private Class<?> testObjectType;

    public TestDesignation( Object obj ) {
        this.testObjectType = null;
        if ( obj != null ) {
            this.testObjectType = obj.getClass();
        }
    }

    private TestDesignation() {}

    public Class<?> getClassType() { return this.testObjectType; }

    @Override
    public void buildObjectOutput(int numTabs) {

    }

    @Override
    public boolean isNull() {
        return false;
    }
}
