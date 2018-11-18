package junit.tests.extensions;

import junit.j3.extensions.RepeatedTest;
import junit.j3.framework.Test;
import junit.j3.framework.TestCase;
import junit.j3.framework.TestResult;
import junit.j3.framework.TestSuite;

/**
 * Testing the RepeatedTest support.
 */
public class RepeatedTestTest extends TestCase {
    private TestSuite fSuite;

    public static class SuccessTest extends TestCase {

        @Override
        public void runTest() {
        }
    }

    public RepeatedTestTest(String name) {
        super(name);
        fSuite = new TestSuite();
        fSuite.addTest(new SuccessTest());
        fSuite.addTest(new SuccessTest());
    }

    public void testRepeatedOnce() {
        Test test = new RepeatedTest(fSuite, 1);
        assertEquals(2, test.countTestCases());
        TestResult result = new TestResult();
        test.run(result);
        assertEquals(2, result.runCount());
    }

    public void testRepeatedMoreThanOnce() {
        Test test = new RepeatedTest(fSuite, 3);
        assertEquals(6, test.countTestCases());
        TestResult result = new TestResult();
        test.run(result);
        assertEquals(6, result.runCount());
    }

    public void testRepeatedZero() {
        Test test = new RepeatedTest(fSuite, 0);
        assertEquals(0, test.countTestCases());
        TestResult result = new TestResult();
        test.run(result);
        assertEquals(0, result.runCount());
    }

    public void testRepeatedNegative() {
        try {
            new RepeatedTest(fSuite, -1);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(">="));
            return;
        }
        fail("Should throw an IllegalArgumentException");
    }
}