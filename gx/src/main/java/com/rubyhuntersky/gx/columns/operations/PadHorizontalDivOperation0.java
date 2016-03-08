package com.rubyhuntersky.gx.columns.operations;

import com.rubyhuntersky.gx.basics.Sizelet;
import com.rubyhuntersky.gx.columns.Column;
import com.rubyhuntersky.gx.columns.Div0;
import com.rubyhuntersky.gx.Human;
import com.rubyhuntersky.gx.presenters.OnPresent;
import com.rubyhuntersky.gx.presenters.Presenter;

/**
 * @author wehjin
 * @since 2/9/16.
 */
public class PadHorizontalDivOperation0 extends DivOperation0 {

    private final Sizelet padlet;

    public PadHorizontalDivOperation0(Sizelet padlet) {
        this.padlet = padlet;
    }

    @Override
    public Div0 apply(final Div0 base) {
        return Div0.create(new OnPresent<Column>() {
            @Override
            public void onPresent(Presenter<Column> presenter) {
                Human human = presenter.getHuman();
                Column column = presenter.getDisplay();
                final float padding = padlet.toFloat(human, column.fixedWidth);
                Column newColumn = column.withFixedWidth(column.fixedWidth - 2 * padding).withShift(padding, 0);
                presenter.addPresentation(base.present(human, newColumn, presenter));
            }
        });
    }
}
