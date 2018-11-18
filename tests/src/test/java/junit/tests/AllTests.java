package junit.tests;

import junit.j3.framework.Test;
import junit.j3.framework.TestSuite;

/**
 * TestSuite that runs all the JUnit tests
 */
public class AllTests {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Framework Tests");
        suite.addTest(junit.tests.framework.AllTests.suite());
        suite.addTest(junit.tests.runner.AllTests.suite());
        suite.addTest(junit.tests.extensions.AllTests.suite());
        return suite;
    }
}