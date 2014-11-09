package alkedr.matchers.reporting.reporters;

import alkedr.matchers.reporting.checks.CheckResult;

import static java.lang.String.format;

public final class PlainTextReporter {
    private PlainTextReporter() {}

    public static String generatePlainTextReport(CheckResult checkResult) {
        return generatePlainTextReport(checkResult, "");
    }


    private static String generatePlainTextReport(CheckResult check, String indent) {
        return indent + status(check) + " - " + check.getActualValueName() + ": " + check.getActualValueAsString()
                + matcherResults(check, indent + "  ");
    }

    private static String status(CheckResult check) {
        if (!check.hasAtLeastOneMatcher()) {
            return "UNCHECKED";
        }
        return check.isSuccessful() ? "OK" : "FAIL";
    }

    private static String matcherResults(CheckResult check, String indent) {
        if (check.isLeaf()) {
            if (check.getMatcherDescription() == null) {
                return "\n";
            }
            if (check.getMismatchDescription() == null) {
                return format(" - %s\n", check.getMatcherDescription());
            }
            return format("\n%sExpected: %s\n%s     but: %s\n", indent, check.getMatcherDescription(), indent, check.getMismatchDescription());
        } else {
            StringBuilder sb = new StringBuilder("\n");
            for (CheckResult fieldCheck : check.getFields()) {
                sb.append(generatePlainTextReport(fieldCheck, indent));
            }
            for (CheckResult nonFieldCheck : check.getNonFields()) {
                sb.append(generatePlainTextReport(nonFieldCheck, indent));
            }
            return sb.toString();
        }
    }
}