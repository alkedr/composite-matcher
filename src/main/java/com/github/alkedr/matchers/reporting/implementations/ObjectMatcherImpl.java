package com.github.alkedr.matchers.reporting.implementations;

import ch.lambdaj.function.argument.Argument;
import ch.lambdaj.function.argument.InvocationException;
import com.github.alkedr.matchers.reporting.ObjectMatcher;
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
import java.util.*;

import static ch.lambdaj.Lambda.argument;
import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.commons.lang3.reflect.FieldUtils.getField;
import static org.apache.commons.lang3.reflect.FieldUtils.readField;
import static org.apache.commons.lang3.reflect.MethodUtils.getMatchingAccessibleMethod;

public class ObjectMatcherImpl<T, U extends ObjectMatcherImpl<T, U>> extends PlanningMatcherImpl<T, U> implements ObjectMatcher<T> {
    private final Class<?> tClass;

    public ObjectMatcherImpl(Class<?> tClass) {
        this.tClass = tClass;
    }


    @Override
    public <V> ValueCheckAdder<T, V> field(String nameForReportAndExtraction) {
        return field(nameForReportAndExtraction, nameForReportAndExtraction);
    }

    @Override
    public <V> ValueCheckAdder<T, V> field(String nameForReport, String nameForExtraction) {
        return field(nameForReport, getFieldByName(nameForExtraction));
    }

    @Override
    public <V> ValueCheckAdder<T, V> field(Field field) {
        return field(field.getName(), field);
    }

    @Override
    public <V> ValueCheckAdder<T, V> field(String nameForReport, Field field) {
        ValueCheckAdderImpl<V> result = new FieldCheckAdder<>(nameForReport, field);
        addPlannedCheck(result);
        return result;
    }



    @Override
    public <V> ValueCheckAdder<T, V> method(String nameForExtraction, Object... arguments) {
        return method(METHOD_NAME_FOR_REPORT.generateNameForReport(nameForExtraction, arguments), nameForExtraction, arguments);
    }

    @Override
    public <V> ValueCheckAdder<T, V> method(String nameForReport, String nameForExtraction, Object... arguments) {
        return method(nameForReport, getMethodByName(nameForExtraction, ClassUtils.toClass(arguments)), arguments);
    }

    @Override
    public <V> ValueCheckAdder<T, V> method(ValueExtractor<T> valueExtractor) {
        getProxyMethodInterceptor().initForMethodValueExtractor();
        valueExtractor.extract(getProxy());
        return method(getProxyMethodInterceptor().method, getProxyMethodInterceptor().arguments);
    }

    @Override
    public <V> ValueCheckAdder<T, V> method(String nameForReport, ValueExtractor<T> valueExtractor) {
        getProxyMethodInterceptor().initForMethodValueExtractor();
        valueExtractor.extract(getProxy());
        return method(nameForReport, getProxyMethodInterceptor().method, getProxyMethodInterceptor().arguments);
    }

    @Override
    public <V> ValueCheckAdder<T, V> method(Method method, Object... arguments) {
        return method(METHOD_NAME_FOR_REPORT.generateNameForReport(method.getName(), arguments), method, arguments);
    }

    @Override
    public <V> ValueCheckAdder<T, V> method(String nameForReport, Method method, Object... arguments) {
        ValueCheckAdderImpl<V> result = new MethodCheckAdder<>(nameForReport, method, arguments);
        addPlannedCheck(result);
        return result;
    }



    @Override
    public <V> ValueCheckAdder<T, V> getter(String nameForExtraction) {
        return method(GETTER_NAME_FOR_REPORT.generateNameForReport(nameForExtraction), nameForExtraction, EMPTY_ARGUMENTS);
    }

    @Override
    public <V> ValueCheckAdder<T, V> getter(ValueExtractor<T> valueExtractor) {
        getProxyMethodInterceptor().initForMethodValueExtractor();
        valueExtractor.extract(getProxy());
        if (getProxyMethodInterceptor().arguments.length != 0) throw new IllegalArgumentException("У геттера не может быть параметров");
        return getter(getProxyMethodInterceptor().method);
    }

    @Override
    public <V> ValueCheckAdder<T, V> getter(Method method) {
        return method(GETTER_NAME_FOR_REPORT.generateNameForReport(method.getName()), method, EMPTY_ARGUMENTS);
    }



    @Override
    public <V> ValueCheckAdder<T, V> property(V lambdajPlaceholder) {
        Argument<V> argument = argument(lambdajPlaceholder);
        return propertyImpl(argument.getInkvokedPropertyName(), argument);
    }

    @Override
    public <V> ValueCheckAdder<T, V> property(String nameForReport, V lambdajPlaceholder) {
        return propertyImpl(nameForReport, argument(lambdajPlaceholder));
    }



    @Override
    @SafeVarargs
    public final <V> T expect(Matcher<? super V>... matchers) {
        Collection<Matcher<? super V>> matcherList = new ArrayList<>(matchers.length);
        Collections.addAll(matcherList, matchers);
        return expectImpl(matcherList);
    }

    @Override
    public <V> T expect(Iterable<? extends Matcher<? super V>> matchers) {
//        return expectImpl(new ArrayList<>(matchers));
        return null;
    }





    private <V> T expectImpl(Collection<Matcher<? super V>> matchersObject) {
//        getProxyMethodInterceptor().initForExpect(matchersObject);  //TODO
        return getProxy();
    }




    private <V> ValueCheckAdder<T, V> propertyImpl(String nameForReport, Argument<V> argument) {
        ValueCheckAdderImpl<V> result = new LambdajPropertyCheckAdder<>(nameForReport, argument);
        addPlannedCheck(result);
        return result;
    }





    private interface MethodNameForReportGenerator {
        String generateNameForReport(String methodName, Object... args);
    }

    private static final MethodNameForReportGenerator METHOD_NAME_FOR_REPORT = new MethodNameForReportGenerator() {
        @Override
        public String generateNameForReport(String methodName, Object... args) {
            return String.format("%s(%s)", methodName, join(args, ", "));
        }
    };

    private static final MethodNameForReportGenerator GETTER_NAME_FOR_REPORT = new MethodNameForReportGenerator() {
        @Override
        public String generateNameForReport(String methodName, Object... args) {
            if (methodName.length() > 2 && methodName.startsWith("is")) return methodName.substring(2, 3).toLowerCase() + methodName.substring(3);
            if (methodName.length() > 3 && methodName.startsWith("get")) return methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
            return methodName;
        }
    };








    @NotNull
    private Field getFieldByName(String name) {
        Field field = getField(tClass, name, true);
        if (field == null) throw new IllegalArgumentException(String.format("Field '%s' is not found", name));
        return field;
    }

    @NotNull
    private Method getMethodByName(String name, Class<?>... argumentClasses) {
        Method method = getMatchingAccessibleMethod(tClass, name, argumentClasses);
        if (method == null) throw new IllegalArgumentException(String.format("Method '%s' with parameter classes [%s] is not found", name, Arrays.toString(argumentClasses)));
        return method;
    }




    public abstract class ValueCheckAdderImpl<V> implements ValueCheckAdder<T, V>, PlannedCheck {
        protected final List<Matcher<?>> matchersObject = new ArrayList<>();
//        protected Object matchersObject = null;

        @Override
        public U is(Matcher<?> matcher) {
            matchersObject.add(matcher);
            return (U) ObjectMatcherImpl.this;
        }

        @Override
        @SafeVarargs
        public final U is(Matcher<?>... matchers) {
            Collections.addAll(this.matchersObject, matchers);
            return (U) ObjectMatcherImpl.this;
        }

        @Override
        public U is(Collection<? extends Matcher<?>> matchers) {
            this.matchersObject.addAll(matchers);
            return (U) ObjectMatcherImpl.this;
        }

        @Override
        public U returns(Matcher<? super V> matcher) {
            return is(matcher);
        }

        @Override
        @SafeVarargs
        public final U returns(Matcher<? super V>... matchers) {
            return is(matchers);
        }

        @Override
        public U returns(Collection<? extends Matcher<? super V>> matchers) {
            return is(matchers);
        }
    }


    private class FieldCheckAdder<V> extends ValueCheckAdderImpl<V> {
        private final String nameForReport;
        private final Field field;

        FieldCheckAdder(String nameForReport, Field field) {
            this.nameForReport = nameForReport;
            this.field = field;
        }

        @Override
        public boolean matches(@Nullable Object item, @NotNull CheckListener listener) {
            Object value;
            try {
                value = readField(field, item, true);
            } catch (IllegalAccessException e) {
                return brokenValue(listener, nameForReport, e, matchersObject);
            }
            return normalValue(listener, nameForReport, value, matchersObject);
        }
    }


    private class MethodCheckAdder<V> extends ValueCheckAdderImpl<V> {
        private final String nameForReport;
        private final Method method;
        private final Object[] arguments;

        MethodCheckAdder(String nameForReport, Method method, Object... arguments) {
            this.nameForReport = nameForReport;
            this.method = method;
            this.arguments = arguments;
        }

        @Override
        public boolean matches(@Nullable Object item, @NotNull CheckListener listener) {
            Object value;
            try {
                method.setAccessible(true);
                value = method.invoke(item, arguments);
            } catch (IllegalAccessException | InvocationTargetException e) {
                return brokenValue(listener, nameForReport, e, matchersObject);
            }
            return normalValue(listener, nameForReport, value, matchersObject);
        }
    }


    private class LambdajPropertyCheckAdder<V> extends ValueCheckAdderImpl<V> {
        private final String nameForReport;
        private final Argument<?> argument;

        LambdajPropertyCheckAdder(String nameForReport, Argument<?> argument) {
            this.nameForReport = nameForReport;
            this.argument = argument;
        }

        @Override
        public boolean matches(@Nullable Object item, @NotNull CheckListener listener) {
            Object value;
            try {
                value = argument.evaluate(item);
            } catch (InvocationException e) {
                return brokenValue(listener, nameForReport, e, matchersObject);
            }
            return normalValue(listener, nameForReport, value, matchersObject);
        }
    }



    private class ProxyMethodInterceptor implements MethodInterceptor {
        @Nullable Collection<Matcher<Object>> matchers = null;
        @Nullable Method method = null;
        @Nullable Object[] arguments = null;

        void initForExpect(@NotNull Collection<Matcher<Object>> newMatchers) {
            this.matchers = newMatchers;
            this.method = null;
        }

        void initForMethodValueExtractor() {
            this.matchers = null;
            this.method = null;
        }

        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy) throws InvocationTargetException, IllegalAccessException {
            if (matchers != null) {
                method(method, args).is(matchers);
            } else {
                if (this.method != null) throw new IllegalStateException("ValueExtractor вызвал два метода");
                this.method = method;
                arguments = args;
            }
            matchers = null;
            return null; // FIXME: Что будет если примитивный тип?
        }
    }

    private ProxyMethodInterceptor getProxyMethodInterceptor() {
        if (proxyMethodInterceptor == null) proxyMethodInterceptor = new ProxyMethodInterceptor();
        return proxyMethodInterceptor;
    }

    private T getProxy() {
        if (proxy == null) {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(tClass);
            enhancer.setCallback(proxyMethodInterceptor);
            proxy = (T) enhancer.create();
        }
        return proxy;
    }

    @Nullable private ProxyMethodInterceptor proxyMethodInterceptor = null;
    @Nullable private T proxy = null;


    private static final Object[] EMPTY_ARGUMENTS = new Object[0];
}
