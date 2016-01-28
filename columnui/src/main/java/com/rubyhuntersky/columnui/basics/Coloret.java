package com.rubyhuntersky.columnui.basics;

import android.graphics.Color;

/**
 * @author wehjin
 * @since 1/23/16.
 */

public interface Coloret {

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
    Coloret DARK_TEXT = new Coloret() {
        @Override
        public int toArgb() {
            return 0xde000000;
        }
    };

    int toArgb();
}
