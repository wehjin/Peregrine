package com.rubyhuntersky.columnui;

import android.support.annotation.Nullable;

import com.rubyhuntersky.columnui.basics.Coloret;
import com.rubyhuntersky.columnui.basics.Frame;
import com.rubyhuntersky.columnui.basics.Sizelet;
import com.rubyhuntersky.columnui.basics.TextSize;
import com.rubyhuntersky.columnui.basics.TextStyle;
import com.rubyhuntersky.columnui.basics.TextStylet;
import com.rubyhuntersky.columnui.columns.Column;
import com.rubyhuntersky.columnui.presentations.BooleanPresentation;
import com.rubyhuntersky.columnui.shapes.RectangleShape;
import com.rubyhuntersky.columnui.shapes.TextShape;

/**
 * @author wehjin
 * @since 1/23/16.
 */

public class Creator {


    public static final String TAG = Creator.class.getSimpleName();

    static public ColumnUi createLabel(final String textString, final TextStylet textStylet) {
        return ColumnUi.create(new OnPresent<Column>() {
            @Override
            public void onPresent(Presenter<Column> presenter) {
                final Column column = presenter.getDisplay();
                final TextStyle textStyle = textStylet.toStyle(presenter.getHuman(), column.relatedHeight);
                final TextSize textSize = column.measureText(textString, textStyle);
                final Shape shape = new TextShape(textString, textStyle, textSize);
                final Frame frame = new Frame(column.fixedWidth, textSize.textHeight.height, column.elevation);
                final Patch patch = column.addPatch(frame, shape);
                final BooleanPresentation presentation = new BooleanPresentation() {

                    @Override
                    public float getWidth() {
                        return column.fixedWidth;
                    }

                    @Override
                    public float getHeight() {
                        return textSize.textHeight.height;
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

    static public ColumnUi createDarkTitle(final String textString) {
        return createLabel(textString, TextStylet.DARK_TITLE);
    }

    static public ColumnUi createDarkImportant(final String textString) {
        return createLabel(textString, TextStylet.DARK_IMPORTANT);
    }

    static public ColumnUi createPanel(final Sizelet heightlet, @Nullable final Coloret coloret) {
        return ColumnUi.create(new OnPresent<Column>() {
            @Override
            public void onPresent(Presenter<Column> presenter) {
                final Column column = presenter.getDisplay();
                final float height = heightlet.toFloat(presenter.getHuman(), column.relatedHeight);
                final Frame frame = new Frame(column.fixedWidth, height, column.elevation);
                final RectangleShape rectangle = new RectangleShape(coloret);
                final Patch patch = coloret == null ? null : column.addPatch(frame, rectangle);
                final Presentation presentation = new BooleanPresentation() {

                    @Override
                    public float getWidth() {
                        return column.fixedWidth;
                    }

                    @Override
                    public float getHeight() {
                        return height;
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
