package com.testanywhere.core.classes.support.cache.strategies.types;

import com.testanywhere.core.classes.support.cache.CacheConstants;
import com.testanywhere.core.classes.support.cache.strategies.AbstractCacheStrategy;

import java.util.Map;

public class DefaultCacheStrategy extends AbstractCacheStrategy
{
    public DefaultCacheStrategy()
    {
        super(CacheConstants.TEMPORAL_CACHE.NONE, CacheConstants.SIZE_CACHE.NONE);
    }

    @Override
    public String toString() { return null; }

    @Override
    public boolean isNull() { return false; }

    @Override
    public void buildObjectOutput(int numTabs)
    {}

    @Override
    public void expire( Map<String, Object> inputs )
    {}
}
