package com.testanywhere.core.classes.support;

import com.testanywhere.core.utilities.class_support.BaseClass;
import com.testanywhere.core.classes.class_support.CompartmentObject;
import com.testanywhere.core.utilities.exceptions.ObjectCreationException;
import com.testanywhere.core.utilities.logging.*;

import java.io.InvalidClassException;
import java.lang.reflect.Method;

public class MethodPointer extends OutputDisplay
{
    private String methodName;
    private Method methodFromClass;
    private CompartmentObject<?>[] methodInputs;
    private CompartmentObject<?> methodOutputs;
    private CompartmentObject<?> objectWithMethod;

    public MethodPointer(final String mName) throws InvalidClassException
    {
        if ( ! TextManager.validString(mName) ) throw new InvalidClassException("Method name is not properly defined!");
        this.methodName = mName;
    }

    public MethodPointer(final Method m, final CompartmentObject<?> obj, final Class<?> output) throws InvalidClassException
    {
        if ( m == null ) throw new InvalidClassException("Method type is NULL!");
        if ( CompartmentObject.isNull(obj) ) throw new InvalidClassException("Object housing method call is NULL!");

        this.objectWithMethod = obj;
        this.methodFromClass = m;
        this.methodName = m.getName();

        Class<?>[] inputTypes = m.getParameterTypes();
        if ( inputTypes.length >= 1 )
        {
            this.methodInputs = new CompartmentObject<?>[inputTypes.length - 1];
            int cntidx = 0;
            // Need to use a factory to allow for dynamic instantiation of the object
            // by saving the class type and using that as the
            for (Class<?> c : inputTypes)
                try
                {
                    this.methodInputs[cntidx] = new CompartmentObject<>(Class.forName(inputTypes[cntidx].getName()).getConstructor(inputTypes[cntidx]).newInstance(inputTypes[cntidx], null));
                }
                catch (Exception e)
                {
                    this.error("Unable to instantiate input data compartment object for " + inputTypes[cntidx].getName());
                    throw new InvalidClassException("Unable to instantiate " + MethodPointer.class.getSimpleName());
                }
        }
        if ( ! output.getSimpleName().startsWith("void") )
            this.methodOutputs = new CompartmentObject<>(output, null);
    }

    public MethodPointer( final MethodPointer mp ) throws ObjectCreationException {
        this();
        if ( mp == null ) throw new ObjectCreationException(MethodPointer.class);

        this.methodFromClass = copy(mp.getMethod());
        this.methodName = mp.getMethod().getName();
        this.methodInputs = copy(mp.getMethodInputs());
        try
        {
            this.methodOutputs = mp.getMethodOutput().copy();
            this.objectWithMethod = mp.objectWithMethod.copy();
        }
        catch (CloneNotSupportedException e)
        {
            throw new ObjectCreationException(MethodPointer.class);
        }
    }

    @Override
    public void buildObjectOutput( int numTabs )
    {
        if ( numTabs < 0 ) numTabs = 0;
        Tabbing tabEnvironment = new Tabbing(numTabs);
        DisplayManager dm = this.getDM();

        String outerSpacer = tabEnvironment.getSpacer();
        dm.append(outerSpacer + "Method Pointer Object :", DisplayType.TEXTTYPES.LABEL);

        tabEnvironment.increment();
        String innerSpacer = tabEnvironment.getSpacer();
        dm.append(innerSpacer + "Method Name   : " + this.getMethodName());
        dm.append(innerSpacer + "Method Class  : " + this.getMethod().toString());

        if ( this.getMethodInputs().length > 0 )
        {
            dm.append(innerSpacer + "Method Inputs :", DisplayType.TEXTTYPES.LABEL);
            tabEnvironment.increment();
            for ( CompartmentObject<?> c : this.getMethodInputs() )
            {
                c.buildObjectOutput(tabEnvironment.numberTabs());
                dm.addFormatLines(c.getDM().getLines());
            }
            tabEnvironment.decrement();
        }

        if ( ! CompartmentObject.isNull(this.getMethodOutput()) )
        {
            dm.append(innerSpacer + "Method Output :", DisplayType.TEXTTYPES.LABEL);
            tabEnvironment.increment();
            this.getMethodOutput().buildObjectOutput(tabEnvironment.numberTabs());
            dm.addFormatLines(this.getMethodOutput().getDM().getLines());
            tabEnvironment.decrement();
        }
        else
            dm.append(innerSpacer + "Method Output : " + BaseClass.checkIsNull(this.getMethodOutput()), DisplayType.TEXTTYPES.LABEL);

        if ( this.objectWithMethod == null )
            dm.append(innerSpacer + "Associated Object : " + BaseClass.checkIsNull(this.objectWithMethod), DisplayType.TEXTTYPES.LABEL);
        else
        {
            dm.append(innerSpacer + "Associated Object : ", DisplayType.TEXTTYPES.LABEL);
            tabEnvironment.increment();
            this.objectWithMethod.buildObjectOutput(tabEnvironment.numberTabs());
            dm.addFormatLines(this.objectWithMethod.getDM().getLines());
            tabEnvironment.decrement();
        }
    }

    @Override
    public String toString()
    {
        return null;
    }

    @Override
    public boolean isNull() { return false; }

    public String getMethodName()
    {
        return this.methodName;
    }

    public void setMethod( final Method m )
    {
        if ( m == null ) return;
        this.methodFromClass = m;
    }

    public void setMethodInputs( final Class<?>[] inputClasses ) {
        if (inputClasses == null || inputClasses.length < 1) return;

        this.methodInputs = new CompartmentObject<?>[inputClasses.length - 1];
        int cntidx = 0;
        for (Class<?> c : inputClasses)
            try
            {
                this.methodInputs[cntidx] = new CompartmentObject<>(Class.forName(inputClasses[cntidx].getName()).getConstructor(inputClasses[cntidx]).newInstance(inputClasses[cntidx], null));
            }
            catch ( Exception e)
            {
                this.warn("Unable to instantiate CompartmentObject for class type " + inputClasses[cntidx].getName());
            }
    }

    public void setMethodInputs( final CompartmentObject<?>[] inputClasses )
    {
        if ( inputClasses == null || inputClasses.length < 1 ) return;
        // May need to revisit for purposes of providing a deep copy of Array
        // This may be a general utility method which is templated...
        this.methodInputs = inputClasses.clone();
    }

    public void setMethodOutput( final Class<?> outputClass )
    {
        if ( outputClass == null ) return;
        this.methodOutputs = new CompartmentObject<>(outputClass, null);
    }

    public void setMethodOutput( final CompartmentObject<?> outputClass )
    {
        if ( outputClass == null ) return;
        try {
            this.methodOutputs = outputClass.copy();
        } catch (CloneNotSupportedException ignored)
        {}
    }

    public Method getMethod()
    {
        return this.methodFromClass;
    }

    public Object getObject() { return this.objectWithMethod.getObject(); }

    public Class<?> getClassType() { return this.objectWithMethod.getClassType(); }

    public CompartmentObject<?>[] getMethodInputs()
    {
        return this.methodInputs;
    }

    public CompartmentObject<?> getMethodOutput()
    {
        return this.methodOutputs;
    }

    private MethodPointer() {}
}
