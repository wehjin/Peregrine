package com.rubyhuntersky.gx;

import android.support.annotation.NonNull;

import com.rubyhuntersky.gx.bars.Bar;
import com.rubyhuntersky.gx.bars.Span0;
import com.rubyhuntersky.gx.bars.BarSeed;
import com.rubyhuntersky.gx.basics.Frame;
import com.rubyhuntersky.gx.basics.ShapeSize;
import com.rubyhuntersky.gx.basics.TextSize;
import com.rubyhuntersky.gx.basics.TextStyle;
import com.rubyhuntersky.gx.client.Human;
import com.rubyhuntersky.gx.client.Observer;
import com.rubyhuntersky.gx.client.Presentation;
import com.rubyhuntersky.gx.internal.devices.patches.Patch;
import com.rubyhuntersky.gx.internal.shapes.Shape;
import com.rubyhuntersky.gx.internal.presenters.OnPresent;
import com.rubyhuntersky.gx.internal.presenters.Presenter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static com.rubyhuntersky.gx.client.Gx.colorBar;
import static com.rubyhuntersky.coloret.Coloret.BLACK;
import static com.rubyhuntersky.coloret.Coloret.GREEN;
import static com.rubyhuntersky.gx.basics.Sizelet.pixels;
import static org.junit.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class Span0UnitTest {

    private Human human;
    private Bar bar;
    private ArrayList<Frame> frames;
    private Presentation presentation;

    @Before
    public void setUp() throws Exception {
        human = new Human(17, 13);
        frames = new ArrayList<>();
        bar = new BarSeed(100, 27, 5) {

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
    }

    @After
    public void tearDown() throws Exception {
        if (presentation != null) {
            presentation.cancel();
            presentation = null;
        }
    }

    @Test
    public void expandStart_movesEndFrame() throws Exception {
        final Span0 ui = colorBar(BLACK, pixels(30)).expandStart(colorBar(GREEN, pixels(50)));
        presentation = ui.present(human, bar, Observer.EMPTY);
        assertEquals(50, frames.get(1).horizontal.start, .0001);
    }

    @Test
    public void expandStart_combinesWidths() throws Exception {
        final Span0 ui = colorBar(BLACK, pixels(30)).expandStart(colorBar(GREEN, pixels(50)));
        presentation = ui.present(human, bar, Observer.EMPTY);
        assertEquals(80, presentation.getWidth(), .0001);
    }

    @Test
    public void padStart_movesFrame() throws Exception {
        final Span0 padStartUi = colorBar(BLACK, pixels(30)).padStart(pixels(10));
        presentation = padStartUi.present(human, bar, Observer.EMPTY);
        assertEquals(10, frames.get(0).horizontal.start, .0001);
    }

    @Test
    public void padStart_increasesWidth() throws Exception {
        final Span0 padStartUi = colorBar(BLACK, pixels(30)).padStart(pixels(10));
        presentation = padStartUi.present(human, bar, Observer.EMPTY);
        assertEquals(40, presentation.getWidth(), .0001);
    }

    @Test
    public void presentation_takesHeightFromBar() throws Exception {
        final Span0 span0 = Span0.create(new OnPresent<Bar>() {
            @Override
            public void onPresent(Presenter<Bar> presenter) {
                // Do nothing.
            }
        });
        final Presentation presentation = span0.present(human, bar, Observer.EMPTY);
        assertEquals(100, presentation.getHeight(), .001);
    }
}