package alkedr.compositematcher;

import alkedr.compositematcher.objectdescribers.ObjectDescriber;
import alkedr.compositematcher.objectdescribers.ToStringObjectDescriber;
import ch.lambdaj.Lambda;
import org.hamcrest.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static java.util.Collections.*;
import static org.hamcrest.core.IsEqual.equalTo;

public abstract class CompositeMatcher <T> extends TypeSafeMatcher<T> {
    private final ObjectDescriber objectDescriber;

    // хранит информацию о запуске matchesSafely()
    // нельзя сделать локальной переменной в matchesSafely, т. к. к этим данным должен иметь доступ checkThat()
    private MatchingResult result = null;

    // хранит информацию о запуске другого CompositeMatcher'а, который быз вызван из текущего CompositeMatcher'а
    // нужно для того, чтобы присоединить отчёт о проверках внутреннего матчера к отчёту текущего матчера
    // checkThat зануляет INNER_COMPOSITE_MATCHER_RESULT и вызывает matcher.matches()
    // если после этого INNER_COMPOSITE_MATCHER_RESULT не нулл, значит matcher является CompositeMatcher'ом или использует CompositeMatcher внутри
    // checkThat не может просто попытаться покастить matcher к CompositeMatcher'у, т. к. бывают обёртки для матчеров (напр. describedAs())
    private static final ThreadLocal<MatchingResult> INNER_COMPOSITE_MATCHER_RESULT = new ThreadLocal<MatchingResult>();


    protected CompositeMatcher() {
        this(new ToStringObjectDescriber());
    }

    protected CompositeMatcher(ObjectDescriber objectDescriber) {
        this.objectDescriber = objectDescriber;
    }

    // дочерние классы имплементят этот метод и вызывают checkThat() вместо assertThat()
    protected abstract void check(T item);

    @Override
    protected boolean matchesSafely(T item) {
        result = new MatchingResult(item);
        check(item);
        INNER_COMPOSITE_MATCHER_RESULT.set(result);
        return result.matches();
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("is correct");
    }

    @Override
    protected void describeMismatchSafely(T item, Description mismatchDescription) {
//        mismatchDescription.appendText(this.mismatchDescription.toString());
    }

    protected <U> boolean checkThat(U object, Matcher<? super U> matcher) {
        return checkThat(objectDescriber.describe(object), object, matcher);
    }

    protected <U> boolean checkThat(String objectDescription, U actual, Matcher<? super U> matcher) {
        Check<U> check = makeCheck(objectDescription, actual, matcher);
        result.checks.add(check);
        return check.isSuccessful();
    }

    public List<Check<?>> getChecks() {
        return result.checks;
    }


    // TODO: вынести в отдельный класс
    private static Field findFieldInObjectRecursively(Object root, Object o) {
        for (Field field : root.getClass().getFields()) {
            if (!field.getType().isPrimitive()) {
                field.setAccessible(true);
                if (field.get(root) == o) {  // сравниваем указатели, без .equals
                    return field;
                }
                if (field.getType().isArray()) {

                }
            }
        }
        return null;
    }

    private static <U> Check<U> makeCheck(String objectDescription, U actual, Matcher<? super U> matcher) {
        Check<U> check = new Check<U>();
        check.setActualValueName(objectDescription);
        check.setActualValue(actual);
        check.setMatcherDescription(getDescriptionString(matcher));

        INNER_COMPOSITE_MATCHER_RESULT.remove();
        check.setSuccessful(matcher.matches(actual));
        if (!check.isSuccessful()) {
            check.setMismatchDescription(getMismatchDescriptionString(actual, matcher));
        }
        if (INNER_COMPOSITE_MATCHER_RESULT.get() != null) {
            check.setInnerChecks(INNER_COMPOSITE_MATCHER_RESULT.get().checks);
        }

        return check;
    }

    private static String getDescriptionString(SelfDescribing selfDescribing) {
        StringDescription description = new StringDescription();
        selfDescribing.describeTo(description);
        return description.toString();
    }

    private static <U> String getMismatchDescriptionString(U actual, Matcher<?> matcher) {
        StringDescription description = new StringDescription();
        matcher.describeMismatch(actual, description);
        return description.toString();
    }


    private static class MatchingResult {
        private final List<Check<?>> checks = new ArrayList<Check<?>>();
        private final Object actualObjectRoot;

        private MatchingResult(Object actualObjectRoot) {
            this.actualObjectRoot = actualObjectRoot;
        }

        public boolean matches() {
//            return !Lambda.exists(checks, having(on(Check.class).isSuccessful(), equalTo(false)));
            for (Check<?> check : checks) {
                if (!check.isSuccessful()) {
                    return false;
                }
            }
            return true;
        }
    }


    public static class Check<T> {
        private boolean isSuccessful = false;
        private String actualValueName = null;
        private T actualValue = null;
        private String matcherDescription = null;
        private String mismatchDescription = null;
        private List<Check<?>> innerChecks = null;


        public boolean isSuccessful() {
            return isSuccessful;
        }

        public void setSuccessful(boolean isSuccessful) {
            this.isSuccessful = isSuccessful;
        }

        public String getActualValueName() {
            return actualValueName;
        }

        public void setActualValueName(String actualValueName) {
            this.actualValueName = actualValueName;
        }

        public T getActualValue() {
            return actualValue;
        }

        public void setActualValue(T actualValue) {
            this.actualValue = actualValue;
        }

        public String getMatcherDescription() {
            return matcherDescription;
        }

        public void setMatcherDescription(String matcherDescription) {
            this.matcherDescription = matcherDescription;
        }

        public String getMismatchDescription() {
            return mismatchDescription;
        }

        public void setMismatchDescription(String mismatchDescription) {
            this.mismatchDescription = mismatchDescription;
        }

        public List<Check<?>> getInnerChecks() {
            return unmodifiableList(innerChecks);
        }

        public void setInnerChecks(List<Check<?>> innerChecks) {
            this.innerChecks = new ArrayList<Check<?>>(innerChecks);
        }
    }
}