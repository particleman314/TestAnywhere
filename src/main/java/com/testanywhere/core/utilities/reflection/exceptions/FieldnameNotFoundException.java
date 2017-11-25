package com.testanywhere.core.utilities.reflection.exceptions;

/**
 * Indicates that the fieldname could not be found
 * 
 */
public class FieldnameNotFoundException extends RuntimeException {

   public String fieldName;

   public FieldnameNotFoundException(String fieldName) {
      this(fieldName, null);
   }

   public FieldnameNotFoundException(String fieldName, Throwable cause) {
      this("Could not find fieldName ("+fieldName+") on object", fieldName, cause);
   }

   public FieldnameNotFoundException(String message, String fieldName, Throwable cause) {
      super(message, cause);
      this.fieldName = fieldName;
   }

}
