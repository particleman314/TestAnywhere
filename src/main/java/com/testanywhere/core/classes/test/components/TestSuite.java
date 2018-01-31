package com.testanywhere.core.classes.test.components;

import com.testanywhere.core.classes.test.identification.TestDesignation;
import com.testanywhere.core.classes.test.identification.TestIdGenerator;
import com.testanywhere.core.classes.test.statistics.TestStatistic;
import com.testanywhere.core.utilities.logging.TextManager;
import org.apache.commons.collections4.SortedBag;
import org.apache.commons.collections4.bag.TreeBag;

import java.util.*;

public class TestSuite extends TestObject {

    private TestStatistic suiteCollector;
    private String suiteName;
    private Integer suiteID;
    private List<Test> associatedTests;
    private List<Integer> testRunIDs;

    public TestSuite() {
        this._initialize();
    }

    public TestSuite( String testSuiteName ) {
        super();
        if ( ! TextManager.validString(testSuiteName) ) {
            suiteName = "";
        }
    }

    public TestSuite addTestToSuite( Test t ) {
        if ( t == null ) { return this; }
        if ( this.associatedTests.contains(t) ) { return this; }
        this.associatedTests.add(t);
        return this;
    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public void buildObjectOutput(int numTabs) {

    }

    public int getNumberAssertionsByType( TestStatistic.ASSERT_RESULT type ) {
        int numberAssertions = 0;
        if ( this.associatedTests.isEmpty() ) { return numberAssertions; }

        for ( Test t : this.associatedTests ) {
            numberAssertions += t.getNumberAssertionsByType(type);
        }
        return numberAssertions;
    }

    public int getNumberTestsByType(TestResult.TESTTYPE tr) {
        int numberMatchingTests = 0;
        if ( this.associatedTests.isEmpty() ) { return numberMatchingTests; }

        for ( Test t : this.associatedTests ) {
            if ( t.getTestResult().equals(tr) ) { numberMatchingTests++; }
        }

        return numberMatchingTests;
    }

    public int getNumberErrorAssertions() {
        return this.suiteCollector.getField(TestStatistic.ASSERT_RESULT.ERROR);
    }


    public int getSuiteID() {
        return this.suiteID;
    }

    public void setSuiteName( final String suiteName ) {
        if ( ! TextManager.validString(suiteName) ) { return; }
        this.suiteName = suiteName;
    }


    private void _initialize() {
        this.suiteCollector = new TestStatistic();

        this.associatedTests = new LinkedList<>();
        this.testRunIDs = new LinkedList<>();

        this.suiteID = TestIdGenerator.generate( new TestDesignation(this) );
    }
}
