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
    private long lastFrameTime;
    private Paint paint = new Paint(1);
    private RectF rect;
    private int side;

    public int getOpacity() {
        return -2;
    }

    public void setAlpha(int i) {
    }

    public CloseProgressDrawable2() {
        new DecelerateInterpolator();
        this.rect = new RectF();
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

    public void setSide(int i) {
        this.side = i;
    }

    /* JADX WARNING: Removed duplicated region for block: B:61:0x00ee  */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x0101  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x0115  */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x0129  */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x0165  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x016f  */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x0171  */
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
            if (r5 == 0) goto L_0x0041
            long r1 = r8 - r1
            boolean r3 = r0.animating
            if (r3 != 0) goto L_0x001f
            float r4 = r0.angle
            int r4 = (r4 > r11 ? 1 : (r4 == r11 ? 0 : -1))
            if (r4 == 0) goto L_0x0041
        L_0x001f:
            float r4 = r0.angle
            r5 = 360(0x168, double:1.78E-321)
            long r1 = r1 * r5
            float r1 = (float) r1
            r2 = 1140457472(0x43fa0000, float:500.0)
            float r1 = r1 / r2
            float r4 = r4 + r1
            r0.angle = r4
            if (r3 != 0) goto L_0x0035
            int r1 = (r4 > r10 ? 1 : (r4 == r10 ? 0 : -1))
            if (r1 < 0) goto L_0x0035
            r0.angle = r11
            goto L_0x003e
        L_0x0035:
            float r1 = r4 / r10
            int r1 = (int) r1
            int r1 = r1 * 720
            float r1 = (float) r1
            float r4 = r4 - r1
            r0.angle = r4
        L_0x003e:
            r17.invalidateSelf()
        L_0x0041:
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
            if (r3 < 0) goto L_0x0075
            int r3 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r3 >= 0) goto L_0x0075
            float r1 = r1 / r2
            float r1 = r13 - r1
        L_0x006d:
            r14 = 1065353216(0x3var_, float:1.0)
        L_0x006f:
            r15 = 1065353216(0x3var_, float:1.0)
        L_0x0071:
            r16 = 0
            goto L_0x00ea
        L_0x0075:
            r3 = 1127481344(0x43340000, float:180.0)
            int r4 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r4 < 0) goto L_0x0086
            int r4 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r4 >= 0) goto L_0x0086
            float r1 = r1 - r2
            float r1 = r1 / r2
            float r1 = r13 - r1
            r14 = r1
            r1 = 0
            goto L_0x006f
        L_0x0086:
            r4 = 1132920832(0x43870000, float:270.0)
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 < 0) goto L_0x0098
            int r5 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r5 >= 0) goto L_0x0098
            float r1 = r1 - r3
            float r1 = r1 / r2
            float r1 = r13 - r1
            r15 = r1
            r1 = 0
            r14 = 0
            goto L_0x0071
        L_0x0098:
            int r3 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r3 < 0) goto L_0x00a8
            int r3 = (r1 > r12 ? 1 : (r1 == r12 ? 0 : -1))
            if (r3 >= 0) goto L_0x00a8
            float r1 = r1 - r4
            float r1 = r1 / r2
        L_0x00a2:
            r16 = r1
            r1 = 0
            r14 = 0
            r15 = 0
            goto L_0x00ea
        L_0x00a8:
            r3 = 1138819072(0x43e10000, float:450.0)
            int r4 = (r1 > r12 ? 1 : (r1 == r12 ? 0 : -1))
            if (r4 < 0) goto L_0x00b7
            int r4 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r4 >= 0) goto L_0x00b7
            float r1 = r1 - r12
            float r1 = r1 / r2
            float r1 = r13 - r1
            goto L_0x00a2
        L_0x00b7:
            r4 = 1141309440(0x44070000, float:540.0)
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 < 0) goto L_0x00c6
            int r5 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r5 >= 0) goto L_0x00c6
            float r1 = r1 - r3
            float r1 = r1 / r2
            r14 = 0
        L_0x00c4:
            r15 = 0
            goto L_0x0071
        L_0x00c6:
            r3 = 1142784000(0x441d8000, float:630.0)
            int r5 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r5 < 0) goto L_0x00d7
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 >= 0) goto L_0x00d7
            float r1 = r1 - r4
            float r1 = r1 / r2
            r14 = r1
            r1 = 1065353216(0x3var_, float:1.0)
            goto L_0x00c4
        L_0x00d7:
            int r4 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r4 < 0) goto L_0x00e7
            int r4 = (r1 > r10 ? 1 : (r1 == r10 ? 0 : -1))
            if (r4 >= 0) goto L_0x00e7
            float r1 = r1 - r3
            float r1 = r1 / r2
            r15 = r1
            r1 = 1065353216(0x3var_, float:1.0)
            r14 = 1065353216(0x3var_, float:1.0)
            goto L_0x0071
        L_0x00e7:
            r1 = 1065353216(0x3var_, float:1.0)
            goto L_0x006d
        L_0x00ea:
            int r2 = (r1 > r11 ? 1 : (r1 == r11 ? 0 : -1))
            if (r2 == 0) goto L_0x00fd
            r2 = 0
            r3 = 0
            r4 = 0
            int r5 = r0.side
            float r5 = (float) r5
            float r5 = r5 * r1
            android.graphics.Paint r6 = r0.paint
            r1 = r18
            r1.drawLine(r2, r3, r4, r5, r6)
        L_0x00fd:
            int r1 = (r14 > r11 ? 1 : (r14 == r11 ? 0 : -1))
            if (r1 == 0) goto L_0x0111
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
        L_0x0111:
            int r1 = (r15 > r11 ? 1 : (r15 == r11 ? 0 : -1))
            if (r1 == 0) goto L_0x0125
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
        L_0x0125:
            int r1 = (r16 > r13 ? 1 : (r16 == r13 ? 0 : -1))
            if (r1 == 0) goto L_0x0138
            int r1 = r0.side
            float r2 = (float) r1
            float r2 = r2 * r16
            r3 = 0
            float r4 = (float) r1
            r5 = 0
            android.graphics.Paint r6 = r0.paint
            r1 = r18
            r1.drawLine(r2, r3, r4, r5, r6)
        L_0x0138:
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
            if (r3 >= 0) goto L_0x0165
            goto L_0x0167
        L_0x0165:
            float r11 = r1 - r12
        L_0x0167:
            r3 = 1110704128(0x42340000, float:45.0)
            float r3 = r11 - r3
            int r4 = (r1 > r12 ? 1 : (r1 == r12 ? 0 : -1))
            if (r4 >= 0) goto L_0x0171
            r4 = r1
            goto L_0x0173
        L_0x0171:
            float r10 = r10 - r1
            r4 = r10
        L_0x0173:
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
