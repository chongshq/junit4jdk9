package org.junit.categories;

import org.junit.common.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        CategoriesAndParameterizedTest.class,
        CategoryFilterFactoryTest.class,
        CategoryTest.class,
        CategoryValidatorTest.class,
        JavadocTest.class,
        MultiCategoryTest.class
})
public class AllCategoriesTests {
}
