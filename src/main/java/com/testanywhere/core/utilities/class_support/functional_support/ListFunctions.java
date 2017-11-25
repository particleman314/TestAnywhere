package com.testanywhere.core.utilities.class_support.functional_support;

import com.testanywhere.core.utilities.class_support.BaseClass;
import com.testanywhere.core.utilities.class_support.Cast;
import com.testanywhere.core.utilities.classes.NullObject;
import com.testanywhere.core.utilities.logging.LogConfiguration;
import com.testanywhere.core.utilities.logging.Tabbing;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class ListFunctions
{
    public static Logger logger;

    static
    {
        ListFunctions.logger = Logger.getLogger("ListFunctions");
        LogConfiguration.configure();
    }

    public static <T extends Comparable<? super T>> Collection<T> asSortedList(Collection<T> c)
    {
        if ( c == null || c.isEmpty() ) return null;

        List<T> list = new ArrayList<>(c);
        Collections.sort(list);
        return list;
    }

    public static<T> void filterNull( Collection<T> c )
    {
        if ( c == null || c.isEmpty() ) return;
        Iterator<T> iter = c.iterator();
        while ( iter.hasNext() )
        {
            T elem = iter.next();
            if (elem == null)
                iter.remove();
            else if ( elem instanceof NullObject )
                iter.remove();
            else {
                try {
                    Method m = elem.getClass().getMethod("isNull", (Class[]) null);
                    Boolean result = Cast.cast(m.invoke(elem, (Object[]) null));
                    if ( result != null && result) iter.remove();
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
                }
            }
        }
    }

    public static<T> Collection<String> asNumberedList( Collection<T> c, Tabbing tabEnvironment )
    {
        if ( c == null || c.isEmpty() ) return null;

        Collection<String> numListData = new ArrayList<>();

        Integer count   = 1;
        int maxCharSize = Integer.toString(c.size()).length();
        String spacer   = tabEnvironment.getSpacer();

        for ( T data : c )
        {
            String countStr = StringUtils.repeat(" ", maxCharSize - count.toString().length());
            String s = spacer + countStr + count.toString() + ") ";

            if ( data instanceof BaseClass ) {
                BaseClass myData = (BaseClass) data;
                s = s + BaseClass.checkIsNull(myData);
            }
            else
                s = s + data.toString();

            numListData.add(s);
            ++count;
        }
        return numListData;
    }

    private ListFunctions() {}
}
