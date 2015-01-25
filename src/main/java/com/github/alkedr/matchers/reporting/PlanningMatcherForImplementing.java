package com.github.alkedr.matchers.reporting;

import com.github.alkedr.matchers.reporting.impl.ReportingMatcherImpl;

public interface PlanningMatcherForImplementing<T, U extends PlanningMatcherForImplementing<T, U>> extends ReportingMatcher<T> {
    U addPlannedCheck(PlannedCheck<T> plannedCheck);

    interface PlannedCheck<T> {
        void execute(T item, ReportingMatcherImpl.ExecutedCompositeCheckBuilder checker);
    }
}
