package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class SendingFileEx2Drawable extends Drawable {
    private boolean isChat = false;
    private long lastUpdateTime = 0;
    private Paint paint = new Paint(1);
    private float progress;
    private boolean started = false;

    public SendingFileEx2Drawable() {
        this.paint.setColor(Theme.ACTION_BAR_SUBTITLE_COLOR);
        this.paint.setStyle(Style.STROKE);
        this.paint.setStrokeWidth((float) AndroidUtilities.dp(3.0f));
        this.paint.setStrokeCap(Cap.ROUND);
    }

    public void setIsChat(boolean value) {
        this.isChat = value;
    }

    private void update() {
        long newTime = System.currentTimeMillis();
        long dt = newTime - this.lastUpdateTime;
        this.lastUpdateTime = newTime;
        if (dt > 50) {
            dt = 50;
        }
        this.progress += ((float) dt) / 1000.0f;
        while (this.progress > 1.0f) {
            this.progress -= 1.0f;
        }
        invalidateSelf();
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
        float f;
        float f2 = 11.0f;
        int end = (int) (this.progress >= 0.5f ? (float) AndroidUtilities.dp(11.0f) : (((float) AndroidUtilities.dp(11.0f)) * this.progress) * 2.0f);
        float dp = (float) ((int) (this.progress <= 0.5f ? (float) AndroidUtilities.dp(1.0f) : (((float) AndroidUtilities.dp(11.0f)) * (this.progress - 0.5f)) * 2.0f));
        if (this.isChat) {
            f = 11.0f;
        } else {
            f = 12.0f;
        }
        float dp2 = (float) AndroidUtilities.dp(f);
        float f3 = (float) end;
        if (!this.isChat) {
            f2 = 12.0f;
        }
        canvas.drawLine(dp, dp2, f3, (float) AndroidUtilities.dp(f2), this.paint);
        if (this.started) {
            update();
        }
    }

    public void setAlpha(int alpha) {
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
