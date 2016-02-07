package com.rubyhuntersky.columnui;

import android.support.annotation.NonNull;

import com.rubyhuntersky.columnui.basics.Coloret;
import com.rubyhuntersky.columnui.basics.Frame;
import com.rubyhuntersky.columnui.basics.ShapeSize;
import com.rubyhuntersky.columnui.basics.Sizelet;
import com.rubyhuntersky.columnui.basics.TextSize;
import com.rubyhuntersky.columnui.basics.TextStyle;
import com.rubyhuntersky.columnui.columns.Column;
import com.rubyhuntersky.columnui.columns.ColumnUi;
import com.rubyhuntersky.columnui.columns.ColumnUi1;
import com.rubyhuntersky.columnui.columns.ColumnUi2;
import com.rubyhuntersky.columnui.columns.FullColumn;
import com.rubyhuntersky.columnui.conditions.Human;
import com.rubyhuntersky.columnui.patches.Patch;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ColumnUi2UnitTest {

    private Human human;
    private Column column;
    private List<Frame> frames = new ArrayList<>();
    private ColumnUi columnUi;
    private ColumnUi1<Integer> integerColumnUi1;

    @Test
    public void bind_producesColumnUi1() throws Exception {
        final List<String> boundConditions = new ArrayList<>();
        final ColumnUi2<String, Integer> stringIntegerColumnUi2 = ColumnUi2.create(new ColumnUi2.OnBind<String, Integer>() {
            @Override
            public ColumnUi1<Integer> onBind(String condition) {
                boundConditions.add(condition);
                return integerColumnUi1;
            }
        });

        final String condition = "Hello";
        final ColumnUi1<Integer> ui1 = stringIntegerColumnUi2.bind(condition);
        assertEquals("condition", condition, boundConditions.get(0));
        assertEquals("no frame before presentation", 0, frames.size());
        ui1.bind(3).present(human, column, Observer.EMPTY);
        assertEquals("has frame after presentation", 1, frames.size());
    }

    @Before
    public void setUp() throws Exception {
        human = new Human(17, 13);
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
        columnUi = Creator.colorColumn(Sizelet.FINGER, Coloret.BLUE);
        integerColumnUi1 = ColumnUi1.create(new ColumnUi1.OnBind<Integer>() {
            @NonNull
            @Override
            public ColumnUi onBind(Integer condition) {
                return columnUi;
            }
        });
    }
}