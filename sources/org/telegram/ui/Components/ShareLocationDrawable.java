package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.beta.R;

public class ShareLocationDrawable extends Drawable {
    private Drawable drawable;
    private Drawable drawableLeft;
    private Drawable drawableRight;
    private boolean isSmall;
    private long lastUpdateTime = 0;
    private float[] progress = new float[]{0.0f, -0.5f};

    public ShareLocationDrawable(Context context, boolean small) {
        this.isSmall = small;
        if (small) {
            this.drawable = context.getResources().getDrawable(R.drawable.smallanimationpin);
            this.drawableLeft = context.getResources().getDrawable(R.drawable.smallanimationpinleft);
            this.drawableRight = context.getResources().getDrawable(R.drawable.smallanimationpinright);
            return;
        }
        this.drawable = context.getResources().getDrawable(R.drawable.animationpin);
        this.drawableLeft = context.getResources().getDrawable(R.drawable.animationpinleft);
        this.drawableRight = context.getResources().getDrawable(R.drawable.animationpinright);
    }

    private void update() {
        long newTime = System.currentTimeMillis();
        long dt = newTime - this.lastUpdateTime;
        this.lastUpdateTime = newTime;
        if (dt > 16) {
            dt = 16;
        }
        for (int a = 0; a < 2; a++) {
            if (this.progress[a] >= 1.0f) {
                this.progress[a] = 0.0f;
            }
            float[] fArr = this.progress;
            fArr[a] = fArr[a] + (((float) dt) / 1300.0f);
            if (this.progress[a] > 1.0f) {
                this.progress[a] = 1.0f;
            }
        }
        invalidateSelf();
    }

    public void draw(Canvas canvas) {
        int y;
        Canvas canvas2 = canvas;
        int size = AndroidUtilities.dp(this.isSmall ? NUM : NUM);
        int i = 2;
        int y2 = getBounds().top + ((getIntrinsicHeight() - size) / 2);
        int x = getBounds().left + ((getIntrinsicWidth() - size) / 2);
        r0.drawable.setBounds(x, y2, r0.drawable.getIntrinsicWidth() + x, r0.drawable.getIntrinsicHeight() + y2);
        r0.drawable.draw(canvas2);
        int a = 0;
        while (a < i) {
            int size2;
            if (r0.progress[a] < 0.0f) {
                size2 = size;
                y = y2;
            } else {
                float alpha;
                float scale = (r0.progress[a] * 0.5f) + 0.5f;
                int w = AndroidUtilities.dp((r0.isSmall ? 2.5f : 5.0f) * scale);
                int h = AndroidUtilities.dp((r0.isSmall ? 6.5f : 18.0f) * scale);
                int tx = AndroidUtilities.dp((r0.isSmall ? 6.0f : 15.0f) * r0.progress[a]);
                if (r0.progress[a] < 0.5f) {
                    alpha = r0.progress[a] / 0.5f;
                } else {
                    alpha = 1.0f - ((r0.progress[a] - 0.5f) / 0.5f);
                }
                float alpha2 = alpha;
                int cx = (AndroidUtilities.dp(r0.isSmall ? 7.0f : 42.0f) + x) - tx;
                int cy = ((r0.drawable.getIntrinsicHeight() / i) + y2) - (r0.isSmall ? 0 : AndroidUtilities.dp(7.0f));
                r0.drawableLeft.setAlpha((int) (alpha2 * 255.0f));
                size2 = size;
                y = y2;
                r0.drawableLeft.setBounds(cx - w, cy - h, cx + w, cy + h);
                r0.drawableLeft.draw(canvas2);
                size = ((r0.drawable.getIntrinsicWidth() + x) - AndroidUtilities.dp(r0.isSmall ? 7.0f : 42.0f)) + tx;
                r0.drawableRight.setAlpha((int) (alpha2 * 255.0f));
                r0.drawableRight.setBounds(size - w, cy - h, size + w, cy + h);
                r0.drawableRight.draw(canvas2);
            }
            a++;
            size = size2;
            y2 = y;
            i = 2;
        }
        y = y2;
        update();
    }

    public void setAlpha(int alpha) {
    }

    public void setColorFilter(ColorFilter cf) {
        this.drawable.setColorFilter(cf);
        this.drawableLeft.setColorFilter(cf);
        this.drawableRight.setColorFilter(cf);
    }

    public int getOpacity() {
        return 0;
    }

    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(this.isSmall ? 40.0f : 120.0f);
    }

    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(this.isSmall ? 40.0f : 180.0f);
    }
}
