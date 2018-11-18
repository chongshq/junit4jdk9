package org.junit.tests.manipulation;

import static java.util.Collections.reverseOrder;

import org.junit.common.runner.manipulation.Ordering;
import org.junit.common.runner.manipulation.Sorter;

/**
 * A sorter that orders tests reverse alphanumerically by test name.
 */
public final class ReverseAlphanumericSorter implements Ordering.Factory {

    public Ordering create(Ordering.Context context) {
        return new Sorter(reverseOrder(Comparators.alphanumeric()));
    }
}
