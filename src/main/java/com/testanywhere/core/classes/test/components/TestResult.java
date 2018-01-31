package com.testanywhere.core.classes.test.components;

import com.testanywhere.core.utilities.class_support.BaseClass;
import com.testanywhere.core.utilities.class_support.functional_support.ConstantsInterface;
import com.testanywhere.core.utilities.classes.Pair;

import java.util.TreeMap;

public class TestResult implements ConstantsInterface {

    public static final int PASS     = 0;
    public static final int FAIL     = 1;
    public static final int SKIP     = 2;

    public static TreeMap<TestResult.TESTTYPE, Pair<String, Integer>> TESTRESULT_MAP = new TreeMap<>();

    public enum TESTTYPE
    {
        PASS(TestResult.PASS,"PASS"),
        FAIL(TestResult.FAIL, "FAIL"),
        SKIP(TestResult.SKIP, "SKIP");

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
        TestResult.initialize();
    }

    @Override
    public void reset() {
        TestResult.initialize();
    }

    public static TestResult.TESTTYPE getTestType(final String compType)
    {
        if (compType == null) return null;
        for (TestResult.TESTTYPE cf : TestResult.TESTRESULT_MAP.keySet())
        {
            if (TestResult.TESTRESULT_MAP.get(cf).getL().equals(compType)) return cf;
        }
        return TestResult.TESTTYPE.SKIP;
    }

    public static TestResult.TESTTYPE getTestType(final Integer compType)
    {
        if (compType == null) return null;
        for (TestResult.TESTTYPE cf : TestResult.TESTRESULT_MAP.keySet())
        {
            if (TestResult.TESTRESULT_MAP.get(cf).getR().equals(compType)) return cf;
        }
        return TestResult.TESTTYPE.SKIP;
    }

    public static int decodeTestType(final String testType)
    {
        int result = TESTTYPE.SKIP.value;

        if (testType == null) return result;
        for (Pair<String, Integer> p : TestResult.TESTRESULT_MAP.values()) {
            if (testType.equals(p.first())) {
                result = p.second();
                break;
            }
        }
        return result;
    }

    public static String decodeTestType(final int compType)
    {
        String result = TestResult.TESTRESULT_MAP.get(TestResult.TESTTYPE.SKIP).first();

        for (Pair<String, Integer> p : TestResult.TESTRESULT_MAP.values())
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

        for (TestResult.TESTTYPE TT : TestResult.TESTTYPE.values())
        {
            pd.set(TT.getRep(), TT.getValue());
            TestResult.TESTRESULT_MAP.put(TT, BaseClass.copy(pd));
        }
    }
}
