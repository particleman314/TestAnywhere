package com.testanywhere.core.classes.test.statistics;

import com.testanywhere.core.utilities.logging.OutputDisplay;

import java.util.TreeMap;

public class TestStatistic extends OutputDisplay {

    public enum ASSERT_RESULT {
        UNKNOWN(-1),
        PASS(0),
        FAIL(1),
        SKIP(2),
        ERROR(3),
        TOTAL(4);

        private int value;

        ASSERT_RESULT(Integer i) {
            this.value = i;
        }
    }

    private TreeMap<ASSERT_RESULT, Integer> assertionMap;

    public TestStatistic() {
        this.initialize();
    }

    @Override
    public void buildObjectOutput(int numTabs) {

    }

    @Override
    public boolean isNull() {
        return false;
    }

    public Integer getField( ASSERT_RESULT field ) {
        if ( ! this.assertionMap.containsKey(field) ) { return 0; }
        return this.assertionMap.get(field);
    }

    public void updateField( ASSERT_RESULT field, Integer count ) {
        if ( ! this.assertionMap.containsKey(field) || field == ASSERT_RESULT.TOTAL ) { return; }

        Integer result = this.assertionMap.get(field) + count;
        this.assertionMap.put(field, result);

        result = this.assertionMap.get(ASSERT_RESULT.TOTAL);
        this.assertionMap.put(ASSERT_RESULT.TOTAL, result + count);
    }

    private void initialize() {
        this.assertionMap = new TreeMap<>();

        this.assertionMap.put(ASSERT_RESULT.UNKNOWN, 0);
        this.assertionMap.put(ASSERT_RESULT.PASS, 0);
        this.assertionMap.put(ASSERT_RESULT.FAIL, 0);
        this.assertionMap.put(ASSERT_RESULT.SKIP, 0);
        this.assertionMap.put(ASSERT_RESULT.ERROR, 0);
        this.assertionMap.put(ASSERT_RESULT.TOTAL, 0);
    }
}
