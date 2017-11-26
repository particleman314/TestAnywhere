package com.testanywhere.core.classes.support.cache.strategies.types;

import com.testanywhere.core.utilities.class_support.Cast;
import com.testanywhere.core.classes.support.cache.Cache;
import com.testanywhere.core.classes.support.cache.CacheConstants;
import com.testanywhere.core.classes.support.cache.strategies.AbstractCacheStrategy;

import java.util.Iterator;
import java.util.Map;

public class SizeLimitCacheStrategy extends AbstractCacheStrategy
{
    public SizeLimitCacheStrategy(CacheConstants.SIZE_CACHE sizeLimit)
    {
        super(CacheConstants.TEMPORAL_CACHE.NONE, sizeLimit);
    }

    @Override
    public String toString() {
        return null;
    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public void buildObjectOutput(int numTabs) {
    }

    @Override
    public void expire( Map<String, Object> inputs )
    {
        if ( ! inputs.containsKey("MapData") ) return;

        if ( ! inputs.containsKey("ExpireSize") )
        {
            inputs.put("ExpireSize", (long) CacheConstants.NO_SIZE_LIMIT);
            this.expire(inputs);
            return;
        }

        Cache<?> c = (Cache<?>) inputs.get("MapData");
        Long mapSize = (long) c.size();
        Long expirySize = (Long) inputs.get("ExpireSize");

        while ( mapSize >  expirySize )
        {
            Map.Entry<Iterator<?>, Long> firstKey = Cast.cast(c.getAccessMap().entrySet().toArray()[0]);
            if ( firstKey != null )
            {
                c.purgeByIterator(firstKey.getKey());
                c.getAccessMap().remove(firstKey.getKey());
            }
        }
    }
}
