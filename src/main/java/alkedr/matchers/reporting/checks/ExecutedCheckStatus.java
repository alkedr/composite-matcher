package alkedr.matchers.reporting.checks;

public enum ExecutedCheckStatus {
    SKIPPED(true),  // нет проверок
    PASSED(true),   // все проверки успешны
    FAILED(false),  // хотя бы одна проверка неуспешна
    MISSING(false), // не нашлось значение (например в мапе нет нужного ключа)
    UNEXPECTED(false),  // лишнее значение
    ;

    private final boolean isSuccessful;

    ExecutedCheckStatus(boolean isSuccessful) {
        this.isSuccessful = isSuccessful;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }
}
