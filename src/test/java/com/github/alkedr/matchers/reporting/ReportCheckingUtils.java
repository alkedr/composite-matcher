package com.github.alkedr.matchers.reporting;

import com.github.alkedr.matchers.reporting.ReportingMatcher.ExtractionStatus;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import static com.github.alkedr.matchers.reporting.ReportingMatcher.ExecutedCheck.Status.UNCHECKED;
import static com.github.alkedr.matchers.reporting.ReportingMatcher.ExtractionStatus.NORMAL;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * User: alkedr
 * Date: 05.02.2015
 */
public class ReportCheckingUtils {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();


    public static void reportsShouldBeEqual(ReportingMatcher.ExecutedCompositeCheck actual, ReportingMatcher.ExecutedCompositeCheck expected) {
        String actualJson = gson.toJson(actual);
        String expectedJson = gson.toJson(expected);
        assertThat(actualJson, equalTo(expectedJson));



//        ObjectMapper mapper = new ObjectMapper();
//        mapper.writeValue(System.out, album);
//
//
//        String actualJson = gson.toJson(actual);
//        String expectedJson = gson.toJson(expected);
    }

    public static ReportingMatcher.ExecutedCompositeCheck composite() {
        return composite(null, null, NORMAL, null, UNCHECKED, null, null);
    }

    public static ReportingMatcher.ExecutedCompositeCheck composite(@Nullable Object value, @NotNull ReportingMatcher.ExecutedCheck.Status status) {
        return composite(null, value, NORMAL, null, status, null, null);
    }

    public static ReportingMatcher.ExecutedCompositeCheck composite(@Nullable Object value, @NotNull ReportingMatcher.ExecutedCheck.Status status,
                                                                    @NotNull List<ReportingMatcher.ExecutedCompositeCheck> compositeChecks) {
        return composite(null, value, NORMAL, null, status, null, compositeChecks);
    }




    public static ReportingMatcher.ExecutedCompositeCheck composite(@Nullable String name, @Nullable Object value,
            @NotNull ExtractionStatus extractionStatus, @Nullable Exception extractionException,
            @NotNull ReportingMatcher.ExecutedCheck.Status status,
            @Nullable List<? extends ReportingMatcher.ExecutedSimpleCheck> simpleChecks,
            @Nullable List<? extends ReportingMatcher.ExecutedCompositeCheck> compositeChecks
    ) {
        return new ExecutedCompositeCheckForTests(name, value, extractionStatus, extractionException, status, simpleChecks, compositeChecks);
    }


    private static class ExecutedCompositeCheckForTests implements ReportingMatcher.ExecutedCompositeCheck {
        @Nullable private final String name;
        @Nullable private final Object value;
        @NotNull private final ExtractionStatus extractionStatus;
        @Nullable private final Exception extractionException;
        @NotNull private final Status status;
        @Nullable private final List<? extends ReportingMatcher.ExecutedSimpleCheck> simpleChecks;
        @Nullable private final List<? extends ReportingMatcher.ExecutedCompositeCheck> compositeChecks;

        private ExecutedCompositeCheckForTests(@Nullable String name, @Nullable Object value, @NotNull ExtractionStatus extractionStatus,
                                               @Nullable Exception extractionException, @NotNull Status status,
                                               @Nullable List<? extends ReportingMatcher.ExecutedSimpleCheck> simpleChecks,
                                               @Nullable List<? extends ReportingMatcher.ExecutedCompositeCheck> compositeChecks) {
            this.name = name;
            this.value = value;
            this.extractionStatus = extractionStatus;
            this.extractionException = extractionException;
            this.status = status;
            this.simpleChecks = simpleChecks;
            this.compositeChecks = compositeChecks;
        }

        @Nullable
        @Override
        public String getName() {
            return name;
        }

        @Nullable
        @Override
        public Object getValue() {
            return value;
        }

        @NotNull
        @Override
        public ExtractionStatus getExtractionStatus() {
            return extractionStatus;
        }

        @NotNull
        @Override
        public List<? extends ReportingMatcher.ExecutedSimpleCheck> getSimpleChecks() {
            return simpleChecks == null ? Collections.<ReportingMatcher.ExecutedSimpleCheck>emptyList() : simpleChecks;
        }

        @NotNull
        @Override
        public List<? extends ReportingMatcher.ExecutedCompositeCheck> getCompositeChecks() {
            return compositeChecks == null ? Collections.<ReportingMatcher.ExecutedCompositeCheck>emptyList() : compositeChecks;
        }

        @NotNull
        @Override
        public Status getStatus() {
            return status;
        }
    }
}
