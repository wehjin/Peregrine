package com.rubyhuntersky.columnui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.rubyhuntersky.columnui.bars.Bar;
import com.rubyhuntersky.columnui.bars.BarUi;
import com.rubyhuntersky.columnui.basics.Coloret;
import com.rubyhuntersky.columnui.basics.Frame;
import com.rubyhuntersky.columnui.basics.Sizelet;
import com.rubyhuntersky.columnui.basics.TextHeight;
import com.rubyhuntersky.columnui.basics.TextSize;
import com.rubyhuntersky.columnui.basics.TextStyle;
import com.rubyhuntersky.columnui.basics.TextStylet;
import com.rubyhuntersky.columnui.columns.Column;
import com.rubyhuntersky.columnui.columns.Div0;
import com.rubyhuntersky.columnui.conditions.Human;
import com.rubyhuntersky.columnui.patches.Patch;
import com.rubyhuntersky.columnui.presentations.BooleanPresentation;
import com.rubyhuntersky.columnui.presentations.PatchPresentation;
import com.rubyhuntersky.columnui.presentations.Presentation;
import com.rubyhuntersky.columnui.presenters.OnPresent;
import com.rubyhuntersky.columnui.presenters.Presenter;
import com.rubyhuntersky.columnui.shapes.RectangleShape;
import com.rubyhuntersky.columnui.shapes.ViewShape;
import com.rubyhuntersky.columnui.tiles.Mosaic;
import com.rubyhuntersky.columnui.tiles.Tile0;

/**
 * @author wehjin
 * @since 1/23/16.
 */

public class Creator {


    public static final String TAG = Creator.class.getSimpleName();

    static public Tile0 textTile(final String textString, final TextStylet textStylet) {
        return Tile0.create(new OnPresent<Mosaic>() {
            @Override
            public void onPresent(Presenter<Mosaic> presenter) {
                final Human human = presenter.getHuman();
                final Mosaic mosaic = presenter.getDisplay();
                final TextStyle textStyle = textStylet.toStyle(human, mosaic.relatedHeight);
                final TextSize textSize = mosaic.measureText(textString, textStyle);
                final Frame frame = new Frame(textSize.textWidth, textSize.textHeight.height, mosaic.elevation);
                final TextHeight textHeight = textSize.textHeight;
                final Frame textFrame = frame.withVerticalShift(-textHeight.topPadding)
                      .withVerticalLength(textHeight.topPadding + 1.5f * textHeight.height);
                final ViewShape viewShape = new ViewShape() {
                    @Override
                    public View createView(Context context) {
                        final TextView textView = new TextView(context);
                        textView.setGravity(Gravity.TOP);
                        textView.setSingleLine();
                        textView.setTextColor(textStyle.coloret.toArgb());
                        textView.setTypeface(textStyle.typeface);
                        textView.setTextSize(textStyle.typeheight);
                        textView.setText(textString);
                        textView.setIncludeFontPadding(false);
                        textView.setContentDescription("TextTile");
                        return textView;
                    }
                };
                final Patch patch = mosaic.addPatch(textFrame, viewShape);
                presenter.addPresentation(new PatchPresentation(patch, frame));
            }
        });
    }

    static public BarUi colorBar(final Coloret coloret, final Sizelet widthlet) {
        return BarUi.create(new OnPresent<Bar>() {
            @Override
            public void onPresent(Presenter<Bar> presenter) {
                final Bar bar = presenter.getDisplay();
                final float width = widthlet.toFloat(presenter.getHuman(), bar.relatedWidth);
                final Frame frame = new Frame(width, bar.fixedHeight, bar.elevation);
                final Patch patch = bar.addPatch(frame, new RectangleShape(coloret));
                presenter.addPresentation(new PatchPresentation(patch, frame));
            }
        });
    }

    static public Div0 textColumn(final String textString, final TextStylet textStylet) {
        return textTile(textString, textStylet).toColumn();
    }

    static public Div0 gapColumn(final Sizelet heightlet) {
        return colorColumn(heightlet, null);
    }

    static public Div0 colorColumn(final Sizelet heightlet, @Nullable final Coloret coloret) {
        return Div0.create(new OnPresent<Column>() {
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
