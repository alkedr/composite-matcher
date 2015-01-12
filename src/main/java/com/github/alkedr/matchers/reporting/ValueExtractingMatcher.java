package com.github.alkedr.matchers.reporting;

import java.util.Map;

public class ValueExtractingMatcher<T> extends ValueExtractingMatcherImpl<T, ValueExtractingMatcher<T>> {
    public ValueExtractingMatcher(Class<? super T> tClass) {
        super(tClass);
    }





    public static <T> ValueExtractingMatcher<T> object(Class<T> tClass) {
        return new ValueExtractingMatcher<>(tClass);
    }

    public static <T> ValueExtractingMatcher<T> beanWithGetters(Class<T> tClass) {
        // TODO
        return new ValueExtractingMatcher<>(tClass);
    }

    public static <T, U> ValueExtractingMatcher<Map<T, U>> map(Class<T> keyClass, Class<U> valueClass) {
        return new ValueExtractingMatcher<>(Map.class);
    }

//    public static <T> ObjectMatcher<T> beanWithFields(Class<T> tClass) {
//        // TODO: универсальный матчер для непроверенных полей, который знает про коллекции, мапы и пр.
//        // TODO: он должен будет как-то поддерживвть blacklisting полей и методов на случай  Object getThis() { return this; } ?
//        ObjectMatcher<Object> recursiveFieldsMatcher = object(Object.class);
//        recursiveFieldsMatcher.allFieldsAre(recursiveFieldsMatcher);
//        return object(tClass).allFieldsAre(recursiveFieldsMatcher);
//    }
//
//    // TODO: beanWithPrivateFields
//
//    public static <T> ObjectMatcher<T> beanWithGetters(Class<T> tClass) {
//        ObjectMatcher<Object> recursiveGettersMatcher = object(Object.class);
//        recursiveGettersMatcher.allMethodsThatReturnNonVoidReturn(recursiveGettersMatcher);
//        return object(tClass).allMethodsThatReturnNonVoidReturn(recursiveGettersMatcher);
//    }
//
//    // TODO: beanWithPrivateGetters?
//
//
//
//
//    public static <T, U> MapMatcher<T, U> map() {
//        return new MapMatcher<>();
//    }
//
//    public static <T> IterableMatcher<T> iterable() {
//        return new IterableMatcher<>();
//    }
}
