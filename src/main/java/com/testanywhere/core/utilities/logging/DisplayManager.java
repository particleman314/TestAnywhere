package com.testanywhere.core.utilities.logging;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DisplayManager
{
    private static final Pattern TYPEPATTERN = Pattern.compile("\\{\\{\\{(\\w*)\\}\\}\\}");

    private static final String DEFAULT_SEPARATOR = ":";

    private Collection<String> outputLines;

    private String separator;
    private String connectWith;
    private String replacementSeparator;

    private boolean changed;
    private boolean changedPP;
    private boolean trim;

    private StringBuilder builder;

    public DisplayManager()
    {
        super();
        this.initialize();
    }

    public DisplayManager(final Collection<String> lines)
    {
        this();
        this.include(lines);
    }

    public DisplayManager(final String ... lines)
    {
        this();
        Collection<String> obj4Lines = Arrays.asList(lines);
        this.include(obj4Lines);
    }

    public String getPrettyPrint(int numTabs)
    {
        if ( numTabs < 0 ) numTabs = 0;
        StringBuilder sb = new StringBuilder();
        Tabbing tabEnvironment = new Tabbing(numTabs);

        String outerSpacer = tabEnvironment.getSpacer();
        sb.append(outerSpacer + "DisplayManager :" + TextManager.EOL);

        tabEnvironment.increment();
        String innerSpacer = tabEnvironment.getSpacer();

        sb.append(innerSpacer + "Lines                 --> " + this.size() + TextManager.EOL);
        sb.append(innerSpacer + "Separator             --> " + TextManager.specializeName(this.getSeparator()) + TextManager.EOL);
        sb.append(innerSpacer + "Replacement Separator --> " + TextManager.specializeName(this.getReplacementSeparator()) + TextManager.EOL);
        sb.append(innerSpacer + "Allow Trimming        --> " + TextManager.StringRepOfBool(this.canTrim(), "yn") + TextManager.EOL);
        if ( ! this.getLineConnector().equals(TextManager.EOL) )
            sb.append(innerSpacer + "Line Connector       --> " + this.getLineConnector() + TextManager.EOL);
        return sb.toString();
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("SZ:[" + this.size() + "]" + TextManager.STR_OUTPUTSEPARATOR);
        sb.append("SEP:[" + this.getSeparator() + "]" + TextManager.STR_OUTPUTSEPARATOR);
        sb.append("RPS:[" + this.getReplacementSeparator() + "]" + TextManager.STR_OUTPUTSEPARATOR);
        sb.append("TRIM:" + TextManager.StringRepOfBool(this.canTrim(), "yn"));

        return sb.toString();
    }

    public DisplayManager append( String line )
    {
        this.addLine(line);
        return this;
    }

    public DisplayManager append( String line, DisplayType.TEXTTYPES type )
    {
        this.addLine(line, type);
        return this;
    }

    public void addLine( final String line )
    {
        this.addLine(line, DisplayType.TEXTTYPES.TEXT);
    }

    public void addLine( final String line, DisplayType.TEXTTYPES type )
    {
        if ( ! TextManager.validString(line, true) ) return;
        if ( type == null ) type = DisplayType.TEXTTYPES.TEXT;
        if ( line.trim().length() < 1 ) type = DisplayType.TEXTTYPES.LABEL;

        Matcher m = DisplayManager.TYPEPATTERN.matcher(line);
        if ( m.find() )
            this.getLines().add(line);
        else
            this.getLines().add("{{{" + type.name() + "}}}" + line);

        this.changed = true;
        this.changedPP = true;
    }

    public void addFormatLines( Collection<String> lines )
    {
        if ( lines != null && ! lines.isEmpty() )
            for ( String s : lines )
                this.addLine(s);
    }

    public String forDisplay()
    {
        if ( ! this.changed ) return this.getBuilder().toString();

        this.resetTextBuilder();
        for( final String s : this.getLines() )
        {
            String modified = s;
            Matcher m = DisplayManager.TYPEPATTERN.matcher(s);
            if ( m.find() ) modified = m.replaceFirst("");
            if ( this.canTrim()) modified = modified.trim();

            this.builder.append(modified);
            if ( this.getLineConnector() != null) this.builder.append(this.getLineConnector());
        }

        this.changed = false;
        return this.getBuilder().toString();
    }

    public String forFormattedDisplay()
    {
        if ( ! this.changedPP ) return this.getBuilder().toString();

        this.resetTextBuilder();
        int maxSubStrLength = -1;

        String sep = this.getSeparator();

        Collection<String> lines = this.getLines();
        // Pass #1
        for( final String s : lines )
        {
            String modified = s;
            Matcher m = DisplayManager.TYPEPATTERN.matcher(s);
            if ( m.find() ) {
                if ( m.group(1).equals(DisplayType.TEXTTYPES.LABEL.name()) )
                    continue;
                else
                    modified = m.replaceFirst("");
            }

            int strLimit = modified.indexOf(sep);
            if ( strLimit > maxSubStrLength ) maxSubStrLength = strLimit;
        }

        // Pass #2
        for ( final String s : lines )
        {
            String modified = s;
            Matcher m = DisplayManager.TYPEPATTERN.matcher(s);
            if ( m.find() ) {
                modified = m.replaceFirst("");
                if (m.group(1).equals(DisplayType.TEXTTYPES.LABEL.name())) {
                    this.builder.append(modified);
                    if ( this.getLineConnector() != null) this.builder.append(this.getLineConnector());
                    continue;
                }
            }

            int strLimit = modified.indexOf(sep);
            String rebuiltStr;
            if ( strLimit < 0 ) rebuiltStr = modified;
            else {
                if (this.getReplacementSeparator() == null) {
                    rebuiltStr = modified.substring(0, strLimit) + StringUtils.repeat(" ", (maxSubStrLength - strLimit)) + sep + modified.substring(strLimit + sep.length());
                } else {
                    rebuiltStr = modified.substring(0, strLimit) + StringUtils.repeat(" ", (maxSubStrLength - strLimit)) + this.getReplacementSeparator() +
                            modified.substring(strLimit + sep.length());
                }
            }
            if (m.group(1).equals(DisplayType.TEXTTYPES.TEXT.name()))
                this.builder.append(rebuiltStr);

            if ( this.getLineConnector() != null ) this.builder.append(this.getLineConnector());
        }

        if ( this.builder.length() > 0 )
            this.builder.deleteCharAt(this.builder.length() - 1);
        this.changedPP= false;
        return this.getBuilder().toString();
    }

    public void clear()
    {
        this.getLines().clear();
        this.resetTextBuilder();
        this.setSeparator(DisplayManager.DEFAULT_SEPARATOR);
        this.setReplacementSeparator(TextManager.EOL);
        this.changed = true;
        this.changedPP = true;
    }

    public int size() { return this.getLines().size(); }

    public void setSeparator( String separator )
    {
        if ( ! TextManager.validString(separator, true) ) return;
        if ( ! this.separator.equals(separator) )
        {
            this.changed = true;
            this.changedPP = true;
        }

        this.separator = separator;
    }

    public void setReplacementSeparator( String replacementSeparator )
    {
        if ( ( this.replacementSeparator != null && ! this.replacementSeparator.equals(replacementSeparator) ) ||
                ( replacementSeparator != null && ! replacementSeparator.equals(this.replacementSeparator) ) )
        {
            this.changed = true;
            this.changedPP = true;
        }
        this.replacementSeparator = replacementSeparator;
    }

    public void setLineConnector( String lineConnector )
    {
        if ( ! TextManager.validString(lineConnector, true) ) return;
        if ( ! this.connectWith.equals(lineConnector) )
        {
            this.changed = true;
            this.changedPP = true;
        }
        this.connectWith = lineConnector;
    }

    public void allowTrim( boolean trimming )
    {
        if ( this.trim != trimming ) this.changed = true;
        this.trim = trimming;
    }

    public String getSeparator() { return this.separator; }

    public String getReplacementSeparator() { return this.replacementSeparator; }

    public String getLineConnector() { return this.connectWith; }

    public void resetTextBuilder() { this.getBuilder().setLength(0); }

    public void resetCachedOutput() { this.outputLines.clear(); }

    public void replaceLines( String... lines )
    {
        this.getLines().clear();
        this.include(Arrays.asList(lines));
        this.changed = true;
        this.changedPP = true;
    }

    public void replaceLines( Collection<String> lines )
    {
        this.getLines().clear();
        this.include(lines);
        this.changed = true;
        this.changedPP = true;
    }

    public Collection<String> getLines() { return this.outputLines; }

    private boolean canTrim() { return this.trim; }

    private StringBuilder getBuilder() { return this.builder; }

    private void include( Collection<String> lines )
    {
        if ( lines == null ) return;
        for ( String s : lines )
        {
            String modified = s;
            Matcher m = DisplayManager.TYPEPATTERN.matcher(s);
            if ( m.find() ) modified = m.replaceFirst("");
            this.addLine(modified);
        }
    }

    private void initialize()
    {
        this.outputLines = new ArrayList<>();
        this.builder = new StringBuilder();

        this.separator = DisplayManager.DEFAULT_SEPARATOR;
        this.replacementSeparator = null;
        this.connectWith = TextManager.EOL;

        this.trim = false;
        this.changed = false;
        this.changedPP = false;
    }
}
