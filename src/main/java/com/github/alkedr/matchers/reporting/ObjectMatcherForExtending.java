package com.github.alkedr.matchers.reporting;

import org.hamcrest.Matcher;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import static ch.lambdaj.Lambda.argument;
import static org.apache.commons.lang3.reflect.FieldUtils.readField;
import static org.apache.commons.lang3.reflect.MethodUtils.invokeMethod;

// TODO: поддерживать вычисление выражений, например fieldX.methodY().listFieldZ.get(1) ?
public class ObjectMatcherForExtending<T, U extends ObjectMatcherForExtending<T, U>> extends ValueExtractingMatcherForExtending<T, U> {
    public <V> ValueCheckAdder<V> field(String nameForReportAndExtraction) {
        return field(nameForReportAndExtraction, nameForReportAndExtraction);
    }

    public <V> ValueCheckAdder<V> field(String nameForReport, final String nameForExtraction) {
        return field(nameForReport, new SimpleValueExtractor<T, V>() {
            @Override
            public V extract(@NotNull T t) throws IllegalAccessException {
                return (V) readField(t, nameForExtraction, true);
            }
        });
    }

//    public <V> ValueCheckAdder<V> field(SimpleValueExtractor<T, V> fieldValueExtractor) {
//        return field(extractFieldNameFromValueExtractor(fieldValueExtractor), fieldValueExtractor);
//    }

    public <V> ValueCheckAdder<V> field(String nameForReport, SimpleValueExtractor<T, V> fieldValueExtractor) {
        return new ValueCheckAdder<>(nameForReport, fieldValueExtractor);
    }


    public <V> ValueCheckAdder<V> method(String nameForReportAndExtraction, Object... arguments) {
        return method(nameForReportAndExtraction, nameForReportAndExtraction, arguments);
    }

    public <V> ValueCheckAdder<V> method(String nameForReport, final String nameForExtraction, final Object... arguments) {
        return new ValueCheckAdder<>(nameForReport, new SimpleValueExtractor<T, V>() {
            @Override
            public V extract(@NotNull T t) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
                return (V) invokeMethod(t, nameForExtraction, arguments);
            }
        });
    }

//    public <V> ValueCheckAdder<V> method(SimpleValueExtractor<T, V> methodReturnValueExtractor) {
//        return method(extractMethodNameFromValueExtractor(methodReturnValueExtractor), methodReturnValueExtractor);
//    }

    public <V> ValueCheckAdder<V> method(String nameForReport, SimpleValueExtractor<T, V> methodReturnValueExtractor) {
        return new ValueCheckAdder<>(nameForReport, methodReturnValueExtractor);
    }

    // TODO: method(lambdajPlaceholder), как property, только в названии полное имя метода с параметрами


    public <V> ValueCheckAdder<V> property(V lambdajPlaceholder) {
        return property(argument(lambdajPlaceholder).getInkvokedPropertyName(), lambdajPlaceholder);
    }

    public <V> ValueCheckAdder<V> property(String nameForReport, final V lambdajPlaceholder) {
        return new ValueCheckAdder<>(nameForReport, new SimpleValueExtractor<T, V>() {
            @Override
            public V extract(@NotNull T t) {
                return argument(lambdajPlaceholder).evaluate(t);
            }
        });
    }


    public class ValueCheckAdder<V> {
        private final String name;
        private final ValueExtractor<T> extractor;

        private ValueCheckAdder(String name, ValueExtractor<T> extractor) {
            this.name = name;
            this.extractor = extractor;
        }

        public U is(Matcher<?>... matchers) {
            return value(name, extractor, (Matcher<? super Object>[]) matchers);
        }

        public U is(Collection<? extends Matcher<? super V>> matchers) {
            return value(name, extractor, matchers);
        }
    }


/*
    private static final ThreadLocal<Map<Class<?>, String>> SIMPLE_VALUE_EXTRACTOR_FIELD_NAME_CACHE = new ThreadLocal<>();
    private static final ThreadLocal<Map<Class<?>, String>> SIMPLE_VALUE_EXTRACTOR_METHOD_NAME_CACHE = new ThreadLocal<>();

    private <V> String extractFieldNameFromValueExtractor(SimpleValueExtractor<T, V> extractor) {
        return extractNameFromValueExtractor(extractor, new NameExtractingClassVisitor("extract", SimpleValueExtractorType.FIELD), SIMPLE_VALUE_EXTRACTOR_FIELD_NAME_CACHE);
    }

    private <V> String extractMethodNameFromValueExtractor(SimpleValueExtractor<T, V> extractor) {
        return extractNameFromValueExtractor(extractor, new NameExtractingClassVisitor("extract", SimpleValueExtractorType.METHOD), SIMPLE_VALUE_EXTRACTOR_METHOD_NAME_CACHE);
    }

    private <V> String extractNameFromValueExtractor(SimpleValueExtractor<T, V> extractor, NameExtractingClassVisitor visitor, ThreadLocal<Map<Class<?>, String>> cache) {
        if (cache.get() == null) cache.set(new HashMap<Class<?>, String>());
        if (!cache.get().containsKey(extractor.getClass())) {
            new ClassReader(getClassBytecode(extractor.getClass())).accept(visitor, SKIP_DEBUG);
            cache.get().put(extractor.getClass(), visitor.getName());
        }
        return cache.get().get(extractor.getClass());
    }

    private static byte[] getClassBytecode(Class<?> clazz) {
        try {
            return toByteArray(clazz.getResourceAsStream("/" + clazz.getName().replace('.', '/') + ".class"));
        } catch (IOException e) {
            throw new RuntimeException("Unable to read bytecode of class " + clazz.getName(), e);
        }
    }


    private static class NameExtractingClassVisitor extends ClassVisitor {
        private final String methodName;
        private final SimpleValueExtractorType type;
        private String name = null;

        private NameExtractingClassVisitor(String methodName, SimpleValueExtractorType type) {
            super(Opcodes.ASM5);
            this.methodName = methodName;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            return name.equals(methodName) ? new ValueExtractorMethodVisitor() : null;
        }

        private class ValueExtractorMethodVisitor extends MethodVisitor {
            private ValueExtractorMethodVisitor() {
                super(Opcodes.ASM5);
            }

            @Override
            public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                if (type == SimpleValueExtractorType.FIELD && opcode == Opcodes.GETFIELD) {
                    if (NameExtractingClassVisitor.this.name == null) {
                        NameExtractingClassVisitor.this.name = name;
                    } else {
                        throw new RuntimeException("More than one GETFIELD");
                    }
                }
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                if (type == SimpleValueExtractorType.METHOD) {
                    if (NameExtractingClassVisitor.this.name == null) {
                        NameExtractingClassVisitor.this.name = name;
                    } else {
                        throw new RuntimeException("More than one method call");
                    }
                }
            }
        }
    }

    private enum SimpleValueExtractorType {
        FIELD,
        METHOD,
    }*/
}
