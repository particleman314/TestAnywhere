package com.testanywhere.core.classes.support.cache.strategies.types;

import com.testanywhere.core.classes.support.cache.CacheConstants;
import com.testanywhere.core.classes.support.cache.strategies.AbstractCacheStrategy;
import com.testanywhere.core.classes.utilities.TimeUtils;

import java.util.Map;

public class TemporalCacheStrategy extends AbstractCacheStrategy
{
    public TemporalCacheStrategy(CacheConstants.TEMPORAL_CACHE timeout)
    {
        super(timeout, CacheConstants.SIZE_CACHE.NONE);
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
    {
        if ( ! inputs.containsKey("ExpireTime") )
        {
            inputs.put("ExpireTime", TimeUtils.getCurrentDateAsSeconds() );
            this.expire(inputs);
        }


    }

}
