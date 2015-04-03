package com.github.alkedr.matchers.reporting.old;

import java.util.Collection;

public class CollectionMatcherForExtending<T, U extends CollectionMatcherForExtending<T, U>> extends IterableMatcherForExtending<Collection<T>, U> {
    public CollectionMatcherForExtending(Type type) {
        super(type);
    }
//    @Override
//    public U size(int value) {
//        return it(hasSize(value));
//    }
//
//    @Override
//    public U size(Matcher<? super Integer>... matchers) {
//        for (Matcher<? super Integer> matcher : matchers) it(hasSize(matcher));
//        return (U) this;
//    }
//
//    @Override
//    public U size(List<? extends Matcher<? super Integer>> matchers) {
//        for (Matcher<? super Integer> matcher : matchers) it(hasSize(matcher));
//        return (U) this;
//    }
}
