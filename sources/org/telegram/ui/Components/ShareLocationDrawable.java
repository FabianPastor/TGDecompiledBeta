package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import org.telegram.messenger.AndroidUtilities;

public class ShareLocationDrawable extends Drawable {
    private int currentType;
    private Drawable drawable;
    private Drawable drawableLeft;
    private Drawable drawableRight;
    private long lastUpdateTime = 0;
    private float[] progress = {0.0f, -0.5f};

    public ShareLocationDrawable(Context context, int type) {
        this.currentType = type;
        if (type == 4) {
            this.drawable = context.getResources().getDrawable(NUM);
            this.drawableLeft = context.getResources().getDrawable(NUM);
            this.drawableRight = context.getResources().getDrawable(NUM);
        } else if (type == 3) {
            this.drawable = context.getResources().getDrawable(NUM);
            this.drawableLeft = context.getResources().getDrawable(NUM);
            this.drawableRight = context.getResources().getDrawable(NUM);
        } else if (type == 2) {
            this.drawable = context.getResources().getDrawable(NUM);
            this.drawableLeft = context.getResources().getDrawable(NUM);
            this.drawableRight = context.getResources().getDrawable(NUM);
        } else if (type == 1) {
            this.drawable = context.getResources().getDrawable(NUM);
            this.drawableLeft = context.getResources().getDrawable(NUM);
            this.drawableRight = context.getResources().getDrawable(NUM);
        } else {
            this.drawable = context.getResources().getDrawable(NUM);
            this.drawableLeft = context.getResources().getDrawable(NUM);
            this.drawableRight = context.getResources().getDrawable(NUM);
        }
    }

    private void update() {
        long newTime = System.currentTimeMillis();
        long dt = newTime - this.lastUpdateTime;
        this.lastUpdateTime = newTime;
        if (dt > 16) {
            dt = 16;
        }
        for (int a = 0; a < 2; a++) {
            float[] fArr = this.progress;
            if (fArr[a] >= 1.0f) {
                fArr[a] = 0.0f;
            }
            fArr[a] = fArr[a] + (((float) dt) / 1300.0f);
            if (fArr[a] > 1.0f) {
                fArr[a] = 1.0f;
            }
        }
        invalidateSelf();
    }

    public void draw(Canvas canvas) {
        int size;
        int drawableW;
        int cy;
        int cx2;
        int cx;
        int h;
        int w;
        float alpha;
        Canvas canvas2 = canvas;
        int drawableW2 = this.drawable.getIntrinsicWidth();
        int drawableH = this.drawable.getIntrinsicHeight();
        int size2 = this.currentType;
        int i = 3;
        int i2 = 4;
        int i3 = 1;
        if (size2 == 4) {
            size = AndroidUtilities.dp(24.0f);
        } else if (size2 == 3) {
            size = AndroidUtilities.dp(44.0f);
        } else if (size2 == 2) {
            size = AndroidUtilities.dp(32.0f);
        } else if (size2 == 1) {
            size = AndroidUtilities.dp(30.0f);
        } else {
            size = AndroidUtilities.dp(120.0f);
        }
        int y = getBounds().top + ((getIntrinsicHeight() - size) / 2);
        int x = getBounds().left + ((getIntrinsicWidth() - size) / 2);
        this.drawable.setBounds(x, y, x + drawableW2, y + drawableH);
        this.drawable.draw(canvas2);
        int a = 0;
        for (int i4 = 2; a < i4; i4 = 2) {
            float[] fArr = this.progress;
            if (fArr[a] < 0.0f) {
                drawableW = drawableW2;
            } else {
                float scale = (fArr[a] * 0.5f) + 0.5f;
                int w2 = this.currentType;
                if (w2 == i2) {
                    w = AndroidUtilities.dp(2.5f * scale);
                    h = AndroidUtilities.dp(6.5f * scale);
                    int tx = AndroidUtilities.dp(this.progress[a] * 6.0f);
                    cx = (x + AndroidUtilities.dp(3.0f)) - tx;
                    cy = (y + (drawableH / 2)) - AndroidUtilities.dp(2.0f);
                    cx2 = ((x + drawableW2) - AndroidUtilities.dp(3.0f)) + tx;
                } else if (w2 == i) {
                    w = AndroidUtilities.dp(5.0f * scale);
                    h = AndroidUtilities.dp(18.0f * scale);
                    int tx2 = AndroidUtilities.dp(this.progress[a] * 15.0f);
                    cx = (AndroidUtilities.dp(2.0f) + x) - tx2;
                    cy = ((drawableH / 2) + y) - AndroidUtilities.dp(7.0f);
                    cx2 = ((x + drawableW2) - AndroidUtilities.dp(2.0f)) + tx2;
                } else if (w2 == i4) {
                    w = AndroidUtilities.dp(5.0f * scale);
                    h = AndroidUtilities.dp(18.0f * scale);
                    int tx3 = AndroidUtilities.dp(this.progress[a] * 15.0f);
                    cx = (AndroidUtilities.dp(2.0f) + x) - tx3;
                    cy = y + (drawableH / 2);
                    cx2 = ((x + drawableW2) - AndroidUtilities.dp(2.0f)) + tx3;
                } else if (w2 == i3) {
                    w = AndroidUtilities.dp(2.5f * scale);
                    h = AndroidUtilities.dp(6.5f * scale);
                    int tx4 = AndroidUtilities.dp(this.progress[a] * 6.0f);
                    cx = (AndroidUtilities.dp(7.0f) + x) - tx4;
                    cy = y + (drawableH / 2);
                    cx2 = ((x + drawableW2) - AndroidUtilities.dp(7.0f)) + tx4;
                } else {
                    w = AndroidUtilities.dp(5.0f * scale);
                    h = AndroidUtilities.dp(18.0f * scale);
                    int tx5 = AndroidUtilities.dp(this.progress[a] * 15.0f);
                    cx = (x + AndroidUtilities.dp(42.0f)) - tx5;
                    cy = (y + (drawableH / 2)) - AndroidUtilities.dp(7.0f);
                    cx2 = ((x + drawableW2) - AndroidUtilities.dp(42.0f)) + tx5;
                }
                float[] fArr2 = this.progress;
                if (fArr2[a] < 0.5f) {
                    alpha = fArr2[a] / 0.5f;
                } else {
                    alpha = 1.0f - ((fArr2[a] - 0.5f) / 0.5f);
                }
                this.drawableLeft.setAlpha((int) (alpha * 255.0f));
                drawableW = drawableW2;
                this.drawableLeft.setBounds(cx - w, cy - h, cx + w, cy + h);
                this.drawableLeft.draw(canvas2);
                this.drawableRight.setAlpha((int) (alpha * 255.0f));
                this.drawableRight.setBounds(cx2 - w, cy - h, cx2 + w, cy + h);
                this.drawableRight.draw(canvas2);
            }
            a++;
            drawableW2 = drawableW;
            i = 3;
            i2 = 4;
            i3 = 1;
        }
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
        return -2;
    }

    public int getIntrinsicWidth() {
        int i = this.currentType;
        if (i == 4) {
            return AndroidUtilities.dp(42.0f);
        }
        if (i == 3) {
            return AndroidUtilities.dp(100.0f);
        }
        if (i == 2) {
            return AndroidUtilities.dp(74.0f);
        }
        if (i == 1) {
            return AndroidUtilities.dp(40.0f);
        }
        return AndroidUtilities.dp(120.0f);
    }

    public int getIntrinsicHeight() {
        int i = this.currentType;
        if (i == 4) {
            return AndroidUtilities.dp(42.0f);
        }
        if (i == 3) {
            return AndroidUtilities.dp(100.0f);
        }
        if (i == 2) {
            return AndroidUtilities.dp(74.0f);
        }
        if (i == 1) {
            return AndroidUtilities.dp(40.0f);
        }
        return AndroidUtilities.dp(180.0f);
    }
}
