package alkedr.matchers.reporting;

import ch.lambdaj.function.argument.Argument;
import org.hamcrest.Matcher;
import org.jetbrains.annotations.NotNull;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.function.argument.ArgumentsFactory.actualArgument;
import static java.beans.Introspector.getBeanInfo;
import static java.lang.reflect.Modifier.isStatic;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.equalTo;

public class ObjectMatcher<T> extends ReportingMatcher<T> {
    private boolean includeUncheckedFields = true;
    private final List<PlannedCheckExtractor> plannedCheckExtractors = new ArrayList<>();



    public ObjectMatcher<T> includeUncheckedFields() {
        return includeUncheckedFields(true);
    }

    public ObjectMatcher<T> dontIncludeUncheckedFields() {
        return includeUncheckedFields(false);
    }

    public ObjectMatcher<T> includeUncheckedFields(boolean newValue) {
        includeUncheckedFields = newValue;
        return this;
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
        return addPlannedCheckExtractor(new PlannedCheckExtractor() {
            @Override
            public void addChecksToPlan(CheckPlan plan, Object actual) {
                int actualFieldsCount = 0;
                for (Field field : actual.getClass().getFields()) {
                    if (!isStatic(field.getModifiers())) {
                        actualFieldsCount++;
                    }
                }
                plan.addCheck("fields count", actualFieldsCount, valueMatcher);
            }
        });
    }

    public ObjectMatcher<T> fieldsCountIs(int value) {
        return fieldsCountIs(equalTo(value));
    }




    private ObjectMatcher<T> addPlannedCheckExtractor(PlannedCheckExtractor extractor) {
        plannedCheckExtractors.add(extractor);
        return this;
    }


    @NotNull
    @Override
    protected CheckPlan checkPlanFor(T actualValue) {
        CheckPlan plan = new CheckPlan();
        if (includeUncheckedFields) {
            addAllFields(plan, actualValue);
            addAllProperties(plan, actualValue);
        }
        for (PlannedCheckExtractor extractor : plannedCheckExtractors) {
            extractor.addChecksToPlan(plan, actualValue);
        }
        return plan;
    }


    private void addAllFields(CheckPlan plan, T actualValue) {
        for (Field field : actualValue.getClass().getFields()) {
            if (!isStatic(field.getModifiers())) {
                field.setAccessible(true);
                try {
                    plan.addCheck(field.getName(), field.get(actualValue));
                } catch (IllegalAccessException ignored) {
                }
            }
        }
    }

    private void addAllProperties(CheckPlan plan, T actualValue) {
        try {
            for (PropertyDescriptor pd : getBeanInfo(actualValue.getClass()).getPropertyDescriptors()) {
                pd.getReadMethod().setAccessible(true);
                plan.addCheck(pd.getName(), pd.getReadMethod().invoke(actualValue));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    private interface PlannedCheckExtractor {
        void addChecksToPlan(CheckPlan plan, Object actual);
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
            return addPlannedCheckExtractor(new PlannedCheckExtractor() {
                @Override
                public void addChecksToPlan(CheckPlan plan, Object actual) {
                    plan.addCheck(fieldNameForReport, actualArgument(lambdajGetterMethodSelector).evaluate(actual), matcher);
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
            return addPlannedCheckExtractor(new PlannedCheckExtractor() {
                @Override
                public void addChecksToPlan(CheckPlan plan, Object actual) {
                    try {
                        Field field = actual.getClass().getField(fieldNameForValueExtraction);
                        field.setAccessible(true);
                        plan.addCheck(fieldNameForReport, (U)field.get(actual), matcher);
                    } catch (NoSuchFieldException | IllegalAccessException ignored) {
                        // FIXME: В отчёте должно отобразиться, что поле не найдено
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
            return addPlannedCheckExtractor(new PlannedCheckExtractor() {
                @Override
                public void addChecksToPlan(CheckPlan plan, Object actual) {
                    for (Field field : actual.getClass().getFields()) {
                        field.setAccessible(true);
                        if (!isStatic(field.getModifiers()) && fieldNameMatcher.matches(field.getName())) {
                            try {
                                plan.addCheck(field.getName(), field.get(actual), matcher);
                            } catch (IllegalAccessException ignored) {
                            }
                        }
                    }
                }
            });
        }
    }
}
