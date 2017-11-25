package com.nimsoft.class_support;

import com.nimsoft.logging.LogConfiguration;
import com.nimsoft.logging.TextManager;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;

/**
 * <h1>Cast class:</h1>
 * <p>
 * Class to perform casting and conversion from one data type to another
 * based on inputs.  These methods are templated.  IDEs will use this
 * information to provide feedback if inappropriate usage is attemped.
 * <p>
 *
 * @author  Mike Klusman III
 * @version 0.5
 * @since   2015-09-01
 */

@SuppressWarnings("unchecked")
public class Cast
{
    // Logger object for the Cast class
    public static Logger logger;
    private static boolean disableWarnings;

    // Define the Logger object and attempt to ensure the LogConfiguration system is initialized
    static
    {
        Cast.logger = Logger.getLogger("Cast");
        LogConfiguration.configure();
        Cast.disableWarnings = false;
    }

    // allow conversion of input to output using two arguments

    /**
     * Method : convert
     *
     * Allow conversion of an input of type <S> to type <T> where the class type
     * of <T> is passed in as a parameter.
     *
     * @param input data to be converted
     * @param outputClass class type for the output conversion of the data
     * @return <T> data converted to type specified
     * @throws ClassCastException
     */
    @SuppressWarnings("unchecked")
    public static<S,T> T convert( final S input, final Class<?> outputClass ) throws ClassCastException
    {
        try
        {
            return (T) (input == null ? null : outputClass.getConstructor(String.class).newInstance(input.toString()));
        }
        catch ( NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e )
        {
            Cast.logger.warn("Unable to convert input " + TextManager.specializeName(input.toString()));
            throw new ClassCastException(e.getLocalizedMessage());
        }
    }

    /**
     * Method : convert
     *
     * Allow conversion of an input of type <S> to type <T> where the class type
     * of <T> is passed in as a parameter.
     *
     * @param input data to be converted
     * @param outputObj Generic class type for the output conversion of the data
     * @return <T> data converted to type specified
     * @throws ClassCastException
     */
    @SuppressWarnings("unchecked")
    public static<S,T> T convert( final S input, final Generic<T> outputObj)  throws ClassCastException
    {
        try
        {
            return (T) (input == null ? null : outputObj.getClassType().getConstructor(String.class).newInstance(input.toString()));
        }
        catch ( NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e )
        {
            Cast.logger.warn("Unable to convert input " + TextManager.specializeName(input.toString()));
            throw new ClassCastException(e.getLocalizedMessage());
        }
    }

    /**
     * Method : safeCast
     *
     * Allow casting of an input of type <T> from an object
     *
     * @param o data to be converted
     * @param clazz class type for the output conversion of the data
     * @return casted object to requested class or null
     */
    public static <T> T safeCast( final Object o, final Class<T> clazz )
    {
        Cast.logger.debug("Class type of object " + o.getClass() + " --> " + ClassWrapper.isBaseClassType(o.getClass()));
        try
        {
            if ( ClassWrapper.isBaseClassType(o.getClass()) != ClassWrapper.PRIMITIVE ) {
                Cast.logger.debug("Found object as wrapper class --> " + o.getClass() + " to convert to " + clazz);
                return (T) ClassWrapper.instantiateWrapperObject(o, clazz);
            }
            else
                return clazz.cast(o);
        }
        catch( ClassCastException | NumberFormatException e )
        {
            if ( ! Cast.disableWarnings ) {
                Cast.logger.warn("Unable to safely cast input object type " + TextManager.specializeName(o.getClass().toString()) + " to type " + TextManager.specializeName(clazz.getName()));
                Cast.logger.error(e);
            }
            return null;
        }
    }

    /**
     * Method : cast
     *
     * Allow casting of an input of type <S> which can be an extension of <T> of an object
     *
     * @param toCast
     * @return casted object to requested class which can be extend the original type or null
     */
    @SuppressWarnings("unchecked")
    public static <S,T extends S> T cast( final S toCast )
    {
        try
        {
            return (T) toCast;
        }
        catch ( Exception e )
        {
            if ( ! Cast.disableWarnings ) {
                Cast.logger.warn("Unable to safely cast input object");
                Cast.logger.warn(e);
            }
            return null;
        }
    }

    public static void disableWarnings()
    {
        Cast.disableWarnings = true;
    }

    public static void enableWarnings()
    {
        Cast.disableWarnings = false;
    }

    private Cast() {}
}
