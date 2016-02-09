package com.rubyhuntersky.columnui.operations;

import android.util.Pair;

import com.rubyhuntersky.columnui.columns.Column;
import com.rubyhuntersky.columnui.columns.ColumnUi;
import com.rubyhuntersky.columnui.columns.DelayColumn;
import com.rubyhuntersky.columnui.conditions.Human;
import com.rubyhuntersky.columnui.presentations.Presentation;
import com.rubyhuntersky.columnui.presenters.OnPresent;
import com.rubyhuntersky.columnui.presenters.Presenter;

/**
 * @author wehjin
 * @since 2/9/16.
 */

public class PlaceBeforeOperation extends Operation {
    private final ColumnUi background;
    private final int gap;

    public PlaceBeforeOperation(ColumnUi background, int gap) {
        this.background = background;
        this.gap = gap;
    }

    @Override
    public ColumnUi applyTo(final ColumnUi div) {
        return ColumnUi.create(new OnPresent<Column>() {
            @Override
            public void onPresent(Presenter<Column> presenter) {
                Human human = presenter.getHuman();
                Column column = presenter.getDisplay();
                final DelayColumn delayColumn = column.withElevation(column.elevation + gap).withDelay();
                final Presentation frontPresentation = div.present(human, delayColumn, presenter);
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
