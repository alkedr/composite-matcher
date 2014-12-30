package alkedr.matchers.reporting.checks;

public interface ExecutedCheck {
    Status getStatus();

    enum Status {
        UNCHECKED,
        PASSED,
        FAILED,
        ;
    }
}
