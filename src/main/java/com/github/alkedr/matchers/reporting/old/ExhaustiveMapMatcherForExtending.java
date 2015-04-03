package com.github.alkedr.matchers.reporting.old;

import org.hamcrest.Matcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;

// TODO: subclass CollectionMatcher?
public class ExhaustiveMapMatcherForExtending<Key, Value, U extends ExhaustiveMapMatcherForExtending<Key, Value, U>> extends ValueExtractingMatcherForExtending<Map<Key, Value>, U> {
    private boolean allEntriesMustBeChecked = false;

    public ExhaustiveMapMatcherForExtending(@NotNull Class<?> tClass) {
        super(tClass);
    }

    /*
    if (allEntriesMustBeChecked) {
        идём по entrySet, достаём оттуда значения по очереди, ищем для них матчеры
    } else {
        как любой другой PlanningMatcher
    }
     */

    public U allEntriesMustBeChecked() {
        return allEntriesMustBeChecked(true);
    }

    public U someEntriesMayNotBeChecked() {
        return allEntriesMustBeChecked(false);
    }

    public U allEntriesMustBeChecked(boolean value) {
        allEntriesMustBeChecked = value;
        return (U) this;
    }


    public U size(int value) {
        return sizeImpl(equalTo(value));
    }

    public U size(Matcher<? super Integer>... matchers) {
        return sizeImpl(matchers);
    }

    public U size(List<? extends Matcher<? super Integer>> matchers) {
        return sizeImpl(matchers);
    }


    public U size(String name, int value) {
        return sizeImpl(name, equalTo(value));
    }

    public U size(String name, Matcher<? super Integer>... matchers) {
        return sizeImpl(name, matchers);
    }

    public U size(String name, List<? extends Matcher<? super Integer>> matchers) {
        return sizeImpl(name, matchers);
    }


    public U entry(Key key, Value value) {
        return entryImpl(key, equalTo(value));
    }

    public U entry(Key key, Matcher<? super Key>... valueMatchers) {
        return entryImpl(key, equalTo(valueMatchers));
    }

    public U entry(Key key, List<? extends Matcher<? super Key>> valueMatchers) {
        return entryImpl(key, equalTo(valueMatchers));
    }


    public U entry(String name, Key key, Value value) {
        return entryImpl(name, key, equalTo(value));
    }

    public U entry(String name, Key key, Matcher<? super Key>... valueMatchers) {
        return entryImpl(name, key, equalTo(valueMatchers));
    }

    public U entry(String name, Key key, List<? extends Matcher<? super Key>> valueMatchers) {
        return entryImpl(name, key, equalTo(valueMatchers));
    }



    private U sizeImpl(Object matchers) {
        return sizeImpl("size", matchers);
    }

    private U sizeImpl(String name, Object matchers) {
        return addPlannedCheck(new ValueExtractingPlannedCheck<Map<Key, Value>>(name, matchers) {
            @Override
            public Integer getValue(@NotNull Map<Key, Value> item) {
                return item.size();
            }
        });
    }

    private U entryImpl(Key key, Object valueMatchers) {
        return entryImpl(String.valueOf(key), key, valueMatchers);
    }

    private U entryImpl(String name, final Key key, final Object valueMatchers) {
        return addPlannedCheck(new PlannedCheck<Map<Key, Value>>() {
            @Override
            public void execute(@NotNull Class<?> itemClass, @Nullable Map<Key, Value> item, @NotNull ExecutedCompositeCheckBuilder checker) {
                ExecutedCompositeCheckBuilder subcheck = checker.subcheck().name(String.valueOf(key));
                try {
                    if (item != null && item.containsKey(key)) {
                        subcheck.value(item.get(key));
                    } else {
                        subcheck.extractionStatus(ExecutedCompositeCheck.ExtractionStatus.MISSING);
                    }
                } catch (Exception e) {
                    subcheck.extractionStatus(ExecutedCompositeCheck.ExtractionStatus.BROKEN).extractionException(e);
                }
                subcheck.runMatchersObject(valueMatchers);
            }
        });
    }
}
