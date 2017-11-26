package com.testanywhere.core.classes.support.cache.strategies;

import com.testanywhere.core.classes.loaders.ABCClassloader;
import com.testanywhere.core.classes.loaders.LoaderUtils;
import com.testanywhere.core.utilities.logging.TextManager;

public class CacheStrategyLoader extends ABCClassloader
{
    public CacheStrategyLoader()
    {
        super();
        this.debug("Instantiating a " + this.getClass().getSimpleName() + " Class Loader...");
        this.initializeLoader(null);
    }

    @Override
    public void initializeLoader( String packageID )
    {
        if ( ! TextManager.validString(packageID) )
        {
            packageID = new Object(){}.getClass().getName();
        }
        LoaderUtils.initializeLoaderSettings( this, packageID );
        super.setInitialized(true);
    }

    public Class<?> reloadPluginClass(final String className)
    {
        if ( ! TextManager.validString(className, null) ) return null;
        if ( super.isValid() ) {
            if ( super.hasClass(className) ) super.clearClass(className);
            return this.loadPluginClass(className);
        }
        this.warn(this.getClass().getSimpleName() + " Class Loader is not valid at this time for " + TextManager.specializeName("reloadPluginClass"));
        return null;
    }

    public void reloadPluginClasses()
    {
        super.setValid(false);
        super.clearAllClasses();
        super.setValid(true);
        this.loadAllPluginClasses();
    }

    public void loadAllPluginClasses()
    {
        if ( ! super.isValid() )
        {
            this.warn(this.getClass().getSimpleName() + " is not valid at this time for 'loadAllPluginClasses'... (Re)Validating");
            if ( super.isValid() ) this.loadAllPluginClasses();
            else this.error("Unable to re(validate)...");
        }
    }
}
