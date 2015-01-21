package com.github.alkedr.matchers.reporting.reporters;

import com.github.alkedr.matchers.reporting.checks.ExecutedCheck;
import com.github.alkedr.matchers.reporting.checks.ExecutedCompositeCheck;
import com.github.alkedr.matchers.reporting.checks.ExecutedSimpleCheck;

import java.util.Scanner;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

// TODO: report getExtractedValue().getThrowable()
public class HtmlReporter implements Reporter {
    @Override
    public String report(ExecutedCompositeCheck check) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("<!DOCTYPE html>")
                .append("<html>")
                .append("<head>")
                    .append("<meta charset='UTF-8'>")
                    .append("<style type='text/css'>").append(resourceAsString("/report.css")).append("</style>")
                    .append("<script>").append(resourceAsString("/report.js")).append("</script>")
                .append("</head>")
                .append("<body>")
                ;
        appendExecutedCompositeCheckReport(check, stringBuilder);
        stringBuilder
                .append("</body>")
                .append("</html>")
                ;
        return stringBuilder.toString();
    }

    private static void appendExecutedCompositeCheckReport(ExecutedCompositeCheck check, StringBuilder stringBuilder) {
        stringBuilder
                .append("<div class='node ")
                    .append(check.getExtractionStatus().toString().toLowerCase())
                    .append(" ")
                    .append(check.getStatus().toString().toLowerCase())
                .append("'>")
                ;
        appendNameValue(check, stringBuilder);
        if (!check.getSimpleChecks().isEmpty() || !check.getCompositeChecks().isEmpty()) {
            stringBuilder.append("<div class='checks'>");
            appendMatchersTable(check, stringBuilder);
            appendInnerNodesDiv(check, stringBuilder);
            stringBuilder.append("</div>");
        }
        stringBuilder.append("</div>");
    }

    private static void appendNameValue(ExecutedCompositeCheck check, StringBuilder stringBuilder) {
        stringBuilder.append("<div class='name-value'>");
        if (check.getName() != null) {
            stringBuilder
                    .append("<span class='name'>")
                    .append(escapeHtml4(check.getName()))
                    .append("</span>");
        }
        if (check.getCompositeChecks().isEmpty()) {
            stringBuilder
                    .append("<span class='value'>")
                    .append(escapeHtml4(String.valueOf(check.getValue())))
                    .append("</span>");
        }
        stringBuilder.append("</div>");
    }

    private static void appendMatchersTable(ExecutedCompositeCheck check, StringBuilder stringBuilder) {
        if (check.getSimpleChecks().isEmpty()) return;
        stringBuilder.append("<table class='matchers' style='border-spacing: 0px;'>");
        for (ExecutedSimpleCheck simpleCheck : check.getSimpleChecks()) {
            if (simpleCheck.getStatus() == ExecutedCheck.Status.FAILED) {
                stringBuilder
                        .append("<tr class='matcher failed'><td class='image'>×</td><td class='description'>")
                        .append(escapeHtml4(simpleCheck.getMatcherDescription()))
                        .append("</td></tr>")
                        .append("<tr class='matcher failed'><td></td><td class='mismatch-description'>")
                        .append(escapeHtml4(simpleCheck.getMismatchDescription()))
                        .append("</td></tr>");
            } else {
                stringBuilder
                        .append("<tr class='matcher passed'><td class='image'>✔</td><td class='description'>")
                        .append(escapeHtml4(simpleCheck.getMatcherDescription()))
                        .append("</td></tr>");
            }
        }
        stringBuilder.append("</table>");
    }

    private static void appendInnerNodesDiv(ExecutedCompositeCheck check, StringBuilder stringBuilder) {
        if (check.getCompositeChecks().isEmpty()) return;
        stringBuilder.append("<div class='inner-nodes'>");
        for (ExecutedCompositeCheck innerCheck : check.getCompositeChecks()) {
            appendExecutedCompositeCheckReport(innerCheck, stringBuilder);
        }
        stringBuilder.append("</div>");
    }

    private static String resourceAsString(String resourceName) {
        return new Scanner(HtmlReporter.class.getResourceAsStream(resourceName), "UTF-8").useDelimiter("\\A").next();
    }
}
