package com.github.alkedr.matchers.reporting.old.reporters;

public interface ValuesEnumerator<T> {
    void enumerateValues(T t, Functor functor);

    interface Functor {
        void call(String key, Object value);
    }
}
