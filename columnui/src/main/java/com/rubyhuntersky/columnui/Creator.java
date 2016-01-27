package com.rubyhuntersky.columnui;

import android.support.annotation.Nullable;
import android.util.Log;

import com.rubyhuntersky.columnui.conditions.Column;
import com.rubyhuntersky.columnui.presentations.BooleanPresentation;
import com.rubyhuntersky.columnui.shapes.RectangleShape;
import com.rubyhuntersky.columnui.shapes.TextShape;

/**
 * @author wehjin
 * @since 1/23/16.
 */

public class Creator {


    public static final String TAG = Creator.class.getSimpleName();

    static public Ui createLabel(final String textString, final TextStylet textStylet) {
        return Ui.create(new OnPresent() {
            @Override
            public void onPresent(Presenter presenter) {
                final Column column = presenter.getColumn();
                final TextStyle textStyle = textStylet.toStyle(presenter.getHuman(), column.verticalRange.toLength());
                final TextSize textSize = column.measureText(textString, textStyle);
                final Shape shape = new TextShape(textString, textStyle, textSize);
                final Range verticalRange = new Range(0, textSize.textHeight.height);
                Log.d(TAG, "createLabel verticalRange:" + verticalRange);
                final Frame frame = new Frame(column.horizontalRange, verticalRange, column.elevation);
                final Patch patch = column.addPatch(frame, shape);
                final BooleanPresentation presentation = new BooleanPresentation() {
                    @Override
                    public Range getVerticalRange() {
                        return verticalRange;
                    }

                    @Override
                    protected void onCancel() {
                        patch.remove();
                    }
                };
                presenter.addPresentation(presentation);
            }
        });
    }

    static public Ui createDarkTitle(final String textString) {
        return createLabel(textString, TextStylet.DARK_TITLE);
    }

    static public Ui createDarkImportant(final String textString) {
        return createLabel(textString, TextStylet.DARK_IMPORTANT);
    }

    static public Ui createPanel(final Sizelet heightlet, @Nullable final Coloret coloret) {
        return Ui.create(new OnPresent() {
            @Override
            public void onPresent(Presenter presenter) {
                final Column column = presenter.getColumn();
                final Range verticalRange = new Range(0,
                      heightlet.toFloat(presenter.getHuman(), column.verticalRange.toLength()));
                final Frame frame = new Frame(column.horizontalRange, verticalRange, column.elevation);
                final RectangleShape rectangle = new RectangleShape(coloret);
                final Patch patch = coloret == null ? null : column.addPatch(frame, rectangle);
                final Presentation presentation = new BooleanPresentation() {

                    @Override
                    public Range getVerticalRange() {
                        return verticalRange;
                    }

                    @Override
                    protected void onCancel() {
                        if (patch != null) {
                            patch.remove();
                        }
                    }
                };
                presenter.addPresentation(presentation);
            }
        });
    }
}
