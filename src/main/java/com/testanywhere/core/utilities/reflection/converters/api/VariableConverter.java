package com.testanywhere.core.utilities.reflection.converters.api;

import com.testanywhere.core.utilities.reflection.ConversionUtils;


/**
 * Allows a converter to check to see if it can handle converting something before
 * any of the converters are actually called, these converters are called before
 * any of the other converters if they happen to return true, note that the order
 * of the variable converters will match the order they were registered
 * 
 */
public interface VariableConverter extends BaseConverter
{
    /**
     * Check if this value can be converted to this type by this converter
     * 
     * @param value the input value to be converted, this will never be null or the type which you are converting to
     * since these simple cases are already handled in the {@link ConversionUtils}
     * @param toType the type to convert this value to
     * @return true if it can handle the conversion, false otherwise
     */
    public boolean canConvert(Object value, Class<?> toType);

    /**
     * Convert the specified input object into an output object of the type specified
     *
     * @param value the input value to be converted, this will never be null or the type which you are converting to
     * since these simple cases are already handled in the {@link ConversionUtils}
     * @param toType the type to convert this value to
     * @return the converted value (can be null if desired)
     * @throws UnsupportedOperationException if conversion cannot be performed successfully
     */
    public <T> T convert(Object value, Class<T> toType);
}
