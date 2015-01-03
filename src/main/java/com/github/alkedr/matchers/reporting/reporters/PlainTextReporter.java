package com.github.alkedr.matchers.reporting.reporters;

import com.github.alkedr.matchers.reporting.checks.ExecutedCheck;
import com.github.alkedr.matchers.reporting.checks.ExecutedCompositeCheck;
import com.github.alkedr.matchers.reporting.checks.ExecutedSimpleCheck;
import com.github.alkedr.matchers.reporting.checks.ExtractedValue;
import org.hamcrest.Matcher;

// TODO: report getExtractedValue().getThrowable()
public class PlainTextReporter implements Reporter {
    private Matcher<ExecutedCheck> checkMatcher;

    @Override
    public String report(ExecutedCompositeCheck check) {
        return generatePlainTextReport(check);
    }

    private String generatePlainTextReport(ExecutedCompositeCheck check) {
        if (checkMatcher != null && checkMatcher.matches(check)) return "";
        return generateStatusNameValueString(check) + prependIndentToEveryLine("  ", generateSimpleChecksReport(check) + generateCompositeChecksReport(check));
    }

    private static String generateStatusNameValueString(ExecutedCompositeCheck check) {
        return (check.getExtractedValue().getStatus() == ExtractedValue.Status.NORMAL ? "" : check.getExtractedValue().getStatus().toString().toLowerCase() + " ") +
                check.getStatus().toString().toLowerCase() + " " +
                check.getExtractedValue().getName() + ":" +
                (check.getCompositeChecks().isEmpty() ? " " + check.getExtractedValue().getValue() : "") + "\n";
    }

    private String generateSimpleChecksReport(ExecutedCompositeCheck check) {
        StringBuilder stringBuilder = new StringBuilder();
        for (ExecutedSimpleCheck simpleCheck : check.getSimpleChecks()) {
            if (checkMatcher == null || checkMatcher.matches(simpleCheck)) {
                if (simpleCheck.getStatus() == ExecutedCheck.Status.FAILED) {
                    stringBuilder
                            .append("Expected: ")
                            .append(simpleCheck.getMatcherDescription())
                            .append("\n")
                            .append("     but: ")
                            .append(simpleCheck.getMismatchDescription())
                            .append("\n");
                } else {
                    stringBuilder
                            .append(simpleCheck.getStatus().toString().toLowerCase())
                            .append(" ")
                            .append(simpleCheck.getMatcherDescription())
                            .append("\n");
                }
            }
        }
        return stringBuilder.toString();
    }

    private String generateCompositeChecksReport(ExecutedCompositeCheck check) {
        StringBuilder stringBuilder = new StringBuilder();
        for (ExecutedCompositeCheck innerCheck : check.getCompositeChecks()) {
            stringBuilder.append(generatePlainTextReport(innerCheck));
        }
        return stringBuilder.toString();
    }

    private static String prependIndentToEveryLine(String indent, String s) {
        String t = indent + s.replace("\n", "\n" + indent);
        return t.endsWith(indent) ? t.substring(0, t.length()-indent.length()) : t;
    }
}