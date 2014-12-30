package alkedr.matchers.reporting.reporters;

import alkedr.matchers.reporting.checks.ExecutedCheck;
import alkedr.matchers.reporting.checks.ExecutedCompositeCheck;
import alkedr.matchers.reporting.checks.ExecutedSimpleCheck;

import java.util.Scanner;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

// TODO: report getExtractedValue().getThrowable()
public class HtmlReporter implements Reporter {
    @Override
    public String reportCheck(ExecutedCompositeCheck check) {
        return  "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                    "<meta charset='UTF-8'>" +
                    "<style type='text/css'>" + resourceAsString("/report.css") + "</style>" +
                    "<script>" + resourceAsString("/report.js") + "</script>" +
                "</head>" +
                "<body>" + generateExecutedCompositeCheckReport(check) + "</body>" +
                "</html>";
    }

    private static String generateExecutedCompositeCheckReport(ExecutedCompositeCheck check) {
        return  "<div class='node " + check.getExtractedValue().getStatus().toString().toLowerCase() + " " + check.getStatus().toString().toLowerCase() + "'>" +
                    generateNameValue(check) +
                    "<div class='checks'>" +
                        generateMatchersTable(check) +
                        generateInnerNodesDiv(check) +
                    "</div>" +
                "</div>";
    }

    private static String generateNameValue(ExecutedCompositeCheck check) {
        StringBuilder stringBuilder = new StringBuilder("<div class='name-value'>");
        stringBuilder.append("<span class='name'>").append(check.getExtractedValue().getName()).append("</span>");
        if (check.getCompositeChecks().isEmpty()) {
            stringBuilder.append("<span class='value'>").append(escapeHtml4(String.valueOf(check.getExtractedValue().getValue()))).append("</span>");
        }
        stringBuilder.append("</div>");
        return stringBuilder.toString();
    }

    private static String generateMatchersTable(ExecutedCompositeCheck check) {
        StringBuilder stringBuilder = new StringBuilder("<table class='matchers' style='border-spacing: 0px;'>");
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
        return stringBuilder.toString();
    }

    private static String generateInnerNodesDiv(ExecutedCompositeCheck check) {
        StringBuilder stringBuilder = new StringBuilder("<div class='inner-nodes'>");
        for (ExecutedCompositeCheck innerCheck : check.getCompositeChecks()) {
            stringBuilder.append(generateExecutedCompositeCheckReport(innerCheck));
        }
        stringBuilder.append("</div>");
        return stringBuilder.toString();
    }

    private static String resourceAsString(String resourceName) {
        return new Scanner(HtmlReporter.class.getResourceAsStream(resourceName), "UTF-8").useDelimiter("\\A").next();
    }
}
