package alkedr.matchers.reporting.reporters;

import alkedr.matchers.reporting.checks.ExecutedCompositeCheck;
import alkedr.matchers.reporting.checks.ExecutedSimpleCheck;

import java.util.Scanner;

import static ch.lambdaj.Lambda.join;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

public final class HtmlReporter {
    private HtmlReporter() {}

    public static String generateHtmlReport(ExecutedCompositeCheck checkResult) {
        return "<!DOCTYPE html><html>" + head() + "<body class=\"cm-diff-delta\">" + checkMatchers(checkResult, "") + "</body></html>";
    }


    private static String checkMatchers(ExecutedCompositeCheck check, String indent) {
        StringBuilder sb = new StringBuilder();
        for (ExecutedSimpleCheck simpleCheck : check.getInnerSimpleChecks()) {
            sb.append(checkResultHtml(simpleCheck, indent));
        }
        for (ExecutedCompositeCheck compositeCheck : check.getInnerCompositeChecks()) {
            sb.append(checkResultHtml(compositeCheck, indent));
        }
        return sb.toString();
    }

    private static String checkResultHtml(ExecutedSimpleCheck check, String indent) {
        if (check.getMatcherDescription() == null) {
            return "";
        }
        if (check.getMismatchDescription() == null) {
            return format("%sExpected: %s",
                    indent, escapeHtml4(check.getMatcherDescription())
            );
//            return " " + span(format("(%s)", escapeHtml4(check.getMatcherDescription())), "cm-check-matcher-description");
        }
        return format("%sExpected: %s\n%s     but: %s",
                indent, escapeHtml4(check.getMatcherDescription()),
                indent, escapeHtml4(check.getMismatchDescription())
        );
    }

    private static String checkResultHtml(ExecutedCompositeCheck check, String indent) {
        return indent + span(
                checkName(check) + ": " + checkValue(check) + "\n" + checkMatchers(check, indent + "  "),
                "cm-check"
        ) + "\n";
    }


    private static String head() {
        return "<head><style type=\"text/css\">" + resourceAsString("/styles.css") + "</style></head>";
    }

    private static String resourceAsString(String fileName) {
        return new Scanner(HtmlReporter.class.getResourceAsStream(fileName), "UTF-8").useDelimiter("\\A").next();
    }

    private static String checkName(ExecutedCompositeCheck checkResult) {
        return span(escapeHtml4(checkResult.getActualValueName()), "cm-check-name");
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
        return format("<span class=\"%s\">%s</span>", join(asList(styles), " "), body);
    }
}
