package com.testanywhere.core.classes.comparators;

import com.testanywhere.core.utilities.class_support.BaseClass;
import com.testanywhere.core.utilities.class_support.functional_support.ConstantsInterface;
import com.testanywhere.core.utilities.classes.Pair;

import java.util.TreeMap;

public class ComparatorConstants implements ConstantsInterface
{
    public static final int LESSTHAN       = -1;
    public static final int EQUALS         = 0;
    public static final int GREATERTHAN    = 1;

    public static final int NOT_COMPARABLE = 2;
    public static final int NOT_EQUAL      = 3;

    public static TreeMap<COMPARISON_FUNCTION, Pair<String, Integer>> COMPARISON_MAP = new TreeMap<>();

    public enum COMPARISON_FUNCTION
    {
        LESSTHAN(ComparatorConstants.LESSTHAN, "lessthan"),
        EQUALS(ComparatorConstants.EQUALS, "equals"),
        GREATERTHAN(ComparatorConstants.GREATERTHAN, "greaterthan");

        private Integer value = null;
        private String rep = null;

        COMPARISON_FUNCTION(Integer i, String s)
        {
            this.setValue(i);
            this.setRep(s);
        }

        public Integer getValue() {
            return this.value;
        }

        public void setValue(final Integer value) {
            this.value = value;
        }

        public String getRep() {
            return this.rep;
        }

        public void setRep(final String rep) {
            this.rep = rep;
        }
    }

    static
    {
        ComparatorConstants.initialize();
    }

    @Override
    public void reset() {
        ComparatorConstants.initialize();
    }

    public static COMPARISON_FUNCTION getComparisonType(final String compType)
    {
        if (compType == null) return null;
        for (COMPARISON_FUNCTION cf : ComparatorConstants.COMPARISON_MAP.keySet())
        {
            if (ComparatorConstants.COMPARISON_MAP.get(cf).getL().equals(compType)) return cf;
        }
        return COMPARISON_FUNCTION.EQUALS;
    }

    public static COMPARISON_FUNCTION getComparisonType(final Integer compType)
    {
        if (compType == null) return null;
        for (COMPARISON_FUNCTION cf : ComparatorConstants.COMPARISON_MAP.keySet())
        {
            if (ComparatorConstants.COMPARISON_MAP.get(cf).getR().equals(compType)) return cf;
        }
        return COMPARISON_FUNCTION.EQUALS;
    }

    public static int decodeComparison(final String compType)
    {
        int result = ComparatorConstants.EQUALS;

        if (compType == null) return result;
        for (Pair<String, Integer> p : ComparatorConstants.COMPARISON_MAP.values()) {
            if (compType.equals(p.first())) {
                result = p.second();
                break;
            }
        }
        return result;
    }

    public static String decodeComparison(final int compType)
    {
        String result = ComparatorConstants.COMPARISON_MAP.get(COMPARISON_FUNCTION.EQUALS).first();

        for (Pair<String, Integer> p : ComparatorConstants.COMPARISON_MAP.values())
        {
            if (p.second() == compType) {
                result = p.first();
                break;
            }
        }
        return result;
    }

    protected static void initialize()
    {
        Pair<String, Integer> pd = new Pair<>();

        for (COMPARISON_FUNCTION CF : COMPARISON_FUNCTION.values())
        {
            pd.set(CF.getRep(), CF.getValue());
            ComparatorConstants.COMPARISON_MAP.put(CF, BaseClass.copy(pd));
        }
    }
}
