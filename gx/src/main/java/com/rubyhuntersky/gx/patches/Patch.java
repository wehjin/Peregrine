package com.rubyhuntersky.gx.patches;

/**
 * @author wehjin
 * @since 1/23/16.
 */
public interface Patch {
    void remove();

    Patch EMPTY = new Patch() {
        @Override
        public void remove() {
        }
    };
}
