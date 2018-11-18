package org.junit.internal.matchers;

import org.junit.common.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.util.internal.matchers.ThrowableCauseMatcher.hasCause;

public class ThrowableCauseMatcherTest {

    @Test
    public void shouldAllowCauseOfDifferentClassFromRoot() throws Exception {
        NullPointerException expectedCause = new NullPointerException("expected");
        Exception actual = new Exception(expectedCause);

        Assert.assertThat(actual, hasCause(is(expectedCause)));
    }
}