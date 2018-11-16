package org.junit.internal.builders;

import org.junit.common.runner.Description;
import org.junit.common.runner.Runner;
import org.junit.common.runner.notification.RunNotifier;

public class IgnoredClassRunner extends Runner {
    private final Class<?> clazz;

    public IgnoredClassRunner(Class<?> testClass) {
        clazz = testClass;
    }

    @Override
    public void run(RunNotifier notifier) {
        notifier.fireTestIgnored(getDescription());
    }

    @Override
    public Description getDescription() {
        return Description.createSuiteDescription(clazz);
    }
}