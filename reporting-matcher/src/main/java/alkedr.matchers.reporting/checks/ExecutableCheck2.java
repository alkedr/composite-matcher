package alkedr.matchers.reporting.checks;

import org.hamcrest.Matcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static java.util.Collections.unmodifiableCollection;

public class ExecutableCheck2 {
    private final Collection<NamedValue> values;
    private final Collection<Matcher<?>> matchers;

    public ExecutableCheck2(Collection<NamedValue> values, Collection<? extends Matcher<?>> matchers) {
        this.values = new ArrayList<>(values);
        this.matchers = new ArrayList<>(matchers);
    }

    public void addMatchersFrom(ExecutableCheck2 other) {
        matchers.addAll(other.matchers);
    }

    public Collection<NamedValue> getValues() {
        return unmodifiableCollection(values);
    }

    public Collection<Matcher<?>> getMatchers() {
        return unmodifiableCollection(matchers);
    }

    public static class NamedValue {
        private final String name;
        private final Object value;

        public NamedValue(String name, Object value) {
            this.name = name;
            this.value = value;
        }
    }
}


// несколько элементов, несколько груп матчеров
// каждый элемент соответствует группе матчеров
// группа матчеров - ReportingMatcher с несколькими проверками, поэтому считаем, что матчер один

// двудольный граф, одна доля - NamedValue, вторая - Matcher
// ReportingMatcher - несколько таких двудольных графов
// Одни и те же значения могут быть в нескольких графах