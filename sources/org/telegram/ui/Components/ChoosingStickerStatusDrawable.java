package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class ChoosingStickerStatusDrawable extends StatusDrawable {
    int color;
    Paint fillPaint;
    boolean increment = true;
    private boolean isChat = false;
    private long lastUpdateTime = 0;
    float progress;
    private boolean started = false;
    Paint strokePaint;

    public ChoosingStickerStatusDrawable(boolean createPaint) {
        if (createPaint) {
            this.strokePaint = new Paint(1);
            this.fillPaint = new Paint(1);
            this.strokePaint.setStyle(Paint.Style.STROKE);
            this.strokePaint.setStrokeWidth(AndroidUtilities.dpf2(1.2f));
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

    public void setIsChat(boolean value) {
        this.isChat = value;
    }

    public void setColor(int color2) {
        if (this.color != color2) {
            this.fillPaint.setColor(color2);
            this.strokePaint.setColor(color2);
        }
        this.color = color2;
    }

    public void draw(Canvas canvas) {
        float xOffset;
        float cx;
        Canvas canvas2 = canvas;
        float animationProgress = Math.min(this.progress, 1.0f);
        float k = 39322;
        float p = CubicBezierInterpolator.EASE_IN.getInterpolation(animationProgress < 0.3f ? animationProgress / 0.3f : 1.0f);
        float p2 = CubicBezierInterpolator.EASE_OUT.getInterpolation(animationProgress < 0.3f ? 0.0f : (animationProgress - 0.3f) / (1.0f - 0.3f));
        float f = 2.0f;
        if (this.increment) {
            cx = (((float) AndroidUtilities.dp(2.1f)) * p) + (((float) (AndroidUtilities.dp(7.0f) - AndroidUtilities.dp(2.1f))) * (1.0f - p));
            xOffset = AndroidUtilities.dpf2(1.5f) * (1.0f - CubicBezierInterpolator.EASE_OUT.getInterpolation(this.progress / 2.0f));
        } else {
            cx = (((float) AndroidUtilities.dp(2.1f)) * (1.0f - p)) + (((float) (AndroidUtilities.dp(7.0f) - AndroidUtilities.dp(2.1f))) * p);
            xOffset = CubicBezierInterpolator.EASE_OUT_QUINT.getInterpolation(this.progress / 2.0f) * AndroidUtilities.dpf2(1.5f);
        }
        float cy = ((float) AndroidUtilities.dp(11.0f)) / 2.0f;
        float r = AndroidUtilities.dpf2(2.0f);
        float scaleOffset = (AndroidUtilities.dpf2(0.5f) * p) - (AndroidUtilities.dpf2(0.5f) * p2);
        Paint strokePaint2 = this.strokePaint;
        if (strokePaint2 == null) {
            strokePaint2 = Theme.chat_statusRecordPaint;
        }
        Paint paint = this.fillPaint;
        if (paint == null) {
            paint = Theme.chat_statusPaint;
        }
        if (strokePaint2.getStrokeWidth() != ((float) AndroidUtilities.dp(0.8f))) {
            strokePaint2.setStrokeWidth((float) AndroidUtilities.dp(0.8f));
        }
        int i = 0;
        while (i < 2) {
            canvas.save();
            canvas2.translate((strokePaint2.getStrokeWidth() / f) + xOffset + ((float) (AndroidUtilities.dp(9.0f) * i)) + ((float) getBounds().left) + AndroidUtilities.dpf2(0.2f), (strokePaint2.getStrokeWidth() / 2.0f) + AndroidUtilities.dpf2(2.0f) + ((float) getBounds().top));
            AndroidUtilities.rectTmp.set(0.0f, scaleOffset, (float) AndroidUtilities.dp(7.0f), ((float) AndroidUtilities.dp(11.0f)) - scaleOffset);
            canvas2.drawOval(AndroidUtilities.rectTmp, strokePaint2);
            canvas2.drawCircle(cx, cy, r, paint);
            canvas.restore();
            i++;
            animationProgress = animationProgress;
            k = k;
            f = 2.0f;
        }
        float f2 = k;
        if (this.started) {
            update();
        }
    }

    private void update() {
        long newTime = System.currentTimeMillis();
        long dt = newTime - this.lastUpdateTime;
        this.lastUpdateTime = newTime;
        if (dt > 16) {
            dt = 16;
        }
        float f = this.progress + (((float) dt) / 500.0f);
        this.progress = f;
        if (f >= 2.0f) {
            this.progress = 0.0f;
            this.increment = !this.increment;
        }
        invalidateSelf();
    }

    public void setAlpha(int i) {
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    public int getOpacity() {
        return 0;
    }

    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(20.0f);
    }

    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(18.0f);
    }
}
