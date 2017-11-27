package com.testanywhere.core.machines.connections;

import com.testanywhere.core.utilities.logging.*;

public class ConnectionState extends OutputDisplay
{
    private boolean valid;
    private boolean local;
    private boolean busy;
    private ConnectionConstants.CONNECTION_STATE access;

    public ConnectionState()
    {
        super();
        this.initialize();
    }

    @Override
    public boolean isNull()
    {
        return false;
    }

    @Override
    public void buildObjectOutput(int numTabs)
    {
        if ( numTabs < 0 ) numTabs = 0;
        Tabbing tabEnvironment = new Tabbing(numTabs);
        DisplayManager dm = this.getDM();

        String outerSpacer = tabEnvironment.getSpacer();
        dm.append(outerSpacer + "Connection State : ", DisplayType.TEXTTYPES.LABEL);

        tabEnvironment.increment();
        String innerSpacer = tabEnvironment.getSpacer();
        dm.append(innerSpacer + "Valid : " + TextManager.StringRepOfBool(this.isValid(), "yn"));
        dm.append(innerSpacer + "Local : " + TextManager.StringRepOfBool(this.isLocal(), "yn"));
        dm.append(innerSpacer + "Busy : " + TextManager.StringRepOfBool(this.isBusy(), "yn"));
        dm.append(innerSpacer + "Access : " + this.getAccess());
    }

    public boolean isValid()
    {
        return this.valid;
    }

    public void setValid( boolean validity )
    {
        this.valid = validity;
    }

    public boolean isLocal()
    {
        return this.local;
    }

    public void setLocal( boolean locality )
    {
        this.local = locality;
    }

    public boolean isBusy()
    {
        return this.busy;
    }

    public void setBusy( boolean busy )
    {
        this.busy = busy;
    }

    public ConnectionConstants.CONNECTION_STATE getAccess()
    {
        return this.access;
    }

    public void setAccess(ConnectionConstants.CONNECTION_STATE accessType )
    {
        this.access = accessType;
    }

    private void initialize()
    {
        this.valid  = false;
        this.local  = true;
        this.busy   = false;
        this.access = ConnectionConstants.CONNECTION_STATE.DISCONNECTED;
    }
}
