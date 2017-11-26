package com.testanywhere.core.classes.comparators.types;

import com.testanywhere.core.classes.comparators.ComparatorConstants;
import com.testanywhere.core.classes.comparators.GeneralizedComparator;
import com.testanywhere.core.classes.support.MethodConverter;
import com.testanywhere.core.utilities.class_support.Cast;
import com.testanywhere.core.utilities.logging.Tabbing;
import com.testanywhere.core.utilities.logging.TextManager;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.regex.Pattern;

public class StringComparator extends GeneralizedComparator<String>
{
    private static final Logger logger = Logger.getLogger(StringComparator.class);
    private boolean isCaseSensitive;

    static 
    {
        StringComparatorConstants.getInstance(); // Need to initialize
    }

    public StringComparator()
    {
        this(StringComparatorConstants.COMPARISON_FUNCTION.EQUALS.getRep(), StringComparator.logger);
        this.initialize();
    }

    public StringComparator(final StringComparatorConstants.COMPARISON_FUNCTION compType)
    {
    	super(compType, StringComparator.logger);
        this.initialize();
    }

    public StringComparator(final String requestedResult, final Logger logger)
    {
    	super(requestedResult, logger);
        this.initialize();
    }

    public StringComparator( final StringComparator otherComp )
    {
        super(ComparatorConstants.getComparisonType(otherComp.getSpecificRequest()), StringComparator.logger);
        this.isCaseSensitive = otherComp.isCaseSensitive;
    }

    @Override
    public void buildObjectOutput(int numTabs)
    {
        super.__buildObjectOutput(numTabs, this.getClass());
        Tabbing tabEnvironment = new Tabbing(numTabs);
        tabEnvironment.increment();
        String innerSpacer = tabEnvironment.getSpacer();

        this.getDM().append( innerSpacer + "Is Case Sensitive : " + TextManager.StringRepOfBool(this.isCaseSensitive, "yn"));
    }

	@Override
    public int compare(final String o1, final String o2) {
        this.debug("Comparing " + TextManager.specializeName(o1) + " vs. " + TextManager.specializeName(o2));

        final int basic_comparison = this.basic_compare(o1, o2);
        if (basic_comparison == ComparatorConstants.NOT_COMPARABLE)
        {
            if (!this.isCaseSensitive)
                return o1.toLowerCase().compareTo(o2.toLowerCase());
            return o1.compareTo(o2);
        }

        return basic_comparison;
    }
    
    @Override
    public String toString()
    {
    	return this.getStringRepresentation();
    }

    @Override
    public boolean equals( final String s1, final String s2 )
    {
        return this.compare(s1, s2) == ComparatorConstants.EQUALS;
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals( final Object o ) {
        if (o == null) return false;

        StringComparator oipc = Cast.cast(o);
        return oipc != null && (this.getSpecificRequest() == oipc.getSpecificRequest());

    }

    public void setCaseSensitivity( boolean caseSensitive )
    {
        this.isCaseSensitive = caseSensitive;
    }

    public String getStringRepresentation()
    {
        return StringComparatorConstants.getInstance().decodeComparison(this.getSpecificRequest());
    }

    public boolean compareRequest(final String o1, final String o2)
    {
        this.debug(o1 + " <--> " + o2);
        int result = compare(o1, o2);
        return result == this.getSpecificRequest();
    }

    @Override
    public Collection<String> find( final Object s, final MethodConverter mc, final Collection<String> data,
                                     final Boolean findMany ) throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException
    {
        Method m = this.getClass().getMethod("convertToStdPattern", Object.class);
        mc.setConverter(m);
        return super.find(s, mc, data, findMany);
    }

    public Collection<String> find( final Object s, final Collection<String> data, final Boolean findMany ) throws NoSuchMethodException,
                                                                                       InvocationTargetException,
                                                                                       IllegalAccessException
    {
        MethodConverter mc = new MethodConverter();
        mc.addMethodParam(String.class, s);

        Method m;

        // Not a public method if catch enabled
        try
        {
            m = this.getClass().getMethod("convertToStdPattern", Object.class);
        }
        catch ( NoSuchMethodException e )
        {
            m = this.getClass().getDeclaredMethod("convertToStdPattern", Object.class);
            m.setAccessible(true);
        }

        mc.setConverter(m);
        return super.find(this, mc, data, findMany);
    }

    private Pattern convertToStdPattern( final Object sID )
    {
        Pattern p = null;

        if ( sID instanceof Pattern )
            p = Cast.cast(sID);

        if ( sID instanceof String )
            p = Pattern.compile((String) sID);

        return p;
    }

    private void initialize()
    {
        this.isCaseSensitive = true;
    }
}
