package org.junit.tests.running.classes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.experimental.results.PrintableResult.testResult;

import java.util.List;

import junit.framework.JUnit4TestAdapter;
import junit.framework.TestResult;
import org.junit.common.AfterClass;
import org.junit.Assert;
import org.junit.common.BeforeClass;
import org.junit.common.Test;
import org.junit.experimental.results.PrintableResult;
import org.junit.experimental.results.ResultMatchers;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.notify.runner.Result;
import org.junit.notify.runner.RunWith;
import org.junit.notify.runner.Runner;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

public class SuiteTest {
    public static class TestA {
        @Test
        public void pass() {
        }
    }

    public static class TestB {
        @Test
        public void fail() {
            Assert.fail();
        }
    }

    @RunWith(Suite.class)
    @SuiteClasses({TestA.class, TestB.class})
    public static class All {
    }

    @RunWith(Suite.class)
    @SuiteClasses(TestA.class)
    static class NonPublicSuite {
    }

    @RunWith(Suite.class)
    @SuiteClasses(TestA.class)
    static class NonPublicSuiteWithBeforeClass {
        @BeforeClass
        public static void doesNothing() {}
    }

    public static class InheritsAll extends All {
    }

    @Test
    public void ensureTestIsRun() {
        JUnitCore core = new JUnitCore();
        Result result = core.run(All.class);
        Assert.assertEquals(2, result.getRunCount());
        Assert.assertEquals(1, result.getFailureCount());
    }

    @Test
    public void ensureInheritedTestIsRun() {
        JUnitCore core = new JUnitCore();
        Result result = core.run(InheritsAll.class);
        Assert.assertEquals(2, result.getRunCount());
        Assert.assertEquals(1, result.getFailureCount());
    }

    @Test
    public void suiteTestCountIsCorrect() throws Exception {
        Runner runner = Request.aClass(All.class).getRunner();
        Assert.assertEquals(2, runner.testCount());
    }

    @Test
    public void suiteClassDoesNotNeedToBePublic() {
        JUnitCore core = new JUnitCore();
        Result result = core.run(NonPublicSuite.class);
        Assert.assertEquals(1, result.getRunCount());
        Assert.assertEquals(0, result.getFailureCount());
    }

    @Test
    public void nonPublicSuiteClassWithBeforeClassPasses() {
        Assert.assertThat(PrintableResult.testResult(NonPublicSuiteWithBeforeClass.class), ResultMatchers.isSuccessful());
    }

    @Test
    public void ensureSuitesWorkWithForwardCompatibility() {
        junit.framework.Test test = new JUnit4TestAdapter(All.class);
        TestResult result = new TestResult();
        test.run(result);
        assertEquals(2, result.runCount());
    }

    @Test
    public void forwardCompatibilityWorksWithGetTests() {
        JUnit4TestAdapter adapter = new JUnit4TestAdapter(All.class);
        List<? extends junit.framework.Test> tests = adapter.getTests();
        Assert.assertEquals(2, tests.size());
    }

    @Test
    public void forwardCompatibilityWorksWithTestCount() {
        JUnit4TestAdapter adapter = new JUnit4TestAdapter(All.class);
        Assert.assertEquals(2, adapter.countTestCases());
    }


    private static String log = "";

    @RunWith(Suite.class)
    @SuiteClasses({TestA.class, TestB.class})
    public static class AllWithBeforeAndAfterClass {
        @BeforeClass
        public static void before() {
            log += "before ";
        }

        @AfterClass
        public static void after() {
            log += "after ";
        }
    }

    @Test
    public void beforeAndAfterClassRunOnSuite() {
        log = "";
        JUnitCore.runClasses(AllWithBeforeAndAfterClass.class);
        Assert.assertEquals("before after ", log);
    }

    @RunWith(Suite.class)
    public static class AllWithOutAnnotation {
    }

    @Test
    public void withoutSuiteClassAnnotationProducesFailure() {
        Result result = JUnitCore.runClasses(AllWithOutAnnotation.class);
        Assert.assertEquals(1, result.getFailureCount());
        String expected = String.format(
                "class '%s' must have a SuiteClasses annotation",
                AllWithOutAnnotation.class.getName());
        Assert.assertEquals(expected, result.getFailures().get(0).getMessage());
    }

    @RunWith(Suite.class)
    @SuiteClasses(InfiniteLoop.class)
    static public class InfiniteLoop {
    }

    @Test
    public void whatHappensWhenASuiteHasACycle() {
        Result result = JUnitCore.runClasses(InfiniteLoop.class);
        Assert.assertEquals(1, result.getFailureCount());
    }

    @RunWith(Suite.class)
    @SuiteClasses({BiInfiniteLoop.class, BiInfiniteLoop.class})
    static public class BiInfiniteLoop {
    }

    @Test
    public void whatHappensWhenASuiteHasAForkingCycle() {
        Result result = JUnitCore.runClasses(BiInfiniteLoop.class);
        Assert.assertEquals(2, result.getFailureCount());
    }

    // The interesting case here is that Hydra indirectly contains two copies of
    // itself (if it only contains one, Java's StackOverflowError eventually
    // bails us out)

    @RunWith(Suite.class)
    @SuiteClasses({Hercules.class})
    static public class Hydra {
    }

    @RunWith(Suite.class)
    @SuiteClasses({Hydra.class, Hydra.class})
    static public class Hercules {
    }

    @Test
    public void whatHappensWhenASuiteContainsItselfIndirectly() {
        Result result = JUnitCore.runClasses(Hydra.class);
        Assert.assertEquals(2, result.getFailureCount());
    }

    @RunWith(Suite.class)
    @SuiteClasses({})
    public class WithoutDefaultConstructor {
        public WithoutDefaultConstructor(int i) {

        }
    }

    @Test
    public void suiteShouldBeOKwithNonDefaultConstructor() throws Exception {
        Result result = JUnitCore.runClasses(WithoutDefaultConstructor.class);
        Assert.assertTrue(result.wasSuccessful());
    }

    @RunWith(Suite.class)
    public class NoSuiteClassesAnnotation {
    }

    @Test
    public void suiteShouldComplainAboutNoSuiteClassesAnnotation() {
        Assert.assertThat(PrintableResult.testResult(NoSuiteClassesAnnotation.class), ResultMatchers.hasSingleFailureContaining("SuiteClasses"));
    }
}
