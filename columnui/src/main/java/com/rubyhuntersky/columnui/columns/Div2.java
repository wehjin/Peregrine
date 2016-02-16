package com.rubyhuntersky.columnui.columns;

import com.rubyhuntersky.columnui.Observer;
import com.rubyhuntersky.columnui.Reaction;
import com.rubyhuntersky.columnui.basics.Sizelet;
import com.rubyhuntersky.columnui.operations.ExpandBottomWithFutureDivOperation;
import com.rubyhuntersky.columnui.operations.ExpandDownDivOperation1;
import com.rubyhuntersky.columnui.operations.ExpandVerticalOperation;
import com.rubyhuntersky.columnui.operations.PadHorizontalOperation;
import com.rubyhuntersky.columnui.operations.PlaceBeforeOperation;
import com.rubyhuntersky.columnui.presenters.OnPresent;
import com.rubyhuntersky.columnui.presenters.Presenter;
import com.rubyhuntersky.columnui.presenters.SwitchPresenter;

/**
 * @author wehjin
 * @since 1/30/16.
 */

public abstract class Div2<C1, C2> {

    public abstract Div1<C2> bind(C1 condition);

    public static <C1, C2> Div2<C1, C2> create(final OnBind<C1, C2> onBind) {
        return new Div2<C1, C2>() {
            @Override
            public Div1<C2> bind(C1 condition) {
                return onBind.onBind(condition);
            }
        };
    }

    public Div2<C1, C2> padHorizontal(final Sizelet padlet) {
        return new PadHorizontalOperation(padlet).applyTo(this);
    }

    public Div2<C1, C2> placeBefore(final Div0 background, final int gap) {
        return new PlaceBeforeOperation(background, gap).applyTo(this);
    }

    public Div3<C1, C2, Div0> expandDown() {
        return new ExpandBottomWithFutureDivOperation().applyTo(this);
    }

    public Div2<C1, C2> expandDown(final Div0 expansion) {
        return new ExpandDownDivOperation1().apply0(this, expansion);
    }

    public <C3> Div3<C1, C2, C3> expandDown(final Div1<C3> expansion) {
        return new ExpandDownDivOperation1().apply1(this, expansion);
    }

    public <C3, C4> Div4<C1, C2, C3, C4> expandDown(final Div2<C3, C4> expansion) {
        return new ExpandDownDivOperation1().apply2(this, expansion);
    }

    public <C3, C4, C5> Div5<C1, C2, C3, C4, C5> expandDown(final Div3<C3, C4, C5> expansion) {
        return new ExpandDownDivOperation1().apply3(this, expansion);
    }

    public Div2<C1, C2> expandVertical(final Sizelet heightlet) {
        return new ExpandVerticalOperation(heightlet).applyTo(this);
    }

    public Div0 printReadEval(final Repl<C1, C2> repl) {
        final Div2<C1, C2> div2 = this;
        return Div0.create(new OnPresent<Column>() {

            @Override
            public void onPresent(final Presenter<Column> presenter) {
                final SwitchPresenter<Column> switchPresenter = new SwitchPresenter<>(presenter.getHuman(),
                                                                                      presenter.getDisplay(),
                                                                                      presenter);
                presenter.addPresentation(switchPresenter);
                present(repl, switchPresenter);
            }

            void present(final Repl<C1, C2> repl, final SwitchPresenter<Column> presenter) {
                if (presenter.isCancelled())
                    return;
                final Div0 print = repl.print(div2);
                presenter.addPresentation(print.present(presenter.getHuman(), presenter.getDisplay(), new Observer() {
                    @Override
                    public void onReaction(Reaction reaction) {
                        if (presenter.isCancelled())
                            return;
                        repl.read(reaction);
                        if (repl.eval()) {
                            present(repl, presenter);
                        }
                    }

                    @Override
                    public void onEnd() {
                        presenter.onEnd();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        presenter.onError(throwable);
                    }
                }));
            }
        });
    }

    public interface Repl<C1, C2> {
        Div0 print(Div2<C1, C2> unbound);
        void read(Reaction reaction);
        boolean eval();
    }


    public interface OnBind<C1, C2> {
        Div1<C2> onBind(C1 condition);
    }

}
