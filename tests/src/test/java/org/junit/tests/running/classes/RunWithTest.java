package org.junit.tests.running.classes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.common.Test;
import org.junit.common.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.notify.runner.RunWith;
import org.junit.notify.runner.Runner;
import org.junit.notify.runner.notification.RunNotifier;

public class RunWithTest {

    private static String log;

    public static class ExampleRunner extends Runner {
        public ExampleRunner(Class<?> klass) {
            log += "initialize";
        }

        @Override
        public void run(RunNotifier notifier) {
            log += "run";
        }

        @Override
        public int testCount() {
            log += "count";
            return 0;
        }

        @Override
        public Description getDescription() {
            log += "plan";
            return Description.createSuiteDescription("example");
        }
    }

    @RunWith(ExampleRunner.class)
    public static class ExampleTest {
    }

    @Test
    public void run() {
        log = "";

        JUnitCore.runClasses(ExampleTest.class);
        Assert.assertTrue(log.contains("plan"));
        Assert.assertTrue(log.contains("initialize"));
        Assert.assertTrue(log.contains("run"));
    }

    public static class SubExampleTest extends ExampleTest {
    }

    @Test
    public void runWithExtendsToSubclasses() {
        log = "";

        JUnitCore.runClasses(SubExampleTest.class);
        Assert.assertTrue(log.contains("run"));
    }

    public static class BadRunner extends Runner {
        @Override
        public Description getDescription() {
            return null;
        }

        @Override
        public void run(RunNotifier notifier) {
            // do nothing
        }
    }

    @RunWith(BadRunner.class)
    public static class Empty {
    }

    @Test
    public void characterizeErrorMessageFromBadRunner() {
        Assert.assertEquals(
                "Custom runner class BadRunner should have a public constructor with signature BadRunner(Class testClass)",
                JUnitCore.runClasses(Empty.class).getFailures().get(0)
                        .getMessage());
    }
}
