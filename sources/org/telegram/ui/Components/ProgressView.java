package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Paint;
import org.telegram.messenger.AndroidUtilities;

public class ProgressView {
    public float currentProgress = 0.0f;
    public int height;
    private Paint innerPaint = new Paint();
    private Paint outerPaint = new Paint();
    public float progressHeight = ((float) AndroidUtilities.dp(2.0f));
    public int width;

    public void setProgressColors(int innerColor, int outerColor) {
        this.innerPaint.setColor(innerColor);
        this.outerPaint.setColor(outerColor);
    }

    public void setProgress(float progress) {
        this.currentProgress = progress;
        if (progress < 0.0f) {
            this.currentProgress = 0.0f;
        } else if (progress > 1.0f) {
            this.currentProgress = 1.0f;
        }
    }

    public void draw(Canvas canvas) {
        int i = this.height;
        float f = this.progressHeight;
        Canvas canvas2 = canvas;
        canvas2.drawRect(0.0f, ((float) (i / 2)) - (f / 2.0f), (float) this.width, ((float) (i / 2)) + (f / 2.0f), this.innerPaint);
        int i2 = this.height;
        float f2 = this.progressHeight;
        canvas2.drawRect(0.0f, ((float) (i2 / 2)) - (f2 / 2.0f), ((float) this.width) * this.currentProgress, ((float) (i2 / 2)) + (f2 / 2.0f), this.outerPaint);
    }
}
