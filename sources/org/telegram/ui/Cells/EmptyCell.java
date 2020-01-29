package org.telegram.ui.Cells;

import android.content.Context;
import android.view.View;
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

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(this.cellHeight, NUM));
    }
}
