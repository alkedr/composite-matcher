package com.github.alkedr.matchers.reporting.old.reporters;

public class ObjectVisitor3 {
    public interface Callback {
        void onObject(String key, Object value, Callback callback);
    }
}
