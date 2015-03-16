package com.github.alkedr.matchers.reporting;

import org.hamcrest.Matcher;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;

public class NonExhaustiveMapMatcherForExtending<Key, Value, U extends NonExhaustiveMapMatcherForExtending<Key, Value, U>>
        extends ValueExtractingMatcherForExtending<Map<Key, Value>, U> {

    public U size(int value) {
        return sizeImpl(equalTo(value));
    }

    @SafeVarargs
    public final U size(Matcher<? super Integer>... matchers) {
        return sizeImpl(matchers);
    }

    public U size(List<? extends Matcher<? super Integer>> matchers) {
        return sizeImpl(matchers);
    }


    public U size(String name, int value) {
        return sizeImpl(name, equalTo(value));
    }

    @SafeVarargs
    public final U size(String name, Matcher<? super Integer>... matchers) {
        return sizeImpl(name, matchers);
    }

    public U size(String name, List<? extends Matcher<? super Integer>> matchers) {
        return sizeImpl(name, matchers);
    }



    public U entry(Key key, Value value) {
        return entryImpl(key, equalTo(value));
    }

    @SafeVarargs
    public final U entry(Key key, Matcher<? super Key>... valueMatchers) {
        return entryImpl(key, equalTo(valueMatchers));
    }

    public U entry(Key key, List<? extends Matcher<? super Key>> valueMatchers) {
        return entryImpl(key, equalTo(valueMatchers));
    }


    public U entry(String name, Key key, Value value) {
        return entryImpl(name, key, equalTo(value));
    }

    @SafeVarargs
    public final U entry(String name, Key key, Matcher<? super Key>... valueMatchers) {
        return entryImpl(name, key, equalTo(valueMatchers));
    }

    public U entry(String name, Key key, List<? extends Matcher<? super Key>> valueMatchers) {
        return entryImpl(name, key, equalTo(valueMatchers));
    }



    private U sizeImpl(Object matchers) {
        return sizeImpl("size", matchers);
    }

    private U sizeImpl(String name, Object matchers) {
        return addPlannedCheck(new ValueExtractingPlannedCheck<Map<Key, Value>>(name, matchers) {
            @Override
            public Integer getValue(@NotNull Map<Key, Value> item) {
                return item.size();
            }
        });
    }

    private U entryImpl(Key key, Object valueMatchers) {
        return entryImpl(String.valueOf(key), key, valueMatchers);
    }

    private U entryImpl(String name, final Key key, Object valueMatchers) {
        return addPlannedCheck(new ValueExtractingPlannedCheck<Map<Key, Value>>(name, valueMatchers) {
            @Override
            public boolean isMissing(@NotNull Map<Key, Value> item) {
                return !item.containsKey(key);
            }

            @Override
            public Object getValue(@NotNull Map<Key, Value> item) {
                return item.get(key);
            }
        });
    }
}
