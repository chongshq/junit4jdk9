module common {
    requires hamcrest.core;
    exports org.junit.common;
    exports org.junit.common.internal;
    exports org.junit.common.runners;
    exports org.junit.common.function;
    exports org.junit.common.runner;
    exports org.junit.common.runner.manipulation;
    exports org.junit.common.runner.notification;
    opens org.junit.common.runner.notification;
}