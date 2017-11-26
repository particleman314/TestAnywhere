package com.testanywhere.core.classes.support.cache.strategies;

import com.testanywhere.core.classes.support.cache.Cache;
import com.testanywhere.core.classes.support.cache.CacheConstants;
import com.testanywhere.core.classes.support.cache.CacheStatistics;
import com.testanywhere.core.classes.utilities.TimeUtils;
import com.testanywhere.core.utilities.logging.OutputDisplay;

import java.util.Map;

public abstract class AbstractCacheStrategy extends OutputDisplay
{
    private CacheStatistics cacheStats;
    private Long initiationSize;
    private CacheConstants.TEMPORAL_CACHE cacheTimeout;
    private CacheConstants.SIZE_CACHE cacheSizeLimit;

    protected AbstractCacheStrategy( CacheConstants.TEMPORAL_CACHE timeout, CacheConstants.SIZE_CACHE sizeLimit )
    {
        this();
        this.cacheTimeout = timeout;
        this.cacheSizeLimit = sizeLimit;
    }

    protected CacheStatistics getCacheStats()
    {
        return this.cacheStats;
    }

    protected void resetCache( Cache cacheMap )
    {
        if ( cacheMap != null ) cacheMap.purge();
        this.cacheStats.reset();
    }

    public abstract void expire( Map<String, Object> inputs );

    private AbstractCacheStrategy()
    {
        super();
        this.__initialize();
    }

    private void __initialize()
    {
        this.cacheStats = new CacheStatistics();
        this.cacheTimeout = null;
        this.cacheSizeLimit = null;
        this.initiationSize = TimeUtils.getCurrentDateAsSeconds();
    }
}
