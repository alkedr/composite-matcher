package com.github.alkedr.matchers.reporting.old;

import com.github.alkedr.matchers.reporting.old.PlanningMatcherForExtending.PlannedCheck;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.NoSuchElementException;

// Слежение за тем, чтобы не было дубликатов - задача клиента
// Расшаривание списков матчеров между PlannedCheck'ами - задача клиента
public class PlanningMatcherForExtending<T, U extends PlanningMatcherForExtending<T, U>> extends ReportingMatcher<T> implements Iterable<PlannedCheck<T>> {
    @Nullable private PlannedCheck<T> head = null;
    @Nullable private PlannedCheck<T> tail = null;


//    public PlanningMatcherForExtending() {
//    }

    public PlanningMatcherForExtending(@NotNull Class<?> tClass) {
        super(tClass);
    }


    public U addPlannedCheck(PlannedCheck<T> plannedCheck) {
        if (head == null || tail == null) head = plannedCheck; else tail.next = plannedCheck;
        tail = plannedCheck;
        tail.next = null;
        return (U) this;
    }


    @Override
    public Iterator<PlannedCheck<T>> iterator() {
        return new PlannedCheckIterator<>(head);
    }


    @Override
    public void runChecks(@NotNull Class<?> itemClass, @Nullable T item, ExecutedCompositeCheckBuilder checker) {
        for (PlannedCheck<T> plannedCheck : this) plannedCheck.execute(itemClass, item, checker);
    }

    public abstract static class PlannedCheck<T> {
        @Nullable PlannedCheck<T> next = null;

        public abstract void execute(@NotNull Class<?> itemClass, @Nullable T item, @NotNull ExecutedCompositeCheckBuilder checker);
    }



    private static class PlannedCheckIterator<T> implements Iterator<PlannedCheck<T>> {
        private PlannedCheck<T> next;

        PlannedCheckIterator(PlannedCheck<T> head) {
            this.next = head;
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public PlannedCheck<T> next() {
            if (!hasNext()) throw new NoSuchElementException("hasNext() returned false but next() was called anyway");
            PlannedCheck<T> result = next;
            next = next.next;
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove");
        }
    }
}
