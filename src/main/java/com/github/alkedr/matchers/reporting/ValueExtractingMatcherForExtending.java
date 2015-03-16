package com.github.alkedr.matchers.reporting;

import org.hamcrest.Matcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

import static com.github.alkedr.matchers.reporting.ReportingMatcher.ExecutedCompositeCheck.ExtractionStatus.BROKEN;
import static com.github.alkedr.matchers.reporting.ReportingMatcher.ExecutedCompositeCheck.ExtractionStatus.MISSING;
import static com.github.alkedr.matchers.reporting.ReportingMatcher.ExecutedCompositeCheck.ExtractionStatus.UNEXPECTED;

public class ValueExtractingMatcherForExtending<T, U extends ValueExtractingMatcherForExtending<T, U>> extends PlanningMatcherForExtending<T, U> {
//    public ValueExtractingMatcherForExtending() {
//    }

    public ValueExtractingMatcherForExtending(@NotNull Class<?> tClass) {
        super(tClass);
    }

    // TODO: Заменить это на аналог allOf()
    @SafeVarargs
    public final U it(final Matcher<? super T>... matchers) {
        return addPlannedCheck(new PlannedCheck<T>() {
            @Override
            public void execute(@NotNull Class<?> itemClass, @Nullable T item, @NotNull ExecutedCompositeCheckBuilder checker) {
                checker.runMatchers(matchers);
            }
        });
    }

    public U it(final Iterable<? extends Matcher<? super T>> matchers) {
        return addPlannedCheck(new PlannedCheck<T>() {
            @Override
            public void execute(@NotNull Class<?> itemClass, @Nullable T item, @NotNull ExecutedCompositeCheckBuilder checker) {
                checker.runMatchers(matchers);
            }
        });
    }


    @SafeVarargs
    public final <V> U value(String name, ValueExtractor<T> valueExtractor, Matcher<? super V>... matchers) {
        return valueImpl(name, valueExtractor, matchers);
    }

    public <V> U value(String name, ValueExtractor<T> valueExtractor, Collection<? extends Matcher<? super V>> matchers) {
        return valueImpl(name, valueExtractor, matchers);
    }



    public interface ValueExtractor<T> {
        Object extract(@NotNull T item) throws Exception;
    }

    public interface UncheckedValuesAdder {
        void add(String name, Object value);
    }


    public interface ValueExtractingCheckExtractor<T> {
        void extract(Collection<ValueExtractingCheck<T>> storage);
    }

    public abstract static class ValueExtractingCheck<T> implements ValueExtractingCheckExtractor<T> {
        @Nullable PlannedCheck<T> next = null;

        public void execute(@NotNull Class<?> itemClass, @Nullable T item, @NotNull ExecutedCompositeCheckBuilder checker) {
            ExecutedCompositeCheckBuilder subcheck = checker.subcheck().name(getName());
            try {
                if (item == null || isMissing(item)) {
                    subcheck.extractionStatus(MISSING);
                } else {
                    if (isUnexpected(item)) subcheck.extractionStatus(UNEXPECTED);
                    subcheck.value(getValue(item));
                }
            } catch (Exception e) {
                subcheck.extractionStatus(BROKEN).extractionException(e);
            }
//            subcheck.runMatchersObject(matchersObject);
        }

        protected boolean isMissing(@NotNull T item) throws Exception {
            return false;
        }

        protected boolean isUnexpected(@Nullable T item) throws Exception {
            return false;
        }

        protected abstract String getName();

        protected abstract Object getValue(@NotNull T item) throws Exception;

        public void extract(Collection<ValueExtractingCheck<T>> storage) {
        }
    }



    public abstract static class ValueExtractingPlannedCheck<T> extends PlannedCheck<T> {
        private final String valueName;
        private Object matchersObject = null;

        protected ValueExtractingPlannedCheck(String valueName) {
            this.valueName = valueName;
        }

        protected ValueExtractingPlannedCheck(String valueName, Object matchersObject) {
            this.valueName = valueName;
            this.matchersObject = matchersObject;
        }

        public Object getMatchersObject() {
            return matchersObject;
        }

        public void setMatchersObject(Object matchersObject) {
            this.matchersObject = matchersObject;
        }

        @Override
        public void execute(@NotNull Class<?> itemClass, @Nullable T item, @NotNull ExecutedCompositeCheckBuilder checker) {
            ExecutedCompositeCheckBuilder subcheck = checker.subcheck().name(valueName);
            try {
                if (item == null || isMissing(item)) {
                    subcheck.extractionStatus(MISSING);
                } else {
                    if (isUnexpected(item)) subcheck.extractionStatus(UNEXPECTED);
                    subcheck.value(getValue(item));
                }
            } catch (Exception e) {
                subcheck.extractionStatus(BROKEN).extractionException(e);
            }
            subcheck.runMatchersObject(matchersObject);
        }

        protected boolean isMissing(@NotNull T item) throws Exception {
            return false;
        }

        protected boolean isUnexpected(@Nullable T item) throws Exception {
            return false;
        }

        protected abstract Object getValue(@NotNull T item) throws Exception;
    }

    private U valueImpl(String valueName, final ValueExtractor<T> valueExtractor, Object matchersObject) {
        return addPlannedCheck(new ValueExtractingPlannedCheck<T>(valueName, matchersObject) {
            @Override
            public Object getValue(@NotNull T item) throws Exception {
                return valueExtractor.extract(item);
            }
        });
    }
}
