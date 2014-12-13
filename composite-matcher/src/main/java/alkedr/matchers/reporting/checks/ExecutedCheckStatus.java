package alkedr.matchers.reporting.checks;

public enum ExecutedCheckStatus {
    PASSED(true),
    FAILED(false),
    SKIPPED(true),
    ;

    private final boolean isSuccessful;

    ExecutedCheckStatus(boolean isSuccessful) {
        this.isSuccessful = isSuccessful;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }
}
