package alkedr.matchers.reporting.reporters;

import alkedr.matchers.reporting.checks.ExecutedCompositeCheck;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.context.FieldValueResolver;
import com.github.jknack.handlebars.context.JavaBeanValueResolver;
import com.github.jknack.handlebars.context.MethodValueResolver;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Scanner;

public final class HtmlReporter {
    private HtmlReporter() {}

    public static String generateHtmlReport(ExecutedCompositeCheck checkResult) {
        Handlebars handlebars = new Handlebars(new ClassPathTemplateLoader());
        handlebars.setInfiniteLoops(true);
        Context context = Context.newBuilder(new AbstractMap.SimpleEntry<>(null, checkResult))
                .resolver(JavaBeanValueResolver.INSTANCE, FieldValueResolver.INSTANCE, MethodValueResolver.INSTANCE)
                .build();
        try {
            return "<!DOCTYPE html>" +
                    "<html>" +
                    "<head>" +
                        "<meta charset=\"UTF-8\">" +
                        "<style type=\"text/css\">" + resourceAsString("/report.css") + "</style>" +
                        "<script>" + resourceAsString("/report.js") + "</script>" +
                    "</head>" +
                    "<body>" + handlebars.compile("/report").apply(context) + "</body>" +
                    "</html>";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String resourceAsString(String fileName) {
        return new Scanner(HtmlReporter.class.getResourceAsStream(fileName), "UTF-8").useDelimiter("\\A").next();
    }
}
