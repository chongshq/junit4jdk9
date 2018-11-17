package junit.framework;

import junit.j3.framework.Test;
import junit.j3.framework.TestResult;
import org.junit.common.runner.Describable;
import org.junit.common.runner.Description;

public class JUnit4TestCaseFacade implements Test, Describable {
    private final Description fDescription;

    JUnit4TestCaseFacade(Description description) {
        fDescription = description;
    }

    @Override
    public String toString() {
        return getDescription().toString();
    }

    public int countTestCases() {
        return 1;
    }

    public void run(TestResult result) {
        throw new RuntimeException(
                "This test stub created only for informational purposes.");
    }

    public Description getDescription() {
        return fDescription;
    }
}