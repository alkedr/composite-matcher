package com.github.alkedr.matchers.reporting.reporters;

import com.github.alkedr.matchers.reporting.ReportingMatcher;
import org.hamcrest.Matcher;
import org.jetbrains.annotations.Nullable;

// TODO: report getExtractedValue().getException()
public class PlainTextReporter implements Reporter {
    @Nullable private Matcher<ReportingMatcher.ExecutedCheck> checkMatcher = null;

    @Override
    public String report(ReportingMatcher.ExecutedCompositeCheck check) {
        return generatePlainTextReport(check);
    }

    public PlainTextReporter checkMatcher(@Nullable Matcher<ReportingMatcher.ExecutedCheck> newCheckMatcher) {
        checkMatcher = newCheckMatcher;
        return this;
    }


    private String generatePlainTextReport(ReportingMatcher.ExecutedCompositeCheck check) {
        if (checkMatcher != null && checkMatcher.matches(check)) return "";
        return generateStatusNameValueString(check) + prependIndentToEveryLine("  ", generateSimpleChecksReport(check) + generateCompositeChecksReport(check));
    }

    private static String generateStatusNameValueString(ReportingMatcher.ExecutedCompositeCheck check) {
        return (check.getExtractionStatus() == ReportingMatcher.ExtractionStatus.NORMAL ? "" : check.getExtractionStatus().toString().toLowerCase() + " ") +
                check.getStatus().toString().toLowerCase() + " " +
                check.getName() + ":" +
                (check.getCompositeChecks().isEmpty() ? " " + check.getValue() : "") + "\n";
    }

    private String generateSimpleChecksReport(ReportingMatcher.ExecutedCompositeCheck check) {
        StringBuilder stringBuilder = new StringBuilder();
        for (ReportingMatcher.ExecutedSimpleCheck simpleCheck : check.getSimpleChecks()) {
            if (checkMatcher == null || checkMatcher.matches(simpleCheck)) {
                if (simpleCheck.getStatus() == ReportingMatcher.ExecutedCheck.Status.FAILED) {
                    stringBuilder
                            .append("Expected: ").append(simpleCheck.getMatcherDescription()).append("\n")
                            .append("     but: ").append(simpleCheck.getMismatchDescription()).append("\n");
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

    private String generateCompositeChecksReport(ReportingMatcher.ExecutedCompositeCheck check) {
        StringBuilder stringBuilder = new StringBuilder();
        for (ReportingMatcher.ExecutedCompositeCheck innerCheck : check.getCompositeChecks()) {
            stringBuilder.append(generatePlainTextReport(innerCheck));
        }
        return stringBuilder.toString();
    }

    private static String prependIndentToEveryLine(String indent, String s) {
        String t = indent + s.replace("\n", "\n" + indent);
        return t.endsWith(indent) ? t.substring(0, t.length()-indent.length()) : t;
    }
}