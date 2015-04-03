package com.github.alkedr.matchers.reporting.old;

import org.hamcrest.Matcher;

import java.util.*;

import static org.hamcrest.Matchers.equalTo;

// TODO: возможность считать ошибкой лишние значения
public class IterableMatcherForExtending<T, U extends IterableMatcherForExtending<T, U>> extends ObjectMatcherForExtending<Iterable<T>, U> {
    private final Type type;
    private int currentIndex = 0;
    private final Collection<Matcher<Integer>> sizeMatchers = new ArrayList<>();
    private final Map<Integer, Collection<? super Matcher<? super T>>> indexToItemMatchers = new HashMap<>();
    private final Map<Matcher<Integer>, Collection<? super Matcher<? super T>>> indexMatcherToItemMatchers = new HashMap<>();


    public IterableMatcherForExtending(Type type) {
        super(Iterable.class);
        this.type = type;
    }


    public U size(int value) {
        return size(equalTo(value));
    }

    public U size(Matcher<Integer>... matchers) {
        Collections.addAll(sizeMatchers, matchers);
        return (U) this;
    }

    public U size(List<? extends Matcher<Integer>> matchers) {
        sizeMatchers.addAll(matchers);
        return (U) this;
    }


    public U item(Matcher<? super T>... matchers) {
        return item(currentIndex++, matchers);
    }

    public U item(Collection<? extends Matcher<? super T>> matchers) {
        return item(currentIndex++, matchers);
    }


    public U item(int index, Matcher<? super T>... matchers) {
        return items(index, 1, matchers);
    }

    public U item(int index, Collection<? extends Matcher<? super T>> matchers) {
        return items(index, 1, matchers);
    }


    public U items(int index, int count, Matcher<? super T>... matchers) {
        for (int i = index; i < index + count; i++) {
            Collections.addAll(getOrCreateEntryForIndex(i), matchers);
        }
        return (U) this;
    }

    public U items(int index, int count, Collection<? extends Matcher<? super T>> matchers) {
        for (int i = index; i < index + count; i++) {
            getOrCreateEntryForIndex(index).addAll(matchers);
        }
        return (U) this;
    }


//    public U items(final int index, final Matcher<? super Integer> countMatcher, Matcher<? super T>... matchers) {
//        return items(new Predicate<Integer>() {
//            @Override
//            public boolean apply(Integer item) {
//                return (item >= index) && (countMatcher.matches(item - index + 1));
//            }
//        }, matchers);
//    }
//
//    public U items(final int index, final Matcher<? super Integer> countMatcher, Collection<? extends Matcher<? super T>> matchers) {
//        return items(new Predicate<Integer>() {
//            @Override
//            public boolean apply(Integer item) {
//                return (item >= index) && (countMatcher.matches(item - index + 1));
//            }
//        }, matchers);
//    }


    public U items(Matcher<Integer> indexMatcher, Matcher<? super T>... matchers) {
        Collections.addAll(getOrCreateEntryForIndexMatcher(indexMatcher), matchers);
        return (U) this;
    }

    public U items(Matcher<Integer> indexMatcher, Collection<? extends Matcher<? super T>> matchers) {
        getOrCreateEntryForIndexMatcher(indexMatcher).addAll(matchers);
        return (U) this;
    }



//    @Override
//    public void runChecks(@Nullable Iterable<T> item, ExecutedCompositeCheckBuilder checker) {
//        super.runChecks(item, checker);
//        switch (type) {
//            case ORDERED: runChecksOrdered(item, checker); break;
//            case UNORDERED: runChecksUnordered(item, checker); break;
//        }
//    }

//    private void runChecksOrdered(Iterable<T> iterable, ExecutedCompositeCheckBuilder checker) {
//        int i = 0;
//        for (T item : iterable) {
//            ExecutedCompositeCheckBuilder itemChecker = checker.createCompositeCheck(String.valueOf(i), item, ExtractionStatus.NORMAL, null);
//            Collection<? super Matcher<? super T>> matchersForIndex = indexToItemMatchers.get(i);
//            if (matchersForIndex != null) itemChecker.runMatchers(matchersForIndex);
//            for (Map.Entry<Matcher<Integer>, Collection<? super Matcher<? super T>>> entry : indexMatcherToItemMatchers.entrySet()) {
//                if (entry.getKey().matches(i)) itemChecker.runMatchers(entry.getValue());
//            }
//            i++;
//        }
//    }
//
//    private void runChecksUnordered(Iterable<T> item, ExecutedCompositeCheckBuilder checker) {
//        if (item instanceof Collection && item instanceof RandomAccess && indexMatcherToItemMatchers.isEmpty()) {
//            runChecksUnorderedRandomAccess((Collection<T>)item, checker);
//        } else {
//            runChecksUnorderedSequentialAccess(item, checker);
//        }
//    }
//
//    private void runChecksUnorderedRandomAccess(Collection<T> item, ExecutedCompositeCheckBuilder checker) {
//        checker.createCompositeCheck("!size!", item.size(), ExtractionStatus.NORMAL, null).runMatchers(sizeMatchers);
//        for (Map.Entry<Integer, Collection<? super Matcher<? super T>>> entry : indexToItemMatchers.entrySet()) {
//            checker.createCompositeCheck(String.valueOf(entry.getKey()), item(), ExtractionStatus.NORMAL, null).runMatchers(sizeMatchers);
//        }
//    }
//
//    private void runChecksUnorderedSequentialAccess(Iterable<T> item, ExecutedCompositeCheckBuilder checker) {
//
//    }


    private Collection<? super Matcher<? super T>> getOrCreateEntryForIndex(int index) {
        Collection<? super Matcher<? super T>> result = indexToItemMatchers.get(index);
        if (result == null) {
            result = new ArrayList<>();
            indexToItemMatchers.put(index, result);
        }
        return result;
    }

    private Collection<? super Matcher<? super T>> getOrCreateEntryForIndexMatcher(Matcher<Integer> indexMatcher) {
        Collection<? super Matcher<? super T>> result = indexMatcherToItemMatchers.get(indexMatcher);
        if (result == null) {
            result = new ArrayList<>();
            indexMatcherToItemMatchers.put(indexMatcher, result);
        }
        return result;
    }


    public enum Type {
        ORDERED,
        UNORDERED,
    }
}
