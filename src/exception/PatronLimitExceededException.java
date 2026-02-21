package exception;

public class PatronLimitExceededException extends Exception {
    public PatronLimitExceededException(String message) {
        super(message);
    }
}
