package com.rubyhuntersky.columnui;

import android.support.annotation.NonNull;

import com.rubyhuntersky.columnui.columns.ColumnUi;
import com.rubyhuntersky.columnui.columns.FullColumn;
import com.rubyhuntersky.columnui.basics.Coloret;
import com.rubyhuntersky.columnui.basics.Frame;
import com.rubyhuntersky.columnui.basics.ShapeSize;
import com.rubyhuntersky.columnui.basics.Sizelet;
import com.rubyhuntersky.columnui.basics.Sizelet.Ruler;
import com.rubyhuntersky.columnui.basics.TextSize;
import com.rubyhuntersky.columnui.basics.TextStyle;
import com.rubyhuntersky.columnui.columns.Column;
import com.rubyhuntersky.columnui.conditions.Human;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ColumnUiUnitTest {

    private Human human;
    private Column column;
    private ArrayList<Frame> frames;
    private ColumnUi padTopUi;

    @Before
    public void setUp() throws Exception {
        human = new Human(17, 13);
        frames = new ArrayList<>();
        column = new FullColumn(100, 27, 5) {
            @NonNull
            @Override
            public Patch addPatch(Frame frame, Shape shape) {
                frames.add(frame);
                return Patch.EMPTY;
            }

            @NonNull
            @Override
            public TextSize measureText(String text, TextStyle textStyle) {
                return TextSize.ZERO;
            }

            @NonNull
            @Override
            public ShapeSize measureShape(Shape shape) {
                return ShapeSize.ZERO;
            }
        };
        padTopUi = Creator.colorColumn(Sizelet.ofPortion(10, Ruler.PIXEL), Coloret.BLACK)
                          .padTop(Sizelet.ofPortion(15, Ruler.PIXEL));
    }

    @Test
    public void padTop_addsPaddingToFrameTop() throws Exception {
        final Presentation present = padTopUi.present(human, column, Observer.EMPTY);
        present.cancel();
        assertEquals(15, frames.get(0).vertical.start, .001);
    }

    @Test
    public void padTop_addsPaddingToHeight() throws Exception {
        final Presentation present = padTopUi.present(human, column, Observer.EMPTY);
        final float height = present.getHeight();
        present.cancel();
        assertEquals(25, height, .001);
    }


    @Test
    public void presentation_takesWidthFromColumn() throws Exception {
        final ColumnUi columnUi = ColumnUi.create(new OnPresent<Column>() {
            @Override
            public void onPresent(Presenter<Column> presenter) {
                // Do nothing.
            }
        });
        final Presentation presentation = columnUi.present(human, column, Observer.EMPTY);
        assertEquals(100, presentation.getWidth(), .001);
    }
}