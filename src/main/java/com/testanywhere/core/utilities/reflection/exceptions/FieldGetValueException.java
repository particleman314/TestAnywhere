package com.testanywhere.core.utilities.reflection.exceptions;


/**
 * Indicates that there was a failure attempting to get a field value,
 * probably caused by a security exception or obscure java failure reading or writing the value
 * (this indicates the arguments were correct and the field was found but there was a failure at the time of the operation)
 * 
 */
public class FieldGetValueException extends RuntimeException {

   /**
    * the field name we were trying to get the value from 
    */
   public String fieldName;
   /**
    * the object with the field we were trying to get a value from
    */
   public Object object;

   public FieldGetValueException(String fieldName, Object object, Throwable cause) {
      super("Failed to get field ("+fieldName+") value from object ("+object+"), cause=" + cause, cause);
      this.fieldName = fieldName;
      this.object = object;
   }
   public FieldGetValueException(String message, String fieldName, Object object, Throwable cause) {
      super(message, cause);
      this.fieldName = fieldName;
      this.object = object;
   }
   
}
