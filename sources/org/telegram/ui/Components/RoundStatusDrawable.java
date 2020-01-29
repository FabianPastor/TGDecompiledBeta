package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class RoundStatusDrawable extends StatusDrawable {
    private boolean isChat = false;
    private long lastUpdateTime = 0;
    private float progress;
    private int progressDirection = 1;
    private boolean started = false;

    public int getOpacity() {
        return 0;
    }

    public void setAlpha(int i) {
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    public void setIsChat(boolean z) {
        this.isChat = z;
    }

    private void update() {
        long currentTimeMillis = System.currentTimeMillis();
        long j = currentTimeMillis - this.lastUpdateTime;
        this.lastUpdateTime = currentTimeMillis;
        long j2 = 50;
        if (j <= 50) {
            j2 = j;
        }
        float f = this.progress;
        int i = this.progressDirection;
        this.progress = f + (((float) (((long) i) * j2)) / 400.0f);
        if (i > 0 && this.progress >= 1.0f) {
            this.progressDirection = -1;
            this.progress = 1.0f;
        } else if (this.progressDirection < 0 && this.progress <= 0.0f) {
            this.progressDirection = 1;
            this.progress = 0.0f;
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
        Theme.chat_statusPaint.setAlpha(((int) (this.progress * 200.0f)) + 55);
        canvas.drawCircle((float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(this.isChat ? 8.0f : 9.0f), (float) AndroidUtilities.dp(4.0f), Theme.chat_statusPaint);
        if (this.started) {
            update();
        }
    }

    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(12.0f);
    }

    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(10.0f);
    }
}
