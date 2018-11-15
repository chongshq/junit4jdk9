module org.junit.runner {
    requires common;
    requires hamcrest.core;
    exports org.junit.runner;
    exports org.junit.runners;
    exports org.junit.runner.notification to core;
    exports org.junit.runners.model to common;
}