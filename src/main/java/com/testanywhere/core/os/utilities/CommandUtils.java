package com.testanywhere.core.os.utilities;

import com.testanywhere.core.utilities.classes.Pair;
import com.testanywhere.core.utilities.logging.LogConfiguration;
import com.testanywhere.core.utilities.logging.TextManager;
import com.testanywhere.core.os.classes.support.process.CommandOption;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandUtils {

    public static Logger logger;

    static
    {
        CommandUtils.logger = Logger.getLogger("CommandUtils");
        LogConfiguration.configure();
    }

    public static Collection<CommandOption> generateOptionsFromString( String opts )
    {
        Collection<CommandOption> allOptions = new ArrayList<>();
        Pair<Integer[], String[]> result = CommandUtils.breakOption(opts);

        if ( result == null ) return allOptions;

        Integer[] cutPoints = result.first();
        String[] units = result.second();

        int lastPt = 0;
        for ( Integer stride : cutPoints )
        {
            CommandOption co = new CommandOption();

            String arg = units[lastPt];
            co.setOption(arg);

            if ( stride > 1 ) {
                String connector = units[1 + lastPt];
                co.setConnector(connector);
            }

            if ( stride == 3 ) co.setArgument(units[2 + lastPt]);
            lastPt += stride;

            allOptions.add(co);
        }

        return allOptions;
    }

    public static Pair<Integer[], String[]> breakOption( String opt )
    {
        Collection<String> subOpts = new ArrayList<>();
        Collection<Integer> strides = new ArrayList<>();

        Matcher m = Pattern.compile("(?<=[-{1,2}|/])(?<name>[a-zA-Z0-9\\\\b-]*)(?<connector>[ |:|=])?(['|\"]*)(?<value>[\\w| ]*)?(['|\"]*)?(?=[ ]|$)").matcher(opt);
        int stpt = 0;
        while ( stpt <= opt.length() && m.find(stpt) )
        {
            String optSymbol = opt.substring(stpt, m.start());

            String arg = m.group("name");

            String connector = m.group("connector");
            if ( connector == null || connector.length() == 0 ) connector = " ";

            String option = m.group("value");

            if ( ! TextManager.validString(option) )
            {
                subOpts.add(optSymbol + arg);
                stpt += optSymbol.length() + arg.length() + connector.length();
                strides.add(1);
            }
            else
            {
                subOpts.add(optSymbol + arg);
                subOpts.add(connector);

                if (option.contains(" ")) {
                    if ("\"".equals(m.group(3)) && m.group(3).equals(m.group(5)))
                        subOpts.add("\"" + option + "\"");
                    else
                        subOpts.add("'" + option + "'");
                    stpt += 2;
                } else {
                    subOpts.add(option);
                }
                stpt += m.group(0).length();
                strides.add(3);
            }
        }

        if ( subOpts.size() < 1 ) return null;
        String[] subOptsAsStrArray = new String[subOpts.size()-1];
        Integer[] strideArray = new Integer[strides.size()-1];

        return new Pair<>( strides.toArray(strideArray), subOpts.toArray(subOptsAsStrArray) );
    }
}
