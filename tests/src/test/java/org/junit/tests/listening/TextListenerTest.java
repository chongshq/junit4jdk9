package org.junit.tests.listening;

import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.common.Test;
import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;
import org.junit.tests.TestSystem;

public class TextListenerTest extends TestCase {

    private JUnitCore runner;
    private OutputStream results;

    @Override
    public void setUp() {
        runner = new JUnitCore();
        TestSystem system = new TestSystem();
        results = system.outContents();
        runner.addListener(new TextListener(system));
    }

    public static class OneTest {
        @Test
        public void one() {
        }
    }

    public void testSuccess() throws Exception {
        this.setUp();
        runner.run(OneTest.class);
        assertTrue(results.toString().startsWith(convert(".\nTime: ")));
        assertTrue(results.toString().endsWith(convert("\n\nOK (1 test)\n\n")));
    }

    public static class ErrorTest {
        @Test
        public void error() throws Exception {
            throw new Exception();
        }
    }

    public void testError() throws Exception {
        this.setUp();
        runner.run(ErrorTest.class);
        assertTrue(results.toString().startsWith(convert(".E\nTime: ")));
        assertTrue(results.toString().indexOf(convert("\nThere was 1 failure:\n1) error(TextListenerTest$ErrorTest)\njava.lang.Exception")) != -1);
    }

    public static class Time {
        @Test
        public void time() {
        }
    }

    public void testTime() {
        this.setUp();
        runner.run(Time.class);
        Assert.assertThat(results.toString(), containsString("Time: "));
        Assert.assertThat(results.toString(), not(containsString(convert("Time: \n"))));
    }

    private String convert(String string) {
        OutputStream resultsStream = new ByteArrayOutputStream();
        PrintStream writer = new PrintStream(resultsStream);
        writer.println();
        return string.replace("\n", resultsStream.toString());
    }
}
