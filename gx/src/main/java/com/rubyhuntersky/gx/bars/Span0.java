package com.rubyhuntersky.gx.bars;

import android.support.annotation.NonNull;

import com.rubyhuntersky.gx.Observer;
import com.rubyhuntersky.gx.basics.Sizelet;
import com.rubyhuntersky.gx.columns.Column;
import com.rubyhuntersky.gx.columns.Div0;
import com.rubyhuntersky.gx.Human;
import com.rubyhuntersky.gx.presentations.Presentation;
import com.rubyhuntersky.gx.presentations.ResizePresentation;
import com.rubyhuntersky.gx.presenters.BasePresenter;
import com.rubyhuntersky.gx.presenters.OnPresent;
import com.rubyhuntersky.gx.presenters.Presenter;
import com.rubyhuntersky.gx.tiles.Tile0;
import com.rubyhuntersky.gx.ui.Ui;

/**
 * @author wehjin
 * @since 1/27/16.
 */

abstract public class Span0 implements Ui<BarExtender> {

    @Override
    abstract public Presentation present(Human human, BarExtender bar, Observer observer);

    public Div0 toColumn(final Sizelet heightlet) {
        final Span0 span0 = this;
        return Div0.create(new OnPresent<Column>() {
            @Override
            public void onPresent(Presenter<Column> presenter) {
                presenter.addPresentation(
                      presentBarToColumn(span0, heightlet, presenter.getHuman(), presenter.getDisplay(), presenter));
            }
        });
    }

    @NonNull
    private Presentation presentBarToColumn(Span0 span0, Sizelet heightlet, Human human, Column column,
          Observer observer) {
        final float height = heightlet.toFloat(human, column.relatedHeight);
        final BarExtender bar = new BarExtender(height, column.fixedWidth, column.elevation, column);
        final ShiftBar shiftBar = bar.withShift();
        final Presentation presentation = span0.present(human, shiftBar, observer);
        final float presentationWidth = presentation.getWidth();
        final float extraWidth = column.fixedWidth - presentationWidth;
        final float anchor = .5f;
        shiftBar.setShift(extraWidth * anchor, 0);
        return new ResizePresentation(column.fixedWidth, bar.fixedHeight, presentation);
    }

    public Span0 expandStart(final Tile0 startUi) {
        return expandStart(startUi.toBar());
    }

    public Span0 expandStart(final Span0 startUi) {
        final Span0 ui = this;
        return create(new OnPresent<BarExtender>() {
            @Override
            public void onPresent(Presenter<BarExtender> presenter) {
                final BarExtender bar = presenter.getDisplay();
                final ShiftBar shiftBar = bar.withShift();
                final Human human = presenter.getHuman();
                final Presentation endPresentation = ui.present(human, shiftBar, presenter);
                final BarExtender startBar = bar.withRelatedWidth(endPresentation.getWidth());
                final Presentation startPresentation = startUi.present(human, startBar, presenter);
                final float startWidth = startPresentation.getWidth();
                shiftBar.setShift(startWidth, 0);
                final float combinedWidth = startWidth + endPresentation.getWidth();
                presenter.addPresentation(startPresentation);
                presenter.addPresentation(new ResizePresentation(combinedWidth, bar.fixedHeight, endPresentation));
            }
        });
    }

    public Span0 padStart(final Sizelet padlet) {
        final Span0 ui = this;
        return create(new OnPresent<BarExtender>() {
            @Override
            public void onPresent(Presenter<BarExtender> presenter) {
                final BarExtender bar = presenter.getDisplay();
                final ShiftBar shiftBar = bar.withShift();
                final Human human = presenter.getHuman();
                final Presentation presentation = ui.present(human, shiftBar, presenter);
                final float padding = padlet.toFloat(human, presentation.getWidth());
                shiftBar.setShift(padding, 0);
                presenter.addPresentation(
                      new ResizePresentation(padding + presentation.getWidth(), presentation.getHeight(),
                            presentation));
            }
        });
    }

    public static Span0 create(final OnPresent<BarExtender> onPresent) {
        return new Span0() {
            @Override
            public Presentation present(Human human, final BarExtender bar, Observer observer) {
                final BasePresenter<BarExtender> presenter = new BasePresenter<BarExtender>(human, bar, observer) {
                    @Override
                    public float getWidth() {
                        float union = 0;
                        for (Presentation presentation : presentations) {
                            union = Math.max(union, presentation.getWidth());
                        }
                        return union;
                    }

                    @Override
                    public float getHeight() {
                        return display.fixedHeight;
                    }
                };
                onPresent.onPresent(presenter);
                return presenter;
            }
        };
    }
}
