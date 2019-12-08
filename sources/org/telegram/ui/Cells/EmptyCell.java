package org.telegram.ui.Cells;

import android.content.Context;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;

public class EmptyCell extends FrameLayout {
    private int cellHeight;

    public EmptyCell(Context context) {
        this(context, 8);
    }

    public EmptyCell(Context context, int i) {
        super(context);
        this.cellHeight = i;
    }

    public void setHeight(int i) {
        if (this.cellHeight != i) {
            this.cellHeight = i;
            requestLayout();
        }
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(this.cellHeight, NUM));
    }
}
