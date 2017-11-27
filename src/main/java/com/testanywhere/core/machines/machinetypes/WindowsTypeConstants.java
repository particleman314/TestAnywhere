package com.testanywhere.core.machines.machinetypes;

import com.testanywhere.core.utilities.class_support.functional_support.ConstantsInterface;
import com.testanywhere.core.machines.shelltypes.ShellType;
import com.testanywhere.core.machines.shelltypes.ShellTypeInterface;

import java.util.regex.Pattern;

public class WindowsTypeConstants implements ConstantsInterface
{
    public static final String WINDOWS = "WINDOWS";

    public static final String WINDOWSTYPE = "WindowsType";

    public static final String WINDOWS_REG_SZ     = "REG_SZ";
    public static final String WINDOWS_REG_DWORD  = "REG_DWORD";
    public static final String WINDOWS_REG_BINARY = "REG_BINARY";

    public static final Pattern WINDOWS_SUBSTITUTE_REGEX = Pattern.compile("%\\w+%");

    public enum WINDOWS_STYLES
    {
        HOME("Home Edition"),
        ULTIMATE("Ultimate"),
        PRO("Professional"),
        ENTERPRISE("Enterprise");

        private String rep = null;

        WINDOWS_STYLES(String s)
        {
            this.setName(s);
        }

        public void setName(final String rep)
        {
            this.rep = rep;
        }

        public final String getName() { return this.rep; }

        public final String getTranslation() { return "Microsoft"; }
    }

    public enum WINDOWS_SHELL_STYLES implements ShellTypeInterface
    {
        WINDOWS_SHELL("cmd /c"),
        WINDOWS_POWERSHELL("ps");

        private String shellPath = null;

        WINDOWS_SHELL_STYLES(String s)
        {
            this.setShellPath(s);
        }

        public void setShellPath(final String rep)
        {
            this.shellPath = rep;
        }
        public final String getShellPath() { return this.shellPath; }

        public final ShellType getShell() { return new ShellType(this.shellPath); }
    }

    private static boolean isInitialized = false;

    public static WindowsTypeConstants getInstance()
    {
        if ( ! WindowsTypeConstants.isInitialized )
        {
            WindowsTypeConstants.isInitialized = true;
            WindowsTypeConstants.initialize();
        }
        return WindowsTypeConstantsHolder.INSTANCE;
    }

    public static void installShells()
    {
        for ( WINDOWS_SHELL_STYLES ss : WINDOWS_SHELL_STYLES.values() )
            MachineTypeConstants.SHELL_MAP.put(ss.getShellPath(), ss.getShell());
    }

    @Override
    public void reset()
    {
        WindowsTypeConstants.initialize();
    }

    private static class WindowsTypeConstantsHolder
    {
        public static final WindowsTypeConstants INSTANCE = new WindowsTypeConstants();
    }

    private static void initialize()
    {}
}
