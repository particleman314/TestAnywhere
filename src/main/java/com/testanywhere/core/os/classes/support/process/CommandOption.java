package com.testanywhere.core.os.classes.support.process;

import com.testanywhere.core.utilities.class_support.Cast;
import com.testanywhere.core.classes.class_support.CompartmentObject;
import com.testanywhere.core.utilities.class_support.functional_support.ListFunctions;
import com.testanywhere.core.utilities.classes.Pair;
import com.testanywhere.core.utilities.exceptions.ObjectCreationException;
import com.testanywhere.core.utilities.logging.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;

public class CommandOption extends OutputDisplay
{
    private Pair<String, Collection<String>> cmdPairing;
    private String connector;
    private Collection<String> aliases;
    private boolean isLongOption;

    public CommandOption()
    {
        super();
        this.initialize();
    }

    public CommandOption( final String ... opts )
    {
        this();
        this.__process(opts);
    }

    public CommandOption( final Collection<String> opts )
    {
        this();
        String[] optsArray = new String[opts.size()];
        opts.toArray(optsArray);
        this.__process(optsArray);
    }

    public CommandOption( final CommandOption other ) throws ObjectCreationException
    {
        this();
        this.connector = other.connector;
        this.isLongOption = other.isLongOption;
        try
        {
            this.cmdPairing = other.cmdPairing.copy();
        }
        catch (CloneNotSupportedException e)
        {
            throw new ObjectCreationException(CommandOption.class);
        }
        this.aliases = copy(other.aliases);
    }

    @Override
    public void buildObjectOutput( int numTabs )
    {
        if (numTabs < 0) numTabs = 0;
        Tabbing tabEnvironment = new Tabbing(numTabs);
        DisplayManager dm = this.getDM();

        String outerSpacer = tabEnvironment.getSpacer();
        dm.append(outerSpacer + "Command Option :", DisplayType.TEXTTYPES.LABEL);

        tabEnvironment.increment();
        String innerSpacer = tabEnvironment.getSpacer();

        dm.append(innerSpacer + "Connector : " + TextManager.specializeName(this.getConnector()));

        Pair<String, Collection<String>> co = this.getArgumentAndOption();
        if ( co != null && ! co.isEmpty() )
        {
            Collection<String> arguments = co.getR();
            if ( arguments == null || arguments.isEmpty() )
                dm.append(innerSpacer + "Option : " + this.getOption());
            else
                dm.append(innerSpacer + "Option : " + this.getOption() + this.getConnector() + StringUtils.join(arguments, " "));
        }
        dm.append(innerSpacer + "Long Option Style : " + TextManager.StringRepOfBool(this.isLongOption(), "yn"));
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("OPT:" + this.getOption() + TextManager.STR_OUTPUTSEPARATOR);
        sb.append("CON:" + TextManager.specializeName(this.getConnector()) + TextManager.STR_OUTPUTSEPARATOR);
        sb.append("ARG:[" + StringUtils.join(this.getArgument(), " ") + "]");
        return sb.toString();
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals( final Object other )
    {
        if ( ! this.similar(other) ) return false;

        CommandOption cmdOpts = Cast.cast(other);
        String currentContents = StringUtils.join(this.getOption(), " ");
        String otherContents = StringUtils.join(cmdOpts.getOption(), " ");

        return currentContents.equals(otherContents);

    }

    @Override
    public boolean isNull()
    {
        return false;
    }

    public boolean similar( final Object other )
    {
        if ( other == null ) return false;

        CommandOption cmdOpts = Cast.cast(other);
        if ( cmdOpts == null ) return false;

        if ( cmdOpts.isLongOption() != this.isLongOption() ) return false;
        return cmdOpts.getArgument().equals(this.getArgument());

    }

    public boolean isLongOption()
    {
        return this.isLongOption;
    }

    public Pair<String, Collection<String>> getArgumentAndOption()
    {
        return this.cmdPairing;
    }

    public String getOption()
    {
        return this.cmdPairing.getL();
    }

    public String getConnector() { return this.connector; }

    public Collection<String> getArgument()
    {
        return this.cmdPairing.getR();
    }

    public String[] getOptionAsArray()
    {
        Collection<String> opt = this.cmdPairing.getR();

        String[] result = null;
        if (opt.isEmpty()) return result;

        result = new String[opt.size()];
        result = opt.toArray(result);
        return result;
    }

    public boolean setOption( final String arg )
    {
        if ( ! TextManager.validString(arg) ) return false;
        this.cmdPairing.setL(arg);
        this.determineIfLongOption();
        return true;
    }

    public boolean setArgument( final Collection<String> optList )
    {
        if ( optList == null || optList.isEmpty() ) return false;
        ListFunctions.filterNull(optList);

        boolean result = true;
        CommandOption backup = new CommandOption(this);

        for ( String str : optList )
            result = result & this.setOption(str);

        if ( ! result ) this.assign(backup);
        return result;
    }

    public boolean setArgument( final String opt )
    {
        if ( ! TextManager.validString(opt) ) return false;

        if ( this.cmdPairing.second() == null ) this.cmdPairing.setR(new ArrayList<String>());
        this.cmdPairing.getR().add(opt);
        return true;
    }

    public void setConnector( final String connector )
    {
        this.connector = connector;
    }

    public boolean addAlias( final String aliasOpt ) {
        return TextManager.validString(aliasOpt) && (this.hasAlias(aliasOpt) || this.aliases.add(aliasOpt));
    }

    public boolean hasAlias( final String aliasOpt )
    {
        if ( ! TextManager.validString(aliasOpt) ) return false;
        return this.aliases.contains(aliasOpt);
    }

    public boolean removeAlias( final String aliasOpt ) {
        return TextManager.validString(aliasOpt) && this.hasAlias(aliasOpt) && this.aliases.remove(aliasOpt);
    }

    private void __process( final String ... opts )
    {
        int size = opts.length;

        if ( size == 0 )
            return;
        else
        {
            this.cmdPairing.setL(opts[0]);
            this.cmdPairing.setR(new ArrayList<String>());

            if ( size == 2 )
                this.cmdPairing.getR().add(opts[1]);
            else if ( size == 3 )
            {
                this.cmdPairing.getR().add(opts[2]);
                if ( ! TextManager.validString(opts[1], true) )
                    this.connector = " ";
                else
                    this.connector = opts[1];
            }
        }

        this.determineIfLongOption();
    }

    private void determineIfLongOption()
    {
        String arg = this.cmdPairing.first();
        if ( arg.startsWith("--") || ( arg.startsWith("/") && arg.length() > 2 ) || ( arg.startsWith("-") && arg.length() > 2 ))
            this.isLongOption = true;
    }

    private CompartmentObject<?> convertToCompartmentObject( final String[] inputs )
    {
        if ( inputs == null ) return null;
        return new CompartmentObject<>(inputs);
    }

    private void initialize()
    {
        this.cmdPairing = new Pair<>();
        this.isLongOption = false;
        this.connector = " ";
        this.aliases = new ArrayList<>();
    }
}
