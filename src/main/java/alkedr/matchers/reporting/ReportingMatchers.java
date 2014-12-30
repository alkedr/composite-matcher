package alkedr.matchers.reporting;

import alkedr.matchers.reporting.matchers.iterable.IterableMatcher;
import alkedr.matchers.reporting.matchers.map.MapMatcher;
import alkedr.matchers.reporting.matchers.object.ObjectMatcher;

/**
 * User: alkedr
 * Date: 30.12.2014
 */
public class ReportingMatchers {
    public static <T> ObjectMatcher<T> object(Class<T> tClass) {
        return new ObjectMatcher<>(tClass);
    }

    public static <T> ObjectMatcher<T> beanWithFields(Class<T> tClass) {
        // TODO: универсальный матчер для непроверенных полей, который знает про коллекции, мапы и пр.
        // TODO: он должен будет как-то поддерживвть blacklisting полей и методов на случай  Object getThis() { return this; } ?
        ObjectMatcher<Object> recursiveFieldsMatcher = object(Object.class);
        recursiveFieldsMatcher.allFieldsAre(recursiveFieldsMatcher);
        return object(tClass).allFieldsAre(recursiveFieldsMatcher);
    }

    // TODO: beanWithPrivateFields

    public static <T> ObjectMatcher<T> beanWithGetters(Class<T> tClass) {
        ObjectMatcher<Object> recursiveGettersMatcher = object(Object.class);
        recursiveGettersMatcher.allMethodsThatReturnNonVoidReturn(recursiveGettersMatcher);
        return object(tClass).allMethodsThatReturnNonVoidReturn(recursiveGettersMatcher);
    }

    // TODO: beanWithPrivateGetters?




    public static <T, U> MapMatcher<T, U> map() {
        return new MapMatcher<>();
    }

    public static <T> IterableMatcher<T> iterable() {
        return new IterableMatcher<>();
    }
}
