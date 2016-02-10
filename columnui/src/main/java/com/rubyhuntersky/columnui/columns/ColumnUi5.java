package com.rubyhuntersky.columnui.columns;

import com.rubyhuntersky.columnui.Observer;
import com.rubyhuntersky.columnui.Reaction;
import com.rubyhuntersky.columnui.basics.Sizelet;
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

public abstract class ColumnUi5<C1, C2, C3, C4, C5> {

    public static <C1, C2, C3, C4, C5> ColumnUi5<C1, C2, C3, C4, C5> create(final OnBind<C1, C2, C3, C4, C5> onBind) {
        return new ColumnUi5<C1, C2, C3, C4, C5>() {
            @Override
            public ColumnUi4<C2, C3, C4, C5> bind(C1 condition) {
                return onBind.onBind(condition);
            }
        };
    }

    public abstract ColumnUi4<C2, C3, C4, C5> bind(C1 condition);

    public ColumnUi5<C1, C2, C3, C4, C5> expandVertical(final Sizelet heightlet) {
        return new ExpandVerticalOperation(heightlet).applyTo(this);
    }

    public ColumnUi5<C1, C2, C3, C4, C5> padHorizontal(final Sizelet padlet) {
        return new PadHorizontalOperation(padlet).applyTo(this);
    }

    public ColumnUi5<C1, C2, C3, C4, C5> placeBefore(ColumnUi behind, int gap) {
        return new PlaceBeforeOperation(behind, gap).applyTo(this);
    }

    public ColumnUi printReadEval(final Repl<C1, C2, C3, C4, C5> repl) {
        return ColumnUi.create(new OnPresent<Column>() {

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

                final ColumnUi ui = repl.print(ColumnUi5.this);
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
        ColumnUi print(ColumnUi5<C1, C2, C3, C4, C5> unbound);
        void read(Reaction reaction);
        boolean eval();
    }


    public interface OnBind<C1, C2, C3, C4, C5> {
        ColumnUi4<C2, C3, C4, C5> onBind(C1 condition);
    }

}
