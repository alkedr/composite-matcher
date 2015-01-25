package com.github.alkedr.matchers.reporting.impl;

import com.github.alkedr.matchers.reporting.PlanningMatcherForImplementing;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

public class PlanningMatcherImpl<T, U extends PlanningMatcherImpl<T, U>> extends ReportingMatcherImpl<T> implements PlanningMatcherForImplementing<T, U> {
    private final Collection<PlannedCheck<T>> plannedChecks = new ArrayList<>();

    @Override
    public U addPlannedCheck(PlannedCheck<T> plannedCheck) {
        plannedChecks.add(plannedCheck);
        return (U) this;
    }

    @Override
    public void runChecks(@Nullable T item, ExecutedCompositeCheckBuilder checker) {
        for (PlannedCheck<T> plannedCheck : plannedChecks) {
            plannedCheck.execute(item, checker);
        }
    }
}
