package org.junit.internal.requests;

import java.util.Comparator;

import org.junit.common.runner.Description;
import org.junit.runner.Request;
import org.junit.common.runner.Runner;
import org.junit.common.runner.manipulation.Sorter;

public class SortingRequest extends Request {
    private final Request request;
    private final Comparator<Description> comparator;

    public SortingRequest(Request request, Comparator<Description> comparator) {
        this.request = request;
        this.comparator = comparator;
    }

    @Override
    public Runner getRunner() {
        Runner runner = request.getRunner();
        new Sorter(comparator).apply(runner);
        return runner;
    }
}
