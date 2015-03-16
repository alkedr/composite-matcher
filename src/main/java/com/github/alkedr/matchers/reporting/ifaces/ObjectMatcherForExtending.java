package com.github.alkedr.matchers.reporting.ifaces;

import ch.lambdaj.function.argument.Argument;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.commons.lang3.ClassUtils;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static ch.lambdaj.Lambda.argument;
import static com.github.alkedr.matchers.reporting.ifaces.ReportingMatcherForImplementing.CompositeCheck.ExtractionStatus.NORMAL;
import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.commons.lang3.reflect.FieldUtils.getField;
import static org.apache.commons.lang3.reflect.FieldUtils.readField;
import static org.apache.commons.lang3.reflect.MethodUtils.getMatchingAccessibleMethod;

public class ObjectMatcherForExtending<T, U extends ObjectMatcherForExtending<T, U>>
        extends ReportingMatcherForExtending<T, U>
        implements ObjectMatcherForImplementing<T, U>, ObjectMatcher<T>
{
    private final Collection<ValueCheckAdderImpl<?>> valueCheckAdders = new ArrayList<>();
    private final String descriptionString;

    public ObjectMatcherForExtending(@NotNull Class<?> actualItemClass, String descriptionString) {
        super(actualItemClass);
        this.descriptionString = descriptionString;
    }


    @Override
    public <V> ValueCheckAdder<V, U> field(String nameForReportAndExtraction) {
        return field(nameForReportAndExtraction, nameForReportAndExtraction);
    }

    @Override
    public <V> ValueCheckAdder<V, U> field(String nameForReport, String nameForExtraction) {
        return field(nameForReport, getFieldByName(nameForExtraction));
    }

    @Override
    public <V> ValueCheckAdder<V, U> field(Field field) {
        return field(field.getName(), field);
    }

    @Override
    public <V> ValueCheckAdder<V, U> field(String nameForReport, Field field) {
//        for (Node n = head; n != null; n = n.next) {
//            if (n is what we need) return n;
//            if (n.next == null) return n.next = new Node();
//        }
//        return head = new Node();

        for (ValueCheckAdderImpl<?> adder : valueCheckAdders) {
            if (isFieldCheckAdder(adder, nameForReport, field)) return (ValueCheckAdder<V, U>) adder;
        }
        ValueCheckAdderImpl<V> result = new FieldCheckAdder<>(nameForReport, field);
        valueCheckAdders.add(result);
        return result;
    }



    @Override
    public <V> ValueCheckAdder<V, U> method(String nameForExtraction, Object... arguments) {
        return method(METHOD_NAME_FOR_REPORT.generateNameForReport(nameForExtraction, arguments), nameForExtraction, arguments);
    }

    @Override
    public <V> ValueCheckAdder<V, U> method(String nameForReport, String nameForExtraction, Object... arguments) {
        return method(nameForReport, getMethodByName(nameForExtraction, ClassUtils.toClass(arguments)), arguments);
    }

    @Override
    public <V> ValueCheckAdder<V, U> method(ValueExtractor<T> valueExtractor) {
        getProxyMethodInterceptor().initForMethodValueExtractor();
        valueExtractor.extract(getProxy());
        return method(getProxyMethodInterceptor().method, getProxyMethodInterceptor().arguments);
    }

    @Override
    public <V> ValueCheckAdder<V, U> method(String nameForReport, ValueExtractor<T> valueExtractor) {
        getProxyMethodInterceptor().initForMethodValueExtractor();
        valueExtractor.extract(getProxy());
        return method(nameForReport, getProxyMethodInterceptor().method, getProxyMethodInterceptor().arguments);
    }

    @Override
    public <V> ValueCheckAdder<V, U> method(Method method, Object... arguments) {
        return method(METHOD_NAME_FOR_REPORT.generateNameForReport(method.getName(), arguments), method, arguments);
    }

    @Override
    public <V> ValueCheckAdder<V, U> method(String nameForReport, Method method, Object... arguments) {
        for (ValueCheckAdderImpl<?> adder : valueCheckAdders) {
            if (isExtractorForMethod(adder, nameForReport, method, arguments)) return (ValueCheckAdder<V, U>) adder;
        }
        ValueCheckAdderImpl<V> result = new MethodCheckAdder<>(nameForReport, method, arguments);
        valueCheckAdders.add(result);
        return result;
    }



    @Override
    public <V> ValueCheckAdder<V, U> getter(String nameForExtraction) {
        return method(GETTER_NAME_FOR_REPORT.generateNameForReport(nameForExtraction), nameForExtraction, EMPTY_ARGUMENTS);
    }

    @Override
    public <V> ValueCheckAdder<V, U> getter(ValueExtractor<T> valueExtractor) {
        getProxyMethodInterceptor().initForMethodValueExtractor();
        valueExtractor.extract(getProxy());
        if (getProxyMethodInterceptor().arguments.length != 0) throw new IllegalArgumentException("У геттера не может быть параметров");
        return getter(getProxyMethodInterceptor().method);
    }

    @Override
    public <V> ValueCheckAdder<V, U> getter(Method method) {
        return method(GETTER_NAME_FOR_REPORT.generateNameForReport(method.getName()), method, EMPTY_ARGUMENTS);
    }


    @Override
    public <V> ValueCheckAdder<V, U> property(V lambdajPlaceholder) {
        Argument<V> argument = argument(lambdajPlaceholder);
        return propertyImpl(argument.getInkvokedPropertyName(), argument);
    }

    @Override
    public <V> ValueCheckAdder<V, U> property(String nameForReport, V lambdajPlaceholder) {
        return propertyImpl(nameForReport, argument(lambdajPlaceholder));
    }


    @SafeVarargs
    @Override
    public final <V> T expect(Matcher<? super V>... matchers) {
        Collection<Matcher<? super V>> matcherList = new ArrayList<>(matchers.length);
        Collections.addAll(matcherList, matchers);
        return expectImpl(matcherList);
    }

    @Override
    public <V> T expect(Collection<? extends Matcher<? super V>> matchers) {
        return expectImpl(new ArrayList<>(matchers));
    }


    private <V> T expectImpl(Collection<Matcher<? super V>> matchersObject) {
//        getProxyMethodInterceptor().initForExpect(matchersObject);  //TODO
        return getProxy();
    }



    @Override
    public Check.Status addChecksTo(@NotNull Checker storage) {
        for (ValueCheckAdderImpl<?> adder : valueCheckAdders) {
            storage.subcheck(adder);
        }
        return null;
    }


    @Override
    public void describeTo(Description description) {
        description.appendText(descriptionString);
    }




    private <V> ValueCheckAdder<V, U> propertyImpl(String nameForReport, Argument<V> argument) {
        for (ValueCheckAdderImpl<?> adder : valueCheckAdders) {
            if (isExtractorForLambdajProperty(adder, nameForReport, argument)) return (ValueCheckAdder<V, U>) adder;
        }
        ValueCheckAdderImpl<V> result = new LambdajPropertyCheckAdder<>(nameForReport, argument);
        valueCheckAdders.add(result);
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
        Field field = getField(getActualItemClass(), name, true);
        if (field == null) throw new IllegalArgumentException(String.format("Field '%s' is not found", name));
        return field;
    }

    @NotNull
    private Method getMethodByName(String name, Class<?>... argumentClasses) {
        Method method = getMatchingAccessibleMethod(getActualItemClass(), name, argumentClasses);
        if (method == null) throw new IllegalArgumentException(String.format("Method '%s' with parameter classes [%s] is not found", name, Arrays.toString(argumentClasses)));
        return method;
    }





    private boolean isFieldCheckAdder(ValueCheckAdderImpl<?> extractor, String name, Field field) {
        if (!(extractor instanceof FieldCheckAdder)) return false;
        FieldCheckAdder<?> fieldExtractor = (FieldCheckAdder<?>) extractor;
        if (!Objects.equals(name, fieldExtractor.nameForReport)) return false;
        if (!Objects.equals(field, fieldExtractor.field)) return false;
        return true;
    }

    private boolean isExtractorForMethod(ValueCheckAdderImpl<?> extractor, String name, Method method, Object... arguments) {
        if (!(extractor instanceof MethodCheckAdder)) return false;
        MethodCheckAdder<?> methodExtractor = (MethodCheckAdder<?>) extractor;
        if (!Objects.equals(name, methodExtractor.nameForReport)) return false;
        if (!Objects.equals(method, methodExtractor.method)) return false;
        if (!Arrays.equals(arguments, methodExtractor.arguments)) return false;
        return true;
    }

    private boolean isExtractorForLambdajProperty(ValueCheckAdderImpl<?> extractor, String name, Argument<?> argument) {
        if (!(extractor instanceof LambdajPropertyCheckAdder)) return false;
        LambdajPropertyCheckAdder<?> methodExtractor = (LambdajPropertyCheckAdder<?>) extractor;
        if (!Objects.equals(name, methodExtractor.nameForReport)) return false;
        if (!Objects.equals(argument, methodExtractor.argument)) return false;
        return true;
    }



    private abstract class ValueCheckAdderImpl <V> implements ValueCheckAdder<V, U>, CompositeCheckAddingController {
        private final Collection<Matcher<? super V>> matchers = new ArrayList<>();

        @Override
        public U is(Matcher<? super V> matcher) {
            matchers.add(matcher);
            return (U) ObjectMatcherForExtending.this;
        }

        @SafeVarargs
        @Override
        public final U is(Matcher<? super V>... matchers) {
            Collections.addAll(this.matchers, matchers);
            return (U) ObjectMatcherForExtending.this;
        }

        @Override
        public U is(Collection<? extends Matcher<? super V>> matchers) {
            this.matchers.addAll(matchers);
            return (U) ObjectMatcherForExtending.this;
        }

        @Override
        public U returns(Matcher<? super V> matcher) {
            matchers.add(matcher);
            return (U) ObjectMatcherForExtending.this;
        }

        @SafeVarargs
        @Override
        public final U returns(Matcher<? super V>... matchers) {
            Collections.addAll(this.matchers, matchers);
            return (U) ObjectMatcherForExtending.this;
        }

        @Override
        public U returns(Collection<? extends Matcher<? super V>> matchers) {
            this.matchers.addAll(matchers);
            return (U) ObjectMatcherForExtending.this;
        }


        @Override
        public void runChecks(Checker checker) {
            for (Matcher<? super V> matcher : matchers) {
                checker.matcher(matcher);
            }
        }
    }


    private class FieldCheckAdder <V> extends ValueCheckAdderImpl<V> {
        final String nameForReport;
        final Field field;

        FieldCheckAdder(String nameForReport, Field field) {
            this.nameForReport = nameForReport;
            this.field = field;
        }

        @Override
        public boolean isCheckerForTheSameValue(Checker check) {
            return false;
        }

        @Override
        public Checker create(Object valueToExtractFrom) {
            Object value = null;
            Exception extractionException = null;
            try {
                value = readField(field, valueToExtractFrom, true);
            } catch (IllegalAccessException e) {
                extractionException = e;
            }
            return new FieldChecker(nameForReport, value, NORMAL, extractionException, field);
        }
    }


    private class MethodCheckAdder <V> extends ValueCheckAdderImpl<V> {
        final String nameForReport;
        final Method method;
        final Object[] arguments;

        MethodCheckAdder(String nameForReport, Method method, Object... arguments) {
            this.nameForReport = nameForReport;
            this.method = method;
            this.arguments = arguments;
        }

        @Override
        public boolean isCheckerForTheSameValue(Checker check) {
            return false;
        }

        @Override
        public Checker create(Object valueToExtractFrom) {
            Object value = null;
            Exception extractionException = null;
            try {
                method.setAccessible(true);
                value = method.invoke(valueToExtractFrom, arguments);
            } catch (IllegalAccessException | InvocationTargetException e) {
                extractionException = e;
            }
            return new MethodChecker(nameForReport, value, NORMAL, extractionException, method, arguments);
        }
    }


    private class LambdajPropertyCheckAdder <V> extends ValueCheckAdderImpl<V> {
        final String nameForReport;
        final Argument<?> argument;

        LambdajPropertyCheckAdder(String nameForReport, Argument<?> argument) {
            this.nameForReport = nameForReport;
            this.argument = argument;
        }

        @Override
        public boolean isCheckerForTheSameValue(Checker check) {
            return false;
        }

        @Override
        public Checker create(Object valueToExtractFrom) {
            return new LambdajPropertyChecker(nameForReport, argument.evaluate(valueToExtractFrom), NORMAL, null, argument);
        }
    }


    private static class FieldChecker extends BaseChecker {
        final Field field;

        FieldChecker(@Nullable String name, @Nullable Object value, @NotNull ExtractionStatus extractionStatus,
                     @Nullable Exception extractionException, Field field) {
            super(name, value, extractionStatus, extractionException);
            this.field = field;
        }
    }


    private static class MethodChecker extends BaseChecker {
        final Method method;
        final Object[] arguments;

        MethodChecker(@Nullable String name, @Nullable Object value, @NotNull ExtractionStatus extractionStatus,
                     @Nullable Exception extractionException, Method method, Object... arguments) {
            super(name, value, extractionStatus, extractionException);
            this.method = method;
            this.arguments = arguments;
        }
    }


    private static class LambdajPropertyChecker extends BaseChecker {
        final Argument<?> argument;

        LambdajPropertyChecker(@Nullable String name, @Nullable Object value, @NotNull ExtractionStatus extractionStatus,
                     @Nullable Exception extractionException, Argument<?> argument) {
            super(name, value, extractionStatus, extractionException);
            this.argument = argument;
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
