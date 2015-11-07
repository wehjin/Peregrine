package com.rubyhuntersky.peregrine.exception;

/**
 * @author wehjin
 * @since 11/7/15.
 */

public class NotStoredException extends RuntimeException {
    public NotStoredException(String detailMessage) {
        super(detailMessage);
    }
}
