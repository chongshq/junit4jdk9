package org.junit.tests.junit3compatibility;

import junit.j3.framework.Test;
import org.junit.common.runner.RunWith;
import org.junit.runners.AllTests;

@RunWith(AllTests.class)
public class OldTests {
    static public Test suite() {
        return junit.tests.AllTests.suite();
    }
}
