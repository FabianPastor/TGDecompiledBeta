package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import org.telegram.messenger.AndroidUtilities;

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
            this.drawable = context.getResources().getDrawable(NUM);
            this.drawableLeft = context.getResources().getDrawable(NUM);
            this.drawableRight = context.getResources().getDrawable(NUM);
            return;
        }
        this.drawable = context.getResources().getDrawable(NUM);
        this.drawableLeft = context.getResources().getDrawable(NUM);
        this.drawableRight = context.getResources().getDrawable(NUM);
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
            float[] fArr = this.progress;
            if (fArr[i] >= 1.0f) {
                fArr[i] = 0.0f;
            }
            fArr = this.progress;
            fArr[i] = fArr[i] + (((float) currentTimeMillis) / 1300.0f);
            if (fArr[i] > 1.0f) {
                fArr[i] = 1.0f;
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
        Drawable drawable = this.drawable;
        drawable.setBounds(intrinsicWidth, intrinsicHeight, drawable.getIntrinsicWidth() + intrinsicWidth, this.drawable.getIntrinsicHeight() + intrinsicHeight);
        this.drawable.draw(canvas2);
        int i2 = 0;
        while (i2 < i) {
            float[] fArr = this.progress;
            if (fArr[i2] >= 0.0f) {
                float f;
                float f2 = (fArr[i2] * 0.5f) + 0.5f;
                int dp2 = AndroidUtilities.dp((this.isSmall ? 2.5f : 5.0f) * f2);
                int dp3 = AndroidUtilities.dp((this.isSmall ? 6.5f : 18.0f) * f2);
                int dp4 = AndroidUtilities.dp((this.isSmall ? 6.0f : 15.0f) * this.progress[i2]);
                float[] fArr2 = this.progress;
                if (fArr2[i2] < 0.5f) {
                    f = fArr2[i2] / 0.5f;
                } else {
                    f = 1.0f - ((fArr2[i2] - 0.5f) / 0.5f);
                }
                float f3 = 42.0f;
                int dp5 = (AndroidUtilities.dp(this.isSmall ? 7.0f : 42.0f) + intrinsicWidth) - dp4;
                int intrinsicHeight2 = ((this.drawable.getIntrinsicHeight() / i) + intrinsicHeight) - (this.isSmall ? 0 : AndroidUtilities.dp(7.0f));
                int i3 = (int) (f * 255.0f);
                this.drawableLeft.setAlpha(i3);
                i = intrinsicHeight2 - dp3;
                intrinsicHeight2 += dp3;
                this.drawableLeft.setBounds(dp5 - dp2, i, dp5 + dp2, intrinsicHeight2);
                this.drawableLeft.draw(canvas2);
                dp = this.drawable.getIntrinsicWidth() + intrinsicWidth;
                if (this.isSmall) {
                    f3 = 7.0f;
                }
                dp = (dp - AndroidUtilities.dp(f3)) + dp4;
                this.drawableRight.setAlpha(i3);
                this.drawableRight.setBounds(dp - dp2, i, dp + dp2, intrinsicHeight2);
                this.drawableRight.draw(canvas2);
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
