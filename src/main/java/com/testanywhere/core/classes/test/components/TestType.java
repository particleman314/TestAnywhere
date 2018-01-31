package com.testanywhere.core.classes.test.components;

import com.testanywhere.core.utilities.class_support.BaseClass;
import com.testanywhere.core.utilities.class_support.functional_support.ConstantsInterface;
import com.testanywhere.core.utilities.classes.Pair;

import java.util.TreeMap;

public class TestType implements ConstantsInterface {

    public static final int SHELL     = 0;
    public static final int PERL      = 1;
    public static final int PYTHON    = 2;

    public static final int RUBY      = 3;
    public static final int GO        = 4;
    public static final int JAVA      = 5;
    public static final int C         = 6;
    public static final int CPLUSPLUS = 7;

    public static TreeMap<TestType.TESTTYPE, Pair<String, Integer>> TESTTYPE_MAP = new TreeMap<>();

    public enum TESTTYPE
    {
        UNKNOWN(0,"Unknown"),
        SHELL(TestType.SHELL, "Bash Shell"),
        PERL(TestType.PERL, "Perl"),
        PYTHON(TestType.PYTHON, "Python"),
        RUBY(TestType.RUBY, "Ruby"),
        GO(TestType.GO, "Go"),
        JAVA(TestType.JAVA, "Java"),
        C(TestType.C, "C"),
        CPLUSPLUS(TestType.CPLUSPLUS, "C++");

        private Integer value = null;
        private String rep = null;

        TESTTYPE(Integer i, String s)
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
        TestType.initialize();
    }

    @Override
    public void reset() {
        TestType.initialize();
    }

    public static TestType.TESTTYPE getTestType(final String compType)
    {
        if (compType == null) return null;
        for (TestType.TESTTYPE cf : TestType.TESTTYPE_MAP.keySet())
        {
            if (TestType.TESTTYPE_MAP.get(cf).getL().equals(compType)) return cf;
        }
        return TestType.TESTTYPE.UNKNOWN;
    }

    public static TestType.TESTTYPE getTestType(final Integer compType)
    {
        if (compType == null) return null;
        for (TestType.TESTTYPE cf : TestType.TESTTYPE_MAP.keySet())
        {
            if (TestType.TESTTYPE_MAP.get(cf).getR().equals(compType)) return cf;
        }
        return TestType.TESTTYPE.UNKNOWN;
    }

    public static int decodeTestType(final String testType)
    {
        int result = TESTTYPE.UNKNOWN.value;

        if (testType == null) return result;
        for (Pair<String, Integer> p : TestType.TESTTYPE_MAP.values()) {
            if (testType.equals(p.first())) {
                result = p.second();
                break;
            }
        }
        return result;
    }

    public static String decodeTestType(final int compType)
    {
        String result = TestType.TESTTYPE_MAP.get(TestType.TESTTYPE.UNKNOWN).first();

        for (Pair<String, Integer> p : TestType.TESTTYPE_MAP.values())
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

        for (TestType.TESTTYPE TT : TestType.TESTTYPE.values())
        {
            pd.set(TT.getRep(), TT.getValue());
            TestType.TESTTYPE_MAP.put(TT, BaseClass.copy(pd));
        }
    }
}
