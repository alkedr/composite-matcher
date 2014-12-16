package alkedr.matchers.reporting.reporters;

import alkedr.matchers.reporting.checks.ExecutedCompositeCheck;
import alkedr.matchers.reporting.checks.ExecutedSimpleCheck;

import java.util.Map;
import java.util.Scanner;

import static alkedr.matchers.reporting.checks.ExecutedCheckStatus.FAILED;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringEscapeUtils.*;

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
                "<body>" + generateExecutedCompositeCheckReport(null, check) + "</body>" +
                "</html>";
    }

    private static String generateExecutedCompositeCheckReport(String name, ExecutedCompositeCheck check) {
        return  "<div class='node " + check.getStatus() + "'>" +
                    "<div class='name-value'>" +
                        (name == null ? "" : "<span class='name'>" + name + "</span>") +
                        "<span class='value'>" + escapeHtml4(check.getActualValueAsString()) + "</span>" +
                    "</div>" +
                    "<div class='checks'>" +
                        generateMatchersTable(check) +
                        generateInnerNodesDiv(check) +
                    "</div>" +
                "</div>";
    }

    private static String generateMatchersTable(ExecutedCompositeCheck check) {
        StringBuilder stringBuilder = new StringBuilder("<table class='matchers' style='border-spacing: 0px;'>");
        for (ExecutedSimpleCheck simpleCheck : check.getSimpleChecks()) {
            if (simpleCheck.getStatus() == FAILED) {
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
        for (Map.Entry<String, ExecutedCompositeCheck> entry : check.getInnerCompositeChecks().entrySet()) {
            stringBuilder.append(generateExecutedCompositeCheckReport(entry.getKey(), entry.getValue()));
        }
        stringBuilder.append("</div>");
        return stringBuilder.toString();
    }

    private static String resourceAsString(String resourceName) {
        return new Scanner(HtmlReporter.class.getResourceAsStream(resourceName), "UTF-8").useDelimiter("\\A").next();
    }
}
