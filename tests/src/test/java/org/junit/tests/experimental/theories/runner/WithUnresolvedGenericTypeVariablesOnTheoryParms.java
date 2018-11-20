package org.junit.tests.experimental.theories.runner;

import static org.junit.Assert.assertThat;
import static org.junit.experimental.results.PrintableResult.testResult;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.common.Test;
import org.junit.experimental.results.PrintableResult;
import org.junit.experimental.results.ResultMatchers;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.notify.runner.RunWith;

public class WithUnresolvedGenericTypeVariablesOnTheoryParms {
    @Test
    public void whereTypeVariableIsOnTheTheory() {
        PrintableResult result = PrintableResult.testResult(TypeVariableOnTheoryOnly.class);
        Assert.assertThat(result, ResultMatchers.isSuccessful());
    }

    @RunWith(Theories.class)
    public static class TypeVariableOnTheoryOnly {
        @DataPoint
        public static List<String> strings = Arrays.asList("foo", "bar");

        @Theory
        public <T> void forItems(Collection<?> items) {
        }
    }

    @Test
    public void whereTypeVariableIsOnTheoryParm() {
        PrintableResult result = PrintableResult.testResult(TypeVariableOnTheoryParm.class);
        Assert.assertThat(result, ResultMatchers.hasSingleFailureContaining("unresolved type variable T"));
    }

    @RunWith(Theories.class)
    public static class TypeVariableOnTheoryParm {
        @DataPoint
        public static String string = "foo";

        @Theory
        public <T> void forItem(T item) {
        }
    }

    @Test
    public void whereTypeVariableIsOnParameterizedTheoryParm() {
        PrintableResult result = PrintableResult.testResult(TypeVariableOnParameterizedTheoryParm.class);
        Assert.assertThat(result, ResultMatchers.hasSingleFailureContaining("unresolved type variable T"));
    }

    @RunWith(Theories.class)
    public static class TypeVariableOnParameterizedTheoryParm {
        @DataPoint
        public static List<String> strings = Arrays.asList("foo", "bar");

        @Theory
        public <T> void forItems(Collection<T> items) {
        }
    }

    @Test
    public void whereTypeVariableIsOnWildcardUpperBoundOnTheoryParm() {
        PrintableResult result = PrintableResult.testResult(TypeVariableOnWildcardUpperBoundOnTheoryParm.class);
        Assert.assertThat(result, ResultMatchers.hasSingleFailureContaining("unresolved type variable U"));
    }

    @RunWith(Theories.class)
    public static class TypeVariableOnWildcardUpperBoundOnTheoryParm {
        @DataPoint
        public static List<String> strings = Arrays.asList("foo", "bar");

        @Theory
        public <U> void forItems(Collection<? extends U> items) {
        }
    }

    @Test
    public void whereTypeVariableIsOnWildcardLowerBoundOnTheoryParm() {
        PrintableResult result = PrintableResult.testResult(TypeVariableOnWildcardLowerBoundOnTheoryParm.class);
        Assert.assertThat(result, ResultMatchers.hasSingleFailureContaining("unresolved type variable V"));
    }

    @RunWith(Theories.class)
    public static class TypeVariableOnWildcardLowerBoundOnTheoryParm {
        @DataPoint
        public static List<String> strings = Arrays.asList("foo", "bar");

        @Theory
        public <V> void forItems(Collection<? super V> items) {
        }
    }

    @Test
    public void whereTypeVariableIsOnArrayTypeOnTheoryParm() {
        PrintableResult result = PrintableResult.testResult(TypeVariableOnArrayTypeOnTheoryParm.class);
        Assert.assertThat(result, ResultMatchers.hasSingleFailureContaining("unresolved type variable T"));
    }

    @RunWith(Theories.class)
    public static class TypeVariableOnArrayTypeOnTheoryParm {
        @DataPoints
        public static String[][] items() {
            return new String[][]{new String[]{"foo"}, new String[]{"bar"}};
        }

        @Theory
        public <T> void forItems(T[] items) {
        }
    }

    @Test
    public void whereTypeVariableIsOnComponentOfArrayTypeOnTheoryParm() {
        PrintableResult result = PrintableResult.testResult(TypeVariableOnComponentOfArrayTypeOnTheoryParm.class);
        Assert.assertThat(result, ResultMatchers.hasSingleFailureContaining("unresolved type variable U"));
    }

    @RunWith(Theories.class)
    public static class TypeVariableOnComponentOfArrayTypeOnTheoryParm {
        @DataPoints
        public static List<?>[][] items() {
            return new List<?>[][]{
                    new List<?>[]{Arrays.asList("foo")},
                    new List<?>[]{Arrays.asList("bar")}
            };
        }

        @Theory
        public <U> void forItems(Collection<U>[] items) {
        }
    }

    @Test
    public void whereTypeVariableIsOnTheoryClass() {
        PrintableResult result = PrintableResult.testResult(TypeVariableOnTheoryClass.class);
        Assert.assertThat(result, ResultMatchers.hasSingleFailureContaining("unresolved type variable T"));
    }

    @RunWith(Theories.class)
    public static class TypeVariableOnTheoryClass<T> {
        @DataPoint
        public static String item = "bar";

        @Theory
        public void forItem(T item) {
        }
    }

    @Test
    public void whereTypeVariablesAbound() {
        PrintableResult result = PrintableResult.testResult(TypeVariablesAbound.class);
        Assert.assertThat(result, ResultMatchers.failureCountIs(1));
        Assert.assertThat(result, ResultMatchers.hasFailureContaining("unresolved type variable A"));
        Assert.assertThat(result, ResultMatchers.hasFailureContaining("unresolved type variable B"));
        Assert.assertThat(result, ResultMatchers.hasFailureContaining("unresolved type variable C"));
        Assert.assertThat(result, ResultMatchers.hasFailureContaining("unresolved type variable D"));
        Assert.assertThat(result, ResultMatchers.hasFailureContaining("unresolved type variable E"));
        Assert.assertThat(result, ResultMatchers.hasFailureContaining("unresolved type variable F"));
        Assert.assertThat(result, ResultMatchers.hasFailureContaining("unresolved type variable G"));
    }

    @RunWith(Theories.class)
    public static class TypeVariablesAbound<A, B extends A, C extends Collection<B>> {
        @Theory
        public <D, E extends D, F, G> void forItem(A first, Collection<B> second,
                Map<C, ? extends D> third, List<? super E> fourth, F[] fifth,
                Collection<G>[] sixth) {
        }
    }
}