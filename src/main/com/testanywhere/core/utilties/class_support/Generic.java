package com.nimsoft.class_support;

/**
 * <h1>Generic class:</h1>
 *
 * Class for dynamically determining a class type based on the object type
 * provided as its templated argument.  This is to assist in prevention of
 * type erasure where needed
 *
 * @author  Mike Klusman III
 * @version 0.5
 * @since   2015-09-01
 */

public class Generic<T> extends GenericAbstract<T>
{
	/**
	 * Constructor
	 *
	 * @throws Exception
	 */

	public Generic() throws Exception
	{
		super();
		this.initParameter();
	}
}
