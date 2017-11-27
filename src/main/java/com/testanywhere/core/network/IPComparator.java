package com.testanywhere.core.network;

import com.testanywhere.core.utilities.class_support.Cast;
import com.testanywhere.core.classes.comparators.ComparatorConstants;
import com.testanywhere.core.classes.comparators.GeneralizedComparator;
import com.testanywhere.core.classes.support.MethodConverter;
import com.testanywhere.core.utilities.logging.TextManager;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.regex.Pattern;

public class IPComparator extends GeneralizedComparator<IP>
{
    private static final Logger logger = Logger.getLogger(IPComparator.class);

    static 
    {
        IPConstants.getInstance(); // Need to initialize
    }

    public IPComparator()
    {
        this(ComparatorConstants.COMPARISON_FUNCTION.EQUALS.getRep(), IPComparator.logger);
    }

    public IPComparator(final ComparatorConstants.COMPARISON_FUNCTION compType)
    {
    	super(compType, IPComparator.logger);
    }

    public IPComparator(final String requestedResult, final Logger logger)
    {
    	super(requestedResult, logger);
    }

    @Override
    public int compare(final IP o1, final IP o2)
    {
    	this.debug("Comparing " + TextManager.specializeName(o1.toString()) + " vs. " + TextManager.specializeName(o2.toString()));
        return o1.compareTo(o2);
    }
    
    @Override
    public String toString()
    {
    	return this.getStringRepresentation();
    }
    
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals( final Object o ) {
        if (o == null) return false;

        IPComparator oipc = Cast.cast(o);
        return oipc != null && (this.specificRequest == oipc.specificRequest);

    }
    
    public String getStringRepresentation()
    {
        return IPConstants.getInstance().decodeComparison(this.specificRequest);
    }

    public boolean equals(final IP o1, final IP o2)
    {
        return this.compare(o1, o2) == ComparatorConstants.EQUALS;
    }

    public boolean compareRequest(final IP o1, final IP o2)
    {
    	this.debug(o1.toString() + " <--> " + o2.toString());
        int result = compare(o1, o2);
        return result == this.specificRequest;
    }

    @Override
    public Collection<IP> find( final Object ip, final MethodConverter mc, final Collection<IP> data,
                                final Boolean findMany ) throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException
    {
        Method m = this.getClass().getMethod("convertToStdPattern", Object.class);
        mc.setConverter(m);
        return super.find(ip, mc, data, findMany);
    }

    private Pattern convertToStdPattern( final Object ip )
    {
        Pattern p = null;

        if ( ip instanceof Pattern )
            p = Cast.cast(ip);

        if ( ip instanceof IP )
            p = Pattern.compile(((IP) ip).getIP());

        if ( ip instanceof String )
            p = Pattern.compile((String) ip);

        return p;
    }
}
