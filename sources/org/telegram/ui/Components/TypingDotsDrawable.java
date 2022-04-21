package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.view.animation.DecelerateInterpolator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.Theme;

public class TypingDotsDrawable extends StatusDrawable {
    private int currentAccount = UserConfig.selectedAccount;
    private Paint currentPaint;
    private DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
    private float[] elapsedTimes = {0.0f, 0.0f, 0.0f};
    private boolean isChat = false;
    private long lastUpdateTime = 0;
    private float[] scales = new float[3];
    private float[] startTimes = {0.0f, 150.0f, 300.0f};
    private boolean started = false;

    public TypingDotsDrawable(boolean createPaint) {
        if (createPaint) {
            this.currentPaint = new Paint(1);
        }
    }

    public void setColor(int color) {
        Paint paint = this.currentPaint;
        if (paint != null) {
            paint.setColor(color);
        }
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
        for (int a = 0; a < 3; a++) {
            float[] fArr = this.elapsedTimes;
            fArr[a] = fArr[a] + ((float) dt);
            float f = fArr[a];
            float[] fArr2 = this.startTimes;
            float timeSinceStart = f - fArr2[a];
            if (timeSinceStart <= 0.0f) {
                this.scales[a] = 1.33f;
            } else if (timeSinceStart <= 320.0f) {
                this.scales[a] = 1.33f + this.decelerateInterpolator.getInterpolation(timeSinceStart / 320.0f);
            } else if (timeSinceStart <= 640.0f) {
                this.scales[a] = (1.0f - this.decelerateInterpolator.getInterpolation((timeSinceStart - 320.0f) / 320.0f)) + 1.33f;
            } else if (timeSinceStart >= 800.0f) {
                fArr[a] = 0.0f;
                fArr2[a] = 0.0f;
                this.scales[a] = 1.33f;
            } else {
                this.scales[a] = 1.33f;
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
        for (int a = 0; a < 3; a++) {
            this.elapsedTimes[a] = 0.0f;
            this.scales[a] = 1.33f;
        }
        float[] fArr = this.startTimes;
        fArr[0] = 0.0f;
        fArr[1] = 150.0f;
        fArr[2] = 300.0f;
        this.started = false;
    }

    public void draw(Canvas canvas) {
        int y;
        Paint paint;
        if (this.isChat) {
            y = AndroidUtilities.dp(8.5f) + getBounds().top;
        } else {
            y = AndroidUtilities.dp(9.3f) + getBounds().top;
        }
        if (this.currentPaint == null) {
            paint = Theme.chat_statusPaint;
            paint.setAlpha(255);
        } else {
            paint = this.currentPaint;
        }
        canvas.drawCircle((float) AndroidUtilities.dp(3.0f), (float) y, this.scales[0] * AndroidUtilities.density, paint);
        canvas.drawCircle((float) AndroidUtilities.dp(9.0f), (float) y, this.scales[1] * AndroidUtilities.density, paint);
        canvas.drawCircle((float) AndroidUtilities.dp(15.0f), (float) y, this.scales[2] * AndroidUtilities.density, paint);
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
            AndroidUtilities.runOnUIThread(new TypingDotsDrawable$$ExternalSyntheticLambda0(this), 100);
        }
    }

    public void setAlpha(int alpha) {
    }

    public void setColorFilter(ColorFilter cf) {
    }

    public int getOpacity() {
        return -2;
    }

    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(18.0f);
    }

    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(18.0f);
    }
}
