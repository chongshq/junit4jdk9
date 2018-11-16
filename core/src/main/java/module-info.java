module core {
    requires common;
    requires hamcrest.core;
    requires util;
    exports junit.framework;
    exports junit.extensions;
    exports junit.textui;
    exports junit.runner;

    exports org.junit;
}