package com.testanywhere.core.utilities.reflection.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Indicate fields on a class which should be ignored when performing class field operations <br/>
 * This is primarily for indicating that reflection operations should not affect fields in classes
 * which you are extending or constant fields in interfaces <br/>
 * For example: <br/>
 * Given a class with 3 String fields: name, email, phone <br/>
 * Adding the annotation like so to the class will cause the phone field to not be counted for field reflection operations:
 * <pre>
 *    &#064;ReflectIgnoreClassFields("phone")
 *    public class Person {
 *        String name;
 *        String email;
 *        String phone;
 *        ... 
 *    }
 * </pre>
 * And this annotation would cause the email and phone fields to be ignored:
 * <pre>
 *    &#064;ReflectIgnoreClassFields({"phone","email"})
 *    public class Person {
 *        String name;
 *        String email;
 *        String phone;
 *        ... 
 *    }
 * </pre>
 * 
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ReflectIgnoreClassFields {
    String[] value();
}
