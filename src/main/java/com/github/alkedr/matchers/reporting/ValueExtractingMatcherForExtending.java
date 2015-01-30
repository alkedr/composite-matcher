package com.github.alkedr.matchers.reporting;

import org.hamcrest.Matcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

import static com.github.alkedr.matchers.reporting.ReportingMatcher.ExtractionStatus.ERROR;

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
    public final <V> U value(final String name, final ValueExtractor<T> valueExtractor, final Matcher<? super V>... matchers) {
        return addPlannedCheck(new PlannedCheck<T>() {
            @Override
            public void execute(@NotNull Class<?> itemClass, @Nullable T item, @NotNull ExecutedCompositeCheckBuilder checker) {
                valueExtractor.extract(itemClass, item, checker).name(name).runMatchers(matchers);
            }
        });
    }

    public <V> U value(final String name, final ValueExtractor<T> valueExtractor, final List<? extends Matcher<? super V>> matchers) {
        return addPlannedCheck(new PlannedCheck<T>() {
            @Override
            public void execute(@NotNull Class<?> itemClass, @Nullable T item, @NotNull ExecutedCompositeCheckBuilder checker) {
                valueExtractor.extract(itemClass, item, checker).name(name).runMatchers(matchers);
            }
        });
    }


    public interface ValueExtractor<T> {
        ExecutedCompositeCheckBuilder extract(@NotNull Class<?> itemClass, @Nullable T item, @NotNull ExecutedCompositeCheckBuilder checker);
    }

    @FunctionalInterface
    public interface SimpleValueExtractor<T, V> extends ValueExtractor<T> {
        @Override
        default ExecutedCompositeCheckBuilder extract(@NotNull Class<?> itemClass, @Nullable T item, @NotNull ExecutedCompositeCheckBuilder checker) {
            ExecutedCompositeCheckBuilder result = checker.subcheck();
            if (item != null) {
                try {
                    result.value(extract(item));
                } catch (Exception e) {
                    result.extractionStatus(ERROR).extractionException(e);
                }
            }
            return result;
        }

        V extract(@NotNull T t) throws Exception;
    }
}
