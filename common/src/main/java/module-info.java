module common {
    exports org.junit;
    exports org.junit.rules to org.junit.runner;
    requires hamcrest.core;
}