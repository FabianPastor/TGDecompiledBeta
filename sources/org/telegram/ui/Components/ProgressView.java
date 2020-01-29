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

    public void setProgressColors(int i, int i2) {
        this.innerPaint.setColor(i);
        this.outerPaint.setColor(i2);
    }

    public void setProgress(float f) {
        this.currentProgress = f;
        float f2 = this.currentProgress;
        if (f2 < 0.0f) {
            this.currentProgress = 0.0f;
        } else if (f2 > 1.0f) {
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
