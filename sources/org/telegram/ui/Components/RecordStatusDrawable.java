package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class RecordStatusDrawable extends StatusDrawable {
    int alpha = 255;
    Paint currentPaint;
    private boolean isChat = false;
    private long lastUpdateTime = 0;
    private float progress;
    private RectF rect = new RectF();
    private boolean started = false;

    public int getOpacity() {
        return 0;
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    public RecordStatusDrawable(boolean z) {
        if (z) {
            Paint paint = new Paint(1);
            this.currentPaint = paint;
            paint.setStyle(Paint.Style.STROKE);
            this.currentPaint.setStrokeCap(Paint.Cap.ROUND);
            this.currentPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        }
    }

    public void setIsChat(boolean z) {
        this.isChat = z;
    }

    public void setColor(int i) {
        Paint paint = this.currentPaint;
        if (paint != null) {
            paint.setColor(i);
        }
    }

    private void update() {
        long currentTimeMillis = System.currentTimeMillis();
        long j = currentTimeMillis - this.lastUpdateTime;
        this.lastUpdateTime = currentTimeMillis;
        if (j > 50) {
            j = 50;
        }
        this.progress += ((float) j) / 800.0f;
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
        float f = 2.0f;
        if (paint.getStrokeWidth() != ((float) AndroidUtilities.dp(2.0f))) {
            paint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        }
        canvas.save();
        int intrinsicHeight = getIntrinsicHeight() / 2;
        if (this.isChat) {
            f = 1.0f;
        }
        canvas.translate(0.0f, (float) (intrinsicHeight + AndroidUtilities.dp(f)));
        for (int i = 0; i < 4; i++) {
            if (i == 0) {
                paint.setAlpha((int) (((float) this.alpha) * this.progress));
            } else if (i == 3) {
                paint.setAlpha((int) (((float) this.alpha) * (1.0f - this.progress)));
            } else {
                paint.setAlpha(this.alpha);
            }
            float dp = ((float) (AndroidUtilities.dp(4.0f) * i)) + (((float) AndroidUtilities.dp(4.0f)) * this.progress);
            float f2 = -dp;
            this.rect.set(f2, f2, dp, dp);
            canvas.drawArc(this.rect, -15.0f, 30.0f, false, paint);
        }
        canvas.restore();
        if (this.started) {
            update();
        }
    }

    public void setAlpha(int i) {
        this.alpha = i;
    }

    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(18.0f);
    }

    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(14.0f);
    }
}
