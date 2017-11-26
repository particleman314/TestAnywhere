package com.testanywhere.core.os.classes.support.machines;

import com.testanywhere.core.utilities.class_support.Cast;
import com.testanywhere.core.utilities.class_support.functional_support.ConstantsInterface;

import java.util.regex.Pattern;

public class BaseMachineConstants implements ConstantsInterface
{
    public enum MACHINE_TYPE_PATTERNS
    {
        UNIX("^(\\w+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)"),
        WINDOWS("^(\\S+)\\s+(\\d+)\\s+(\\w+)\\s+(\\d+)\\s+(\\S+)$");

        private Pattern processTablePattern = null;

        MACHINE_TYPE_PATTERNS(String p)
        {
            this.setProcessTablePattern(p);
        }

        public Pattern getProcessTablePattern() { return this.processTablePattern; }

        public void setProcessTablePattern( final String pat ) { this.processTablePattern = Pattern.compile(pat); }

        public boolean isSame( Object other )
        {
            if ( other == null ) return false;
            MACHINE_TYPE_PATTERNS otherMT = Cast.cast(other);

            if ( otherMT == null ) return false;
            if ( ! this.getProcessTablePattern().equals(otherMT.getProcessTablePattern()) ) return false;
            return true;
        }
    }

    // This ensures it is initialized when import by any class
    static
    {
        BaseMachineConstants.getInstance();
    }

    // This makes for a singleton object to keep the only copy
    private static boolean isInitialized = false;

    public static BaseMachineConstants getInstance()
    {
        if ( ! BaseMachineConstants.isInitialized ) {
            BaseMachineConstants.isInitialized = true;
            BaseMachineConstants.initialize();
        }
        return BaseMachineConstantsHolder.INSTANCE;
    }

    private static class BaseMachineConstantsHolder
    {
        public static final BaseMachineConstants INSTANCE = new BaseMachineConstants();
    }

    // Allow a means to reset parameters if they are allowed to change
    @Override
    public void reset() {
        BaseMachineConstants.initialize();
    }

    private static void initialize()
    {}

    private BaseMachineConstants()
    {}
}
