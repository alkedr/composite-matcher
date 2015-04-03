package com.github.alkedr.matchers.reporting;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PlanningMatcher<T> extends ReportingMatcher<T> {
    PlanningMatcher<T> addPlannedCheck(PlannedCheck plannedCheck);

    interface PlannedCheck<T> {
        boolean matches(@Nullable Object item, @NotNull CheckListener listener);
    }
}
