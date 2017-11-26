package com.testanywhere.core.classes.utilities;

import com.testanywhere.core.utilities.class_support.Cast;
import com.testanywhere.core.utilities.classes.Pair;
import com.testanywhere.core.utilities.classes.ErrorHandler;
import com.testanywhere.core.utilities.logging.LogConfiguration;
import com.testanywhere.core.utilities.logging.TextManager;
import com.testanywhere.core.classes.managers.ErrorMsgDBManager;
import com.testanywhere.core.classes.managers.Registrar;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Iterator;

public class LoadErrors
{
    public static Logger logger;
    public static final String ERROR_SEPARATOR = "|";

    static
    {
        LoadErrors.logger = Logger.getLogger("LoadErrors");
        LogConfiguration.configure();
    }

    public static void installErrors( final Class<?> errorClass )
    {
        LoadErrors.installErrors(errorClass, null, false);
    }

    public static void installErrors( final Class<?> errorClass, final boolean startup )
    {
        LoadErrors.installErrors(errorClass, null, startup);
    }

    public static void installErrors( final Class<?> errorClass, final Object handler, final boolean startup )
    {
        if ( errorClass == null ) return;

        ErrorMsgDBManager eDB = Registrar.getDefaultManager("ErrorMsgDBManager");
        if ( eDB == null )
        {
            LoadErrors.logger.error("Unable to generate/instantiate the ErrorMsgDBManager");
            return;
        }

        Field[] declaredFields = errorClass.getDeclaredFields();
        //Collection<Field> staticErrDefines = new ArrayList<>();

        for ( Field f : declaredFields )
        {
            if ( Modifier.isPublic(f.getModifiers()) && Modifier.isStatic(f.getModifiers()) ) {
                try
                {
                    String errName = f.getName();
                    int errID = errorClass.getField(errName).getInt(null);

                    String description = "DESCRIPTION : " + errName;

                    if ( handler != null )
                    {
                        try
                        {
                            ErrorHandler callback = Cast.cast(handler);
                            description = callback.call();
                        }
                        catch ( Exception ignored)
                        {}
                    }

                    eDB.associateErrorToID(errID, new Pair<>(errName,description));
                }
                catch ( NoSuchFieldException | IllegalAccessException ignored)
                {}
            }
        }
    }

    public static void installErrors( final String errFilePath )
    {
        LoadErrors.installErrors(errFilePath, false);
    }

    public static void installErrors( final String errFilePath, final boolean startup )
    {
        ErrorMsgDBManager eDB = Registrar.getDefaultManager("ErrorMsgDBManager");
        if ( eDB == null )
        {
            LoadErrors.logger.warn("Unable to prepare error database for use.  Base information available only!");
            return;
        }

        if ( !TextManager.validString(errFilePath) || ! FileDirUtils.fileExists(errFilePath) )
        {
            if ( ! startup )
                LoadErrors.logger.warn("No viable error file found to read and populate error database.");
            return;
        }

        try
        {
            Collection<String> readLines = FileDirUtils.readFileAsLines(errFilePath);
            Iterator<String> iter = readLines.iterator();

            int cnt = 0;

            while ( iter.hasNext() )
            {
                ++cnt;
                String line = iter.next();
                Pair<Integer, Pair<String, String>> data = LoadErrors.parseErrorDefinition(line);

                Pair<String, String> eData = data.second();
                if ( data.second() != null && TextManager.validString(eData.first()) && TextManager.validString(eData.second()))
                {
                    eDB.associateErrorToID(data.first(), data.second(), true);
                }
                else
                {
                    LoadErrors.logger.warn("[ L-" + cnt + " ] Invalid line to parse :: " + TextManager.specializeName(line));
                }
            }
        }
        catch ( IOException e )
        {
            LoadErrors.logger.error("Unable to read requested error file.  Possibly corrupted or no permissions!");
        }
    }

    public static void setSuccessCode( final int success )
    {
        ErrorMsgDBManager eDB = Registrar.getDefaultManager("ErrorMsgDBManager");
        if ( eDB == null )
        {
            LoadErrors.logger.warn("Unable to prepare error database for use.  Base information available only!");
            return;
        }
        if ( success < 0 )
            LoadErrors.logger.info("Cannot redefine success code with negative value");

        if ( eDB.hasErrorID(success) )
            LoadErrors.logger.warn("Redefinition of success will clobber existing definition with ID " + success);

        Pair<String, String> data = eDB.eraseErrorID(ErrorUtils.SUCCESS_ID);
        ErrorUtils.SUCCESS_ID = success;
        eDB.associateErrorToID(success, data, true);
    }

    public static void setGenericFailCode( final int fail )
    {
        ErrorMsgDBManager eDB = Registrar.getDefaultManager("ErrorMsgDBManager");
        if ( eDB == null )
        {
            LoadErrors.logger.warn("Unable to prepare error database for use.  Base information available only!");
            return;
        }
        if (fail < 0)
            LoadErrors.logger.info("Cannot redefine fail code with negative value");

        if (eDB.hasErrorID(fail))
            LoadErrors.logger.warn("Redefinition of generic fail will clobber existing definition with ID " + fail);

        Pair<String, String> data = eDB.eraseErrorID(ErrorUtils.GENERIC_FAIL_ID);
        ErrorUtils.GENERIC_FAIL_ID = fail;
        eDB.associateErrorToID(fail, data, true);
    }

    private static Pair<Integer, Pair<String,String>> parseErrorDefinition( final String inputLine )
    {
        Pair<Integer, Pair<String,String>> result = new Pair<>(-2, null);
        if ( ! TextManager.validString(inputLine) ) return result;

        String[] components = inputLine.split(LoadErrors.ERROR_SEPARATOR);
        if ( components.length != 3 ) return result;

        result.setL(Integer.parseInt(components[0]));
        result.setR(new Pair<>(components[1], components[2]));

        return result;
    }
}
