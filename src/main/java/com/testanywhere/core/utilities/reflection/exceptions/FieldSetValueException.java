package com.testanywhere.core.utilities.reflection.exceptions;

/**
 * Indicates that there was a low level failure setting the value on an object field,
 * this is probably caused by a type failure or security failure
 * 
 */
public class FieldSetValueException extends RuntimeException {

   /**
    * the field name we were trying to set 
    */
   public String fieldName;
   /**
    * the value we were trying to set on the field
    */
   public Object fieldvalue;
   /**
    * the object with the field we were trying to set
    */
   public Object object;

   public FieldSetValueException(String fieldName, Object fieldvalue, Object object, Throwable cause) {
      super("Failed to set field ("+fieldName+") to value ("+fieldvalue+"), cause=" + cause, cause);
      this.fieldName = fieldName;
      this.fieldvalue = fieldvalue;
      this.object = object;
   }
   public FieldSetValueException(String message, String fieldName,
         Object fieldvalue, Object object, Throwable cause) {
      super(message, cause);
      this.fieldName = fieldName;
      this.fieldvalue = fieldvalue;
      this.object = object;
   }
   
}
