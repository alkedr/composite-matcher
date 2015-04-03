package com.github.alkedr.matchers.reporting.old.impl;

import com.github.alkedr.matchers.reporting.old.ReportingMatcher;
import org.hamcrest.Matcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.StringDescription.asString;

// добавить возможность настройки отображения элементов (номер, item.toString, и т. п.)
public class ClassifyingMatcher<T> extends ReportingMatcher<Iterable<T>> {
    private final List<PlannedCheck<T>> plannedChecks = new ArrayList<>();

    protected ClassifyingMatcher(@NotNull Class<?> actualItemClass) {
        super(actualItemClass);
    }


    public ClassifyingMatcher<T> item(T value) {
        return items(value, equalTo(1));
    }

    public ClassifyingMatcher<T> item(Matcher<T> valueMatcher) {
        return items(valueMatcher, equalTo(1));
    }

    public ClassifyingMatcher<T> items(T value, int count) {
        return items(equalTo(value), equalTo(count));
    }

    public ClassifyingMatcher<T> items(Matcher<T> valueMatcher, int count) {
        return items(valueMatcher, equalTo(count));
    }

    public ClassifyingMatcher<T> items(T value, Matcher<Integer> countMatcher) {
        return items(equalTo(value), countMatcher);
    }

    public ClassifyingMatcher<T> items(Matcher<T> valueMatcher, Matcher<Integer> countMatcher) {
        plannedChecks.add(new PlannedCheck<T>(valueMatcher, countMatcher));
        return this;
    }

//    @Override
    public void runChecks(@Nullable Iterable<T> iterable, ExecutedCompositeCheckBuilder checker) {
        Collection<CheckInProgress<T>> checksInProgress = new ArrayList<>();
        for (PlannedCheck<T> plannedCheck : plannedChecks) {
            checksInProgress.add(new CheckInProgress<>(plannedCheck));
        }

        if (iterable != null) {
            for (T item : iterable) {
                for (CheckInProgress<T> checkInProgress : checksInProgress) {
                    if (checkInProgress.valueMatcher.matches(item)) {
                        checkInProgress.matchedItems.add(item);
                        break;
                    }
                }
            }
        }

        for (CheckInProgress<T> checkInProgress : checksInProgress) {
            ExecutedCompositeCheckBuilder checkerForGroup = checker.subcheck().name(asString(checkInProgress.valueMatcher)).value(null);
            checkerForGroup.subcheck().name("count").value(checkInProgress.matchedItems.size()).runMatcher(checkInProgress.countMatcher);
            for (Object matchedItem : checkInProgress.matchedItems) {
                checkerForGroup.subcheck().value(matchedItem);
            }
        }
    }

    @Override
    protected void runChecks(@NotNull Class<?> itemClass, @Nullable Iterable<T> item, ExecutedCompositeCheckBuilder checker) {

    }


    private static class PlannedCheck<T> {
        private final Matcher<T> valueMatcher;
        private final Matcher<Integer> countMatcher;

        private PlannedCheck(Matcher<T> valueMatcher, Matcher<Integer> countMatcher) {
            this.valueMatcher = valueMatcher;
            this.countMatcher = countMatcher;
        }
    }

    private static class CheckInProgress<T> {
        private final Matcher<T> valueMatcher;
        private final Matcher<Integer> countMatcher;
        private final List<Object> matchedItems = new ArrayList<>();

        private CheckInProgress(PlannedCheck<T> plannedCheck) {
            this.valueMatcher = plannedCheck.valueMatcher;
            this.countMatcher = plannedCheck.countMatcher;
        }
    }
}
