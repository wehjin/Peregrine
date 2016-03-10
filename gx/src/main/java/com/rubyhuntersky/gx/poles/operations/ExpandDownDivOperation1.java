package com.rubyhuntersky.gx.poles.operations;

import com.rubyhuntersky.gx.poles.Pole;
import com.rubyhuntersky.gx.poles.Div0;
import com.rubyhuntersky.gx.poles.DelayPole;
import com.rubyhuntersky.gx.client.Human;
import com.rubyhuntersky.gx.client.Presentation;
import com.rubyhuntersky.gx.internal.presentations.ResizePresentation;
import com.rubyhuntersky.gx.internal.presenters.OnPresent;
import com.rubyhuntersky.gx.internal.presenters.Presenter;

/**
 * @author wehjin
 * @since 2/9/16.
 */

public class ExpandDownDivOperation1 extends DivOperation1 {

    @Override
    public Div0 apply0(final Div0 base, final Div0 expansion) {
        return Div0.create(new OnPresent<Pole>() {
            @Override
            public void onPresent(Presenter<Pole> presenter) {
                Human human = presenter.getHuman();
                Pole pole = presenter.getDevice();

                final DelayPole delayColumn = pole.withDelay();
                final Presentation topPresentation = base.present(human, delayColumn, presenter);
                final float topHeight = topPresentation.getHeight();
                final Pole bottomPole = pole.withRelatedHeight(topHeight).withShift(0, topHeight);
                final Presentation bottomPresentation = expansion.present(human, bottomPole, presenter);
                final Presentation bottomResize =
                      new ResizePresentation(bottomPresentation.getWidth(), topHeight + bottomPresentation
                            .getHeight(), bottomPresentation);
                delayColumn.endDelay();
                presenter.addPresentation(bottomResize);
                presenter.addPresentation(topPresentation);
            }
        });
    }
}
