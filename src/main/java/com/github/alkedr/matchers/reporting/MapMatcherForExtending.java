package com.github.alkedr.matchers.reporting;

import org.hamcrest.Matcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.github.alkedr.matchers.reporting.ReportingMatcher.ExecutedCompositeCheck.ExtractionStatus.BROKEN;
import static com.github.alkedr.matchers.reporting.ReportingMatcher.ExecutedCompositeCheck.ExtractionStatus.MISSING;
import static org.hamcrest.Matchers.equalTo;

// TODO: возможность считать ошибкой лишние значения
public class MapMatcherForExtending<Key, Value, U extends MapMatcherForExtending<Key, Value, U>> extends ValueExtractingMatcherForExtending<Map<Key, Value>, U> {
    private boolean allEntriesMustBeChecked = false;

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


    public U keySet(Matcher<? super Set<Key>>... matchers) {
        return keySetImpl(matchers);
    }

    public U keySet(List<? extends Matcher<? super Set<Key>>> matchers) {
        return keySetImpl(matchers);
    }

    public U keySet(String name, Matcher<? super Set<Key>>... matchers) {
        return keySetImpl(name, matchers);
    }

    public U keySet(String name, List<? extends Matcher<? super Set<Key>>> matchers) {
        return keySetImpl(name, matchers);
    }


    public U values(Matcher<? super Collection<Key>>... matchers) {
        return valuesImpl(matchers);
    }

    public U values(List<? extends Matcher<? super Collection<Key>>> matchers) {
        return valuesImpl(matchers);
    }

    public U values(String name, Matcher<? super Collection<Key>>... matchers) {
        return valuesImpl(name, matchers);
    }

    public U values(String name, List<? extends Matcher<? super Collection<Key>>> matchers) {
        return valuesImpl(name, matchers);
    }


    public U entrySet(Matcher<? super Set<Map.Entry<Key, Value>>>... matchers) {
        return entrySetImpl(matchers);
    }

    public U entrySet(List<? extends Matcher<? super Set<Map.Entry<Key, Value>>>> matchers) {
        return entrySetImpl(matchers);
    }

    public U entrySet(String name, Matcher<? super Set<Map.Entry<Key, Value>>>... matchers) {
        return entrySetImpl(name, matchers);
    }

    public U entrySet(String name, List<? extends Matcher<? super Set<Map.Entry<Key, Value>>>> matchers) {
        return entrySetImpl(name, matchers);
    }



    private U sizeImpl(Object matchers) {
        return sizeImpl("size", matchers);
    }

    private U sizeImpl(String name, Object matchers) {
        return addPlannedCheck(new ValueExtractingPlannedCheck<Map<Key, Value>>(name, matchers) {
            @Override
            public Integer extract(@NotNull Map<Key, Value> item) {
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
                if (item == null) {
                    subcheck.extractionStatus(MISSING);
                } else {
                    try {
                        if (item.containsKey(key)) {
                            subcheck.value(item.get(key)).runMatchersObject(valueMatchers);
                        } else {
                            subcheck.extractionStatus(MISSING).runMatchersObject(valueMatchers);
                        }
                    } catch (Exception e) {
                        subcheck.extractionStatus(BROKEN).extractionException(e);
                    }
                }
            }
        });
    }

    private U keySetImpl(Object matchers) {
        return sizeImpl("keys", matchers);
    }

    private U keySetImpl(String name, Object matchers) {
        return addPlannedCheck(new ValueExtractingPlannedCheck<Map<Key, Value>>(name, matchers) {
            @Override
            public Set<Key> extract(@NotNull Map<Key, Value> item) {
                return item.keySet();
            }
        });
    }

    private U valuesImpl(Object matchers) {
        return sizeImpl("values", matchers);
    }

    private U valuesImpl(String name, Object matchers) {
        return addPlannedCheck(new ValueExtractingPlannedCheck<Map<Key, Value>>(name, matchers) {
            @Override
            public Collection<Value> extract(@NotNull Map<Key, Value> item) {
                return item.values();
            }
        });
    }

    private U entrySetImpl(Object matchers) {
        return sizeImpl("entries", matchers);
    }

    private U entrySetImpl(String name, Object matchers) {
        return addPlannedCheck(new ValueExtractingPlannedCheck<Map<Key, Value>>(name, matchers) {
            @Override
            public Collection<Map.Entry<Key, Value>> extract(@NotNull Map<Key, Value> item) {
                return item.entrySet();
            }
        });
    }
}
