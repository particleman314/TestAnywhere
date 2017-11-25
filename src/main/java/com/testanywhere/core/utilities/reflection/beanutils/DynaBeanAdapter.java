package com.nimsoft.reflection.beanutils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nimsoft.logging.OutputDisplay;
import com.nimsoft.reflection.ClassFields.FieldsFilter;
import com.nimsoft.reflection.exceptions.FieldnameNotFoundException;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;

/**
 * This allows dynabeans to work with the field utils,
 * should only be loaded by reflection if the DynaBean class can be found
 */
public class DynaBeanAdapter extends OutputDisplay implements FieldAdapter
{
    public DynaBeanAdapter()
    {
        super();
        this.initialize();
    }

    @Override
    public void buildObjectOutput( int numTabs )
    {}

    @Override
    public boolean isAdaptableObject(Object obj)
    {
        boolean adaptable = false;
        if (obj instanceof DynaBean) adaptable = true;
        return adaptable;
    }

    @Override
    public boolean isAdaptableClass(Class<?> beanClass)
    {
        boolean adaptable = false;
        if (DynaBean.class.isAssignableFrom(beanClass)) adaptable = true;
        return adaptable;
    }

    /* (non-Javadoc)
     * @see com.nimsoft.reflection.beanutils.FieldAdapter#getFieldType(java.lang.Object, java.lang.String)
     */
    @Override
    public Class<?> getFieldType(Object obj, String name) {
        DynaClass dynaClass = ((DynaBean) obj).getDynaClass();
        DynaProperty dynaProperty = dynaClass.getDynaProperty(name);
        if (dynaProperty == null) throw new FieldnameNotFoundException("DynaBean: Could not find this fieldName ("+name+") on the target object: " + obj, name, null);
        return dynaProperty.getType();
    }

    /* (non-Javadoc)
     * @see com.nimsoft.reflection.beanutils.FieldAdapter#getFieldValues(java.lang.Object, org.azeckoski.reflectutils.ClassFields.FieldsFilter)
     */
    @Override
    public Map<String, Object> getFieldValues(Object obj, FieldsFilter filter) {
        Map<String, Object> values = new HashMap<String, Object>();
        DynaProperty[] descriptors =
            ((DynaBean) obj).getDynaClass().getDynaProperties();
        for (int i = 0; i < descriptors.length; i++) {
            String name = descriptors[i].getName();
            Object o = getSimpleValue(obj, name);
            values.put(name, o);
        }
        return values;
    }

    /* (non-Javadoc)
     * @see com.nimsoft.reflection.beanutils.FieldAdapter#getSimpleValue(java.lang.Object, java.lang.String)
     */
    @Override
    public Object getSimpleValue(Object obj, String name) {
        DynaProperty descriptor =
            ((DynaBean) obj).getDynaClass().getDynaProperty(name);
        if (descriptor == null) throw new FieldnameNotFoundException(name);
        Object value = (((DynaBean) obj).get(name));
        return value;
    }

    /* (non-Javadoc)
     * @see com.nimsoft.reflection.beanutils.FieldAdapter#getIndexedValue(java.lang.Object, java.lang.String, int)
     */
    @Override
    public Object getIndexedValue(Object obj, String name, int index) {
        DynaProperty descriptor =
            ((DynaBean) obj).getDynaClass().getDynaProperty(name);
        if (descriptor == null) throw new FieldnameNotFoundException(name);
        Object value = ((DynaBean) obj).get(name, index);
        return value;
    }

    /* (non-Javadoc)
     * @see com.nimsoft.reflection.beanutils.FieldAdapter#getMappedValue(java.lang.Object, java.lang.String, java.lang.String)
     */
    @Override
    public Object getMappedValue(Object obj, String name, String key) {
        DynaProperty descriptor =
            ((DynaBean) obj).getDynaClass().getDynaProperty(name);
        if (descriptor == null) throw new FieldnameNotFoundException(name);

        Object value = ((DynaBean) obj).get(name, key);
        return value;
    }

    /* (non-Javadoc)
     * @see org.azeckoski.reflectutils.beanutils.FieldAdapter#setIndexedValue(java.lang.Object, java.lang.String, int, java.lang.Object)
     */
    @Override
    public boolean setIndexedValue(Object obj, String name, int index, Object value) {
        DynaProperty descriptor =
            ((DynaBean) obj).getDynaClass().getDynaProperty(name);
        if (descriptor == null) throw new FieldnameNotFoundException(name);
        ((DynaBean) obj).set(name, index, value);
        return true;
    }

    /* (non-Javadoc)
     * @see org.azeckoski.reflectutils.beanutils.FieldAdapter#setMappedValue(java.lang.Object, java.lang.String, java.lang.String, java.lang.Object)
     */
    @Override
    public boolean setMappedValue(Object obj, String name, String key, Object value) {
        DynaProperty descriptor =
            ((DynaBean) obj).getDynaClass().getDynaProperty(name);
        if (descriptor == null) throw new FieldnameNotFoundException(name);
        ((DynaBean) obj).set(name, key, value);
        return true;
    }

    /* (non-Javadoc)
     * @see org.azeckoski.reflectutils.beanutils.FieldAdapter#setSimpleValue(java.lang.Object, java.lang.String, java.lang.Object)
     */
    @Override
    public boolean setSimpleValue(Object obj, String name, Object value) {
        DynaProperty descriptor =
            ((DynaBean) obj).getDynaClass().getDynaProperty(name);
        if (descriptor == null) throw new FieldnameNotFoundException(name);
        ((DynaBean) obj).set(name, value);
        return true;
    }

    @Override
    public Object newInstance(Object bean) {
        try {
            return ((DynaBean) bean).getDynaClass().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Could not instantiate DynaBean: " + bean, e);
        } // make new dynabean
    }

    @Override
    public List<String> getPropertyNames(Object bean)
    {
        List<String> names = new ArrayList<>();
        DynaProperty origDescriptors[] =
            ((DynaBean) bean).getDynaClass().getDynaProperties();
        for (DynaProperty dynaProperty : origDescriptors) {
            String name = dynaProperty.getName();
            names.add(name);
        }
        return names;
    }

    @Override
    public boolean isNull() { return false; }

    private void initialize()
    {}
}
