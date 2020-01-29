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
        if (this.isSelected != z) {
            this.isSelected = z;
            this.animationInProgress = z2;
            if (z2) {
                this.lastAnimationTime = SystemClock.elapsedRealtime();
            } else {
                if (!z) {
                    f = 0.0f;
                }
                this.currentAnimationProgress = f;
            }
            calcRadius();
            invalidate();
        } else if (this.animationInProgress != z2 && !z2) {
            if (!z) {
                f = 0.0f;
            }
            this.currentAnimationProgress = f;
            this.animationInProgress = false;
        }
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
        int i2 = bounds.top;
        this.finalRadius = (float) Math.ceil(Math.sqrt((double) (((((float) i) - centerX) * (((float) i) - centerX)) + ((((float) i2) - centerY) * (((float) i2) - centerY)))));
    }

    public void setTouchCoords(float f, float f2) {
        this.touchX = f;
        this.touchY = f2;
        this.lastTouchTime = SystemClock.elapsedRealtime();
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

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0044, code lost:
        if (r6 >= 0.0f) goto L_0x0056;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0051, code lost:
        if (r6 >= 0.0f) goto L_0x0056;
     */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x00aa  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void draw(android.graphics.Canvas r10) {
        /*
            r9 = this;
            float r0 = r9.currentAnimationProgress
            r1 = 1065353216(0x3var_, float:1.0)
            r2 = 0
            int r3 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r3 != 0) goto L_0x0013
            android.graphics.Rect r0 = r9.getBounds()
            android.graphics.Paint r3 = r9.paint
            r10.drawRect(r0, r3)
            goto L_0x0069
        L_0x0013:
            int r3 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r3 == 0) goto L_0x0069
            boolean r3 = r9.isSelected
            if (r3 == 0) goto L_0x0022
            org.telegram.ui.Components.CubicBezierInterpolator r3 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT
            float r0 = r3.getInterpolation(r0)
            goto L_0x002c
        L_0x0022:
            org.telegram.ui.Components.CubicBezierInterpolator r3 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT
            float r0 = r1 - r0
            float r0 = r3.getInterpolation(r0)
            float r0 = r1 - r0
        L_0x002c:
            android.graphics.Rect r3 = r9.getBounds()
            int r4 = r3.centerX()
            float r4 = (float) r4
            int r3 = r3.centerY()
            float r3 = (float) r3
            float r5 = r9.touchOverrideX
            int r6 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1))
            if (r6 < 0) goto L_0x0047
            float r6 = r9.touchOverrideY
            int r7 = (r6 > r2 ? 1 : (r6 == r2 ? 0 : -1))
            if (r7 < 0) goto L_0x0047
            goto L_0x0056
        L_0x0047:
            float r5 = r9.touchX
            int r6 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1))
            if (r6 < 0) goto L_0x0054
            float r6 = r9.touchY
            int r7 = (r6 > r2 ? 1 : (r6 == r2 ? 0 : -1))
            if (r7 < 0) goto L_0x0054
            goto L_0x0056
        L_0x0054:
            r6 = r3
            r5 = r4
        L_0x0056:
            float r7 = r1 - r0
            float r5 = r5 - r4
            float r5 = r5 * r7
            float r4 = r4 + r5
            float r6 = r6 - r3
            float r7 = r7 * r6
            float r3 = r3 + r7
            float r5 = r9.finalRadius
            float r5 = r5 * r0
            android.graphics.Paint r0 = r9.paint
            r10.drawCircle(r4, r3, r5, r0)
        L_0x0069:
            boolean r10 = r9.animationInProgress
            if (r10 == 0) goto L_0x00b9
            long r3 = android.os.SystemClock.elapsedRealtime()
            long r5 = r9.lastAnimationTime
            long r5 = r3 - r5
            r7 = 20
            int r10 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r10 <= 0) goto L_0x007d
            r5 = 17
        L_0x007d:
            r9.lastAnimationTime = r3
            boolean r10 = r9.isSelected
            r0 = 1
            r3 = 1131413504(0x43700000, float:240.0)
            r4 = 0
            if (r10 == 0) goto L_0x0097
            float r10 = r9.currentAnimationProgress
            float r2 = (float) r5
            float r2 = r2 / r3
            float r10 = r10 + r2
            r9.currentAnimationProgress = r10
            float r10 = r9.currentAnimationProgress
            int r10 = (r10 > r1 ? 1 : (r10 == r1 ? 0 : -1))
            if (r10 < 0) goto L_0x00a7
            r9.currentAnimationProgress = r1
            goto L_0x00a8
        L_0x0097:
            float r10 = r9.currentAnimationProgress
            float r1 = (float) r5
            float r1 = r1 / r3
            float r10 = r10 - r1
            r9.currentAnimationProgress = r10
            float r10 = r9.currentAnimationProgress
            int r10 = (r10 > r2 ? 1 : (r10 == r2 ? 0 : -1))
            if (r10 > 0) goto L_0x00a7
            r9.currentAnimationProgress = r2
            goto L_0x00a8
        L_0x00a7:
            r0 = 0
        L_0x00a8:
            if (r0 == 0) goto L_0x00b6
            r10 = -1082130432(0xffffffffbvar_, float:-1.0)
            r9.touchX = r10
            r9.touchY = r10
            r9.touchOverrideX = r10
            r9.touchOverrideY = r10
            r9.animationInProgress = r4
        L_0x00b6:
            r9.invalidate()
        L_0x00b9:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.MessageBackgroundDrawable.draw(android.graphics.Canvas):void");
    }
}
