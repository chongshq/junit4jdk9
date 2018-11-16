package org.junit.tests.listening;

import org.junit.common.Before;
import org.junit.common.Test;
import org.junit.runner.Request;
import org.junit.common.runner.notification.RunNotifier;
import org.junit.common.runner.notification.StoppedByUserException;

public class UserStopTest {
    private RunNotifier fNotifier;

    @Before
    public void createNotifier() {
        fNotifier = new RunNotifier();
        fNotifier.pleaseStop();
    }

    @Test(expected = StoppedByUserException.class)
    public void userStop() {
        fNotifier.fireTestStarted(null);
    }

    public static class OneTest {
        @Test
        public void foo() {
        }
    }

    @Test(expected = StoppedByUserException.class)
    public void stopClassRunner() throws Exception {
        Request.aClass(OneTest.class).getRunner().run(fNotifier);
    }
}
