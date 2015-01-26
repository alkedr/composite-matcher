package com.github.alkedr.matchers.reporting;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

public class PlanningMatcherForExtending<T, U extends PlanningMatcherForExtending<T, U>> extends ReportingMatcher<T> {
    private final Collection<PlannedCheck<T>> plannedChecks = new ArrayList<>();

    public U addPlannedCheck(PlannedCheck<T> plannedCheck) {
        plannedChecks.add(plannedCheck);
        return (U) this;
    }

    @Override
    public void runChecks(@Nullable T item, ExecutedCompositeCheckBuilder checker) {
        for (PlannedCheck<T> plannedCheck : plannedChecks) {
            plannedCheck.execute(item == null ? getActualItemClass() : item.getClass(), item, checker);
        }
    }

    public interface PlannedCheck<T> {
        void execute(@NotNull Class<?> itemClass, @Nullable T item, @NotNull ExecutedCompositeCheckBuilder checker);
    }
}
