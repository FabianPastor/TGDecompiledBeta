package org.telegram.ui.Components;

import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;

public class MessageBackgroundDrawable extends Drawable {
    private boolean animationInProgress;
    private float currentAnimationProgress;
    private float finalRadius;
    private boolean isSelected;
    private long lastAnimationTime;
    private long lastTouchTime;
    private Paint paint = new Paint(1);
    private View parentView;
    private float touchOverrideX = -1.0f;
    private float touchOverrideY = -1.0f;
    private float touchX = -1.0f;
    private float touchY = -1.0f;

    public int getOpacity() {
        return -2;
    }

    public MessageBackgroundDrawable(View view) {
        this.parentView = view;
    }

    public void setColor(int i) {
        this.paint.setColor(i);
    }

    public void setSelected(boolean z, boolean z2) {
        float f = 1.0f;
        if (this.isSelected == z) {
            if (!(this.animationInProgress == z2 || z2)) {
                if (!z) {
                    f = 0.0f;
                }
                this.currentAnimationProgress = f;
                this.animationInProgress = false;
            }
            return;
        }
        this.isSelected = z;
        this.animationInProgress = z2;
        if (z2) {
            this.lastAnimationTime = SystemClock.uptimeMillis();
        } else {
            if (!z) {
                f = 0.0f;
            }
            this.currentAnimationProgress = f;
        }
        calcRadius();
        invalidate();
    }

    private void invalidate() {
        View view = this.parentView;
        if (view != null) {
            view.invalidate();
            if (this.parentView.getParent() != null) {
                ((ViewGroup) this.parentView.getParent()).invalidate();
            }
        }
    }

    private void calcRadius() {
        Rect bounds = getBounds();
        float centerX = (float) bounds.centerX();
        float centerY = (float) bounds.centerY();
        int i = bounds.left;
        float f = (((float) i) - centerX) * (((float) i) - centerX);
        int i2 = bounds.top;
        this.finalRadius = (float) Math.ceil(Math.sqrt((double) (f + ((((float) i2) - centerY) * (((float) i2) - centerY)))));
    }

    public void setTouchCoords(float f, float f2) {
        this.touchX = f;
        this.touchY = f2;
        this.lastTouchTime = SystemClock.uptimeMillis();
    }

    public void setTouchCoordsOverride(float f, float f2) {
        this.touchOverrideX = f;
        this.touchOverrideY = f2;
    }

    public float getTouchX() {
        return this.touchX;
    }

    public float getTouchY() {
        return this.touchY;
    }

    public long getLastTouchTime() {
        return this.lastTouchTime;
    }

    public boolean isAnimationInProgress() {
        return this.animationInProgress;
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

    /* JADX WARNING: Removed duplicated region for block: B:34:0x00aa  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x00aa  */
    /* JADX WARNING: Missing block: B:12:0x0044, code skipped:
            if (r6 >= 0.0f) goto L_0x0056;
     */
    /* JADX WARNING: Missing block: B:16:0x0051, code skipped:
            if (r6 >= 0.0f) goto L_0x0056;
     */
    public void draw(android.graphics.Canvas r10) {
        /*
        r9 = this;
        r0 = r9.currentAnimationProgress;
        r1 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r2 = 0;
        r3 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1));
        if (r3 != 0) goto L_0x0013;
    L_0x0009:
        r0 = r9.getBounds();
        r3 = r9.paint;
        r10.drawRect(r0, r3);
        goto L_0x0069;
    L_0x0013:
        r3 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r3 == 0) goto L_0x0069;
    L_0x0017:
        r3 = r9.isSelected;
        if (r3 == 0) goto L_0x0022;
    L_0x001b:
        r3 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT;
        r0 = r3.getInterpolation(r0);
        goto L_0x002c;
    L_0x0022:
        r3 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT;
        r0 = r1 - r0;
        r0 = r3.getInterpolation(r0);
        r0 = r1 - r0;
    L_0x002c:
        r3 = r9.getBounds();
        r4 = r3.centerX();
        r4 = (float) r4;
        r3 = r3.centerY();
        r3 = (float) r3;
        r5 = r9.touchOverrideX;
        r6 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1));
        if (r6 < 0) goto L_0x0047;
    L_0x0040:
        r6 = r9.touchOverrideY;
        r7 = (r6 > r2 ? 1 : (r6 == r2 ? 0 : -1));
        if (r7 < 0) goto L_0x0047;
    L_0x0046:
        goto L_0x0056;
    L_0x0047:
        r5 = r9.touchX;
        r6 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1));
        if (r6 < 0) goto L_0x0054;
    L_0x004d:
        r6 = r9.touchY;
        r7 = (r6 > r2 ? 1 : (r6 == r2 ? 0 : -1));
        if (r7 < 0) goto L_0x0054;
    L_0x0053:
        goto L_0x0056;
    L_0x0054:
        r6 = r3;
        r5 = r4;
    L_0x0056:
        r7 = r1 - r0;
        r5 = r5 - r4;
        r5 = r5 * r7;
        r4 = r4 + r5;
        r6 = r6 - r3;
        r7 = r7 * r6;
        r3 = r3 + r7;
        r5 = r9.finalRadius;
        r5 = r5 * r0;
        r0 = r9.paint;
        r10.drawCircle(r4, r3, r5, r0);
    L_0x0069:
        r10 = r9.animationInProgress;
        if (r10 == 0) goto L_0x00b9;
    L_0x006d:
        r3 = android.os.SystemClock.uptimeMillis();
        r5 = r9.lastAnimationTime;
        r5 = r3 - r5;
        r7 = 20;
        r10 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1));
        if (r10 <= 0) goto L_0x007d;
    L_0x007b:
        r5 = 17;
    L_0x007d:
        r9.lastAnimationTime = r3;
        r10 = r9.isSelected;
        r0 = 1;
        r3 = NUM; // 0x43700000 float:240.0 double:5.589925436E-315;
        r4 = 0;
        if (r10 == 0) goto L_0x0097;
    L_0x0087:
        r10 = r9.currentAnimationProgress;
        r2 = (float) r5;
        r2 = r2 / r3;
        r10 = r10 + r2;
        r9.currentAnimationProgress = r10;
        r10 = r9.currentAnimationProgress;
        r10 = (r10 > r1 ? 1 : (r10 == r1 ? 0 : -1));
        if (r10 < 0) goto L_0x00a7;
    L_0x0094:
        r9.currentAnimationProgress = r1;
        goto L_0x00a8;
    L_0x0097:
        r10 = r9.currentAnimationProgress;
        r1 = (float) r5;
        r1 = r1 / r3;
        r10 = r10 - r1;
        r9.currentAnimationProgress = r10;
        r10 = r9.currentAnimationProgress;
        r10 = (r10 > r2 ? 1 : (r10 == r2 ? 0 : -1));
        if (r10 > 0) goto L_0x00a7;
    L_0x00a4:
        r9.currentAnimationProgress = r2;
        goto L_0x00a8;
    L_0x00a7:
        r0 = 0;
    L_0x00a8:
        if (r0 == 0) goto L_0x00b6;
    L_0x00aa:
        r10 = -NUM; // 0xffffffffbvar_ float:-1.0 double:NaN;
        r9.touchX = r10;
        r9.touchY = r10;
        r9.touchOverrideX = r10;
        r9.touchOverrideY = r10;
        r9.animationInProgress = r4;
    L_0x00b6:
        r9.invalidate();
    L_0x00b9:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.MessageBackgroundDrawable.draw(android.graphics.Canvas):void");
    }
}
