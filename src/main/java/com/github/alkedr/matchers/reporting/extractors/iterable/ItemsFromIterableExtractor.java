package com.github.alkedr.matchers.reporting.extractors.iterable;

import com.github.alkedr.matchers.reporting.checks.ExtractedValue;
import com.github.alkedr.matchers.reporting.extractors.ValueExtractor;
import com.github.alkedr.matchers.reporting.extractors.ValueExtractorsExtractor;
import org.hamcrest.Matcher;
import org.jetbrains.annotations.Nullable;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.StringDescription.asString;

public class ItemsFromIterableExtractor<T> implements ValueExtractorsExtractor<Iterable<T>, T> {
    private final Matcher<? super Map.Entry<? super Integer, ? super T>> matcher;

    public ItemsFromIterableExtractor(Matcher<? super Map.Entry<? super Integer, ? super T>> matcher) {
        this.matcher = matcher;
    }

    @Override
    public List<? extends ValueExtractor<? super Iterable<T>, T>> extractValueExtractors(@Nullable Iterable<T> item) {
        if (item == null) {
            return asList(new ValueExtractor<Iterable<T>, T>() {
                @Override
                public ExtractedValue extractValue(@Nullable Iterable<T> item) {
                    return new ExtractedValue("!<" + asString(matcher) + ">!", null, ExtractedValue.Status.MISSING);
                }
            });
        }
        List<ValueExtractor<Iterable<T>, T>> result = new ArrayList<>();
        int i = 0;
        for (final T t : item) {
            if (matcher.matches(new AbstractMap.SimpleEntry<>(i, t))) {
                final int finalI = i;
                result.add(new ValueExtractor<Iterable<T>, T>() {
                    @Override
                    public ExtractedValue extractValue(@Nullable Iterable<T> item) {
                        return new ExtractedValue(String.valueOf(finalI), t);
                    }
                });
            }
            i++;
        }
        return result;
    }


    public static <T> ItemsFromIterableExtractor<T> itemsWithIndexes(Matcher<Integer> matcher) {
        return indexItemPairs(having(on(Map.Entry.class).getKey(), matcher));
    }

    public static <T> ItemsFromIterableExtractor<T> items(Matcher<? super T> matcher) {
        return indexItemPairs(having(on(Map.Entry.class).getValue(), matcher));
    }

    public static <T> ItemsFromIterableExtractor<T> allItems() {
        return indexItemPairs(anything());
    }

    public static <T> ItemsFromIterableExtractor<T> indexItemPairs(Matcher<? super Map.Entry<? super Integer, ? super T>> matcher) {
        return new ItemsFromIterableExtractor<>(matcher);
    }
}
