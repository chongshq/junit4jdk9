package org.junit.tests.junit3compatibility;

import static org.junit.Assert.assertNull;

import org.junit.Assert;
import org.junit.common.Test;
import org.junit.internal.builders.SuiteMethodBuilder;

public class ClassRequestTest {
    public static class PrivateSuiteMethod {
        static junit.framework.Test suite() {
            return null;
        }
    }

    @Test
    public void noSuiteMethodIfMethodPrivate() throws Throwable {
        Assert.assertNull(new SuiteMethodBuilder()
                .runnerForClass(PrivateSuiteMethod.class));
    }
}
