package com.github.alkedr.matchers.reporting.checks;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.github.alkedr.matchers.reporting.checks.ExtractedValue.Status.NORMAL;

/**
 * User: alkedr
 * Date: 29.12.2014
 */
public class ExtractedValue {
    @NotNull private final String name;
    @Nullable private final Object value;
    @NotNull private final Status status;
    @Nullable private final Throwable throwable;

    public ExtractedValue(@NotNull String name, @Nullable Object value, @NotNull Status status, @Nullable Throwable throwable) {
        this.name = name;
        this.value = value;
        this.status = status;
        this.throwable = throwable;
    }

    public ExtractedValue(@NotNull String name, @Nullable Object value, @NotNull Status status) {
        this(name, value, status, null);
    }

    public ExtractedValue(@NotNull String name, @Nullable Object value) {
        this(name, value, NORMAL);
    }

    @NotNull
    public String getName() {
        return name;
    }

    @Nullable
    public Object getValue() {
        return value;
    }

    @NotNull
    public Status getStatus() {
        return status;
    }

    @Nullable
    public Throwable getThrowable() {
        return throwable;
    }


    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof ExtractedValue)) return false;
        ExtractedValue that = (ExtractedValue) object;
        if (!name.equals(that.name)) return false;
        if (status != that.status) return false;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + status.hashCode();
        return result;
    }


    public enum Status {
        NORMAL,
        MISSING,
        UNEXPECTED,
        ERROR,
        ;
    }
}
