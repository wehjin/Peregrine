package com.rubyhuntersky.columnui.operations;

import com.rubyhuntersky.columnui.columns.Column;
import com.rubyhuntersky.columnui.columns.Div0;
import com.rubyhuntersky.columnui.columns.DelayColumn;
import com.rubyhuntersky.columnui.conditions.Human;
import com.rubyhuntersky.columnui.presentations.Presentation;
import com.rubyhuntersky.columnui.presentations.ResizePresentation;
import com.rubyhuntersky.columnui.presenters.OnPresent;
import com.rubyhuntersky.columnui.presenters.Presenter;

/**
 * @author wehjin
 * @since 2/9/16.
 */

public class ExpandDownDivOperation1 extends DivOperation1 {

    @Override
    public Div0 apply0(final Div0 base, final Div0 expansion) {
        return Div0.create(new OnPresent<Column>() {
            @Override
            public void onPresent(Presenter<Column> presenter) {
                Human human = presenter.getHuman();
                Column column = presenter.getDisplay();

                final DelayColumn delayColumn = column.withDelay();
                final Presentation topPresentation = base.present(human, delayColumn, presenter);
                final float topHeight = topPresentation.getHeight();
                final Column bottomColumn = column.withRelatedHeight(topHeight).withShift(0, topHeight);
                final Presentation bottomPresentation = expansion.present(human, bottomColumn, presenter);
                final Presentation bottomResize =
                      new ResizePresentation(bottomPresentation.getWidth(), topHeight + bottomPresentation
                            .getHeight(), bottomPresentation);
                delayColumn.endDelay();
                presenter.addPresentation(bottomResize);
                presenter.addPresentation(topPresentation);
            }
        });
    }
}
