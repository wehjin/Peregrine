package com.rubyhuntersky.columnui;

/**
 * @author wehjin
 * @since 1/23/16.
 */

public interface Observer {
    Observer EMPTY = new Observer() {
        @Override
        public void onReaction(Reaction reaction) {
            // Do nothing
        }

        @Override
        public void onEnd() {
            // Do nothing
        }

        @Override
        public void onError(Throwable throwable) {
            // Do nothing
        }
    };

    void onReaction(Reaction reaction);
    void onEnd();
    void onError(Throwable throwable);
}
