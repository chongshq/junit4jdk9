module org.junit.internal {
    requires hamcrest.core;
    requires org.junit.runner;
    exports org.junit.internal;
    exports org.junit.internal.builders to org.junit.runner;
    exports org.junit.internal.runners to org.junit.runner;
}