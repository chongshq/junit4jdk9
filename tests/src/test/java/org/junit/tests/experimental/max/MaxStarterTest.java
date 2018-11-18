package org.junit.tests.experimental.max;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import org.junit.common.After;
import org.junit.common.Before;
import org.junit.common.Test;
import org.junit.experimental.max.MaxCore;
import org.junit.internal.runners.JUnit38ClassRunner;
import org.junit.runner.Computer;
import org.junit.common.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.common.runner.Result;
import org.junit.common.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.common.runner.notification.Failure;
import org.junit.common.runner.notification.RunListener;
import org.junit.tests.AllTests;

public class MaxStarterTest {
    private MaxCore fMax;

    private File fMaxFile;

    @Before
    public void createMax() {
        fMaxFile = new File("MaxCore.ser");
        if (fMaxFile.exists()) {
            fMaxFile.delete();
        }
        fMax = MaxCore.storedLocally(fMaxFile);
    }

    @After
    public void forgetMax() {
        fMaxFile.delete();
    }

    public static class TwoTests {
        @Test
        public void succeed() {
        }

        @Test
        public void dontSucceed() {
            Assert.fail();
        }
    }

    @Test
    public void twoTestsNotRunComeBackInRandomOrder() {
        Request request = Request.aClass(TwoTests.class);
        List<Description> things = fMax.sortedLeavesForTest(request);
        Description succeed = Description.createTestDescription(TwoTests.class,
                "succeed");
        Description dontSucceed = Description.createTestDescription(
                TwoTests.class, "dontSucceed");
        Assert.assertTrue(things.contains(succeed));
        Assert.assertTrue(things.contains(dontSucceed));
        Assert.assertEquals(2, things.size());
    }

    @Test
    public void preferNewTests() {
        Request one = Request.method(TwoTests.class, "succeed");
        fMax.run(one);
        Request two = Request.aClass(TwoTests.class);
        List<Description> things = fMax.sortedLeavesForTest(two);
        Description dontSucceed = Description.createTestDescription(
                TwoTests.class, "dontSucceed");
        Assert.assertEquals(dontSucceed, things.get(0));
        Assert.assertEquals(2, things.size());
    }

    // This covers a seemingly-unlikely case, where you had a test that failed
    // on the
    // last run and you also introduced new tests. In such a case it pretty much
    // doesn't matter
    // which order they run, you just want them both to be early in the sequence
    @Test
    public void preferNewTestsOverTestsThatFailed() {
        Request one = Request.method(TwoTests.class, "dontSucceed");
        fMax.run(one);
        Request two = Request.aClass(TwoTests.class);
        List<Description> things = fMax.sortedLeavesForTest(two);
        Description succeed = Description.createTestDescription(TwoTests.class,
                "succeed");
        Assert.assertEquals(succeed, things.get(0));
        Assert.assertEquals(2, things.size());
    }

    @Test
    public void preferRecentlyFailed() {
        Request request = Request.aClass(TwoTests.class);
        fMax.run(request);
        List<Description> tests = fMax.sortedLeavesForTest(request);
        Description dontSucceed = Description.createTestDescription(
                TwoTests.class, "dontSucceed");
        Assert.assertEquals(dontSucceed, tests.get(0));
    }

    @Test
    public void sortTestsInMultipleClasses() {
        Request request = Request.classes(Computer.serial(), TwoTests.class,
                TwoTests.class);
        fMax.run(request);
        List<Description> tests = fMax.sortedLeavesForTest(request);
        Description dontSucceed = Description.createTestDescription(
                TwoTests.class, "dontSucceed");
        Assert.assertEquals(dontSucceed, tests.get(0));
        Assert.assertEquals(dontSucceed, tests.get(1));
    }

    public static class TwoUnEqualTests {
        @Test
        public void slow() throws InterruptedException {
            Thread.sleep(100);
            Assert.fail();
        }

        @Test
        public void fast() {
            Assert.fail();
        }

    }

    @Test
    public void rememberOldRuns() {
        fMax.run(TwoUnEqualTests.class);

        MaxCore reincarnation = MaxCore.storedLocally(fMaxFile);
        List<Failure> failures = reincarnation.run(TwoUnEqualTests.class)
                .getFailures();
        Assert.assertEquals("fast", failures.get(0).getDescription().getMethodName());
        Assert.assertEquals("slow", failures.get(1).getDescription().getMethodName());
    }

    @Test
    public void preferFast() {
        Request request = Request.aClass(TwoUnEqualTests.class);
        fMax.run(request);
        Description thing = fMax.sortedLeavesForTest(request).get(1);
        Assert.assertEquals(Description.createTestDescription(TwoUnEqualTests.class,
                "slow"), thing);
    }

    @Test
    public void listenersAreCalledCorrectlyInTheFaceOfFailures()
            throws Exception {
        JUnitCore core = new JUnitCore();
        final List<Failure> failures = new ArrayList<Failure>();
        core.addListener(new RunListener() {
            @Override
            public void testRunFinished(Result result) throws Exception {
                failures.addAll(result.getFailures());
            }
        });
        fMax.run(Request.aClass(TwoTests.class), core);
        Assert.assertEquals(1, failures.size());
    }

    @Test
    public void testsAreOnlyIncludedOnceWhenExpandingForSorting()
            throws Exception {
        Result result = fMax.run(Request.aClass(TwoTests.class));
        Assert.assertEquals(2, result.getRunCount());
    }

    public static class TwoOldTests extends TestCase {
        public void testOne() {
        }

        public void testTwo() {
        }
    }

    @Test
    public void junit3TestsAreRunOnce() throws Exception {
        Result result = fMax.run(Request.aClass(TwoOldTests.class),
                new JUnitCore());
        Assert.assertEquals(2, result.getRunCount());
    }

    @Test
    public void filterSingleMethodFromOldTestClass() throws Exception {
        final Description method = Description.createTestDescription(
                TwoOldTests.class, "testOne");
        Filter filter = Filter.matchMethodDescription(method);
        JUnit38ClassRunner child = new JUnit38ClassRunner(TwoOldTests.class);
        child.filter(filter);
        Assert.assertEquals(1, child.testCount());
    }

    @Test
    public void testCountsStandUpToFiltration() {
        assertFilterLeavesTestUnscathed(AllTests.class);
    }

    private void assertFilterLeavesTestUnscathed(Class<?> testClass) {
        Request oneClass = Request.aClass(testClass);
        Request filtered = oneClass.filterWith(new Filter() {
            @Override
            public boolean shouldRun(Description description) {
                return true;
            }

            @Override
            public String describe() {
                return "Everything";
            }
        });

        int filterCount = filtered.getRunner().testCount();
        int coreCount = oneClass.getRunner().testCount();
        Assert.assertEquals("Counts match up in " + testClass, coreCount, filterCount);
    }

    private static class MalformedJUnit38Test {
        private MalformedJUnit38Test() {
        }

        @SuppressWarnings("unused")
        public void testSucceeds() {
        }
    }

    @Test
    public void maxShouldSkipMalformedJUnit38Classes() {
        Request request = Request.aClass(MalformedJUnit38Test.class);
        fMax.run(request);
    }

    public static class MalformedJUnit38TestMethod extends TestCase {
        @SuppressWarnings("unused")
        private void testNothing() {
        }
    }

    @Test
    public void correctErrorFromMalformedTest() {
        Request request = Request.aClass(MalformedJUnit38TestMethod.class);
        JUnitCore core = new JUnitCore();
        Request sorted = fMax.sortRequest(request);
        Runner runner = sorted.getRunner();
        Result result = core.run(runner);
        Failure failure = result.getFailures().get(0);
        Assert.assertThat(failure.toString(), containsString("MalformedJUnit38TestMethod"));
        Assert.assertThat(failure.toString(), containsString("testNothing"));
        Assert.assertThat(failure.toString(), containsString("isn't public"));
    }

    public static class HalfMalformedJUnit38TestMethod extends TestCase {
        public void testSomething() {
        }

        @SuppressWarnings("unused")
        private void testNothing() {
        }
    }

    @Test
    public void halfMalformed() {
        Assert.assertThat(JUnitCore.runClasses(HalfMalformedJUnit38TestMethod.class)
                .getFailureCount(), is(1));
    }


    @Test
    public void correctErrorFromHalfMalformedTest() {
        Request request = Request.aClass(HalfMalformedJUnit38TestMethod.class);
        JUnitCore core = new JUnitCore();
        Request sorted = fMax.sortRequest(request);
        Runner runner = sorted.getRunner();
        Result result = core.run(runner);
        Failure failure = result.getFailures().get(0);
        Assert.assertThat(failure.toString(), containsString("MalformedJUnit38TestMethod"));
        Assert.assertThat(failure.toString(), containsString("testNothing"));
        Assert.assertThat(failure.toString(), containsString("isn't public"));
    }
}
