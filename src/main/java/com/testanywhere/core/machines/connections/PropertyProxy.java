package com.nimsoft.connections;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.EnumVariant;
import com.testanywhere.core.machines.exceptions.PropertyException;

import java.util.ArrayList;

/**  
 * A thin wrapping proxy over the ActiveXComponent in the WMI library so that  
 * properties can be retrieved without having to expose the code under the  
 * covers This class is NOT used for invocation but for querying the values of  
 * properties on a Win32_ class Java Types Represented : String int boolean  
 * char[]
 * TODO: PropertyProxy does NOT allow invocations of methods or  
 * subroutines -- this should be wrapped in a future update  
 *  
 * @author berch11  
 *  
 */ 
public class PropertyProxy 
{  
    ActiveXComponent component;//the component used underneath
   
    /**  
     * Constructor that takes an ActiveXComponent object  
     *  
     * @param acx  
     */ 
    public PropertyProxy(ActiveXComponent acx) 
    {  
        this.component = acx;  
    }  
   
    /**  
     * Constructor that takes a Dispatch object  
     *  
     * @param dispatch  
     */ 
    public PropertyProxy(Dispatch dispatch)
    {  
        this.component = new ActiveXComponent(dispatch);  
    }  
   
    /**  
     * Return a property as its string equivalent Applies to string AND  
     * uint64/sint64  
     *  
     * @param propertyName  
     * @return property as string  
     */ 
    public String getPropertyAsString(String propertyName) throws PropertyException
    {  
        //DOES NOT CALL getPropertyAsString because this can cause an illegal state exception  
        String property = null;  
        try 
        {  
            property = this.component.getPropertyAsString(propertyName);  
        } 
        catch (IllegalStateException ie) 
        {  
            throw new PropertyException(propertyName, "String", ie);
        }  
        return property;  
    }  
   
    /**  
     * Return a a property as it is integer equivalent Applies to uint16,  
     * sint16, uint32, and sint32  
     *  
     * @param propertyName  
     * @return property as int  
     */ 
    public Integer getPropertyAsInt(String propertyName) throws PropertyException
    {  
        Integer property = null;  
        try 
        {  
            property = this.component.getPropertyAsInt(propertyName);  
        } 
        catch (IllegalStateException ie) 
        {  
            throw new PropertyException(propertyName, "Integer", ie);
        }  
        return property;  
    }  
   
    /**  
     * Return a property as its boolean equivalent  
     *  
     * @param propertyName  
     * @return property as boolean  
     */ 
    public boolean getPropertyAsBoolean(String propertyName) throws PropertyException
    {
        try 
        {  
            return this.component.getPropertyAsBoolean(propertyName);
        } 
        catch (IllegalStateException ie) 
        {  
            throw new PropertyException(propertyName, "Boolean", ie);
        }  
   }
   
    /**  
     * Retrieves a property as a byte  
     *  
     * @param propertyName  
     * @return  
     * @throws PropertyException
     */ 
    public byte getPropertyAsByte(String propertyName) throws PropertyException
    {  
        byte property = 0x0;  
        try 
        {  
            property = this.component.getPropertyAsByte(propertyName);  
        } 
        catch (IllegalStateException ie) 
        {  
            throw new PropertyException(propertyName, "Byte", ie);
        }  
        return property;  
    }  
   
    /**  
     * For those who don't want to use strings...  
     *  
     * @param propertyName  
     * @return string property as char array  
     */ 
    public char[] getPropertyAsCharArray(String propertyName) throws PropertyException
    {  
        return this.getPropertyAsString(propertyName).toCharArray();  
    }  
   
    /**  
     * Used if a Win32_Class stores OTHER win32 classes and a new proxy is  
     * needed to access that object  
     *  
     * @param propertyName  
     * @return a new PropertyProxy to the requested win32 class  
     */ 
    public PropertyProxy getPropertyProxy(String propertyName) throws PropertyException
    {  
        PropertyProxy proxy = null;
        try 
        {  
            proxy = new PropertyProxy(this.component.getPropertyAsComponent(propertyName));
        } 
        catch (IllegalStateException ie) 
        {  
            throw new PropertyException(propertyName, "PropertyProxy", ie);
        }  
        return proxy;  
    }  
   
    /**  
     * Retrieve property names  
     */ 
    public String[] getPropertyNames() 
    {  
        ArrayList<String> listOfProperties = new ArrayList<>();
        EnumVariant propertiesVariant = new EnumVariant(this.component.getProperty("Properties_").getDispatch());
        ActiveXComponent propertyComponent = null;  
        while (propertiesVariant.hasMoreElements()) 
        {  
            propertyComponent = new ActiveXComponent(propertiesVariant.nextElement().toDispatch());  
            String sPropName = propertyComponent.getPropertyAsString("Name");  
            listOfProperties.add(sPropName);  
        }  
        return listOfProperties.toArray(new String[listOfProperties.size()]);  
    }  
   
    public String[] getPropertyAsStringArray(String propertyName) throws PropertyException
    {  
        String[] property = null;  
        try 
        {  
            property = this.component.getProperty(propertyName).toSafeArray().toStringArray();  
        } 
        catch (IllegalStateException ie) 
        {  
            throw new PropertyException(propertyName, "String []", ie);
        }  
        return property;  
    }  
} 
