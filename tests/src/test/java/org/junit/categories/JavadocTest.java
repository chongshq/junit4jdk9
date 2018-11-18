package org.junit.categories;

import org.junit.Assert;
import org.junit.common.Test;
import org.junit.experimental.categories.Categories;
import org.junit.experimental.categories.Category;
import org.junit.runner.JUnitCore;
import org.junit.common.runner.Result;
import org.junit.common.runner.RunWith;
import org.junit.runners.Suite;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author tibor17
 * @version 4.12
 * @since 4.12
 */
public class JavadocTest {
    public static interface FastTests {}
    public static interface SlowTests {}
    public static interface SmokeTests {}

    public static class A {
        public void a() {
            Assert.fail();
        }

        @Category(SlowTests.class)
        @Test
        public void b() {
        }

        @Category({FastTests.class, SmokeTests.class})
        @Test
        public void c() {
        }
    }

    @Category({SlowTests.class, FastTests.class})
    public static class B {
        @Test
        public void d() {
        }
    }

    @RunWith(Categories.class)
    @Categories.IncludeCategory(SlowTests.class)
    @Suite.SuiteClasses({A.class, B.class})
    public static class SlowTestSuite {
        // Will run A.b and B.d, but not A.a and A.c
    }

    @RunWith(Categories.class)
    @Categories.IncludeCategory({FastTests.class, SmokeTests.class})
    @Suite.SuiteClasses({A.class, B.class})
    public static class FastOrSmokeTestSuite {
        // Will run A.c and B.d, but not A.b because it is not any of FastTests or SmokeTests
    }

    @Test
    public void slowTests() {
        Result testResult= JUnitCore.runClasses(SlowTestSuite.class);
        Assert.assertTrue(testResult.wasSuccessful());
        Assert.assertThat("unexpected run count", testResult.getRunCount(), is(2));
        Assert.assertThat("unexpected failure count", testResult.getFailureCount(), is(0));
    }

    @Test
    public void fastSmokeTests() {
        Result testResult= JUnitCore.runClasses(FastOrSmokeTestSuite.class);
        Assert.assertTrue(testResult.wasSuccessful());
        Assert.assertThat("unexpected run count", testResult.getRunCount(), is(2));
        Assert.assertThat("unexpected failure count", testResult.getFailureCount(), is(0));
    }
}
