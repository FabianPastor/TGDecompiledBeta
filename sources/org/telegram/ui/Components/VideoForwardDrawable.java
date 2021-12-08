package org.telegram.ui.Components;

import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;

public class VideoForwardDrawable extends Drawable {
    private static final int[] playPath = {10, 7, 26, 16, 10, 25};
    private boolean animating;
    private float animationProgress;
    private Path clippingPath;
    private VideoForwardDrawableDelegate delegate;
    private float enterAnimationProgress;
    private boolean isOneShootAnimation;
    private boolean isRound;
    private long lastAnimationTime;
    private int lastClippingPath;
    private boolean leftSide;
    private Paint paint = new Paint(1);
    private Path path1 = new Path();
    private boolean showing;
    private TextPaint textPaint = new TextPaint(1);
    private long time;
    private String timeStr;

    public interface VideoForwardDrawableDelegate {
        void invalidate();

        void onAnimationEnd();
    }

    public void setTime(long dt) {
        this.time = dt;
        if (dt >= 1000) {
            this.timeStr = LocaleController.formatPluralString("Seconds", (int) (dt / 1000));
        } else {
            this.timeStr = null;
        }
    }

    public VideoForwardDrawable(boolean isRound2) {
        this.isRound = isRound2;
        this.paint.setColor(-1);
        this.textPaint.setColor(-1);
        this.textPaint.setTextSize((float) AndroidUtilities.dp(12.0f));
        this.textPaint.setTextAlign(Paint.Align.CENTER);
        this.path1.reset();
        int a = 0;
        while (true) {
            int[] iArr = playPath;
            if (a < iArr.length / 2) {
                if (a == 0) {
                    this.path1.moveTo((float) AndroidUtilities.dp((float) iArr[a * 2]), (float) AndroidUtilities.dp((float) iArr[(a * 2) + 1]));
                } else {
                    this.path1.lineTo((float) AndroidUtilities.dp((float) iArr[a * 2]), (float) AndroidUtilities.dp((float) iArr[(a * 2) + 1]));
                }
                a++;
            } else {
                this.path1.close();
                return;
            }
        }
    }

    public boolean isAnimating() {
        return this.animating;
    }

    public void startAnimation() {
        this.animating = true;
        this.animationProgress = 0.0f;
        invalidateSelf();
    }

    public void setOneShootAnimation(boolean isOneShootAnimation2) {
        if (this.isOneShootAnimation != isOneShootAnimation2) {
            this.isOneShootAnimation = isOneShootAnimation2;
            this.timeStr = null;
            this.time = 0;
            this.animationProgress = 0.0f;
        }
    }

    public void setLeftSide(boolean value) {
        boolean z = this.leftSide;
        if (z != value || this.animationProgress < 1.0f || !this.isOneShootAnimation) {
            if (z != value) {
                this.time = 0;
                this.timeStr = null;
            }
            this.leftSide = value;
            startAnimation();
        }
    }

    public void setDelegate(VideoForwardDrawableDelegate videoForwardDrawableDelegate) {
        this.delegate = videoForwardDrawableDelegate;
    }

    public void setAlpha(int alpha) {
        this.paint.setAlpha(alpha);
        this.textPaint.setAlpha(alpha);
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.paint.setColorFilter(colorFilter);
    }

    public void setColor(int value) {
        this.paint.setColor(value);
    }

    public int getOpacity() {
        return -2;
    }

    /* JADX WARNING: Removed duplicated region for block: B:102:0x029f  */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x02a2  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void draw(android.graphics.Canvas r15) {
        /*
            r14 = this;
            android.graphics.Rect r0 = r14.getBounds()
            int r1 = r0.left
            int r2 = r0.width()
            int r3 = r14.getIntrinsicWidth()
            int r2 = r2 - r3
            int r2 = r2 / 2
            int r1 = r1 + r2
            int r2 = r0.top
            int r3 = r0.height()
            int r4 = r14.getIntrinsicHeight()
            int r3 = r3 - r4
            int r3 = r3 / 2
            int r2 = r2 + r3
            boolean r3 = r14.leftSide
            r4 = 1098907648(0x41800000, float:16.0)
            if (r3 == 0) goto L_0x0033
            int r3 = r0.width()
            int r3 = r3 / 4
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r3 = r3 - r5
            int r1 = r1 - r3
            goto L_0x003f
        L_0x0033:
            int r3 = r0.width()
            int r3 = r3 / 4
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r3 = r3 + r5
            int r1 = r1 + r3
        L_0x003f:
            r15.save()
            boolean r3 = r14.isRound
            if (r3 == 0) goto L_0x0081
            android.graphics.Path r3 = r14.clippingPath
            if (r3 != 0) goto L_0x0051
            android.graphics.Path r3 = new android.graphics.Path
            r3.<init>()
            r14.clippingPath = r3
        L_0x0051:
            int r3 = r0.left
            int r5 = r0.top
            int r5 = r5 << 8
            int r3 = r3 + r5
            int r5 = r0.bottom
            int r5 = r5 << 16
            int r3 = r3 + r5
            int r5 = r0.right
            int r5 = r5 << 24
            int r3 = r3 + r5
            int r5 = r14.lastClippingPath
            if (r5 == r3) goto L_0x007b
            android.graphics.Path r5 = r14.clippingPath
            r5.reset()
            android.graphics.RectF r5 = org.telegram.messenger.AndroidUtilities.rectTmp
            r5.set(r0)
            android.graphics.Path r5 = r14.clippingPath
            android.graphics.RectF r6 = org.telegram.messenger.AndroidUtilities.rectTmp
            android.graphics.Path$Direction r7 = android.graphics.Path.Direction.CCW
            r5.addOval(r6, r7)
            r14.lastClippingPath = r3
        L_0x007b:
            android.graphics.Path r5 = r14.clippingPath
            r15.clipPath(r5)
            goto L_0x008c
        L_0x0081:
            int r3 = r0.left
            int r5 = r0.top
            int r6 = r0.right
            int r7 = r0.bottom
            r15.clipRect(r3, r5, r6, r7)
        L_0x008c:
            boolean r3 = r14.isOneShootAnimation
            r5 = 1117782016(0x42a00000, float:80.0)
            r6 = 1132396544(0x437var_, float:255.0)
            r7 = 1065353216(0x3var_, float:1.0)
            if (r3 != 0) goto L_0x00ab
            android.graphics.Paint r3 = r14.paint
            float r8 = r14.enterAnimationProgress
            float r8 = r8 * r5
            int r5 = (int) r8
            r3.setAlpha(r5)
            android.text.TextPaint r3 = r14.textPaint
            float r5 = r14.enterAnimationProgress
            float r5 = r5 * r6
            int r5 = (int) r5
            r3.setAlpha(r5)
            goto L_0x00ee
        L_0x00ab:
            float r3 = r14.animationProgress
            r8 = 1060320051(0x3var_, float:0.7)
            r9 = 1050253722(0x3e99999a, float:0.3)
            int r10 = (r3 > r8 ? 1 : (r3 == r8 ? 0 : -1))
            if (r10 > 0) goto L_0x00d4
            android.graphics.Paint r8 = r14.paint
            float r3 = r3 / r9
            float r3 = java.lang.Math.min(r7, r3)
            float r3 = r3 * r5
            int r3 = (int) r3
            r8.setAlpha(r3)
            android.text.TextPaint r3 = r14.textPaint
            float r5 = r14.animationProgress
            float r5 = r5 / r9
            float r5 = java.lang.Math.min(r7, r5)
            float r5 = r5 * r6
            int r5 = (int) r5
            r3.setAlpha(r5)
            goto L_0x00ee
        L_0x00d4:
            android.graphics.Paint r10 = r14.paint
            float r3 = r3 - r8
            float r3 = r3 / r9
            float r3 = r7 - r3
            float r3 = r3 * r5
            int r3 = (int) r3
            r10.setAlpha(r3)
            android.text.TextPaint r3 = r14.textPaint
            float r5 = r14.animationProgress
            float r5 = r5 - r8
            float r5 = r5 / r9
            float r5 = r7 - r5
            float r5 = r5 * r6
            int r5 = (int) r5
            r3.setAlpha(r5)
        L_0x00ee:
            int r3 = r0.width()
            int r5 = r0.height()
            int r3 = java.lang.Math.max(r3, r5)
            int r3 = r3 / 4
            boolean r5 = r14.leftSide
            r8 = -1
            r9 = 1
            if (r5 == 0) goto L_0x0104
            r5 = -1
            goto L_0x0105
        L_0x0104:
            r5 = 1
        L_0x0105:
            int r3 = r3 * r5
            int r3 = r3 + r1
            float r3 = (float) r3
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r4 = r4 + r2
            float r4 = (float) r4
            int r5 = r0.width()
            int r10 = r0.height()
            int r5 = java.lang.Math.max(r5, r10)
            int r5 = r5 / 2
            float r5 = (float) r5
            android.graphics.Paint r10 = r14.paint
            r15.drawCircle(r3, r4, r5, r10)
            r15.restore()
            java.lang.String r3 = r14.timeStr
            if (r3 == 0) goto L_0x014a
            int r4 = r14.getIntrinsicWidth()
            boolean r5 = r14.leftSide
            if (r5 == 0) goto L_0x0133
            goto L_0x0134
        L_0x0133:
            r8 = 1
        L_0x0134:
            int r4 = r4 * r8
            int r4 = r4 + r1
            float r4 = (float) r4
            int r5 = r14.getIntrinsicHeight()
            int r5 = r5 + r2
            r8 = 1097859072(0x41700000, float:15.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r5 = r5 + r8
            float r5 = (float) r5
            android.text.TextPaint r8 = r14.textPaint
            r15.drawText(r3, r4, r5, r8)
        L_0x014a:
            r15.save()
            boolean r3 = r14.leftSide
            if (r3 == 0) goto L_0x015f
            r3 = 1127481344(0x43340000, float:180.0)
            float r4 = (float) r1
            int r5 = r14.getIntrinsicHeight()
            int r5 = r5 / 2
            int r5 = r5 + r2
            float r5 = (float) r5
            r15.rotate(r3, r4, r5)
        L_0x015f:
            float r3 = (float) r1
            float r4 = (float) r2
            r15.translate(r3, r4)
            float r3 = r14.animationProgress
            r4 = 1058642330(0x3var_a, float:0.6)
            r5 = 255(0xff, float:3.57E-43)
            r8 = 1053609165(0x3ecccccd, float:0.4)
            r9 = 1045220557(0x3e4ccccd, float:0.2)
            int r4 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r4 > 0) goto L_0x019f
            int r4 = (r3 > r8 ? 1 : (r3 == r8 ? 0 : -1))
            if (r4 >= 0) goto L_0x0182
            float r3 = r3 * r6
            float r3 = r3 / r9
            int r3 = (int) r3
            int r3 = java.lang.Math.min(r5, r3)
            goto L_0x0189
        L_0x0182:
            float r3 = r3 - r8
            float r3 = r3 / r9
            float r3 = r7 - r3
            float r3 = r3 * r6
            int r3 = (int) r3
        L_0x0189:
            boolean r4 = r14.isOneShootAnimation
            if (r4 != 0) goto L_0x0193
            float r4 = (float) r3
            float r10 = r14.enterAnimationProgress
            float r4 = r4 * r10
            int r3 = (int) r4
        L_0x0193:
            android.graphics.Paint r4 = r14.paint
            r4.setAlpha(r3)
            android.graphics.Path r4 = r14.path1
            android.graphics.Paint r10 = r14.paint
            r15.drawPath(r4, r10)
        L_0x019f:
            r3 = 1099956224(0x41900000, float:18.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r4 = (float) r4
            r10 = 0
            r15.translate(r4, r10)
            float r4 = r14.animationProgress
            int r11 = (r4 > r9 ? 1 : (r4 == r9 ? 0 : -1))
            if (r11 < 0) goto L_0x01e3
            r11 = 1061997773(0x3f4ccccd, float:0.8)
            int r11 = (r4 > r11 ? 1 : (r4 == r11 ? 0 : -1))
            if (r11 > 0) goto L_0x01e3
            float r4 = r4 - r9
            int r11 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1))
            if (r11 >= 0) goto L_0x01c5
            float r11 = r4 * r6
            float r11 = r11 / r9
            int r11 = (int) r11
            int r11 = java.lang.Math.min(r5, r11)
            goto L_0x01cd
        L_0x01c5:
            float r11 = r4 - r8
            float r11 = r11 / r9
            float r11 = r7 - r11
            float r11 = r11 * r6
            int r11 = (int) r11
        L_0x01cd:
            boolean r12 = r14.isOneShootAnimation
            if (r12 != 0) goto L_0x01d7
            float r12 = (float) r11
            float r13 = r14.enterAnimationProgress
            float r12 = r12 * r13
            int r11 = (int) r12
        L_0x01d7:
            android.graphics.Paint r12 = r14.paint
            r12.setAlpha(r11)
            android.graphics.Path r12 = r14.path1
            android.graphics.Paint r13 = r14.paint
            r15.drawPath(r12, r13)
        L_0x01e3:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            r15.translate(r3, r10)
            float r3 = r14.animationProgress
            int r4 = (r3 > r8 ? 1 : (r3 == r8 ? 0 : -1))
            if (r4 < 0) goto L_0x0221
            int r4 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
            if (r4 > 0) goto L_0x0221
            float r3 = r3 - r8
            int r4 = (r3 > r8 ? 1 : (r3 == r8 ? 0 : -1))
            if (r4 >= 0) goto L_0x0203
            float r6 = r6 * r3
            float r6 = r6 / r9
            int r4 = (int) r6
            int r4 = java.lang.Math.min(r5, r4)
            goto L_0x020b
        L_0x0203:
            float r4 = r3 - r8
            float r4 = r4 / r9
            float r4 = r7 - r4
            float r4 = r4 * r6
            int r4 = (int) r4
        L_0x020b:
            boolean r5 = r14.isOneShootAnimation
            if (r5 != 0) goto L_0x0215
            float r5 = (float) r4
            float r6 = r14.enterAnimationProgress
            float r5 = r5 * r6
            int r4 = (int) r5
        L_0x0215:
            android.graphics.Paint r5 = r14.paint
            r5.setAlpha(r4)
            android.graphics.Path r5 = r14.path1
            android.graphics.Paint r6 = r14.paint
            r15.drawPath(r5, r6)
        L_0x0221:
            r15.restore()
            boolean r3 = r14.animating
            if (r3 == 0) goto L_0x02a8
            long r3 = java.lang.System.currentTimeMillis()
            long r5 = r14.lastAnimationTime
            long r5 = r3 - r5
            r8 = 17
            int r11 = (r5 > r8 ? 1 : (r5 == r8 ? 0 : -1))
            if (r11 <= 0) goto L_0x0238
            r5 = 17
        L_0x0238:
            r14.lastAnimationTime = r3
            float r8 = r14.animationProgress
            int r9 = (r8 > r7 ? 1 : (r8 == r7 ? 0 : -1))
            if (r9 >= 0) goto L_0x0273
            float r9 = (float) r5
            r11 = 1145569280(0x44480000, float:800.0)
            float r9 = r9 / r11
            float r8 = r8 + r9
            r14.animationProgress = r8
            boolean r9 = r14.isOneShootAnimation
            if (r9 != 0) goto L_0x0259
            int r8 = (r8 > r7 ? 1 : (r8 == r7 ? 0 : -1))
            if (r8 < 0) goto L_0x0270
            boolean r8 = r14.showing
            if (r8 == 0) goto L_0x0256
            r14.animationProgress = r10
            goto L_0x0270
        L_0x0256:
            r14.animationProgress = r7
            goto L_0x0270
        L_0x0259:
            int r8 = (r8 > r7 ? 1 : (r8 == r7 ? 0 : -1))
            if (r8 < 0) goto L_0x0270
            r14.animationProgress = r10
            r8 = 0
            r14.animating = r8
            r8 = 0
            r14.time = r8
            r8 = 0
            r14.timeStr = r8
            org.telegram.ui.Components.VideoForwardDrawable$VideoForwardDrawableDelegate r8 = r14.delegate
            if (r8 == 0) goto L_0x0270
            r8.onAnimationEnd()
        L_0x0270:
            r14.invalidate()
        L_0x0273:
            boolean r8 = r14.isOneShootAnimation
            if (r8 != 0) goto L_0x02a8
            boolean r8 = r14.showing
            r9 = 1037726734(0x3dda740e, float:0.10666667)
            if (r8 == 0) goto L_0x028b
            float r11 = r14.enterAnimationProgress
            int r12 = (r11 > r7 ? 1 : (r11 == r7 ? 0 : -1))
            if (r12 == 0) goto L_0x028b
            float r11 = r11 + r9
            r14.enterAnimationProgress = r11
            r14.invalidate()
            goto L_0x0299
        L_0x028b:
            if (r8 != 0) goto L_0x0299
            float r8 = r14.enterAnimationProgress
            int r11 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
            if (r11 == 0) goto L_0x0299
            float r8 = r8 - r9
            r14.enterAnimationProgress = r8
            r14.invalidate()
        L_0x0299:
            float r8 = r14.enterAnimationProgress
            int r9 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
            if (r9 >= 0) goto L_0x02a2
            r14.enterAnimationProgress = r10
            goto L_0x02a8
        L_0x02a2:
            int r8 = (r8 > r7 ? 1 : (r8 == r7 ? 0 : -1))
            if (r8 <= 0) goto L_0x02a8
            r14.enterAnimationProgress = r7
        L_0x02a8:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.VideoForwardDrawable.draw(android.graphics.Canvas):void");
    }

    public void setShowing(boolean showing2) {
        this.showing = showing2;
        invalidate();
    }

    private void invalidate() {
        VideoForwardDrawableDelegate videoForwardDrawableDelegate = this.delegate;
        if (videoForwardDrawableDelegate != null) {
            videoForwardDrawableDelegate.invalidate();
        } else {
            invalidateSelf();
        }
    }

    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(32.0f);
    }

    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(32.0f);
    }

    public int getMinimumWidth() {
        return AndroidUtilities.dp(32.0f);
    }

    public int getMinimumHeight() {
        return AndroidUtilities.dp(32.0f);
    }

    public void addTime(long time2) {
        long j = this.time + time2;
        this.time = j;
        this.timeStr = LocaleController.formatPluralString("Seconds", (int) (j / 1000));
    }
}
