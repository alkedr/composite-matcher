package com.github.alkedr.matchers.reporting.extractors.iterable;

import com.github.alkedr.matchers.reporting.checks.ExtractedValue;
import com.github.alkedr.matchers.reporting.extractors.ValueExtractor;
import org.hamcrest.Matcher;
import org.jetbrains.annotations.Nullable;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static org.hamcrest.StringDescription.asString;

public class SublistWithItemsExtractor<T> implements ValueExtractor<Iterable<T>, List<T>> {
    private final Matcher<? super Map.Entry<? super Integer, ? super T>> matcher;

    public SublistWithItemsExtractor(Matcher<? super Map.Entry<? super Integer, ? super T>> matcher) {
        this.matcher = matcher;
    }

    @Override
    public ExtractedValue extractValue(@Nullable Iterable<T> item) {
        if (item == null) {
            return new ExtractedValue("!<" + asString(matcher) + ">!", null, ExtractedValue.Status.MISSING);
        }
        List<T> result = new ArrayList<>();
        int i = 0;
        for (T t : item) {
            if (matcher.matches(new AbstractMap.SimpleEntry<>(i, t))) {
                result.add(t);
            }
            i++;
        }
        return new ExtractedValue("!<" + asString(matcher) + ">!", result);
    }


    public static <T> SublistWithItemsExtractor<T> sublistWithItemsWithIndexes(Matcher<Integer> matcher) {
        return sublistWithIndexItemPairs(having(on(Map.Entry.class).getKey(), matcher));
    }

    public static <T> SublistWithItemsExtractor<T> sublistWithItems(Matcher<? super T> matcher) {
        return sublistWithIndexItemPairs(having(on(Map.Entry.class).getValue(), matcher));
    }

    public static <T> SublistWithItemsExtractor<T> sublistWithIndexItemPairs(Matcher<? super Map.Entry<? super Integer, ? super T>> matcher) {
        return new SublistWithItemsExtractor<>(matcher);
    }
}
