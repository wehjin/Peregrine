package com.rubyhuntersky.columnui.operations;

import com.rubyhuntersky.columnui.basics.Sizelet;
import com.rubyhuntersky.columnui.columns.Column;
import com.rubyhuntersky.columnui.columns.Div0;
import com.rubyhuntersky.columnui.conditions.Human;
import com.rubyhuntersky.columnui.presenters.OnPresent;
import com.rubyhuntersky.columnui.presenters.Presenter;

/**
 * @author wehjin
 * @since 2/9/16.
 */
public class PadHorizontalOperation extends Operation {

    private final Sizelet padlet;

    public PadHorizontalOperation(Sizelet padlet) {
        this.padlet = padlet;
    }

    @Override
    public Div0 applyTo(final Div0 div) {
        return Div0.create(new OnPresent<Column>() {
            @Override
            public void onPresent(Presenter<Column> presenter) {
                Human human = presenter.getHuman();
                Column column = presenter.getDisplay();
                final float padding = padlet.toFloat(human, column.fixedWidth);
                Column newColumn = column.withFixedWidth(column.fixedWidth - 2 * padding).withShift(padding, 0);
                presenter.addPresentation(div.present(human, newColumn, presenter));
            }
        });
    }
}
