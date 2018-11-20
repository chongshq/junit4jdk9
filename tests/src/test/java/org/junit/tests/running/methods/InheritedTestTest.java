package org.junit.tests.running.methods;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Assert;
import org.junit.common.Before;
import org.junit.common.Test;
import org.junit.runner.JUnitCore;
import org.junit.notify.runner.Result;

public class InheritedTestTest {
    public abstract static class Super {
        @Test
        public void nothing() {
        }
    }

    public static class Sub extends Super {
    }

    @Test
    public void subclassWithOnlyInheritedTestsRuns() {
        Result result = JUnitCore.runClasses(Sub.class);
        Assert.assertTrue(result.wasSuccessful());
    }

    public static class SubWithBefore extends Super {
        @Before
        public void gack() {
            Assert.fail();
        }
    }

    @Test
    public void subclassWithInheritedTestAndOwnBeforeRunsBefore() {
        Assert.assertFalse(JUnitCore.runClasses(SubWithBefore.class).wasSuccessful());
    }
} 
