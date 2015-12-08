package org.cloudoholiq.catalog.common.exception;

/**
 * The data received from client have some consistency issues.
 */
public class InvalidClientDataException extends RuntimeException {
    public InvalidClientDataException() {
    }

    public InvalidClientDataException(String message) {
        super(message);
    }

    public InvalidClientDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidClientDataException(Throwable cause) {
        super(cause);
    }

    public InvalidClientDataException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
