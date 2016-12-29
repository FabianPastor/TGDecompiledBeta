package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View.MeasureSpec;
import org.telegram.messenger.AndroidUtilities;

public class DividerCell extends BaseCell {
    private static Paint paint;

    public DividerCell(Context context) {
        super(context);
        if (paint == null) {
            paint = new Paint();
            paint.setColor(-2500135);
            paint.setStrokeWidth(1.0f);
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(16.0f) + 1);
    }

    protected void onDraw(Canvas canvas) {
        canvas.drawLine((float) getPaddingLeft(), (float) AndroidUtilities.dp(8.0f), (float) (getWidth() - getPaddingRight()), (float) AndroidUtilities.dp(8.0f), paint);
    }
}
