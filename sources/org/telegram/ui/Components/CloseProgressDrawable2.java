package org.telegram.ui.Components;

import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.animation.DecelerateInterpolator;
import org.telegram.messenger.AndroidUtilities;

public class CloseProgressDrawable2 extends Drawable {
    private float angle;
    private boolean animating;
    private DecelerateInterpolator interpolator = new DecelerateInterpolator();
    private long lastFrameTime;
    private Paint paint = new Paint(1);
    private RectF rect = new RectF();
    private int side;

    public int getOpacity() {
        return -2;
    }

    public void setAlpha(int i) {
    }

    public CloseProgressDrawable2() {
        this.paint.setColor(-1);
        this.paint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.paint.setStrokeCap(Paint.Cap.ROUND);
        this.paint.setStyle(Paint.Style.STROKE);
        this.side = AndroidUtilities.dp(8.0f);
    }

    public void startAnimation() {
        this.animating = true;
        this.lastFrameTime = System.currentTimeMillis();
        invalidateSelf();
    }

    public void stopAnimation() {
        this.animating = false;
    }

    public boolean isAnimating() {
        return this.animating;
    }

    public void setColor(int i) {
        this.paint.setColor(i);
    }

    public void setSide(int i) {
        this.side = i;
    }

    /* JADX WARNING: Removed duplicated region for block: B:61:0x0104  */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x0117  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x012b  */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x013f  */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x017b  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x0187  */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x0189  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void draw(android.graphics.Canvas r18) {
        /*
            r17 = this;
            r0 = r17
            r7 = r18
            long r8 = java.lang.System.currentTimeMillis()
            long r1 = r0.lastFrameTime
            r10 = 1144258560(0x44340000, float:720.0)
            r11 = 0
            r3 = 0
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 == 0) goto L_0x0047
            long r1 = r8 - r1
            boolean r3 = r0.animating
            if (r3 != 0) goto L_0x001f
            float r3 = r0.angle
            int r3 = (r3 > r11 ? 1 : (r3 == r11 ? 0 : -1))
            if (r3 == 0) goto L_0x0047
        L_0x001f:
            float r3 = r0.angle
            r4 = 360(0x168, double:1.78E-321)
            long r1 = r1 * r4
            float r1 = (float) r1
            r2 = 1140457472(0x43fa0000, float:500.0)
            float r1 = r1 / r2
            float r3 = r3 + r1
            r0.angle = r3
            boolean r1 = r0.animating
            if (r1 != 0) goto L_0x0039
            float r1 = r0.angle
            int r1 = (r1 > r10 ? 1 : (r1 == r10 ? 0 : -1))
            if (r1 < 0) goto L_0x0039
            r0.angle = r11
            goto L_0x0044
        L_0x0039:
            float r1 = r0.angle
            float r2 = r1 / r10
            int r2 = (int) r2
            int r2 = r2 * 720
            float r2 = (float) r2
            float r1 = r1 - r2
            r0.angle = r1
        L_0x0044:
            r17.invalidateSelf()
        L_0x0047:
            r18.save()
            int r1 = r17.getIntrinsicWidth()
            int r1 = r1 / 2
            float r1 = (float) r1
            int r2 = r17.getIntrinsicHeight()
            int r2 = r2 / 2
            float r2 = (float) r2
            r7.translate(r1, r2)
            r1 = -1036779520(0xffffffffCLASSNAME, float:-45.0)
            r7.rotate(r1)
            float r1 = r0.angle
            r12 = 1135869952(0x43b40000, float:360.0)
            r13 = 1065353216(0x3var_, float:1.0)
            r2 = 1119092736(0x42b40000, float:90.0)
            int r3 = (r1 > r11 ? 1 : (r1 == r11 ? 0 : -1))
            if (r3 < 0) goto L_0x007b
            int r3 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r3 >= 0) goto L_0x007b
            float r1 = r1 / r2
            float r1 = r13 - r1
        L_0x0073:
            r14 = 1065353216(0x3var_, float:1.0)
        L_0x0075:
            r15 = 1065353216(0x3var_, float:1.0)
        L_0x0077:
            r16 = 0
            goto L_0x0100
        L_0x007b:
            float r1 = r0.angle
            r3 = 1127481344(0x43340000, float:180.0)
            int r4 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r4 < 0) goto L_0x008e
            int r4 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r4 >= 0) goto L_0x008e
            float r1 = r1 - r2
            float r1 = r1 / r2
            float r1 = r13 - r1
            r14 = r1
            r1 = 0
            goto L_0x0075
        L_0x008e:
            float r1 = r0.angle
            r4 = 1132920832(0x43870000, float:270.0)
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 < 0) goto L_0x00a2
            int r5 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r5 >= 0) goto L_0x00a2
            float r1 = r1 - r3
            float r1 = r1 / r2
            float r1 = r13 - r1
            r15 = r1
            r1 = 0
            r14 = 0
            goto L_0x0077
        L_0x00a2:
            float r1 = r0.angle
            int r3 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r3 < 0) goto L_0x00b4
            int r3 = (r1 > r12 ? 1 : (r1 == r12 ? 0 : -1))
            if (r3 >= 0) goto L_0x00b4
            float r1 = r1 - r4
            float r1 = r1 / r2
        L_0x00ae:
            r16 = r1
            r1 = 0
            r14 = 0
            r15 = 0
            goto L_0x0100
        L_0x00b4:
            float r1 = r0.angle
            r3 = 1138819072(0x43e10000, float:450.0)
            int r4 = (r1 > r12 ? 1 : (r1 == r12 ? 0 : -1))
            if (r4 < 0) goto L_0x00c5
            int r4 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r4 >= 0) goto L_0x00c5
            float r1 = r1 - r12
            float r1 = r1 / r2
            float r1 = r13 - r1
            goto L_0x00ae
        L_0x00c5:
            float r1 = r0.angle
            r4 = 1141309440(0x44070000, float:540.0)
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 < 0) goto L_0x00d6
            int r5 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r5 >= 0) goto L_0x00d6
            float r1 = r1 - r3
            float r1 = r1 / r2
            r14 = 0
        L_0x00d4:
            r15 = 0
            goto L_0x0077
        L_0x00d6:
            float r1 = r0.angle
            r3 = 1142784000(0x441d8000, float:630.0)
            int r5 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r5 < 0) goto L_0x00e9
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 >= 0) goto L_0x00e9
            float r1 = r1 - r4
            float r1 = r1 / r2
            r14 = r1
            r1 = 1065353216(0x3var_, float:1.0)
            goto L_0x00d4
        L_0x00e9:
            float r1 = r0.angle
            int r4 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r4 < 0) goto L_0x00fc
            int r4 = (r1 > r10 ? 1 : (r1 == r10 ? 0 : -1))
            if (r4 >= 0) goto L_0x00fc
            float r1 = r1 - r3
            float r1 = r1 / r2
            r15 = r1
            r1 = 1065353216(0x3var_, float:1.0)
            r14 = 1065353216(0x3var_, float:1.0)
            goto L_0x0077
        L_0x00fc:
            r1 = 1065353216(0x3var_, float:1.0)
            goto L_0x0073
        L_0x0100:
            int r2 = (r1 > r11 ? 1 : (r1 == r11 ? 0 : -1))
            if (r2 == 0) goto L_0x0113
            r2 = 0
            r3 = 0
            r4 = 0
            int r5 = r0.side
            float r5 = (float) r5
            float r5 = r5 * r1
            android.graphics.Paint r6 = r0.paint
            r1 = r18
            r1.drawLine(r2, r3, r4, r5, r6)
        L_0x0113:
            int r1 = (r14 > r11 ? 1 : (r14 == r11 ? 0 : -1))
            if (r1 == 0) goto L_0x0127
            int r1 = r0.side
            int r1 = -r1
            float r1 = (float) r1
            float r2 = r1 * r14
            r3 = 0
            r4 = 0
            r5 = 0
            android.graphics.Paint r6 = r0.paint
            r1 = r18
            r1.drawLine(r2, r3, r4, r5, r6)
        L_0x0127:
            int r1 = (r15 > r11 ? 1 : (r15 == r11 ? 0 : -1))
            if (r1 == 0) goto L_0x013b
            r2 = 0
            int r1 = r0.side
            int r1 = -r1
            float r1 = (float) r1
            float r3 = r1 * r15
            r4 = 0
            r5 = 0
            android.graphics.Paint r6 = r0.paint
            r1 = r18
            r1.drawLine(r2, r3, r4, r5, r6)
        L_0x013b:
            int r1 = (r16 > r13 ? 1 : (r16 == r13 ? 0 : -1))
            if (r1 == 0) goto L_0x014e
            int r1 = r0.side
            float r2 = (float) r1
            float r2 = r2 * r16
            r3 = 0
            float r4 = (float) r1
            r5 = 0
            android.graphics.Paint r6 = r0.paint
            r1 = r18
            r1.drawLine(r2, r3, r4, r5, r6)
        L_0x014e:
            r18.restore()
            android.graphics.Rect r1 = r17.getBounds()
            int r1 = r1.centerX()
            android.graphics.Rect r2 = r17.getBounds()
            int r2 = r2.centerY()
            android.graphics.RectF r3 = r0.rect
            int r4 = r0.side
            int r5 = r1 - r4
            float r5 = (float) r5
            int r6 = r2 - r4
            float r6 = (float) r6
            int r1 = r1 + r4
            float r1 = (float) r1
            int r2 = r2 + r4
            float r2 = (float) r2
            r3.set(r5, r6, r1, r2)
            android.graphics.RectF r2 = r0.rect
            float r1 = r0.angle
            int r3 = (r1 > r12 ? 1 : (r1 == r12 ? 0 : -1))
            if (r3 >= 0) goto L_0x017b
            goto L_0x017d
        L_0x017b:
            float r11 = r1 - r12
        L_0x017d:
            r1 = 1110704128(0x42340000, float:45.0)
            float r3 = r11 - r1
            float r1 = r0.angle
            int r4 = (r1 > r12 ? 1 : (r1 == r12 ? 0 : -1))
            if (r4 >= 0) goto L_0x0189
            r4 = r1
            goto L_0x018b
        L_0x0189:
            float r10 = r10 - r1
            r4 = r10
        L_0x018b:
            r5 = 0
            android.graphics.Paint r6 = r0.paint
            r1 = r18
            r1.drawArc(r2, r3, r4, r5, r6)
            r0.lastFrameTime = r8
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.CloseProgressDrawable2.draw(android.graphics.Canvas):void");
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.paint.setColorFilter(colorFilter);
    }

    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(24.0f);
    }

    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(24.0f);
    }
}
