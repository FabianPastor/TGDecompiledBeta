package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class DividerCell extends View {
    boolean forceDarkTheme;
    Paint paint;

    public DividerCell(Context context) {
        super(context);
        setPadding(0, AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        setMeasuredDimension(View.MeasureSpec.getSize(i), getPaddingTop() + getPaddingBottom() + 1);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        Paint paint2 = Theme.dividerPaint;
        if (this.forceDarkTheme) {
            if (this.paint == null) {
                Paint paint3 = new Paint();
                this.paint = paint3;
                paint3.setColor(ColorUtils.blendARGB(-16777216, Theme.getColor("voipgroup_dialogBackground"), 0.2f));
            }
            paint2 = this.paint;
        }
        Canvas canvas2 = canvas;
        canvas2.drawLine((float) getPaddingLeft(), (float) getPaddingTop(), (float) (getWidth() - getPaddingRight()), (float) getPaddingTop(), paint2);
    }

    public void setForceDarkTheme(boolean z) {
        this.forceDarkTheme = z;
    }
}
