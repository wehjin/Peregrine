package com.rubyhuntersky.coloret;

import android.graphics.Color;

/**
 * @author Jeffrey Yu
 * @since 1/23/16.
 */

public interface Coloret {

    Coloret WHITE = new Coloret() {
        @Override
        public int toArgb() {
            return Color.WHITE;
        }
    };
    Coloret BLACK = new Coloret() {
        @Override
        public int toArgb() {
            return Color.BLACK;
        }
    };
    Coloret GREEN = new Coloret() {
        @Override
        public int toArgb() {
            return Color.GREEN;
        }
    };
    Coloret RED = new Coloret() {
        @Override
        public int toArgb() {
            return Color.RED;
        }
    };
    Coloret BLUE = new Coloret() {
        @Override
        public int toArgb() {
            return Color.BLUE;
        }
    };
    Coloret YELLOW = new Coloret() {
        @Override
        public int toArgb() {
            return Color.YELLOW;
        }
    };
    Coloret CYAN = new Coloret() {
        @Override
        public int toArgb() {
            return Color.CYAN;
        }
    };
    Coloret MAGENTA = new Coloret() {

        @Override
        public int toArgb() {
            return Color.MAGENTA;
        }
    };
    Coloret DARK_TEXT = new Coloret() {
        @Override
        public int toArgb() {
            return 0xde000000;
        }
    };

    int toArgb();
}
