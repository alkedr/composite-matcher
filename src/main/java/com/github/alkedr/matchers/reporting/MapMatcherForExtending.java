package com.github.alkedr.matchers.reporting;

import org.hamcrest.Matcher;

import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.on;
import static org.hamcrest.Matchers.equalTo;

public class MapMatcherForExtending<Key, Value, U extends MapMatcherForExtending<Key, Value, U>> extends ObjectMatcherForExtending<Map<Key, Value>, U> {
    public U size(int value) {
        return size(equalTo(value));
    }

    private U size(Matcher<? super Integer>... matchers) {
        return property(on(Map.class).size()).is(matchers);
    }

    private U size(List<? extends Matcher<? super Integer>> matchers) {
        return property(on(Map.class).size()).is(matchers);
    }

    // TODO: возможность считать ошибкой лишние значения

//    @SafeVarargs
//    public final U valueOf(final Key key, Matcher<? super Value>... matchers) {
//        return value(String.valueOf(key), new ValueExtractor<Map<Key, Value>>() {  // FIXME: ситуация, когда такого ключа нет
//            @Override
//            public Value extract(Map<Key, Value> map) throws Exception {
//                return map.get(key);
//            }
//        });
//    }
//
//    public U valueOf(final Key key, Collection<Matcher<? super Value>> matchers) {
//        return value(String.valueOf(key), new ValueExtractor<Map<Key, Value>>() {  // FIXME: ситуация, когда такого ключа нет
//            @Override
//            public Value extract(Map<Key, Value> map) throws Exception {
//                return map.get(key);
//            }
//        });
//    }
//
//
//    @SafeVarargs
//    public final U valuesOf(Matcher<? super Key> keyMatcher, Matcher<? super Value>... matchers) {
//        return null;
//    }
//
//    public U valuesOf(Matcher<? super Key> keyMatcher, Collection<Matcher<? super Value>> matchers) {
//        return null;
//    }
}
