package com.rubyhuntersky.columnui.columns;

import android.util.Pair;

import com.rubyhuntersky.columnui.Observer;
import com.rubyhuntersky.columnui.Reaction;
import com.rubyhuntersky.columnui.basics.Sizelet;
import com.rubyhuntersky.columnui.presenters.OnPresent;
import com.rubyhuntersky.columnui.presenters.Presenter;
import com.rubyhuntersky.columnui.presenters.SwitchPresenter;

/**
 * @author wehjin
 * @since 1/30/16.
 */

public abstract class ColumnUi2<C1, C2> {

    public abstract ColumnUi1<C1> bind(C2 condition);

    public static <C1, C2> ColumnUi2<C1, C2> create(final OnBind<C1, C2> onBind) {
        return new ColumnUi2<C1, C2>() {
            @Override
            public ColumnUi1<C1> bind(C2 condition) {
                return onBind.onBind(condition);
            }
        };
    }

    public ColumnUi2<C1, C2> padBottom(final Sizelet heightlet) {
        return create(new OnBind<C1, C2>() {
            @Override
            public ColumnUi1<C1> onBind(C2 condition) {
                return ColumnUi2.this.bind(condition).padBottom(heightlet);
            }
        });
    }

    public ColumnUi2<C1, C2> padHorizontal(final Sizelet padlet) {
        return create(new OnBind<C1, C2>() {
            @Override
            public ColumnUi1<C1> onBind(C2 condition) {
                return ColumnUi2.this.bind(condition).padHorizontal(padlet);
            }
        });
    }

    public ColumnUi2<C1, C2> placeBefore(final ColumnUi columnUi, final int gap) {
        return create(new OnBind<C1, C2>() {
            @Override
            public ColumnUi1<C1> onBind(C2 condition) {
                return ColumnUi2.this.bind(condition).placeBefore(columnUi, gap);
            }
        });
    }

    public ColumnUi printReadEval(final Repl<C1, C2> repl) {
        final ColumnUi2<C1, C2> columnUi2 = this;
        return ColumnUi.create(new OnPresent<Column>() {

            @Override
            public void onPresent(final Presenter<Column> presenter) {
                final SwitchPresenter<Column> switchPresenter = new SwitchPresenter<>(presenter.getHuman(),
                      presenter.getDisplay(), presenter);
                presenter.addPresentation(switchPresenter);
                present(repl, switchPresenter);
            }

            void present(final Repl<C1, C2> repl, final SwitchPresenter<Column> presenter) {
                if (presenter.isCancelled()) return;
                final Pair<C1, C2> print = repl.print();
                final ColumnUi bound = columnUi2.bind(print.second).bind(print.first);
                presenter.addPresentation(bound.present(presenter.getHuman(), presenter.getDisplay(), new Observer() {
                    @Override
                    public void onReaction(Reaction reaction) {
                        if (presenter.isCancelled()) return;
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
        Pair<C1, C2> print();
        void read(Reaction reaction);
        boolean eval();
    }


    public interface OnBind<C1, C2> {
        ColumnUi1<C1> onBind(C2 condition);
    }

}
