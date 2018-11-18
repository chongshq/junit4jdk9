package org.junit.tests.experimental.theories.internal;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.common.Assume.assumeThat;

import org.hamcrest.CoreMatchers;
import org.junit.common.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.experimental.theories.internal.ParameterizedAssertionError;
import org.junit.common.runner.RunWith;

@RunWith(Theories.class)
public class ParameterizedAssertionErrorTest {
    @DataPoint
    public static final String METHOD_NAME = "methodName";

    @DataPoint
    public static final NullPointerException NULL_POINTER_EXCEPTION = new NullPointerException();

    @DataPoint
    public static Object[] NO_OBJECTS = new Object[0];

    @DataPoint
    public static ParameterizedAssertionError A = new ParameterizedAssertionError(
            NULL_POINTER_EXCEPTION, METHOD_NAME);

    @DataPoint
    public static ParameterizedAssertionError B = new ParameterizedAssertionError(
            NULL_POINTER_EXCEPTION, METHOD_NAME);

    @DataPoint
    public static ParameterizedAssertionError B2 = new ParameterizedAssertionError(
            NULL_POINTER_EXCEPTION, "methodName2");

    @Theory
    public void equalParameterizedAssertionErrorsHaveSameToString(
            ParameterizedAssertionError a, ParameterizedAssertionError b) {
        Assume.assumeThat(a, CoreMatchers.is(b));
        Assert.assertThat(a.toString(), CoreMatchers.is(b.toString()));
    }

    @Theory
    public void differentParameterizedAssertionErrorsHaveDifferentToStrings(
            ParameterizedAssertionError a, ParameterizedAssertionError b) {
        Assume.assumeThat(a, CoreMatchers.not(b));
        Assert.assertThat(a.toString(), CoreMatchers.not(b.toString()));
    }

    @Theory
    public void equalsReturnsTrue(Throwable targetException, String methodName,
            Object[] params) {
        Assert.assertThat(
                new ParameterizedAssertionError(targetException, methodName, params),
                CoreMatchers.is(new ParameterizedAssertionError(targetException, methodName, params)));
    }

    @Theory
    public void sameHashCodeWhenEquals(Throwable targetException, String methodName,
            Object[] params) {
        ParameterizedAssertionError one = new ParameterizedAssertionError(
                targetException, methodName, params);
        ParameterizedAssertionError two = new ParameterizedAssertionError(
                targetException, methodName, params);
        Assume.assumeThat(one, CoreMatchers.is(two));

        Assert.assertThat(one.hashCode(), CoreMatchers.is(two.hashCode()));
    }

    @Theory(nullsAccepted = false)
    public void buildParameterizedAssertionError(String methodName, String param) {
        Assert.assertThat(new ParameterizedAssertionError(
                new RuntimeException(), methodName, param).toString(),
                containsString(methodName));
    }

    @Theory
    public void isNotEqualToNull(ParameterizedAssertionError a) {
        Assert.assertFalse(a.equals(null));
    }

    @Test
    public void canJoinWhenToStringFails() {
        Assert.assertThat(ParameterizedAssertionError.join(" ", new Object() {
            @Override
            public String toString() {
                throw new UnsupportedOperationException();
            }
        }), is("[toString failed]"));
    }
}
