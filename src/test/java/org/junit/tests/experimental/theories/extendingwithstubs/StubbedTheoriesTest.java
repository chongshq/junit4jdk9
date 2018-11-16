package org.junit.tests.experimental.theories.extendingwithstubs;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.common.Assume.assumeThat;

import org.junit.experimental.theories.Theory;
import org.junit.common.runner.RunWith;

@RunWith(StubbedTheories.class)
public class StubbedTheoriesTest {
    @Theory
    public void ask(@Stub Correspondent correspondent) {
        assumeThat(correspondent.getAnswer("What is five?", "four", "five"),
                is("five"));
    }
}
