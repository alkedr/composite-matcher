package com.github.alkedr.matchers.reporting;

import org.hamcrest.Matcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

import static ch.lambdaj.Lambda.argument;
import static com.github.alkedr.matchers.reporting.ReportingMatcher.ExtractionStatus.*;
import static org.apache.commons.lang3.reflect.FieldUtils.readField;
import static org.apache.commons.lang3.reflect.MethodUtils.invokeMethod;

// TODO: поддерживать вычисление выражений, например fieldX.methodY().listFieldZ.get(1) ?
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

    // TODO: method(lambdajPlaceholder), как property, только в названии полное имя метода с параметрами
    // TODO: objectMatcher.expect(equalTo(42)).getAnswer();


    public <V> ValueCheckAdder<V> property(V lambdajPlaceholder) {
        return property(argument(lambdajPlaceholder).getInkvokedPropertyName(), lambdajPlaceholder);
    }

    public <V> ValueCheckAdder<V> property(String nameForReport, V lambdajPlaceholder) {
        clear();
        this.propertyNameForReport = nameForReport;
        this.propertyLambdajPlaceholder = lambdajPlaceholder;
        return (ValueCheckAdder<V>) valueCheckAdder;
    }



    public class ValueCheckAdder<V> {
        public final U is(Matcher<?>... matchers) {
            return addPlannedCheck(getPlannedCheck(matchers));
        }

        public U is(Collection<? extends Matcher<? super V>> matchers) {
            return addPlannedCheck(getPlannedCheck(matchers));
        }

        public final U returns(Matcher<?>... matchers) {
            return is(matchers);
        }

        public U returns(Collection<? extends Matcher<? super V>> matchers) {
            return is(matchers);
        }
    }


    private PlannedCheck<T> getPlannedCheck(Object matchers) {
        if (fieldNameForReport != null) {
            if (fieldNameForExtraction != null) return getFieldNamePlannedCheck(fieldNameForReport, fieldNameForExtraction, matchers);
            if (fieldValueExtractor != null) return getFieldExtractorPlannedCheck(fieldNameForReport, fieldValueExtractor, matchers);
            throw new RuntimeException("fieldNameForReport is not null but both fieldNameForExtraction and fieldValueExtractor are null");
        }
        if (methodNameForReport != null) {
            if (methodNameForExtraction != null && methodArguments != null) {
                return getMethodNamePlannedCheck(methodNameForReport, methodNameForExtraction, methodArguments, matchers);
            }
            if (methodReturnValueExtractor != null) return getMathodExtractorPlannedCheck(methodNameForReport, methodReturnValueExtractor, matchers);
            throw new RuntimeException("methodNameForReport is not null but one of methodNameForExtraction and methodArguments and methodReturnValueExtractor are null");
        }
        if (propertyNameForReport != null) {
            if (propertyLambdajPlaceholder != null) return getLambdajPropertyPlannedCheck(propertyNameForReport, propertyLambdajPlaceholder, matchers);
            throw new RuntimeException("propertyNameForReport is not null but propertyLambdajPlaceholder is null");
        }
        throw new RuntimeException("fieldNameForReport, methodNameForReport and propertyNameForReport are null");
    }


    private static <T> PlannedCheck<T> getFieldNamePlannedCheck(final String fieldNameForReport, final String fieldNameForExtraction, final Object matchers) {
        return new PlannedCheck<T>() {
            @Override
            public void execute(@NotNull Class<?> itemClass, @Nullable T item, @NotNull ExecutedCompositeCheckBuilder checker) {
                if (item == null) {
                    checker.subcheck().name(fieldNameForReport).extractionStatus(MISSING);
                } else {
                    try {
                        checker.subcheck().name(fieldNameForReport).value(readField(item, fieldNameForExtraction, true)).runMatchersObject(matchers);
                    } catch (Exception e) {
                        checker.subcheck().name(fieldNameForReport).extractionStatus(ERROR).extractionException(e);
                    }
                }
            }
        };
    }

    private static <T> PlannedCheck<T> getFieldExtractorPlannedCheck(final String fieldNameForReport, final ValueExtractor<T> fieldValueExtractor, final Object matchers) {
        return new PlannedCheck<T>() {
            @Override
            public void execute(@NotNull Class<?> itemClass, @Nullable T item, @NotNull ExecutedCompositeCheckBuilder checker) {
                fieldValueExtractor.extract(itemClass, item, checker).name(fieldNameForReport).runMatchersObject(matchers);
            }
        };
    }

    private static <T> PlannedCheck<T> getMethodNamePlannedCheck(final String methodNameForReport, final String methodNameForExtraction, final Object[] methodArguments, final Object matchers) {
        return new PlannedCheck<T>() {
            @Override
            public void execute(@NotNull Class<?> itemClass, @Nullable T item, @NotNull ExecutedCompositeCheckBuilder checker) {
                if (item == null) {
                    checker.subcheck().name(methodNameForReport).extractionStatus(MISSING);
                } else {
                    try {
                        checker.subcheck().name(methodNameForReport).value(invokeMethod(item, methodNameForExtraction, methodArguments)).runMatchersObject(matchers);
                    } catch (Exception e) {
                        checker.subcheck().name(methodNameForReport).extractionStatus(ERROR).extractionException(e);
                    }
                }
            }
        };
    }

    private static <T> PlannedCheck<T> getMathodExtractorPlannedCheck(final String methodNameForReport, final ValueExtractor<T> methodReturnValueExtractor, final Object matchers) {
        return new PlannedCheck<T>() {
            @Override
            public void execute(@NotNull Class<?> itemClass, @Nullable T item, @NotNull ExecutedCompositeCheckBuilder checker) {
                methodReturnValueExtractor.extract(itemClass, item, checker).name(methodNameForReport).runMatchersObject(matchers);
            }
        };
    }

    private static <T> PlannedCheck<T> getLambdajPropertyPlannedCheck(final String propertyNameForReport, final Object propertyLambdajPlaceholder, final Object matchers) {
        return new PlannedCheck<T>() {
            @Override
            public void execute(@NotNull Class<?> itemClass, @Nullable T item, @NotNull ExecutedCompositeCheckBuilder checker) {
                if (item == null) {
                    checker.subcheck().name(propertyNameForReport).extractionStatus(MISSING);
                } else {
                    try {
                        checker.subcheck().name(propertyNameForReport).value(argument(propertyLambdajPlaceholder).evaluate(item)).runMatchersObject(matchers);
                    } catch (Exception e) {
                        checker.subcheck().name(propertyNameForReport).extractionStatus(ERROR).extractionException(e);
                    }
                }
            }
        };
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


    private final ValueCheckAdder<?> valueCheckAdder = new ValueCheckAdder<Object>();

    @Nullable private String fieldNameForReport = null;
    @Nullable private String fieldNameForExtraction = null;
    @Nullable private ValueExtractor<T> fieldValueExtractor = null;

    @Nullable private String methodNameForReport = null;
    @Nullable private String methodNameForExtraction = null;
    @Nullable private Object[] methodArguments = null;
    @Nullable private ValueExtractor<T> methodReturnValueExtractor = null;

    @Nullable private String propertyNameForReport = null;
    @Nullable private Object propertyLambdajPlaceholder = null;
}
