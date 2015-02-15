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
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.join;
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
        return fieldImpl(nameForReportAndExtraction, matchers);
    }

    public <V> U field(String nameForReportAndExtraction, List<Matcher<?>> matchers) {
        return fieldImpl(nameForReportAndExtraction, matchers);
    }


    public <V> U field(String nameForReport, String nameForExtraction, Matcher<?>... matchers) {
        return fieldImpl(nameForReport, nameForExtraction, matchers);
    }

    public <V> U field(String nameForReport, String nameForExtraction, List<Matcher<?>> matchers) {
        return fieldImpl(nameForReport, nameForExtraction, matchers);
    }


    public <V> U field(Field field, Matcher<?>... matchers) {
        return fieldImpl(field, matchers);
    }

    public <V> U field(Field field, List<Matcher<?>> matchers) {
        return fieldImpl(field, matchers);
    }


    public <V> U field(String nameForReport, Field field, Matcher<?>... matchers) {
        return fieldImpl(nameForReport, field, matchers);
    }

    public <V> U field(String nameForReport, Field field, List<Matcher<?>> matchers) {
        return fieldImpl(nameForReport, field, matchers);
    }



    public <V> U method(String nameForReportAndExtraction, Matcher<?> matcher, Object... arguments) {
        return methodImpl(nameForReportAndExtraction, arguments, matcher);
    }

    public <V> U method(String nameForReportAndExtraction, Matcher<?>[] matchers, Object... arguments) {
        return methodImpl(nameForReportAndExtraction, arguments, matchers);
    }

    public <V> U method(String nameForReportAndExtraction, List<Matcher<?>> matchers, Object... arguments) {
        return methodImpl(nameForReportAndExtraction, arguments, matchers);
    }


    public <V> U method(String nameForReport, String nameForExtraction, Matcher<?> matcher, Object... arguments) {
        return methodImpl(nameForReport, nameForExtraction, arguments, matcher);
    }

    public <V> U method(String nameForReport, String nameForExtraction, Matcher<?>[] matchers, Object... arguments) {
        return methodImpl(nameForReport, nameForExtraction, arguments, matchers);
    }

    public <V> U method(String nameForReport, String nameForExtraction, List<Matcher<?>> matchers, Object... arguments) {
        return methodImpl(nameForReport, nameForExtraction, arguments, matchers);
    }


    public <V> U method(Method method, Matcher<?> matcher, Object... arguments) {
        return methodImpl(method, arguments, matcher);
    }

    public <V> U method(Method method, Matcher<?>[] matchers, Object... arguments) {
        return methodImpl(method, arguments, matchers);
    }

    public <V> U method(Method method, List<Matcher<?>> matchers, Object... arguments) {
        return methodImpl(method, arguments, matchers);
    }


    public <V> U method(String nameForReport, Method method, Matcher<?> matcher, Object... arguments) {
        return methodImpl(nameForReport, method, arguments, matcher);
    }

    public <V> U method(String nameForReport, Method method, Matcher<?>[] matchers, Object... arguments) {
        return methodImpl(nameForReport, method, arguments, matchers);
    }

    public <V> U method(String nameForReport, Method method, List<Matcher<?>> matchers, Object... arguments) {
        return methodImpl(nameForReport, method, arguments, matchers);
    }


    public <V> U method(ValueExtractor<T> valueExtractor, Matcher<?>... matchers) {
        return methodImpl(valueExtractor, matchers);
    }

    public <V> U method(ValueExtractor<T> valueExtractor, List<Matcher<?>> matchers) {
        return methodImpl(valueExtractor, matchers);
    }


    public <V> U method(String nameForReport, ValueExtractor<T> valueExtractor, Matcher<?>... matchers) {
        return methodImpl(nameForReport, valueExtractor, matchers);
    }

    public <V> U method(String nameForReport, ValueExtractor<T> valueExtractor, List<Matcher<?>> matchers) {
        return methodImpl(nameForReport, valueExtractor, matchers);
    }



    public <V> U getter(String nameForExtraction, Matcher<?>... matchers) {
        return getterImpl(nameForExtraction, matchers);
    }

    public <V> U getter(String nameForExtraction, List<Matcher<?>> matchers) {
        return getterImpl(nameForExtraction, matchers);
    }


    public <V> U getter(Method method, Matcher<?>... matchers) {
        return getterImpl(method, matchers);
    }

    public <V> U getter(Method method, List<Matcher<?>> matchers) {
        return getterImpl(method, matchers);
    }


    public <V> U getter(ValueExtractor<T> valueExtractor, Matcher<?>... matchers) {
        return getterImpl(valueExtractor, matchers);
    }

    public <V> U getter(ValueExtractor<T> valueExtractor, List<Matcher<?>> matchers) {
        return getterImpl(valueExtractor, matchers);
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




    private U fieldImpl(String nameForReportAndExtraction, Object matchers) {
        return fieldImpl(nameForReportAndExtraction, nameForReportAndExtraction, matchers);
    }

    private U fieldImpl(String nameForReport, String nameForExtraction, Object matchers) {
        Field field = getField(getActualItemClass(), nameForExtraction, true);
        Validate.isTrue(field != null, "Field %s is not found", nameForExtraction);
        return fieldImpl(nameForReport, field, matchers);
    }

    private U fieldImpl(Field field, Object matchers) {
        return fieldImpl(field.getName(), field, matchers);
    }

    private U fieldImpl(String nameForReport, final Field field, Object matchers) {
        return addPlannedCheck(new ValueExtractingPlannedCheck<T>(nameForReport, matchers) {
            @Override
            public Object extract(@NotNull T item) throws Exception {
                return readField(field, item, true);
            }
        });
    }


    public U methodImpl(String nameForReportAndExtraction, Object[] arguments, Object matchers) {
        return methodImpl(generateMethodNameForReport(nameForReportAndExtraction, arguments), nameForReportAndExtraction, arguments, matchers);
    }

    private U methodImpl(String nameForReport, String nameForExtraction, Object[] arguments, Object matchers) {
        Method method = getMatchingAccessibleMethod(getActualItemClass(), nameForExtraction, ClassUtils.toClass(arguments));
        Validate.isTrue(method != null, "Method %s with parameter classes %s is not found", nameForExtraction, join(asList(arguments), ", "));
        return methodImpl(nameForReport, method, arguments, matchers);
    }

    public U methodImpl(Method method, Object[] arguments, Object matchers) {
        return methodImpl(generateMethodNameForReport(method.getName(), arguments), method, arguments, matchers);
    }

    private U methodImpl(String nameForReport, final Method method, final Object[] arguments, Object matchers) {
        return addPlannedCheck(new ValueExtractingPlannedCheck<T>(nameForReport, matchers) {
            @Override
            public Object extract(@NotNull T item) throws Exception {
                method.setAccessible(true);
                return method.invoke(item, arguments);
            }
        });
    }

    public U methodImpl(ValueExtractor<T> valueExtractor, Object matchers) {
        return addPlannedCheck(new PlannedCheck<T>() {
            @Override
            public void execute(@NotNull Class<?> itemClass, @Nullable T item, @NotNull ExecutedCompositeCheckBuilder checker) {
                // TODO: run valueExtractor and record called methods
            }
        });
    }

    public U methodImpl(String nameForReport, final ValueExtractor<T> valueExtractor, Object matchers) {
        return addPlannedCheck(new ValueExtractingPlannedCheck<T>(nameForReport, matchers) {
            @Override
            public Object extract(@NotNull T item) throws Exception {
                return valueExtractor.extract(item);
            }
        });
    }


    private U getterImpl(String nameForExtraction, Object matchers) {
        return methodImpl(generateGetterNameForReport(nameForExtraction), nameForExtraction, EMPTY_ARGUMENTS, matchers);
    }

    private U getterImpl(Method method, Object matchers) {
        return methodImpl(generateGetterNameForReport(method.getName()), method, EMPTY_ARGUMENTS, matchers);
    }

    private U getterImpl(ValueExtractor<T> valueExtractor, Object matchers) {
        return methodImpl(valueExtractor, matchers);  // TODO: convert method name to property name (generateGetterNameForReport)
    }


    private static String generateMethodNameForReport(String name, Object... arguments) {

    }

    private static String generateGetterNameForReport(String nameForExtraction) {

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

    private static final Object[] EMPTY_ARGUMENTS = new Object[0];
}
