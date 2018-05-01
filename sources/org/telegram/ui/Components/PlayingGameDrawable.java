package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.animation.DecelerateInterpolator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.Theme;

public class PlayingGameDrawable extends StatusDrawable {
    private int currentAccount = UserConfig.selectedAccount;
    private DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
    private boolean isChat = false;
    private long lastUpdateTime = 0;
    private Paint paint = new Paint(1);
    private float progress;
    private RectF rect = new RectF();
    private boolean started = false;

    /* renamed from: org.telegram.ui.Components.PlayingGameDrawable$1 */
    class C12821 implements Runnable {
        C12821() {
        }

        public void run() {
            PlayingGameDrawable.this.checkUpdate();
        }
    }

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
        currentTimeMillis = 16;
        if (j <= 16) {
            currentTimeMillis = j;
        }
        if (this.progress >= 1.0f) {
            this.progress = 0.0f;
        }
        this.progress += ((float) currentTimeMillis) / 300.0f;
        if (this.progress > 1.0f) {
            this.progress = 1.0f;
        }
        invalidateSelf();
    }

    public void start() {
        this.lastUpdateTime = System.currentTimeMillis();
        this.started = true;
        invalidateSelf();
    }

    public void stop() {
        this.progress = 0.0f;
        this.started = false;
    }

    public void draw(Canvas canvas) {
        int dp = AndroidUtilities.dp(10.0f);
        int intrinsicHeight = getBounds().top + ((getIntrinsicHeight() - dp) / 2);
        if (!this.isChat) {
            intrinsicHeight += AndroidUtilities.dp(1.0f);
        }
        int i = intrinsicHeight;
        this.paint.setColor(Theme.getColor(Theme.key_actionBarDefaultSubtitle));
        this.rect.set(0.0f, (float) i, (float) dp, (float) (i + dp));
        if (this.progress < 0.5f) {
            intrinsicHeight = (int) (35.0f * (1.0f - (this.progress / 0.5f)));
        } else {
            intrinsicHeight = (int) ((35.0f * (this.progress - 0.5f)) / 0.5f);
        }
        for (int i2 = 0; i2 < 3; i2++) {
            float dp2 = ((float) ((AndroidUtilities.dp(5.0f) * i2) + AndroidUtilities.dp(9.2f))) - (((float) AndroidUtilities.dp(5.0f)) * this.progress);
            if (i2 == 2) {
                this.paint.setAlpha(Math.min(255, (int) ((255.0f * this.progress) / 0.5f)));
            } else if (i2 != 0) {
                this.paint.setAlpha(255);
            } else if (this.progress > 0.5f) {
                this.paint.setAlpha((int) (255.0f * (1.0f - ((this.progress - 0.5f) / 0.5f))));
            } else {
                this.paint.setAlpha(255);
            }
            canvas.drawCircle(dp2, (float) ((dp / 2) + i), (float) AndroidUtilities.dp(1.2f), this.paint);
        }
        this.paint.setAlpha(255);
        canvas.drawArc(this.rect, (float) intrinsicHeight, (float) (360 - (intrinsicHeight * 2)), true, this.paint);
        this.paint.setColor(Theme.getColor(Theme.key_actionBarDefault));
        canvas.drawCircle((float) AndroidUtilities.dp(4.0f), (float) ((i + (dp / 2)) - AndroidUtilities.dp(2.0f)), (float) AndroidUtilities.dp(1.0f), this.paint);
        checkUpdate();
    }

    private void checkUpdate() {
        if (!this.started) {
            return;
        }
        if (NotificationCenter.getInstance(this.currentAccount).isAnimationInProgress()) {
            AndroidUtilities.runOnUIThread(new C12821(), 100);
        } else {
            update();
        }
    }

    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(20.0f);
    }

    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(18.0f);
    }
}
