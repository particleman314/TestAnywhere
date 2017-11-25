package com.testanywhere.core.utilities.class_support;

import com.testanywhere.core.utilities.Constants;
import com.testanywhere.core.utilities.class_support.functional_support.MethodFunctions;
import com.testanywhere.core.utilities.class_support.identification.IdGenerator;
import com.testanywhere.core.utilities.class_support.identification.IdHelper;
import com.testanywhere.core.utilities.classes.Pair;
import com.testanywhere.core.utilities.hash_support.HashCodeUtil;
import com.testanywhere.core.utilities.logging.LogConfiguration;
import com.testanywhere.core.utilities.logging.OutputDisplay;
import com.testanywhere.core.utilities.logging.TextManager;
import com.testanywhere.core.utilities.reflection.ReflectUtils;
import com.testanywhere.core.utilities.serialization.SerializeHelper;
import com.testanywhere.core.utilities.serialization.Serializer;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("unchecked")
public abstract class BaseClass extends Serializer implements CopyAssignSerialize, Externalizable
{
    protected static Logger logger;
    private static final SerializeHelper __skippedFields = new SerializeHelper();

    static
    {
        BaseClass.logger = Logger.getLogger("BaseClass");
        BaseClass.logger.setLevel(Level.INFO);

        BaseClass.__skippedFields.addSkipField("logger");
        BaseClass.__skippedFields.addSkipField("cloneStd");
        BaseClass.__skippedFields.addSkipField("id");
        BaseClass.__skippedFields.addSkipField("address");
        BaseClass.__skippedFields.addSkipField("dm");
        BaseClass.__skippedFields.addSkipField("__skippedFields");

        LogConfiguration.configure();
    }

    @Override
    public void writeExternal(final ObjectOutput out) throws IOException
    {
        Map<String, Pair<Field, Object>> composition = MethodFunctions.decomposeObject(this, BaseClass.__skippedFields.getSkipFields());
        for ( String fs : composition.keySet() )
        {
            BaseClass.logger.log(Level.DEBUG, "Serialization of field : " + TextManager.specializeName(fs));
            Object value = composition.get(fs).getR();
            out.writeObject(value);
        }
    }

    @Override
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException
    {
        Map<String, Pair<Field, Object>> composition = MethodFunctions.decomposeObject(this, BaseClass.__skippedFields.getSkipFields(), false);
        for ( String fs : composition.keySet() )
        {
            BaseClass.logger.log(Level.DEBUG, "Deserialization of field : " + TextManager.specializeName(fs));
            Object value = in.readObject();
            Pair<Field, Object> mapResult = composition.get(fs);

            ReflectUtils.getInstance().setFieldValue(this, mapResult.getL().getName(), value);
        }
    }

    @Override
    public boolean isCopyable()
    {
        return true;
    }

    @Override
    public boolean isAssignable()
    {
        return true;
    }

    @Override
    public boolean isSerializable()
    {
        return true;
    }

    public<T> T copy() throws CloneNotSupportedException
    {
        if ( ! this.isCopyable() ) throw new CloneNotSupportedException("Unable to clone/copy " + this.getClass());
        T dataCopy = (T) Serializer.cloneStd.deepClone(this);
        if ( dataCopy instanceof OutputDisplay )
        {
            OutputDisplay castedDataCopy = (OutputDisplay) dataCopy;
            castedDataCopy.setId(IdGenerator.generate());
            castedDataCopy.setAddress(Long.toHexString(IdGenerator.address(this)));
            IdHelper.account(castedDataCopy);
        }
        return dataCopy;
    }

    public static<T> T copy( T obj )
    {
        if ( obj == null ) return null;
        Class<?> objClazz = obj.getClass();
        try
        {
            Constructor m = objClazz.getDeclaredConstructor(objClazz);
            if (m == null) return null;
            try
            {
                return Cast.cast(m.newInstance(obj));
            }
            catch (InstantiationException | IllegalAccessException | InvocationTargetException e)
            {
                return BaseClass.__handleObjectCopy(obj);
            }
        }
        catch (NoSuchMethodException e)
        {
            return BaseClass.__handleObjectCopy(obj);
        }
    }

    public<T> void assign( T obj )
    {
        if ( obj == null ) return;
        if ( ! this.isAssignable() ) return;

        Map<String, Pair<Field, Object>> compositionThis = MethodFunctions.decomposeObject(this, BaseClass.__skippedFields.getSkipFields());
        Map<String, Pair<Field, Object>> compositionObj = MethodFunctions.decomposeObject(obj, BaseClass.__skippedFields.getSkipFields());

        for ( String fs : compositionObj.keySet() )
        {
            BaseClass.logger.log(Level.DEBUG, "Assignment of field : " + TextManager.specializeName(fs));
            Object value = compositionObj.get(fs).getR();

            if ( compositionThis.containsKey(fs) ) {
                Pair<Field, Object> mapResult = compositionThis.get(fs);
                ReflectUtils.getInstance().setFieldValue(this, mapResult.getL().getName(), value);
            }
            else
                BaseClass.logger.log(Level.WARN, "Unable to find field : " + TextManager.specializeName(fs) + " on destination object");
        }
    }

    @Override
    public int hashCode()
    {
        int result = HashCodeUtil.SEED;

        Map<String, Pair<Field, Object>> composition = MethodFunctions.decomposeObject(this, BaseClass.__skippedFields.getSkipFields());
        if ( composition.isEmpty() ) return ( result - HashCodeUtil.SEED );

        for ( String fs : composition.keySet() )
        {
            Object value = composition.get(fs).getR();
            result += HashCodeUtil.hash(result, value);
            BaseClass.logger.log(Level.DEBUG, "Hash code of field : " + TextManager.specializeName(fs));
        }

        return result;
    }

    public abstract boolean isNull();

    public static SerializeHelper getSerializeHelper() { return BaseClass.__skippedFields; }

    public static<T> String checkIsNull( T obj )
    {
        if ( obj == null ) return TextManager.specializeName(Constants.nullRep);
        if ( obj instanceof BaseClass )
        {
            BaseClass bc = (BaseClass) obj;
            if ( bc.isNull() )
                return TextManager.specializeName(Constants.nullRep);
        }
        return obj.toString();
    }

    private static<T> T __handleObjectCopy( T obj )
    {
        T dataCopy = Cast.cast(Serializer.cloneStd.deepClone(obj));
        if ( dataCopy instanceof OutputDisplay )
        {
            OutputDisplay castedDataCopy = (OutputDisplay) dataCopy;
            OutputDisplay castedData = (OutputDisplay) obj;
            if ( castedDataCopy.id() == castedData.id() && !Objects.equals(castedDataCopy.address(), castedData.address()))
                castedDataCopy.setId(IdGenerator.generate());
        }
        return dataCopy;
    }
}
