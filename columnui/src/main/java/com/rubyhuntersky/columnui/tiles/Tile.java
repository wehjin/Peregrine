package com.rubyhuntersky.columnui.tiles;

/**
 * @author wehjin
 * @since 1/28/16.
 */

abstract public class Tile {

    public final float lowerWidth;
    public final float lowerHeight;
    public final int elevation;

    public Tile(float lowerWidth, float lowerHeight, int elevation) {
        this.lowerWidth = lowerWidth;
        this.lowerHeight = lowerHeight;
        this.elevation = elevation;
    }
}
