package com.github.alkedr.matchers.reporting;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.commons.lang3.ClassUtils;
import org.hamcrest.Matcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

import static ch.lambdaj.Lambda.argument;
import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.commons.lang3.reflect.FieldUtils.getField;
import static org.apache.commons.lang3.reflect.FieldUtils.readField;
import static org.apache.commons.lang3.reflect.MethodUtils.getMatchingAccessibleMethod;

// TODO: поддерживать вычисление выражений, например fieldX.methodY().listFieldZ.get(1) ?
public class ObjectMatcherForExtending<T, U extends ObjectMatcherForExtending<T, U>> extends ValueExtractingMatcherForExtending<T, U> {

    public ObjectMatcherForExtending(@NotNull Class<?> tClass) {
        super(tClass);
    }


    public <V> ValueCheckAdder<V, U> field(String nameForReportAndExtraction) {
        return field(nameForReportAndExtraction, nameForReportAndExtraction);
    }

    public <V> ValueCheckAdder<V, U> field(String nameForReport, String nameForExtraction) {
        Field field = getField(getActualItemClass(), nameForExtraction, true);
        if (field == null) throw new IllegalArgumentException(String.format("Field '%s' is not found", nameForExtraction));
        return field(nameForReport, field);
    }

    public <V> ValueCheckAdder<V, U> field(Field field) {
        return field(field.getName(), field);
    }

    public <V> ValueCheckAdder<V, U> field(String nameForReport, final Field field) {
        return new AbstractValueExtractingCheckAdder<V>(nameForReport) {
            @Override
            public Object extract(@NotNull T item) throws IllegalAccessException {
                return readField(field, item, true);
            }
        };
    }

    public <V> ValueCheckAdder<V, U> field(String nameForReport, final ValueExtractor<T> valueExtractor) {
        return new AbstractValueExtractingCheckAdder<V>(nameForReport) {
            @Override
            public Object extract(@NotNull T item) throws Exception {
                return valueExtractor.extract(item);
            }
        };
    }



    public <V> ValueCheckAdder<V, U> method(String nameForExtraction, Object... arguments) {
        return method(generateMethodNameForReport(nameForExtraction, arguments), nameForExtraction, arguments);
    }

    public <V> ValueCheckAdder<V, U> method(String nameForReport, String nameForExtraction, Object... arguments) {
        Method method = getMatchingAccessibleMethod(getActualItemClass(), nameForExtraction, ClassUtils.toClass(arguments));
        if (method == null) throw new IllegalArgumentException(String.format("Method '%s' with parameter classes [%s] is not found", nameForExtraction, Arrays.toString(arguments)));
        return method(nameForReport, method, arguments);
    }

    public <V> ValueCheckAdder<V, U> method(Method method, Object... arguments) {
        return method(generateMethodNameForReport(method.getName(), arguments), method, arguments);
    }

    public <V> ValueCheckAdder<V, U> method(String nameForReport, final Method method, final Object... arguments) {
        return new AbstractValueExtractingCheckAdder<V>(nameForReport) {
            @Override
            public Object extract(@NotNull T item) throws InvocationTargetException, IllegalAccessException {
                method.setAccessible(true);
                return method.invoke(item, arguments);
            }
        };
    }

    public <V> ValueCheckAdder<V, U> method(final ValueExtractor<T> valueExtractor) {
        return new AbstractValueExtractingCheckAdder<V>() {
            @Override
            public Object extract(@NotNull T item) throws Exception {
                getProxyMethodInterceptor().initForMethodValueExtractor(item, this);
                return valueExtractor.extract(getProxy());
            }
        };
    }

    public <V> ValueCheckAdder<V, U> method(String nameForReport, final ValueExtractor<T> valueExtractor) {
        return new AbstractValueExtractingCheckAdder<V>(nameForReport) {
            @Override
            public Object extract(@NotNull T item) throws Exception {
                return valueExtractor.extract(item);
            }
        };
    }



    public <V> ValueCheckAdder<V, U> getter(String nameForExtraction) {
        return method(generateGetterNameForReport(nameForExtraction), nameForExtraction, EMPTY_ARGUMENTS);
    }

    public <V> ValueCheckAdder<V, U> getter(Method method) {
        return method(generateGetterNameForReport(method.getName()), method, EMPTY_ARGUMENTS);
    }

    public <V> ValueCheckAdder<V, U> getter(ValueExtractor<T> valueExtractor) {
        return method(valueExtractor);  // TODO: convert method name to property name (generateGetterNameForReport)
    }



    public <V> ValueCheckAdder<V, U> property(V lambdajPlaceholder) {
        // TODO: поддерживать цепочки вызовов lambdaj  on(X.class).getX().getY()
        // TODO: (делать из этого дерево проверок, а не одну проверку?)
        // TODO: Как?
        return property(argument(lambdajPlaceholder).getInkvokedPropertyName(), lambdajPlaceholder);
    }

    public <V> ValueCheckAdder<V, U> property(String nameForReport, final V lambdajPlaceholder) {
        return new AbstractValueExtractingCheckAdder<V>(nameForReport) {
            @Override
            public Object extract(@NotNull T item) {
                return argument(lambdajPlaceholder).evaluate(item);
            }
        };
    }



    public <V> T expect(Matcher<?>... matchers) {
        return expectImpl(matchers);
    }

    public <V> T expect(Collection<Matcher<?>> matchers) {
        return expectImpl(matchers);
    }

    private T expectImpl(Object matchersObject) {
        getProxyMethodInterceptor().initForExpect(matchersObject);
        return getProxy();
    }



    public interface ValueCheckAdder<V, U> {
        U is(Matcher<?>... matchers);
        U is(Collection<? extends Matcher<? super V>> matchers);
        U returns(Matcher<?>... matchers);
        U returns(Collection<? extends Matcher<? super V>> matchers);
    }


    private abstract class AbstractValueExtractingCheckAdder<V> extends ValueExtractingPlannedCheck<T> implements ValueCheckAdder<V, U> {
        private AbstractValueExtractingCheckAdder() {
        }

        private AbstractValueExtractingCheckAdder(String valueName) {
            super(valueName);
        }

        @Override
        public final U is(Matcher<?>... matchers) {
            return isImpl(matchers);
        }

        @Override
        public U is(Collection<? extends Matcher<? super V>> matchers) {
            return isImpl(matchers);
        }

        @Override
        public final U returns(Matcher<?>... matchers) {
            return is(matchers);
        }

        @Override
        public U returns(Collection<? extends Matcher<? super V>> matchers) {
            return is(matchers);
        }


        private U isImpl(Object newMatchersObject) {
            if (getMatchersObject() != null) throw new IllegalStateException("Tried to call 'is' or 'returns' twice");
            setMatchersObject(newMatchersObject);
            return addPlannedCheck(this);
        }
    }



    private static String generateMethodNameForReport(String name, Object... arguments) {
        return String.format("%s(%s)", name, join(arguments, ", "));
    }

    private static String generateGetterNameForReport(String nameForExtraction) {
        if (nameForExtraction.length() > 2 && nameForExtraction.startsWith("is")) return nameForExtraction.substring(2, 3).toLowerCase() + nameForExtraction.substring(3);
        if (nameForExtraction.length() > 3 && nameForExtraction.startsWith("get")) return nameForExtraction.substring(3, 4).toLowerCase() + nameForExtraction.substring(4);
        return nameForExtraction;
    }



    private class ProxyMethodInterceptor implements MethodInterceptor {
        @Nullable private Object matchers = null;
        @Nullable private Object actualObject = null;
        @Nullable private ValueExtractingPlannedCheck<T> valueExtractingPlannedCheck = null;

        private void initForExpect(@NotNull Object newMatchers) {
            this.matchers = newMatchers;
            this.actualObject = null;
            this.valueExtractingPlannedCheck = null;
        }

        private void initForMethodValueExtractor(@Nullable Object newActualObject, @NotNull ValueExtractingPlannedCheck<T> newValueExtractingPlannedCheck) {
            this.matchers = null;
            this.actualObject = newActualObject;
            this.valueExtractingPlannedCheck = newValueExtractingPlannedCheck;
        }

        @Override
        public Object intercept(Object obj, final Method method, final Object[] args, MethodProxy methodProxy) throws Throwable {
            if (matchers != null) {
                addPlannedCheck(new ValueExtractingPlannedCheck<T>(method.getName(), matchers) {
                    @Override
                    public Object extract(@NotNull Object item) throws InvocationTargetException, IllegalAccessException {
                        return method.invoke(item, args);
                    }
                });
            } else if (valueExtractingPlannedCheck != null) {
                valueExtractingPlannedCheck.setValueName(generateMethodNameForReport(method.getName(), args));
            }
            Object returnValue = actualObject == null ? null : methodProxy.invoke(actualObject, args);  // FIXME: Что будет если примитивный тип?
            matchers = null;
            actualObject = null;
            valueExtractingPlannedCheck = null;
            return returnValue;
        }
    }

    private ProxyMethodInterceptor getProxyMethodInterceptor() {
        if (proxyMethodInterceptor == null) proxyMethodInterceptor = new ProxyMethodInterceptor();
        return proxyMethodInterceptor;
    }

    private T getProxy() {
        if (proxy == null) {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(getActualItemClass());
            enhancer.setCallback(proxyMethodInterceptor);
            proxy = (T) enhancer.create();
        }
        return proxy;
    }


    @Nullable private ProxyMethodInterceptor proxyMethodInterceptor = null;
    @Nullable private T proxy = null;

    private static final Object[] EMPTY_ARGUMENTS = new Object[0];
}
