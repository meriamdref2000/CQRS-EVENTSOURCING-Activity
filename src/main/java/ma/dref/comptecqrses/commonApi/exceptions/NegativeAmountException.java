package ma.dref.comptecqrses.commonApi.exceptions;

public class NegativeAmountException extends RuntimeException {
    public NegativeAmountException(String message) {
        super(message);
    }
}
