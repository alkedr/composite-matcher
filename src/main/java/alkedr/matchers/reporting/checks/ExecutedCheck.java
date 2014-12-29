package alkedr.matchers.reporting.checks;

public interface ExecutedCheck {
    /**
     * @return true если все проверки успешны, false если хотя бы одна неуспешна
     */
    boolean isSuccessful();
}
