package com.testanywhere.core.utilities.reflection.beanutils;

import com.testanywhere.core.utilities.logging.OutputDisplay;
import com.testanywhere.core.utilities.logging.TextManager;
import com.testanywhere.core.utilities.reflection.ClassLoaderUtils;
import org.apache.log4j.Level;

public class FieldAdapterManager extends OutputDisplay
{
    public static final String DYNABEAN_CLASSNAME = "org.apache.commons.beanutils.DynaBean";
    public static final String DYNABEAN_ADAPTER   = "DynaBeanAdapter";

    private FieldAdapter fieldAdapter;

    public FieldAdapterManager()
    {
        super();
        this.initialize();
    }

    @Override
    public void buildObjectOutput (int numTabs)
    {}

    @Override
    public boolean isNull() { return this.getFieldAdapter() == null; }

    public FieldAdapter getFieldAdapter() {
        return this.fieldAdapter;
    }

    public boolean isAdaptableObject(Object obj)
    {
        boolean adaptable;
        try
        {
            adaptable = this.fieldAdapter.isAdaptableObject(obj);
        }
        catch (NoClassDefFoundError e)
        {
            this.fieldAdapter = new DefaultFieldAdapter();
            adaptable = false;
        }
        return adaptable;
    }

    public boolean isAdaptableClass(Class<?> beanClass)
    {
        boolean adaptable;
        try
        {
            adaptable = this.fieldAdapter.isAdaptableClass(beanClass);
        }
        catch (NoClassDefFoundError e)
        {
            this.fieldAdapter = new DefaultFieldAdapter();
            adaptable = false;
        }
        return adaptable;
    }

    private void initialize()
    {
        Class<?> dynaBean = ClassLoaderUtils.getClassFromString(DYNABEAN_CLASSNAME);
        if (dynaBean != null)
        {
            // assumes the adapter impl is in the same path as the interface
            String path = FieldAdapter.class.getName();
            path = path.replace(FieldAdapter.class.getSimpleName(), DYNABEAN_ADAPTER);
            Class<?> adapterClass = ClassLoaderUtils.getClassFromString(path);
            if (adapterClass == null) {
                this.log(Level.WARN,"Class did not find adapter class: " + TextManager.specializeName(path) + ", will continue without the dynabean adapter");
            } else {
                try {
                    this.fieldAdapter = (FieldAdapter) adapterClass.newInstance();
                } catch (Exception e) {
                    this.log(Level.WARN, "Failed to instantiate field adapter " + TextManager.specializeName(adapterClass.getName()) + ", will continue without the dynabean adapter: " + e);
                }
            }
        } else {
            this.fieldAdapter = new DefaultFieldAdapter();
        }

        if (this.fieldAdapter == null) this.fieldAdapter = new DefaultFieldAdapter();
    }
}
