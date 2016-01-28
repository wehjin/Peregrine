package com.rubyhuntersky.columnui;

import android.support.annotation.NonNull;

import com.rubyhuntersky.columnui.bars.Bar;
import com.rubyhuntersky.columnui.basics.Frame;
import com.rubyhuntersky.columnui.basics.TextSize;
import com.rubyhuntersky.columnui.basics.TextStyle;
import com.rubyhuntersky.columnui.conditions.Human;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class BarUiUnitTest {
    @Test
    public void presentation_takesHeightFromBar() throws Exception {
        final BarUi barUi = BarUi.create(new OnPresent<Bar>() {
            @Override
            public void onPresent(Presenter<Bar> presenter) {
                // Do nothing.
            }
        });
        final Human human = new Human(17, 13);
        final Bar bar = new Bar(100, 27, 5) {

            @NonNull
            @Override
            public Patch addPatch(Frame frame, Shape shape) {
                return Patch.EMPTY;
            }

            @NonNull
            @Override
            public TextSize measureText(String text, TextStyle textStyle) {
                return TextSize.ZERO;
            }
        };
        final Presentation presentation = barUi.present(human, bar, Observer.EMPTY);
        assertEquals(100, presentation.getHeight(), .001);
    }
}