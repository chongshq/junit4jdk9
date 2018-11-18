package org.junit.samples;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import junit.framework.JUnit4TestAdapter;
import org.junit.Assert;
import org.junit.common.Before;
import org.junit.common.BeforeClass;
import org.junit.common.Ignore;
import org.junit.common.Test;

/**
 * A sample test case, testing {@link java.util.ArrayList}.
 */
public class ListTest {
    protected List<Integer> fEmpty;
    protected List<Integer> fFull;
    protected static List<Integer> fgHeavy;

    public static void main(String... args) {
        junit.textui.TestRunner.run(suite());
    }

    @BeforeClass
    public static void setUpOnce() {
        fgHeavy = new ArrayList<Integer>();
        for (int i = 0; i < 1000; i++) {
            fgHeavy.add(i);
        }
    }

    @Before
    public void setUp() {
        fEmpty = new ArrayList<Integer>();
        fFull = new ArrayList<Integer>();
        fFull.add(1);
        fFull.add(2);
        fFull.add(3);
    }

    public static junit.j3.framework.Test suite() {
        return new JUnit4TestAdapter(ListTest.class);
    }

    @Ignore("not today")
    @Test
    public void capacity() {
        int size = fFull.size();
        for (int i = 0; i < 100; i++) {
            fFull.add(i);
        }
        Assert.assertTrue(fFull.size() == 100 + size);
    }

    @Test
    public void testCopy() {
        List<Integer> copy = new ArrayList<Integer>(fFull.size());
        copy.addAll(fFull);
        Assert.assertTrue(copy.size() == fFull.size());
        Assert.assertTrue(copy.contains(1));
    }

    @Test
    public void contains() {
        Assert.assertTrue(fFull.contains(1));
        Assert.assertTrue(!fEmpty.contains(1));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void elementAt() {
        int i = fFull.get(0);
        Assert.assertTrue(i == 1);
        fFull.get(fFull.size()); // Should throw IndexOutOfBoundsException
    }

    @Test
    public void removeAll() {
        fFull.removeAll(fFull);
        fEmpty.removeAll(fEmpty);
        Assert.assertTrue(fFull.isEmpty());
        Assert.assertTrue(fEmpty.isEmpty());
    }

    @Test
    public void removeElement() {
        fFull.remove(new Integer(3));
        Assert.assertTrue(!fFull.contains(3));
    }
}