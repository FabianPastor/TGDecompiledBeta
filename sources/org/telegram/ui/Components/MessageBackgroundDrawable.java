package org.telegram.ui.Components;

import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class MessageBackgroundDrawable extends Drawable {
    public static final float ANIMATION_DURATION = 200.0f;
    private boolean animationInProgress;
    private float currentAnimationProgress;
    private int finalRadius;
    private boolean isSelected;
    private long lastAnimationTime;
    private Paint paint = new Paint(1);
    private float touchX = -1.0f;
    private float touchY = -1.0f;

    public int getOpacity() {
        return -2;
    }

    public MessageBackgroundDrawable(int i) {
        this.paint.setColor(i);
    }

    public void setColor(int i) {
        this.paint.setColor(i);
    }

    public void setSelected(boolean z, boolean z2) {
        float f = 1.0f;
        if (this.isSelected == z) {
            if (this.animationInProgress) {
                if (!z) {
                    f = 0.0f;
                }
                this.currentAnimationProgress = f;
                this.animationInProgress = false;
            }
            return;
        }
        this.isSelected = z;
        this.animationInProgress = false;
        if (!z) {
            f = 0.0f;
        }
        this.currentAnimationProgress = f;
        calcRadius();
        invalidateSelf();
    }

    /* JADX WARNING: Missing block: B:3:0x000f, code skipped:
            if (r3 >= 0.0f) goto L_0x001c;
     */
    private void calcRadius() {
        /*
        r7 = this;
        r0 = r7.getBounds();
        r1 = r7.touchX;
        r2 = 0;
        r3 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1));
        if (r3 < 0) goto L_0x0012;
    L_0x000b:
        r3 = r7.touchY;
        r2 = (r3 > r2 ? 1 : (r3 == r2 ? 0 : -1));
        if (r2 < 0) goto L_0x0012;
    L_0x0011:
        goto L_0x001c;
    L_0x0012:
        r1 = r0.centerX();
        r1 = (float) r1;
        r2 = r0.centerY();
        r3 = (float) r2;
    L_0x001c:
        r2 = 0;
        r7.finalRadius = r2;
    L_0x001f:
        r4 = 4;
        if (r2 >= r4) goto L_0x005f;
    L_0x0022:
        if (r2 == 0) goto L_0x003d;
    L_0x0024:
        r4 = 1;
        if (r2 == r4) goto L_0x0037;
    L_0x0027:
        r4 = 2;
        if (r2 == r4) goto L_0x0031;
    L_0x002a:
        r4 = r0.right;
        r4 = (float) r4;
        r5 = r0.bottom;
    L_0x002f:
        r5 = (float) r5;
        goto L_0x0043;
    L_0x0031:
        r4 = r0.right;
        r4 = (float) r4;
        r5 = r0.top;
        goto L_0x002f;
    L_0x0037:
        r4 = r0.left;
        r4 = (float) r4;
        r5 = r0.bottom;
        goto L_0x002f;
    L_0x003d:
        r4 = r0.left;
        r4 = (float) r4;
        r5 = r0.top;
        goto L_0x002f;
    L_0x0043:
        r6 = r7.finalRadius;
        r4 = r4 - r1;
        r4 = r4 * r4;
        r5 = r5 - r3;
        r5 = r5 * r5;
        r4 = r4 + r5;
        r4 = (double) r4;
        r4 = java.lang.Math.sqrt(r4);
        r4 = java.lang.Math.ceil(r4);
        r4 = (int) r4;
        r4 = java.lang.Math.max(r6, r4);
        r7.finalRadius = r4;
        r2 = r2 + 1;
        goto L_0x001f;
    L_0x005f:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.MessageBackgroundDrawable.calcRadius():void");
    }

    public void setTouchCoords(float f, float f2) {
        this.touchX = f;
        this.touchY = f2;
        calcRadius();
        invalidateSelf();
    }

    public void setBounds(int i, int i2, int i3, int i4) {
        super.setBounds(i, i2, i3, i4);
        calcRadius();
    }

    public void setBounds(Rect rect) {
        super.setBounds(rect);
        calcRadius();
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.paint.setColorFilter(colorFilter);
    }

    public void setAlpha(int i) {
        this.paint.setAlpha(i);
    }

    /* JADX WARNING: Missing block: B:20:0x0069, code skipped:
            if (r1 >= 0.0f) goto L_0x007d;
     */
    public void draw(android.graphics.Canvas r10) {
        /*
        r9 = this;
        r0 = r9.animationInProgress;
        r1 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r2 = 0;
        if (r0 == 0) goto L_0x004b;
    L_0x0007:
        r3 = android.os.SystemClock.uptimeMillis();
        r5 = r9.lastAnimationTime;
        r5 = r3 - r5;
        r9.lastAnimationTime = r3;
        r0 = r9.isSelected;
        r3 = 0;
        r4 = NUM; // 0x43480000 float:200.0 double:5.5769738E-315;
        r7 = -NUM; // 0xffffffffbvar_ float:-1.0 double:NaN;
        if (r0 == 0) goto L_0x0033;
    L_0x001a:
        r0 = r9.currentAnimationProgress;
        r5 = (float) r5;
        r5 = r5 / r4;
        r0 = r0 + r5;
        r9.currentAnimationProgress = r0;
        r0 = r9.currentAnimationProgress;
        r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1));
        if (r0 < 0) goto L_0x002f;
    L_0x0027:
        r9.touchX = r7;
        r9.touchY = r7;
        r9.currentAnimationProgress = r1;
        r9.animationInProgress = r3;
    L_0x002f:
        r9.invalidateSelf();
        goto L_0x004b;
    L_0x0033:
        r0 = r9.currentAnimationProgress;
        r5 = (float) r5;
        r5 = r5 / r4;
        r0 = r0 - r5;
        r9.currentAnimationProgress = r0;
        r0 = r9.currentAnimationProgress;
        r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r0 > 0) goto L_0x0048;
    L_0x0040:
        r9.touchX = r7;
        r9.touchY = r7;
        r9.currentAnimationProgress = r2;
        r9.animationInProgress = r3;
    L_0x0048:
        r9.invalidateSelf();
    L_0x004b:
        r0 = r9.currentAnimationProgress;
        r1 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1));
        if (r1 != 0) goto L_0x005b;
    L_0x0051:
        r0 = r9.getBounds();
        r1 = r9.paint;
        r10.drawRect(r0, r1);
        goto L_0x008f;
    L_0x005b:
        r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r0 == 0) goto L_0x008f;
    L_0x005f:
        r0 = r9.touchX;
        r1 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r1 < 0) goto L_0x006c;
    L_0x0065:
        r1 = r9.touchY;
        r2 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1));
        if (r2 < 0) goto L_0x006c;
    L_0x006b:
        goto L_0x007d;
    L_0x006c:
        r0 = r9.getBounds();
        r1 = r0.centerX();
        r1 = (float) r1;
        r0 = r0.centerY();
        r0 = (float) r0;
        r8 = r1;
        r1 = r0;
        r0 = r8;
    L_0x007d:
        r2 = r9.finalRadius;
        r2 = (float) r2;
        r3 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT;
        r4 = r9.currentAnimationProgress;
        r3 = r3.getInterpolation(r4);
        r2 = r2 * r3;
        r3 = r9.paint;
        r10.drawCircle(r0, r1, r2, r3);
    L_0x008f:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.MessageBackgroundDrawable.draw(android.graphics.Canvas):void");
    }
}
