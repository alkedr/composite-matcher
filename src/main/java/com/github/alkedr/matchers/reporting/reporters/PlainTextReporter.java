package com.github.alkedr.matchers.reporting.reporters;

import com.github.alkedr.matchers.reporting.checks.ExecutedCheck;
import com.github.alkedr.matchers.reporting.checks.ExecutedCompositeCheck;
import com.github.alkedr.matchers.reporting.checks.ExecutedSimpleCheck;

// TODO: report getExtractedValue().getThrowable()
public class PlainTextReporter<T> implements Reporter {
    @Override
    public String reportCheck(ExecutedCompositeCheck check) {
        return generatePlainTextReport("", check);
    }

    public static String generatePlainTextReport(String indent, ExecutedCompositeCheck check) {
        if (check.getStatus() != ExecutedCheck.Status.FAILED) {
            return "";
        }
        return indent +
                check.getExtractedValue().getStatus().toString().toLowerCase() + " " +
                check.getStatus().toString().toLowerCase() + " " +
                check.getExtractedValue().getName() + ": " +
                (check.getCompositeChecks().isEmpty() ? String.valueOf(check.getExtractedValue().getValue()) : "") + "\n" +
                generateSimpleChecksReport(indent + "  ", check) +
                generateInnerCompositeChecksReport(indent + "  ", check);
    }

    private static String generateSimpleChecksReport(String indent, ExecutedCompositeCheck check) {
        StringBuilder stringBuilder = new StringBuilder();
        for (ExecutedSimpleCheck simpleCheck : check.getSimpleChecks()) {
            if (simpleCheck.getStatus() == ExecutedCheck.Status.FAILED) {
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
        for (ExecutedCompositeCheck innerCheck : check.getCompositeChecks()) {
            stringBuilder.append(generatePlainTextReport(indent, innerCheck));
        }
        return stringBuilder.toString();
    }
}