package alkedr.matchers.reporting.matchers.iterable;

import alkedr.matchers.reporting.TypeSafeReportingMatcher;
import alkedr.matchers.reporting.checks.ExecutedCompositeCheck;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static ch.lambdaj.Lambda.filter;
import static org.hamcrest.Matchers.equalTo;

/**
 * User: alkedr
 * Date: 22.12.2014
 */
public class IterableMatcher<T> extends TypeSafeReportingMatcher<Iterable<T>> {
    private final List<Matcher<? super T>> elementMatchers = new ArrayList<>();
    private final List<Matcher<? super T>> matchersForEveryElement = new ArrayList<>();
    private final Map<Matcher<? super T>, List<Matcher<? super List<T>>>> sublistSelectorToMatcher = new LinkedHashMap<>();


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


    public IterableMatcher<T> select(Matcher<? super T> selector, Matcher<? super List<T>> matcher) {
        List<Matcher<? super List<T>>> list = sublistSelectorToMatcher.get(selector);
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
        List<Matcher<? super T>> remainingMatchers = new LinkedList<>(elementMatchers);
        ExecutedCompositeCheck result = new ExecutedCompositeCheck(item);
        if (item != null) {
            for (Map.Entry<Matcher<? super T>, List<Matcher<? super List<T>>>> entry : sublistSelectorToMatcher.entrySet()) {
                Iterable<T> filtered = filter(entry.getKey(), item);
                for (Matcher<? super List<T>> matcher : entry.getValue()) {
                    result.checkThat(StringDescription.toString(entry.getKey()), filtered, matcher);
                }
            }

            int i = 0;
            for (T element : item) {
                for (Matcher<? super T> matcher : matchersForEveryElement) {
                    result.checkThat(String.valueOf(i), element, matcher);
                }
                Iterator<Matcher<? super T>> remainingMatchersIterator = remainingMatchers.iterator();
                while (remainingMatchersIterator.hasNext()) {
                    Matcher<? super T> elementMatcher = remainingMatchersIterator.next();
                    if (result.checkAndReportIfMatches(String.valueOf(i), element, elementMatcher)) {
                        remainingMatchersIterator.remove();
                    }
                }
                i++;
            }
        }
        for (Matcher<? super T> remainingMatcher : remainingMatchers) {
            result.reportMissingValue(StringDescription.asString(remainingMatcher));
        }
        ExecutedCompositeCheck.INNER_CHECK_RESULT.set(result);  //FIXME
        return result;
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
