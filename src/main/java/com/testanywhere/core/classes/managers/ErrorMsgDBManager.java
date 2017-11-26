package com.testanywhere.core.classes.managers;

import com.testanywhere.core.classes.utilities.ErrorUtils;
import com.testanywhere.core.utilities.classes.Pair;
import com.testanywhere.core.utilities.logging.DisplayManager;
import com.testanywhere.core.utilities.logging.Tabbing;
import com.testanywhere.core.utilities.logging.TextManager;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.TreeBidiMap;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("ExternalizableWithoutPublicNoArgConstructor")
public class ErrorMsgDBManager extends ABCManager
{
    private static final String FAILURE_PREFIX = "FAIL";
    private static final String SUCCESS_PREFIX = "PASS";

    public static final String NO_ENTRY_MSG = "UNKNOWN ERROR ID";
    public static final String STD_SUCCESS_MSG = "No error detected";

    public static final int BAD_ENTRY_ID = -1;
    private static final int DEFAULT_ENTRY_ID = 1;

    private int lastEntry;
    private BidiMap<String, Integer> errorIDMap;
    private BidiMap<Integer, String> errorMap;

    //private final Lock lock = new Lock();

    static
    {
       Registrar.getInstance().addManagerPath(ErrorMsgDBManager.class.getPackage().getName());
    }

    private static class ErrorMsgDBManagerHolder
    {
        public static final ErrorMsgDBManager INSTANCE = new ErrorMsgDBManager();
    }

    public static ErrorMsgDBManager getInstance()
    {
        return ErrorMsgDBManagerHolder.INSTANCE;
    }

    private ErrorMsgDBManager()
    {
        Class<?> clazz = ErrorMsgDBManager.class;
        this.__initialize();

        super.setManagerType(clazz);
        super.setManagerName(clazz.getSimpleName());
        super.configure();
    }

    @Override
    public void buildObjectOutput( int numTabs )
    {
        super.buildObjectOutput(numTabs);
        Tabbing tabEnvironment = new Tabbing(numTabs + 1);
        DisplayManager dm = this.getDM();

        String innerSpacer = tabEnvironment.getSpacer();
        dm.append(innerSpacer + "Number of error messages recorded : " + this.size());

        Integer maxEIDSize = this.findLargestEIDSize();

        tabEnvironment.increment();
        String subInnerSpacer = tabEnvironment.getSpacer();
        for ( Integer i : this.getErrorMap().keySet() )
        {
            String modifiedMsg = this.determinePassFail(i, this.getErrorMap().get(i));
            dm.append(subInnerSpacer + " [ EID : " + this.format(i.toString(), maxEIDSize) + TextManager.STR_OUTPUTSEPARATOR + " EMSG : " + TextManager.specializeName(modifiedMsg) + " ]");
        }
    }

    @Override
    public void reset()
    {
        this.getErrorMap().clear();
        this.lastEntry = 0;

        this.seedMap();
    }

    @Override
    public void cleanup()
    {}

    public int size()
    {
        return this.getErrorMap().size();
    }

    public int getErrorIDFromType( final String eType )
    {
        if ( ! TextManager.validString(eType) ) return ErrorMsgDBManager.BAD_ENTRY_ID;
        for (  Map.Entry<String, Integer> entry : this.errorIDMap.entrySet() )
        {
            if ( entry.getKey().equals(eType) )
                return entry.getValue();
        }
        return ErrorMsgDBManager.BAD_ENTRY_ID;
    }

    public int getErrorIDFromMsg( final String errMsg )
    {
        if ( ! TextManager.validString(errMsg) ) return ErrorMsgDBManager.BAD_ENTRY_ID;
        for (  Map.Entry<Integer, String> entry : this.getErrorMap().entrySet() )
        {
            if ( entry.getValue().equals(errMsg) )
                return entry.getKey();
        }
        return ErrorMsgDBManager.BAD_ENTRY_ID;
    }

    public String getErrorMsgFromID( int errID )
    {
        if ( errID < ErrorMsgDBManager.BAD_ENTRY_ID || ! this.hasErrorID(errID) ) errID = ErrorMsgDBManager.BAD_ENTRY_ID;
        return this.determinePassFail(errID, this.getErrorMap().get(errID));
    }

    public int addErrorMessage( final Pair<String, String> data )
    {
        this.associateErrorToID(null, data);
        return this.lastEntry;
    }

    public int addErrorMessage( final String errMsg, final String eType )
    {
        Pair<String, String> data = new Pair<>(errMsg,eType);
        return this.addErrorMessage(data);
    }

    public int translateToID( final String errorType )
    {
        if ( this.errorIDMap.containsKey(errorType) ) return this.errorIDMap.get(errorType);
        Pattern p = Pattern.compile("*" + errorType + "*");
        for ( String s : this.errorIDMap.keySet() )
        {
            Matcher m = p.matcher(s);
            if ( m.find() ) return this.errorIDMap.get(s);
        }
        return ErrorMsgDBManager.BAD_ENTRY_ID;
    }

    public void associateErrorToID( final Integer errID, final Pair<String, String> errData )
    {
        this.associateErrorToID(errID, errData, false);
    }

    public void associateErrorToID( Integer errID, final Pair<String, String> errData, final boolean overwrite )
    {
        if ( errData == null ||
                !TextManager.validString(errData.first()) || !TextManager.validString(errData.second())) return;

        // errID == null, overwrite = [ T/F ], contained in map [T/F] --> find available ID
        // errID != null, overwrite = false, contained in map = false --> find available ID
        // errID != null, overwrite = true, contained in map = false  --> possible overwrite
        // errID != null, overwrite = false, contained in map = true  --> find available ID
        // errID != null, overwrite = true, contained in map = true   --> possible overwrite
        if ( errID == null || ( (! overwrite) && this.hasErrorID(errID)) )
            errID = this.findNextAvailableID();

        this.getErrorMap().put(errID, errData.first());
        this.errorIDMap.put(errData.second(), errID);

        if ( this.getErrorMap().containsKey(errID) )
            this.lastEntry = errID;
    }

    public boolean hasErrorID( final int errID )
    {
        return this.getErrorMap().containsKey(errID);
    }

    public Pair<String, String> eraseErrorID( final int errID )
    {
        Pair<String, String> result = new Pair<>();

        if ( this.hasErrorID(errID) )
        {
            String emsg = this.getErrorMap().get(errID);
            String etype = this.errorIDMap.getKey(errID);

            result.setL(emsg);
            result.setR(etype);

            this.errorIDMap.remove(emsg);
            this.getErrorMap().remove(errID);
        }

        return result;
    }

    private BidiMap<Integer, String> getErrorMap()
    {
        return this.errorMap;
    }

    private int findNextAvailableID()
    {
        if ( this.getErrorMap().isEmpty() ) return ErrorMsgDBManager.DEFAULT_ENTRY_ID;
        return (this.lastEntry + 1);
    }

    private String determinePassFail( final int eID, final String defMsg )
    {
        if ( eID <= ErrorMsgDBManager.BAD_ENTRY_ID ) return defMsg;
        if ( eID == ErrorUtils.SUCCESS_ID ) return ErrorMsgDBManager.SUCCESS_PREFIX + " : " + defMsg;
        else return ErrorMsgDBManager.FAILURE_PREFIX + " : " + defMsg;
    }

    private Integer findLargestEIDSize()
    {
        String rep = "";
        for ( Integer i : this.getErrorMap().keySet() )
        {
            if ( i.toString().length() > rep.length() )
                rep = i.toString();
        }
        return rep.length();
    }

    private String format( final String str, final Integer maxSize )
    {
        int diff = maxSize - str.length();
        if ( diff > 0 )
        {
            return StringUtils.repeat(" ", diff) + str;
        }
        return str;
    }

    private void seedMap()
    {
        if ( ! this.hasErrorID(ErrorUtils.SUCCESS_ID) )
        {
            this.getErrorMap().put(ErrorMsgDBManager.BAD_ENTRY_ID, ErrorMsgDBManager.NO_ENTRY_MSG);
            this.getErrorMap().put(0, ErrorMsgDBManager.STD_SUCCESS_MSG);

            this.errorIDMap.put("SUCCESS", ErrorUtils.SUCCESS_ID);
            this.errorIDMap.put("UNKNOWN", ErrorMsgDBManager.BAD_ENTRY_ID);
            this.errorIDMap.put("FAIL", ErrorUtils.GENERIC_FAIL_ID);
        }
    }

    private void __initialize()
    {
        //synchronized (this.lock)
        //{
            this.errorMap = new TreeBidiMap<>();
            this.errorIDMap = new TreeBidiMap<>();
            this.lastEntry = 0;

            this.seedMap();
        //}
    }
}
