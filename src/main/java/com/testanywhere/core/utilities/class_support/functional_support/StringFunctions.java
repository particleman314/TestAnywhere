package com.testanywhere.core.utilities.class_support.functional_support;

import com.testanywhere.core.utilities.classes.Pair;
import com.testanywhere.core.utilities.logging.LogConfiguration;
import com.testanywhere.core.utilities.logging.TextManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringFunctions
{
    public static Logger logger;

    public static final String DEFAULT_COMMENT_MARKER = "#";
    public static String COMMENT_MARKER = DEFAULT_COMMENT_MARKER;

    static
    {
        StringFunctions.logger = Logger.getLogger("StringFunctions");
        LogConfiguration.configure();
    }

    public static boolean isComment( final String data )
    {
        return StringFunctions.isComment(data, StringFunctions.COMMENT_MARKER);
    }

    public static boolean isComment( final String data, final String commentMarker ) {
        return !TextManager.validString(data) || TextManager.validString(commentMarker) && data.startsWith(commentMarker);
    }

    public static void changeCommentMarker( final String newCommentMarker )
    {
        if ( !TextManager.validString(newCommentMarker) ) return;
        StringFunctions.logger.log(Level.DEBUG, "Changed comment marker from " + StringFunctions.COMMENT_MARKER + " to " + newCommentMarker);
        StringFunctions.COMMENT_MARKER = newCommentMarker;
    }

    public static boolean comparisonWithNull( final String s1, final String s2 )
    {
        if ( s1 == null && s2 == null ) return true;
        if ( ( s1 != null && s2 != null ) && s1.equals(s2) ) return true;
        return (s2 != null && s1 != null) && s2.equals(s1);
    }

    public static void resetCommentMarker()
    {
        StringFunctions.logger.log(Level.DEBUG, "Reset comment marker to " + StringFunctions.DEFAULT_COMMENT_MARKER);
        StringFunctions.COMMENT_MARKER = StringFunctions.DEFAULT_COMMENT_MARKER;
    }

    public static String substitute( final String str, final Pattern pat )
    {
        return StringFunctions.substitute(str, pat, "");
    }

    public static String substitute( String str, final Pattern pat, final String replacement )
    {
        if ( ! TextManager.validString(str) || pat == null || ! TextManager.validString(replacement) ) return null;

        String updateStr = str;
        boolean revisit;
        int count = 0;

        do
        {
            count++;
            revisit = false;
            Matcher m = pat.matcher(str);

            while ( m.find() )
            {
                revisit = true;

                Pair<String, String> results = StringFunctions.threeWaySplit(str, m.start(), m.end(), replacement);
                if ( results.second() == null )
                {
                    revisit = false;
                    break;
                }

                updateStr = StringUtils.replace(updateStr, results.first(), results.second());
            }
            if ( revisit ) str = updateStr;
        } while ( revisit );

        if ( count == 1 ) return str;
        return updateStr;
    }

    private static Pair<String, String> threeWaySplit( final String str, final int marker1, final int marker2, String replacement)
    {
        if ( str == null ) return null;
        if ( marker2 < marker1 ) return null;

        String fullsubstr = str.substring(marker1, marker2);
        String substr = str.substring( marker1 + 1, marker2 - 1 );

        if ( ! TextManager.validString(replacement, true) )
            replacement = System.getenv(substr);

        return new Pair<>(fullsubstr, replacement);
    }

    private StringFunctions() {}
}
