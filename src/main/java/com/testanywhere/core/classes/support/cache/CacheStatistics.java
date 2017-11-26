package com.testanywhere.core.classes.support.cache;

import com.testanywhere.core.utilities.logging.DisplayManager;
import com.testanywhere.core.utilities.logging.DisplayType;
import com.testanywhere.core.utilities.logging.OutputDisplay;
import com.testanywhere.core.utilities.logging.Tabbing;

public class CacheStatistics extends OutputDisplay
{
    private int cacheHits;
    private int cacheMisses;

    public CacheStatistics()
    {
        super();
        this.initialize();
    }

    @Override
    public boolean isNull() { return false; }

    @Override
    public void buildObjectOutput(int numTabs )
    {
        if ( numTabs < 0 ) numTabs = 0;
        Tabbing tabEnvironment = new Tabbing(numTabs);
        DisplayManager dm = this.getDM();

        String outerSpacer = tabEnvironment.getSpacer();
        dm.append(outerSpacer + "Cache Stats : ", DisplayType.TEXTTYPES.LABEL);

        tabEnvironment.increment();
        String innerSpacer = tabEnvironment.getSpacer();
        dm.append(innerSpacer + "Cache Hits : " + this.getCacheHits());
        dm.append(innerSpacer + "Cache Misses : " + this.getCacheMisses());
    }

    public void incrementHit()
    {
        this.cacheHits++;
    }

    public void incrementMiss()
    {
        this.cacheMisses++;
    }

    public int getCacheHits()
    {
        return this.cacheHits;
    }

    public int getCacheMisses()
    {
        return this.cacheMisses;
    }

    public void reset()
    {
        this.initialize();
    }

    private void initialize()
    {
        this.cacheHits = 0;
        this.cacheMisses = 0;
    }
}
