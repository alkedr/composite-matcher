package com.github.alkedr.matchers.reporting;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.Validate;
import org.hamcrest.Matcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import static ch.lambdaj.Lambda.argument;
import static ch.lambdaj.Lambda.join;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.reflect.FieldUtils.getField;
import static org.apache.commons.lang3.reflect.FieldUtils.readField;
import static org.apache.commons.lang3.reflect.MethodUtils.getMatchingAccessibleMethod;
import static org.apache.commons.lang3.reflect.MethodUtils.invokeMethod;

// TODO: поддерживать вычисление выражений, например fieldX.methodY().listFieldZ.get(1) ?
// TODO: поддерживать цепочки вызовов lambdaj  on(X.class).getX().getY()
public class ObjectMatcherForExtending<T, U extends ObjectMatcherForExtending<T, U>> extends ValueExtractingMatcherForExtending<T, U> {
    public ObjectMatcherForExtending(@NotNull Class<?> tClass) {
        super(tClass);
    }


    public <V> U field(String nameForReportAndExtraction, Matcher<?>... matchers) {
        return fieldWithMatchersObject(nameForReportAndExtraction, nameForReportAndExtraction, matchers);
    }

    public <V> U field(String nameForReportAndExtraction, List<Matcher<?>> matchers) {
        return fieldWithMatchersObject(nameForReportAndExtraction, nameForReportAndExtraction, matchers);
    }


    public <V> U field(String nameForReport, String nameForExtraction, Matcher<?>... matchers) {
        return fieldWithMatchersObject(nameForReport, nameForExtraction, matchers);
    }

    public <V> U field(String nameForReport, String nameForExtraction, List<Matcher<?>> matchers) {
        return fieldWithMatchersObject(nameForReport, nameForExtraction, matchers);
    }


    public <V> U field(Field field, Matcher<?>... matchers) {
        return fieldWithMatchersObject(field.getName(), field, matchers);
    }

    public <V> U field(Field field, List<Matcher<?>> matchers) {
        return fieldWithMatchersObject(field.getName(), field, matchers);
    }


    public <V> U field(String nameForReport, Field field, Matcher<?>... matchers) {
        return fieldWithMatchersObject(nameForReport, field, matchers);
    }

    public <V> U field(String nameForReport, Field field, List<Matcher<?>> matchers) {
        return fieldWithMatchersObject(nameForReport, field, matchers);
    }



    public <V> U method(String nameForReportAndExtraction, Matcher<?> matchers, Object... arguments) {
        return methodWithMatchersObject(generateMethodNameForReport(nameForReportAndExtraction, arguments), nameForReportAndExtraction, matchers, arguments);
    }

    public <V> U method(String nameForReportAndExtraction, List<Matcher<?>> matchers, Object... arguments) {
        return methodWithMatchersObject(generateMethodNameForReport(nameForReportAndExtraction, arguments), nameForReportAndExtraction, matchers, arguments);
    }


    public <V> U method(String nameForReport, String nameForExtraction, Matcher<?> matchers, Object... arguments) {
        return methodWithMatchersObject(nameForReport, nameForExtraction, matchers, arguments);
    }

    public <V> U method(String nameForReport, String nameForExtraction, List<Matcher<?>> matchers, Object... arguments) {
        return methodWithMatchersObject(nameForReport, nameForExtraction, matchers, arguments);
    }



    public <V> U method(Method method, Matcher<?> matchers, Object... arguments) {
        return methodWithMatchersObject(generateMethodNameForReport(method.getName(), arguments), method, matchers, arguments);
    }

    public <V> U method(Method method, List<Matcher<?>> matchers, Object... arguments) {
        return methodWithMatchersObject(generateMethodNameForReport(method.getName(), arguments), method, matchers, arguments);
    }


    public <V> U method(String nameForReport, Method method, Matcher<?> matchers, Object... arguments) {
        return methodWithMatchersObject(nameForReport, method, matchers, arguments);
    }

    public <V> U method(String nameForReport, Method method, List<Matcher<?>> matchers, Object... arguments) {
        return methodWithMatchersObject(nameForReport, method, matchers, arguments);
    }



    public <V> U getter(String nameForExtraction, Matcher<?>... matchers) {
        return getter(nameForExtraction, asList(matchers));
    }

    public <V> U getter(String nameForExtraction, List<Matcher<?>> matchers) {
        return method(generateGetterNameForReport(nameForExtraction), nameForExtraction, matchers);
    }



    public <V> U getter(Method method, Matcher<?>... matchers) {
        return getter(nameForExtraction, asList(matchers));
    }

    public <V> U getter(Method method, List<Matcher<?>> matchers) {
        return method(getterNameToNameForReport(nameForExtraction), nameForExtraction, matchers);
    }



    public <V> ValueCheckAdder<V> property(V lambdajPlaceholder) {
        return property(argument(lambdajPlaceholder).getInkvokedPropertyName(), lambdajPlaceholder);
    }

    public <V> ValueCheckAdder<V> property(String nameForReport, V lambdajPlaceholder) {
        clear();
        this.propertyNameForReport = nameForReport;
        this.propertyLambdajPlaceholder = lambdajPlaceholder;
        return (ValueCheckAdder<V>) valueCheckAdder;
    }


    // Это должно быть в ValueExtractingMatcherForExtending
//    public <V> U field(String nameForReport, ValueExtractor<T> valueExtractor, Matcher<?>... matchers) {
//        return fieldWithMatchersObject(nameForReport, valueExtractor, matchers);
//    }
//
//    public <V> U field(String nameForReport, ValueExtractor<T> valueExtractor, List<Matcher<?>> matchers) {
//        return fieldWithMatchersObject(nameForReport, valueExtractor, matchers);
//    }

//    public <V> U field(ValueExtractor<NamedValue<T>> namedValueExtractor, Matcher<?>... matchers) {
//        return fieldWithMatchersObject(namedValueExtractor, matchers);
//    }
//
//    public <V> U field(ValueExtractor<NamedValue<T>> namedValueExtractor, List<Matcher<?>> matchers) {
//        return fieldWithMatchersObject(namedValueExtractor, matchers);
//    }

//    public <V> ValueCheckAdder<V> method(String nameForReport, Matcher<?> matchers, ValueExtractor<T> returnValueExtractor) {
//        clear();
//        this.methodNameForReport = nameForReport;
//        this.methodReturnValueExtractor = returnValueExtractor;
//        return (ValueCheckAdder<V>) valueCheckAdder;
//    }
//
//    public <V> ValueCheckAdder<V> method(String nameForReport, List<Matcher<?>> matchers, ValueExtractor<T> returnValueExtractor) {
//        clear();
//        this.methodNameForReport = nameForReport;
//        this.methodReturnValueExtractor = returnValueExtractor;
//        return (ValueCheckAdder<V>) valueCheckAdder;
//    }

//    public static class NamedValue<T> {
//        private final String name;
//        private final T value;
//
//        public NamedValue(String name, T value) {
//            this.name = name;
//            this.value = value;
//        }
//
//        public String getName() {
//            return name;
//        }
//
//        public T getValue() {
//            return value;
//        }
//    }






    private U fieldWithMatchersObject(String nameForReport, String nameForExtraction, Object matchers) {
        Field field = getField(getActualItemClass(), nameForExtraction, true);
        Validate.isTrue(field != null, "Field %s is not found", nameForExtraction);
        return fieldWithMatchersObject(nameForReport, field, matchers);
    }

    private U fieldWithMatchersObject(String nameForReport, final Field field, Object matchers) {
        return addPlannedCheck(new ValueExtractingPlannedCheck<T>(nameForReport, matchers) {
            @Override
            public Object extract(@NotNull T item) throws Exception {
                return readField(field, item, true);
            }
        });
    }

    private U methodWithMatchersObject(String nameForReport, String nameForExtraction, Object matchers, Object... arguments) {
        Method method = getMatchingAccessibleMethod(getActualItemClass(), nameForExtraction, ClassUtils.toClass(arguments));
        Validate.isTrue(method != null, "Method %s with parameter classes %s is not found", nameForExtraction, join(asList(arguments), ", "));
        return methodWithMatchersObject(nameForReport, method, matchers);
    }

    private U methodWithMatchersObject(String nameForReport, final Method method, Object matchers, final Object... arguments) {
        return addPlannedCheck(new ValueExtractingPlannedCheck<T>(nameForReport, matchers) {
            @Override
            public Object extract(@NotNull Object item) throws Exception {
                method.setAccessible(true);
                return method.invoke(item, arguments);
            }
        });
    }


    private String getterNameToNameForReport(String nameForExtraction) {

    }




    public <V> T expect(Matcher<V>... matchers) {
        return expectMatcherObject(matchers);
    }

    public <V> T expect(List<Matcher<V>> matchers) {
        return expectMatcherObject(matchers);
    }

    private T expectMatcherObject(Object matchersObject) {
        clear();
        this.expectMatchers = matchersObject;
        return expectProxy;
    }


    public class ValueCheckAdder<V> {
        public final U is(Matcher<?>... matchers) {
            return addPlannedCheck(createPlannedCheck(matchers));
        }

        public U is(Collection<? extends Matcher<? super V>> matchers) {
            return addPlannedCheck(createPlannedCheck(matchers));
        }

        public final U returns(Matcher<?>... matchers) {
            return is(matchers);
        }

        public U returns(Collection<? extends Matcher<? super V>> matchers) {
            return is(matchers);
        }


        private PlannedCheck<T> createPlannedCheck(Object matchers) {
            if (fieldNameForReport != null) {
                if (fieldNameForExtraction != null) {
                    return new ValueExtractingPlannedCheck<T>(fieldNameForReport, matchers) {
                        @Override
                        public Object extract(@NotNull Object item) throws Exception {
                            return readField(item, fieldNameForExtraction, true);
                        }
                    };
                }
                if (fieldValueExtractor != null) {
                    return valueExtractingPlannedCheckFromValueExtractor(fieldNameForReport, fieldValueExtractor, matchers);
                }
                throw new IllegalStateException("fieldNameForReport is not null but both fieldNameForExtraction and fieldValueExtractor are null");
            }
            if (methodNameForReport != null) {
                if (methodNameForExtraction != null && methodArguments != null) {
                    return new ValueExtractingPlannedCheck<T>(methodNameForReport, matchers) {
                        @Override
                        public Object extract(@NotNull Object item) throws Exception {
                            return invokeMethod(item, methodNameForExtraction, methodArguments);
                        }
                    };
                }
                if (methodReturnValueExtractor != null) {
                    return valueExtractingPlannedCheckFromValueExtractor(methodNameForReport, methodReturnValueExtractor, matchers);
                }
                throw new IllegalStateException("methodNameForReport is not null but one of methodNameForExtraction and methodArguments and methodReturnValueExtractor are null");
            }
//            if (propertyNameForReport != null) {
//                if (propertyLambdajPlaceholder != null) {
//                    return new ValueExtractingPlannedCheck<T>(propertyNameForReport, matchers) {
//                        @Override
//                        public Object extract(@NotNull Object item) throws Exception {
//                            return argument(propertyLambdajPlaceholder).evaluate(item);
//                        }
//                    };
//                }
//                throw new IllegalStateException("propertyNameForReport is not null but propertyLambdajPlaceholder is null");
//            }
            throw new IllegalStateException("fieldNameForReport, methodNameForReport and propertyNameForReport are null");
        }
    }


    private void clear() {
        fieldNameForReport = null;
        fieldNameForExtraction = null;
        fieldValueExtractor = null;
        methodNameForReport = null;
        methodNameForExtraction = null;
        methodArguments = null;
        methodReturnValueExtractor = null;
        propertyNameForReport = null;
        propertyLambdajPlaceholder = null;
    }

    private T createExpectProxy() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(getActualItemClass());
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, final Method method, final Object[] args, MethodProxy proxy) throws Throwable {
                addPlannedCheck(new ValueExtractingPlannedCheck<T>(method.getName(), expectMatchers) {
                    @Override
                    public Object extract(@NotNull Object item) throws Exception {
                        return method.invoke(item, args);
                    }
                });
                return null; // FIXME: Что будет если примитивный тип?
            }
        });
        return (T) enhancer.create();
    }


    @NotNull private final ValueCheckAdder<?> valueCheckAdder = new ValueCheckAdder<Object>();

    @Nullable private String fieldNameForReport = null;
    @Nullable private String fieldNameForExtraction = null;
    @Nullable private ValueExtractor<T> fieldValueExtractor = null;

    @Nullable private String methodNameForReport = null;
    @Nullable private String methodNameForExtraction = null;
    @Nullable private Object[] methodArguments = null;
    @Nullable private ValueExtractor<T> methodReturnValueExtractor = null;

    @Nullable private String propertyNameForReport = null;
    @Nullable private Object propertyLambdajPlaceholder = null;

    @Nullable private Object expectMatchers = null;
    @NotNull private final T expectProxy = createExpectProxy();
}
