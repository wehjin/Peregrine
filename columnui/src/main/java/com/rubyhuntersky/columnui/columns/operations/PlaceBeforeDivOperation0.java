package com.rubyhuntersky.columnui.columns.operations;

import android.util.Pair;

import com.rubyhuntersky.columnui.columns.Column;
import com.rubyhuntersky.columnui.columns.DelayColumn;
import com.rubyhuntersky.columnui.columns.Div0;
import com.rubyhuntersky.columnui.Human;
import com.rubyhuntersky.columnui.presentations.Presentation;
import com.rubyhuntersky.columnui.presenters.OnPresent;
import com.rubyhuntersky.columnui.presenters.Presenter;

/**
 * TODO Extend DivOperation1.
 *
 * @author wehjin
 * @since 2/9/16.
 */

public class PlaceBeforeDivOperation0 extends DivOperation0 {
    private final Div0 background;
    private final int gap;

    public PlaceBeforeDivOperation0(Div0 background, int gap) {
        this.background = background;
        this.gap = gap;
    }

    @Override
    public Div0 apply(final Div0 base) {
        return Div0.create(new OnPresent<Column>() {
            @Override
            public void onPresent(Presenter<Column> presenter) {
                Human human = presenter.getHuman();
                Column column = presenter.getDisplay();
                final DelayColumn delayColumn = column.withElevation(column.elevation + gap).withDelay();
                final Presentation frontPresentation = base.present(human, delayColumn, presenter);
                final Column backgroundColumn = column.withRelatedHeight(frontPresentation.getHeight());
                final Presentation backgroundPresentation = background.present(human, backgroundColumn, presenter);
                delayColumn.endDelay();
                final Pair<Presentation, Presentation> presentations =
                      new Pair<>(frontPresentation, backgroundPresentation);
                presenter.addPresentation(presentations.first);
                presenter.addPresentation(presentations.second);
            }
        });
    }
}
