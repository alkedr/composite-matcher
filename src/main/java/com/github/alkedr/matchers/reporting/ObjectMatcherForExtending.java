package com.github.alkedr.matchers.reporting;

import org.hamcrest.Matcher;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static ch.lambdaj.Lambda.argument;
import static org.apache.commons.lang3.reflect.FieldUtils.readField;
import static org.apache.commons.lang3.reflect.MethodUtils.invokeMethod;

public class ObjectMatcherForExtending<T, U extends ObjectMatcherForExtending<T, U>> extends ValueExtractingMatcherForExtending<T, U> {
    public <V> ValueCheckAdder<V> field(String nameForReportAndExtraction) {
        return field(nameForReportAndExtraction, nameForReportAndExtraction);
    }

    public <V> ValueCheckAdder<V> field(String nameForReport, final String nameForExtraction) {
        return field(nameForReport, new SimpleValueExtractor<T, V>() {
            @Override
            public V extract(@NotNull T t) throws IllegalAccessException {
                return (V) readField(t, nameForExtraction, true);
            }
        });
    }

    public <V> ValueCheckAdder<V> field(SimpleValueExtractor<T, V> fieldValueExtractor) {
        return field(extractFieldNameFromValueExtractor(fieldValueExtractor), fieldValueExtractor);
    }

    public <V> ValueCheckAdder<V> field(String nameForReport, SimpleValueExtractor<T, V> fieldValueExtractor) {
        return new ValueCheckAdder<>(nameForReport, fieldValueExtractor);
    }


    public <V> ValueCheckAdder<V> method(String nameForReportAndExtraction, Object... arguments) {
        return method(nameForReportAndExtraction, nameForReportAndExtraction, arguments);
    }

    public <V> ValueCheckAdder<V> method(String nameForReport, final String nameForExtraction, final Object... arguments) {
        return new ValueCheckAdder<>(nameForReport, new SimpleValueExtractor<T, V>() {
            @Override
            public V extract(@NotNull T t) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
                return (V) invokeMethod(t, nameForExtraction, arguments);
            }
        });
    }

    public <V> ValueCheckAdder<V> method(SimpleValueExtractor<T, V> methodReturnValueExtractor) {
        return method(extractMethodNameFromValueExtractor(methodReturnValueExtractor), methodReturnValueExtractor);
    }

    public <V> ValueCheckAdder<V> method(String nameForReport, SimpleValueExtractor<T, V> methodReturnValueExtractor) {
        return new ValueCheckAdder<>(nameForReport, methodReturnValueExtractor);
    }

    // TODO: method(lambdajPlaceholder), как property, только в названии полное имя метода с параметрами


    public <V> ValueCheckAdder<V> property(V lambdajPlaceholder) {
        return property(argument(lambdajPlaceholder).getInkvokedPropertyName(), lambdajPlaceholder);
    }

    public <V> ValueCheckAdder<V> property(String nameForReport, final V lambdajPlaceholder) {
        return new ValueCheckAdder<>(nameForReport, new SimpleValueExtractor<T, V>() {
            @Override
            public V extract(@NotNull T t) {
                return argument(lambdajPlaceholder).evaluate(t);
            }
        });
    }


    private <V> String extractFieldNameFromValueExtractor(SimpleValueExtractor<T, V> extractor) {
        // TODO
        return null;
    }

    private <V> String extractMethodNameFromValueExtractor(SimpleValueExtractor<T, V> methodReturnValueExtractor) {
        // TODO
        return null;
    }


    public class ValueCheckAdder<V> {
        private final String name;
        private final ValueExtractor<T> extractor;

        private ValueCheckAdder(String name, ValueExtractor<T> extractor) {
            this.name = name;
            this.extractor = extractor;
        }

        public U is(Matcher<? super V>... matchers) {
            return value(name, extractor, matchers);
        }

        public U is(List<? extends Matcher<? super V>> matchers) {
            return value(name, extractor, matchers);
        }
    }
}
