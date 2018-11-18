package org.junit.tests.description;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.common.Test;
import org.junit.common.runner.Description;

public class TestDescriptionTest {
    @Test
    public void equalsIsFalseForNonTestDescription() {
        Assert.assertFalse(Description.createTestDescription(getClass(), "a").equals(new Integer(5)));
    }

    @Test
    public void equalsIsTrueForSameNameAndNoExplicitUniqueId() {
        Assert.assertTrue(Description.createSuiteDescription("Hello").equals(Description.createSuiteDescription("Hello")));
    }

    @Test
    public void equalsIsFalseForSameNameAndDifferentUniqueId() {
        Assert.assertFalse(Description.createSuiteDescription("Hello", 2).equals(Description.createSuiteDescription("Hello", 3)));
    }
}