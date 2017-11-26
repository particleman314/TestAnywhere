package com.testanywhere.core.classes.comparators;

import com.testanywhere.core.classes.support.MethodConverter;
import com.testanywhere.core.utilities.class_support.Cast;
import com.testanywhere.core.utilities.classes.Range;
import com.testanywhere.core.utilities.logging.DisplayManager;
import com.testanywhere.core.utilities.logging.DisplayType;
import com.testanywhere.core.utilities.logging.OutputDisplay;
import com.testanywhere.core.utilities.logging.Tabbing;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class GeneralizedComparator<T> extends OutputDisplay implements Comparator<T>
{
    protected Range<Integer> compareLimits;
    protected int specificRequest;

    public GeneralizedComparator()
    {
        super();
        this.initialize();
    }

    public GeneralizedComparator(final ComparatorConstants.COMPARISON_FUNCTION compType, final Logger logger)
    {
        this();
        this.replaceLog(logger);

        this.specificRequest = this.compareLimits.bound(compType.getValue());

        this.debug("Setting to '" + ComparatorConstants.decodeComparison(this.specificRequest) + "' check for comparator.");
    }

    public GeneralizedComparator(final String requestedResult, final Logger logger)
    {
    	this();
    	this.replaceLog(logger);

        switch ( requestedResult.toLowerCase() ) {
            case "lessthan":
            case "less":
                this.specificRequest = ComparatorConstants.LESSTHAN;
                break;
            case "greaterthan":
            case "greater":
                this.specificRequest = ComparatorConstants.GREATERTHAN;
                break;
            default:
                this.specificRequest = ComparatorConstants.EQUALS;
                break;
        }

        this.debug("Setting to '" + ComparatorConstants.decodeComparison(this.specificRequest) + "' check for comparator.");
    }

	@Override
	public abstract int compare(final T o1, final T o2);

    @Override
    public void buildObjectOutput( final int numTabs )
    {
        this.__buildObjectOutput(numTabs, this.getClass());
    }

    @Override
    public boolean isNull()
    {
        return false;
    }

    public abstract boolean equals(final T o1, final T o2);

    public int getSpecificRequest()
    {
        return this.specificRequest;
    }

    public void setSpecificRequest( int request )
    {
        this.specificRequest = this.compareLimits.bound(request);
    }

    public Range<?> getCompareLimits() { return this.compareLimits; }

    public void setCompareLimits( Range<Integer> range ) { this.compareLimits = range; }

    public void __buildObjectOutput( int numTabs, final Class<?> comparatorType)
    {
        if ( numTabs < 0 ) numTabs = 0;
        Tabbing tabEnvironment = new Tabbing(numTabs);
        DisplayManager dm = this.getDM();

        String outerSpacer = tabEnvironment.getSpacer();
        dm.append(outerSpacer + "Comparator Type : " + comparatorType.getSimpleName(), DisplayType.TEXTTYPES.LABEL);

        tabEnvironment.increment();
        String innerSpacer = tabEnvironment.getSpacer();

        this.compareLimits.buildObjectOutput(tabEnvironment.numberTabs());
        dm.addFormatLines(this.compareLimits.getDM().getLines());
        dm.append(innerSpacer + "Requested Comparison : " + ComparatorConstants.getComparisonType(this.specificRequest));
    }

    public Collection<T> find( final Object o, final MethodConverter methodData, final Collection<T> data ) throws NoSuchMethodException,
                                                                                            IllegalAccessException,
                                                                                            InvocationTargetException
    {
        return this.find(o, methodData, data, false);
    }

    public Collection<T> find( final Object o, final MethodConverter methodData, final Collection<T> data, Boolean findMany ) throws NoSuchMethodException,
                                                                                                              InvocationTargetException,
                                                                                                              IllegalAccessException
    {
        Collection<T> matches = new LinkedList<>();

        Pattern p = methodData.convert(o);

        if ( p == null || p.pattern().length() < 1 ) return matches;
        if ( data.isEmpty() ) return matches;

        if ( findMany == null ) findMany = false;

        for ( T t : data )
        {
            String methodName = methodData.getMethodName();
            Method dataMethod = t.getClass().getMethod(methodName, methodData.getMethodDataClasses());
            Object o_result = dataMethod.invoke(t, methodData.getMethodDataObjects());

            String s = Cast.cast(o_result);
            if ( s == null ) break;
            Matcher m = p.matcher(s);

            if ( m.find() )
            {
                if ( ! findMany )
                {
                    matches.add(t);
                    break;
                }
            }
        }

        return matches;
    }

    protected int basic_compare( T o1, T o2 )
    {
        if ( o1 == null && o2 == null ) return ComparatorConstants.EQUALS;
        if ( o1 == null ) return ComparatorConstants.GREATERTHAN;
        if ( o2 == null ) return ComparatorConstants.LESSTHAN;

        return ComparatorConstants.NOT_COMPARABLE;
    }

    @SuppressWarnings("unchecked")
    private void initialize()
    {
        this.compareLimits = new Range(ComparatorConstants.COMPARISON_FUNCTION.LESSTHAN.getValue(),
                                       ComparatorConstants.COMPARISON_FUNCTION.GREATERTHAN.getValue());
        this.compareLimits.setEndPointInclusion(true);
    }
}
