package alkedr.matchers.reporting;

import alkedr.matchers.reporting.checks.CheckResult;
import alkedr.matchers.reporting.checks.PlannedCheck;
import alkedr.matchers.reporting.checks.PlannedCheckExtractor;
import ch.lambdaj.function.convert.Converter;
import org.hamcrest.*;

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
    private CheckResult result = null;

    /**
     * Добавляет проверку поля.
     * @return this
     */
    public This field(PlannedCheckExtractor<T, ?> plannedCheckExtractor) {
        fieldCheckExtractors.add(plannedCheckExtractor);
        return (This)this;
    }

    /**
     * Добавляет проверку не-поля.
     * В отчёте такие проверки отобразится одним блоком отдельно от полей.
     * @return this
     */
    public This value(PlannedCheckExtractor<T, ?> plannedCheckExtractor) {
        nonFieldCheckExtractors.add(plannedCheckExtractor);
        return (This)this;
    }

    /**
     * @return результат проверки или null если метод {@link org.hamcrest.Matcher#matches} не был вызван
     */
    public CheckResult getCheckResult() {
        return result;
    }


    @Override
    protected boolean matchesSafely(T item, Description mismatchDescription) {
        result = new CheckResult();
        result.setActualValueName("object");
        result.setFields(extractAndExecuteChecks(item, fieldCheckExtractors));
        result.setNonFields(extractAndExecuteChecks(item, nonFieldCheckExtractors));
        return result.isSuccessful();
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("is correct");
    }



    private List<CheckResult> extractAndExecuteChecks(T item, Iterable<PlannedCheckExtractor<T, ?>> checkExtractors) {
        return executeChecks(extractChecks(item, checkExtractors));
    }

    private List<PlannedCheck<?>> extractChecks(T item, Iterable<PlannedCheckExtractor<T, ?>> checkExtractors) {
        List<PlannedCheck<?>> extractedChecks = new ArrayList<>();
        for (PlannedCheckExtractor<T, ?> extractor : checkExtractors) {
            extractedChecks.addAll(extractor.extractChecks(item));
        }
        return extractedChecks;
    }

    private static List<CheckResult> executeChecks(List<PlannedCheck<?>> plannedChecks) {
        return convert(plannedChecks, new Converter<PlannedCheck<?>, CheckResult>() {
            @Override
            public CheckResult convert(PlannedCheck<?> from) {
                CheckResult check = new CheckResult();
                check.setActualValueName(from.getActualValueName());
                check.setMatcherDescription(getDescription(from.getMatcher()));
                if (!from.getMatcher().matches(from.getActualValue())) {
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





/*



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
            JAXBContext.newInstance(CheckResult.class).createMarshaller().marshal(result, output);
            return output.toString();
        } catch (JAXBException | IOException e) {
            throw new RuntimeException(e);
        }
    }
*/


}
