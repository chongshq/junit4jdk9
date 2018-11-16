package org.junit.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface ClassRule {

    /**
     * Specifies the order in which rules are applied. The rules with a higher value are inner.
     *
     * @since 4.13
     */
    int order() default Rule.DEFAULT_ORDER;

}
