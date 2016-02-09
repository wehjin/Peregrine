package com.rubyhuntersky.columnui.material;

import android.support.annotation.NonNull;

import com.rubyhuntersky.columnui.Observer;
import com.rubyhuntersky.columnui.bars.BarUi;
import com.rubyhuntersky.columnui.bars.BarUi1;
import com.rubyhuntersky.columnui.basics.Frame;
import com.rubyhuntersky.columnui.basics.ShapeSize;
import com.rubyhuntersky.columnui.columns.ColumnUi;
import com.rubyhuntersky.columnui.patches.Patch;
import com.rubyhuntersky.columnui.presentations.PatchPresentation;
import com.rubyhuntersky.columnui.presenters.OnPresent;
import com.rubyhuntersky.columnui.presenters.Presenter;
import com.rubyhuntersky.columnui.shapes.SpinnerViewShape;
import com.rubyhuntersky.columnui.tiles.Tile;
import com.rubyhuntersky.columnui.tiles.TileUi;
import com.rubyhuntersky.columnui.tiles.TileUi1;

import java.util.List;

/**
 * @author wehjin
 * @since 1/27/16.
 */

public class Android {

    public static TileUi spinnerTile(final List<String> options, final int selectedOption) {
        return TileUi.create(new OnPresent<Tile>() {
            @Override
            public void onPresent(Presenter<Tile> presenter) {
                final Tile tile = presenter.getDisplay();
                final SpinnerViewShape spinnerViewShape = new SpinnerViewShape(options, selectedOption, Observer.EMPTY);
                final ShapeSize shapeSize = tile.measureShape(spinnerViewShape);
                final int adjustedWidth = shapeSize.measuredWidth + 2; // A few pixels short causes ellipsis.
                final Frame frame = new Frame(adjustedWidth, shapeSize.measuredHeight, tile.elevation);
                final SpinnerViewShape shape = new SpinnerViewShape(options, selectedOption, presenter);
                final Patch patch = tile.addPatch(frame, shape);
                presenter.addPresentation(new PatchPresentation(patch, frame));
            }
        });
    }

    public static TileUi1<Integer> spinnerTile(final List<String> options) {
        return TileUi1.create(new TileUi1.OnBind<Integer>() {
            @NonNull
            @Override
            public TileUi onBind(Integer condition) {
                return spinnerTile(options, condition);
            }
        });
    }

    public static ColumnUi spinnerColumn(final List<String> options, final int selectedOption) {
        return spinnerTile(options, selectedOption).toColumn();
    }

    public static BarUi spinnerBar(final List<String> options, final int selectedOption) {
        return spinnerTile(options, selectedOption).toBar();
    }

    public static BarUi1<Integer> spinnerBar(final List<String> options) {
        return BarUi1.create(new BarUi1.OnBind<Integer>() {
            @Override
            public BarUi onBind(Integer condition) {
                return spinnerBar(options, condition);
            }
        });
    }

}
