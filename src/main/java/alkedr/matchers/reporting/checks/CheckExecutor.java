package alkedr.matchers.reporting.checks;

import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * User: alkedr
 * Date: 29.12.2014
 */
public class CheckExecutor<T> {
    @NotNull private final ExtractedValue extractedValue;
    private boolean isSuccessful = true;
    @NotNull private final List<ExecutedSimpleCheck> simpleChecks = new ArrayList<>();
    @NotNull private final List<ExecutedCompositeCheck> compositeChecks = new ArrayList<>();


    public CheckExecutor(@NotNull ExtractedValue extractedValue) {
        this.extractedValue = extractedValue;
    }


    public boolean checkThat(Matcher<? super T> matcher) {
        return checkAndReportIf(matcher, null);
    }

    public boolean checkAndReportIfMatches(Matcher<? super T> matcher) {
        return checkAndReportIf(matcher, true);
    }

    public boolean checkAndReportIfDoesntMatch(Matcher<? super T> matcher) {
        return checkAndReportIf(matcher, false);
    }

    public void addCompositeCheck(ExecutedCompositeCheck check) {
        compositeChecks.add(check);
        isSuccessful &= check.isSuccessful();
    }

    public void addDataFrom(ExecutedCompositeCheck check) {
        simpleChecks.addAll(check.getSimpleChecks());
        compositeChecks.addAll(check.getCompositeChecks());
    }


    public ExecutedCompositeCheck buildCompositeCheck() {
        // TODO: объединить проверки с одинаковым именем, переименовать проверки с одинаковым именем и значением
        return new ExecutedCompositeCheck(extractedValue, isSuccessful, simpleChecks, compositeChecks);
    }


    private boolean checkAndReportIf(Matcher<? super T> matcher, Boolean matcherResultThatGoesToReport) {
        INNER_CHECK_RESULT.remove();
        boolean matcherResult = matcher.matches(extractedValue.getValue());
        if (matcherResultThatGoesToReport == null || matcherResultThatGoesToReport == matcherResult) {
            if (INNER_CHECK_RESULT.get() != null && INNER_CHECK_RESULT.get().getExtractedValue().getValue() == extractedValue.getValue()) {
                addDataFrom(INNER_CHECK_RESULT.get());
            } else {
                simpleChecks.add(new ExecutedSimpleCheck(StringDescription.toString(matcher),
                        matcherResult ? null : getMismatchDescription(matcher, extractedValue.getValue())));
            }
        }
        isSuccessful &= matcherResult;
        return matcherResult;
    }

    private static String getMismatchDescription(Matcher<?> matcher, Object actualValue) {
        StringDescription stringMismatchDescription = new StringDescription();
        matcher.describeMismatch(actualValue, stringMismatchDescription);
        return stringMismatchDescription.toString();
    }

    /**
     * хранит информацию о выполнении другого ReportingMatcher'а, которое было вызвано из текущего ReportingMatcher'а
     * нужно для того, чтобы присоединить отчёт о проверках внутреннего ReportingMatcher'а к отчёту
     * зануляем INNER_CHECK_RESULT и вызываем matcher.matches()
     * если после этого INNER_CHECK_RESULT не нулл, значит matcher является ReportingMatcher'ом или использует ReportingMatcher внутри
     * нельзя просто попытаться покастить matcher к ReportingMatcher'у, т. к. бывают обёртки для матчеров (напр. describedAs())
     */
    public static final ThreadLocal<ExecutedCompositeCheck> INNER_CHECK_RESULT = new ThreadLocal<>();
}
