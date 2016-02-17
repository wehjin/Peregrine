package com.rubyhuntersky.coloret;

import android.graphics.Color;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author wehjin
 * @since 2/17/16.
 */

public class ColoretTest {

    @Test
    public void testGrayscale() throws Exception {
        assertEquals(Color.BLACK, Coloret.BLACK.toArgb());
        assertEquals(Color.WHITE, Coloret.WHITE.toArgb());
    }

    @Test
    public void testPrimaries() throws Exception {
        assertEquals(Color.RED, Coloret.RED.toArgb());
        assertEquals(Color.GREEN, Coloret.GREEN.toArgb());
        assertEquals(Color.BLUE, Coloret.BLUE.toArgb());
    }

    @Test
    public void testSecondaries() throws Exception {
        assertEquals(Color.YELLOW, Coloret.YELLOW.toArgb());
        assertEquals(Color.CYAN, Coloret.CYAN.toArgb());
        assertEquals(Color.MAGENTA, Coloret.MAGENTA.toArgb());
    }
}