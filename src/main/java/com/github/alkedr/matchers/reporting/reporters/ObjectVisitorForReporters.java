package com.github.alkedr.matchers.reporting.reporters;

import java.util.Map;

/**
 * User: alkedr
 * Date: 26.12.2014
 */
public abstract class ObjectVisitorForReporters {
    protected abstract void onObject(Object object, ObjectVisitor2 ctx);
    protected abstract void onMap(Map<Object, Object> map, ObjectVisitor2 ctx);
    protected abstract void onArray(Object[] array, ObjectVisitor2 ctx);
    protected abstract void onCollection(Object object, ObjectVisitor2 ctx);

    protected abstract void onKey(String key);
    protected abstract void onPrimitiveValue(Object value);
}
