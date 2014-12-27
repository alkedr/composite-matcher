package alkedr.matchers.reporting.reporters;

import alkedr.matchers.reporting.checks.ExecutedCompositeCheck;
import alkedr.matchers.reporting.checks.ExecutedSimpleCheck;

import java.util.Map;

import static alkedr.matchers.reporting.checks.ExecutedCheckStatus.FAILED;

public class PlainTextReporter<T> implements Reporter {
    @Override
    public String reportCheck(ExecutedCompositeCheck check) {
        return generatePlainTextReport(null, "", check);
    }

    public static String generatePlainTextReport(String name, String indent, ExecutedCompositeCheck check) {
        if (check.getStatus().isSuccessful()) {
            return "";
        }
        return indent + check.getStatus() + " " + (name == null ? "" : name + ": ") + (check.getCompositeChecks().isEmpty() ? String.valueOf(check.getActualValue()) : "") + "\n"
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
        for (Map.Entry<String, Map<Object, ExecutedCompositeCheck>> nameToValuesChecks : check.getCompositeChecks().entrySet()) {
            for (Map.Entry<Object, ExecutedCompositeCheck> valueToCheck : nameToValuesChecks.getValue().entrySet()) {
                stringBuilder.append(generatePlainTextReport(nameToValuesChecks.getKey(), indent, valueToCheck.getValue()));
            }
        }
        return stringBuilder.toString();
    }
}