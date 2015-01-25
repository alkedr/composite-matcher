package com.github.alkedr.matchers.reporting;

import org.hamcrest.Matcher;

import java.util.Collection;
import java.util.List;

public class ValueExtractingMatcherForExtending<T, U extends ValueExtractingMatcherForExtending<T, U>> extends PlanningMatcherForExtending<T, U> {
    @SafeVarargs
    public final U it(final Matcher<? super T>... matchers) {
        return addPlannedCheck(new PlannedCheck<T>() {
            @Override
            public void execute(T item, ExecutedCompositeCheckBuilder checker) {
                for (Matcher<? super T> matcher : matchers) checker.runMatcher(matcher);
            }
        });
    }

    public U it(final Collection<Matcher<? super T>> matchers) {
        return addPlannedCheck(new PlannedCheck<T>() {
            @Override
            public void execute(T item, ExecutedCompositeCheckBuilder checker) {
                for (Matcher<? super T> matcher : matchers) checker.runMatcher(matcher);
            }
        });
    }

    public U it(final Matcher<? super T> matcher) {
        return addPlannedCheck(new PlannedCheck<T>() {
            @Override
            public void execute(T item, ExecutedCompositeCheckBuilder checker) {
                checker.runMatcher(matcher);
            }
        });
    }


    @SafeVarargs
    public final <V> U value(final ValueExtractor<T, V> valueExtractor, final Matcher<? super V>... matchers) {
        return addPlannedCheck(new PlannedCheck<T>() {
            @Override
            public void execute(T item, ExecutedCompositeCheckBuilder checker) {
                for (Matcher<? super V> matcher : matchers) extractValueAndCreateCheckBuilder(item, checker, valueExtractor).runMatcher(matcher);
            }
        });
    }

    public <V> U value(final ValueExtractor<T, V> valueExtractor, final List<Matcher<? super V>> matchers) {
        return addPlannedCheck(new PlannedCheck<T>() {
            @Override
            public void execute(T item, ExecutedCompositeCheckBuilder checker) {
                for (Matcher<? super V> matcher : matchers) extractValueAndCreateCheckBuilder(item, checker, valueExtractor).runMatcher(matcher);
            }
        });
    }

    public <V> U value(final ValueExtractor<T, V> valueExtractor, final Matcher<? super V> matcher) {
        return addPlannedCheck(new PlannedCheck<T>() {
            @Override
            public void execute(T item, ExecutedCompositeCheckBuilder checker) {
                extractValueAndCreateCheckBuilder(item, checker, valueExtractor).runMatcher(matcher);
            }
        });
    }

    @SafeVarargs
    public final <V> U value(final String name, final ValueExtractor<T, V> valueExtractor, final Matcher<? super V>... matchers) {
        return addPlannedCheck(new PlannedCheck<T>() {
            @Override
            public void execute(T item, ExecutedCompositeCheckBuilder checker) {
                for (Matcher<? super V> matcher : matchers) extractValueAndCreateCheckBuilder(item, checker, name, valueExtractor).runMatcher(matcher);
            }
        });
    }

    public <V> U value(final String name, final ValueExtractor<T, V> valueExtractor, final List<Matcher<? super V>> matchers) {
        return addPlannedCheck(new PlannedCheck<T>() {
            @Override
            public void execute(T item, ExecutedCompositeCheckBuilder checker) {
                for (Matcher<? super V> matcher : matchers) extractValueAndCreateCheckBuilder(item, checker, name, valueExtractor).runMatcher(matcher);
            }
        });
    }

    public <V> U value(final String name, final ValueExtractor<T, V> valueExtractor, final Matcher<? super V> matcher) {
        return addPlannedCheck(new PlannedCheck<T>() {
            @Override
            public void execute(T item, ExecutedCompositeCheckBuilder checker) {
                extractValueAndCreateCheckBuilder(item, checker, name, valueExtractor).runMatcher(matcher);
            }
        });
    }

    public interface ValueExtractor<T, V> {
        V extract(T t) throws Exception;
    }
}
