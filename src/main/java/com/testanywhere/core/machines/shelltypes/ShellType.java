package com.testanywhere.core.machines.shelltypes;

public class ShellType implements ShellTypeInterface
{
    private String shellPath;

    public ShellType()
    {
        super();
        this.initialize();
    }

    public ShellType( String path )
    {
        this();
        this.shellPath = path;
    }

    @Override
    public String getShellPath() {
        return shellPath;
    }

    private void initialize()
    {
        this.shellPath = null;
    }
}
