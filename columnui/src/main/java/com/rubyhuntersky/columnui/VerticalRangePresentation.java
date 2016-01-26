package com.rubyhuntersky.columnui;

/**
 * @author wehjin
 * @since 1/24/16.
 */
class VerticalRangePresentation implements Presentation {

    private final Range verticalRange;
    private final Presentation original;

    public VerticalRangePresentation(Range verticalRange, Presentation original) {
        this.verticalRange = verticalRange;
        this.original = original;
    }

    @Override
    public Range getVerticalRange() {
        return verticalRange;
    }

    @Override
    public boolean isCancelled() {
        return original.isCancelled();
    }

    @Override
    public void cancel() {
        original.cancel();
    }
}