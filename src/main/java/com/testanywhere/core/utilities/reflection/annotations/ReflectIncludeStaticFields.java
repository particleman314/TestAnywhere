package com.testanywhere.core.utilities.reflection.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Indicate that all static fields on a class should be included when performing class
 * field reflection operations (default is to ignore static fields) <br/>
 * Example: <br/>
 * A class has 1 static field: String phone; <br/>
 * It also has 1 other field: String name; <br/>
 * Normally only the name field would be including when performing field reflection operations.
 * If this annotation is added to the class then both fields would be included in the 
 * list of fields for a class. Also, any static fields on any extended classes or interfaces
 * would also be included.
 *  
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ReflectIncludeStaticFields { }
