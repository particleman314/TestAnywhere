package com.testanywhere.core.classes.test.components;

import com.testanywhere.core.classes.class_support.CompartmentObject;
import com.testanywhere.core.classes.test.identification.TestDesignation;
import com.testanywhere.core.classes.test.identification.TestIdGenerator;
import com.testanywhere.core.classes.test.statistics.TestStatistic;
import com.testanywhere.core.utilities.logging.TextManager;

import java.util.*;

public class Test extends TestObject {

    private TestStatistic statCollector;

    private int testID;
    private int priority;
    private String testName;

    private SortedSet<Integer> dependencies;
    private List<Integer> testSuiteIDs;

    private boolean isParameterizable;
    private Map<String, List<CompartmentObject<?>>> parameterSettings;

    private TestType.TESTTYPE testIdenificationType;
    private TestResult.TESTTYPE testResult;

    public Test() {
        this(TestType.TESTTYPE.UNKNOWN);
    }

    public Test(TestType.TESTTYPE testTypeIdentifier) {
        this._initialize();
        this.testIdenificationType = testTypeIdentifier;
    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public void buildObjectOutput(int numTabs) {

    }

    public TestResult.TESTTYPE getTestResult() { return this.testResult; }

    public int getTestPriority() { return this.priority; }

    public void setTestPriority( int priority ) {
        if ( priority <= 0 ) { priority = Integer.MAX_VALUE; }
        this.priority = priority;
    }

    public void toggleParameterization( boolean setting ) {
        this.isParameterizable = setting;
    }

    public Test addParameter(String paramName, List<CompartmentObject<?>> values) {
        if ( this.isParameterized() ) {
            if (!TextManager.validString(paramName)) {
                return this;
            }
            if (values.isEmpty()) {
                return this;
            }
            if (this.parameterSettings.containsKey(paramName)) {
                System.out.println("Overriding preset parameters");
            }
            this.parameterSettings.put(paramName, values);
        }
        return this;
    }

    public Test removeParameter( String paramName ) {
        if ( this.isParameterized() ) {
            if (!TextManager.validString(paramName)) {
                return this;
            }
            if (!this.parameterSettings.containsKey(paramName)) {
                System.out.println("Unable to remove non-existent parameters");
            }
            this.parameterSettings.remove(paramName);
        }
        return this;
    }

    public boolean isParameterized() {
        return this.isParameterizable;
    }

    public Test assignToSuite(int suiteID) {
        if ( suiteID <= 0 ) { return this; }
        if ( this.testSuiteIDs.contains(suiteID) ) { return this; }
        this.testSuiteIDs.add(suiteID);
        return this;
    }

    public int getTestID() { return this.testID; }

    public SortedSet<Integer> getDependencies() { return this.dependencies; }

    public Test addTestDependencyID(int testDepID) {
        if ( testDepID <= 0 ) { return this; }
        this.dependencies.add(testDepID);
        return this;
    }

    public Test addTestDependencyIDs( List<Integer> testDepIDs ) {
        return this;
    }

    public Test addTestDependencies( List<Test> testDeps ) {
        return this;
    }

    public int getNumberAssertionsByType( TestStatistic.ASSERT_RESULT type ) {
        return this.statCollector.getField(type);
    }

    public int getNumberAssertions() {
        return this.getNumberAssertionsByType(TestStatistic.ASSERT_RESULT.TOTAL);
    }

    public int getNumberPassingAssertions() {
        return this.getNumberAssertionsByType(TestStatistic.ASSERT_RESULT.PASS);
    }

    public int getNumberFailingAssertions() {
        return this.getNumberAssertionsByType(TestStatistic.ASSERT_RESULT.FAIL);
    }

    public int getNumberSkippedAssertions() {
        return this.getNumberAssertionsByType(TestStatistic.ASSERT_RESULT.SKIP);
    }

    public int getNumberErrorAssertions() {
        return this.getNumberAssertionsByType(TestStatistic.ASSERT_RESULT.ERROR);
    }

    public int getTestTypeByIntID() {
        return this.testIdenificationType.getValue();
    }

    public String getTestTypeByStringType() {
        return this.testIdenificationType.getRep();
    }

    public String getAssociatedTestSuite() { return ""; }
    public List<Integer> getAssociatedTestSuiteIDs() { return this.testSuiteIDs; }

    public String getAssociatedTestRun() { return ""; }
    public List<Integer> getAssociatedTestRunIDs() { return new LinkedList<>(); }

    private void _initialize() {

        this.statCollector = new TestStatistic();
        this.testIdenificationType = TestType.TESTTYPE.UNKNOWN;

        this.testSuiteIDs = new LinkedList<>();

        this.testID = TestIdGenerator.generate( new TestDesignation(this) );
        this.priority = 0;
        this.isParameterizable = false;
        this.testName = null;
        this.dependencies = new TreeSet<>();
        this.parameterSettings = null;

        this.testResult = TestResult.TESTTYPE.SKIP;
    }
}
