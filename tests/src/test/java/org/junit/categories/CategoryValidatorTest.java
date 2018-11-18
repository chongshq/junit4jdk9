package org.junit.categories;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.common.After;
import org.junit.common.AfterClass;
import org.junit.common.Before;
import org.junit.common.BeforeClass;
import org.junit.common.Test;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;

public class CategoryValidatorTest {

    public static class SampleCategory {
    }

    public static class CategoryTest {
        @BeforeClass
        @Category(value = SampleCategory.class)
        public static void methodWithCategoryAndBeforeClass() {
        }

        @AfterClass
        @Category(value = SampleCategory.class)
        public static void methodWithCategoryAndAfterClass() {
        }

        @Before
        @Category(value = SampleCategory.class)
        public static void methodWithCategoryAndBefore() {
        }

        @After
        @Category(value = SampleCategory.class)
        public static void methodWithCategoryAndAfter() {
        }

        @Category(value = SampleCategory.class)
        public static void methodWithCategory() {
        }
    }

    @Test
    public void errorIsAddedWhenCategoryIsUsedWithBeforeClass() {
        FrameworkMethod method = new TestClass(CategoryTest.class).getAnnotatedMethods(BeforeClass.class).get(0);
        testAndAssertErrorMessage(method, "@BeforeClass can not be combined with @Category");
    }

    @Test
    public void errorIsAddedWhenCategoryIsUsedWithAfterClass() {
        FrameworkMethod method = new TestClass(CategoryTest.class).getAnnotatedMethods(AfterClass.class).get(0);
        testAndAssertErrorMessage(method, "@AfterClass can not be combined with @Category");
    }

    @Test
    public void errorIsAddedWhenCategoryIsUsedWithBefore() {
        FrameworkMethod method = new TestClass(CategoryTest.class).getAnnotatedMethods(Before.class).get(0);
        testAndAssertErrorMessage(method, "@Before can not be combined with @Category");
    }

    @Test
    public void errorIsAddedWhenCategoryIsUsedWithAfter() {
        FrameworkMethod method = new TestClass(CategoryTest.class).getAnnotatedMethods(After.class).get(0);
        testAndAssertErrorMessage(method, "@After can not be combined with @Category");
    }

    private void testAndAssertErrorMessage(FrameworkMethod method, String expectedErrorMessage) {
        List<Exception> errors = new CategoryValidator().validateAnnotatedMethod(method);

        Assert.assertThat(errors.size(), is(1));
        Exception exception = errors.get(0);
        Assert.assertThat(exception.getMessage(), is(expectedErrorMessage));
    }

    @Test
    public void errorIsNotAddedWhenCategoryIsNotCombinedWithIllegalCombination() throws NoSuchMethodException {
        FrameworkMethod method = new FrameworkMethod(CategoryTest.class.getMethod("methodWithCategory"));
        List<Exception> errors = new CategoryValidator().validateAnnotatedMethod(method);

        Assert.assertThat(errors.size(), is(0));
    }
}
