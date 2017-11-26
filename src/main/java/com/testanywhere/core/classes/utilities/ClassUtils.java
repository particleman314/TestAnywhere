package com.testanywhere.core.classes.utilities;

import com.testanywhere.core.utilities.class_support.Cast;
import com.testanywhere.core.classes.class_support.CompartmentObject;
import com.testanywhere.core.utilities.logging.LogConfiguration;
import com.testanywhere.core.utilities.logging.TextManager;
import com.testanywhere.core.utilities.logging.TraceHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.*;

public class ClassUtils
{
    public static final String javaExt = ".java";
    public static final String classExt = ".class";

    public static Logger logger;

    static
    {
        ClassUtils.logger = Logger.getLogger("ClassUtils");
        LogConfiguration.configure();
    }

    public static String convertStringOutput( Object output )
    {
        if ( output == null ) return null;

        CompartmentObject<?> coOutput = Cast.safeCast(output, CompartmentObject.class);

        if ( coOutput !=  null )
            output = coOutput.getObject();

        String outputType = output.getClass().getSimpleName();

        String result;
        switch ( outputType.toLowerCase() )
        {
            case "string" :
            {
                result = Cast.safeCast(output, String.class);
                break;
            }
            case "stringbuilder" :
            {
                result = Cast.safeCast(output, StringBuilder.class).toString();
                break;
            }
            default :
            {
                result = output.toString();
            }
        }

        return result;
    }

    public static String convertToClassFile( final String javaFile )
    {
        if ( javaFile == null || javaFile.length() < ClassUtils.javaExt.length() )
        {
            String callMethodName = TraceHelper.getMethodName(0);
            ClassUtils.logger.error("Improper input for " + callMethodName + " found.  Not processed.");
            return javaFile;
        }
        return javaFile.replaceAll(ClassUtils.javaExt, ClassUtils.classExt);
    }

    public static String convertToJavaFile( final String classFile )
    {
        String callMethodName = new Object(){}.getClass().getEnclosingMethod().getName();
        if ( classFile == null || classFile.length() < ClassUtils.classExt.length() )
        {
            ClassUtils.logger.error("Improper input for " + callMethodName + " found.  Not processed.");
            return classFile;
        }
        return classFile.replaceAll(ClassUtils.classExt, ClassUtils.javaExt);
    }

    public static String convertFileToLoader( final String filePath )
    {
        if ( !TextManager.validString(filePath) ) return null;
        return filePath.replaceAll(File.separator, ".");
    }

    public static String convertLoaderToFile( final String loaderPath )
    {
        if ( !TextManager.validString(loaderPath) ) return null;
        return loaderPath.replaceAll("\\.", File.separator);
    }

    public static String getParentClass( final String childClass )
    {
        if ( childClass.length() < 1 ) return "";
        List<String> tmp = new ArrayList<>(Arrays.asList(childClass.split("\\.")));
        tmp.remove(tmp.size()-1);
        return StringUtils.join(tmp,'.');
    }

    public static<S> Collection<CompartmentObject<?>> makeCollection( final Collection<S> group )
    {
        if ( group == null || group.size() < 1 ) return null;

        Collection<CompartmentObject<?>> objs = new ArrayList<>();

        for (S aGroup : group) objs.add(new CompartmentObject<>(aGroup));
        return objs;
    }

    public static<S,T> Collection<CompartmentObject<?>> makeCollection( final Map<S,T> map, final T[] fields )
    {
        if ( map == null || map.size() < 1 ) return null;
        if ( fields == null || fields.length < 1 ) return null;

        Collection<CompartmentObject<?>> objs = new ArrayList<>();
        for ( T s : fields )
        {
            if (map.containsKey(s))
                objs.add(new CompartmentObject<>(map.get(s)));
        }
        return objs;
    }
}
