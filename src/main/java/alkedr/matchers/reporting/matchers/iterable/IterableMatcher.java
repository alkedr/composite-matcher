package alkedr.matchers.reporting.matchers.iterable;

import alkedr.matchers.reporting.TypeSafeReportingMatcher;
import alkedr.matchers.reporting.checks.CheckExecutor;
import alkedr.matchers.reporting.checks.ExecutedCompositeCheck;
import alkedr.matchers.reporting.checks.ExtractedValue;
import org.hamcrest.Matcher;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static ch.lambdaj.Lambda.filter;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.StringDescription.asString;

/**
 * User: alkedr
 * Date: 22.12.2014
 */
public class IterableMatcher<T> extends TypeSafeReportingMatcher<Iterable<T>> {
    private final List<Matcher<? super T>> elementMatchers = new ArrayList<>();
    private final List<Matcher<? super T>> matchersForEveryElement = new ArrayList<>();
    private final Map<Matcher<? super T>, List<Matcher<? super Iterable<T>>>> sublistSelectorToMatcher = new LinkedHashMap<>();

    public IterableMatcher() {
        super(Iterable.class);
    }


    public IterableMatcher<T> hasItem(T value) {
        return hasItem(equalTo(value));
    }

    public IterableMatcher<T> hasItem(Matcher<? super T> matcher) {
        elementMatchers.add(matcher);
        return this;
    }

    public IterableMatcher<T> allItemsAre(Matcher<? super T> matcher) {
        matchersForEveryElement.add(matcher);
        return this;
    }

    public ItemsThatAreChecksAdder itemsThatAre(Matcher<? super T> selector) {
        return new ItemsThatAreChecksAdder(selector);
    }


    public IterableMatcher<T> select(Matcher<? super T> selector, Matcher<? super Iterable<T>> matcher) {
        List<Matcher<? super Iterable<T>>> list = sublistSelectorToMatcher.get(selector);
        if (list == null) {
            list = new ArrayList<>();
            sublistSelectorToMatcher.put(selector, list);
        }
        list.add(matcher);
        return this;
    }




    public class ItemsThatAreChecksAdder {
        private final Matcher<? super T> selector;

        public ItemsThatAreChecksAdder(Matcher<? super T> selector) {
            this.selector = selector;
        }

        public IterableMatcher<T> alsoAre(Matcher<? super T> matcher) {
            return select(selector, new IterableMatcher<T>().hasItem(matcher));
        }
    }




    @Override
    public ExecutedCompositeCheck getReportSafely(@Nullable Iterable<T> item) {
        CheckExecutor<T> executor = new CheckExecutor<>(new ExtractedValue("", item));

        List<Matcher<? super T>> remainingMatchers = new LinkedList<>(elementMatchers);
        if (item != null) {
            for (Map.Entry<Matcher<? super T>, List<Matcher<? super Iterable<T>>>> entry : sublistSelectorToMatcher.entrySet()) {
                CheckExecutor<Iterable<T>> checkExecutorForFiltered = new CheckExecutor<>(new ExtractedValue(asString(entry.getKey()), filter(entry.getKey(), item)));
                for (Matcher<? super Iterable<T>> matcher : entry.getValue()) {
                    checkExecutorForFiltered.checkThat(matcher);
                }
                executor.addCompositeCheck(checkExecutorForFiltered.buildCompositeCheck());
            }

            int i = 0;
            for (T element : item) {
                CheckExecutor<T> checkExecutorForElement = new CheckExecutor<>(new ExtractedValue(String.valueOf(i), element));
                for (Matcher<? super T> matcher : matchersForEveryElement) {
                    checkExecutorForElement.checkThat(matcher);
                }
                Iterator<Matcher<? super T>> remainingMatchersIterator = remainingMatchers.iterator();
                while (remainingMatchersIterator.hasNext()) {
                    Matcher<? super T> elementMatcher = remainingMatchersIterator.next();
                    if (checkExecutorForElement.checkAndReportIfMatches(elementMatcher)) {
                        remainingMatchersIterator.remove();
                    }
                }
                i++;
                executor.addCompositeCheck(checkExecutorForElement.buildCompositeCheck());
            }
        }
        for (Matcher<? super T> remainingMatcher : remainingMatchers) {
            executor.addCompositeCheck(new CheckExecutor<>(new ExtractedValue(asString(remainingMatcher), null, ExtractedValue.Status.MISSING)).buildCompositeCheck());
        }

        return executor.buildCompositeCheck();
    }


//
//
//    public SubiterableCheckAdder elements(Matcher<? super T> selector) {
//    }
//
//
//    public ElementCheckAdder has() {
//        return has(1);
//    }
//
//    public ElementCheckAdder has(int count) {
//        return has(equalTo(count));
//    }
//
//    public ElementCheckAdder hasAtLeast(int count) {
//        return has(greaterThanOrEqualTo(count));
//    }
//
//    public ElementCheckAdder hasNoMoreThan(int count) {
//        return has(lessThanOrEqualTo(count));
//    }
//
//    public ElementCheckAdder has(Matcher<? super Integer> countMatcher) {
//        return new ElementCheckAdder(countMatcher);
//    }
//
//
//
//    public class SubiterableCheckAdder {
//
//        public IterableMatcher<T> are(Matcher<? super List<T>> matcher) {
//        }
//    }
//
//
//    public class ElementCheckAdder {
//        private final Matcher<? super Integer> countMatcher;
//
//        public ElementCheckAdder(Matcher<? super Integer> countMatcher) {
//            this.countMatcher = countMatcher;
//        }
//
//        public IterableMatcher<T> element(Matcher<? super T> matcher) {
//        }
//
//        public IterableMatcher<T> elementEqualTo(T value) {
//            return element(equalTo(value));
//        }
//
//
//        public IterableMatcher<T> elements(Matcher<? super T> matcher) {
//            return element(matcher);
//        }
//
//        public IterableMatcher<T> elementsEqualTo(T value) {
//            return elementEqualTo(value);
//        }
//    }
}
