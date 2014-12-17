package alkedr.matchers.reporting.reporters;

import alkedr.matchers.reporting.checks.ExecutedCompositeCheck;
import alkedr.matchers.reporting.checks.ExecutedSimpleCheck;

import java.util.Map;

import static alkedr.matchers.reporting.checks.ExecutedCheckStatus.FAILED;

public class PlainTextReporter<T> implements Reporter<T> {
    @Override
    public String reportCheck(T currentActualValue, ExecutedCompositeCheck check) {
        return generatePlainTextReport(null, "", check);
    }

    public static String generatePlainTextReport(String name, String indent, ExecutedCompositeCheck check) {
        if (check.getStatus() != FAILED) {
            return "";
        }
        return indent + (name == null ? "" : name + ": ") + (check.getInnerCompositeChecks().isEmpty() ? check.getActualValueAsString() : "") + "\n"
                + generateSimpleChecksReport(indent + "  ", check)
                + generateInnerCompositeChecksReport(indent + "  ", check);
    }

    private static String generateSimpleChecksReport(String indent, ExecutedCompositeCheck check) {
        StringBuilder stringBuilder = new StringBuilder();
        for (ExecutedSimpleCheck simpleCheck : check.getSimpleChecks()) {
            if (simpleCheck.getStatus() == FAILED) {
                stringBuilder
                        .append(indent)
                        .append(simpleCheck.getMatcherDescription())   // TODO: append indent to every line
                        .append("\n")
                        .append(indent)
                        .append(simpleCheck.getMismatchDescription())
                        .append("\n");
            }
        }
        return stringBuilder.toString();
    }

    private static String generateInnerCompositeChecksReport(String indent, ExecutedCompositeCheck check) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, ExecutedCompositeCheck> entry : check.getInnerCompositeChecks().entrySet()) {
            stringBuilder.append(generatePlainTextReport(entry.getKey(), indent, entry.getValue()));
        }
        return stringBuilder.toString();
    }
}