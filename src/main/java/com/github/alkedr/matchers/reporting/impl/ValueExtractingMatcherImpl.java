package com.github.alkedr.matchers.reporting.impl;

import com.github.alkedr.matchers.reporting.PlanningMatcherForImplementing;
import com.github.alkedr.matchers.reporting.ValueExtractingMatcherForImplementing;
import org.hamcrest.Matcher;

import java.util.Collection;
import java.util.List;

public class ValueExtractingMatcherImpl<T, U extends ValueExtractingMatcherImpl<T, U>> extends PlanningMatcherImpl<T, U> implements ValueExtractingMatcherForImplementing<T, U> {
    @Override
    @SafeVarargs
    public final U it(final Matcher<? super T>... matchers) {
        return addPlannedCheck(new PlanningMatcherForImplementing.PlannedCheck<T>() {
            @Override
            public void execute(T item, ExecutedCompositeCheckBuilder checker) {
                for (Matcher<? super T> matcher : matchers) checker.runMatcher(matcher);
            }
        });
    }

    @Override
    public U it(final Collection<Matcher<? super T>> matchers) {
        return addPlannedCheck(new PlanningMatcherForImplementing.PlannedCheck<T>() {
            @Override
            public void execute(T item, ExecutedCompositeCheckBuilder checker) {
                for (Matcher<? super T> matcher : matchers) checker.runMatcher(matcher);
            }
        });
    }

    @Override
    public U it(final Matcher<? super T> matcher) {
        return addPlannedCheck(new PlanningMatcherForImplementing.PlannedCheck<T>() {
            @Override
            public void execute(T item, ExecutedCompositeCheckBuilder checker) {
                checker.runMatcher(matcher);
            }
        });
    }


    @Override
    @SafeVarargs
    public final <V> U value(final ValueExtractor<T, V> valueExtractor, final Matcher<? super V>... matchers) {
        return addPlannedCheck(new PlanningMatcherForImplementing.PlannedCheck<T>() {
            @Override
            public void execute(T item, ExecutedCompositeCheckBuilder checker) {
                for (Matcher<? super V> matcher : matchers) extractValueAndCreateCheckBuilder(item, checker, valueExtractor).runMatcher(matcher);
            }
        });
    }

    @Override
    public <V> U value(final ValueExtractor<T, V> valueExtractor, final List<Matcher<? super V>> matchers) {
        return addPlannedCheck(new PlanningMatcherForImplementing.PlannedCheck<T>() {
            @Override
            public void execute(T item, ExecutedCompositeCheckBuilder checker) {
                for (Matcher<? super V> matcher : matchers) extractValueAndCreateCheckBuilder(item, checker, valueExtractor).runMatcher(matcher);
            }
        });
    }

    @Override
    public <V> U value(final ValueExtractor<T, V> valueExtractor, final Matcher<? super V> matcher) {
        return addPlannedCheck(new PlanningMatcherForImplementing.PlannedCheck<T>() {
            @Override
            public void execute(T item, ExecutedCompositeCheckBuilder checker) {
                extractValueAndCreateCheckBuilder(item, checker, valueExtractor).runMatcher(matcher);
            }
        });
    }

    @SafeVarargs
    @Override
    public final <V> U value(final String name, final ValueExtractor<T, V> valueExtractor, final Matcher<? super V>... matchers) {
        return addPlannedCheck(new PlanningMatcherForImplementing.PlannedCheck<T>() {
            @Override
            public void execute(T item, ExecutedCompositeCheckBuilder checker) {
                for (Matcher<? super V> matcher : matchers) extractValueAndCreateCheckBuilder(item, checker, name, valueExtractor).runMatcher(matcher);
            }
        });
    }

    @Override
    public <V> U value(final String name, final ValueExtractor<T, V> valueExtractor, final List<Matcher<? super V>> matchers) {
        return addPlannedCheck(new PlanningMatcherForImplementing.PlannedCheck<T>() {
            @Override
            public void execute(T item, ExecutedCompositeCheckBuilder checker) {
                for (Matcher<? super V> matcher : matchers) extractValueAndCreateCheckBuilder(item, checker, name, valueExtractor).runMatcher(matcher);
            }
        });
    }

    @Override
    public <V> U value(final String name, final ValueExtractor<T, V> valueExtractor, final Matcher<? super V> matcher) {
        return addPlannedCheck(new PlanningMatcherForImplementing.PlannedCheck<T>() {
            @Override
            public void execute(T item, ExecutedCompositeCheckBuilder checker) {
                extractValueAndCreateCheckBuilder(item, checker, name, valueExtractor).runMatcher(matcher);
            }
        });
    }
}
