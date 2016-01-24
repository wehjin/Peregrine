package com.rubyhuntersky.columnui.conditions;

import android.support.annotation.NonNull;

import com.rubyhuntersky.columnui.Coloret;
import com.rubyhuntersky.columnui.Condition;
import com.rubyhuntersky.columnui.Frame;
import com.rubyhuntersky.columnui.Patch;
import com.rubyhuntersky.columnui.Range;
import com.rubyhuntersky.columnui.Shape;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wehjin
 * @since 1/23/16.
 */

abstract public class Column extends Condition {

    @NonNull
    final public Range horizontalRange;
    final public int elevation;

    public Column(@NonNull Range horizontalRange, int elevation) {
        this.horizontalRange = horizontalRange;
        this.elevation = elevation;
    }

    @NonNull
    abstract public Patch addPatch(Frame frame, Shape shape, Coloret color);

    public Column withElevation(int elevation) {
        if (elevation == this.elevation) return this;

        final Column original = this;
        return new Column(horizontalRange, elevation) {
            @NonNull
            @Override
            public Patch addPatch(Frame frame, Shape shape, Coloret color) {
                return original.addPatch(frame, shape, color);
            }
        };
    }

    public Column withHorizontalRange(Range range) {
        final Column original = this;
        if (original.horizontalRange == range) {
            return this;
        }
        return new Column(range, elevation) {
            @NonNull
            @Override
            public Patch addPatch(Frame frame, Shape shape, Coloret color) {
                return original.addPatch(frame, shape, color);
            }
        };
    }

    public VerticalShiftColumn withVerticalShift(Column original) {
        return new VerticalShiftColumn(original);
    }


    static public class VerticalShiftColumn extends Column {

        @NonNull
        private final Column original;
        private boolean didShift;
        private float shift;
        private List<VerticalShiftPatch> pending = new ArrayList<>();

        public VerticalShiftColumn(@NonNull Column original) {
            super(original.horizontalRange, original.elevation);
            this.original = original;
        }

        @NonNull
        @Override
        public Patch addPatch(Frame frame, Shape shape, Coloret color) {
            final VerticalShiftPatch patch = new VerticalShiftPatch(frame, shape, color, original);
            if (didShift) {
                patch.setVerticalShift(shift);
            } else {
                pending.add(patch);
            }
            return patch;
        }

        public void setVerticalShift(float shift) {
            if (didShift) {
                throw new IllegalStateException("Vertical shift already present");
            }
            didShift = true;
            this.shift = shift;
            final ArrayList<VerticalShiftPatch> toShift = new ArrayList<>(pending);
            pending.clear();
            for (VerticalShiftPatch patch : toShift) {
                patch.setVerticalShift(shift);
            }
        }
    }

    static public class VerticalShiftPatch implements Patch {

        private final Frame frame;
        private final Shape shape;
        private final Coloret color;
        private final Column column;
        private Patch patch;

        public VerticalShiftPatch(Frame frame, Shape shape, Coloret color, @NonNull Column column) {

            this.frame = frame;
            this.shape = shape;
            this.color = color;
            this.column = column;
        }

        public void setVerticalShift(float shift) {
            if (patch != null) {
                throw new IllegalStateException("Already shifted");
            }
            Frame newFrame = this.frame.withVerticalShift(shift);
            patch = column.addPatch(newFrame, shape, color);
        }

        @Override
        public void remove() {
            if (patch != null) {
                patch.remove();
                patch = null;
            }
        }
    }
}
