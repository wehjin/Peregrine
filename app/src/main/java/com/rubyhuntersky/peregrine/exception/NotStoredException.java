package com.rubyhuntersky.peregrine.exception;

/**
 * @author wehjin
 * @since 11/7/15.
 */

public class NotStoredException extends RuntimeException {
    public NotStoredException() {
    }

    public NotStoredException(String detailMessage) {
        super(detailMessage);
    }

    public NotStoredException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public NotStoredException(Throwable throwable) {
        super(throwable);
    }
}
