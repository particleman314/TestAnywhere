package com.testanywhere.core.utilities.reflection.converters.api;

import com.testanywhere.core.utilities.reflection.ConversionUtils;


/**
 * Special converter which is used for converting to an interface when more control
 * over the type of implementation to create under the interface is desired<br/>
 * @see Converter for more details about the core converter interface and converters in general
 * 
 */
public interface InterfaceConverter<T> extends Converter<T>
{
    /**
     * Convert the value into the implementationType for the interface T
     * 
     * @param value the input value to be converted, this will never be null or the type which you are converting to
     * since these simple cases are already handled in the {@link ConversionUtils}
     * @param implementationType
     * @return the converted value (can be null if desired)
     * @throws UnsupportedOperationException if conversion cannot be performed successfully
     */
    public T convertInterface(Object value, Class<? extends T> implementationType);
}
