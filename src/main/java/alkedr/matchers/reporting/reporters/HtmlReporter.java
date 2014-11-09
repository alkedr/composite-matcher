package alkedr.matchers.reporting.reporters;

import alkedr.matchers.reporting.checks.CheckResult;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.Scanner;

import static ch.lambdaj.Lambda.join;
import static java.lang.String.*;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringEscapeUtils.*;

public final class HtmlReporter {
    private HtmlReporter() {}

    public static String generateHtmlReport(CheckResult checkResult) {
        return "<!DOCTYPE html><html>" + head() + "<body class=\"cm-diff-delta\">" + checkResultHtml(checkResult, "") + "</body></html>";
    }

    private static String head() {
        return "<head><style type=\"text/css\">" + resourceAsString("/styles.css") + "</style></head>";
    }

    private static String resourceAsString(String fileName) {
        return new Scanner(HtmlReporter.class.getResourceAsStream(fileName), "UTF-8").useDelimiter("\\A").next();
    }

    private static String checkResultHtml(CheckResult checkResult, String indent) {
        return indent + span(
                checkName(checkResult) + ": " + checkValue(checkResult) + checkMatchers(checkResult, indent + "  "),
                "cm-check"
        ) + "\n";
    }

    private static String checkName(CheckResult checkResult) {
        return span(escapeHtml4(checkResult.getActualValueName()), "cm-check-name");
    }

    private static String checkValue(CheckResult check) {
        return span(escapeHtml4(check.getActualValueAsString()), checkValueClass(check));
    }

    private static String checkValueClass(CheckResult check) {
        if (!check.hasAtLeastOneMatcher()) {
            return "cm-check-without-matchers";
        }
        if (!check.isLeaf()) {
            return "cm-check-value";
        }
        return check.isSuccessful() ? "cm-check-value-actual-correct" : "cm-check-value-actual-wrong";
    }

    private static String checkMatchers(CheckResult check, String indent) {
        if (check.isLeaf()) {
            if (check.getMatcherDescription() == null) {
                return "";
            }
            if (check.getMismatchDescription() == null) {
                return " " + span(format("(%s)", escapeHtml4(check.getMatcherDescription())), "cm-check-matcher-description");
            }
            return format("\n%sExpected: %s\n%s     but: %s",
                    indent, escapeHtml4(check.getMatcherDescription()),
                    indent, escapeHtml4(check.getMismatchDescription())
            );
        } else {
            StringBuilder sb = new StringBuilder("\n");
            for (CheckResult fieldCheck : check.getFields()) {
                sb.append(checkResultHtml(fieldCheck, indent));
            }
            for (CheckResult nonFieldCheck : check.getNonFields()) {
                sb.append(checkResultHtml(nonFieldCheck, indent));
            }
            return sb.toString();
        }
    }

    private static String span(String body, String... styles) {
        return format("<span class=\"%s\">%s</span>", join(asList(styles), " "), body);
    }
}
