package com.rubyhuntersky.gx;

import android.support.annotation.NonNull;

import com.rubyhuntersky.coloret.Coloret;
import com.rubyhuntersky.gx.basics.Frame;
import com.rubyhuntersky.gx.basics.ShapeSize;
import com.rubyhuntersky.gx.basics.Sizelet;
import com.rubyhuntersky.gx.basics.TextSize;
import com.rubyhuntersky.gx.basics.TextStyle;
import com.rubyhuntersky.gx.client.Human;
import com.rubyhuntersky.gx.client.Observer;
import com.rubyhuntersky.gx.poles.Pole;
import com.rubyhuntersky.gx.poles.Div0;
import com.rubyhuntersky.gx.poles.Div1;
import com.rubyhuntersky.gx.poles.Div2;
import com.rubyhuntersky.gx.poles.PoleSeed;
import com.rubyhuntersky.gx.internal.devices.patches.Patch;
import com.rubyhuntersky.gx.internal.shapes.Shape;
import com.rubyhuntersky.gx.client.Gx;

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
    private Pole pole;
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
        ui1.bind(3).present(human, pole, Observer.EMPTY);
        assertEquals("has frame after presentation", 1, frames.size());
    }

    @Before
    public void setUp() throws Exception {
        human = new Human(17, 13);
        pole = new PoleSeed(100, 27, 5) {
            @NonNull
            @Override
            public Patch addPatch(Frame frame, Shape shape, int argbColor) {
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
        div0 = Gx.colorColumn(Sizelet.FINGER, Coloret.BLUE);
        integerDiv1 = Div1.create(new Div1.OnBind<Integer>() {
            @NonNull
            @Override
            public Div0 onBind(Integer condition) {
                return div0;
            }
        });
    }
}