package alkedr.matchers.reporting.reporters;

import alkedr.matchers.reporting.checks.ExecutedCompositeCheck;
import alkedr.matchers.reporting.checks.ExecutedSimpleCheck;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Scanner;

import static ch.lambdaj.Lambda.join;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

public final class HtmlReporter {
    private HtmlReporter() {}

    public static String generateHtmlReport(ExecutedCompositeCheck checkResult) {
        return "<!DOCTYPE html><html>" + head() + "<body class=\"cm-diff-delta\">" + executedCompositeCheckReport(null, checkResult, "") + "</body></html>";
    }

    private static String executedCompositeCheckReport(@Nullable String name, ExecutedCompositeCheck check, String indent) {
        StringBuilder sb = new StringBuilder();
        if (name != null) {
            sb.append(checkName(name)).append(": ");
        }
        sb.append(checkValue(check)).append("\n");
        for (ExecutedSimpleCheck simpleCheck : check.getInnerSimpleChecks()) {
            sb.append(executedSimpleCheckReport(simpleCheck, indent + "  "));
        }
        for (Map.Entry<String, ExecutedCompositeCheck> entry: check.getValueNameToInnerCompositeCheck().entrySet()) {
            sb.append(executedCompositeCheckReport(entry.getKey(), entry.getValue(), indent + "  "));
        }
        return indent + div(sb.toString(), "cm-check") + "\n";
    }

    private static String executedSimpleCheckReport(ExecutedSimpleCheck check, String indent) {
        if (check.getMatcherDescription() == null) {
            return "";
        }
        if (check.getMismatchDescription() == null) {
            return format("%sExpected: %s", indent, escapeHtml4(check.getMatcherDescription()));
        }
        return format("%sExpected: %s\n%s     but: %s",
                indent, escapeHtml4(check.getMatcherDescription()),
                indent, escapeHtml4(check.getMismatchDescription())
        );
    }


    private static String head() {
        return "<head><style type=\"text/css\">" + resourceAsString("/styles.css") + "</style></head>";
    }

    private static String resourceAsString(String fileName) {
        return new Scanner(HtmlReporter.class.getResourceAsStream(fileName), "UTF-8").useDelimiter("\\A").next();
    }

    private static String checkName(String checkResult) {
        return span(escapeHtml4(checkResult), "cm-check-name");
    }

    private static String checkValue(ExecutedCompositeCheck check) {
        return span(escapeHtml4(check.getActualValueAsString()), checkValueClass(check));
    }

    private static String checkValueClass(ExecutedCompositeCheck check) {
        if (!check.hasAtLeastOneMatcher()) {
            return "cm-check-without-matchers";
        }
        if (!check.isLeaf()) {
            return "cm-check-value";
        }
        return check.isSuccessful() ? "cm-check-value-actual-correct" : "cm-check-value-actual-wrong";
    }

    private static String span(String body, String... styles) {
        return tag("span", body, styles);
    }

    private static String div(String body, String... styles) {
        return tag("div", body, styles);
    }

    private static String tag(String tagName, String body, String... styles) {
        return format("<%s class=\"%s\">%s</%s>", tagName, join(asList(styles), " "), body, tagName);
    }
}
