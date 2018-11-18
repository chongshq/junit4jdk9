package org.junit.tests.description;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.common.Test;
import org.junit.common.runner.Description;

public class SuiteDescriptionTest {
    Description childless = Description.createSuiteDescription("a");
    Description anotherChildless = Description.createSuiteDescription("a");
    Description namedB = Description.createSuiteDescription("b");

    Description twoKids = descriptionWithTwoKids("foo", "bar");
    Description anotherTwoKids = descriptionWithTwoKids("foo", "baz");

    @Test
    public void equalsIsCorrect() {
        Assert.assertEquals(childless, anotherChildless);
        Assert.assertFalse(childless.equals(namedB));
        Assert.assertEquals(childless, twoKids);
        Assert.assertEquals(twoKids, anotherTwoKids);
        Assert.assertFalse(twoKids.equals(new Integer(5)));
    }

    @Test
    public void hashCodeIsReasonable() {
        Assert.assertEquals(childless.hashCode(), anotherChildless.hashCode());
        Assert.assertFalse(childless.hashCode() == namedB.hashCode());
    }

    private Description descriptionWithTwoKids(String first, String second) {
        Description twoKids = Description.createSuiteDescription("a");
        twoKids.addChild(Description.createTestDescription(getClass(), first));
        twoKids.addChild(Description.createTestDescription(getClass(), second));
        return twoKids;
    }
}
