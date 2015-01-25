package com.github.alkedr.matchers.reporting;

import org.hamcrest.Matcher;

import java.util.Collection;
import java.util.List;

public interface ValueExtractingMatcherForImplementing<T, U extends ValueExtractingMatcherForImplementing<T, U>> extends ReportingMatcher<T> {
    // запуск матчеров на item
    U it(Matcher<? super T>... matchers);
    U it(Collection<Matcher<? super T>> matchers);
    U it(Matcher<? super T> matcher);
    // valueExtractor - лямбда вида 'x -> x.field' или 'x -> x.method()', анализируем байт-код для определения поля или метода
    <V> U value(ValueExtractor<T, V> valueExtractor, Matcher<? super V>... matchers);
    <V> U value(ValueExtractor<T, V> valueExtractor, List<Matcher<? super V>> matchers);
    <V> U value(ValueExtractor<T, V> valueExtractor, Matcher<? super V> matcher);
    // valueExtractor - любая лямбда, name - имя для отчёта
    <V> U value(String name, ValueExtractor<T, V> valueExtractor, Matcher<? super V>... matchers);
    <V> U value(String name, ValueExtractor<T, V> valueExtractor, List<Matcher<? super V>> matchers);
    <V> U value(String name, ValueExtractor<T, V> valueExtractor, Matcher<? super V> matcher);


    interface ValueExtractor<T, V> {
        V extract(T t) throws Exception;
    }




    /*
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
*/
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
