package com.github.alkedr.matchers.reporting;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.hamcrest.Matcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import static ch.lambdaj.Lambda.argument;
import static org.apache.commons.lang3.reflect.FieldUtils.readField;
import static org.apache.commons.lang3.reflect.MethodUtils.invokeMethod;

// TODO: поддерживать вычисление выражений, например fieldX.methodY().listFieldZ.get(1) ?
// TODO: поддерживать цепочки вызовов lambdaj  on(X.class).getX().getY()
public class ObjectMatcherForExtending<T, U extends ObjectMatcherForExtending<T, U>> extends ValueExtractingMatcherForExtending<T, U> {
    public <V> ValueCheckAdder<V> field(String nameForReportAndExtraction) {
        return field(nameForReportAndExtraction, nameForReportAndExtraction);
    }

    public <V> ValueCheckAdder<V> field(String nameForReport, String nameForExtraction) {
        clear();
        this.fieldNameForReport = nameForReport;
        this.fieldNameForExtraction = nameForExtraction;
        return (ValueCheckAdder<V>) valueCheckAdder;
    }

    public <V> ValueCheckAdder<V> field(String nameForReport, ValueExtractor<T> valueExtractor) {
        clear();
        this.fieldNameForReport = nameForReport;
        this.fieldValueExtractor = valueExtractor;
        return (ValueCheckAdder<V>) valueCheckAdder;
    }


    public <V> ValueCheckAdder<V> method(String nameForReportAndExtraction, Object... arguments) {
        return method(nameForReportAndExtraction, nameForReportAndExtraction, arguments);
    }

    public <V> ValueCheckAdder<V> method(String nameForReport, String nameForExtraction, Object... arguments) {
        clear();
        this.methodNameForReport = nameForReport;
        this.methodNameForExtraction = nameForExtraction;
        this.methodArguments = arguments;
        return (ValueCheckAdder<V>) valueCheckAdder;
    }

    public <V> ValueCheckAdder<V> method(String nameForReport, ValueExtractor<T> returnValueExtractor) {
        clear();
        this.methodNameForReport = nameForReport;
        this.methodReturnValueExtractor = returnValueExtractor;
        return (ValueCheckAdder<V>) valueCheckAdder;
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
            if (propertyNameForReport != null) {
                if (propertyLambdajPlaceholder != null) {
                    return new ValueExtractingPlannedCheck<T>(propertyNameForReport, matchers) {
                        @Override
                        public Object extract(@NotNull Object item) throws Exception {
                            return argument(propertyLambdajPlaceholder).evaluate(item);
                        }
                    };
                }
                throw new IllegalStateException("propertyNameForReport is not null but propertyLambdajPlaceholder is null");
            }
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
