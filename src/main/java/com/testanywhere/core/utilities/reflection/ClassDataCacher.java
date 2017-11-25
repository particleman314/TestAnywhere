package com.testanywhere.core.utilities.reflection;

import com.testanywhere.core.utilities.classes.Pair;
import com.testanywhere.core.utilities.logging.LogConfiguration;
import com.testanywhere.core.utilities.reflection.ClassFields.FieldFindMode;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class ClassDataCacher
{
    private static Logger logger;
    private FieldFindMode fieldFindMode;
    private boolean includeClassField;

    @SuppressWarnings("unchecked")
    private Map<Class<?>, ClassFields> reflectionCache;

    private int lookups = 0;
    private Pair<Integer, Integer> cacheHitsAndMisses = new Pair<>(0, 0);

    // This makes for a singleton object to keep the only copy
    private static boolean isInitialized = false;

    // This ensures it is initialized when import by any class
    static
    {
        LogConfiguration.configure();
        ClassDataCacher.logger = Logger.getLogger(ClassDataCacher.class);
        ClassDataCacher.getInstance();
    }

    public static ClassDataCacher getInstance()
    {
        if ( ! ClassDataCacher.isInitialized ) {
            ClassDataCacher.isInitialized = true;
            ClassDataCacher.initialize();
        }
        return ClassDataCacherHolder.INSTANCE;
    }

    private static class ClassDataCacherHolder
    {
        public static final ClassDataCacher INSTANCE = new ClassDataCacher();
    }

    private static void initialize()
    {}

    private ClassDataCacher()
    {
        super();

        this.includeClassField = false;
        this.reflectionCache = new HashMap<>();

        this.setFieldFindMode(FieldFindMode.HYBRID);
    }

    /**
     * Construct and specify a mode for looking up fields which does not match the default: {@link FieldFindMode#HYBRID}
     * @param fieldFindMode the mode when looking up fields in classes
     * <br/>
     * <b>WARNING:</b> if you don't need this control then just use the {@link #getInstance()} method to get this
     */
    public ClassDataCacher(FieldFindMode fieldFindMode)
    {
        this();
        this.setFieldFindMode(fieldFindMode);
    }

    /**
     * Construct and specify the field finding mode and your own cache when caching class data, must implement the standard map interface but
     * only the following methods are required:<br/>
     * {@link Map#clear()}, {@link Map#size()}, {@link Map#put(Object, Object)}, {@link Map#get(Object)} <br/>
     * <br/>
     * <b>WARNING:</b> if you don't need this control then just use the {@link #getInstance()} method to get this
     * 
     * @param fieldFindMode the mode when looking up fields in classes (null for default of {@link FieldFindMode#HYBRID})
     * @param reflectionCache a map implementation to use as the cache mechanism (null to use internal)
     */
    @SuppressWarnings("unchecked")
    public ClassDataCacher(FieldFindMode fieldFindMode, Map<Class<?>, ClassFields> reflectionCache)
    {
        this();
        this.setFieldFindMode(fieldFindMode);
        this.setReflectionCache(reflectionCache);
    }

    // class fields

    /**
     * Set the mode used to find fields on classes (default {@link FieldFindMode#HYBRID}) <br/>
     * <b>WARNING</b>: changing modes will clear the existing cache
     * 
     * @param fieldFindMode see FieldFindMode enum for details
     * @see FieldFindMode
     */
    public void setFieldFindMode(FieldFindMode fieldFindMode)
    {
        if (fieldFindMode == null) fieldFindMode = FieldFindMode.HYBRID;
        if ( this.fieldFindMode != null && ! this.fieldFindMode.equals(fieldFindMode)) {
            Map<Class<?>, ClassFields> map = this.getReflectionCache();
            if (map != null) map.clear();
        }
        this.fieldFindMode = fieldFindMode;
    }

    public FieldFindMode getFieldFindMode() {
        return this.fieldFindMode;
    }

    /**
     * Setting to determine if the result of "getClass()" should be included in the reflection data <br/>
     * <b>WARNING</b>: changing this will clear the existing cache
     * 
     * @param includeClassField if true then getClass() will be treated as a readable field called "class", default is false
     */    
    public void setIncludeClassField(boolean includeClassField)
    {
        if (this.includeClassField != includeClassField) this.getReflectionCache().clear();
        this.includeClassField = includeClassField;
    }

    public boolean isIncludeClassField() {
        return this.includeClassField;
    }

    @SuppressWarnings("unchecked")
    protected Map<Class<?>, ClassFields> getReflectionCache()
    {
        return this.reflectionCache;
    }
    /**
     * Set the cache to be used for holding the reflection data, 
     * this allows control over where the reflection caches are stored,
     * this should store the data in a way that it will not hold open the classloader the class comes from <br/>
     * Note that you can set this to a map implementation which does not store anything to disable caching if you like
     * 
     * @param reflectionCache a cache for holding class cache data (implements map), null to use the default internal cache
     */
    @SuppressWarnings("unchecked")
    public void setReflectionCache(Map<Class<?>, ClassFields> reflectionCache)
    {
        if (reflectionCache != null) this.reflectionCache = reflectionCache;
        else this.getReflectionCache();
    }

    /**
     * Get the class fields analysis of a class which contains information about that class and its fields,
     * includes annotations, fields/properties, etc. packaged in a way which makes the data easy to get to,
     * use the {@link ClassData} object to get to the more raw data
     * 
     * @param <T>
     * @param cls any {@link Class}
     * @return the ClassFields analysis object which contains the information about this class
     */
    public <T> ClassFields<T> getClassFields(Class<T> cls)
    {
        return this.getClassFields(cls, this.fieldFindMode);
    }

    /**
     * Get the class fields analysis of a class which contains information about that class and its fields,
     * includes annotations, fields/properties, etc. packaged in a way which makes the data easy to get to,
     * use the {@link ClassData} object to get to the more raw data
     *
     * @param <T>
     * @param cls any {@link Class}
     * @param mode (optional) mode for searching the class for fields, default HYBRID
     * @return the ClassFields analysis object which contains the information about this class
     */
    @SuppressWarnings("unchecked")
    public <T> ClassFields<T> getClassFields(Class<T> cls, FieldFindMode mode)
    {
        if (cls == null) throw new IllegalArgumentException("cls (type) cannot be null");
        if (mode == null)
        {
            if (this.fieldFindMode == null) mode = FieldFindMode.HYBRID; // default
            else mode = this.fieldFindMode;
        }

        this.lookups++;

        ClassFields<T> cf = this.getReflectionCache().get(cls);
        if (cf != null && !mode.equals(cf.getFieldFindMode())) cf = null;
        if (cf == null)
        {
            // make new and put in cache
            cf = new ClassFields<T>(cls, mode, false, this.includeClassField);
            getReflectionCache().put(cls, cf);
            this.cacheHitsAndMisses.setR(cacheHitsAndMisses.getR() + 1);
        }
        else
        {
            this.cacheHitsAndMisses.setL(cacheHitsAndMisses.getL() + 1);
        }
        return cf;
    }

    /**
     * Convenience Method: <br/>
     * Gets the class data object which contains information about this class,
     * will retrieve this from the class data cache if available or generate it if not<br/>
     * This is also available from the {@link ClassFields} object
     * 
     * @param <T>
     * @param cls any {@link Class}
     * @return the class data cache object (contains reflected data from this class)
     */
    public <T> ClassData<T> getClassData(Class<T> cls)
    {
        ClassFields<T> cf = this.getClassFields(cls);
        return cf.getClassData();
    }

    /**
     * Convenience Method: <br/>
     * Analyze an object and produce an object which contains information about it and its fields,
     * see {@link ClassDataCacher#getClassData(Class)}
     * 
     * @param <T>
     * @param obj any {@link Object}
     * @return the ClassFields analysis object which contains the information about this objects class
     * @throws IllegalArgumentException if obj is null
     */
    @SuppressWarnings("unchecked")
    public <T> ClassFields<T> getClassFieldsFromObject(Object obj)
    {
        if (obj == null) throw new IllegalArgumentException("obj cannot be null");
        Class<T> cls = (Class<T>) obj.getClass();
        return this.getClassFields(cls);
    }

    /**
     * Convenience Method: <br/>
     * Gets the class data object which contains information about this objects class,
     * will retrieve this from the class data cache if available or generate it if not<br/>
     * This is also available from the {@link ClassFields} object
     * 
     * @param <T>
     * @param obj any {@link Object}
     * @return the raw ClassData cache object which contains reflection data about this objects class
     * @throws IllegalArgumentException if obj is null
     */
    public <T> ClassData<T> getClassData(Object obj)
    {
        ClassFields<T> cf = this.getClassFieldsFromObject(obj);
        return cf.getClassData();
    }

    /**
     * Clears all cached objects
     */
    public void clear()
    {
        this.getReflectionCache().clear();
    }

    /**
     * @return the size of the cache (number of cached {@link ClassFields} entries)
     */
    public int size()
    {
        return this.getReflectionCache().size();
    }

    @Override
    public String toString()
    {
        return null;
    }
}
