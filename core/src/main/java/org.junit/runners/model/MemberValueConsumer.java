package org.junit.runners.model;

import org.junit.model.runners.model.FrameworkMember;

/**
 * Represents a receiver for values of annotated fields/methods together with the declaring member.
 *
 * @see TestClass#collectAnnotatedFieldValues(Object, Class, Class, MemberValueConsumer)
 * @see TestClass#collectAnnotatedMethodValues(Object, Class, Class, MemberValueConsumer)
 * @since 4.13
 */
public interface MemberValueConsumer<T> {
    /**
     * Receives the next value and its declaring member.
     *
     * @param member declaring member ({@link FrameworkMethod} or  FrameworkField}
     * @param value the value of the next member
     */
    void accept(FrameworkMember member, T value);
}
