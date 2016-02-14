package com.rubyhuntersky.columnui.operations;

import android.support.annotation.NonNull;

import com.rubyhuntersky.columnui.columns.Column;
import com.rubyhuntersky.columnui.columns.ColumnUi;
import com.rubyhuntersky.columnui.columns.ColumnUi1;
import com.rubyhuntersky.columnui.presentations.Presentation;
import com.rubyhuntersky.columnui.presentations.ResizePresentation;
import com.rubyhuntersky.columnui.presenters.OnPresent;
import com.rubyhuntersky.columnui.presenters.Presenter;
import com.rubyhuntersky.columnui.tiles.ShiftMosaic;
import com.rubyhuntersky.columnui.tiles.Mosaic;
import com.rubyhuntersky.columnui.tiles.TileUi;
import com.rubyhuntersky.columnui.tiles.TileUi1;

/**
 * @author wehjin
 * @since 2/9/16.
 */

public class ToColumnOperation {

    public ColumnUi applyTo(final TileUi tileUi) {
        return ColumnUi.create(new OnPresent<Column>() {
            @Override
            public void onPresent(Presenter<Column> presenter) {
                Column column = presenter.getDisplay();
                final Mosaic mosaic = new Mosaic(column.fixedWidth, column.relatedHeight, column.elevation, column);
                final ShiftMosaic frameShiftTile = mosaic.withShift();
                final Presentation presentation = tileUi.present(presenter.getHuman(), frameShiftTile, presenter);
                final float presentationWidth = presentation.getWidth();
                final float extraWidth = column.fixedWidth - presentationWidth;
                final float anchor = .5f;
                frameShiftTile.setShift(extraWidth * anchor, 0);
                presenter.addPresentation(new ResizePresentation(column.fixedWidth,
                                                                 presentation.getHeight(),
                                                                 presentation));
            }
        });
    }

    public <C> ColumnUi1<C> applyTo(final TileUi1<C> tileUi1) {
        return ColumnUi1.create(new ColumnUi1.OnBind<C>() {
            @NonNull
            @Override
            public ColumnUi onBind(final C condition) {
                return ToColumnOperation.this.applyTo(tileUi1.bind(condition));
            }
        });
    }
}
