package com.nimsoft.class_support;

import com.nimsoft.logging.TextManager;

import java.lang.reflect.Type;

public class ClassWrapper {

    public static final boolean PRIMITIVE = true;
    public static final boolean WRAPPER = false;

    public static final int GENERIC_SUPERCLASS = 1;
    public static final int SUPERCLASS = 2;

    public static Boolean isBaseClassType( final Class<?> clazz )
    {
        return clazz.isPrimitive();
        //return ClassUtils.isPrimitiveWrapper(clazz) == ClassWrapper.WRAPPER;
    }

    public static Type getBaseClass( final Class<?> objClass, final int superClassType )
    {
        if ( objClass == null ) return null;
        if ( ClassWrapper.isBaseClassType(objClass) )
        {
            return objClass;
        }
        else
        {
            if ( superClassType == ClassWrapper.GENERIC_SUPERCLASS )
                return objClass.getGenericSuperclass();
            else
                return objClass.getSuperclass();
        }

    }
    public static Type getBaseClass( final Class<?> objClass )
    {
        return ClassWrapper.getBaseClass(objClass, ClassWrapper.SUPERCLASS );
    }

    public static Type convertToWrapperType( final Class<?> c )
    {
        switch ( c.getSimpleName().toLowerCase() )
        {
            case "int" :
                return Integer.class;
            case "double" :
                return Double.class;
            case "boolean" :
                return Boolean.class;
            case "long" :
                return Long.class;
            case "short" :
                return Short.class;
            case "string" :
                return String.class;
            case "byte" :
                return Byte.class;
            default :
                return c;
        }
    }

    public static Type convertFromWrapperType( final Class<?> c )
    {
        switch ( c.getSimpleName().toLowerCase() )
        {
            case "integer" :
                return int.class;
            case "double" :
                return double.class;
            case "boolean" :
                return boolean.class;
            case "long" :
                return long.class;
            case "short" :
                return short.class;
            case "byte" :
                return byte.class;
            default :
                return c;
        }
    }

    public static<T> Object instantiateWrapperObject( final T data, final Class<?> clazz )
    {
        if ( data == null ) return null;
        if ( ! TextManager.validString(clazz.getName(), null) ) return null;

        String dataAsStr = data.toString();
        switch ( clazz.getSimpleName().toLowerCase() )
        {
            case "integer" : case "int" :
                return Integer.parseInt(dataAsStr);
            case "double" :
                return Double.valueOf(dataAsStr);
            case "boolean" :
                return Boolean.valueOf(dataAsStr);
            case "long" :
                return Long.valueOf(dataAsStr);
            case "short" :
                return Short.valueOf(dataAsStr);
            case "byte" :
                return Byte.valueOf(dataAsStr);
            case "string"  :
                return dataAsStr;
            default:
                return data;
        }
    }
}
