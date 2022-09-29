package org.telegram.ui.Components;

import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.animation.DecelerateInterpolator;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;

public class CloseProgressDrawable2 extends Drawable {
    private float angle;
    private boolean animating;
    private int currentColor;
    private int globalColorAlpha;
    private long lastFrameTime;
    private Paint paint;
    private RectF rect;
    private int side;

    /* access modifiers changed from: protected */
    public int getCurrentColor() {
        throw null;
    }

    public int getOpacity() {
        return -2;
    }

    public void setAlpha(int i) {
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    public CloseProgressDrawable2() {
        this(2.0f);
    }

    public CloseProgressDrawable2(float f) {
        this.paint = new Paint(1);
        new DecelerateInterpolator();
        this.rect = new RectF();
        this.globalColorAlpha = 255;
        this.paint.setColor(-1);
        this.paint.setStrokeWidth((float) AndroidUtilities.dp(f));
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

    private void setColor(int i) {
        if (this.currentColor != i) {
            this.globalColorAlpha = Color.alpha(i);
            this.paint.setColor(ColorUtils.setAlphaComponent(i, 255));
        }
    }

    public void setSide(int i) {
        this.side = i;
    }

    /* JADX WARNING: Removed duplicated region for block: B:69:0x0132  */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x0145  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x0159  */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x016d  */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x01a9  */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x01b3  */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x01b5  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void draw(android.graphics.Canvas r18) {
        /*
            r17 = this;
            r0 = r17
            r8 = r18
            long r9 = java.lang.System.currentTimeMillis()
            int r1 = r17.getCurrentColor()
            r0.setColor(r1)
            long r1 = r0.lastFrameTime
            r11 = 1144258560(0x44340000, float:720.0)
            r12 = 0
            r3 = 0
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 == 0) goto L_0x0048
            long r1 = r9 - r1
            boolean r3 = r0.animating
            if (r3 != 0) goto L_0x0026
            float r4 = r0.angle
            int r4 = (r4 > r12 ? 1 : (r4 == r12 ? 0 : -1))
            if (r4 == 0) goto L_0x0048
        L_0x0026:
            float r4 = r0.angle
            r5 = 360(0x168, double:1.78E-321)
            long r1 = r1 * r5
            float r1 = (float) r1
            r2 = 1140457472(0x43fa0000, float:500.0)
            float r1 = r1 / r2
            float r4 = r4 + r1
            r0.angle = r4
            if (r3 != 0) goto L_0x003c
            int r1 = (r4 > r11 ? 1 : (r4 == r11 ? 0 : -1))
            if (r1 < 0) goto L_0x003c
            r0.angle = r12
            goto L_0x0045
        L_0x003c:
            float r1 = r4 / r11
            int r1 = (int) r1
            int r1 = r1 * 720
            float r1 = (float) r1
            float r4 = r4 - r1
            r0.angle = r4
        L_0x0045:
            r17.invalidateSelf()
        L_0x0048:
            int r1 = r0.globalColorAlpha
            r2 = 255(0xff, float:3.57E-43)
            if (r1 == r2) goto L_0x0085
            android.graphics.Rect r1 = r17.getBounds()
            if (r1 == 0) goto L_0x0085
            android.graphics.Rect r1 = r17.getBounds()
            boolean r1 = r1.isEmpty()
            if (r1 == 0) goto L_0x005f
            goto L_0x0085
        L_0x005f:
            android.graphics.Rect r1 = r17.getBounds()
            int r1 = r1.left
            float r2 = (float) r1
            android.graphics.Rect r1 = r17.getBounds()
            int r1 = r1.top
            float r3 = (float) r1
            android.graphics.Rect r1 = r17.getBounds()
            int r1 = r1.right
            float r4 = (float) r1
            android.graphics.Rect r1 = r17.getBounds()
            int r1 = r1.bottom
            float r5 = (float) r1
            int r6 = r0.globalColorAlpha
            r7 = 31
            r1 = r18
            r1.saveLayerAlpha(r2, r3, r4, r5, r6, r7)
            goto L_0x0088
        L_0x0085:
            r18.save()
        L_0x0088:
            int r1 = r17.getIntrinsicWidth()
            int r1 = r1 / 2
            float r1 = (float) r1
            int r2 = r17.getIntrinsicHeight()
            int r2 = r2 / 2
            float r2 = (float) r2
            r8.translate(r1, r2)
            r1 = -1036779520(0xffffffffCLASSNAME, float:-45.0)
            r8.rotate(r1)
            float r1 = r0.angle
            r7 = 1135869952(0x43b40000, float:360.0)
            r13 = 1065353216(0x3var_, float:1.0)
            r2 = 1119092736(0x42b40000, float:90.0)
            int r3 = (r1 > r12 ? 1 : (r1 == r12 ? 0 : -1))
            if (r3 < 0) goto L_0x00b9
            int r3 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r3 >= 0) goto L_0x00b9
            float r1 = r1 / r2
            float r1 = r13 - r1
        L_0x00b1:
            r14 = 1065353216(0x3var_, float:1.0)
        L_0x00b3:
            r15 = 1065353216(0x3var_, float:1.0)
        L_0x00b5:
            r16 = 0
            goto L_0x012e
        L_0x00b9:
            r3 = 1127481344(0x43340000, float:180.0)
            int r4 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r4 < 0) goto L_0x00ca
            int r4 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r4 >= 0) goto L_0x00ca
            float r1 = r1 - r2
            float r1 = r1 / r2
            float r1 = r13 - r1
            r14 = r1
            r1 = 0
            goto L_0x00b3
        L_0x00ca:
            r4 = 1132920832(0x43870000, float:270.0)
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 < 0) goto L_0x00dc
            int r5 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r5 >= 0) goto L_0x00dc
            float r1 = r1 - r3
            float r1 = r1 / r2
            float r1 = r13 - r1
            r15 = r1
            r1 = 0
            r14 = 0
            goto L_0x00b5
        L_0x00dc:
            int r3 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r3 < 0) goto L_0x00ec
            int r3 = (r1 > r7 ? 1 : (r1 == r7 ? 0 : -1))
            if (r3 >= 0) goto L_0x00ec
            float r1 = r1 - r4
            float r1 = r1 / r2
        L_0x00e6:
            r16 = r1
            r1 = 0
            r14 = 0
            r15 = 0
            goto L_0x012e
        L_0x00ec:
            r3 = 1138819072(0x43e10000, float:450.0)
            int r4 = (r1 > r7 ? 1 : (r1 == r7 ? 0 : -1))
            if (r4 < 0) goto L_0x00fb
            int r4 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r4 >= 0) goto L_0x00fb
            float r1 = r1 - r7
            float r1 = r1 / r2
            float r1 = r13 - r1
            goto L_0x00e6
        L_0x00fb:
            r4 = 1141309440(0x44070000, float:540.0)
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 < 0) goto L_0x010a
            int r5 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r5 >= 0) goto L_0x010a
            float r1 = r1 - r3
            float r1 = r1 / r2
            r14 = 0
        L_0x0108:
            r15 = 0
            goto L_0x00b5
        L_0x010a:
            r3 = 1142784000(0x441d8000, float:630.0)
            int r5 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r5 < 0) goto L_0x011b
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 >= 0) goto L_0x011b
            float r1 = r1 - r4
            float r1 = r1 / r2
            r14 = r1
            r1 = 1065353216(0x3var_, float:1.0)
            goto L_0x0108
        L_0x011b:
            int r4 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r4 < 0) goto L_0x012b
            int r4 = (r1 > r11 ? 1 : (r1 == r11 ? 0 : -1))
            if (r4 >= 0) goto L_0x012b
            float r1 = r1 - r3
            float r1 = r1 / r2
            r15 = r1
            r1 = 1065353216(0x3var_, float:1.0)
            r14 = 1065353216(0x3var_, float:1.0)
            goto L_0x00b5
        L_0x012b:
            r1 = 1065353216(0x3var_, float:1.0)
            goto L_0x00b1
        L_0x012e:
            int r2 = (r1 > r12 ? 1 : (r1 == r12 ? 0 : -1))
            if (r2 == 0) goto L_0x0141
            r2 = 0
            r3 = 0
            r4 = 0
            int r5 = r0.side
            float r5 = (float) r5
            float r5 = r5 * r1
            android.graphics.Paint r6 = r0.paint
            r1 = r18
            r1.drawLine(r2, r3, r4, r5, r6)
        L_0x0141:
            int r1 = (r14 > r12 ? 1 : (r14 == r12 ? 0 : -1))
            if (r1 == 0) goto L_0x0155
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
        L_0x0155:
            int r1 = (r15 > r12 ? 1 : (r15 == r12 ? 0 : -1))
            if (r1 == 0) goto L_0x0169
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
        L_0x0169:
            int r1 = (r16 > r13 ? 1 : (r16 == r13 ? 0 : -1))
            if (r1 == 0) goto L_0x017c
            int r1 = r0.side
            float r2 = (float) r1
            float r2 = r2 * r16
            r3 = 0
            float r4 = (float) r1
            r5 = 0
            android.graphics.Paint r6 = r0.paint
            r1 = r18
            r1.drawLine(r2, r3, r4, r5, r6)
        L_0x017c:
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
            int r3 = (r1 > r7 ? 1 : (r1 == r7 ? 0 : -1))
            if (r3 >= 0) goto L_0x01a9
            goto L_0x01ab
        L_0x01a9:
            float r12 = r1 - r7
        L_0x01ab:
            r3 = 1110704128(0x42340000, float:45.0)
            float r3 = r12 - r3
            int r4 = (r1 > r7 ? 1 : (r1 == r7 ? 0 : -1))
            if (r4 >= 0) goto L_0x01b5
            r4 = r1
            goto L_0x01b7
        L_0x01b5:
            float r11 = r11 - r1
            r4 = r11
        L_0x01b7:
            r5 = 0
            android.graphics.Paint r6 = r0.paint
            r1 = r18
            r1.drawArc(r2, r3, r4, r5, r6)
            r0.lastFrameTime = r9
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.CloseProgressDrawable2.draw(android.graphics.Canvas):void");
    }

    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(24.0f);
    }

    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(24.0f);
    }
}
