package com.github.alkedr.matchers.reporting;

import org.hamcrest.Matcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public class ValueExtractingMatcherForExtending<T, U extends ValueExtractingMatcherForExtending<T, U>> extends PlanningMatcherForExtending<T, U> {
    @SafeVarargs
    public final U it(final Matcher<?>... matchers) {
        return addPlannedCheck(new PlannedCheck<T>() {
            @Override
            public void execute(@NotNull Class<?> itemClass, @Nullable T item, @NotNull ExecutedCompositeCheckBuilder checker) {
                checker.runMatchers(matchers);
            }
        });
    }

    public U it(final Collection<? extends Matcher<?>> matchers) {
        return addPlannedCheck(new PlannedCheck<T>() {
            @Override
            public void execute(@NotNull Class<?> itemClass, @Nullable T item, @NotNull ExecutedCompositeCheckBuilder checker) {
                checker.runMatchers(matchers);
            }
        });
    }

    public U it(final Matcher<?> matcher) {
        return addPlannedCheck(new PlannedCheck<T>() {
            @Override
            public void execute(@NotNull Class<?> itemClass, @Nullable T item, @NotNull ExecutedCompositeCheckBuilder checker) {
                checker.runMatcher(matcher);
            }
        });
    }


    @SafeVarargs
    public final <V> U value(final ValueExtractor<T, V> valueExtractor, final Matcher<?>... matchers) {
        return addPlannedCheck(new PlannedCheck<T>() {
            @Override
            public void execute(@NotNull Class<?> itemClass, @Nullable T item, @NotNull ExecutedCompositeCheckBuilder checker) {
                extractValueAndCreateCheckBuilder(item, checker, valueExtractor).runMatchers(matchers);
            }
        });
    }

    public <V> U value(final ValueExtractor<T, V> valueExtractor, final List<? extends Matcher<?>> matchers) {
        return addPlannedCheck(new PlannedCheck<T>() {
            @Override
            public void execute(@NotNull Class<?> itemClass, @Nullable T item, @NotNull ExecutedCompositeCheckBuilder checker) {
                extractValueAndCreateCheckBuilder(item, checker, valueExtractor).runMatchers(matchers);
            }
        });
    }

    public <V> U value(final ValueExtractor<T, V> valueExtractor, final Matcher<?> matcher) {
        return addPlannedCheck(new PlannedCheck<T>() {
            @Override
            public void execute(@NotNull Class<?> itemClass, @Nullable T item, @NotNull ExecutedCompositeCheckBuilder checker) {
                extractValueAndCreateCheckBuilder(item, checker, valueExtractor).runMatcher(matcher);
            }
        });
    }


    @SafeVarargs
    public final <V> U value(final String name, final ValueExtractor<T, V> valueExtractor, final Matcher<?>... matchers) {
        return addPlannedCheck(new PlannedCheck<T>() {
            @Override
            public void execute(@NotNull Class<?> itemClass, @Nullable T item, @NotNull ExecutedCompositeCheckBuilder checker) {
                extractValueAndCreateCheckBuilder(item, checker, name, valueExtractor).runMatchers(matchers);
            }
        });
    }

    public <V> U value(final String name, final ValueExtractor<T, V> valueExtractor, final List<? extends Matcher<?>> matchers) {
        return addPlannedCheck(new PlannedCheck<T>() {
            @Override
            public void execute(@NotNull Class<?> itemClass, @Nullable T item, @NotNull ExecutedCompositeCheckBuilder checker) {
                extractValueAndCreateCheckBuilder(item, checker, name, valueExtractor).runMatchers(matchers);
            }
        });
    }

    public <V> U value(final String name, final ValueExtractor<T, V> valueExtractor, final Matcher<?> matcher) {
        return addPlannedCheck(new PlannedCheck<T>() {
            @Override
            public void execute(@NotNull Class<?> itemClass, @Nullable T item, @NotNull ExecutedCompositeCheckBuilder checker) {
                extractValueAndCreateCheckBuilder(item, checker, name, valueExtractor).runMatcher(matcher);
            }
        });
    }


    private <V> ExecutedCompositeCheckBuilder extractValueAndCreateCheckBuilder(T item, ExecutedCompositeCheckBuilder checker, ValueExtractor<T, V> extractor) {
        return extractValueAndCreateCheckBuilder(item, checker, extractValueNameFromValueExtractor(extractor), extractor);
    }

    private <V> ExecutedCompositeCheckBuilder extractValueAndCreateCheckBuilder(T item, ExecutedCompositeCheckBuilder checker,
                                                                                String name, ValueExtractor<T, V> extractor) {
        try {
            return checker.createCompositeCheck(name, extractor.extract(item), ExtractionStatus.NORMAL, null);
        } catch (Exception exception) {
            return checker.createCompositeCheck(name, null, ExtractionStatus.ERROR, exception);
        }
    }

    private <V> String extractValueNameFromValueExtractor(ValueExtractor<T, V> extractor) {
        // TODO
        return null;
    }


    public interface ValueExtractor<T, V> {
        V extract(T t) throws Exception;
    }
}
