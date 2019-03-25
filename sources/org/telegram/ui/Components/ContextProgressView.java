package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class ContextProgressView extends View {
    private RectF cicleRect = new RectF();
    private int currentColorType;
    private String innerKey;
    private Paint innerPaint = new Paint(1);
    private long lastUpdateTime;
    private String outerKey;
    private Paint outerPaint = new Paint(1);
    private int radOffset = 0;

    public ContextProgressView(Context context, int colorType) {
        super(context);
        this.innerPaint.setStyle(Style.STROKE);
        this.innerPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.outerPaint.setStyle(Style.STROKE);
        this.outerPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.outerPaint.setStrokeCap(Cap.ROUND);
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

    public void updateColors() {
        this.innerPaint.setColor(Theme.getColor(this.innerKey));
        this.outerPaint.setColor(Theme.getColor(this.outerKey));
        invalidate();
    }

    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        this.lastUpdateTime = System.currentTimeMillis();
        invalidate();
    }

    /* Access modifiers changed, original: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.lastUpdateTime = System.currentTimeMillis();
        invalidate();
    }

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        if (getVisibility() == 0) {
            long newTime = System.currentTimeMillis();
            long dt = newTime - this.lastUpdateTime;
            this.lastUpdateTime = newTime;
            this.radOffset = (int) (((float) this.radOffset) + (((float) (360 * dt)) / 1000.0f));
            int x = (getMeasuredWidth() / 2) - AndroidUtilities.dp(9.0f);
            int y = (getMeasuredHeight() / 2) - AndroidUtilities.dp(9.0f);
            this.cicleRect.set((float) x, (float) y, (float) (AndroidUtilities.dp(18.0f) + x), (float) (AndroidUtilities.dp(18.0f) + y));
            canvas.drawCircle((float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2), (float) AndroidUtilities.dp(9.0f), this.innerPaint);
            canvas.drawArc(this.cicleRect, (float) (this.radOffset - 90), 90.0f, false, this.outerPaint);
            invalidate();
        }
    }
}
