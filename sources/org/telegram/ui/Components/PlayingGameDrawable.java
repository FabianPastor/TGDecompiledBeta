package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes3.dex */
public class PlayingGameDrawable extends StatusDrawable {
    private final boolean isDialogScreen;
    private float progress;
    Theme.ResourcesProvider resourcesProvider;
    private boolean isChat = false;
    private Paint paint = new Paint(1);
    private int currentAccount = UserConfig.selectedAccount;
    private long lastUpdateTime = 0;
    private boolean started = false;
    private RectF rect = new RectF();

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -2;
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
    }

    @Override // org.telegram.ui.Components.StatusDrawable
    public void setColor(int i) {
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
    }

    public PlayingGameDrawable(boolean z, Theme.ResourcesProvider resourcesProvider) {
        this.isDialogScreen = z;
        this.resourcesProvider = resourcesProvider;
    }

    @Override // org.telegram.ui.Components.StatusDrawable
    public void setIsChat(boolean z) {
        this.isChat = z;
    }

    private void update() {
        long currentTimeMillis = System.currentTimeMillis();
        long j = currentTimeMillis - this.lastUpdateTime;
        this.lastUpdateTime = currentTimeMillis;
        if (j > 16) {
            j = 16;
        }
        if (this.progress >= 1.0f) {
            this.progress = 0.0f;
        }
        float f = this.progress + (((float) j) / 300.0f);
        this.progress = f;
        if (f > 1.0f) {
            this.progress = 1.0f;
        }
        invalidateSelf();
    }

    @Override // org.telegram.ui.Components.StatusDrawable
    public void start() {
        this.lastUpdateTime = System.currentTimeMillis();
        this.started = true;
        invalidateSelf();
    }

    @Override // org.telegram.ui.Components.StatusDrawable
    public void stop() {
        this.progress = 0.0f;
        this.started = false;
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        int dp = AndroidUtilities.dp(10.0f);
        int intrinsicHeight = getBounds().top + ((getIntrinsicHeight() - dp) / 2);
        if (!this.isChat) {
            intrinsicHeight += AndroidUtilities.dp(1.0f);
        }
        int i = intrinsicHeight;
        this.paint.setColor(Theme.getColor(this.isDialogScreen ? "chats_actionMessage" : "chat_status", this.resourcesProvider));
        this.rect.set(0.0f, i, dp, i + dp);
        float f = this.progress;
        int i2 = (int) (f < 0.5f ? (1.0f - (f / 0.5f)) * 35.0f : ((f - 0.5f) * 35.0f) / 0.5f);
        for (int i3 = 0; i3 < 3; i3++) {
            float f2 = this.progress;
            float dp2 = ((AndroidUtilities.dp(5.0f) * i3) + AndroidUtilities.dp(9.2f)) - (AndroidUtilities.dp(5.0f) * f2);
            if (i3 == 2) {
                this.paint.setAlpha(Math.min(255, (int) ((f2 * 255.0f) / 0.5f)));
            } else if (i3 != 0) {
                this.paint.setAlpha(255);
            } else if (f2 > 0.5f) {
                this.paint.setAlpha((int) ((1.0f - ((f2 - 0.5f) / 0.5f)) * 255.0f));
            } else {
                this.paint.setAlpha(255);
            }
            canvas.drawCircle(dp2, (dp / 2) + i, AndroidUtilities.dp(1.2f), this.paint);
        }
        this.paint.setAlpha(255);
        canvas.drawArc(this.rect, i2, 360 - (i2 * 2), true, this.paint);
        this.paint.setColor(Theme.getColor(this.isDialogScreen ? "windowBackgroundWhite" : "actionBarDefault"));
        canvas.drawCircle(AndroidUtilities.dp(4.0f), (i + (dp / 2)) - AndroidUtilities.dp(2.0f), AndroidUtilities.dp(1.0f), this.paint);
        checkUpdate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkUpdate() {
        if (this.started) {
            if (!NotificationCenter.getInstance(this.currentAccount).isAnimationInProgress()) {
                update();
            } else {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.PlayingGameDrawable$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        PlayingGameDrawable.this.checkUpdate();
                    }
                }, 100L);
            }
        }
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(20.0f);
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(18.0f);
    }
}
