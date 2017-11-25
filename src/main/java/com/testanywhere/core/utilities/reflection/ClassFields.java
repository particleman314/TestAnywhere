package com.testanywhere.core.utilities.reflection;

import com.testanywhere.core.utilities.reflection.annotations.ReflectIgnoreClassFields;
import com.testanywhere.core.utilities.reflection.annotations.ReflectIncludeStaticFields;
import com.testanywhere.core.utilities.reflection.annotations.ReflectTransientClassFields;
import com.testanywhere.core.utilities.reflection.exceptions.FieldnameNotFoundException;
import com.testanywhere.core.utilities.reflection.map.ArrayOrderedMap;
import com.testanywhere.core.utilities.reflection.map.OrderedMap;

import java.beans.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.Map.Entry;

public class ClassFields<T>
{
    public static final String FIELD_CLASS = "class";
    public static final String METHOD_GET_CLASS = "getClass";
    private static final String PREFIX_IS = "is";
    private static final String PREFIX_GET = "get";
    private static final String PREFIX_SET = "set";

    private final FieldFindMode fieldFindMode;
    /**
     * if true then include the "class" field as if it were a regular field
     */
    private boolean includeClassField = false;
    /**
     * if true then include static fields, otherwise skip them (default is to skip)
     */
    private boolean includeStaticFields = false;
    /**
     * includes all the field names which should be ignored for reflection
     */
    private final Set<String> ignoredFieldNames = new HashSet<>();
    /**
     * includes all the field names which should be marked as transient
     */
    private final Set<String> transientFieldNames = new HashSet<>();
    private final ClassData<T> classData;
    private final OrderedMap<String, ClassProperty> namesToProperties; // this contains all properties data (includes partials)

    /**
     * Indicates the fields filtering to use when retrieving fields from types/objects: <br/>
     * {@link #COMPLETE} (default)
     */
    public static enum FieldsFilter {
        /**
         * all complete (read and write) fields,
         * getters and setters included
         */
        COMPLETE, 
        /**
         * (default)
         * all readable fields (may not be writeable),
         * includes transient and virtual (getter only) fields
         */
        READABLE, 
        /**
         * all writeable fields (may not be readable),
         * public and setters
         */
        WRITEABLE, 
        /**
         * all accessible serializable fields or getters (skips transients)
         */
        SERIALIZABLE,
        /**
         * all accessible serializable fields (skips transient fields),
         * no virtual fields (getters only)
         */
        SERIALIZABLE_FIELDS,
        /**
         * all fields including ones that are virtual (read or write only)
         */
        ALL
    }

    // PUBLIC constructors

    /**
     * Constructor for when you have everything already in nice arrays, the getter and setter method
     * arrays MUST be the same length AND the same order (i.e. getter[0] and setter[0] must refer to
     * the same property), no arguments can be null but arrays can be empty, null values in arrays
     * will be ignored<br/>
     * <b>NOTE:</b> validation of the getters and setters is somewhat costly
     * so you may want to cache the constructed object
     *
     * @param fieldClass
     *            this is the class whose fields are represented
     * @param getterMethods
     *            array of getter methods for properties
     * @param setterMethods
     *            array of setter methods for properties
     * @param publicFields
     *            array of public fields
     */
    public ClassFields(Class<T> fieldClass, Method[] getterMethods, Method[] setterMethods, Field[] publicFields) {
        if (fieldClass == null || getterMethods == null || setterMethods == null || publicFields == null)
            throw new IllegalArgumentException("None of the params can be null");
        if ( getterMethods.length != setterMethods.length )
            throw new IllegalArgumentException("Getter and setter methods must be the same length");

        // set the field class
        this.classData = new ClassData<T>(fieldClass);
        this.fieldFindMode = FieldFindMode.HYBRID;
        // set the properties
        this.namesToProperties = new ArrayOrderedMap<String, ClassProperty>(getterMethods.length
                + publicFields.length);
        for (int i = 0; i < getterMethods.length; i++) {
            if (getterMethods[i] != null && setterMethods[i] != null) {
                String fieldName = this.checkPropertyMethods(getterMethods[i], setterMethods[i]);
                if (!this.namesToProperties.containsKey(fieldName)) {
                    ClassProperty p = new ClassProperty(fieldName, getterMethods[i],
                            setterMethods[i]);
                    this.namesToProperties.put(fieldName, p);
                }
            }
        }
        this.populateFields(publicFields);
        this.populateAnnotationsFields();
    }

    /**
     * Constructor for when you have descriptors and fields in arrays (if you have descriptors it is
     * easy to get the fields from the class using {@link Class#getFields()}), no arguments can be
     * null but the arrays can be empty
     *
     * @param fieldClass
     *            this is the class whose fields are represented
     * @param descriptors
     *            an array of descriptors, you can get these from
     *            {@link BeanInfo#getPropertyDescriptors()} and you can get the {@link BeanInfo}
     *            from {@link Introspector#getBeanInfo(Class)}
     * @param publicFields
     *            array of public fields
     */
    public ClassFields(Class<T> fieldClass, PropertyDescriptor[] descriptors, Field[] publicFields) {
        if (fieldClass == null || descriptors == null || publicFields == null)
            throw new IllegalArgumentException("None of the params can be null");

        this.classData = new ClassData<T>(fieldClass);
        this.fieldFindMode = FieldFindMode.HYBRID;
        this.namesToProperties = new ArrayOrderedMap<String, ClassProperty>(descriptors.length + publicFields.length);
        this.populateProperties(descriptors);
        this.populateFields(publicFields);
        this.populateAnnotationsFields();
    }

    /**
     * Constructor to use when you know nothing about the class at all, this will use very
     * straightforward walking over the PUBLIC methods and fields on the class and will extract the field
     * information, this uses the default field finding mode of {@link FieldFindMode#HYBRID}<br/>
     * <b>NOTE:</b> This is fairly expensive so it is recommended that you cache the constructed object
     * by using {@link FieldUtils}
     *
     * @param fieldClass
     *            this is the class whose fields are represented
     */
    public ClassFields(Class<T> fieldClass) {
        this(fieldClass, FieldFindMode.HYBRID, false, false);
    }

    /**
     * Constructor to use when you know nothing about the class at all, this will use very
     * straightforward walking over the methods and fields on the class and will extract the field
     * information according to the field finding mode<br/>
     * <b>NOTE:</b> This is fairly expensive so it is recommended that you cache the constructed object
     * by using {@link FieldUtils}
     *
     * @param fieldClass this is the class whose fields are represented
     * @param findMode the search mode for fields on the classes, allows the developer to control the way the
     * fields are found for later usage<br/>
     * HYBRID (default): finds fields by matched public getters and setters first and then public fields <br/>
     * FIELD: finds all fields which are accessible (public, protected, or private), ignores getters and setters <br/>
     * PROPERTY: find all matched getters and setters only, ignores fields <br/>
     */
    public ClassFields(Class<T> fieldClass, FieldFindMode findMode) {
        this(fieldClass, findMode, false, false);
    }

    /**
     * Constructor to use when you know nothing about the class at all, this will use the java
     * introspector as long as the param is true, otherwise it will simply call over to
     * {@link ClassFields#ClassFields(Class)} and use very
     * straightforward walking over the methods and fields on the class to extract the field
     * information according to the field finding mode<br/>
     * <b>NOTE:</b> This is fairly expensive so it is recommended that you cache the constructed object
     * by using {@link FieldUtils}
     *
     * @param fieldClass this is the class whose fields are represented
     * @param findMode the search mode for fields on the classes, allows the developer to control the way the
     * fields are found for later usage<br/>
     * HYBRID (default): finds fields by matched public getters and setters first and then public fields <br/>
     * FIELD: finds all fields which are accessible (public, protected, or private), ignores getters and setters <br/>
     * PROPERTY: find all matched getters and setters only, ignores fields <br/>
     * @param useIntrospector
     *            if this is true then the {@link Introspector} is used, this is generally the
     *            slowest way to get information about a class but some people prefer it, if false
     *            then the internal methods are used
     * @param includeClassFields if true then the "class" field (the result of getClass()) is included as a read only field
     */
    public ClassFields(Class<T> fieldClass, FieldFindMode findMode, boolean useIntrospector, boolean includeClassFields) {
        // set the field class
        if (fieldClass == null)
            throw new IllegalArgumentException("field class cannot be null");

        this.classData = new ClassData<T>(fieldClass);
        this.fieldFindMode = findMode;
        this.includeClassField = includeClassFields;
        this.namesToProperties = new ArrayOrderedMap<String, ClassProperty>();

        // check for reflect annotations on the class
        List<Annotation> annotations = this.classData.getAnnotations();
        for (Annotation annotation : annotations) {
            // note that we compare the simple name to avoid issues with cross classloader types
            if (ReflectIncludeStaticFields.class.getSimpleName().equals(annotation.annotationType().getSimpleName())) {
                this.includeStaticFields = true;
            } else if (ReflectIgnoreClassFields.class.getSimpleName().equals(annotation.annotationType().getSimpleName())) {
                // this does not work if the classloader of the annotation is not our classloader
                String[] ignore = new String[0];
                if (ReflectIgnoreClassFields.class.equals(annotation.annotationType())) {
                    ignore = ((ReflectIgnoreClassFields)annotation).value();
                } else {
                    // get the value out by reflection on the annotation
                    ClassData<?> cd = ClassDataCacher.getInstance().getClassData(annotation);
                    List<Method> methods = cd.getMethods();
                    for (Method method : methods) {
                        if (method.getName().equals("value")) {
                            try {
                                @SuppressWarnings("RedundantArrayCreation") Object o = method.invoke(annotation, new Object[] {});
                                ignore = (String[]) o;
                            } catch (Exception e) {
                                throw new RuntimeException("Annotation of the same name has invalid value() method ("+method+") and is not of the right type: " + ReflectIgnoreClassFields.class);
                            }
                            break;
                        }
                    }
                }
                Collections.addAll(this.ignoredFieldNames, ignore);
            } else if (ReflectTransientClassFields.class.getSimpleName().equals(annotation.annotationType().getSimpleName())) {
                // this does not work if the classloader of the annotation is not our classloader
                String[] transients = new String[0];
                if (ReflectTransientClassFields.class.equals(annotation.annotationType())) {
                    transients = ((ReflectTransientClassFields)annotation).value();
                } else {
                    // get the value out by reflection on the annotation
                    ClassData<?> cd = ClassDataCacher.getInstance().getClassData(annotation);
                    List<Method> methods = cd.getMethods();
                    for (Method method : methods) {
                        if (method.getName().equals("value")) {
                            try {
                                @SuppressWarnings("RedundantArrayCreation") Object o = method.invoke(annotation, new Object[] {});
                                transients = (String[]) o;
                            } catch (Exception e) {
                                throw new RuntimeException("Annotation of the same name has invalid value() method ("+method+") and is not of the right type: " + ReflectTransientClassFields.class);
                            }
                            break;
                        }
                    }
                }
                Collections.addAll(this.transientFieldNames, transients);
            }
        }

        // populate the internal storage with the list of properties
        if (useIntrospector) {
            // use the java Introspector
            BeanInfo bi;
            try {
                bi = Introspector.getBeanInfo(fieldClass);
                PropertyDescriptor[] descriptors = bi.getPropertyDescriptors();
                if (descriptors != null) {
                    this.populateProperties(descriptors);
                    this.populateFields(false);
                } else {
                    useIntrospector = false;
                }
            } catch (IntrospectionException e) {
                // no descriptors so fail over to using the internal methods
                useIntrospector = false;
            }
        }

        if (!useIntrospector) {
            // construct using pure reflection
            if (FieldFindMode.HYBRID.equals(findMode) || FieldFindMode.PROPERTY.equals(findMode) || FieldFindMode.ALL.equals(findMode)) {
                List<ClassProperty> properties = this.findProperties(true);
                for (ClassProperty property : properties) {
                    String fieldName = property.getFieldName();
                    this.namesToProperties.put(fieldName, property);
                }
            }
            if (FieldFindMode.HYBRID.equals(findMode) || FieldFindMode.FIELD.equals(findMode) || FieldFindMode.ALL.equals(findMode)) {
                if (FieldFindMode.FIELD.equals(findMode) || FieldFindMode.ALL.equals(findMode)) {
                    this.populateFields(true);
                } else {
                    this.populateFields(false);
                }
            }
        }
        this.populateAnnotationsFields();
    }

    /**
     * @param cp the {@link ClassProperty} object which represents a field
     * @param filter (optional) indicates the fields to return the names for, can be null for defaults
     * @return true if this field is in the given filter
     */
    public boolean isFieldInFilter(ClassProperty cp, FieldsFilter filter) {
        boolean in = false;
        if (FieldsFilter.ALL.equals(filter)) {
            in = true;
        } else if (FieldsFilter.READABLE.equals(filter)) {
            if ( isGettable(cp) ) {
                in = true;
            }
        } else if (FieldsFilter.WRITEABLE.equals(filter)) {
            if ( isSettable(cp) ) {
                in = true;
            }
        } else if (FieldsFilter.SERIALIZABLE.equals(filter)) {
            if ( isGettable(cp) && !cp.isTransient()) {
                in = true;
            }
        } else if (FieldsFilter.SERIALIZABLE_FIELDS.equals(filter)) {
            if (cp.isField() && !cp.isTransient()) {
                in = true;
            }
        } else {
            // return complete
            if ( isComplete(cp) ) {
                in = true;
            }
        }
        return in;
    }

    /**
     * Mode for finding the fields on classes/objects:<br/>
     * {@link #HYBRID} (default)
     */
    public static enum FieldFindMode { 
        /**
         * (default) finds fields by matched public getters and setters first and then public fields
         */
        HYBRID, 
        /**
         * finds all fields which are accessible (public, protected, or private), ignores getters and setters
         */
        FIELD, 
        /**
         * find all matched getters and setters only, ignores fields
         */
        PROPERTY,
        /**
         * finds all possible fields including matched getter/setter
         */
        ALL
    }

    // PUBLIC access methods

    /**
     * @return the class that this {@link ClassFields} object represents the fields for
     * @throws java.lang.Exception class loading exception,
     *             if the class this refers to has been garbage collected
     */
    public Class<T> getFieldClass() {
        return this.getStoredClass();
    }

    /**
     * @return the field find mode being used (cannot be changed)
     */
    public FieldFindMode getFieldFindMode() {
        return this.fieldFindMode;
    }

    /**
     * @return the list of fields we are explicitly skipping over
     */
    public Set<String> getIgnoredFieldNames() {
        return this.ignoredFieldNames;
    }

    /**
     * @return the list of field names <br/> Only returns the complete
     *         fields, the partial ones (only settable or gettable) will not be returned
     */
    public List<String> getFieldNames() {
        return this.getFieldNames(null);
    }

    /**
     * Get the field names but filter the fields to return
     * @param filter (optional) indicates the fields to return the names for, can be null for defaults
     * @return the list of field names
     */
    public List<String> getFieldNames(FieldsFilter filter) {
        List<String> names = new ArrayList<String>();
        for (Entry<String, ClassProperty> entry : this.namesToProperties.getEntries()) {
            String name = entry.getKey();
            ClassProperty cp = entry.getValue();
            if ( this.isFieldInFilter(cp, filter) ) {
                names.add(name);
            }
        }
        return names;
    }

    /**
     * Get the types for fields in a class
     * @return the map of fieldName -> field type
     */
    public Map<String, Class<?>> getFieldTypes() {
        return this.getFieldTypes(null);
    }

    /**
     * Get the types for fields in a class but filter the fields to get the types for
     * @param filter (optional) indicates the fields to return the types for, can be null for defaults
     * @return the map of fieldName -> field type
     */
    public Map<String, Class<?>> getFieldTypes(FieldsFilter filter) {
        OrderedMap<String, Class<?>> fieldTypes = new ArrayOrderedMap<String, Class<?>>();
        fieldTypes.setName(getStoredClass().getName());
        for (Entry<String, ClassProperty> entry : this.namesToProperties.getEntries()) {
            String name = entry.getKey();
            ClassProperty cp = entry.getValue();
            if ( this.isFieldInFilter(cp, filter) ) {
                fieldTypes.put(name, cp.getType());
            }
        }
        return fieldTypes;
    }

    /**
     * @return the number of fields (only includes complete fields)
     */
    public int size() {
        return size(null);
    }

    /**
     * @param filter (optional) indicates the fields to include in the count, can be null for defaults
     * @return the number of fields
     */
    public int size(FieldsFilter filter)
    {
        int size;
        if (FieldsFilter.ALL.equals(filter)) {
            size = namesToProperties.size();
        } else {
            size = this.getFieldNames(filter).size();
        }
        return size;
    }

    /**
     * @return true if this has no fields, false otherwise (only includes complete fields)
     */
    public boolean isEmpty() {
        return this.size() <= 0;
    }

    /**
     * @param filter (optional) indicates the fields to include in the check, can be null for defaults
     * @return true if this has no fields, false otherwise (only includes complete fields)
     */
    public boolean isEmpty(FieldsFilter filter) {
        return this.size(filter) <= 0;
    }

    /**
     * Checks is this is a complete field
     * @param name the fieldName
     * @return true if this fieldName is valid, false otherwise
     */
    public boolean isFieldNameValid(String name) {
        return this.isFieldNameValid(name, null);
    }

    /**
     * Checks if a field is valid for the given filter
     * 
     * @param name the fieldName
     * @param filter (optional) indicates the fields to include in the check, can be null for defaults
     * @return true if this fieldName is valid, false otherwise
     */
    public boolean isFieldNameValid(String name, FieldsFilter filter) {
        boolean valid = this.namesToProperties.containsKey(name);
        if (valid) {
            // the field is real so check to see if it is in this filter
            ClassProperty cp = this.getAnyPropertyOrFail(name);
            if ( this.isFieldInFilter(cp, filter) )
                valid = true;
            else
                valid = false;
        }
        return valid;
    }

    /**
     * @param name the fieldName
     * @return the type of this field
     * @throws FieldnameNotFoundException if this fieldName is invalid
     */
    public Class<?> getFieldType(String name)
    {
        ClassProperty cp = this.getAnyPropertyOrFail(name);
        Class<?> type = cp.getType();
        return type;
    }

    // ANNOTATIONS

    /**
     * Get all annotations present on the represented class
     * 
     * @return the set of all annotations on the class this refers to
     */
    public Set<Annotation> getClassAnnotations() {
        return new HashSet<Annotation>( this.classData.getAnnotations() );
    }

    /**
     * Gets the annotation from the represented class if one exists of the type specified
     * 
     * @param annotationType
     *            the annotation type to look for on this class
     * @return the annotation if found OR null if none found
     */
    @SuppressWarnings({ "unchecked", "hiding" })
    public <T extends Annotation> T getClassAnnotation(Class<T> annotationType) {
        if (annotationType == null) {
            throw new IllegalArgumentException("annotationType must not be null");
        }
        T annote = null;
        List<Annotation> annotations = this.classData.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotationType.equals(annotation.annotationType())) {
                annote = (T) annotation;
                break;
            }
        }
        return annote;
    }

    /**
     * Get all annotations on the field in the represented class
     * 
     * @param name the fieldName
     * @return the set of all annotations on this field
     * @throws FieldnameNotFoundException if this fieldName is invalid
     */
    public Set<Annotation> getFieldAnnotations(String name) {
        ClassProperty cp = this.getAnyPropertyOrFail(name);
        Collection<Annotation> annotes = cp.getAnnotationsCollection();
        Set<Annotation> s = new HashSet<>(annotes);
        return s;
    }

    /**
     * @param annotationType
     *            the annotation type to look for on this field
     * @param name the fieldName
     * @return the annotation if found OR null if none found
     * @throws FieldnameNotFoundException if this fieldName is invalid
     */
    @SuppressWarnings("hiding")
    public <T extends Annotation> T getFieldAnnotation(Class<T> annotationType, String name) {
        if (annotationType == null)
            throw new IllegalArgumentException("annotationType must not be null");
        ClassProperty cp = this.getAnyPropertyOrFail(name);
        T a = cp.getAnnotation(annotationType);
        return a;
    }

    /**
     * Will find the first field which has this annotation type
     * 
     * @param annotationType
     *            the annotation type to look for on this field
     * @return the name of the field which has this annotation OR null if none found
     */
    public String getFieldNameByAnnotation(Class<? extends Annotation> annotationType) {
        if (annotationType == null)
            throw new IllegalArgumentException("annotationType must not be null");
        String fieldName = null;
        Collection<ClassProperty> cps = this.namesToProperties.values();
        for (ClassProperty classProperty : cps) {
            if (classProperty.getAnnotation(annotationType) != null) {
                fieldName = classProperty.getFieldName();
                break;
            }
        }
        return fieldName;
    }

    /**
     * Finds the names of all the fields with the given annotation type
     * @param annotationType
     *            the annotation type to look for on this field
     * @return the list of all field names sorted OR empty if none found
     */
    public List<String> getFieldNamesWithAnnotation(Class<? extends Annotation> annotationType) {
        if (annotationType == null)
            throw new IllegalArgumentException("annotationType must not be null");

        HashSet<String> fieldNames = new HashSet<>();
        Collection<ClassProperty> cps = this.namesToProperties.values();
        for (ClassProperty classProperty : cps) {
            if (classProperty.getAnnotation(annotationType) != null)
                fieldNames.add( classProperty.getFieldName() );
        }
        ArrayList<String> l = new ArrayList<>(fieldNames);
        Collections.sort(l);
        return l;
    }

    // SPECIAL methods

    /**
     * SPECIAL METHOD: accesses the internal data<br/>
     * Returns a field property object for any field (not limited to only complete fields)
     * 
     * @param name the fieldName
     * @return the Property object which holds information about a field
     * @throws FieldnameNotFoundException if this fieldName is invalid
     */
    public ClassProperty getClassProperty(String name)
    {
        ClassProperty cp = this.getAnyPropertyOrFail(name);
        return cp;
    }

    /**
     * SPECIAL METHOD: accesses the internal data<br/>
     * Gets all the cached class properties objects related to this class<br/>
     * WARNING: Special method which allows access to the internal properties storage structure,
     * this should really only be used for filtering results in a way that is not supported by the
     * standard class fields methods
     * 
     * @return the complete set of field properties objects for this class
     */
    public Map<String, ClassProperty> getAllClassProperties() {
        return this.namesToProperties;
    }

    /**
     * SPECIAL METHOD: accesses the internal data<br/>
     * Gets the internal cache object for the class which holds all fields, methods, constructors, and annotations
     * @return the class data cache object
     */
    public ClassData<T> getClassData() {
        return this.classData;
    }


    // PRIVATE analyzer methods

    /**
     * @param name
     * @return
     */
    private ClassProperty getAnyPropertyOrFail(String name) {
        ClassProperty cp = this.namesToProperties.get(name);
        if (cp == null) {
            throw new FieldnameNotFoundException(name);
        }
        return cp;
    }

    private Class<T> getStoredClass() {
        return this.classData.getType();
    }

    /**
     * Determines if a {@link ClassProperty} is complete based on the {@link FieldFindMode} setting
     * @param cp a class property setting
     * @return true if it is complete, false otherwise
     */
    private boolean isComplete(ClassProperty cp) {
        boolean complete = false;
        if (FieldFindMode.FIELD.equals(fieldFindMode)) {
            if (cp.isField()) {
                complete = true;
            }
        } else if (FieldFindMode.PROPERTY.equals(fieldFindMode)) {
            if (cp.isProperty()) {
                complete = true;
            }
        } else {
            // default
            if (cp.isComplete()) {
                complete = true;
            }
        }
        return complete;
    }

    /**
     * Determines if a {@link ClassProperty} is gettable based on the {@link FieldFindMode} setting
     * @param cp a class property setting
     * @return true if it is gettable, false otherwise
     */
    private boolean isGettable(ClassProperty cp) {
        boolean gettable = false;
        if (FieldFindMode.FIELD.equals(fieldFindMode)) {
            if (cp.isField() && cp.isGettable()) {
                gettable = true;
            }
        } else if (FieldFindMode.PROPERTY.equals(fieldFindMode)) {
            if (cp.isProperty() && cp.isGettable()) {
                gettable = true;
            }
        } else {
            // default
            if (cp.isPublicGettable()) {
                gettable = true;
            }
        }
        return gettable;
    }

    /**
     * Determines if a {@link ClassProperty} is settable based on the {@link FieldFindMode} setting
     * @param cp a class property setting
     * @return true if it is settable, false otherwise
     */
    private boolean isSettable(ClassProperty cp) {
        boolean settable = false;
        if (FieldFindMode.FIELD.equals(fieldFindMode)) {
            if (cp.isField() && cp.isSettable()) {
                settable = true;
            }
        } else if (FieldFindMode.PROPERTY.equals(fieldFindMode)) {
            if (cp.isProperty() && cp.isSettable()) {
                settable = true;
            }
        } else {
            // default
            if (cp.isPublicSettable()) {
                settable = true;
            }
        }
        return settable;
    }

    /**
     * Populate the internal storage from an array of descriptors
     * 
     * @param descriptors
     */
    private void populateProperties(PropertyDescriptor[] descriptors) {
        for (PropertyDescriptor pd : descriptors) {
            String fieldName = pd.getName();
            if (!namesToProperties.containsKey(fieldName)) {
                Method getter = pd.getReadMethod();
                Method setter = pd.getWriteMethod();
                if (getter != null && setter != null) {
                    ClassProperty p = null;
                    if (pd instanceof IndexedPropertyDescriptor) {
                        IndexedPropertyDescriptor ipd = (IndexedPropertyDescriptor) pd;
                        // Class<?> iType = ipd.getIndexedPropertyType();
                        Method iGetter = ipd.getIndexedReadMethod();
                        Method iSetter = ipd.getIndexedWriteMethod();
                        if (iGetter != null && iSetter != null) {
                            p = new ClassProperty.IndexedProperty(fieldName, getter, setter, iGetter, iSetter);
                        }
                    }
                    if (p == null) {
                        p = new ClassProperty(fieldName, getter, setter);
                    }
                    this.namesToProperties.put(fieldName, p);
                }
            }
        }
    }

    /**
     * Checks a getter and setter pair and returns the proper property name
     * 
     * @param getter
     * @param setter
     * @return the property name for this getter and setter pair
     */
    private String checkPropertyMethods(Method getter, Method setter) {
        String name = null;
        String gName = getter.getName();
        Class<?> gType = null;
        Class<?>[] paramTypes = getter.getParameterTypes();
        if (paramTypes.length == 0) {
            if (ClassFields.isGetClassMethod(getter)) {
                throw new GetClassMethodException();
            } else if (gName.startsWith(PREFIX_GET) || gName.startsWith(PREFIX_IS)) {
                gType = getter.getReturnType();
                if (gType != null) {
                    name = ClassFields.makeFieldNameFromMethod(gName);
                }
            }
        }
        if (name != null) {
            boolean validSetter = false;
            String sName = setter.getName();
            Class<?> sType;
            if (sName.startsWith(PREFIX_SET)) {
                Class<?>[] sParamTypes = setter.getParameterTypes();
                if (sParamTypes.length == 1) {
                    sType = sParamTypes[0];
                    if (sType == null || !sType.isAssignableFrom(gType)) {
                        throw new IllegalArgumentException("setter (" + sName + ") type (" + sType
                                + ") and " + "getter (" + gName + ") type (" + gType
                                + ") do not match, they MUST match");
                    } else {
                        validSetter = true;
                    }
                }
            }
            if (!validSetter) {
                throw new IllegalArgumentException("setter (" + sName
                        + ") does not appear to be a valid setter method "
                        + "(single param, name starts with set)");
            }
        } else {
            throw new IllegalArgumentException("getter (" + gName
                    + ") does not appear to be a valid getter method "
                    + "(0 params, name starts with get or is)");
        }
        return name;
    }

    /**
     * Simple manual method for walking over a class and finding the bean properties, generally used
     * because the sun Introspector is so slow, note that this will also return all the non-matching
     * getters or setters if requested
     * 
     * @param includePartial
     *            if true then includes getters and setters that do not match, if false then ONLY
     *            include the getters and setters which match exactly
     * @return a list Property objects
     */
    @SuppressWarnings("SameParameterValue")
    private List<ClassProperty> findProperties(boolean includePartial) {
        // find the property methods from all public methods
        Map<String, ClassProperty> propertyMap = new ArrayOrderedMap<String, ClassProperty>();
        for (Method method : this.classData.getPublicMethods()) {
            Class<?>[] paramTypes = method.getParameterTypes();
            String name = method.getName();
            if (! this.includeStaticFields && isStatic(method)) {
                continue; // skip statics
            } else if (ClassFields.isGetClassMethod(method) && ! this.includeClassField) {
                continue; // skip class field
            } else if (paramTypes.length == 0) {
                // no params
                if (name.startsWith(PREFIX_GET) || name.startsWith(PREFIX_IS)) {
                    // getter
                    Class<?> returnType = method.getReturnType();
                    if (returnType != null) {
                        try {
                            String fieldName = ClassFields.makeFieldNameFromMethod(method.getName());
                            ClassProperty p;
                            if (propertyMap.containsKey(fieldName)) {
                                p = propertyMap.get(fieldName);
                                p.setGetter(method);
                            } else {
                                p = new ClassProperty(fieldName, method, null);
                                propertyMap.put(fieldName, p);
                            }
                        } catch (Exception e) {
                            // nothing to do here but move on
                        }
                    }
                }
            } else if (paramTypes.length == 1) {
                if (name.startsWith(PREFIX_SET)) {
                    // setter (one param)
                    String fieldName = ClassFields.makeFieldNameFromMethod(method.getName());
                    ClassProperty p;
                    if (propertyMap.containsKey(fieldName)) {
                        p = propertyMap.get(fieldName);
                        p.setSetter(method);
                    } else {
                        p = new ClassProperty(fieldName, null, method);
                        propertyMap.put(fieldName, p);
                    }
                }
            }
        }
        List<ClassProperty> properties = new ArrayList<ClassProperty>();
        for (Entry<String, ClassProperty> entry : propertyMap.entrySet()) {
            ClassProperty p = entry.getValue();
            if (p != null) {
                if (! this.includeStaticFields && p.isStatic()) {
                    continue; // skip any props with static fields
                }
                if (this.ignoredFieldNames.contains(p.getFieldName())) {
                    continue; // skip ignored names
                }
                if (includePartial) {
                    // put all of them in
                    properties.add(p);
                } else {
                    // filter down to only the one which are complete
                    if (p.isGettable() && p.isSettable()) {
                        properties.add(p);
                    }
                }
            }
        }
        return properties;
    }

    /**
     * Populates the fields from the class data object
     * @param includeNonPublic if true then all fields will be used, if false then only public fields
     */
    private void populateFields(boolean includeNonPublic) {
        // set the fields
        List<Field> fields;
        if (includeNonPublic) {
            fields = this.classData.getFields();
        } else {
            fields = this.classData.getPublicFields();
        }
        for (Field field : fields) {
            if (! this.includeStaticFields && isStatic(field)) {
                continue; // skip statics
            }
            String fieldName = field.getName();
            if (this.ignoredFieldNames.contains(fieldName)) {
                continue; // skip ignored names
            }
            if (! this.namesToProperties.containsKey(fieldName)) {
                ClassProperty p = new ClassProperty(fieldName, field);
                this.namesToProperties.put(fieldName, p);
            }
        }
    }

    /**
     * @param member any class member
     * @return true if static, false otherwise
     */
    private boolean isStatic(Member member) {
        if ( Modifier.isStatic(member.getModifiers()) ) {
            return true;
        }
        return false;
    }

    /**
     * @param publicFields
     *            takes an array of fields and populates the internal storage mechanisms
     */
    private void populateFields(Field[] publicFields) {
        // set the fields
        for (Field publicField : publicFields) {
            if (publicField != null) {
                String fieldName = publicField.getName();
                if (!this.namesToProperties.containsKey(fieldName)) {
                    ClassProperty p = new ClassProperty(fieldName, publicField);
                    this.namesToProperties.put(fieldName, p);
                }
            }
        }
    }

    /**
     * We need to find all the class, method, and field annotations and not just the ones on public
     * methods so we are going through everything on this class and all the super classes<br/>
     * WARNING: this is NOT cheap and the results need to be cached badly<br/>
     * NOTE: This also populates all the fields in every property and checks and removes
     * all statics or excluded fields if any slipped through the previous processes
     */
    private void populateAnnotationsFields() {
        // get the annotations from all class methods
        for (Method method : this.classData.getMethods()) {
            int paramCount = method.getParameterTypes().length;
            // only include annotations on methods which *might* be fields (no args, 1 arg, or 2 args for the special ones)
            if (paramCount <= 2) {
                try {
                    String name = ClassFields.makeFieldNameFromMethod(method.getName());
                    ClassProperty cp = getAnyPropertyOrFail(name);
                    Annotation[] annotations = method.getAnnotations();
                    for (Annotation annotation : annotations) {
                        if (annotation != null) cp.addAnnotation(annotation);
                    }
                } catch (FieldnameNotFoundException e) {
                    // nothing to do but keep going
                }
            }
        }
        // get the annotations from all class fields
        for (Field field : this.classData.getFields()) {
            try {
                String fieldName = field.getName();
                if (! this.includeStaticFields && this.isStatic(field)) {
                    this.namesToProperties.remove(fieldName); // as a final check, take this out if it is in there
                    continue; // skip statics
                }
                if (this.ignoredFieldNames.contains(fieldName)) {
                    this.namesToProperties.remove(fieldName); // as a final check, take this out if it is in there
                    continue; // skip ignored names
                }
                ClassProperty cp = this.getAnyPropertyOrFail(fieldName);
                if (cp.getField() == null) {
                    cp.setField(field); // ensure there is a field set for all methods
                }
                Annotation[] annotations = field.getAnnotations();
                for (Annotation annotation : annotations) {
                    if (annotation != null) cp.addAnnotation(annotation);
                }
            } catch (FieldnameNotFoundException e) {
                // nothing to do but keep going
            }
        }
        // now mark the transient fields as such
        for (String fieldName : this.transientFieldNames) {
            try {
                ClassProperty cp = getAnyPropertyOrFail(fieldName);
                cp.transientField = true;
            } catch (FieldnameNotFoundException e) {
                // nothing to do but keep going
            }
        }
    }

    @Override
    public String toString() {
        return "(" + getStoredClass().getName() + "):mode="+fieldFindMode.name()+":" + getFieldNames(FieldsFilter.ALL);
    }

    // STATIC methods

    /**
     * @param methodName
     *            a getter or setter style method name (e.g. getThing, setStuff, isType)
     * @return the fieldName equivalent (thing, stuff, type)
     */
    public static String makeFieldNameFromMethod(String methodName) {
        String name;
        if (methodName.startsWith(PREFIX_IS)) {
            name = ClassFields.unCapitalize(methodName.substring(2));
        } else if (methodName.startsWith(PREFIX_GET) || methodName.startsWith(PREFIX_SET)) {
            // set or get
            name = ClassFields.unCapitalize(methodName.substring(3));
        } else {
            name = methodName;
        }
        return name;
    }

    /**
     * Capitalize a string
     * 
     * @param input
     *            any string
     * @return the string capitalized (e.g. myString -> MyString)
     */
    public static String capitalize(String input) {
        String cap;
        if (input.length() == 0) {
            cap = "";
        } else {
            char first = Character.toUpperCase(input.charAt(0));
            if (input.length() == 1) {
                cap = first + "";
            } else {
                cap = first + input.substring(1);
            }
        }
        return cap;
    }

    /**
     * undo string capitalization
     * 
     * @param input
     *            any string
     * @return the string uncapitalized (e.g. MyString -> myString)
     */
    public static String unCapitalize(String input) {
        String cap;
        if (input.length() == 0) {
            cap = "";
        } else {
            char first = Character.toLowerCase(input.charAt(0));
            if (input.length() == 1) {
                cap = first + "";
            } else {
                cap = first + input.substring(1);
            }
        }
        return cap;
    }

    /**
     * @param method
     *            any method
     * @return true if this is the getClass() method for standard objects
     */
    public static boolean isGetClassMethod(Method method) {
        boolean gc = false;
        if (method != null) {
            if (METHOD_GET_CLASS.equals(method.getName())) {
                gc = true;
            }
        }
        return gc;
    }

    // INNER classes

    /**
     * Indicates that this is the getter or setter for the class type and should be ignored
     */
    public static class GetClassMethodException extends RuntimeException {
        public GetClassMethodException() {
            super();
        }
    }
}
