package com.rubyhuntersky.columnui;

/**
 * @author wehjin
 * @since 1/23/16.
 */

public interface Observer {
    void onReaction(Reaction reaction);
    void onEnd();
    void onError(Throwable throwable);
}
