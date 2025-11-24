package fr.groupebpce.sepia.exception;

public class FetchException extends QueryException {

    public FetchException() {
    }

    public FetchException(String message) {
        super(message);
    }

    public FetchException(String message, Throwable cause) {
        super(message, cause);
    }

    public FetchException(Throwable cause) {
        super(cause);
    }

    public FetchException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
