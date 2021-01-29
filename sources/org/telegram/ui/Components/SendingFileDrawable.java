package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class SendingFileDrawable extends StatusDrawable {
    Paint currentPaint;
    private boolean isChat = false;
    private long lastUpdateTime = 0;
    private float progress;
    private boolean started = false;

    public int getOpacity() {
        return 0;
    }

    public void setAlpha(int i) {
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    public SendingFileDrawable(boolean z) {
        if (z) {
            Paint paint = new Paint(1);
            this.currentPaint = paint;
            paint.setStyle(Paint.Style.STROKE);
            this.currentPaint.setStrokeCap(Paint.Cap.ROUND);
            this.currentPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        }
    }

    public void setColor(int i) {
        Paint paint = this.currentPaint;
        if (paint != null) {
            paint.setColor(i);
        }
    }

    public void setIsChat(boolean z) {
        this.isChat = z;
    }

    private void update() {
        long currentTimeMillis = System.currentTimeMillis();
        long j = currentTimeMillis - this.lastUpdateTime;
        this.lastUpdateTime = currentTimeMillis;
        if (j > 50) {
            j = 50;
        }
        this.progress += ((float) j) / 500.0f;
        while (true) {
            float f = this.progress;
            if (f > 1.0f) {
                this.progress = f - 1.0f;
            } else {
                invalidateSelf();
                return;
            }
        }
    }

    public void start() {
        this.lastUpdateTime = System.currentTimeMillis();
        this.started = true;
        invalidateSelf();
    }

    public void stop() {
        this.started = false;
    }

    public void draw(Canvas canvas) {
        Paint paint = this.currentPaint;
        if (paint == null) {
            paint = Theme.chat_statusRecordPaint;
        }
        for (int i = 0; i < 3; i++) {
            if (i == 0) {
                paint.setAlpha((int) (this.progress * 255.0f));
            } else if (i == 2) {
                paint.setAlpha((int) ((1.0f - this.progress) * 255.0f));
            } else {
                paint.setAlpha(255);
            }
            float dp = ((float) (AndroidUtilities.dp(5.0f) * i)) + (((float) AndroidUtilities.dp(5.0f)) * this.progress);
            float f = 7.0f;
            canvas.drawLine(dp, (float) AndroidUtilities.dp(this.isChat ? 3.0f : 4.0f), dp + ((float) AndroidUtilities.dp(4.0f)), (float) AndroidUtilities.dp(this.isChat ? 7.0f : 8.0f), paint);
            float dp2 = (float) AndroidUtilities.dp(this.isChat ? 11.0f : 12.0f);
            float dp3 = dp + ((float) AndroidUtilities.dp(4.0f));
            if (!this.isChat) {
                f = 8.0f;
            }
            canvas.drawLine(dp, dp2, dp3, (float) AndroidUtilities.dp(f), paint);
        }
        if (this.started) {
            update();
        }
    }

    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(18.0f);
    }

    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(14.0f);
    }
}
