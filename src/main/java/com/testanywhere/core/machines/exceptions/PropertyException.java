package com.testanywhere.core.machines.exceptions;

import java.util.HashMap;

import static com.jacob.com.Variant.*;

public class PropertyException extends Exception
{	
	private static final HashMap<Short,String> ValueTypes = new HashMap<Short,String>() 
	{  
	    private static final long serialVersionUID = -3478873887562152554L;  
	    {  
			put(VariantArray, "Array");
			put(VariantBoolean, "Boolean");
			put(VariantByref, "Byref");
			put(VariantByte, "Byte");
			put(VariantCurrency, "Currency");
			put(VariantDate, "Date");
			put(VariantDecimal, "Decimal");
			put(VariantDispatch, "Dispatch");
			put(VariantDouble, "Double");
			put(VariantEmpty, "Empty");
			put(VariantError, "Error");
			put(VariantFloat, "Float");
			put(VariantInt, "Int");
			put(VariantLongInt, "LongInt");
			put(VariantNull, "Null");
			put(VariantObject, "Object");
			put(VariantPointer, "Pointer");
			put(VariantShort, "Short");
			put(VariantString, "String");
			put(VariantTypeMask, "TypeMask");
			put(VariantVariant, "Variant");
	    }
	};

    private static final long serialVersionUID = -8992330507126201701L;  
    private final String requestedPropertyName;
    private final String requestedPropertyType;
    private final String validPropertyType;
    
    public PropertyException(String propertyName, String requestedType, IllegalStateException exception) 
    {  
        //create message
        super(PropertyException.createMessage(propertyName, requestedType, exception.getMessage()), exception);
        this.requestedPropertyName = propertyName;  
        this.requestedPropertyType = requestedType;  
        this.validPropertyType = PropertyException.getExceptionTypeName(exception.getMessage());
    }  
       
    /**  
     * Creates a message using the message from the IllegalStateException  
     * @param message  
     * @return "PropertyName" was requested as type "PropertyType", but is type "RealType"  
     */ 
    private static String createMessage(String propertyName, String requestedType, String message) 
    {  
        return "\"" + propertyName + "\" was requested as type " + requestedType + ", but is type "+ PropertyException.getExceptionTypeName(message);
    }  
       
       
    /**  
     * Returns the name of a variant type OR null  
     * @param short representing the code  
     * @return string name or null  
     */ 
    private static String getExceptionTypeName(String message) 
    {  
        short code = -1;  
        /*Pretty confident all the error messages for getX() on a Variant(which occurs underneath) are the same, but  
         * This block protects against error messages that are not in the correct format*/ 
        try 
        {   
            String [] parts = message.split(" ");  
            code = Short.parseShort(parts[parts.length-1]);  
        }
        catch (Exception e) 
        { // NOPMD  
            //short code stays = -1  
        }  
        return ( ValueTypes.get(code) == null ) ?  "UnknownType" : ValueTypes.get(code);  
    }  
   
    /**  
     * @return the requestedPropertyName  
     */ 
    public String getRequestedPropertyName() 
    {  
        return this.requestedPropertyName;  
    }  
   
    /**  
     * @return the requestedPropertyType  
     */ 
    public String getRequestedPropertyType() 
    {  
        return this.requestedPropertyType;  
    }  
   
    /**  
     * @return the validPropertyType  
     */ 
    public String getValidPropertyType() 
    {  
        return this.validPropertyType;  
    }
}
