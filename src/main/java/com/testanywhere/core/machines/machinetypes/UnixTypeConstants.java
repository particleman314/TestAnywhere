package com.testanywhere.core.machines.machinetypes;

import com.testanywhere.core.utilities.class_support.functional_support.ConstantsInterface;
import com.testanywhere.core.classes.support.map.DynamicMap;
import com.testanywhere.core.machines.shelltypes.ShellType;
import com.testanywhere.core.machines.shelltypes.ShellTypeInterface;

import java.util.regex.Pattern;

public class UnixTypeConstants implements ConstantsInterface
{
    public static final String UNIX      = "UNIX";
    public static final String UNIXTYPE  = "UnixType";
    public static final String LINUXTYPE = "LinuxType";

    public static final Pattern UNIX_SUBSTITUTE_REGEX      = Pattern.compile("$\\w+");
    public static final DynamicMap<UNIX_STYLES> UNIX_MAP   = new DynamicMap<>();
    public static final DynamicMap<LINUX_STYLES> LINUX_MAP = new DynamicMap<>();

    public enum UNIX_STYLES
    {
        AS400(),
        HPUX("HP-UX"),
        SOLARIS(null, "SunOS"),
        AIX(),
        ZLINUX(null, "zLinux"),
        IRIX(),
        DARWIN(null, "Darwin/MAC"),
        OTHER("UNKNOWN", "UNKNOWN");

        private String rep = null;
        private String translation = null;

        UNIX_STYLES() {
            this.setName(this.name());
            this.setTranslation(this.name());
        }

        UNIX_STYLES(String s)
        {
            this.setName(s);
            this.setTranslation(this.name());
        }

        UNIX_STYLES(String s, String t)
        {
            if ( s != null )
                this.setName(s);
            else
                this.setName(this.name());
            this.setTranslation(t);
        }

        public void setName(final String rep)
        {
            this.rep = rep;
        }
        public void setTranslation(final String translation) { this.translation = translation; }
        public final String getName() { return this.rep; }

        public final String getTranslation() { return this.translation; }
    }

    public enum LINUX_STYLES
    {
        REDHAT("RedHat"),
        CENTOS("CentOS"),
        UBUNTU("Ubuntu"),
        DEBIAN("Debian"),
        SUSE("SuSE"),
        OTHER("UNKNOWN");

        private String rep = null;

        LINUX_STYLES(String s)
        {
            this.setName(s);
        }

        public void setName(final String rep)
        {
            this.rep = rep;
        }

        public final String getName() { return this.rep; }
    }

    public enum UNIX_SHELL_STYLES implements ShellTypeInterface
    {
        BOURNE_SHELL("/bin/sh"),
        BASH_SHELL("/bin/bash"),
        C_SHELL("/bin/csh"),
        TC_SHELL("/bin/tcsh"),
        Z_SHELL("/bin/zsh"),
        DASH_SHELL("/bin/dash"),
        KORN_SHELL("/bin/ksh");

        private String shellPath = null;

        UNIX_SHELL_STYLES(String s)
        {
            this.setShellPath(s);
        }

        public void setShellPath(final String rep)
        {
            this.shellPath = rep;
        }
        public final ShellType getShell() { return new ShellType(this.shellPath); }

        public final String getShellPath() { return this.shellPath; }
    }

    private static boolean isInitialized = false;

    public static UnixTypeConstants getInstance()
    {
        if ( ! UnixTypeConstants.isInitialized )
        {
            UnixTypeConstants.isInitialized = true;
            UnixTypeConstants.initialize();
        }
        return UnixTypeConstantsHolder.INSTANCE;
    }

    public static void installShells()
    {
        for ( UNIX_SHELL_STYLES ss : UNIX_SHELL_STYLES.values() )
            MachineTypeConstants.SHELL_MAP.put(ss.getShellPath(), ss.getShell());
    }

    @Override
    public void reset()
    {
        UnixTypeConstants.initialize();
    }

    private static class UnixTypeConstantsHolder
    {
        public static final UnixTypeConstants INSTANCE = new UnixTypeConstants();
    }

    private static void initialize()
    {
        for ( UNIX_STYLES u : UNIX_STYLES.values() )
            UnixTypeConstants.UNIX_MAP.put(u, u.getName());

        for ( LINUX_STYLES l : LINUX_STYLES.values() )
            UnixTypeConstants.LINUX_MAP.put(l, l.getName());
    }
}
