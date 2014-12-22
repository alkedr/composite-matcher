package alkedr.matchers.reporting.reporters;

import alkedr.matchers.reporting.checks.ExecutedCompositeCheck;
import alkedr.matchers.reporting.checks.ExecutedSimpleCheck;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.hamcrest.Matchers.anything;

public class ReportBuilder {

    public void addUncheckedValue(String name, String value) {
        anything();
    }


    public void addSuccessfulSimpleCheck(@NotNull String matcherDescription) {
        addSimpleCheck(matcherDescription, null);
    }

    public void addFailedSimpleCheck(@NotNull String matcherDescription, @NotNull String mismatchDescription) {
        addSimpleCheck(matcherDescription, mismatchDescription);
    }

    public void addSimpleCheck(@NotNull String matcherDescription, @Nullable String mismatchDescription) {
        addSimpleCheck(new ExecutedSimpleCheck(matcherDescription, mismatchDescription));
    }

    public void addSimpleCheck(@NotNull ExecutedSimpleCheck check) {
        addCheck(null, check);
    }


    public void addSuccessfulValueCheck(@NotNull String name, @NotNull String matcherDescription) {
        addValueCheck(name, matcherDescription, null);
    }

    public void addFailedValueCheck(@NotNull String name, @NotNull String matcherDescription, @NotNull String mismatchDescription) {
        addValueCheck(name, matcherDescription, mismatchDescription);
    }

    public void addValueCheck(@NotNull String name, @NotNull String matcherDescription, @Nullable String mismatchDescription) {
        addCheck(name, matcherDescription, mismatchDescription);
    }





    public void addCheck(@Nullable String name, @NotNull String matcherDescription, @Nullable String mismatchDescription) {
    }

    public void addCheck(@Nullable String name, @NotNull ExecutedSimpleCheck executedSimpleCheck) {
    }

    public void addCheck(@Nullable String name, @NotNull ExecutedCompositeCheck executedCompositeCheck) {
    }



    /*
        Все значения должны быть добавлены с пом. addValue перед использованием
        непроверенное значение - addValue("fieldName", "fieldValue")
        проверенное правильное значение - addCheck("fieldName", "is correct", null)
        проверенное неправильное значение - addCheck("fieldName", "is correct", "was not correct")

        просто матчер - addCheck(null, "is correct", null) или addCheck(null, "is correct", "was not correct")

        отсутствующее значение - addCheck()
     */
}
