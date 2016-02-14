package com.rubyhuntersky.columnui.tiles;

import android.support.annotation.NonNull;

import com.rubyhuntersky.columnui.Creator;
import com.rubyhuntersky.columnui.basics.Sizelet;
import com.rubyhuntersky.columnui.basics.TextStylet;
import com.rubyhuntersky.columnui.presentations.BooleanPresentation;
import com.rubyhuntersky.columnui.presenters.OnPresent;
import com.rubyhuntersky.columnui.presenters.Presenter;

/**
 * @author wehjin
 * @since 1/30/16.
 */

public class TileCreator {

    public static Tile1<String> textTile1(final TextStylet textStylet) {
        return Tile1.create(new Tile1.OnBind<String>() {
            @NonNull
            @Override
            public Tile0 onBind(final String condition) {
                return Creator.textTile(condition, textStylet);
            }
        });
    }

    public static Tile0 gapTile(final Sizelet widthlet, final Sizelet heightlet) {
        return Tile0.create(new OnPresent<Mosaic>() {
            @Override
            public void onPresent(Presenter<Mosaic> presenter) {
                final float width = widthlet.toFloat(presenter.getHuman(), 0);
                final float height = heightlet.toFloat(presenter.getHuman(), 0);
                presenter.addPresentation(new BooleanPresentation() {
                    @Override
                    public float getWidth() {
                        return width;
                    }

                    @Override
                    public float getHeight() {
                        return height;
                    }

                    @Override
                    protected void onCancel() {
                        // Do nothing
                    }
                });
            }
        });
    }
}
