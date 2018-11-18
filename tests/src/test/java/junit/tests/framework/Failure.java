package junit.tests.framework;

import junit.j3.framework.TestCase;

/**
 * A test case testing the testing framework.
 */
public class Failure extends TestCase {
    @Override
    public void runTest() {
        fail();
    }
}