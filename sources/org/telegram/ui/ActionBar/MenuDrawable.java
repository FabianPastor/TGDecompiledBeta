package org.telegram.ui.ActionBar;

import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.view.animation.DecelerateInterpolator;
import org.telegram.messenger.AndroidUtilities;

public class MenuDrawable extends Drawable {
    public static int TYPE_DEFAULT = 0;
    public static int TYPE_UDPATE_AVAILABLE = 1;
    public static int TYPE_UDPATE_DOWNLOADING = 2;
    private int alpha;
    private float animatedDownloadProgress;
    private int backColor;
    private Paint backPaint;
    private int currentAnimationTime;
    private float currentRotation;
    private float downloadProgress;
    private float downloadProgressAnimationStart;
    private float downloadProgressTime;
    private float downloadRadOffset;
    private float finalRotation;
    private int iconColor;
    private DecelerateInterpolator interpolator;
    private long lastFrameTime;
    private boolean miniIcon;
    private Paint paint;
    private int previousType;
    private RectF rect;
    private boolean reverseAngle;
    private boolean rotateToBack;
    private int type;
    private float typeAnimationProgress;

    public int getOpacity() {
        return -2;
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    public MenuDrawable() {
        this(TYPE_DEFAULT);
    }

    public MenuDrawable(int i) {
        this.paint = new Paint(1);
        this.backPaint = new Paint(1);
        this.rotateToBack = true;
        this.interpolator = new DecelerateInterpolator();
        this.rect = new RectF();
        this.alpha = 255;
        this.paint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.backPaint.setStrokeWidth(AndroidUtilities.density * 1.66f);
        this.backPaint.setStrokeCap(Paint.Cap.ROUND);
        this.backPaint.setStyle(Paint.Style.STROKE);
        this.previousType = TYPE_DEFAULT;
        this.type = i;
        this.typeAnimationProgress = 1.0f;
    }

    public void setRotateToBack(boolean z) {
        this.rotateToBack = z;
    }

    public void setRotation(float f, boolean z) {
        this.lastFrameTime = 0;
        float f2 = this.currentRotation;
        if (f2 == 1.0f) {
            this.reverseAngle = true;
        } else if (f2 == 0.0f) {
            this.reverseAngle = false;
        }
        this.lastFrameTime = 0;
        if (z) {
            if (f2 < f) {
                this.currentAnimationTime = (int) (f2 * 200.0f);
            } else {
                this.currentAnimationTime = (int) ((1.0f - f2) * 200.0f);
            }
            this.lastFrameTime = SystemClock.elapsedRealtime();
            this.finalRotation = f;
        } else {
            this.currentRotation = f;
            this.finalRotation = f;
        }
        invalidateSelf();
    }

    public void setType(int i, boolean z) {
        int i2 = this.type;
        if (i2 != i) {
            this.previousType = i2;
            this.type = i;
            if (z) {
                this.typeAnimationProgress = 0.0f;
            } else {
                this.typeAnimationProgress = 1.0f;
            }
            invalidateSelf();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:37:0x0101  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x019b  */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x02fa  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x0319  */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x036d  */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x03a0  */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x03b0  */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x03ed  */
    /* JADX WARNING: Removed duplicated region for block: B:83:0x03fc  */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x0458  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void draw(android.graphics.Canvas r26) {
        /*
            r25 = this;
            r0 = r25
            r7 = r26
            long r1 = android.os.SystemClock.elapsedRealtime()
            long r3 = r0.lastFrameTime
            long r8 = r1 - r3
            float r5 = r0.currentRotation
            float r6 = r0.finalRotation
            r10 = 1128792064(0x43480000, float:200.0)
            r11 = 1065353216(0x3var_, float:1.0)
            int r12 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
            if (r12 == 0) goto L_0x004e
            r12 = 0
            int r14 = (r3 > r12 ? 1 : (r3 == r12 ? 0 : -1))
            if (r14 == 0) goto L_0x004b
            int r3 = r0.currentAnimationTime
            long r3 = (long) r3
            long r3 = r3 + r8
            int r4 = (int) r3
            r0.currentAnimationTime = r4
            r3 = 200(0xc8, float:2.8E-43)
            if (r4 < r3) goto L_0x002c
            r0.currentRotation = r6
            goto L_0x004b
        L_0x002c:
            int r3 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
            if (r3 >= 0) goto L_0x003f
            android.view.animation.DecelerateInterpolator r3 = r0.interpolator
            float r4 = (float) r4
            float r4 = r4 / r10
            float r3 = r3.getInterpolation(r4)
            float r4 = r0.finalRotation
            float r3 = r3 * r4
            r0.currentRotation = r3
            goto L_0x004b
        L_0x003f:
            android.view.animation.DecelerateInterpolator r3 = r0.interpolator
            float r4 = (float) r4
            float r4 = r4 / r10
            float r3 = r3.getInterpolation(r4)
            float r3 = r11 - r3
            r0.currentRotation = r3
        L_0x004b:
            r25.invalidateSelf()
        L_0x004e:
            float r3 = r0.typeAnimationProgress
            int r4 = (r3 > r11 ? 1 : (r3 == r11 ? 0 : -1))
            if (r4 >= 0) goto L_0x0062
            float r4 = (float) r8
            float r4 = r4 / r10
            float r3 = r3 + r4
            r0.typeAnimationProgress = r3
            int r3 = (r3 > r11 ? 1 : (r3 == r11 ? 0 : -1))
            if (r3 <= 0) goto L_0x005f
            r0.typeAnimationProgress = r11
        L_0x005f:
            r25.invalidateSelf()
        L_0x0062:
            r0.lastFrameTime = r1
            r26.save()
            int r1 = r25.getIntrinsicWidth()
            int r1 = r1 / 2
            r12 = 1091567616(0x41100000, float:9.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r1 = r1 - r2
            float r1 = (float) r1
            int r2 = r25.getIntrinsicHeight()
            int r2 = r2 / 2
            float r2 = (float) r2
            r7.translate(r1, r2)
            int r1 = r0.iconColor
            if (r1 != 0) goto L_0x0089
            java.lang.String r1 = "actionBarDefaultIcon"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
        L_0x0089:
            int r2 = r0.backColor
            if (r2 != 0) goto L_0x0093
            java.lang.String r2 = "actionBarDefault"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
        L_0x0093:
            r13 = r2
            int r2 = r0.type
            int r3 = TYPE_DEFAULT
            r14 = 1088421888(0x40e00000, float:7.0)
            r15 = 0
            if (r2 != r3) goto L_0x00b8
            int r2 = r0.previousType
            if (r2 == r3) goto L_0x00b4
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r2 = (float) r2
            float r3 = r0.typeAnimationProgress
            float r3 = r11 - r3
            float r2 = r2 * r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r3 = (float) r3
            float r4 = r0.typeAnimationProgress
            goto L_0x00e9
        L_0x00b4:
            r3 = 0
            r16 = 0
            goto L_0x00ef
        L_0x00b8:
            int r2 = r0.previousType
            if (r2 != r3) goto L_0x00d7
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r2 = (float) r2
            float r3 = r0.typeAnimationProgress
            float r2 = r2 * r3
            float r3 = r0.currentRotation
            float r3 = r11 - r3
            float r2 = r2 * r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r3 = (float) r3
            float r4 = r0.typeAnimationProgress
            float r3 = r3 * r4
            float r4 = r0.currentRotation
            goto L_0x00e9
        L_0x00d7:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r2 = (float) r2
            float r3 = r0.currentRotation
            float r3 = r11 - r3
            float r2 = r2 * r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r3 = (float) r3
            float r4 = r0.currentRotation
        L_0x00e9:
            float r4 = r11 - r4
            float r3 = r3 * r4
            r16 = r2
        L_0x00ef:
            boolean r2 = r0.rotateToBack
            r17 = 1099431936(0x41880000, float:17.0)
            r18 = 1075838976(0x40200000, float:2.5)
            r19 = 1056964608(0x3var_, float:0.5)
            r20 = 1099956224(0x41900000, float:18.0)
            r21 = 1073741824(0x40000000, float:2.0)
            r22 = 1084227584(0x40a00000, float:5.0)
            r23 = 1077936128(0x40400000, float:3.0)
            if (r2 == 0) goto L_0x019b
            float r2 = r0.currentRotation
            boolean r4 = r0.reverseAngle
            if (r4 == 0) goto L_0x010a
            r4 = -180(0xffffffffffffff4c, float:NaN)
            goto L_0x010c
        L_0x010a:
            r4 = 180(0xb4, float:2.52E-43)
        L_0x010c:
            float r4 = (float) r4
            float r2 = r2 * r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r4 = (float) r4
            r7.rotate(r2, r4, r15)
            android.graphics.Paint r2 = r0.paint
            r2.setColor(r1)
            android.graphics.Paint r1 = r0.paint
            int r2 = r0.alpha
            r1.setAlpha(r2)
            r2 = 0
            r4 = 0
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r20)
            float r1 = (float) r1
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r23)
            float r5 = (float) r5
            float r6 = r0.currentRotation
            float r5 = r5 * r6
            float r1 = r1 - r5
            float r5 = r1 - r3
            r6 = 0
            android.graphics.Paint r12 = r0.paint
            r1 = r26
            r3 = r4
            r4 = r5
            r5 = r6
            r6 = r12
            r1.drawLine(r2, r3, r4, r5, r6)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
            float r1 = (float) r1
            float r2 = r0.currentRotation
            float r2 = java.lang.Math.abs(r2)
            float r2 = r11 - r2
            float r1 = r1 * r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r19)
            float r2 = (float) r2
            float r3 = r0.currentRotation
            float r3 = java.lang.Math.abs(r3)
            float r2 = r2 * r3
            float r1 = r1 - r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r20)
            float r2 = (float) r2
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r3 = (float) r3
            float r4 = r0.currentRotation
            float r4 = java.lang.Math.abs(r4)
            float r3 = r3 * r4
            float r2 = r2 - r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r22)
            float r3 = (float) r3
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r4 = (float) r4
            float r5 = r0.currentRotation
            float r5 = java.lang.Math.abs(r5)
            float r4 = r4 * r5
            float r3 = r3 + r4
            r4 = 1089470464(0x40var_, float:7.5)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            float r5 = r0.currentRotation
            float r5 = java.lang.Math.abs(r5)
        L_0x0192:
            float r4 = r4 * r5
        L_0x0194:
            r6 = r1
            r14 = r2
            r12 = r4
            r5 = r13
            r13 = r3
            goto L_0x02f6
        L_0x019b:
            float r2 = r0.currentRotation
            boolean r4 = r0.reverseAngle
            if (r4 == 0) goto L_0x01a4
            r4 = -225(0xffffffffffffff1f, float:NaN)
            goto L_0x01a6
        L_0x01a4:
            r4 = 135(0x87, float:1.89E-43)
        L_0x01a6:
            float r4 = (float) r4
            float r2 = r2 * r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r4 = (float) r4
            r7.rotate(r2, r4, r15)
            boolean r2 = r0.miniIcon
            if (r2 == 0) goto L_0x025b
            android.graphics.Paint r2 = r0.paint
            r2.setColor(r1)
            android.graphics.Paint r1 = r0.paint
            int r2 = r0.alpha
            r1.setAlpha(r2)
            float r1 = org.telegram.messenger.AndroidUtilities.dpf2(r21)
            float r2 = r0.currentRotation
            float r2 = java.lang.Math.abs(r2)
            float r2 = r11 - r2
            float r1 = r1 * r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r2 = (float) r2
            float r4 = r0.currentRotation
            float r2 = r2 * r4
            float r2 = r2 + r1
            r4 = 0
            r20 = 1098907648(0x41800000, float:16.0)
            float r1 = org.telegram.messenger.AndroidUtilities.dpf2(r20)
            float r5 = r0.currentRotation
            float r5 = r11 - r5
            float r1 = r1 * r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r5 = (float) r5
            float r6 = r0.currentRotation
            float r5 = r5 * r6
            float r1 = r1 + r5
            float r5 = r1 - r3
            r6 = 0
            android.graphics.Paint r3 = r0.paint
            r1 = r26
            r24 = r3
            r3 = r4
            r4 = r5
            r5 = r6
            r6 = r24
            r1.drawLine(r2, r3, r4, r5, r6)
            float r1 = org.telegram.messenger.AndroidUtilities.dpf2(r22)
            float r2 = r0.currentRotation
            float r2 = java.lang.Math.abs(r2)
            float r2 = r11 - r2
            float r1 = r1 * r2
            float r2 = org.telegram.messenger.AndroidUtilities.dpf2(r19)
            float r3 = r0.currentRotation
            float r3 = java.lang.Math.abs(r3)
            float r2 = r2 * r3
            float r1 = r1 - r2
            float r2 = org.telegram.messenger.AndroidUtilities.dpf2(r20)
            float r3 = r0.currentRotation
            float r3 = java.lang.Math.abs(r3)
            float r3 = r11 - r3
            float r2 = r2 * r3
            float r3 = org.telegram.messenger.AndroidUtilities.dpf2(r12)
            float r4 = r0.currentRotation
            float r4 = java.lang.Math.abs(r4)
            float r3 = r3 * r4
            float r2 = r2 + r3
            float r3 = org.telegram.messenger.AndroidUtilities.dpf2(r22)
            float r4 = org.telegram.messenger.AndroidUtilities.dpf2(r23)
            float r5 = r0.currentRotation
            float r5 = java.lang.Math.abs(r5)
            float r4 = r4 * r5
            float r3 = r3 + r4
            float r4 = org.telegram.messenger.AndroidUtilities.dpf2(r21)
            float r5 = org.telegram.messenger.AndroidUtilities.dpf2(r14)
            float r6 = r0.currentRotation
            float r6 = java.lang.Math.abs(r6)
            float r5 = r5 * r6
            float r4 = r4 + r5
            goto L_0x0194
        L_0x025b:
            java.lang.String r2 = "actionBarActionModeDefaultIcon"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            java.lang.String r4 = "actionBarActionModeDefault"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            float r5 = r0.currentRotation
            int r13 = org.telegram.messenger.AndroidUtilities.getOffsetColor(r13, r4, r5, r11)
            android.graphics.Paint r4 = r0.paint
            float r5 = r0.currentRotation
            int r1 = org.telegram.messenger.AndroidUtilities.getOffsetColor(r1, r2, r5, r11)
            r4.setColor(r1)
            android.graphics.Paint r1 = r0.paint
            int r2 = r0.alpha
            r1.setAlpha(r2)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r1 = (float) r1
            float r2 = r0.currentRotation
            float r2 = r2 * r1
            r4 = 0
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r20)
            float r1 = (float) r1
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r5 = (float) r5
            float r6 = r0.currentRotation
            float r5 = r5 * r6
            float r1 = r1 - r5
            float r5 = r1 - r3
            r6 = 0
            android.graphics.Paint r14 = r0.paint
            r1 = r26
            r3 = r4
            r4 = r5
            r5 = r6
            r6 = r14
            r1.drawLine(r2, r3, r4, r5, r6)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
            float r1 = (float) r1
            float r2 = r0.currentRotation
            float r2 = java.lang.Math.abs(r2)
            float r2 = r11 - r2
            float r1 = r1 * r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r19)
            float r2 = (float) r2
            float r3 = r0.currentRotation
            float r3 = java.lang.Math.abs(r3)
            float r2 = r2 * r3
            float r1 = r1 - r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r20)
            float r2 = (float) r2
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r3 = (float) r3
            float r4 = r0.currentRotation
            float r4 = java.lang.Math.abs(r4)
            float r3 = r3 * r4
            float r2 = r2 - r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r22)
            float r3 = (float) r3
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r23)
            float r4 = (float) r4
            float r5 = r0.currentRotation
            float r5 = java.lang.Math.abs(r5)
            float r4 = r4 * r5
            float r3 = r3 + r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r4 = (float) r4
            float r5 = r0.currentRotation
            float r5 = java.lang.Math.abs(r5)
            goto L_0x0192
        L_0x02f6:
            boolean r1 = r0.miniIcon
            if (r1 == 0) goto L_0x0319
            float r3 = -r13
            float r4 = -r6
            android.graphics.Paint r2 = r0.paint
            r1 = r26
            r16 = r2
            r2 = r12
            r19 = r4
            r4 = r14
            r10 = r5
            r5 = r19
            r19 = r6
            r6 = r16
            r1.drawLine(r2, r3, r4, r5, r6)
            android.graphics.Paint r6 = r0.paint
            r3 = r13
            r5 = r19
            r1.drawLine(r2, r3, r4, r5, r6)
            goto L_0x0335
        L_0x0319:
            r10 = r5
            r19 = r6
            float r3 = -r13
            float r4 = r14 - r16
            float r5 = -r6
            android.graphics.Paint r2 = r0.paint
            r1 = r26
            r16 = r2
            r2 = r12
            r6 = r16
            r1.drawLine(r2, r3, r4, r5, r6)
            android.graphics.Paint r6 = r0.paint
            r3 = r13
            r4 = r14
            r5 = r19
            r1.drawLine(r2, r3, r4, r5, r6)
        L_0x0335:
            int r1 = r0.type
            int r2 = TYPE_DEFAULT
            if (r1 == r2) goto L_0x0341
            float r1 = r0.currentRotation
            int r1 = (r1 > r11 ? 1 : (r1 == r11 ? 0 : -1))
            if (r1 != 0) goto L_0x034b
        L_0x0341:
            int r1 = r0.previousType
            if (r1 == r2) goto L_0x047a
            float r1 = r0.typeAnimationProgress
            int r1 = (r1 > r11 ? 1 : (r1 == r11 ? 0 : -1))
            if (r1 == 0) goto L_0x047a
        L_0x034b:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r12 = (float) r1
            r1 = 1083179008(0x40900000, float:4.5)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = -r1
            float r13 = (float) r1
            float r1 = org.telegram.messenger.AndroidUtilities.density
            r2 = 1085276160(0x40b00000, float:5.5)
            float r1 = r1 * r2
            float r2 = r0.currentRotation
            float r3 = r11 - r2
            float r2 = r11 - r2
            r7.scale(r3, r2, r12, r13)
            int r2 = r0.type
            int r3 = TYPE_DEFAULT
            if (r2 != r3) goto L_0x0373
            float r2 = r0.typeAnimationProgress
            float r2 = r11 - r2
            float r1 = r1 * r2
        L_0x0373:
            android.graphics.Paint r2 = r0.backPaint
            r2.setColor(r10)
            android.graphics.Paint r2 = r0.backPaint
            int r3 = r0.alpha
            r2.setAlpha(r3)
            android.graphics.Paint r2 = r0.paint
            r7.drawCircle(r12, r13, r1, r2)
            int r1 = r0.type
            int r2 = TYPE_UDPATE_AVAILABLE
            if (r1 == r2) goto L_0x038e
            int r1 = r0.previousType
            if (r1 != r2) goto L_0x03d3
        L_0x038e:
            android.graphics.Paint r1 = r0.backPaint
            float r2 = org.telegram.messenger.AndroidUtilities.density
            r3 = 1070889697(0x3fd47ae1, float:1.66)
            float r2 = r2 * r3
            r1.setStrokeWidth(r2)
            int r1 = r0.previousType
            int r2 = TYPE_UDPATE_AVAILABLE
            if (r1 != r2) goto L_0x03b0
            android.graphics.Paint r1 = r0.backPaint
            int r2 = r0.alpha
            float r2 = (float) r2
            float r3 = r0.typeAnimationProgress
            float r3 = r11 - r3
            float r2 = r2 * r3
            int r2 = (int) r2
            r1.setAlpha(r2)
            goto L_0x03b7
        L_0x03b0:
            android.graphics.Paint r1 = r0.backPaint
            int r2 = r0.alpha
            r1.setAlpha(r2)
        L_0x03b7:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r1 = (float) r1
            float r3 = r13 - r1
            android.graphics.Paint r6 = r0.backPaint
            r1 = r26
            r2 = r12
            r4 = r12
            r5 = r13
            r1.drawLine(r2, r3, r4, r5, r6)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r1 = (float) r1
            float r1 = r1 + r13
            android.graphics.Paint r2 = r0.backPaint
            r7.drawPoint(r12, r1, r2)
        L_0x03d3:
            int r1 = r0.type
            int r2 = TYPE_UDPATE_DOWNLOADING
            if (r1 == r2) goto L_0x03dd
            int r1 = r0.previousType
            if (r1 != r2) goto L_0x047a
        L_0x03dd:
            android.graphics.Paint r1 = r0.backPaint
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r2 = (float) r2
            r1.setStrokeWidth(r2)
            int r1 = r0.previousType
            int r2 = TYPE_UDPATE_DOWNLOADING
            if (r1 != r2) goto L_0x03fc
            android.graphics.Paint r1 = r0.backPaint
            int r2 = r0.alpha
            float r2 = (float) r2
            float r3 = r0.typeAnimationProgress
            float r11 = r11 - r3
            float r2 = r2 * r11
            int r2 = (int) r2
            r1.setAlpha(r2)
            goto L_0x0403
        L_0x03fc:
            android.graphics.Paint r1 = r0.backPaint
            int r2 = r0.alpha
            r1.setAlpha(r2)
        L_0x0403:
            r1 = 1082130432(0x40800000, float:4.0)
            r2 = 1135869952(0x43b40000, float:360.0)
            float r3 = r0.animatedDownloadProgress
            float r3 = r3 * r2
            float r4 = java.lang.Math.max(r1, r3)
            android.graphics.RectF r1 = r0.rect
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r23)
            float r2 = (float) r2
            float r2 = r12 - r2
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r23)
            float r3 = (float) r3
            float r3 = r13 - r3
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r23)
            float r5 = (float) r5
            float r12 = r12 + r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r23)
            float r5 = (float) r5
            float r13 = r13 + r5
            r1.set(r2, r3, r12, r13)
            android.graphics.RectF r2 = r0.rect
            float r3 = r0.downloadRadOffset
            r5 = 0
            android.graphics.Paint r6 = r0.backPaint
            r1 = r26
            r1.drawArc(r2, r3, r4, r5, r6)
            float r1 = r0.downloadRadOffset
            r2 = 360(0x168, double:1.78E-321)
            long r2 = r2 * r8
            float r2 = (float) r2
            r3 = 1159479296(0x451CLASSNAME, float:2500.0)
            float r2 = r2 / r3
            float r1 = r1 + r2
            r0.downloadRadOffset = r1
            float r1 = org.telegram.ui.Components.MediaActionDrawable.getCircleValue(r1)
            r0.downloadRadOffset = r1
            float r1 = r0.downloadProgress
            float r2 = r0.downloadProgressAnimationStart
            float r3 = r1 - r2
            int r4 = (r3 > r15 ? 1 : (r3 == r15 ? 0 : -1))
            if (r4 <= 0) goto L_0x0477
            float r4 = r0.downloadProgressTime
            float r5 = (float) r8
            float r4 = r4 + r5
            r0.downloadProgressTime = r4
            r5 = 1128792064(0x43480000, float:200.0)
            int r6 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            if (r6 < 0) goto L_0x046b
            r0.animatedDownloadProgress = r1
            r0.downloadProgressAnimationStart = r1
            r0.downloadProgressTime = r15
            goto L_0x0477
        L_0x046b:
            android.view.animation.DecelerateInterpolator r1 = r0.interpolator
            float r4 = r4 / r5
            float r1 = r1.getInterpolation(r4)
            float r3 = r3 * r1
            float r2 = r2 + r3
            r0.animatedDownloadProgress = r2
        L_0x0477:
            r25.invalidateSelf()
        L_0x047a:
            r26.restore()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.MenuDrawable.draw(android.graphics.Canvas):void");
    }

    public void setUpdateDownloadProgress(float f, boolean z) {
        if (!z) {
            this.animatedDownloadProgress = f;
            this.downloadProgressAnimationStart = f;
        } else {
            if (this.animatedDownloadProgress > f) {
                this.animatedDownloadProgress = f;
            }
            this.downloadProgressAnimationStart = this.animatedDownloadProgress;
        }
        this.downloadProgress = f;
        this.downloadProgressTime = 0.0f;
        invalidateSelf();
    }

    public void setAlpha(int i) {
        if (this.alpha != i) {
            this.alpha = i;
            this.paint.setAlpha(i);
            this.backPaint.setAlpha(i);
            invalidateSelf();
        }
    }

    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(24.0f);
    }

    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(24.0f);
    }

    public void setIconColor(int i) {
        this.iconColor = i;
    }

    public void setBackColor(int i) {
        this.backColor = i;
    }

    public void setRoundCap() {
        this.paint.setStrokeCap(Paint.Cap.ROUND);
    }

    public void setMiniIcon(boolean z) {
        this.miniIcon = z;
    }
}
