package com.rubyhuntersky.columnui.columns.operations;

import com.rubyhuntersky.columnui.basics.Sizelet;
import com.rubyhuntersky.columnui.columns.Column;
import com.rubyhuntersky.columnui.columns.Div0;
import com.rubyhuntersky.columnui.conditions.Human;
import com.rubyhuntersky.columnui.presentations.Presentation;
import com.rubyhuntersky.columnui.presentations.ResizePresentation;
import com.rubyhuntersky.columnui.presenters.OnPresent;
import com.rubyhuntersky.columnui.presenters.Presenter;

/**
 * @author wehjin
 * @since 2/9/16.
 */

public class ExpandVerticalDivOperation0 extends DivOperation0 {

    private final Sizelet heightlet;

    public ExpandVerticalDivOperation0(Sizelet heightlet) {
        this.heightlet = heightlet;
    }

    @Override
    public Div0 apply(final Div0 base) {
        return Div0.create(new OnPresent<Column>() {
            @Override
            public void onPresent(Presenter<Column> presenter) {
                final Human human = presenter.getHuman();
                final Column column = presenter.getDisplay();
                final float expansion = heightlet.toFloat(human, column.relatedHeight);
                final Column shiftColumn = column.withShift(0, expansion);
                final Presentation present = base.present(human, shiftColumn, presenter);
                final float expanded = present.getHeight() + 2 * expansion;
                final Presentation resize = new ResizePresentation(column.fixedWidth, expanded, present);
                presenter.addPresentation(resize);
            }
        });
    }
}
