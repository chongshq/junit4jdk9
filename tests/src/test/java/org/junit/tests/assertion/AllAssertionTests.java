package org.junit.tests.assertion;

import org.junit.notify.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        AssertionTest.class,
        ComparisonFailureTest.class,
        MultipleFailureExceptionTest.class
})
public class AllAssertionTests {
}
