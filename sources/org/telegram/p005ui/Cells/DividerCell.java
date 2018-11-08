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
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.m10dp(16.0f) + 1);
    }

    protected void onDraw(Canvas canvas) {
        canvas.drawLine((float) getPaddingLeft(), (float) AndroidUtilities.m10dp(8.0f), (float) (getWidth() - getPaddingRight()), (float) AndroidUtilities.m10dp(8.0f), Theme.dividerPaint);
    }
}
