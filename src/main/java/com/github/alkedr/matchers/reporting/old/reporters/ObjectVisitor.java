package com.github.alkedr.matchers.reporting.old.reporters;

import java.util.Collection;
import java.util.Map;

/**
 * User: alkedr
 * Date: 23.12.2014
 */
public abstract class ObjectVisitor {
    private ValuesEnumerator<Object> objectValuesEnumerator = null;
    private ValuesEnumerator<Map<Object, Object>> mapValuesEnumerator = null;

    protected abstract void onObjectBegin();
    protected abstract void onObjectEnd();
    protected abstract void onMapBegin();
    protected abstract void onMapEnd();
    protected abstract void onArrayBegin();
    protected abstract void onArrayEnd();
    protected abstract void onKey(String key);
    protected abstract void onPrimitiveValue(Object value);

    public ObjectVisitor objectValuesEnumerator(ValuesEnumerator<Object> objectValuesEnumerator) {
        this.objectValuesEnumerator = objectValuesEnumerator;
        return this;
    }

    public ObjectVisitor mapValuesEnumerator(ValuesEnumerator<Map<Object, Object>> mapValuesEnumerator) {
        this.mapValuesEnumerator = mapValuesEnumerator;
        return this;
    }

    public void accept(Object object) {
        ValuesEnumerator.Functor functor = new ValuesEnumerator.Functor() {
            @Override
            public void call(String key, Object value) {
                onKey(key);
                accept(value);
            }
        };

        if (object instanceof Map) {
            onMapBegin();
            mapValuesEnumerator.enumerateValues((Map<Object, Object>) object, functor);
            onMapEnd();
        } else if (object instanceof Collection) {
            onArrayBegin();
            int i = 0;
            for (Object value : (Iterable<Object>) object) {
                onKey(String.valueOf(i++));
                accept(value);
            }
            onArrayEnd();
        } else if (object instanceof Object[]) {   // TODO: проверить, что это ловит все массивы
            onArrayBegin();
            int i = 0;
            for (Object value : (Object[]) object) {
                onKey(String.valueOf(i++));
                accept(value);
            }
            onArrayEnd();
        } else if (object instanceof Boolean || object instanceof Character || object instanceof Byte
                || object instanceof Short || object instanceof Integer || object instanceof Long
                || object instanceof Float || object instanceof Double || object instanceof String) {
            onPrimitiveValue(object);
        } else {
            onObjectBegin();
            objectValuesEnumerator.enumerateValues(object, functor);
            onObjectEnd();
        }
    }
}
