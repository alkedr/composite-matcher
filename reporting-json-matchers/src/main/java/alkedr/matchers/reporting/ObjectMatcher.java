package alkedr.matchers.reporting;

import alkedr.matchers.reporting.checkextractors.ExecutableCheckExtractor;
import alkedr.matchers.reporting.checks.ExecutableCheck;
import ch.lambdaj.function.argument.Argument;
import org.hamcrest.Matcher;
import org.jetbrains.annotations.NotNull;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

import static ch.lambdaj.function.argument.ArgumentsFactory.actualArgument;
import static java.beans.Introspector.getBeanInfo;
import static java.lang.reflect.Modifier.isStatic;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableCollection;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.equalTo;

/**
 * Два метода использования: построение с пом. fluent API и написание обёртки с определением своего fluent API
 * TODO: класс, упрощающий написание обёртки
 */
public class ObjectMatcher<T> extends ValueExtractingMatcher<T> {
    private final Collection<ExecutableCheckExtractor> executableCheckExtractors = new ArrayList<>();

    public ObjectMatcher(Class<T> tClass) {
        super(tClass);
    }


    public ObjectMatcher<T> includeUncheckedFields() {
        return addExecutableCheckExtractor(new ExecutableCheckExtractor() {
            @Override
            public Collection<ExecutableCheck> extract(Class<?> clazz, Object actual) {
                Collection<ExecutableCheck> result = new ArrayList<>();
                for (Field field : clazz.getFields()) {
                    if (!isStatic(field.getModifiers())) {
                        field.setAccessible(true);
                        try {
                            result.add(new ExecutableCheck(field.getName(), field.get(actual), new ArrayList<Matcher<?>>()));
                        } catch (IllegalAccessException ignored) {
                        }
                    }
                }
                return result;
            }
        });
    }

    public ObjectMatcher<T> includeUncheckedProperties() {
        return addExecutableCheckExtractor(new ExecutableCheckExtractor() {
            @Override
            public Collection<ExecutableCheck> extract(Class<?> clazz, Object actual) {
                Collection<ExecutableCheck> result = new ArrayList<>();
                try {
                    for (PropertyDescriptor pd : getBeanInfo(clazz).getPropertyDescriptors()) {
                        pd.getReadMethod().setAccessible(true);
                        result.add(new ExecutableCheck(pd.getName(), pd.getReadMethod().invoke(actual), new ArrayList<Matcher<?>>()));
                    }
                } catch (InvocationTargetException | IntrospectionException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                return result;
            }
        });
    }



    public <U> PropertyCheckAdder<U> property(String fieldNameForReport, U lambdajGetterMethodSelector) {
        return new PropertyCheckAdder<>(fieldNameForReport, lambdajGetterMethodSelector);
    }

    public <U> PropertyCheckAdder<U> property(U lambdajGetterMethodSelector) {
        Argument<U> argument = actualArgument(lambdajGetterMethodSelector);
        return property(argument.getInkvokedPropertyName(), lambdajGetterMethodSelector);
    }


    public FieldsCheckAdder fieldsWithoutGetters(Matcher<String> fieldNameMatcher) {
        return new FieldsCheckAdder(fieldNameMatcher);
    }


    public <U> FieldCheckAdder<U> fieldWithoutGetter(String fieldNameForReport, String fieldNameForValueExtraction) {
        return new FieldCheckAdder<>(fieldNameForReport, fieldNameForValueExtraction);
    }

    public <U> FieldCheckAdder<U> fieldWithoutGetter(String fieldNameForValueExtraction) {
        return fieldWithoutGetter(null, fieldNameForValueExtraction);
    }


    public ObjectMatcher<T> allFieldsAre(Matcher<? super Object> valueMatcher) {
        return fieldsWithoutGetters(any(String.class)).are(valueMatcher);
    }


    public ObjectMatcher<T> fieldsCountIs(final Matcher<? super Integer> valueMatcher) {
        return addExecutableCheckExtractor(new ExecutableCheckExtractor() {
            @Override
            public Collection<ExecutableCheck> extract(Class<?> clazz, Object actual) {
                int actualFieldsCount = 0;
                for (Field field : actual.getClass().getFields()) {
                    if (!isStatic(field.getModifiers())) {
                        actualFieldsCount++;
                    }
                }
                return asList(new ExecutableCheck("fields count", actualFieldsCount, asList(valueMatcher)));
            }
        });
    }

    public ObjectMatcher<T> fieldsCountIs(int value) {
        return fieldsCountIs(equalTo(value));
    }




    private ObjectMatcher<T> addExecutableCheckExtractor(ExecutableCheckExtractor extractor) {
        executableCheckExtractors.add(extractor);
        return this;
    }


    @NotNull
    @Override
    protected Collection<ExecutableCheckExtractor> getExecutableCheckExtractors(Class<?> clazz, Object actual) {
        return unmodifiableCollection(executableCheckExtractors);
    }



    public class PropertyCheckAdder<U> {
        private final String fieldNameForReport;
        private final U lambdajGetterMethodSelector;

        private PropertyCheckAdder(String fieldNameForReport, U lambdajGetterMethodSelector) {
            this.fieldNameForReport = fieldNameForReport;
            this.lambdajGetterMethodSelector = lambdajGetterMethodSelector;
        }

        public ObjectMatcher<T> is(U expectedValue) {
            return is(equalTo(expectedValue));
        }

        public ObjectMatcher<T> is(final Matcher<U> matcher) {
            return addExecutableCheckExtractor(new ExecutableCheckExtractor() {
                @Override
                public Collection<ExecutableCheck> extract(Class<?> clazz, Object actual) {
                    return asList(new ExecutableCheck(fieldNameForReport, actualArgument(lambdajGetterMethodSelector).evaluate(actual), asList(matcher)));
                }
            });
        }
    }


    public class FieldCheckAdder<U> {
        private final String fieldNameForReport;
        private final String fieldNameForValueExtraction;

        private FieldCheckAdder(String fieldNameForReport, String fieldNameForValueExtraction) {
            this.fieldNameForReport = fieldNameForReport;
            this.fieldNameForValueExtraction = fieldNameForValueExtraction;
        }

        public ObjectMatcher<T> is(U expectedValue) {
            return is(equalTo(expectedValue));
        }

        public ObjectMatcher<T> is(final Matcher<U> matcher) {
            return addExecutableCheckExtractor(new ExecutableCheckExtractor() {
                @Override
                public Collection<ExecutableCheck> extract(Class<?> clazz, Object actual) {
                    try {
                        Field field = actual.getClass().getField(fieldNameForValueExtraction);
                        field.setAccessible(true);
                        return asList(new ExecutableCheck(fieldNameForReport, field.get(actual), asList(matcher)));
                    } catch (NoSuchFieldException | IllegalAccessException ignored) {
                        // FIXME: В отчёте должно отобразиться, что поле не найдено
                        return null;
                    }
                }
            });
        }
    }


    public class FieldsCheckAdder {
        private final Matcher<String> fieldNameMatcher;

        private FieldsCheckAdder(Matcher<String> fieldNameMatcher) {
            this.fieldNameMatcher = fieldNameMatcher;
        }

        public ObjectMatcher<T> are(final Matcher<? super Object> matcher) {
            return addExecutableCheckExtractor(new ExecutableCheckExtractor() {
                @Override
                public Collection<ExecutableCheck> extract(Class<?> clazz, Object actual) {
                    Collection<ExecutableCheck> result = new ArrayList<>();
                    for (Field field : actual.getClass().getFields()) {
                        field.setAccessible(true);
                        if (!isStatic(field.getModifiers()) && fieldNameMatcher.matches(field.getName())) {
                            try {
                                result.add(new ExecutableCheck(field.getName(), field.get(actual), asList(matcher)));
                            } catch (IllegalAccessException ignored) {
                            }
                        }
                    }
                    return result;
                }
            });
        }
    }
}
