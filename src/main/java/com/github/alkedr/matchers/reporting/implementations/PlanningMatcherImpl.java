package com.github.alkedr.matchers.reporting.implementations;

import com.github.alkedr.matchers.reporting.PlanningMatcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PlanningMatcherImpl<T, U extends PlanningMatcherImpl<T, U>> extends ReportingMatcherImpl<T, U> implements PlanningMatcher<T> {
    private final Collection<PlannedCheck> plannedChecks = new ArrayList<>();

    @Override
    public U addPlannedCheck(PlannedCheck plannedCheck) {
        new AbstractMap() {
            @Override
            public Set<Entry> entrySet() {
                return new AbstractSet<Entry>() {
                    @Override
                    public Iterator<Entry> iterator() {
                        return null;
                    }

                    @Override
                    public int size() {
                        return 0;
                    }
                };
            }
        }
        plannedChecks.add(plannedCheck);
        return (U) this;
    }

    @Override
    public boolean matches(@Nullable Object item, @NotNull CheckListener listener) {
        boolean result = true;
        for (PlannedCheck plannedCheck : plannedChecks) {
            result &= plannedCheck.matches(item, listener);
        }
        return result;
    }
}
