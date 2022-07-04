package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.Theme;

public class PlayingGameDrawable extends StatusDrawable {
    private int currentAccount = UserConfig.selectedAccount;
    private boolean isChat = false;
    private final boolean isDialogScreen;
    private long lastUpdateTime = 0;
    private Paint paint = new Paint(1);
    private float progress;
    private RectF rect = new RectF();
    Theme.ResourcesProvider resourcesProvider;
    private boolean started = false;

    public PlayingGameDrawable(boolean isDialogScreen2, Theme.ResourcesProvider resourcesProvider2) {
        this.isDialogScreen = isDialogScreen2;
        this.resourcesProvider = resourcesProvider2;
    }

    public void setIsChat(boolean value) {
        this.isChat = value;
    }

    public void setColor(int color) {
    }

    private void update() {
        long newTime = System.currentTimeMillis();
        long dt = newTime - this.lastUpdateTime;
        this.lastUpdateTime = newTime;
        if (dt > 16) {
            dt = 16;
        }
        if (this.progress >= 1.0f) {
            this.progress = 0.0f;
        }
        float f = this.progress + (((float) dt) / 300.0f);
        this.progress = f;
        if (f > 1.0f) {
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
        int y;
        int rad;
        int size = AndroidUtilities.dp(10.0f);
        int y2 = getBounds().top + ((getIntrinsicHeight() - size) / 2);
        if (this.isChat) {
            y = y2;
        } else {
            y = y2 + AndroidUtilities.dp(1.0f);
        }
        this.paint.setColor(Theme.getColor(this.isDialogScreen ? "chats_actionMessage" : "chat_status", this.resourcesProvider));
        this.rect.set(0.0f, (float) y, (float) size, (float) (y + size));
        float f = this.progress;
        if (f < 0.5f) {
            rad = (int) ((1.0f - (f / 0.5f)) * 35.0f);
        } else {
            rad = (int) (((f - 0.5f) * 35.0f) / 0.5f);
        }
        for (int a = 0; a < 3; a++) {
            float f2 = this.progress;
            float x = ((float) ((AndroidUtilities.dp(5.0f) * a) + AndroidUtilities.dp(9.2f))) - (((float) AndroidUtilities.dp(5.0f)) * f2);
            if (a == 2) {
                this.paint.setAlpha(Math.min(255, (int) ((f2 * 255.0f) / 0.5f)));
            } else if (a != 0) {
                this.paint.setAlpha(255);
            } else if (f2 > 0.5f) {
                this.paint.setAlpha((int) ((1.0f - ((f2 - 0.5f) / 0.5f)) * 255.0f));
            } else {
                this.paint.setAlpha(255);
            }
            canvas.drawCircle(x, (float) ((size / 2) + y), (float) AndroidUtilities.dp(1.2f), this.paint);
        }
        this.paint.setAlpha(255);
        canvas.drawArc(this.rect, (float) rad, (float) (360 - (rad * 2)), true, this.paint);
        this.paint.setColor(Theme.getColor(this.isDialogScreen ? "windowBackgroundWhite" : "actionBarDefault"));
        canvas.drawCircle((float) AndroidUtilities.dp(4.0f), (float) (((size / 2) + y) - AndroidUtilities.dp(2.0f)), (float) AndroidUtilities.dp(1.0f), this.paint);
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
            AndroidUtilities.runOnUIThread(new PlayingGameDrawable$$ExternalSyntheticLambda0(this), 100);
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
        return AndroidUtilities.dp(20.0f);
    }

    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(18.0f);
    }
}
