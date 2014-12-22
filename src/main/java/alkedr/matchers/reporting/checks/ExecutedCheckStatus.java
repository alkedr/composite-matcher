package alkedr.matchers.reporting.checks;

public enum ExecutedCheckStatus {
    SKIPPED(true),  // TODO: extracted but not checked (MapMatcher.hasKey(keyMatcher))
    PASSED(true),
    FAILED(false),
    MISSING(false),
    UNEXPECTED(false),
    ;

    private final boolean isSuccessful;

    ExecutedCheckStatus(boolean isSuccessful) {
        this.isSuccessful = isSuccessful;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }
}
