package com.rubyhuntersky.columnui.columns;

import com.rubyhuntersky.columnui.Observer;
import com.rubyhuntersky.columnui.Reaction;
import com.rubyhuntersky.columnui.basics.Sizelet;
import com.rubyhuntersky.columnui.operations.ExpandBottomOperation;
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

public abstract class ColumnUi3<C1, C2, C3> {

    public static <C1, C2, C3> ColumnUi3<C1, C2, C3> create(final OnBind<C1, C2, C3> onBind) {
        return new ColumnUi3<C1, C2, C3>() {
            @Override
            public ColumnUi2<C2, C3> bind(C1 condition) {
                return onBind.onBind(condition);
            }
        };
    }

    public abstract ColumnUi2<C2, C3> bind(C1 condition);

    public ColumnUi3<C1, C2, C3> expandVertical(final Sizelet heightlet) {
        return new ExpandVerticalOperation(heightlet).applyTo(this);
    }

    public ColumnUi3<C1, C2, C3> padHorizontal(final Sizelet padlet) {
        return new PadHorizontalOperation(padlet).applyTo(this);
    }

    public ColumnUi3<C1, C2, C3> placeBefore(ColumnUi behind, int gap) {
        return new PlaceBeforeOperation(behind, gap).applyTo(this);
    }

    public ColumnUi3<C1, C2, C3> expandBottom(ColumnUi expansion) {
        return new ExpandBottomOperation(expansion).applyTo(this);
    }

    public ColumnUi printReadEval(final Repl<C1, C2, C3> repl) {
        return ColumnUi.create(new OnPresent<Column>() {

            @Override
            public void onPresent(final Presenter<Column> presenter) {
                final SwitchPresenter<Column> switchPresenter = new SwitchPresenter<>(presenter.getHuman(),
                                                                                      presenter.getDisplay(),
                                                                                      presenter);
                presenter.addPresentation(switchPresenter);
                present(repl, switchPresenter);
            }

            void present(final Repl<C1, C2, C3> repl, final SwitchPresenter<Column> presenter) {
                if (presenter.isCancelled())
                    return;

                final ColumnUi ui = repl.print(ColumnUi3.this);
                final Observer observer = new Observer() {
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
                };
                presenter.addPresentation(ui.present(presenter.getHuman(), presenter.getDisplay(), observer));
            }
        });
    }


    public interface Repl<C1, C2, C3> {
        ColumnUi print(ColumnUi3<C1, C2, C3> unbound);
        void read(Reaction reaction);
        boolean eval();
    }


    public interface OnBind<C1, C2, C3> {
        ColumnUi2<C2, C3> onBind(C1 condition);
    }

}
