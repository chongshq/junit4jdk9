package org.junit.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.common.Assume.assumeNotNull;
import static org.junit.common.Assume.assumeThat;

import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.common.Assume;
import org.junit.common.Test;
import org.junit.common.Test.None;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.common.runner.RunWith;
import org.junit.runners.model.FrameworkMethod;

@RunWith(Theories.class)
public class ObjectContractTest {
    @DataPoints
    public static Object[] objects = {new FrameworkMethod(toStringMethod()),
            new FrameworkMethod(toStringMethod()), 3, null};

    @Theory
    @Test(expected = None.class)
    public void equalsThrowsNoException(Object a, Object b) {
        Assume.assumeNotNull(a);
        a.equals(b);
    }

    @Theory
    public void equalsMeansEqualHashCodes(Object a, Object b) {
        Assume.assumeNotNull(a, b);
        Assume.assumeThat(a, is(b));
        Assert.assertThat(a.hashCode(), is(b.hashCode()));
    }

    private static Method toStringMethod() {
        try {
            return Object.class.getMethod("toString");
        } catch (SecurityException e) {
        } catch (NoSuchMethodException e) {
        }
        return null;
    }
}
