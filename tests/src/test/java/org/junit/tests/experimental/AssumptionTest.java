package org.junit.tests.experimental;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.common.Assume.assumeNoException;
import static org.junit.common.Assume.assumeNotNull;
import static org.junit.common.Assume.assumeThat;
import static org.junit.common.Assume.assumeTrue;
import static org.junit.experimental.results.PrintableResult.testResult;
import static org.junit.experimental.results.ResultMatchers.isSuccessful;

import java.util.ArrayList;
import java.util.List;

import org.junit.common.Assume;
import org.junit.common.AssumptionViolatedException;
import org.junit.common.Before;
import org.junit.common.BeforeClass;
import org.junit.common.Test;
import org.junit.runner.JUnitCore;
import org.junit.common.runner.Result;
import org.junit.common.runner.notification.Failure;
import org.junit.common.runner.notification.RunListener;

public class AssumptionTest {
    public static class HasFailingAssumption {
        @Test
        public void assumptionsFail() {
            Assume.assumeThat(3, is(4));
            Assert.fail();
        }
    }

    @Test
    public void failedAssumptionsMeanPassing() {
        Result result = JUnitCore.runClasses(HasFailingAssumption.class);
        Assert.assertThat(result.getRunCount(), is(1));
        Assert.assertThat(result.getIgnoreCount(), is(0));
        Assert.assertThat(result.getFailureCount(), is(0));
    }

    private static int assumptionFailures = 0;

    @Test
    public void failedAssumptionsCanBeDetectedByListeners() {
        assumptionFailures = 0;
        JUnitCore core = new JUnitCore();
        core.addListener(new RunListener() {
            @Override
            public void testAssumptionFailure(Failure failure) {
                assumptionFailures++;
            }
        });
        core.run(HasFailingAssumption.class);

        Assert.assertThat(assumptionFailures, is(1));
    }

    public static class HasPassingAssumption {
        @Test
        public void assumptionsFail() {
            Assume.assumeThat(3, is(3));
            Assert.fail();
        }
    }

    @Test
    public void passingAssumptionsScootThrough() {
        Result result = JUnitCore.runClasses(HasPassingAssumption.class);
        Assert.assertThat(result.getRunCount(), is(1));
        Assert.assertThat(result.getIgnoreCount(), is(0));
        Assert.assertThat(result.getFailureCount(), is(1));
    }

    @Test
    public void assumeThatWorks() {
        try {
            Assume.assumeThat(1, is(2));
            Assert.fail("should throw AssumptionViolatedException");
        } catch (AssumptionViolatedException e) {
            // expected
        }
    }

    @Test
    public void assumeThatPasses() {
        Assume.assumeThat(1, is(1));
        assertCompletesNormally();
    }

    @Test
    public void assumeThatPassesOnStrings() {
        Assume.assumeThat("x", is("x"));
        assertCompletesNormally();
    }

    @Test
    public void assumeNotNullThrowsException() {
        Object[] objects = {1, 2, null};
        try {
            Assume.assumeNotNull(objects);
            Assert.fail("should throw AssumptionViolatedException");
        } catch (AssumptionViolatedException e) {
            // expected
        }
    }

    @Test
    public void assumeNotNullThrowsExceptionForNullArray() {
        try {
            Assume.assumeNotNull((Object[]) null);
            Assert.fail("should throw AssumptionViolatedException");
        } catch (AssumptionViolatedException e) {
            // expected
        }
    }

    @Test
    public void assumeNotNullPasses() {
        Object[] objects = {1, 2};
        Assume.assumeNotNull(objects);
        assertCompletesNormally();
    }

    @Test
    public void assumeNotNullIncludesParameterList() {
        try {
            Object[] objects = {1, 2, null};
            Assume.assumeNotNull(objects);
        } catch (AssumptionViolatedException e) {
            Assert.assertThat(e.getMessage(), containsString("1, 2, null"));
        } catch (Exception e) {
            Assert.fail("Should have thrown AssumptionViolatedException");
        }
    }

    @Test
    public void assumeNoExceptionThrows() {
        final Throwable exception = new NullPointerException();
        try {
            Assume.assumeNoException(exception);
            Assert.fail("Should have thrown exception");
        } catch (AssumptionViolatedException e) {
            Assert.assertThat(e.getCause(), is(exception));
        }
    }

    private void assertCompletesNormally() {
    }

    @Test
    public void assumeTrueWorks() {
        try {
            Assume.assumeTrue(false);
            Assert.fail("should throw AssumptionViolatedException");
        } catch (AssumptionViolatedException e) {
            // expected
        }
    }

    public static class HasFailingAssumeInBefore {
        @Before
        public void checkForSomethingThatIsntThere() {
            Assume.assumeTrue(false);
        }

        @Test
        public void failing() {
            Assert.fail();
        }
    }

    @Test
    public void failingAssumptionInBeforePreventsTestRun() {
        Assert.assertThat(PrintableResult.testResult(HasFailingAssumeInBefore.class), ResultMatchers.isSuccessful());
    }

    public static class HasFailingAssumeInBeforeClass {
        @BeforeClass
        public static void checkForSomethingThatIsntThere() {
            Assume.assumeTrue(false);
        }

        @Test
        public void failing() {
            Assert.fail();
        }
    }

    @Test
    public void failingAssumptionInBeforeClassIgnoresClass() {
        Assert.assertThat(PrintableResult.testResult(HasFailingAssumeInBeforeClass.class), ResultMatchers.isSuccessful());
    }

    public static class AssumptionFailureInConstructor {
        public AssumptionFailureInConstructor() {
            Assume.assumeTrue(false);
        }

        @Test
        public void shouldFail() {
            Assert.fail();
        }
    }

    @Test
    public void failingAssumptionInConstructorIgnoresClass() {
        Assert.assertThat(PrintableResult.testResult(AssumptionFailureInConstructor.class), ResultMatchers.isSuccessful());
    }

    public static class TestClassWithAssumptionFailure {

        @Test(expected = IllegalArgumentException.class)
        public void assumeWithExpectedException() {
            Assume.assumeTrue(false);
        }
    }

    @Test
    public void assumeWithExpectedExceptionShouldThrowAssumptionViolatedException() {
        Result result = JUnitCore.runClasses(TestClassWithAssumptionFailure.class);
        Assert.assertThat(result.getAssumptionFailureCount(), is(1));
    }

    final static String message = "Some random message string.";
    final static Throwable e = new Throwable();

    /**
     * @see AssumptionTest#assumptionsWithMessage()
     */
    public static class HasAssumeWithMessage {
        @Test
        public void testMethod() {
            Assume.assumeTrue(message, false);
        }
    }

    @Test
    public void assumptionsWithMessage() {
        final List<Failure> failures =
                runAndGetAssumptionFailures(HasAssumeWithMessage.class);

        Assert.assertTrue(failures.get(0).getMessage().contains(message));
    }

    /**
     * @see AssumptionTest#assumptionsWithMessageAndCause()
     */
    public static class HasAssumeWithMessageAndCause {
        @Test
        public void testMethod() {
            Assume.assumeNoException(message, e);
        }
    }

    @Test
    public void assumptionsWithMessageAndCause() {
        final List<Failure> failures =
                runAndGetAssumptionFailures(HasAssumeWithMessageAndCause.class);
        Assert.assertTrue(failures.get(0).getMessage().contains(message));
        Assert.assertSame(failures.get(0).getException().getCause(), e);
    }

    public static class HasFailingAssumptionWithMessage {
        @Test
        public void assumptionsFail() {
            Assume.assumeThat(message, 3, is(4));
            Assert.fail();
        }
    }

    @Test
    public void failedAssumptionsWithMessage() {
        final List<Failure> failures =
                runAndGetAssumptionFailures(HasFailingAssumptionWithMessage.class);

        Assert.assertEquals(1, failures.size());
        Assert.assertTrue(failures.get(0).getMessage().contains(message));
    }

    /**
     * Helper method that runs tests on <code>clazz</code> and returns any
     * {@link Failure} objects that were {@link AssumptionViolatedException}s.
     */
    private static List<Failure> runAndGetAssumptionFailures(Class<?> clazz) {
        final List<Failure> failures = new ArrayList<Failure>();
        final JUnitCore core = new JUnitCore();
        core.addListener(new RunListener() {
            @Override
            public void testAssumptionFailure(Failure failure) {
                failures.add(failure);
            }
        });
        core.run(clazz);
        return failures;
    }
}
