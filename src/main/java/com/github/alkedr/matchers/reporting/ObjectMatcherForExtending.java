package com.github.alkedr.matchers.reporting;

import org.hamcrest.Matcher;

import java.util.List;

import static ch.lambdaj.Lambda.argument;
import static org.apache.commons.lang3.reflect.FieldUtils.readField;
import static org.apache.commons.lang3.reflect.MethodUtils.invokeMethod;

public class ObjectMatcherForExtending<T, U extends ObjectMatcherForExtending<T, U>> extends ValueExtractingMatcherForExtending<T, U> {
    public <V> ValueCheckAdder<V> field(String nameForReportAndExtraction) {
        return field(nameForReportAndExtraction, nameForReportAndExtraction);
    }

    public <V> ValueCheckAdder<V> field(String nameForReport, final String nameForExtraction) {
        return field(nameForReport, new ValueExtractor<T, V>() {
            @Override
            public V extract(T t) throws Exception {
                return (V) readField(t, nameForExtraction, true);
            }
        });
    }

    public <V> ValueCheckAdder<V> field(ValueExtractor<T, V> fieldValueExtractor) {
        return field(null, fieldValueExtractor);
    }

    public <V> ValueCheckAdder<V> field(String nameForReport, ValueExtractor<T, V> fieldValueExtractor) {
        return new ValueCheckAdder<>(nameForReport, fieldValueExtractor);
    }


    public <V> ValueCheckAdder<V> method(String nameForReportAndExtraction, Object... arguments) {
        return method(nameForReportAndExtraction, nameForReportAndExtraction, arguments);
    }

    public <V> ValueCheckAdder<V> method(String nameForReport, final String nameForExtraction, final Object... arguments) {
        return new ValueCheckAdder<>(nameForReport, new ValueExtractor<T, V>() {
            @Override
            public V extract(T t) throws Exception {
                return (V) invokeMethod(t, nameForExtraction, arguments);
            }
        });
    }

    public <V> ValueCheckAdder<V> method(ValueExtractor<T, V> methodReturnValueExtractor) {
        return method(null, methodReturnValueExtractor);
    }

    public <V> ValueCheckAdder<V> method(String nameForReport, ValueExtractor<T, V> methodReturnValueExtractor) {
        return new ValueCheckAdder<>(nameForReport, methodReturnValueExtractor);
    }


    public <V> ValueCheckAdder<V> property(V lambdajPlaceholder) {
        return property(argument(lambdajPlaceholder).getInkvokedPropertyName(), lambdajPlaceholder);
    }

    public <V> ValueCheckAdder<V> property(String nameForReport, final V lambdajPlaceholder) {
        return new ValueCheckAdder<>(nameForReport, new ValueExtractor<T, V>() {
            @Override
            public V extract(T t) throws Exception {
                return argument(lambdajPlaceholder).evaluate(t);
            }
        });
    }


    public class ValueCheckAdder<V> {
        private final String name;
        private final ValueExtractor<T, V> extractor;

        private ValueCheckAdder(String name, ValueExtractor<T, V> extractor) {
            this.name = name;
            this.extractor = extractor;
        }

        public U is(Matcher<?/* super V*/>... matchers) {
            return name == null ? value(extractor, matchers) : value(name, extractor, matchers);
        }

        public U is(List<? extends Matcher<?/*FIXME super V*/>> matchers) {
            return name == null ? value(extractor, matchers) : value(name, extractor, matchers);
        }
    }
}
