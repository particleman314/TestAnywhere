package com.testanywhere.core.utilities.reflection.beanutils;

import com.testanywhere.core.utilities.logging.OutputDisplay;
import com.testanywhere.core.utilities.reflection.ClassFields.FieldsFilter;

import java.util.List;
import java.util.Map;

/**
 * Does nothing but implement with the defaults, used when the normal adapter is not available
 * 
 */
public class DefaultFieldAdapter extends OutputDisplay implements FieldAdapter
{
    public DefaultFieldAdapter()
    {
        super();
        this.initialize();
    }

    @Override
    public void buildObjectOutput( int numTabs )
    {}

    /* (non-Javadoc)
     * @see org.azeckoski.reflectutils.beanutils.FieldAdapter#isAdaptableObject(java.lang.Object)
     */
    @Override
    public boolean isAdaptableObject(Object obj) {
        return false;
    }

    /* (non-Javadoc)
     * @see org.azeckoski.reflectutils.beanutils.FieldAdapter#isAdaptableClass(java.lang.Class)
     */
    @Override
    public boolean isAdaptableClass(Class<?> beanClass) {
        return false;
    }

    // NOTE: nothing below here should ever get called

    /* (non-Javadoc)
     * @see org.azeckoski.reflectutils.beanutils.FieldAdapter#getFieldType(java.lang.Object, java.lang.String)
     */
    @Override
    public Class<?> getFieldType(Object obj, String name) {
        return null;
    }

    /* (non-Javadoc)
     * @see org.azeckoski.reflectutils.beanutils.FieldAdapter#getFieldValues(java.lang.Object, org.azeckoski.reflectutils.ClassFields.FieldsFilter)
     */
    @Override
    public Map<String, Object> getFieldValues(Object obj, FieldsFilter filter) {
        return null;
    }

    /* (non-Javadoc)
     * @see org.azeckoski.reflectutils.beanutils.FieldAdapter#getIndexedValue(java.lang.Object, java.lang.String, int)
     */
    @Override
    public Object getIndexedValue(Object obj, String name, int index) {
        return null;
    }

    /* (non-Javadoc)
     * @see org.azeckoski.reflectutils.beanutils.FieldAdapter#getMappedValue(java.lang.Object, java.lang.String, java.lang.String)
     */
    @Override
    public Object getMappedValue(Object obj, String name, String key) {
        return null;
    }

    /* (non-Javadoc)
     * @see org.azeckoski.reflectutils.beanutils.FieldAdapter#getSimpleValue(java.lang.Object, java.lang.String)
     */
    @Override
    public Object getSimpleValue(Object obj, String name) {
        return null;
    }

    /* (non-Javadoc)
     * @see org.azeckoski.reflectutils.beanutils.FieldAdapter#setIndexedValue(java.lang.Object, java.lang.String, int, java.lang.Object)
     */
    @Override
    public boolean setIndexedValue(Object obj, String name, int index, Object value)
    {
        return false;
    }

    /* (non-Javadoc)
     * @see org.azeckoski.reflectutils.beanutils.FieldAdapter#setMappedValue(java.lang.Object, java.lang.String, java.lang.String, java.lang.Object)
     */
    @Override
    public boolean setMappedValue(Object obj, String name, String key, Object value)
    {
        return false;
    }

    /* (non-Javadoc)
     * @see org.azeckoski.reflectutils.beanutils.FieldAdapter#setSimpleValue(java.lang.Object, java.lang.String, java.lang.Object)
     */
    @Override
    public boolean setSimpleValue(Object obj, String name, Object value)
    {
        return false;
    }

    @Override
    public List<String> getPropertyNames(Object bean) {
        return null;
    }

    @Override
    public Object newInstance(Object bean) {
        return null;
    }

    @Override
    public boolean isNull() { return false; }

    private void initialize()
    {}
}
