package org.junit.tests.experimental.theories.runner;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.experimental.results.PrintableResult.testResult;
import static org.junit.experimental.results.ResultMatchers.failureCountIs;
import static org.junit.experimental.results.ResultMatchers.isSuccessful;

import org.junit.Assert;
import org.junit.common.Test;
import org.junit.experimental.results.PrintableResult;
import org.junit.experimental.results.ResultMatchers;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.runner.JUnitCore;
import org.junit.common.runner.Result;
import org.junit.common.runner.RunWith;

public class WithOnlyTestAnnotations {
    @RunWith(Theories.class)
    public static class HonorExpectedException {
        @Test(expected = NullPointerException.class)
        public void shouldThrow() {

        }
    }

    @Test
    public void honorExpected() throws Exception {
        Assert.assertThat(PrintableResult.testResult(HonorExpectedException.class).failureCount(), is(1));
    }

    @RunWith(Theories.class)
    public static class HonorExpectedExceptionPasses {
        @Test(expected = NullPointerException.class)
        public void shouldThrow() {
            throw new NullPointerException();
        }
    }

    @Test
    public void honorExpectedPassing() throws Exception {
        Assert.assertThat(PrintableResult.testResult(HonorExpectedExceptionPasses.class), ResultMatchers.isSuccessful());
    }

    @RunWith(Theories.class)
    public static class HonorTimeout {
        @Test(timeout = 5)
        public void shouldStop() {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {

                }
            }
        }
    }

    @Test
    public void honorTimeout() throws Exception {
        Assert.assertThat(PrintableResult.testResult(HonorTimeout.class), ResultMatchers.failureCountIs(1));
    }

    @RunWith(Theories.class)
    static public class ErrorWhenTestHasParametersDespiteTheories {
        @DataPoint
        public static int ZERO = 0;

        @Test
        public void testMethod(int i) {
        }
    }

    @Test
    public void testErrorWhenTestHasParametersDespiteTheories() {
        JUnitCore core = new JUnitCore();
        Result result = core.run(ErrorWhenTestHasParametersDespiteTheories.class);
        Assert.assertEquals(1, result.getFailureCount());
        String message = result.getFailures().get(0).getMessage();
        Assert.assertThat(message, containsString("should have no parameters"));
    }
}