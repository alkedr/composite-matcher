package com.github.alkedr.matchers.reporting.old;

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
import static org.apache.commons.lang3.reflect.MethodUtils.getMatchingAccessibleMethod;

/*
    Экстракторы непроверенного
        - должны передаваться матчерам для непроверенных значений
        - матчеры непроверенных значений должны иметь возможность заоверрайдить их полностью или добавить правила для отдельных классов
        - должны уметь работать с любыми классами (бины, списки, мапы, примитивные типы, строки)
        - должны позволять добавление правил для отдельных классов (для случаев, когда у бина с геттерами есть поле-бин без геттеров)

        UncheckedValuesExtractorsDispatcher - мапа Class -> UncheckedValuesExtractor
        UncheckedValuesExtractor - вещи типа "все поля", "все методы", "все геттеры", "все элементы" и пр.,
                                   возвращают мапу название -> значение в каком-то виде
        UncheckedValuesFilter - вещи типа "игнорировать поле 'abc'"

                                   У каждого UncheckedValuesExtractor'а ссылка на следующий
                                   Применяем первый, если он не сработал, применяем второй и так далее
 */

// TODO: экстрактор - Field или Method + args

// TODO: поддерживать вычисление выражений, например fieldX.methodY().listFieldZ.get(1) ?
public class ObjectMatcherForExtending<T, U extends ObjectMatcherForExtending<T, U>> extends ValueExtractingMatcherForExtending<T, U> {
//    private AbstractPlannedCheck firstCheck = null;
//    private AbstractPlannedCheck lastCheck = null;
//    private Collection<AbstractPlannedFieldCheck> fieldChecks;
//    private Collection<AbstractPlannedMethodCheck> methodChecks;


//    public Iterable<AbstractPlannedCheck> getPlannedChecks() {
//        Set
//        ArrayList a;
//        a.sort();
//    }


//    private abstract static class AbstractPlannedCheck {
//        private Object matchersObject;
//        private Check next;
//
//
//        // addMatchersObject
//
//        // getMatchersObject
//
//        // getMatchers
//
//        // execute
//
//
//        protected boolean isMissing(@NotNull T item) throws Exception {
//            return false;
//        }
//
//        protected boolean isUnexpected(@Nullable T item) throws Exception {
//            return false;
//        }
//
//        protected abstract Object getValue(@NotNull T item) throws Exception;
//    }


//    private abstract static class AbstractPlannedFieldCheck extends AbstractPlannedCheck {
//        @Override
//        protected Object getValue(@NotNull T item) throws Exception {
//            return null;
//        }
//
//        protected abstract Field getField();
//    }
//
//
//    private abstract static class AbstractPlannedMethodCheck extends AbstractPlannedCheck {
//        @Override
//        protected Object getValue(@NotNull T item) throws Exception {
//            return null;
//        }
//
//        protected abstract Method getMethod();
//        protected abstract Object[] getArguments();
//    }




    public interface ValueExtractor<U extends ValueExtractor<U>> extends Comparable<U> {
        Object extract(Object o);
    }

    public interface FieldExtractor extends ValueExtractor<FieldExtractor> {
        Field getField();
    }

    public interface MethodExtractor extends ValueExtractor<MethodExtractor> {
        Method getMethod();
        Object[] getArguments();
    }

    public interface ArrayElementExtractor extends ValueExtractor<ArrayElementExtractor> {
        int getIndex();
    }




    public ObjectMatcherForExtending(@NotNull Class<?> tClass) {
        super(tClass);
    }


//    public <V> ValueCheckAdder<T, V> field(String nameForReportAndExtraction) {
//        return field(nameForReportAndExtraction, nameForReportAndExtraction);
//    }

//    public <V> ValueCheckAdder<T, V> field(String nameForReport, String nameForExtraction) {
//        Field field = getField(getActualItemClass(), nameForExtraction, true);
//        if (field == null) throw new IllegalArgumentException(String.format("Field '%s' is not found", nameForExtraction));
//        return field(nameForReport, field);
//    }

//    public <V> ValueCheckAdder<T, V> field(Field field) {
//        return field(field.getName(), field);
//    }

//    public <V> ValueCheckAdder<T, V> field(String nameForReport, final Field field) {
//        return new AbstractValueExtractingCheckAdder<V>(nameForReport) {
//            @Override
//            public Object getValue(@NotNull T item) throws IllegalAccessException {
//                return readField(field, item, true);
//            }
//        };
//    }

    public <V> ValueCheckAdder<V, U> field(String nameForReport, final ValueExtractor valueExtractor) {
        return new AbstractValueExtractingCheckAdder<V>(nameForReport) {
            @Override
            public Object getValue(@NotNull T item) throws Exception {
                return valueExtractor.extract(item);
            }
        };
    }



    public <V> ValueCheckAdder<V, U> method(String nameForExtraction, Object... arguments) {
        return method(METHOD_NAME_FOR_REPORT, nameForExtraction, arguments);
    }

    private <V> ValueCheckAdder<V, U> method(MethodNameForReportGenerator methodNameForReportGenerator, String nameForExtraction, Object... arguments) {
        return method(methodNameForReportGenerator.generateNameForReport(nameForExtraction, arguments), nameForExtraction, arguments);
    }

    public <V> ValueCheckAdder<V, U> method(String nameForReport, String nameForExtraction, Object... arguments) {
        Method method = getMatchingAccessibleMethod(getActualItemClass(), nameForExtraction, ClassUtils.toClass(arguments));
        if (method == null) throw new IllegalArgumentException(String.format("Method '%s' with parameter classes [%s] is not found", nameForExtraction, Arrays.toString(arguments)));
        return method(nameForReport, method, arguments);
    }

    public <V> ValueCheckAdder<V, U> method(Method method, Object... arguments) {
        return method(METHOD_NAME_FOR_REPORT, method, arguments);
    }

    private <V> ValueCheckAdder<V, U> method(MethodNameForReportGenerator methodNameForReportGenerator, Method method, Object... arguments) {
        return method(methodNameForReportGenerator.generateNameForReport(method.getName(), arguments), method, arguments);
    }

    public <V> ValueCheckAdder<V, U> method(String nameForReport, final Method method, final Object... arguments) {
        return new AbstractValueExtractingCheckAdder<V>(nameForReport) {
            @Override
            public Object getValue(@NotNull T item) throws InvocationTargetException, IllegalAccessException {
                method.setAccessible(true);
                return method.invoke(item, arguments);
            }
        };
    }

    public <V> ValueCheckAdder<V, U> method(ValueExtractor valueExtractor) {
        return method(getMethodName(valueExtractor, METHOD_NAME_FOR_REPORT), valueExtractor);
    }

    public <V> ValueCheckAdder<V, U> method(String nameForReport, final ValueExtractor valueExtractor) {
        return new AbstractValueExtractingCheckAdder<V>(nameForReport) {
            @Override
            public Object getValue(@NotNull T item) throws Exception {
                return valueExtractor.extract(item);
            }
        };
    }



    public <V> ValueCheckAdder<V, U> getter(String nameForExtraction) {
        return method(GETTER_NAME_FOR_REPORT, nameForExtraction, EMPTY_ARGUMENTS);
    }

    public <V> ValueCheckAdder<V, U> getter(Method method) {
        return method(GETTER_NAME_FOR_REPORT, method, EMPTY_ARGUMENTS);
    }

    public <V> ValueCheckAdder<V, U> getter(ValueExtractor valueExtractor) {
        return method(getMethodName(valueExtractor, GETTER_NAME_FOR_REPORT), valueExtractor);
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
            public Object getValue(@NotNull T item) {
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


        // TODO: silent checks?


//        public U isIgnored() {
//            // для извлечения непроверенных значений
//        }


        private U isImpl(Object newMatchersObject) {
            // TODO: merge matcher objects
            if (getMatchersObject() != null) throw new IllegalStateException("Tried to call 'is' or 'returns' twice");
            setMatchersObject(newMatchersObject);
            return addPlannedCheck(this);
        }
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


    private class ProxyMethodInterceptor implements MethodInterceptor {
        @Nullable private Object matchers = null;
        @Nullable private Method method = null;
        @Nullable private Object[] arguments = null;

        private void initForExpect(@NotNull Object newMatchers) {
            this.matchers = newMatchers;
            this.method = null;
        }

        private void initForMethodValueExtractor() {
            this.matchers = null;
            this.method = null;
        }

        @Nullable
        public Method getMethod() {
            return method;
        }

        @Nullable
        public Object[] getArguments() {
            return arguments;
        }

        @Override
        public Object intercept(Object obj, final Method method, final Object[] args, MethodProxy methodProxy) throws Throwable {
            if (matchers != null) {
                addPlannedCheck(new ValueExtractingPlannedCheck<T>(method.getName(), matchers) {
                    @Override
                    public Object getValue(@NotNull Object item) throws InvocationTargetException, IllegalAccessException {
                        return method.invoke(item, args);
                    }
                });
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
            enhancer.setSuperclass(getActualItemClass());
            enhancer.setCallback(proxyMethodInterceptor);
            proxy = (T) enhancer.create();
        }
        return proxy;
    }

    private String getMethodName(ValueExtractor valueExtractor, MethodNameForReportGenerator generator) {
        getProxyMethodInterceptor().initForMethodValueExtractor();
        try {
            valueExtractor.extract(getProxy());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return generator.generateNameForReport(getProxyMethodInterceptor().getMethod().getName(), getProxyMethodInterceptor().getArguments());
    }


    @Nullable private ProxyMethodInterceptor proxyMethodInterceptor = null;
    @Nullable private T proxy = null;

    private static final Object[] EMPTY_ARGUMENTS = new Object[0];
}
