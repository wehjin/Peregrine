package com.rubyhuntersky.columnui.columns;

import com.rubyhuntersky.columnui.Observer;
import com.rubyhuntersky.columnui.Reaction;
import com.rubyhuntersky.columnui.basics.Sizelet;
import com.rubyhuntersky.columnui.columns.operations.ExpandVerticalDivOperation0;
import com.rubyhuntersky.columnui.columns.operations.PadHorizontalDivOperation0;
import com.rubyhuntersky.columnui.columns.operations.PlaceBeforeDivOperation0;
import com.rubyhuntersky.columnui.presenters.OnPresent;
import com.rubyhuntersky.columnui.presenters.Presenter;
import com.rubyhuntersky.columnui.presenters.SwitchPresenter;

/**
 * @author wehjin
 * @since 1/30/16.
 */

public abstract class Div5<C1, C2, C3, C4, C5> {

    public static <C1, C2, C3, C4, C5> Div5<C1, C2, C3, C4, C5> create(final OnBind<C1, C2, C3, C4, C5> onBind) {
        return new Div5<C1, C2, C3, C4, C5>() {
            @Override
            public Div4<C2, C3, C4, C5> bind(C1 condition) {
                return onBind.onBind(condition);
            }
        };
    }

    public abstract Div4<C2, C3, C4, C5> bind(C1 condition);

    public Div5<C1, C2, C3, C4, C5> expandVertical(final Sizelet heightlet) {
        return new ExpandVerticalDivOperation0(heightlet).apply(this);
    }

    public Div5<C1, C2, C3, C4, C5> padHorizontal(final Sizelet padlet) {
        return new PadHorizontalDivOperation0(padlet).apply(this);
    }

    public Div5<C1, C2, C3, C4, C5> placeBefore(Div0 behind, int gap) {
        return new PlaceBeforeDivOperation0(behind, gap).apply(this);
    }

    public Div0 printReadEval(final Repl<C1, C2, C3, C4, C5> repl) {
        return Div0.create(new OnPresent<Column>() {

            @Override
            public void onPresent(final Presenter<Column> presenter) {
                final SwitchPresenter<Column> switchPresenter = new SwitchPresenter<>(presenter.getHuman(),
                                                                                      presenter.getDisplay(),
                                                                                      presenter);
                presenter.addPresentation(switchPresenter);
                present(repl, switchPresenter);
            }

            void present(final Repl<C1, C2, C3, C4, C5> repl, final SwitchPresenter<Column> switchPresenter) {
                if (switchPresenter.isCancelled())
                    return;

                final Div0 ui = repl.print(Div5.this);
                final Observer observer = new Observer() {
                    @Override
                    public void onReaction(Reaction reaction) {
                        if (switchPresenter.isCancelled())
                            return;

                        repl.read(reaction);
                        if (repl.eval()) {
                            present(repl, switchPresenter);
                        }
                    }

                    @Override
                    public void onEnd() {
                        switchPresenter.onEnd();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        switchPresenter.onError(throwable);
                    }
                };
                switchPresenter.addPresentation(ui.present(switchPresenter.getHuman(),
                                                           switchPresenter.getDisplay(),
                                                           observer));
            }
        });
    }


    public interface Repl<C1, C2, C3, C4, C5> {
        Div0 print(Div5<C1, C2, C3, C4, C5> unbound);
        void read(Reaction reaction);
        boolean eval();
    }


    public interface OnBind<C1, C2, C3, C4, C5> {
        Div4<C2, C3, C4, C5> onBind(C1 condition);
    }

}
