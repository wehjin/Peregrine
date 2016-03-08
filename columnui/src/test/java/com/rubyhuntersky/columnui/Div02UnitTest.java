package com.rubyhuntersky.columnui;

import android.support.annotation.NonNull;

import com.rubyhuntersky.coloret.Coloret;
import com.rubyhuntersky.columnui.basics.Frame;
import com.rubyhuntersky.columnui.basics.ShapeSize;
import com.rubyhuntersky.columnui.basics.Sizelet;
import com.rubyhuntersky.columnui.basics.TextSize;
import com.rubyhuntersky.columnui.basics.TextStyle;
import com.rubyhuntersky.columnui.columns.Column;
import com.rubyhuntersky.columnui.columns.Div0;
import com.rubyhuntersky.columnui.columns.Div1;
import com.rubyhuntersky.columnui.columns.Div2;
import com.rubyhuntersky.columnui.columns.FullColumn;
import com.rubyhuntersky.columnui.patches.Patch;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class Div02UnitTest {

    private Human human;
    private Column column;
    private List<Frame> frames = new ArrayList<>();
    private Div0 div0;
    private Div1<Integer> integerDiv1;

    @Test
    public void bind_producesColumnUi1() throws Exception {
        final List<String> boundConditions = new ArrayList<>();
        final Div2<String, Integer> stringIntegerDiv2 = Div2.create(new Div2.OnBind<String, Integer>() {
            @Override
            public Div1<Integer> onBind(String condition) {
                boundConditions.add(condition);
                return integerDiv1;
            }
        });

        final String condition = "Hello";
        final Div1<Integer> ui1 = stringIntegerDiv2.bind(condition);
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
        div0 = Creator.colorColumn(Sizelet.FINGER, Coloret.BLUE);
        integerDiv1 = Div1.create(new Div1.OnBind<Integer>() {
            @NonNull
            @Override
            public Div0 onBind(Integer condition) {
                return div0;
            }
        });
    }
}