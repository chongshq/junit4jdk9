package org.junit.runner;

import org.hamcrest.MatcherAssert;
import org.junit.common.Test;
import org.junit.notify.runner.Result;
import org.junit.tests.TestSystem;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class JUnitCoreTest {
    @Test
    public void shouldAddFailuresToResult() {
        JUnitCore jUnitCore = new JUnitCore();

        Result result = jUnitCore.runMain(new TestSystem(), "NonExistentTest");

        MatcherAssert.assertThat(result.getFailureCount(), is(1));
        MatcherAssert.assertThat(result.getFailures().get(0).getException(), instanceOf(IllegalArgumentException.class));
    }
}
