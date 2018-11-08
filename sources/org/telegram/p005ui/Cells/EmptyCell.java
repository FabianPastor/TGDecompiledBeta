package org.telegram.p005ui.Cells;

import android.content.Context;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;

/* renamed from: org.telegram.ui.Cells.EmptyCell */
public class EmptyCell extends FrameLayout {
    int cellHeight;

    public EmptyCell(Context context) {
        this(context, 8);
    }

    public EmptyCell(Context context, int height) {
        super(context);
        this.cellHeight = height;
    }

    public void setHeight(int height) {
        this.cellHeight = height;
        requestLayout();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(this.cellHeight, NUM));
    }
}
