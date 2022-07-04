package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class ContextProgressView extends View {
    private RectF cicleRect = new RectF();
    private int currentColorType;
    private int innerColor;
    private String innerKey;
    private Paint innerPaint = new Paint(1);
    private long lastUpdateTime;
    private int outerColor;
    private String outerKey;
    private Paint outerPaint = new Paint(1);
    private int radOffset = 0;

    public ContextProgressView(Context context, int colorType) {
        super(context);
        this.innerPaint.setStyle(Paint.Style.STROKE);
        this.innerPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.outerPaint.setStyle(Paint.Style.STROKE);
        this.outerPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.outerPaint.setStrokeCap(Paint.Cap.ROUND);
        if (colorType == 0) {
            this.innerKey = "contextProgressInner1";
            this.outerKey = "contextProgressOuter1";
        } else if (colorType == 1) {
            this.innerKey = "contextProgressInner2";
            this.outerKey = "contextProgressOuter2";
        } else if (colorType == 2) {
            this.innerKey = "contextProgressInner3";
            this.outerKey = "contextProgressOuter3";
        } else if (colorType == 3) {
            this.innerKey = "contextProgressInner4";
            this.outerKey = "contextProgressOuter4";
        }
        updateColors();
    }

    public void setColors(int innerColor2, int outerColor2) {
        this.innerKey = null;
        this.outerKey = null;
        this.innerColor = innerColor2;
        this.outerColor = outerColor2;
        updateColors();
    }

    public void updateColors() {
        String str = this.innerKey;
        if (str != null) {
            this.innerPaint.setColor(Theme.getColor(str));
        } else {
            this.innerPaint.setColor(this.innerColor);
        }
        String str2 = this.outerKey;
        if (str2 != null) {
            this.outerPaint.setColor(Theme.getColor(str2));
        } else {
            this.outerPaint.setColor(this.outerColor);
        }
        invalidate();
    }

    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        this.lastUpdateTime = System.currentTimeMillis();
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.lastUpdateTime = System.currentTimeMillis();
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (getVisibility() == 0) {
            long newTime = System.currentTimeMillis();
            this.lastUpdateTime = newTime;
            this.radOffset = (int) (((float) this.radOffset) + (((float) (360 * (newTime - this.lastUpdateTime))) / 1000.0f));
            int x = (getMeasuredWidth() / 2) - AndroidUtilities.dp(9.0f);
            int y = (getMeasuredHeight() / 2) - AndroidUtilities.dp(9.0f);
            this.cicleRect.set((float) x, (float) y, (float) (AndroidUtilities.dp(18.0f) + x), (float) (AndroidUtilities.dp(18.0f) + y));
            canvas.drawCircle((float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2), (float) AndroidUtilities.dp(9.0f), this.innerPaint);
            canvas.drawArc(this.cicleRect, (float) (this.radOffset - 90), 90.0f, false, this.outerPaint);
            invalidate();
        }
    }
}
