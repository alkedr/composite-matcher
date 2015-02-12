package com.github.alkedr.matchers.reporting;

import org.jetbrains.annotations.NotNull;

public class PlanningMatcher<T> extends PlanningMatcherForExtending<T, PlanningMatcher<T>> {
    public PlanningMatcher() {
    }

    public PlanningMatcher(@NotNull Class<T> tClass) {
        super(tClass);
    }
}
