package com.github.alkedr.matchers.reporting.old.reporters;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * User: alkedr
 * Date: 26.12.2014
 */
public class ObjectVisitor2 {
    public abstract static class ValueHandler<T> {
        public void onObjectUnsafe(@Nullable String key, @Nullable Object value, @NotNull ObjectVisitor2 ctx) {
            onObject(key, (T) value, ctx);
        }

        public abstract void onObject(@Nullable String key, @Nullable Object value, @NotNull ObjectVisitor2 ctx);
    }

    private final Map<Class<?>, ValueHandler<?>> classToValueHandler = new HashMap<>();
    private ValueHandler<Object> defaultValueHandler = null;

    public void visit(Object object) {
        ValueHandler<?> enumerator = classToValueHandler.get(object.getClass());
        if (enumerator == null) enumerator = defaultValueHandler;
        enumerator.onObjectUnsafe(null, object, this);
    }

    public <T> ObjectVisitor2 valueHandler(Class<T> tClass, ValueHandler<? extends T> valueHandler) {
        classToValueHandler.put(tClass, valueHandler);
        return this;
    }

    public ObjectVisitor2 defaultValueHandler(ValueHandler<Object> valueHandler) {
        defaultValueHandler = valueHandler;
        return this;
    }




    /*
    private final Map<Class<?>, ValueHandler<?>> classToValuesEnumerator = new HashMap<>();
    private ValueHandler<Object> defaultValueHandler = null;


    public <T> ObjectVisitor2 valuesEnumerator(Class<T> tClass, ValueHandler<? extends T> valueHandler) {
        classToValuesEnumerator.put(tClass, valueHandler);
        return this;
    }

    public ObjectVisitor2 defaultValuesEnumerator(ValueHandler<Object> valueHandler) {
        defaultValueHandler = valueHandler;
        return this;
    }


    public void visit(Object object) {
        ValueHandler<?> enumerator = classToValuesEnumerator.get(object.getClass());
        if (enumerator == null) enumerator = defaultValueHandler;
        enumerator.onValueUnsafe(object, this);
    }


    public abstract static class ValueHandler<T> {
        public void onValueUnsafe(Object object, ObjectVisitor2 context) {
            onValue((T)object, context);
        }

        public abstract void onValue(T t, ObjectVisitor2 context);
    }*/



//    public static ObjectVisitor2 objectVisitorForReporters(ValueHandler<Object> primitiveValueHandler, ValueHandler<Object> defaultValueHandler) {
//        ValueHandler<Object> primitiveValueHandler = new ValueHandler<Object>() {
//            @Override
//            public void onValue(Object o, ObjectVisitor2 context) {
//
//            }
//        };
//
//
//        return new ObjectVisitor2()
//                .valuesEnumerator(Boolean.class, defaultPrimitiveValueEnumerator(Boolean.class))
//                .valuesEnumerator(Character.class, defaultPrimitiveValueEnumerator(Character.class))
//                .valuesEnumerator(Byte.class, defaultPrimitiveValueEnumerator(Byte.class))
//                .valuesEnumerator(Short.class, defaultPrimitiveValueEnumerator(Short.class))
//                .valuesEnumerator(Integer.class, defaultPrimitiveValueEnumerator(Integer.class))
//                .valuesEnumerator(Long.class, defaultPrimitiveValueEnumerator(Long.class))
//                .valuesEnumerator(Float.class, defaultPrimitiveValueEnumerator(Float.class))
//                .valuesEnumerator(Double.class, defaultPrimitiveValueEnumerator(Double.class))
//                .valuesEnumerator(String.class, defaultPrimitiveValueEnumerator(String.class))
//
//                .valuesEnumerator(Map.class, defaultMapValuesEnumerator())
//                .valuesEnumerator(Collection.class, defaultCollectionValuesEnumerator())
//
//                .defaultValuesEnumerator(defaultObjectEnumerator())
//                .valuesEnumerator(Map.class, )
//                ;
//    }
}
