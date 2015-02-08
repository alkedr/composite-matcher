package com.github.alkedr.matchers.reporting;

import org.hamcrest.Matcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

import static com.github.alkedr.matchers.reporting.ReportingMatcher.ExecutedCompositeCheck.ExtractionStatus.*;

public class ValueExtractingMatcherForExtending<T, U extends ValueExtractingMatcherForExtending<T, U>> extends PlanningMatcherForExtending<T, U> {
    @SafeVarargs
    public final U it(final Matcher<? super T>... matchers) {
        return addPlannedCheck(new PlannedCheck<T>() {
            @Override
            public void execute(@NotNull Class<?> itemClass, @Nullable T item, @NotNull ExecutedCompositeCheckBuilder checker) {
                checker.runMatchers(matchers);
            }
        });
    }

    public U it(final Collection<? extends Matcher<? super T>> matchers) {
        return addPlannedCheck(new PlannedCheck<T>() {
            @Override
            public void execute(@NotNull Class<?> itemClass, @Nullable T item, @NotNull ExecutedCompositeCheckBuilder checker) {
                checker.runMatchers(matchers);
            }
        });
    }


    @SafeVarargs
    public final <V> U value(String name, ValueExtractor<T> valueExtractor, Matcher<? super V>... matchers) {
        return addPlannedCheck(valueExtractingPlannedCheckFromValueExtractor(name, valueExtractor, matchers));
    }

    public <V> U value(String name, ValueExtractor<T> valueExtractor, Collection<? extends Matcher<? super V>> matchers) {
        return addPlannedCheck(valueExtractingPlannedCheckFromValueExtractor(name, valueExtractor, matchers));
    }


    public interface ValueExtractor<T> {
        Object extract(@NotNull T item) throws Exception;
    }


    public abstract static class ValueExtractingPlannedCheck<T> implements PlannedCheck<T> {
        private final String valueName;
        private final Object matchersObject;

        protected ValueExtractingPlannedCheck(String valueName, Object matchersObject) {
            this.valueName = valueName;
            this.matchersObject = matchersObject;
        }

        @Override
        public void execute(@NotNull Class<?> itemClass, @Nullable T item, @NotNull ExecutedCompositeCheckBuilder checker) {
            if (item == null) {
                checker.subcheck().name(valueName).extractionStatus(MISSING);
            } else {
                try {
                    checker.subcheck().name(valueName).value(extract(item)).runMatchersObject(matchersObject);
                } catch (Exception e) {
                    checker.subcheck().name(valueName).extractionStatus(BROKEN).extractionException(e);
                }
            }
        }

        public abstract Object extract(@NotNull T item) throws Exception;
    }

    public static <T> ValueExtractingPlannedCheck<T> valueExtractingPlannedCheckFromValueExtractor(String valueName, final ValueExtractor<T> valueExtractor, Object matchersObject) {
        return new ValueExtractingPlannedCheck<T>(valueName, matchersObject) {
            @Override
            public Object extract(@NotNull T item) throws Exception {
                return valueExtractor.extract(item);
            }
        };
    }
}
