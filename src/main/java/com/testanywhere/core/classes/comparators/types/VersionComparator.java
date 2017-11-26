package com.testanywhere.core.classes.comparators.types;

import com.testanywhere.core.utilities.class_support.Cast;
import com.testanywhere.core.classes.comparators.ComparatorConstants;
import com.testanywhere.core.classes.comparators.GeneralizedComparator;
import com.testanywhere.core.classes.support.MethodConverter;
import com.testanywhere.core.classes.support.version.Version;
import com.testanywhere.core.classes.support.version.VersionConstants;
import com.testanywhere.core.classes.support.version.VersionTokenizer;
import com.testanywhere.core.utilities.logging.TextManager;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.regex.Pattern;

public class VersionComparator extends GeneralizedComparator<Version>
{
	private static final Logger logger = Logger.getLogger(VersionComparator.class);
	
	static
	{
		VersionConstants.getInstance(); // Need to initialize
	}
	
	public VersionComparator() 
	{
        this(VersionConstants.COMPARISON_FUNCTION.EQUALS.getRep(), VersionComparator.logger);
	}

	public VersionComparator( final VersionConstants.COMPARISON_FUNCTION compType )
	{
		super(compType, VersionComparator.logger);
	}
	
    public VersionComparator(final String requestedResult, final Logger logger)
    {
    	super(requestedResult, logger);
     }

    public VersionComparator( final VersionComparator otherComp )
    {
        super(ComparatorConstants.getComparisonType(otherComp.getSpecificRequest()), VersionComparator.logger);
    }

 	public String getStringRepresentation()
	{
		return VersionConstants.getInstance().decodeComparison(this.getSpecificRequest());
	}

    @Override
    public boolean equals(final Version v1, final Version v2)
    {
        return this.compare(v1, v2) == ComparatorConstants.EQUALS;
    }

    public boolean compareRequest(final Version o1, final Version o2)
    {
    	this.debug(o1.toString() + " <--> " + o2.toString());
        return ( compare(o1, o2) == this.getSpecificRequest() );
    }
    
    @Override
    public int compare(final Version o1, final Version o2)
    {
        final int basic_comparison = this.basic_compare(o1, o2);
        if (basic_comparison != ComparatorConstants.NOT_COMPARABLE)
            return basic_comparison;

        this.debug("Comparing " + TextManager.specializeName(o1.toString()) + " vs. " + TextManager.specializeName(o2.toString()));
		
        VersionTokenizer tokenizer1 = new VersionTokenizer(o1.toString());
        VersionTokenizer tokenizer2 = new VersionTokenizer(o2.toString());

        int number1;
        int number2;

        String suffix1;
        String suffix2;

        while (tokenizer1.MoveNext()) 
        {
            if (!tokenizer2.MoveNext()) 
            {
                do 
                {
                    number1 = tokenizer1.getNumber();
                    suffix1 = tokenizer1.getSuffix();
                    if (number1 != 0 || suffix1.length() != 0) 
                    {
                        // Version one is longer than number two, and non-zero
                        return VersionConstants.GREATERTHAN;
                    }
                }
                while (tokenizer1.MoveNext());

                // Version one is longer than version two, but zero
                return VersionConstants.EQUALS;
            }

            number1 = tokenizer1.getNumber();
            suffix1 = tokenizer1.getSuffix();
            number2 = tokenizer2.getNumber();
            suffix2 = tokenizer2.getSuffix();

            if (number1 > number2) 
            {
                // Number one is greater than number two
                return VersionConstants.GREATERTHAN;
            }
            if (number1 < number2) 
            {
                // Number one is less than number two
                return VersionConstants.LESSTHAN;
            }

            boolean empty1 = suffix1.length() == 0;
            boolean empty2 = suffix2.length() == 0;

            if (empty1 && empty2) continue; // No suffixes
            if (empty1) return VersionConstants.LESSTHAN; // First suffix is empty (1.2 < 1.2b)
            if (empty2) return VersionConstants.GREATERTHAN; // Second suffix is empty (1.2a > 1.2)

            // Lexical comparison of suffixes
            int result = suffix1.compareTo(suffix2);
            if (result != VersionConstants.EQUALS) return result;

        }
        if (tokenizer2.MoveNext()) 
        {
            do {
                number2 = tokenizer2.getNumber();
                suffix2 = tokenizer2.getSuffix();
                if (number2 != 0 || suffix2.length() != 0) 
                {
                    // Version one is longer than version two, and non-zero
                    return VersionConstants.LESSTHAN;
                }
            }
            while (tokenizer2.MoveNext());

            // Version two is longer than version one, but zero
            return VersionConstants.EQUALS;
        }

        return VersionConstants.EQUALS;
    }

    @Override
    public Collection<Version> find( final Object versionID, final MethodConverter mc, final Collection<Version> data,
                                     final Boolean findMany ) throws NoSuchMethodException, InvocationTargetException,
                                                               IllegalAccessException
    {
        Method m = this.getClass().getMethod("convertToStdPattern", Object.class);
        mc.setConverter(m);
        return super.find(versionID, mc, data, findMany);
    }

    private Pattern convertToStdPattern( final Object vID )
    {
        Pattern p = null;

        if ( vID instanceof Pattern )
            p = Cast.cast(vID);

        if ( vID instanceof Version )
            p = Pattern.compile(((Version) vID).getVersion());

        if ( vID instanceof String )
            p = Pattern.compile((String) vID);

        return p;

    }
}