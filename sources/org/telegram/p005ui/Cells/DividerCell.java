package org.telegram.p005ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import android.view.View.MeasureSpec;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.p005ui.ActionBar.Theme;

/* renamed from: org.telegram.ui.Cells.DividerCell */
public class DividerCell extends View {
    public DividerCell(Context context) {
        super(context);
        setPadding(0, AndroidUtilities.m9dp(8.0f), 0, AndroidUtilities.m9dp(8.0f));
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), (getPaddingTop() + getPaddingBottom()) + 1);
    }

    protected void onDraw(Canvas canvas) {
        canvas.drawLine((float) getPaddingLeft(), (float) getPaddingTop(), (float) (getWidth() - getPaddingRight()), (float) getPaddingTop(), Theme.dividerPaint);
    }
}
