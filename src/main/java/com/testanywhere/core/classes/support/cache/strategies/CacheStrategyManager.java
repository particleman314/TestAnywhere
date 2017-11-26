package com.testanywhere.core.classes.support.cache.strategies;

import com.testanywhere.core.classes.managers.ABCManager;

@SuppressWarnings("ExternalizableWithoutPublicNoArgConstructor")
public class CacheStrategyManager extends ABCManager
{
    private static class CacheStrategyManagerHolder
    {
        public static final CacheStrategyManager INSTANCE = new CacheStrategyManager();
    }

    public static CacheStrategyManager getInstance()
    {
        return CacheStrategyManagerHolder.INSTANCE;
    }

    private CacheStrategyManager()
    {
        Class<?> clazz = CacheStrategyManager.class;
        this.__initialize();

        super.setManagerType(clazz);
        super.setManagerName(clazz.getSimpleName());
        super.configure();
    }

    @Override
    public void reset()
    {}

    @Override
    public void cleanup()
    {}

    private void __initialize()
    {}
}
