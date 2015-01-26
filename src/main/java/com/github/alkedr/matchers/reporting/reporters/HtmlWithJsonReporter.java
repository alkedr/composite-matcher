package com.github.alkedr.matchers.reporting.reporters;

import com.github.alkedr.matchers.reporting.ReportingMatcher;

import java.io.StringWriter;
import java.util.Scanner;

import static com.github.alkedr.matchers.reporting.ReportingMatcher.ExtractionStatus.NORMAL;

/**
 * User: alkedr
 * Date: 23.01.2015
 */
public class HtmlWithJsonReporter implements Reporter {
    @Override
    public String report(ReportingMatcher.ExecutedCompositeCheck check) {
        StringWriter w = new StringWriter();
        w.write("<!DOCTYPE html>");
        w.write("<html>");
        w.write("<head>");
            w.write("<meta charset='UTF-8'>");
            w.write("<style type='text/css'>");
                w.write(resourceAsString("/report.css"));
            w.write("</style>");
            w.write("<script>");
                w.write("var checks=");
                writeCompositeCheck(check, w);
                w.write(resourceAsString("/report.js"));
            w.write("</script>");
        w.write("</head>");
        w.write("<body/>");
        w.write("</html>");
        return w.toString();
    }

    private static void writeCompositeCheck(ReportingMatcher.ExecutedCompositeCheck check, StringWriter w) {
        w.write("{s:");
        w.write(String.valueOf(check.getStatus().ordinal()));
        if (check.getName() != null && !check.getName().isEmpty()) {
            w.write(",n:");
            writeJavaScriptStringLiteral(check.getName(), w);
        }
        if (check.getCompositeChecks().isEmpty()) {
            w.write(",v:");
            writeJavaScriptStringLiteral(String.valueOf(check.getValue()), w);
        }
        if (check.getExtractionStatus() != NORMAL) {
            w.write(",es:");
            w.write(String.valueOf(check.getStatus().ordinal()));
        }
        if (check.getExtractionException() != null) {
            w.write(",ee:");
            writeJavaScriptStringLiteral(String.valueOf(check.getExtractionException()), w);
        }
        if (!check.getSimpleChecks().isEmpty()) {
            w.write(",sc:[");
            boolean first = true;
            for (ReportingMatcher.ExecutedSimpleCheck simpleCheck : check.getSimpleChecks()) {
                if (!first) w.write(',');
                first = false;
                w.write("{d:");
                writeJavaScriptStringLiteral(simpleCheck.getMatcherDescription(), w);
                w.write(",m:");
                writeJavaScriptStringLiteral(simpleCheck.getMismatchDescription(), w);
                w.write("}");
            }
            w.write("]");
        }
        if (!check.getCompositeChecks().isEmpty()) {
            w.write(",cc:[");
            boolean first = true;
            for (ReportingMatcher.ExecutedCompositeCheck compositeCheck : check.getCompositeChecks()) {
                if (!first) w.write(',');
                first = false;
                writeCompositeCheck(compositeCheck, w);
            }
            w.write("]");
        }
        w.write("}");
    }


    private static void writeJavaScriptStringLiteral(String s, StringWriter w) {
        w.write("\"");
        w.write(s);
        w.write("\"");
//        int length = s.length();
//        for (int offset = 0; offset < length; ) {
//            int codepoint = s.codePointAt(offset);
//            if (codepoint == '"') w.write("\\\""); else
//            if (codepoint == '\\') w.write("\\\\"); else
//            if (codepoint == '/') w.write("\\/"); else
//            if (codepoint == '\b') w.write("\\b"); else
//            if (codepoint == '\f') w.write("\\f"); else
//            if (codepoint == '\n') w.write("\\n"); else
//            if (codepoint == '\r') w.write("\\r"); else
//            if (codepoint == '\t') w.write("\\t"); else
//            if (codepoint < ' ') w.write("\\u");/*TODO: u ....*/ else {
//                w.write(codepoint);
//            }
//
//            // do something with the codepoint
//
//            offset += Character.charCount(codepoint);
//        }
    }

    private static String resourceAsString(String resourceName) {
        return new Scanner(HtmlReporter.class.getResourceAsStream(resourceName), "UTF-8").useDelimiter("\\A").next();
    }

}
