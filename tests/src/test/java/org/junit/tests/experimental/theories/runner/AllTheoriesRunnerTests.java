package org.junit.tests.experimental.theories.runner;

import org.junit.notify.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        FailingDataPointMethods.class,
        SuccessfulWithDataPointFields.class,
        TheoriesPerformanceTest.class,
        TypeMatchingBetweenMultiDataPointsMethod.class,
        UnsuccessfulWithDataPointFields.class,
        WhenNoParametersMatch.class,
        WithAutoGeneratedDataPoints.class,
        WithDataPointMethod.class,
        WithExtendedParameterSources.class,
        WithNamedDataPoints.class,
        WithOnlyTestAnnotations.class,
        WithParameterSupplier.class,
        WithUnresolvedGenericTypeVariablesOnTheoryParms.class
})
public class AllTheoriesRunnerTests {
}