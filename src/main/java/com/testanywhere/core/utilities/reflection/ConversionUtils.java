package com.testanywhere.core.utilities.reflection;

import java.io.File;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;

import com.testanywhere.core.utilities.logging.LogConfiguration;
import com.testanywhere.core.utilities.reflection.converters.variable.ArrayConverter;
import com.testanywhere.core.utilities.reflection.converters.BigDecimalConverter;
import com.testanywhere.core.utilities.reflection.converters.BigIntegerConverter;
import com.testanywhere.core.utilities.reflection.converters.BooleanConverter;
import com.testanywhere.core.utilities.reflection.converters.ByteConverter;
import com.testanywhere.core.utilities.reflection.converters.CalendarConverter;
import com.testanywhere.core.utilities.reflection.converters.CharacterConverter;
import com.testanywhere.core.utilities.reflection.converters.ClassConverter;
import com.testanywhere.core.utilities.reflection.converters.CollectionConverter;
import com.testanywhere.core.utilities.reflection.converters.DateConverter;
import com.testanywhere.core.utilities.reflection.converters.DoubleConverter;
import com.testanywhere.core.utilities.reflection.converters.EnumConverter;
import com.testanywhere.core.utilities.reflection.converters.FileConverter;
import com.testanywhere.core.utilities.reflection.converters.FloatConverter;
import com.testanywhere.core.utilities.reflection.converters.IntegerConverter;
import com.testanywhere.core.utilities.reflection.converters.LongConverter;
import com.testanywhere.core.utilities.reflection.converters.MapConverter;
import com.testanywhere.core.utilities.reflection.converters.NumberConverter;
import com.testanywhere.core.utilities.reflection.converters.SQLDateConverter;
import com.testanywhere.core.utilities.reflection.converters.SQLTimeConverter;
import com.testanywhere.core.utilities.reflection.converters.variable.ScalarConverter;
import com.testanywhere.core.utilities.reflection.converters.ShortConverter;
import com.testanywhere.core.utilities.reflection.converters.StringConverter;
import com.testanywhere.core.utilities.reflection.converters.TimestampConverter;
import com.testanywhere.core.utilities.reflection.converters.URLConverter;
import com.testanywhere.core.utilities.reflection.converters.api.Converter;
import com.testanywhere.core.utilities.reflection.converters.api.InterfaceConverter;
import com.testanywhere.core.utilities.reflection.converters.api.VariableConverter;
import org.apache.log4j.Logger;

public class ConversionUtils
{
    private static Logger logger;
    private Map<Class<?>, Converter<?>> converters;
    private Collection<VariableConverter> variableConverters;

    // This ensures it is initialized when import by any class
    static
    {
        LogConfiguration.configure();
        ConversionUtils.logger = Logger.getLogger(ConversionUtils.class);
        ConversionUtils.getInstance();
    }

    // This makes for a singleton object to keep the only copy
    private static boolean isInitialized = false;

    public static ConversionUtils getInstance()
    {
        if ( ! ConversionUtils.isInitialized ) {
            ConversionUtils.isInitialized = true;
            ConversionUtils.initialize();
        }
        return ConversionUtilsHolder.INSTANCE;
    }

    private static class ConversionUtilsHolder
    {
        public static final ConversionUtils INSTANCE = new ConversionUtils();
    }

    private static void initialize() {}

    /**
     * Empty constructor
     */
    private ConversionUtils()
    {
        super();

        this.converters = new HashMap<>();
        this.variableConverters = new ArrayList<>();

        this.loadDefaultConverters();
        this.loadDefaultVariableConverters();
    }

    /**
     * Constructor which allows adding to the initial set of converters
     * <br/>
     * <b>WARNING:</b> if you don't need this control then just use the {@link #getInstance()} method to get this
     * 
     * @param converters a map of converters to add to the default set
     */
    public ConversionUtils(Map<Class<?>, Converter<?>> converters)
    {
        this();
        this.setConverters(converters);
    }

    /**
     * Set the object converters to add to the default converters
     * @param converters a map of converters to add to the default set
     */
    public void setConverters(Map<Class<?>, Converter<?>> converters)
    {
        this.loadDefaultConverters();
        if (converters != null && ! converters.isEmpty() )
        {
            for (Entry<Class<?>, Converter<?>> entry : converters.entrySet())
            {
                if (entry.getKey() != null && entry.getValue() != null)
                {
                    this.converters.put(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    private Map<Class<?>, Converter<?>> getConverters()
    {
        if (this.converters == null) this.loadDefaultConverters();
        return this.converters;
    }

    /**
     * this loads up all the default converters
     */
    private void loadDefaultConverters()
    {
        this.converters.clear();

        // Should use a means to load them dynamically from the converters path

        // order is not important here but maintain alpha order for readability
        this.converters.put(BigDecimal.class, new BigDecimalConverter());
        this.converters.put(BigInteger.class, new BigIntegerConverter());
        this.converters.put(Boolean.class, new BooleanConverter());
        this.converters.put(Byte.class, new ByteConverter());
        this.converters.put(Calendar.class, new CalendarConverter());
        this.converters.put(Character.class, new CharacterConverter());
        this.converters.put(Class.class, new ClassConverter());
        this.converters.put(Collection.class, new CollectionConverter());
        this.converters.put(Date.class, new DateConverter());
        this.converters.put(Double.class, new DoubleConverter());
        this.converters.put(Enum.class, new EnumConverter());
        this.converters.put(File.class, new FileConverter());
        this.converters.put(Float.class, new FloatConverter());
        this.converters.put(Integer.class, new IntegerConverter());
        this.converters.put(Long.class, new LongConverter());
        this.converters.put(Map.class, new MapConverter());
        this.converters.put(Number.class, new NumberConverter());
        this.converters.put(Short.class, new ShortConverter());
        this.converters.put(String.class, new StringConverter());
        this.converters.put(java.sql.Date.class, new SQLDateConverter());
        this.converters.put(java.sql.Time.class, new SQLTimeConverter());
        this.converters.put(java.sql.Timestamp.class, new TimestampConverter());
        this.converters.put(URL.class, new URLConverter());
    }

    /**
     * Add a converter to the default set which will convert objects to the supplied type
     * @param type the type this converter will convert objects to
     * @param converter the converter
     */
    public boolean addConverter(Class<?> type, Converter<?> converter)
    {
        if (type == null || converter == null) throw new IllegalArgumentException("You must specify a type and a converter in order to add a converter (no nulls)");
        this.getConverters().put(type, converter);
        return true;
    }

    /**
     * Replace the current or default variable converters with a new set,
     * this will remove the default variable converters and will not add them back in
     * @param variableConverters the variable object converters
     */
    public void setVariableConverters(List<VariableConverter> variableConverters)
    {
        this.loadDefaultVariableConverters();
        if (variableConverters != null)
        {
            for (VariableConverter variableConverter : variableConverters)
            {
                if (variableConverter != null) this.variableConverters.add(variableConverter);
            }
        }
    }

    private Collection<VariableConverter> getVariableConverters()
    {
        if (this.variableConverters == null) this.loadDefaultVariableConverters();
        return this.variableConverters;
    }

    private void loadDefaultVariableConverters()
    {
        this.variableConverters.clear();
        this.variableConverters.add( new ArrayConverter() );
        this.variableConverters.add( new ScalarConverter() );
    }

    /**
     * Adds a variable converter to the end of the list of default variable converters
     * @param variableConverter
     */
    public void addVariableConverter(VariableConverter variableConverter)
    {
        if (variableConverter == null) throw new IllegalArgumentException("You must specify a variableConverter in order to add it (no nulls)");
        this.getVariableConverters().add(variableConverter);
    }

    /**
     * Resets and removes all variable converters including the defaults,
     * use this when you want to override the existing variable converters
     */
    public void clearVariableConverters()
    {
        this.variableConverters.clear();
    }

    /**
     * Added for apache commons beanutils compatibility,
     * you should probably use {@link #convert(Object, Class)}<br/>
     * Convert the specified value into a String.  If the specified value
     * is an array, the first element (converted to a String) will be
     * returned.  The registered {@link Converter} for the
     * <code>java.lang.String</code> class will be used, which allows
     * applications to customize Object->String conversions (the default
     * implementation simply uses toString()).
     *  
     * @param object any object
     * @return the string OR null if one cannot be found
     */
    public String convertToString(Object object)
    {
        // code here is basically from ConvertUtilsBeans 1.8.0
        String convert = null;
        if (object == null) convert = null;
        else if (object.getClass().isArray())
        {
            if (Array.getLength(object) < 1) convert = null;
            else
            {
                Object value = Array.get(object, 0);
                if (value == null) convert = null;
                else
                {
                    Converter<String> converter = this.getConverter(String.class);
                    return converter.convert(value);
                }
            }
        }
        else
        {
            Converter<String> converter = this.getConverter(String.class);
            return converter.convert(object);
        }
        return convert;
    }

    /**
     * Added for apache commons beanutils compatibility,
     * you should probably use {@link #convert(Object, Class)}<br/>
     * Convert the string value to an object of the specified class (if
     * possible).  Otherwise, return a String representation of the value.
     *
     * @param value the string value to be converted
     * @param type any class type that you want to try to convert the object to
     * @return the converted value
     * @throws UnsupportedOperationException if the conversion cannot be completed
     */
    public Object convertString(String value, Class<?> type)
    {
        Object convert;
        try
        {
            convert = this.convert(value, type);
        }
        catch (UnsupportedOperationException e)
        {
            convert = value;
        }
        return convert;
    }

    /**
     * Converts an object to any other object if possible using the current set of converters,
     * will allow nulls to pass through unless there is a converter which will handle them <br/>
     * Includes special handling for primitives, arrays, and collections 
     * (will take the first value when converting to scalar)
     * 
     * @param <T>
     * @param value any object
     * @param type any class type that you want to try to convert the object to
     * @return the converted value (allows null to pass through except in the case of primitives which become the primitive default)
     * @throws UnsupportedOperationException if the conversion cannot be completed
     */
    @SuppressWarnings("unchecked")
    public <T> T convert(Object value, Class<T> type)
    {
        T convert = null;
        Object toConvert = value;
        if (toConvert != null)
        {
            Class<?> fromType = toConvert.getClass();
            // first we check to see if we even need to do the conversion
            if ( ConstructorUtils.classAssignable(fromType, type) )
            {
                // this is already equivalent so no reason to convert
                convert = (T) value;
            }
            else
            {
                // needs to be converted
                try
                {
                    convert = this.convertWithConverter(toConvert, type);
                }
                catch (RuntimeException e)
                {
                    throw new UnsupportedOperationException("Could not convert object ("+toConvert+") from type ("+fromType+") to type ("+type+"): " + e.getMessage(), e);
                }
            }
        }
        else
        {
            // object is null but type requested may be primitive
            if ( ConstructorUtils.isClassPrimitive(type) ) {
                // for primitives we return the default value
                if (ConstructorUtils.getPrimitiveDefaults().containsKey(type)) {
                    convert = (T) ConstructorUtils.getPrimitiveDefaults().get(type);
                }
            }
        }
        return convert;
    }

    /**
     * Use the converters to convert the value to the provided type,
     * this simply finds the converter and does the conversion,
     * will convert interface types automatically <br/>
     * WARNING: you should use {@link #convert(Object, Class)} unless you have a special need
     * for this, it is primarily for reducing code complexity
     * 
     * @param <T>
     * @param value any object to be converted
     * @param type the type to convert to
     * @return the converted object (may be null)
     * @throws UnsupportedOperationException is the conversion could not be completed
     */
    protected <T> T convertWithConverter(Object value, Class<T> type)
    {
        T convert = null;
        // check for a variable converter that says it will handle the conversion first
        VariableConverter variableConverter = this.getVariableConverter(value, type);
        if (variableConverter != null)
        {
            // use the variable converter
            convert = variableConverter.convert(value, type);
        }
        else
        {
            // use a converter
            Converter<T> converter = this.getConverterOrFail(type);
            if (InterfaceConverter.class.isAssignableFrom(converter.getClass()))
            {
                convert = ((InterfaceConverter<T>)converter).convertInterface(value, type);
            }
            else
            {
                // standard converter
                convert = converter.convert(value);
            }
        }
        return convert;
    }

    /**
     * Get the converter or throw exception
     * @param <T>
     * @param type type to convert to
     * @return the converter or die
     * @throws UnsupportedOperationException if the converter cannot be found
     */
    protected <T> Converter<T> getConverterOrFail(Class<T> type)
    {
        Converter<T> converter = this.getConverter(type);
        if (converter == null) throw new UnsupportedOperationException("Conversion failure: No converter available to handle conversions to type ("+type+")");
        return converter;
    }

    /**
     * Get the converter for the given type if there is one
     * @param <T>
     * @param type the type to convert to
     * @return the converter for this type OR null if there is not one
     */
    @SuppressWarnings("unchecked")
    protected <T> Converter<T> getConverter(Class<T> type)
    {
        if (type == null) throw new IllegalArgumentException("Cannot get a converter for nulls");
        // first make sure we are using an actual wrapper class and not the primitive class (int.class)
        Class<?> toType = ConstructorUtils.getWrapper(type);
        Converter<T> converter = (Converter<T>) getConverters().get(toType);

        if (converter == null)
        {
            // none found so try not using the interface
            toType = ConstructorUtils.getClassFromInterface(toType);
            converter = (Converter<T>) this.getConverters().get(toType);
            if (converter == null)
            {
                // still no converter found so try the interfaces
                for (Class<?> iface : ConstructorUtils.getExtendAndInterfacesForClass(toType))
                {
                    converter = (Converter<T>) this.getConverters().get(iface);
                    if (converter != null) break;
                }
            }
        }
        return converter;
    }

    /**
     * Get the variable converter for this value and type if there is one,
     * returns null if no converter is available
     * @param value the value to convert
     * @param type the type to convert to
     * @return the variable converter if there is one OR null if none exists
     */
    protected VariableConverter getVariableConverter(Object value, Class<?> type)
    {
        VariableConverter converter = null;
        Class<?> toType = ConstructorUtils.getWrapper(type);
        for (VariableConverter variableConverter : this.getVariableConverters())
        {
            if (variableConverter.canConvert(value, toType))
            {
                converter = variableConverter;
                break;
            }
        }
        return converter;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("converters=");
        sb.append(getConverters().size());
        sb.append(":");
        for (Entry<Class<?>, Converter<?>> entry : getConverters().entrySet()) {
            sb.append("[");
            sb.append(entry.getKey().getName());
            sb.append("=>");
            sb.append(entry.getValue().getClass().getName());
            sb.append("]");
        }
        sb.append(":variable=");
        sb.append(getVariableConverters().size());
        sb.append(":");
        for (VariableConverter variableConverter : getVariableConverters()) {
            sb.append("(");
            sb.append(variableConverter.getClass().getName());
            sb.append(")");
        }
        return sb.toString();
    }

    private ConstructorUtils getConstructorUtils() {
        return ConstructorUtils.getInstance();
    }

    private FieldUtils getFieldUtils() {
        return FieldUtils.getInstance();
    }
}
