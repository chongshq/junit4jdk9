package org.junit.tests.experimental.results;

import org.junit.notify.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        PrintableResultTest.class,
        ResultMatchersTest.class
})
public class AllResultsTests {
}
