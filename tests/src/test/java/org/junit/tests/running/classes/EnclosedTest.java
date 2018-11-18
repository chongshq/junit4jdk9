package org.junit.tests.running.classes;

import static org.junit.Assert.assertEquals;

import org.junit.common.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.common.runner.Result;
import org.junit.common.runner.RunWith;
import org.junit.common.runner.Runner;

public class EnclosedTest {
    @RunWith(Enclosed.class)
    public static class Enclosing {
        public static class A {
            @Test
            public void a() {}

            @Test
            public void b() {}
        }
        public static class B {
            @Test
            public void a() {}

            @Test
            public void b() {}

            @Test
            public void c() {}
        }
        abstract public static class C {
            @Test public void a() {}
        }
    }

    @Test
    public void enclosedRunnerPlansConcreteEnclosedClasses() throws Exception {
        Runner runner= Request.aClass(Enclosing.class).getRunner();
        Assert.assertEquals(5, runner.testCount());
    }

    @Test
    public void enclosedRunnerRunsConcreteEnclosedClasses() throws Exception {
        Result result= JUnitCore.runClasses(Enclosing.class);
        Assert.assertEquals(5, result.getRunCount());
    }

    @Test
    public void enclosedRunnerIsNamedForEnclosingClass() throws Exception {
        Assert.assertEquals(Enclosing.class.getName(), Request.aClass(Enclosing.class)
                .getRunner().getDescription().getDisplayName());
    }
}
