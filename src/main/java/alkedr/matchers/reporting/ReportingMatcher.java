package alkedr.matchers.reporting;

import alkedr.matchers.reporting.beans.Check;
import alkedr.matchers.reporting.beans.Report;
import alkedr.matchers.reporting.checks.PlannedCheck;
import alkedr.matchers.reporting.checks.PlannedCheckExtractor;
import ch.lambdaj.function.convert.Converter;
import org.hamcrest.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.is;

/**
 * Матчер, проверяющий поля, свойства и возвращаемые значения методов любого класса
 * Использовать этот класс напрямую неудобно, лучше наследоваться и добавлять нужные методы
 * TODO: пример использования
 * TODO: механизм для интеграции с аннотациями GSON, Jaxb, Selenium, htmlelements и пр.
 *       (interface NameExtractor, который преобразовывает (actual, defaultName) -> name)?
 * TODO: глобальная ThreadLocal-переменная для сбора результатов вложенных матчеров (потому что describedAs, should и пр.)
 */
public class ReportingMatcher<T, This extends ReportingMatcher<T, This>> extends TypeSafeDiagnosingMatcher<T> {
    private final Collection<PlannedCheckExtractor<T, ?>> fieldCheckExtractors = new ArrayList<>();
    private final Collection<PlannedCheckExtractor<T, ?>> nonFieldCheckExtractors = new ArrayList<>();
    private Report report = null;


    /**
     * Добавляет проверку поля.
     */
    public This field(PlannedCheckExtractor<T, ?> plannedCheckExtractor) {
        fieldCheckExtractors.add(plannedCheckExtractor);
        return (This)this;
    }

    /**
     * Добаляет проверку не-поля.
     * В отчёте отобразится отдельно от полей.
     */
    public This value(PlannedCheckExtractor<T, ?> plannedCheckExtractor) {
        nonFieldCheckExtractors.add(plannedCheckExtractor);
        return (This)this;
    }


    @Override
    protected boolean matchesSafely(T item, Description mismatchDescription) {
        report = new Report();
        report.setFieldChecks(extractAndExecuteChecks(item, fieldCheckExtractors));
        report.setNonFieldChecks(extractAndExecuteChecks(item, nonFieldCheckExtractors));
        return !hasFailedChecks(report);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("is correct");
    }



    private static final String HTML_REPORT_XSL = "/html-report.xsl";
    private static final String PLAIN_TEXT_REPORT_XSL = "/plain-text-report.xsl";
    private static final TransformerFactory TRANSFORMER_FACTORY = TransformerFactory.newInstance();
    private static final Transformer HTML_TRANSFORMER = loadXslTransformer(HTML_REPORT_XSL);
    private static final Transformer PLAIN_TEXT_TRANSFORMER = loadXslTransformer(PLAIN_TEXT_REPORT_XSL);

    public static Transformer loadXslTransformer(String xslResourceName) {
        try (InputStream inputStream = ReportingMatcher.class.getResourceAsStream(xslResourceName)) {
            return TRANSFORMER_FACTORY.newTransformer(new StreamSource(inputStream));
        } catch (TransformerConfigurationException | IOException e) {
            throw new RuntimeException(e);
        }
    }


    public String generateHtmlReport() {
        return generateReport(HTML_TRANSFORMER);
    }

    public String generatePlainTextReport() {
        return generateReport(PLAIN_TEXT_TRANSFORMER);
    }

    private String generateReport(Transformer transformer) {
        try (StringWriter output = new StringWriter()) {
            transformer.transform(new StreamSource(new StringReader(getReportXml())), new StreamResult(output));
            return output.toString();
        } catch (TransformerException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getReportXml() {
        try (StringWriter output = new StringWriter()) {
            JAXBContext.newInstance(Report.class).createMarshaller().marshal(report, output);
            return output.toString();
        } catch (JAXBException | IOException e) {
            throw new RuntimeException(e);
        }
    }



    private List<Check> extractAndExecuteChecks(T item, Iterable<PlannedCheckExtractor<T, ?>> checkExtractors) {
        return executeChecks(item, extractChecks(item, checkExtractors));
    }

    private List<PlannedCheck<?>> extractChecks(T item, Iterable<PlannedCheckExtractor<T, ?>> checkExtractors) {
        List<PlannedCheck<?>> result = new ArrayList<>();
        for (PlannedCheckExtractor<T, ?> extractor : checkExtractors) {
            result.addAll(extractor.extractChecks(item));
        }
        return result;
    }

    private List<Check> executeChecks(T item, List<PlannedCheck<?>> plannedChecks) {
        return convert(plannedChecks, new Converter<PlannedCheck<?>, Check>() {
            @Override
            public Check convert(PlannedCheck<?> from) {
                Check check = new Check();
                check.setActualValueName(from.getActualValueName());
                check.setActualValue(from.getActualValue());
                check.setMatcherDescription(getDescription(from.getMatcher()));
                if (from.getMatcher().matches(from.getActualValue())) {
                    check.setSuccessful(true);
                    check.setMismatchDescription(null);
                } else {
                    check.setSuccessful(false);
                    check.setMismatchDescription(getMismatchDescription(from.getActualValue(), from.getMatcher()));
                }
                // TODO: innerChecks
                return check;
            }
        });
    }

    private static String getMismatchDescription(Object actualValue, Matcher<?> matcher) {
        StringDescription stringDescription = new StringDescription();
        matcher.describeMismatch(actualValue, stringDescription);
        return stringDescription.toString();
    }

    private static String getDescription(SelfDescribing selfDescribing) {
        StringDescription stringDescription = new StringDescription();
        selfDescribing.describeTo(stringDescription);
        return stringDescription.toString();
    }

    private static boolean hasFailedChecks(Report report) {
        return hasFailedChecks(report.getFieldChecks()) || hasFailedChecks(report.getNonFieldChecks());
    }

    private static boolean hasFailedChecks(List<Check> checks) {
        return selectFirst(checks, having(on(Check.class).isSuccessful(), is(false))) != null;
    }
}
