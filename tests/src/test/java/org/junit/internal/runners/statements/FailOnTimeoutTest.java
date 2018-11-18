package org.junit.internal.runners.statements;

import static java.lang.Long.MAX_VALUE;
import static java.lang.Math.atan;
import static java.lang.System.currentTimeMillis;
import static java.lang.Thread.currentThread;
import static java.lang.Thread.sleep;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.internal.runners.statements.FailOnTimeout.builder;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.junit.common.Test;
import org.junit.common.function.ThrowingRunnable;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestTimedOutException;

/**
 * @author Asaf Ary, Stefan Birkner
 */
public class FailOnTimeoutTest {
    private static final long TIMEOUT = 100;
    private static final long DURATION_THAT_EXCEEDS_TIMEOUT = 60 * 60 * 1000; //1 hour

    private final TestStatement statement = new TestStatement();

    private final FailOnTimeout failOnTimeout = FailOnTimeout.builder().withTimeout(TIMEOUT, MILLISECONDS).build(statement);

    @Test
    public void throwsTestTimedOutException() {
        Assert.assertThrows(
                TestTimedOutException.class,
                evaluateWithWaitDuration(DURATION_THAT_EXCEEDS_TIMEOUT));
    }

    @Test
    public void throwExceptionWithNiceMessageOnTimeout() {
        TestTimedOutException e = Assert.assertThrows(
                TestTimedOutException.class,
                evaluateWithWaitDuration(DURATION_THAT_EXCEEDS_TIMEOUT));
        Assert.assertEquals("test timed out after 100 milliseconds", e.getMessage());
    }

    @Test
    public void sendUpExceptionThrownByStatement() {
        RuntimeException exception = new RuntimeException();
        RuntimeException e = Assert.assertThrows(
                RuntimeException.class,
                evaluateWithException(exception));
        Assert.assertSame(exception, e);
    }

    @Test
    public void throwExceptionIfTheSecondCallToEvaluateNeedsTooMuchTime()
            throws Throwable {
        evaluateWithWaitDuration(0).run();
        Assert.assertThrows(
                TestTimedOutException.class,
                evaluateWithWaitDuration(DURATION_THAT_EXCEEDS_TIMEOUT));
    }

    @Test
    public void throwTimeoutExceptionOnSecondCallAlthoughFirstCallThrowsException() {
        try {
            evaluateWithException(new RuntimeException()).run();
        } catch (Throwable expected) {
        }

        TestTimedOutException e = Assert.assertThrows(
                TestTimedOutException.class,
                evaluateWithWaitDuration(DURATION_THAT_EXCEEDS_TIMEOUT));
        Assert.assertEquals("test timed out after 100 milliseconds", e.getMessage());
    }

    @Test
    public void throwsExceptionWithTimeoutValueAndTimeUnitSet() {
        TestTimedOutException e = Assert.assertThrows(
                TestTimedOutException.class,
                evaluateWithWaitDuration(DURATION_THAT_EXCEEDS_TIMEOUT));
        Assert.assertEquals(TIMEOUT, e.getTimeout());
        Assert.assertEquals(TimeUnit.MILLISECONDS, e.getTimeUnit());
    }

    private ThrowingRunnable evaluateWithException(final Exception exception) {
        return new ThrowingRunnable() {
            public void run() throws Throwable {
                statement.nextException = exception;
                statement.waitDuration = 0;
                failOnTimeout.evaluate();
            }
        };
    }

    private ThrowingRunnable evaluateWithWaitDuration(final long waitDuration) {
        return new ThrowingRunnable() {
            public void run() throws Throwable {
                statement.nextException = null;
                statement.waitDuration = waitDuration;
                failOnTimeout.evaluate();
            }
        };
    }

    private static final class TestStatement extends Statement {
        long waitDuration;

        Exception nextException;

        @Override
        public void evaluate() throws Throwable {
            sleep(waitDuration);
            if (nextException != null) {
                throw nextException;
            }
        }
    }

    @Test
    public void stopEndlessStatement() throws Throwable {
        InfiniteLoopStatement infiniteLoop = new InfiniteLoopStatement();
        FailOnTimeout infiniteLoopTimeout = FailOnTimeout.builder().withTimeout(TIMEOUT, MILLISECONDS).build(infiniteLoop);
        try {
            infiniteLoopTimeout.evaluate();
        } catch (Exception timeoutException) {
            sleep(20); // time to interrupt the thread
            int firstCount = InfiniteLoopStatement.COUNT;
            sleep(20); // time to increment the count
            Assert.assertTrue("Thread has not been stopped.",
                    firstCount == InfiniteLoopStatement.COUNT);
        }
    }

    private static final class InfiniteLoopStatement extends Statement {
        private static int COUNT = 0;

        @Override
        public void evaluate() throws Throwable {
            while (true) {
                sleep(10); // sleep in order to enable interrupting thread
                ++COUNT;
            }
        }
    }

    @Test
    public void stackTraceContainsRealCauseOfTimeout() throws Throwable {
        StuckStatement stuck = new StuckStatement();
        FailOnTimeout stuckTimeout = FailOnTimeout.builder().withTimeout(TIMEOUT, MILLISECONDS).build(stuck);
        try {
            stuckTimeout.evaluate();
            // We must not get here, we expect a timeout exception
            Assert.fail("Expected timeout exception");
        } catch (Exception timeoutException) {
            StackTraceElement[] stackTrace = timeoutException.getStackTrace();
            boolean stackTraceContainsTheRealCauseOfTheTimeout = false;
            boolean stackTraceContainsOtherThanTheRealCauseOfTheTimeout = false;
            for (StackTraceElement element : stackTrace) {
                String methodName = element.getMethodName();
                if ("theRealCauseOfTheTimeout".equals(methodName)) {
                    stackTraceContainsTheRealCauseOfTheTimeout = true;
                }
                if ("notTheRealCauseOfTheTimeout".equals(methodName)) {
                    stackTraceContainsOtherThanTheRealCauseOfTheTimeout = true;
                }
            }
            Assert.assertTrue(
                    "Stack trace does not contain the real cause of the timeout",
                    stackTraceContainsTheRealCauseOfTheTimeout);
            Assert.assertFalse(
                    "Stack trace contains other than the real cause of the timeout, which can be very misleading",
                    stackTraceContainsOtherThanTheRealCauseOfTheTimeout);
        }
    }

    private static final class StuckStatement extends Statement {

        @Override
        public void evaluate() throws Throwable {
            try {
                // Must show up in stack trace
                theRealCauseOfTheTimeout();
            } catch (InterruptedException e) {
            } finally {
                // Must _not_ show up in stack trace
                notTheRealCauseOfTheTimeout();
            }
        }

        private void theRealCauseOfTheTimeout() throws InterruptedException {
            sleep(MAX_VALUE);
        }

        private void notTheRealCauseOfTheTimeout() {
            for (long now = currentTimeMillis(), eta = now + 1000L; now < eta; now = currentTimeMillis()) {
                // Doesn't matter, just pretend to be busy
                atan(now);
            }
        }
    }

    @Test
    public void threadGroupNotLeaked() throws Throwable {
        Collection<ThreadGroup> groupsBeforeSet = subGroupsOfCurrentThread();
        
        evaluateWithWaitDuration(0);
        
        for (ThreadGroup group: subGroupsOfCurrentThread()) {
            if (!groupsBeforeSet.contains(group) && "FailOnTimeoutGroup".equals(group.getName())) {
                Assert.fail("A 'FailOnTimeoutGroup' thread group remains referenced after the test execution.");
            }
        }
    }
    
    private Collection<ThreadGroup> subGroupsOfCurrentThread() {
        ThreadGroup[] subGroups = new ThreadGroup[256];
        int numGroups = currentThread().getThreadGroup().enumerate(subGroups);
        return Arrays.asList(subGroups).subList(0, numGroups);
    }
}
