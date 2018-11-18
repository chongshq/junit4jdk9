package org.junit.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.experimental.results.PrintableResult.testResult;
import static org.junit.experimental.results.ResultMatchers.isSuccessful;

import org.junit.Assert;
import org.junit.common.Rule;
import org.junit.common.Test;
import org.junit.experimental.results.PrintableResult;
import org.junit.experimental.results.ResultMatchers;

public class VerifierRuleTest {

    private static String sequence;

    public static class UsesVerifier {
        @Rule
        public Verifier collector = new Verifier() {
            @Override
            protected void verify() {
                sequence += "verify ";
            }
        };

        @Test
        public void example() {
            sequence += "test ";
        }
    }

    @Test
    public void verifierRunsAfterTest() {
        sequence = "";
        Assert.assertThat(PrintableResult.testResult(UsesVerifier.class), ResultMatchers.isSuccessful());
        Assert.assertEquals("test verify ", sequence);
    }
}
