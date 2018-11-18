package org.junit.tests.junit3compatibility;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import junit.framework.JUnit4TestAdapter;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.junit.common.Test;
import org.junit.runner.JUnitCore;
import org.junit.common.runner.RunWith;
import org.junit.runners.AllTests;

public class AllTestsTest {

    private static boolean run;

    public static class OneTest extends TestCase {
        public void testSomething() {
            run = true;
        }
    }

    @RunWith(AllTests.class)
    public static class All {
        static public junit.framework.Test suite() {
            TestSuite suite = new TestSuite();
            suite.addTestSuite(OneTest.class);
            return suite;
        }
    }

    @Test
    public void ensureTestIsRun() {
        JUnitCore runner = new JUnitCore();
        run = false; // Have to explicitly set run here because the runner might independently run OneTest above
        runner.run(All.class);
        Assert.assertTrue(run);
    }

    @Test
    public void correctTestCount() throws Throwable {
        AllTests tests = new AllTests(All.class);
        Assert.assertEquals(1, tests.testCount());
    }

    @Test
    public void someUsefulDescription() throws Throwable {
        AllTests tests = new AllTests(All.class);
        Assert.assertThat(tests.getDescription().toString(), containsString("OneTest"));
    }

    public static class JUnit4Test {
        @Test
        public void testSomething() {
            run = true;
        }
    }

    @RunWith(AllTests.class)
    public static class AllJUnit4 {
        static public junit.framework.Test suite() {
            TestSuite suite = new TestSuite();
            suite.addTest(new JUnit4TestAdapter(JUnit4Test.class));
            return suite;
        }
    }

    @Test
    public void correctTestCountAdapted() throws Throwable {
        AllTests tests = new AllTests(AllJUnit4.class);
        Assert.assertEquals(1, tests.testCount());
    }

    @RunWith(AllTests.class)
    public static class BadSuiteMethod {
        public static junit.framework.Test suite() {
            throw new RuntimeException("can't construct");
        }
    }

    @Test(expected = RuntimeException.class)
    public void exceptionThrownWhenSuiteIsBad() throws Throwable {
        new AllTests(BadSuiteMethod.class);
    }
}
