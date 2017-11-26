package com.testanywhere.core.classes.comparators.types;

import com.testanywhere.core.classes.comparators.ComparatorConstants;
import com.testanywhere.core.classes.comparators.GeneralizedComparator;
import com.testanywhere.core.classes.support.MethodConverter;
import com.testanywhere.core.utilities.class_support.Cast;
import com.testanywhere.core.utilities.logging.TextManager;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.regex.Pattern;

public class FieldComparator extends GeneralizedComparator<Field>
{
    private static final Logger logger = Logger.getLogger(FieldComparator.class);

    static
    {
        FieldComparatorConstants.getInstance(); // Need to initialize
    }

    public FieldComparator()
    {
        this(StringComparatorConstants.COMPARISON_FUNCTION.EQUALS.getRep(), FieldComparator.logger);
    }

    public FieldComparator(final FieldComparatorConstants.COMPARISON_FUNCTION compType)
    {
    	super(compType, FieldComparator.logger);
    }

    public FieldComparator(final String requestedResult, final Logger logger)
    {
    	super(requestedResult, logger);
    }

    public FieldComparator(final FieldComparator otherComp )
    {
        super(ComparatorConstants.getComparisonType(otherComp.getSpecificRequest()), FieldComparator.logger);
    }

	@Override
    public int compare(final Field o1, final Field o2)
    {
        this.debug("Comparing " + TextManager.specializeName(o1.getName()) + " vs. " + TextManager.specializeName(o2.getName()));

        final int basic_comparison = this.basic_compare(o1, o2);
        if (basic_comparison == ComparatorConstants.NOT_COMPARABLE)
            return o1.getName().compareTo(o2.getName());

        return basic_comparison;
    }
    
    @Override
    public String toString()
    {
    	return this.getStringRepresentation();
    }

    @Override
    public boolean equals( final Field f1, final Field f2 )
    {
        return this.compare(f1, f2) == ComparatorConstants.EQUALS;
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals( final Object o ) {
        if (o == null) return false;

        FieldComparator oipc = Cast.cast(o);
        return oipc != null && (this.getSpecificRequest() == oipc.getSpecificRequest());

    }
    
   public String getStringRepresentation()
    {
        return FieldComparatorConstants.getInstance().decodeComparison(this.getSpecificRequest());
    }

    public boolean compareRequest(final Field o1, final Field o2)
    {
        this.debug(o1 + " <--> " + o2);
        int result = compare(o1, o2);
        return result == this.getSpecificRequest();
    }

    @Override
    public Collection<Field> find( final Object s, final MethodConverter mc, final Collection<Field> data,
                                     final Boolean findMany ) throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException
    {
        Method m = this.getClass().getMethod("convertToStdPattern", Object.class);
        mc.setConverter(m);
        return super.find(s, mc, data, findMany);
    }

    public Collection<Field> find( final Object s, final Collection<Field> data, final Boolean findMany ) throws NoSuchMethodException,
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

        if ( sID instanceof Field )
            p = Pattern.compile(((Field) sID).getName());

        if ( sID instanceof String )
            p = Pattern.compile((String) sID);

        return p;
    }
}
