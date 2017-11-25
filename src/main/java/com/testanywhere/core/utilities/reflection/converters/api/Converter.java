package com.testanywhere.core.utilities.reflection.converters.api;

import com.testanywhere.core.utilities.reflection.ConversionUtils;

/**
 * General converter interface written to specifically be compatible with the commons beanutils one <br/>
 * Allows for conversion between types that is more complete than the current ones<br/>
 * To use this, implement the interface with the type that you care about and then register it with the
 * {@link ConversionUtils} by calling {@link ConversionUtils#addConverter(Class, Converter)}
 * or you can use the constructor (the method is recommended though)
 */
public interface Converter<T> extends BaseConverter
{
    /**
     * Convert the specified input object into an output object of the type for this converter implementation
     *
     * @param value the input value to be converted, this will never be null or the type which you are converting to
     * since these simple cases are already handled in the {@link ConversionUtils}
     * @return the converted value (can be null if desired)
     * @throws UnsupportedOperationException if conversion cannot be performed successfully
     */
    public T convert(Object value);
}
