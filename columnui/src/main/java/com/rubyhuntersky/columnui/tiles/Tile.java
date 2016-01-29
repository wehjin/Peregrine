package com.rubyhuntersky.columnui.tiles;

/**
 * @author wehjin
 * @since 1/28/16.
 */

abstract public class Tile {

    public final float relatedWidth;
    public final float relatedHeight;
    public final int elevation;

    public Tile(float relatedWidth, float relatedHeight, int elevation) {
        this.relatedWidth = relatedWidth;
        this.relatedHeight = relatedHeight;
        this.elevation = elevation;
    }
}
