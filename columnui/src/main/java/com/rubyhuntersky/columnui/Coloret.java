package com.rubyhuntersky.columnui;

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

    int toArgb();
}
