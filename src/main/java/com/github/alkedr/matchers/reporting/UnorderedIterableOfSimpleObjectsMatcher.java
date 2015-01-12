package com.github.alkedr.matchers.reporting;

import com.github.alkedr.matchers.reporting.checks.CheckExecutor;
import com.github.alkedr.matchers.reporting.checks.ExecutedCompositeCheck;
import com.github.alkedr.matchers.reporting.checks.ExtractedValue;
import org.hamcrest.Matcher;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Runtime.getRuntime;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.StringDescription.asString;

public class UnorderedIterableOfSimpleObjectsMatcher<T> extends ReportingMatcher<Iterable<T>> {
    private final List<PlannedCheck<T>> plannedChecks = new ArrayList<>();


    public UnorderedIterableOfSimpleObjectsMatcher<T> item(T value) {
        return items(value, equalTo(1));
    }

    public UnorderedIterableOfSimpleObjectsMatcher<T> item(Matcher<T> valueMatcher) {
        return items(valueMatcher, equalTo(1));
    }

    public UnorderedIterableOfSimpleObjectsMatcher<T> items(T value, int count) {
        return items(equalTo(value), equalTo(count));
    }

    public UnorderedIterableOfSimpleObjectsMatcher<T> items(Matcher<T> valueMatcher, int count) {
        return items(valueMatcher, equalTo(count));
    }

    public UnorderedIterableOfSimpleObjectsMatcher<T> items(T value, Matcher<Integer> countMatcher) {
        return items(equalTo(value), countMatcher);
    }

    public UnorderedIterableOfSimpleObjectsMatcher<T> items(Matcher<T> valueMatcher, Matcher<Integer> countMatcher) {
        plannedChecks.add(new PlannedCheck<T>(valueMatcher, countMatcher));
        return this;
    }


    @Override
    public ExecutedCompositeCheck getReportSafely(@Nullable Iterable<T> iterable) {
        System.out.println((getRuntime().totalMemory() - getRuntime().freeMemory()) / 1024 / 1024);

        List<CheckInProgress<T>> checksInProgress = new ArrayList<>();
        for (PlannedCheck<T> plannedCheck : plannedChecks) {
            checksInProgress.add(new CheckInProgress<>(plannedCheck));
        }

        if (iterable != null) {
            int i = 0;
            for (T item : iterable) {
                CheckExecutor<T> executorForItem = new CheckExecutor<>(new ExtractedValue(String.valueOf(i), item));
                for (CheckInProgress<T> checkInProgress : checksInProgress) {
                    if (executorForItem.checkAndReportIfMatches(checkInProgress.valueMatcher)) {
                        checkInProgress.checks.add(executorForItem.buildCompositeCheck());
                        break;
                    }
                }
                i++;
            }
        }

        System.out.println((getRuntime().totalMemory() - getRuntime().freeMemory()) / 1024 / 1024);

        CheckExecutor<T> executor = new CheckExecutor<>(new ExtractedValue("", iterable));
        for (CheckInProgress<T> checkInProgress : checksInProgress) {
            CheckExecutor<T> executorForGroup = new CheckExecutor<>(new ExtractedValue(asString(checkInProgress.valueMatcher), null));
            CheckExecutor<Integer> executorForCount = new CheckExecutor<>(new ExtractedValue("|count|", checkInProgress.checks.size()));
            executorForCount.checkThat(checkInProgress.countMatcher);
            executorForGroup.addCompositeCheck(executorForCount.buildCompositeCheck());
            for (ExecutedCompositeCheck check : checkInProgress.checks) {
                executorForGroup.addCompositeCheck(check);
            }
            executor.addCompositeCheck(executorForGroup.buildCompositeCheck());
        }
        System.out.println((getRuntime().totalMemory() - getRuntime().freeMemory()) / 1024 / 1024);
        return executor.buildCompositeCheck();
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
        private final List<ExecutedCompositeCheck> checks = new ArrayList<>();

        private CheckInProgress(PlannedCheck<T> plannedCheck) {
            this.valueMatcher = plannedCheck.valueMatcher;
            this.countMatcher = plannedCheck.countMatcher;
        }
    }
}
