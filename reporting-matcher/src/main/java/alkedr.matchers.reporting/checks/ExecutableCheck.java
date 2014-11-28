package alkedr.matchers.reporting.checks;

import org.hamcrest.Matcher;

import java.util.ArrayList;
import java.util.Collection;

import static java.util.Collections.*;

/**
 * Хранит информацию о проверке конкретного поля/проперти конкретного объекта
 */
public class ExecutableCheck {
    private final String name;
    private final Object value;
    private final Collection<Matcher<?>> matchers;

    public ExecutableCheck(String name, Object value, Collection<? extends Matcher<?>> matchers) {
        this.name = name;
        this.value = value;
        this.matchers = new ArrayList<>(matchers);
    }

    public void addMatchersFrom(ExecutableCheck other) {
        matchers.addAll(other.matchers);
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public Collection<Matcher<?>> getMatchers() {
        return unmodifiableCollection(matchers);
    }
}
