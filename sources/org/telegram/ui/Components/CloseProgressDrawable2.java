package org.telegram.ui.Components;

import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
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
        this.paint.setStrokeCap(Cap.ROUND);
        this.paint.setStyle(Style.STROKE);
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
    /* JADX WARNING: Removed duplicated region for block: B:77:0x0189  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x0187  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x0104  */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x0117  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x012b  */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x013f  */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x017b  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x0187  */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x0189  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x0104  */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x0117  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x012b  */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x013f  */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x017b  */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x0189  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x0187  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x0104  */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x0117  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x012b  */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x013f  */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x017b  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x0187  */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x0189  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x0104  */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x0117  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x012b  */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x013f  */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x017b  */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x0189  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x0187  */
    public void draw(android.graphics.Canvas r18) {
        /*
        r17 = this;
        r0 = r17;
        r7 = r18;
        r8 = java.lang.System.currentTimeMillis();
        r1 = r0.lastFrameTime;
        r10 = NUM; // 0x44340000 float:720.0 double:5.653388445E-315;
        r11 = 0;
        r3 = 0;
        r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1));
        if (r5 == 0) goto L_0x0047;
    L_0x0013:
        r1 = r8 - r1;
        r3 = r0.animating;
        if (r3 != 0) goto L_0x001f;
    L_0x0019:
        r3 = r0.angle;
        r3 = (r3 > r11 ? 1 : (r3 == r11 ? 0 : -1));
        if (r3 == 0) goto L_0x0047;
    L_0x001f:
        r3 = r0.angle;
        r4 = 360; // 0x168 float:5.04E-43 double:1.78E-321;
        r1 = r1 * r4;
        r1 = (float) r1;
        r2 = NUM; // 0x43fa0000 float:500.0 double:5.634608575E-315;
        r1 = r1 / r2;
        r3 = r3 + r1;
        r0.angle = r3;
        r1 = r0.animating;
        if (r1 != 0) goto L_0x0039;
    L_0x0030:
        r1 = r0.angle;
        r1 = (r1 > r10 ? 1 : (r1 == r10 ? 0 : -1));
        if (r1 < 0) goto L_0x0039;
    L_0x0036:
        r0.angle = r11;
        goto L_0x0044;
    L_0x0039:
        r1 = r0.angle;
        r2 = r1 / r10;
        r2 = (int) r2;
        r2 = r2 * 720;
        r2 = (float) r2;
        r1 = r1 - r2;
        r0.angle = r1;
    L_0x0044:
        r17.invalidateSelf();
    L_0x0047:
        r18.save();
        r1 = r17.getIntrinsicWidth();
        r1 = r1 / 2;
        r1 = (float) r1;
        r2 = r17.getIntrinsicHeight();
        r2 = r2 / 2;
        r2 = (float) r2;
        r7.translate(r1, r2);
        r1 = -NUM; // 0xffffffffCLASSNAME float:-45.0 double:NaN;
        r7.rotate(r1);
        r1 = r0.angle;
        r12 = NUM; // 0x43b40000 float:360.0 double:5.611943214E-315;
        r13 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r2 = NUM; // 0x42b40000 float:90.0 double:5.529052754E-315;
        r3 = (r1 > r11 ? 1 : (r1 == r11 ? 0 : -1));
        if (r3 < 0) goto L_0x007b;
    L_0x006c:
        r3 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1));
        if (r3 >= 0) goto L_0x007b;
    L_0x0070:
        r1 = r1 / r2;
        r1 = r13 - r1;
    L_0x0073:
        r14 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
    L_0x0075:
        r15 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
    L_0x0077:
        r16 = 0;
        goto L_0x0100;
    L_0x007b:
        r1 = r0.angle;
        r3 = NUM; // 0x43340000 float:180.0 double:5.570497984E-315;
        r4 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1));
        if (r4 < 0) goto L_0x008e;
    L_0x0083:
        r4 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1));
        if (r4 >= 0) goto L_0x008e;
    L_0x0087:
        r1 = r1 - r2;
        r1 = r1 / r2;
        r1 = r13 - r1;
        r14 = r1;
        r1 = 0;
        goto L_0x0075;
    L_0x008e:
        r1 = r0.angle;
        r4 = NUM; // 0x43870000 float:270.0 double:5.597372625E-315;
        r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1));
        if (r5 < 0) goto L_0x00a2;
    L_0x0096:
        r5 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1));
        if (r5 >= 0) goto L_0x00a2;
    L_0x009a:
        r1 = r1 - r3;
        r1 = r1 / r2;
        r1 = r13 - r1;
        r15 = r1;
        r1 = 0;
        r14 = 0;
        goto L_0x0077;
    L_0x00a2:
        r1 = r0.angle;
        r3 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1));
        if (r3 < 0) goto L_0x00b4;
    L_0x00a8:
        r3 = (r1 > r12 ? 1 : (r1 == r12 ? 0 : -1));
        if (r3 >= 0) goto L_0x00b4;
    L_0x00ac:
        r1 = r1 - r4;
        r1 = r1 / r2;
    L_0x00ae:
        r16 = r1;
        r1 = 0;
        r14 = 0;
        r15 = 0;
        goto L_0x0100;
    L_0x00b4:
        r1 = r0.angle;
        r3 = NUM; // 0x43e10000 float:450.0 double:5.626513803E-315;
        r4 = (r1 > r12 ? 1 : (r1 == r12 ? 0 : -1));
        if (r4 < 0) goto L_0x00c5;
    L_0x00bc:
        r4 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1));
        if (r4 >= 0) goto L_0x00c5;
    L_0x00c0:
        r1 = r1 - r12;
        r1 = r1 / r2;
        r1 = r13 - r1;
        goto L_0x00ae;
    L_0x00c5:
        r1 = r0.angle;
        r4 = NUM; // 0x44070000 float:540.0 double:5.638817856E-315;
        r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1));
        if (r5 < 0) goto L_0x00d6;
    L_0x00cd:
        r5 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1));
        if (r5 >= 0) goto L_0x00d6;
    L_0x00d1:
        r1 = r1 - r3;
        r1 = r1 / r2;
        r14 = 0;
    L_0x00d4:
        r15 = 0;
        goto L_0x0077;
    L_0x00d6:
        r1 = r0.angle;
        r3 = NUM; // 0x441d8000 float:630.0 double:5.64610315E-315;
        r5 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1));
        if (r5 < 0) goto L_0x00e9;
    L_0x00df:
        r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1));
        if (r5 >= 0) goto L_0x00e9;
    L_0x00e3:
        r1 = r1 - r4;
        r1 = r1 / r2;
        r14 = r1;
        r1 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        goto L_0x00d4;
    L_0x00e9:
        r1 = r0.angle;
        r4 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1));
        if (r4 < 0) goto L_0x00fc;
    L_0x00ef:
        r4 = (r1 > r10 ? 1 : (r1 == r10 ? 0 : -1));
        if (r4 >= 0) goto L_0x00fc;
    L_0x00f3:
        r1 = r1 - r3;
        r1 = r1 / r2;
        r15 = r1;
        r1 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r14 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        goto L_0x0077;
    L_0x00fc:
        r1 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        goto L_0x0073;
    L_0x0100:
        r2 = (r1 > r11 ? 1 : (r1 == r11 ? 0 : -1));
        if (r2 == 0) goto L_0x0113;
    L_0x0104:
        r2 = 0;
        r3 = 0;
        r4 = 0;
        r5 = r0.side;
        r5 = (float) r5;
        r5 = r5 * r1;
        r6 = r0.paint;
        r1 = r18;
        r1.drawLine(r2, r3, r4, r5, r6);
    L_0x0113:
        r1 = (r14 > r11 ? 1 : (r14 == r11 ? 0 : -1));
        if (r1 == 0) goto L_0x0127;
    L_0x0117:
        r1 = r0.side;
        r1 = -r1;
        r1 = (float) r1;
        r2 = r1 * r14;
        r3 = 0;
        r4 = 0;
        r5 = 0;
        r6 = r0.paint;
        r1 = r18;
        r1.drawLine(r2, r3, r4, r5, r6);
    L_0x0127:
        r1 = (r15 > r11 ? 1 : (r15 == r11 ? 0 : -1));
        if (r1 == 0) goto L_0x013b;
    L_0x012b:
        r2 = 0;
        r1 = r0.side;
        r1 = -r1;
        r1 = (float) r1;
        r3 = r1 * r15;
        r4 = 0;
        r5 = 0;
        r6 = r0.paint;
        r1 = r18;
        r1.drawLine(r2, r3, r4, r5, r6);
    L_0x013b:
        r1 = (r16 > r13 ? 1 : (r16 == r13 ? 0 : -1));
        if (r1 == 0) goto L_0x014e;
    L_0x013f:
        r1 = r0.side;
        r2 = (float) r1;
        r2 = r2 * r16;
        r3 = 0;
        r4 = (float) r1;
        r5 = 0;
        r6 = r0.paint;
        r1 = r18;
        r1.drawLine(r2, r3, r4, r5, r6);
    L_0x014e:
        r18.restore();
        r1 = r17.getBounds();
        r1 = r1.centerX();
        r2 = r17.getBounds();
        r2 = r2.centerY();
        r3 = r0.rect;
        r4 = r0.side;
        r5 = r1 - r4;
        r5 = (float) r5;
        r6 = r2 - r4;
        r6 = (float) r6;
        r1 = r1 + r4;
        r1 = (float) r1;
        r2 = r2 + r4;
        r2 = (float) r2;
        r3.set(r5, r6, r1, r2);
        r2 = r0.rect;
        r1 = r0.angle;
        r3 = (r1 > r12 ? 1 : (r1 == r12 ? 0 : -1));
        if (r3 >= 0) goto L_0x017b;
    L_0x017a:
        goto L_0x017d;
    L_0x017b:
        r11 = r1 - r12;
    L_0x017d:
        r1 = NUM; // 0x42340000 float:45.0 double:5.487607523E-315;
        r3 = r11 - r1;
        r1 = r0.angle;
        r4 = (r1 > r12 ? 1 : (r1 == r12 ? 0 : -1));
        if (r4 >= 0) goto L_0x0189;
    L_0x0187:
        r4 = r1;
        goto L_0x018b;
    L_0x0189:
        r10 = r10 - r1;
        r4 = r10;
    L_0x018b:
        r5 = 0;
        r6 = r0.paint;
        r1 = r18;
        r1.drawArc(r2, r3, r4, r5, r6);
        r0.lastFrameTime = r8;
        return;
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
