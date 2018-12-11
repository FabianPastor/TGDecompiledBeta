package org.telegram.p005ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View.MeasureSpec;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.p005ui.ActionBar.Theme;

/* renamed from: org.telegram.ui.Cells.HashtagSearchCell */
public class HashtagSearchCell extends TextView {
    private boolean needDivider;

    public HashtagSearchCell(Context context) {
        super(context);
        setGravity(16);
        setPadding(AndroidUtilities.m9dp(16.0f), 0, AndroidUtilities.m9dp(16.0f), 0);
        setTextSize(1, 17.0f);
        setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
    }

    public void setNeedDivider(boolean value) {
        this.needDivider = value;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.m9dp(48.0f) + 1);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.needDivider) {
            canvas.drawLine(0.0f, (float) (getHeight() - 1), (float) getWidth(), (float) (getHeight() - 1), Theme.dividerPaint);
        }
    }
}
