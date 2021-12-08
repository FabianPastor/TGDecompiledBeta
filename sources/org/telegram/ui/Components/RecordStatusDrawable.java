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

    public RecordStatusDrawable(boolean createPaint) {
        if (createPaint) {
            Paint paint = new Paint(1);
            this.currentPaint = paint;
            paint.setStyle(Paint.Style.STROKE);
            this.currentPaint.setStrokeCap(Paint.Cap.ROUND);
            this.currentPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        }
    }

    public void setIsChat(boolean value) {
        this.isChat = value;
    }

    public void setColor(int color) {
        Paint paint = this.currentPaint;
        if (paint != null) {
            paint.setColor(color);
        }
    }

    private void update() {
        long newTime = System.currentTimeMillis();
        long dt = newTime - this.lastUpdateTime;
        this.lastUpdateTime = newTime;
        if (dt > 50) {
            dt = 50;
        }
        this.progress += ((float) dt) / 800.0f;
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
        for (int a = 0; a < 4; a++) {
            if (a == 0) {
                paint.setAlpha((int) (((float) this.alpha) * this.progress));
            } else if (a == 3) {
                paint.setAlpha((int) (((float) this.alpha) * (1.0f - this.progress)));
            } else {
                paint.setAlpha(this.alpha);
            }
            float side = ((float) (AndroidUtilities.dp(4.0f) * a)) + (((float) AndroidUtilities.dp(4.0f)) * this.progress);
            this.rect.set(-side, -side, side, side);
            canvas.drawArc(this.rect, -15.0f, 30.0f, false, paint);
        }
        canvas.restore();
        if (this.started) {
            update();
        }
    }

    public void setAlpha(int alpha2) {
        this.alpha = alpha2;
    }

    public void setColorFilter(ColorFilter cf) {
    }

    public int getOpacity() {
        return 0;
    }

    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(18.0f);
    }

    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(14.0f);
    }
}
