package com.testanywhere.core.classes.support.cache;

import com.testanywhere.core.utilities.class_support.Cast;
import com.testanywhere.core.classes.class_support.CompartmentObject;
import com.testanywhere.core.utilities.classes.NullObject;
import com.testanywhere.core.classes.support.cache.strategies.AbstractCacheStrategy;
import com.testanywhere.core.classes.support.cache.strategies.CacheStrategyManager;
import com.testanywhere.core.classes.support.map.DynamicMap;
import com.testanywhere.core.utilities.logging.*;
import com.testanywhere.core.classes.utilities.TimeUtils;

import java.util.*;

public class Cache<T> extends OutputDisplay
{
    private DynamicMap<T> cachedData;
    private Map<Iterator<?>, Long> lastAccessMap;
    private boolean useCache;
    private boolean useNullObjects;

    private AbstractCacheStrategy cacheStrategy;

    public Cache()
    {
        super();
        this.initialize();
    }

    public Cache(String cacheStrategyName, Collection<CompartmentObject<?>> csn_inputs)
    {
        this();
        if ( ! TextManager.validString(cacheStrategyName) ) return;
        Object cs = CacheStrategyManager.getInstance().getFactory().instantiate(cacheStrategyName, csn_inputs);
        if ( cs != null ) this.cacheStrategy = Cast.cast(cs);
        else this.cacheStrategy = Cast.cast(CacheStrategyManager.getInstance().getFactory().instantiate("DefaultCache", null));
    }

    @Override
    public void buildObjectOutput( int numTabs )
    {
        if ( numTabs < 0 ) numTabs = 0;
        Tabbing tabEnvironment = new Tabbing(numTabs);
        DisplayManager dm = this.getDM();

        String outerSpacer = tabEnvironment.getSpacer();
        dm.append(outerSpacer + "Cache : ", DisplayType.TEXTTYPES.LABEL);

        tabEnvironment.increment();
        String innerSpacer = tabEnvironment.getSpacer();
        dm.append(innerSpacer + "Allow caching : " + TextManager.StringRepOfBool(this.isCaching(), "yn"));
        dm.append(innerSpacer + "Use Null Object generation : " + TextManager.StringRepOfBool(this.useNullObjects, "yn"));

        this.getCacheStrategy().buildObjectOutput(tabEnvironment.numberTabs());
        dm.addFormatLines(this.getCacheStrategy().getDM().getLines());

        this.getCachedData().buildObjectOutput(tabEnvironment.numberTabs());
        dm.addFormatLines(this.getCachedData().getDM().getLines());
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("CA:" + this.isCaching() + TextManager.STR_OUTPUTSEPARATOR);
        sb.append("UNO:" + this.useNullObjects + TextManager.STR_OUTPUTSEPARATOR);
        sb.append("STR:" + this.cacheStrategy.toString() + TextManager.STR_OUTPUTSEPARATOR);
        sb.append(this.getCachedData().toString());
        return sb.toString();
    }

    @Override
    public boolean isNull() { return false; }

    public boolean isCaching()
    {
        return this.useCache;
    }

    public void allowCaching( final boolean allow )
    {
        this.allowCaching(allow, false);
    }

    public void allowNullObjectCreation( final boolean allowNullObj )
    {
        this.useNullObjects = allowNullObj;
    }

    public void allowCaching( final boolean allow, final boolean purge )
    {
        if ( this.isCaching() )
            if (purge) this.purge();

        this.useCache = allow;
    }

    public boolean put( final T key, final Object data )
    {
        return this.pushToCache(key, data);
    }

    public boolean pushToCache( final T key, final Object data )
    {
        if ( !this.isCaching() || CompartmentObject.isNull(key) ) return false;

        Long secondsSinceEpoch = TimeUtils.getCurrentDateAsSeconds();

        Map<String, Object> cacheParameters = new TreeMap<>();
        cacheParameters.put( "ExpireTime", secondsSinceEpoch );
        cacheParameters.put( "MapData", this.getCachedData() );

        this.cacheStrategy.expire(cacheParameters);

        if (data == null)
        {
            if (this.useNullObjects)
            {
                NullObject no = new NullObject();
                return this.cachedData.put(key, no, false);
            }
            else
                return this.getCachedData().put(key, null);
        }
        else
            return this.getCachedData().put(key, data);
    }

    public boolean containsKey( final T key )
    {
        return this.getCachedData().containsKey(key);
    }

    public Object get( final T key )
    {
        return this.getFromCache(key);
    }

    public Object getFromCache( final T key )
    {
        if ( key == null ) return null;

        if ( this.getCachedData().containsKey(key) ) return this.getCachedData().get(key);
        return null;
    }

    public int size()
    {
        if ( this.isCaching() )
            return this.getCachedData().size();
        return 0;
    }

    public void purge()
    {
        this.getCachedData().clear();
        this.getAccessMap().clear();
    }

    public boolean remove( final T key )
    {
        return this.purge(key);
    }

    public boolean purge( final T key ) {
        if (key == null) return false;

        return this.getCachedData().containsKey(key) && this.getCachedData().remove(key);

    }

    public boolean purgeByIterator( Iterator<?> iterator ) { return false; }
    public AbstractCacheStrategy getCacheStrategy()
    {
        return this.cacheStrategy;
    }

    public Map<Iterator<?>, Long> getAccessMap() { return this.lastAccessMap; }

    private DynamicMap<T> getCachedData()
    {
        return this.cachedData;
    }

    private Cache( Cache other )
    {}

    private void initialize()
    {
        this.cachedData     = new DynamicMap<>();
        this.useCache       = true;
        this.useNullObjects = true;

        this.lastAccessMap  = new LinkedHashMap<>();
        this.cacheStrategy  = Cast.cast(CacheStrategyManager.getInstance().getFactory().instantiate("DefaultCache", null));
    }
}
