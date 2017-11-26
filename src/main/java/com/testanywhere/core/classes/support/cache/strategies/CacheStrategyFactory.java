package com.testanywhere.core.classes.support.cache.strategies;

import com.testanywhere.core.classes.class_support.CompartmentObject;
import com.testanywhere.core.classes.factory.ABCFactory;
import com.testanywhere.core.classes.factory.FactoryUtils;
import com.testanywhere.core.classes.support.cache.Cache;
import com.testanywhere.core.utilities.logging.TextManager;

import java.util.Collection;

public class CacheStrategyFactory extends ABCFactory
{
    public CacheStrategyFactory()
    {
        super();
        this.info("Instantiating a " + TextManager.specializeName(this.getClass().getSimpleName()) + "...");
        this.initializeFactory();
    }

    public Cache instantiate( final Class<?> type, final Collection<CompartmentObject<?>> alo )
    {
        return FactoryUtils.instantiateObject(this, type, alo);
    }

    public Cache instantiate( final String pluginType, final Collection<CompartmentObject<?>> alo )
    {
        return FactoryUtils.instantiateObject(this, pluginType, alo);
    }

    @Override
    public void initializeFactory()
    {
        if ( ! super.isInitialized() )
        {
            // This is the location of where we want to begin the initialization process based on the fact
            // a ConnectionLoader is attached to this ConnectionFactory
            CacheStrategyLoader pluginLoader = new CacheStrategyLoader();
            pluginLoader.initializeLoader(CacheStrategyFactory.class.getPackage().getName() + ".types");
            this.setLoader(pluginLoader);

            FactoryUtils.initializeFactorySettings(this, pluginLoader.getClass());

            super.setInitialized(true);
        }
    }

    @Override
    public void findFactoryClasses() {

    }
}
