package alkedr.matchers.reporting.plan;

import org.jetbrains.annotations.Nullable;

public interface NamedValueExtractor<T, U> extends ValueExtractor<T, U> {
    @Nullable
    String getName(); // Вызывать можно только после setItem()
}
