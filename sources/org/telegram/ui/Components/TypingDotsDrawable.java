package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.view.animation.DecelerateInterpolator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.Theme;

public class TypingDotsDrawable extends StatusDrawable {
    private int currentAccount = UserConfig.selectedAccount;
    private DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
    private float[] elapsedTimes = {0.0f, 0.0f, 0.0f};
    private boolean isChat = false;
    private long lastUpdateTime = 0;
    private float[] scales = new float[3];
    private float[] startTimes = {0.0f, 150.0f, 300.0f};
    private boolean started = false;

    public int getOpacity() {
        return -2;
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
        if (j > 50) {
            j = 50;
        }
        for (int i = 0; i < 3; i++) {
            float[] fArr = this.elapsedTimes;
            fArr[i] = fArr[i] + ((float) j);
            float f = fArr[i];
            float[] fArr2 = this.startTimes;
            float f2 = f - fArr2[i];
            if (f2 <= 0.0f) {
                this.scales[i] = 1.33f;
            } else if (f2 <= 320.0f) {
                this.scales[i] = this.decelerateInterpolator.getInterpolation(f2 / 320.0f) + 1.33f;
            } else if (f2 <= 640.0f) {
                this.scales[i] = (1.0f - this.decelerateInterpolator.getInterpolation((f2 - 320.0f) / 320.0f)) + 1.33f;
            } else if (f2 >= 800.0f) {
                fArr[i] = 0.0f;
                fArr2[i] = 0.0f;
                this.scales[i] = 1.33f;
            } else {
                this.scales[i] = 1.33f;
            }
        }
        invalidateSelf();
    }

    public void start() {
        this.lastUpdateTime = System.currentTimeMillis();
        this.started = true;
        invalidateSelf();
    }

    public void stop() {
        for (int i = 0; i < 3; i++) {
            this.elapsedTimes[i] = 0.0f;
            this.scales[i] = 1.33f;
        }
        float[] fArr = this.startTimes;
        fArr[0] = 0.0f;
        fArr[1] = 150.0f;
        fArr[2] = 300.0f;
        this.started = false;
    }

    public void draw(Canvas canvas) {
        int i;
        int i2;
        if (this.isChat) {
            i2 = AndroidUtilities.dp(8.5f);
            i = getBounds().top;
        } else {
            i2 = AndroidUtilities.dp(9.3f);
            i = getBounds().top;
        }
        Theme.chat_statusPaint.setAlpha(255);
        float f = (float) (i2 + i);
        canvas.drawCircle((float) AndroidUtilities.dp(3.0f), f, this.scales[0] * AndroidUtilities.density, Theme.chat_statusPaint);
        canvas.drawCircle((float) AndroidUtilities.dp(9.0f), f, this.scales[1] * AndroidUtilities.density, Theme.chat_statusPaint);
        canvas.drawCircle((float) AndroidUtilities.dp(15.0f), f, this.scales[2] * AndroidUtilities.density, Theme.chat_statusPaint);
        checkUpdate();
    }

    /* access modifiers changed from: private */
    public void checkUpdate() {
        if (!this.started) {
            return;
        }
        if (!NotificationCenter.getInstance(this.currentAccount).isAnimationInProgress()) {
            update();
        } else {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    TypingDotsDrawable.this.checkUpdate();
                }
            }, 100);
        }
    }

    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(18.0f);
    }

    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(18.0f);
    }
}
