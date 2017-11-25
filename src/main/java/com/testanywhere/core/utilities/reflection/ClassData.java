package com.testanywhere.core.utilities.reflection;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;

/**
 * @param <T> the class type
 * A class which retrieves and holds all the reflected class data related to a class 
 * (annotations, constructors, fields, methods), this is immutable<br/>
 * WARNING: this is NOT cheap and the results need to be cached badly,
 * you should get this from the {@link ClassDataCacher} rather than constructing it yourself<br/>
 */
public class ClassData<T>
{
    private final Class<T> type;
    private final List<Annotation> annotations;
    private final List<Constructor<T>> constructors;
    private final List<Field> fields;
    private final List<Method> methods;
    private final List<Class<?>> interfaces;
    private final List<Class<?>> superclasses;
    private final List<T> enumConstants;

    public ClassData(Class<T> type)
    {
        if (type == null) throw new IllegalArgumentException("ClassData requires a valid class, type is null");
        if (type.isPrimitive() || type.isArray()) throw new IllegalArgumentException("Invalid type to make reflection cache for ("+type.getName()+"), cannot reflect over primitives or arrays");
        this.type = type;

        T[] ecs = type.getEnumConstants();
        if (ecs != null)
        {
            this.enumConstants = new ArrayList<T>(ecs.length);
            for (T t : ecs) this.enumConstants.add(t);
        }
        else
        {
            this.enumConstants = new ArrayList<T>(0);
        }

        this.annotations = new ArrayList<>(5);
        this.getAllAnnotations(type, this.annotations);
        this.constructors = new ArrayList<>(5);
        this.fields = new ArrayList<>(5);
        this.methods = new ArrayList<>(5);
        this.interfaces = new ArrayList<>(5);
        this.superclasses = new ArrayList<>(5);

        this.getAllThings(type, this.fields, this.methods, this.constructors, this.interfaces, this.superclasses);

        // sort the fields,methods,constructors
        Collections.sort(this.fields, new MemberComparator());
        Collections.sort(this.methods, new MemberComparator());
        Collections.sort(this.constructors, new MemberComparator());

        // remove duplicates from the list of interfaces
        ArrayUtils.removeDuplicates(this.interfaces);
    }

    /**
     * @return only the public constructors in the class this data represents
     */
    public List<Constructor<T>> getPublicConstructors()
    {
        List<Constructor<T>> pubConstructors = new ArrayList<>();
        for (Constructor<T> constructor : this.constructors)
        {
            if (Modifier.isPublic(constructor.getModifiers())) pubConstructors.add(constructor);
        }
        return pubConstructors;
    }

    /**
     * @return only the public fields in the class this data represents
     */
    public List<Field> getPublicFields()
    {
        List<Field> pubFields = new ArrayList<>();
        for (Field field : this.fields)
        {
            if (Modifier.isPublic(field.getModifiers())) pubFields.add(field);
        }
        return pubFields;
    }

    /**
     * @return only the public methods for the class this data represents
     */
    public List<Method> getPublicMethods()
    {
        List<Method> pubMethods = new ArrayList<>();
        for (Method method : this.methods)
        {
            if (Modifier.isPublic(method.getModifiers())) pubMethods.add(method);
        }
        return pubMethods;
    }

    private void getAllAnnotations(final Class<?> type, final Collection<Annotation> list)
    {
        // get only public annotations
        Annotation[] annotations = type.getAnnotations();
        for (Annotation annotation : annotations)
        {
            if (annotation != null) list.add(annotation);
        }
    }

    @SuppressWarnings("unchecked")
    private void getAllThings(final Class<?> type, final Collection<Field> fList,
                              final Collection<Method> mList, final Collection<Constructor<T>> cList,
                              final Collection<Class<?>> iList, final Collection<Class<?>> sList) {
        // get the fields for the current class
        Field[] fields = type.getDeclaredFields();
        for (final Field field : fields)
        {
            if (field != null)
            {
                int modifiers = field.getModifiers();
                if (Modifier.isPublic(modifiers)) fList.add(field);
                else
                {
                    try
                    {
                        AccessController.doPrivileged(new PrivilegedAction<T>() {
                            public T run() {
                                field.setAccessible(true);
                                return null;
                            }
                        });
                        fList.add(field);
                    }
                    catch (SecurityException e)
                    {
                        // oh well, this does not get added then
                    }
                }
            }
        }

        Method[] methods = type.getDeclaredMethods();
        for (final Method method : methods)
        {
            if (method != null) {
                int modifiers = method.getModifiers();
                if (Modifier.isPublic(modifiers)) mList.add(method);
                else
                {
                    try
                    {
                        AccessController.doPrivileged(new PrivilegedAction<T>() {
                            public T run() {
                                method.setAccessible(true);
                                return null;
                            }
                        });
                        mList.add(method);
                    }
                    catch (SecurityException e)
                    {
                        // oh well, this does not get added then
                    }
                }
            }
        }
        Constructor<?>[] constructors = type.getDeclaredConstructors();
        for (final Constructor<?> constructor : constructors)
        {
            // need to avoid the Object constructor
            if (!Object.class.equals(type) && constructor != null)
            {
                int modifiers = constructor.getModifiers();
                if (Modifier.isPublic(modifiers))
                    cList.add((Constructor<T>)constructor);
                else
                {
                    try
                    {
                        AccessController.doPrivileged(new PrivilegedAction<T>() {
                            public T run() {
                                constructor.setAccessible(true);
                                return null;
                            }
                        });
                        cList.add((Constructor<T>)constructor);
                    }
                    catch (SecurityException e)
                    {
                        // oh well, this does not get added then
                    }
                }
            }
        }
        // now we recursively go through the interfaces and super classes
        Class<?>[] interfaces = type.getInterfaces();
        for (Class<?> iface : interfaces) iList.add(iface); // add to the interfaces list

        for (int i = 0; i < interfaces.length; i++) this.getAllThings(interfaces[i], fList, mList, cList, iList, sList);

        Class<?> superClass = type.getSuperclass();

        if (superClass != null)
        {
            sList.add(superClass); // add to superclasses list
            this.getAllThings(superClass, fList, mList, cList, iList, sList);
        }
    }

    /**
     * @return the type of the class this data represents
     */
    public Class<T> getType() {
        return this.type;
    }

    /**
     * @return the annotations on the class this data represents
     */
    public List<Annotation> getAnnotations() {
        return this.annotations;
    }

    /**
     * @return the constructors for the class this data represents
     */
    public List<Constructor<T>> getConstructors() {
        return this.constructors;
    }

    /**
     * @return all fields in the class this data represents
     */
    public List<Field> getFields() {
        return this.fields;
    }

    /**
     * @return all methods for the class this data represents
     */
    public List<Method> getMethods() {
        return this.methods;
    }

    /**
     * @return all interfaces for the class this data represents
     */
    public List<Class<?>> getInterfaces() {
        return this.interfaces;
    }

    /**
     * @return all superclasses (extends) for the class this data represents
     */
    public List<Class<?>> getSuperclasses() {
        return this.superclasses;
    }

    /**
     * @return the list of all enum constants (these are the actual enum statics for this enum) OR empty if this is not an enum
     */
    public List<T> getEnumConstants() {
        return this.enumConstants;
    }

    /**
     * Sorts the members by visibility and name order
     */
    public static final class MemberComparator implements Comparator<Member>, Serializable
    {
        public static final long serialVersionUID = 1l;
        public int compare(Member o1, Member o2)
        {
            String c1 = ClassData.getModifierPrefix(o1.getModifiers()) + o1.getName();
            String c2 = ClassData.getModifierPrefix(o2.getModifiers()) + o2.getName();
            return c1.compareTo(c2);
        }
    }

    public static final String getModifierPrefix(int modifier)
    {
        String prefix = "0public-";
        if (Modifier.isProtected(modifier)) prefix = "1protected-";
        else if (Modifier.isPrivate(modifier)) prefix = "2private-";
        return prefix;
    }
}

