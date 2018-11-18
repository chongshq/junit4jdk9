package org.junit;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.common.Assume.assumeThat;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.common.Assume;
import org.junit.common.AssumptionViolatedException;
import org.junit.common.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.common.runner.RunWith;

@RunWith(Theories.class)
public class AssumptionViolatedExceptionTest {
    @DataPoint
    public static Integer TWO = 2;

    @DataPoint
    public static Matcher<Integer> IS_THREE = is(3);

    @DataPoint
    public static Matcher<Integer> NULL = null;

    @Theory
    public void toStringReportsMatcher(Integer actual, Matcher<Integer> matcher) {
        Assume.assumeThat(matcher, notNullValue());
        Assert.assertThat(new AssumptionViolatedException(actual, matcher).toString(),
                containsString(matcher.toString()));
    }

    @Theory
    public void toStringReportsValue(Integer actual, Matcher<Integer> matcher) {
        Assert.assertThat(new AssumptionViolatedException(actual, matcher).toString(),
                containsString(String.valueOf(actual)));
    }

    @Test
    public void assumptionViolatedExceptionWithMatcherDescribesItself() {
        AssumptionViolatedException e = new AssumptionViolatedException(3, is(2));
        Assert.assertThat(StringDescription.asString(e), is("got: <3>, expected: is <2>"));
    }

    @Test
    public void simpleAssumptionViolatedExceptionDescribesItself() {
        AssumptionViolatedException e = new AssumptionViolatedException("not enough money");
        Assert.assertThat(StringDescription.asString(e), is("not enough money"));
    }

    @Test
    public void canInitCauseWithInstanceCreatedWithString() {
      AssumptionViolatedException e = new AssumptionViolatedException("invalid number");
      Throwable cause = new RuntimeException("cause");
      e.initCause(cause);
      Assert.assertThat(e.getCause(), is(cause));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void canSetCauseWithInstanceCreatedWithObjectAndMatcher() {
      Throwable testObject = new Exception();
      org.junit.common.internal.AssumptionViolatedException e
              = new org.junit.common.internal.AssumptionViolatedException(
                      testObject, containsString("test matcher"));
      Assert.assertThat(e.getCause(), is(testObject));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void canSetCauseWithInstanceCreatedWithAssumptionObjectAndMatcher() {
      Throwable testObject = new Exception();
      org.junit.common.internal.AssumptionViolatedException e
              = new org.junit.common.internal.AssumptionViolatedException(
                      "sample assumption", testObject, containsString("test matcher"));
      Assert.assertThat(e.getCause(), is(testObject));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void canSetCauseWithInstanceCreatedWithMainConstructor() {
      Throwable testObject = new Exception();
      org.junit.common.internal.AssumptionViolatedException e
              = new org.junit.common.internal.AssumptionViolatedException(
                      "sample assumption", false, testObject, containsString("test matcher"));
      Assert.assertThat(e.getCause(), is(testObject));
    }

    @Test
    public void canSetCauseWithInstanceCreatedWithExplicitThrowableConstructor() {
      Throwable cause = new Exception();
      AssumptionViolatedException e = new AssumptionViolatedException("invalid number", cause);
      Assert.assertThat(e.getCause(), is(cause));
    }
}
