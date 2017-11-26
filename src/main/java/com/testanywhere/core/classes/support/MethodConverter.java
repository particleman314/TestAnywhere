package com.testanywhere.core.classes.support;

import com.testanywhere.core.utilities.class_support.BaseClass;
import com.testanywhere.core.utilities.class_support.Cast;
import com.testanywhere.core.classes.class_support.CompartmentObject;
import com.testanywhere.core.classes.class_support.ParameterizedObject;
import com.testanywhere.core.utilities.classes.Pair;
import com.testanywhere.core.utilities.exceptions.ObjectCreationException;
import com.testanywhere.core.utilities.logging.*;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

public class MethodConverter extends OutputDisplay
{
    private Pair<String, ParameterizedObject> methodInfo;
    private Method methodConverter;

    public MethodConverter()
    {
        super();
        this.initialize();
    }

    public MethodConverter(final String methodName)
    {
        this();
        this.methodInfo.setL(methodName);
    }

    public MethodConverter(final String methodName, final ParameterizedObject methodInputs)
    {
        this(methodName);
        this.methodInfo.setR(methodInputs);
    }

    public MethodConverter( final MethodConverter mc ) throws ObjectCreationException
    {
        this();
        if ( mc == null ) throw new ObjectCreationException(MethodConverter.class);

        try {
            this.methodInfo = mc.methodInfo.copy();
        } catch (CloneNotSupportedException e) {
            throw new ObjectCreationException(MethodConverter.class);
        }
        this.methodConverter = copy(mc.methodConverter);
    }

    @Override
    public boolean isNull()
    {
        return (this.methodConverter == null && this.methodInfo.isNull());
    }

    @Override
    public void buildObjectOutput(int numTabs)
    {
        if ( numTabs < 0 ) numTabs = 0;
        Tabbing tabEnvironment = new Tabbing(numTabs);
        DisplayManager dm = this.getDM();

        String outerSpacer = tabEnvironment.getSpacer();
        dm.append(outerSpacer + "Method Data Object :", DisplayType.TEXTTYPES.LABEL);

        tabEnvironment.increment();
        String innerSpacer = tabEnvironment.getSpacer();

        dm.append(innerSpacer + "Method Name   : " + BaseClass.checkIsNull(this.getMethodName()));
        dm.append(innerSpacer + "Method Exists : " + TextManager.StringRepOfBool(((this.methodConverter != null)), "yn"));

        this.methodInfo.buildObjectOutput(tabEnvironment.numberTabs());
        dm.addFormatLines(this.methodInfo.getDM().getLines());
    }

    public String getMethodName()
    {
        return this.methodInfo.first();
    }

    public ParameterizedObject getMethodParams()
    {
        if ( this.methodInfo.second() == null ) this.methodInfo.setR(new ParameterizedObject());
        return this.methodInfo.second();
    }

    public void addMethodParam( final CompartmentObject<?> input )
    {
        if (this.methodInfo.second() == null) this.methodInfo.setR(new ParameterizedObject());

        if ( input == null ) return;
        this.methodInfo.second().addPairing(input);
    }

    public void addMethodParam( final Type type, final Object obj )
    {
        if (this.methodInfo.second() == null) this.methodInfo.setR(new ParameterizedObject());

        if ( type == null ) return;
        this.methodInfo.second().addPairing(type, obj);
    }

    public void setConverter ( final Method m )
    {
        this.methodConverter = m;
        this.methodInfo.setL(m.getName());
    }

    public Pattern convert( final Object o )
    {
        if ( this.methodConverter == null ) return null;

        try
        {
            return Cast.cast(this.methodConverter.invoke(o, ParameterizedObject.getParameterizedObjectsAsArray(this.methodInfo.getR())));
        }
        catch (IllegalAccessException | InvocationTargetException e)
        {
            this.error("Unable to run converter method.  Exception raised : " + e);
            return null;
        }
    }

    public Class<?>[] getMethodDataClasses()
    {
        try
        {
            return this.getMethodData("getClassType");
        }
        catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e)
        {
            this.error("Unable to extract class data for method.  Exception raised : " + e);
            return null;
        }
     }

    public Object[] getMethodDataObjects()
    {
        try
        {
            return this.getMethodData("getObject");
        }
        catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e)
        {
            this.error("Unable to extract object data for method.  Exception raised : " + e);
            return null;
        }
    }

    private<T> T[] getMethodData( final String methodID ) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        Collection<T> lo = new ArrayList<>();

        if ( this.methodInfo.second() == null || ! TextManager.validString(methodID) ) return null;

        Class<?> clazz = null;

        for ( CompartmentObject<?> co : this.methodInfo.second().getParameterization() )
        {
            if ( co == null ) continue;
            Method m = co.getClass().getMethod(methodID, (Class<?>[]) null);

            T result = Cast.cast(m.invoke(co, (Object[]) null));
            lo.add(result);

            if ( ! lo.isEmpty() )
            {
                if (clazz != null && !clazz.equals(result.getClass()))
                    clazz = Object.class;
                else
                    clazz = result.getClass();
            }
        }

        if ( lo.isEmpty() ) return null;

        T[] array = (T[]) Array.newInstance(clazz, lo.size());
        if ( array != null )
            array = lo.toArray(array);

        return array;
    }

    private void initialize()
    {
        this.methodInfo = new Pair<>();
        this.methodConverter = null;
    }
}
