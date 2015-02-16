package com.github.alkedr.matchers.reporting;

import org.hamcrest.Matcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

import static com.github.alkedr.matchers.reporting.ReportingMatcher.ExecutedCompositeCheck.ExtractionStatus.BROKEN;
import static com.github.alkedr.matchers.reporting.ReportingMatcher.ExecutedCompositeCheck.ExtractionStatus.MISSING;

public class ValueExtractingMatcherForExtending<T, U extends ValueExtractingMatcherForExtending<T, U>> extends PlanningMatcherForExtending<T, U> {
    public ValueExtractingMatcherForExtending() {
    }

    public ValueExtractingMatcherForExtending(@NotNull Class<?> tClass) {
        super(tClass);
    }

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
        private String valueName = null;
        private Object matchersObject = null;

        protected ValueExtractingPlannedCheck() {
        }

        protected ValueExtractingPlannedCheck(String valueName) {
            this.valueName = valueName;
        }

        protected ValueExtractingPlannedCheck(String valueName, Object matchersObject) {
            this.valueName = valueName;
            this.matchersObject = matchersObject;
        }

        public String getValueName() {
            return valueName;
        }

        public void setValueName(String valueName) {
            this.valueName = valueName;
        }

        public Object getMatchersObject() {
            return matchersObject;
        }

        public void setMatchersObject(Object matchersObject) {
            this.matchersObject = matchersObject;
        }

        @Override
        public void execute(@NotNull Class<?> itemClass, @Nullable T item, @NotNull ExecutedCompositeCheckBuilder checker) {
            if (item == null) {
                checker.subcheck().name(valueName).extractionStatus(MISSING);
            } else {
                try {
                    // value(extract(item)) call must be first because extract() can change valueName
                    checker.subcheck().value(extract(item)).name(valueName).runMatchersObject(matchersObject);
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
