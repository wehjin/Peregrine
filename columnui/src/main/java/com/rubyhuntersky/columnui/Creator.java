package com.rubyhuntersky.columnui;

import com.rubyhuntersky.columnui.conditions.Column;
import com.rubyhuntersky.columnui.conditions.Human;

/**
 * @author wehjin
 * @since 1/23/16.
 */

public class Creator {

    static public Ui createPanel(final Coloret coloret, final Heightlet heightlet) {
        return Ui.create(new OnPresent() {
            @Override
            public void onPresent(Presenter presenter) {
                final Column column = presenter.getColumn();
                final Human human = presenter.getHuman();
                final Widthlet widthlet = column.widthlet;
                final float width = widthlet.toFloat(human, 0); // TODO rethink this
                final float height = heightlet.toFloat(human, width);
                final Frame frame = new Frame(width, height);
                final Patch patch = column.addPatch(frame, Shape.RECTANGLE, coloret);
                final Presentation presentation = new BooleanPresentation() {
                    @Override
                    public float getHeight() {
                        return height;
                    }

                    @Override
                    protected void onCancel() {
                        patch.remove();
                    }
                };
                presenter.addPresentation(presentation);
            }
        });
    }

    public Ui createLabel(TextStyle textStyle, String text) {
        return Ui.create(new OnPresent() {
            @Override
            public void onPresent(Presenter presenter) {
                final Human human = presenter.getHuman();
                final Column column = presenter.getColumn();
                final Presentation presentation = new BooleanPresentation() {
                    @Override
                    public float getHeight() {
                        return 0;
                    }

                    @Override
                    protected void onCancel() {
                    }
                };
                presenter.addPresentation(presentation);
            }
        });
    }

    public Ui2<TextStyle, String> createLabel() {
        return Ui2.create(new OnBind<TextStyle, Ui1<String>>() {
            @Override
            public Ui1<String> onBind(final TextStyle textStyle) {
                return Ui1.create(new OnBind<String, Ui>() {
                    @Override
                    public Ui onBind(String text) {
                        return createLabel(textStyle, text);
                    }
                });
            }
        }, new OnBind<String, Ui1<TextStyle>>() {
            @Override
            public Ui1<TextStyle> onBind(final String text) {
                return Ui1.create(new OnBind<TextStyle, Ui>() {
                    @Override
                    public Ui onBind(TextStyle textStyle) {
                        return createLabel(textStyle, text);
                    }
                });
            }
        });
    }

}
