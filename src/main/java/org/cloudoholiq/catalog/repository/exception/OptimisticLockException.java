package org.cloudoholiq.catalog.repository.exception;

/**
 * Created by vrastil on 20.2.2015.
 */
public class OptimisticLockException extends RuntimeException {
    public OptimisticLockException(String message) {
        super(message);
    }
}
