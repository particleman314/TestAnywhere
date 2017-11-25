package com.testanywhere.core.utilities.class_support;

import com.google.common.reflect.TypeToken;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * <h1>GenericAbstract class:</h1>
 *
 * Base class for the Generic class to prevent type erasure in java
 * when it is important to keep.
 *
 * @author  Mike Klusman III
 * @version 0.5
 * @since   2015-09-01
 */

public class GenericAbstract<T>
{
    protected T parameter;
    protected TypeToken<T> typeToken = new TypeToken<T>(getClass()){ };
    protected Class<?> parameterType;
    
    @SuppressWarnings("unchecked")
    void initParameter() throws Exception
    {
        // Get the class name of this instance's type.

        ParameterizedType pt = (ParameterizedType) getClass().getGenericSuperclass();
        // You may need this split or not, use logging to check
		Type[] b = pt.getActualTypeArguments();

		if ( b.length > 0 )
		{
            String[] components = b[0].toString().split("\\s");
			String parameterClassName = components[1];
			// Instantiate the Parameter and initialize it.
			this.parameter = (T) Class.forName(parameterClassName).newInstance();
		}
        else
        {
			this.parameter = (T) Class.forName(this.parameter.getClass().toString()).newInstance();
		}

		this.parameterType = this.parameter.getClass();
    }
    
    public T get()
    {
    	return this.parameter;
    }
    
    public Class<?> getClassType()
    {
    	return this.parameterType;
    }
}
