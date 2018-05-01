package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;

public class ShareLocationDrawable extends Drawable {
    private Drawable drawable;
    private Drawable drawableLeft;
    private Drawable drawableRight;
    private boolean isSmall;
    private long lastUpdateTime = 0;
    private float[] progress = new float[]{0.0f, -0.5f};

    public int getOpacity() {
        return 0;
    }

    public void setAlpha(int i) {
    }

    public ShareLocationDrawable(Context context, boolean z) {
        this.isSmall = z;
        if (z) {
            this.drawable = context.getResources().getDrawable(C0446R.drawable.smallanimationpin);
            this.drawableLeft = context.getResources().getDrawable(C0446R.drawable.smallanimationpinleft);
            this.drawableRight = context.getResources().getDrawable(true);
            return;
        }
        this.drawable = context.getResources().getDrawable(C0446R.drawable.animationpin);
        this.drawableLeft = context.getResources().getDrawable(C0446R.drawable.animationpinleft);
        this.drawableRight = context.getResources().getDrawable(true);
    }

    private void update() {
        long currentTimeMillis = System.currentTimeMillis();
        long j = currentTimeMillis - this.lastUpdateTime;
        this.lastUpdateTime = currentTimeMillis;
        currentTimeMillis = 16;
        if (j <= 16) {
            currentTimeMillis = j;
        }
        for (int i = 0; i < 2; i++) {
            if (this.progress[i] >= 1.0f) {
                this.progress[i] = 0.0f;
            }
            float[] fArr = this.progress;
            fArr[i] = fArr[i] + (((float) currentTimeMillis) / 1300.0f);
            if (this.progress[i] > 1.0f) {
                this.progress[i] = 1.0f;
            }
        }
        invalidateSelf();
    }

    public void draw(Canvas canvas) {
        Canvas canvas2 = canvas;
        int dp = AndroidUtilities.dp(this.isSmall ? 30.0f : 120.0f);
        int i = 2;
        int intrinsicHeight = getBounds().top + ((getIntrinsicHeight() - dp) / 2);
        int intrinsicWidth = getBounds().left + ((getIntrinsicWidth() - dp) / 2);
        r0.drawable.setBounds(intrinsicWidth, intrinsicHeight, r0.drawable.getIntrinsicWidth() + intrinsicWidth, r0.drawable.getIntrinsicHeight() + intrinsicHeight);
        r0.drawable.draw(canvas2);
        int i2 = 0;
        while (i2 < i) {
            if (r0.progress[i2] >= 0.0f) {
                float f;
                float f2 = (r0.progress[i2] * 0.5f) + 0.5f;
                int dp2 = AndroidUtilities.dp((r0.isSmall ? 2.5f : 5.0f) * f2);
                int dp3 = AndroidUtilities.dp((r0.isSmall ? 6.5f : 18.0f) * f2);
                int dp4 = AndroidUtilities.dp((r0.isSmall ? 6.0f : 15.0f) * r0.progress[i2]);
                if (r0.progress[i2] < 0.5f) {
                    f = r0.progress[i2] / 0.5f;
                } else {
                    f = 1.0f - ((r0.progress[i2] - 0.5f) / 0.5f);
                }
                float f3 = 42.0f;
                int dp5 = (AndroidUtilities.dp(r0.isSmall ? 7.0f : 42.0f) + intrinsicWidth) - dp4;
                int intrinsicHeight2 = ((r0.drawable.getIntrinsicHeight() / i) + intrinsicHeight) - (r0.isSmall ? 0 : AndroidUtilities.dp(7.0f));
                int i3 = (int) (f * 255.0f);
                r0.drawableLeft.setAlpha(i3);
                i = intrinsicHeight2 - dp3;
                intrinsicHeight2 += dp3;
                r0.drawableLeft.setBounds(dp5 - dp2, i, dp5 + dp2, intrinsicHeight2);
                r0.drawableLeft.draw(canvas2);
                dp = r0.drawable.getIntrinsicWidth() + intrinsicWidth;
                if (r0.isSmall) {
                    f3 = 7.0f;
                }
                dp = (dp - AndroidUtilities.dp(f3)) + dp4;
                r0.drawableRight.setAlpha(i3);
                r0.drawableRight.setBounds(dp - dp2, i, dp + dp2, intrinsicHeight2);
                r0.drawableRight.draw(canvas2);
            }
            i2++;
            i = 2;
        }
        update();
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.drawable.setColorFilter(colorFilter);
        this.drawableLeft.setColorFilter(colorFilter);
        this.drawableRight.setColorFilter(colorFilter);
    }

    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(this.isSmall ? 40.0f : 120.0f);
    }

    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(this.isSmall ? 40.0f : 180.0f);
    }
}
