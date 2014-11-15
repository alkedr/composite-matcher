package alkedr.matchers.reporting.plan;

import org.jetbrains.annotations.Nullable;

public interface ValueExtractor<T, U> {
    void setItem(@Nullable T item);
    @Nullable U getValue(); // Вызывать можно только после setItem()
}
