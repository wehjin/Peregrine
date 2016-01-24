package com.rubyhuntersky.columnui;

import com.rubyhuntersky.columnui.conditions.Column;

/**
 * @author wehjin
 * @since 1/23/16.
 */

public class Creator {

    static public Ui createPanel(final Coloret coloret, final Sizelet heightlet) {
        return Ui.create(new OnPresent() {
            @Override
            public void onPresent(Presenter presenter) {
                final Column column = presenter.getColumn();
                final Range horizontalRange = column.horizontalRange;
                final float height = heightlet.toFloat(presenter.getHuman(), 0);
                final Range verticalRange = new Range(0, height);
                final Frame frame = new Frame(horizontalRange, verticalRange);
                final Patch patch = column.addPatch(frame, Shape.RECTANGLE, coloret);
                final Presentation presentation = new BooleanPresentation() {

                    @Override
                    public Range getVerticalRange() {
                        return verticalRange;
                    }

                    @Override
                    protected void onCancel() {
                        patch.remove();
                    }
                };
                presenter.addPresentation(presentation);
            }
        });
    }
}
