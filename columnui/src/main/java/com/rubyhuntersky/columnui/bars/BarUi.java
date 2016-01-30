package com.rubyhuntersky.columnui.bars;

import com.rubyhuntersky.columnui.presenters.BasePresenter;
import com.rubyhuntersky.columnui.BaseUi;
import com.rubyhuntersky.columnui.columns.ColumnUi;
import com.rubyhuntersky.columnui.Observer;
import com.rubyhuntersky.columnui.OnPresent;
import com.rubyhuntersky.columnui.presentations.Presentation;
import com.rubyhuntersky.columnui.presenters.Presenter;
import com.rubyhuntersky.columnui.basics.Sizelet;
import com.rubyhuntersky.columnui.columns.Column;
import com.rubyhuntersky.columnui.conditions.Human;
import com.rubyhuntersky.columnui.presentations.ResizePresentation;
import com.rubyhuntersky.columnui.tiles.TileUi;

/**
 * @author wehjin
 * @since 1/27/16.
 */

abstract public class BarUi extends BaseUi<Bar> {

    @Override
    abstract public Presentation present(Human human, Bar bar, Observer observer);

    public ColumnUi toColumn(final Sizelet heightlet) {
        final BarUi barUi = this;
        return ColumnUi.create(new OnPresent<Column>() {
            @Override
            public void onPresent(Presenter<Column> presenter) {
                final Column column = presenter.getDisplay();
                final Human human = presenter.getHuman();
                final float height = heightlet.toFloat(human, column.relatedHeight);
                final Bar bar = new Bar(height, column.fixedWidth, column.elevation, column);
                final ShiftBar shiftBar = bar.withShift();
                final Presentation presentation = barUi.present(human, shiftBar, presenter);
                final float presentationWidth = presentation.getWidth();
                final float extraWidth = column.fixedWidth - presentationWidth;
                final float anchor = .5f;
                shiftBar.setShift(extraWidth * anchor, 0);
                presenter.addPresentation(new ResizePresentation(column.fixedWidth, bar.fixedHeight, presentation));
            }
        });
    }

    public BarUi expandStart(final TileUi startUi) {
        return expandStart(startUi.toBar());
    }

    public BarUi expandStart(final BarUi startUi) {
        final BarUi ui = this;
        return create(new OnPresent<Bar>() {
            @Override
            public void onPresent(Presenter<Bar> presenter) {
                final Bar bar = presenter.getDisplay();
                final ShiftBar shiftBar = bar.withShift();
                final Human human = presenter.getHuman();
                final Presentation endPresentation = ui.present(human, shiftBar, presenter);
                final Bar startBar = bar.withRelatedWidth(endPresentation.getWidth());
                final Presentation startPresentation = startUi.present(human, startBar, presenter);
                final float startWidth = startPresentation.getWidth();
                shiftBar.setShift(startWidth, 0);
                final float combinedWidth = startWidth + endPresentation.getWidth();
                presenter.addPresentation(startPresentation);
                presenter.addPresentation(new ResizePresentation(combinedWidth, bar.fixedHeight, endPresentation));
            }
        });
    }

    public BarUi padStart(final Sizelet padlet) {
        final BarUi ui = this;
        return create(new OnPresent<Bar>() {
            @Override
            public void onPresent(Presenter<Bar> presenter) {
                final Bar bar = presenter.getDisplay();
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

    public static BarUi create(final OnPresent<Bar> onPresent) {
        return new BarUi() {
            @Override
            public Presentation present(Human human, final Bar bar, Observer observer) {
                final BasePresenter<Bar> presenter = new BasePresenter<Bar>(human, bar, observer) {
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

