package alkedr.matchers.reporting.reporters;

import alkedr.matchers.reporting.checks.CheckResult;

import java.util.Scanner;

public final class HtmlReporter {
    private HtmlReporter() {}

    public static String generateHtmlReport(CheckResult checkResult) {
        return "";
//        return new Scanner(HtmlReporter.class.getResourceAsStream("foo.txt"), "UTF-8").useDelimiter("\\A").next()
//                .replace("%CHECK_RESULT%", generateCheckResultHtml(checkResult))
//                ;
    }

    private static String generateCheckResultHtml(CheckResult checkResult) {
        if (checkResult.getFields().size() + checkResult.getNonFields().size() > 0) {
            return generateCheckResultWithInnerChecksHtml(checkResult);
        } else {
            return generateCheckResultWithoutInnerChecksHtml(checkResult);
        }
    }

    private static String generateCheckResultWithInnerChecksHtml(CheckResult checkResult) {
        return null;
    }

    private static String generateCheckResultWithoutInnerChecksHtml(CheckResult checkResult) {
        return null;
    }
}
