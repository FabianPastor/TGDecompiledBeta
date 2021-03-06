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
    private VideoForwardDrawableDelegate delegate;
    private float enterAnimationProgress;
    private boolean isOneShootAnimation;
    private long lastAnimationTime;
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

    public int getOpacity() {
        return -2;
    }

    public void setTime(long j) {
        this.time = j;
        if (j >= 1000) {
            this.timeStr = LocaleController.formatPluralString("Seconds", (int) (j / 1000));
        } else {
            this.timeStr = null;
        }
    }

    public VideoForwardDrawable() {
        this.paint.setColor(-1);
        this.textPaint.setColor(-1);
        this.textPaint.setTextSize((float) AndroidUtilities.dp(12.0f));
        this.textPaint.setTextAlign(Paint.Align.CENTER);
        this.path1.reset();
        int i = 0;
        while (true) {
            int[] iArr = playPath;
            if (i < iArr.length / 2) {
                if (i == 0) {
                    int i2 = i * 2;
                    this.path1.moveTo((float) AndroidUtilities.dp((float) iArr[i2]), (float) AndroidUtilities.dp((float) iArr[i2 + 1]));
                } else {
                    int i3 = i * 2;
                    this.path1.lineTo((float) AndroidUtilities.dp((float) iArr[i3]), (float) AndroidUtilities.dp((float) iArr[i3 + 1]));
                }
                i++;
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

    public void setOneShootAnimation(boolean z) {
        if (this.isOneShootAnimation != z) {
            this.isOneShootAnimation = z;
            this.timeStr = null;
            this.time = 0;
            this.animationProgress = 0.0f;
        }
    }

    public void setLeftSide(boolean z) {
        boolean z2 = this.leftSide;
        if (z2 != z || this.animationProgress < 1.0f || !this.isOneShootAnimation) {
            if (z2 != z) {
                this.time = 0;
                this.timeStr = null;
            }
            this.leftSide = z;
            startAnimation();
        }
    }

    public void setDelegate(VideoForwardDrawableDelegate videoForwardDrawableDelegate) {
        this.delegate = videoForwardDrawableDelegate;
    }

    public void setAlpha(int i) {
        this.paint.setAlpha(i);
        this.textPaint.setAlpha(i);
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.paint.setColorFilter(colorFilter);
    }

    /* JADX WARNING: Removed duplicated region for block: B:92:0x025d  */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x0260  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void draw(android.graphics.Canvas r12) {
        /*
            r11 = this;
            android.graphics.Rect r0 = r11.getBounds()
            int r1 = r0.left
            int r2 = r0.width()
            int r3 = r11.getIntrinsicWidth()
            int r2 = r2 - r3
            int r2 = r2 / 2
            int r1 = r1 + r2
            int r2 = r0.top
            int r3 = r0.height()
            int r4 = r11.getIntrinsicHeight()
            int r3 = r3 - r4
            int r3 = r3 / 2
            int r2 = r2 + r3
            boolean r3 = r11.leftSide
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
            r12.save()
            int r3 = r0.left
            int r5 = r0.top
            int r6 = r0.right
            int r7 = r0.bottom
            r12.clipRect(r3, r5, r6, r7)
            boolean r3 = r11.isOneShootAnimation
            r5 = 1117782016(0x42a00000, float:80.0)
            r6 = 1132396544(0x437var_, float:255.0)
            r7 = 1065353216(0x3var_, float:1.0)
            if (r3 != 0) goto L_0x006c
            android.graphics.Paint r3 = r11.paint
            float r8 = r11.enterAnimationProgress
            float r8 = r8 * r5
            int r5 = (int) r8
            r3.setAlpha(r5)
            android.text.TextPaint r3 = r11.textPaint
            float r5 = r11.enterAnimationProgress
            float r5 = r5 * r6
            int r5 = (int) r5
            r3.setAlpha(r5)
            goto L_0x00af
        L_0x006c:
            float r3 = r11.animationProgress
            r8 = 1060320051(0x3var_, float:0.7)
            r9 = 1050253722(0x3e99999a, float:0.3)
            int r10 = (r3 > r8 ? 1 : (r3 == r8 ? 0 : -1))
            if (r10 > 0) goto L_0x0095
            android.graphics.Paint r8 = r11.paint
            float r3 = r3 / r9
            float r3 = java.lang.Math.min(r7, r3)
            float r3 = r3 * r5
            int r3 = (int) r3
            r8.setAlpha(r3)
            android.text.TextPaint r3 = r11.textPaint
            float r5 = r11.animationProgress
            float r5 = r5 / r9
            float r5 = java.lang.Math.min(r7, r5)
            float r5 = r5 * r6
            int r5 = (int) r5
            r3.setAlpha(r5)
            goto L_0x00af
        L_0x0095:
            android.graphics.Paint r10 = r11.paint
            float r3 = r3 - r8
            float r3 = r3 / r9
            float r3 = r7 - r3
            float r3 = r3 * r5
            int r3 = (int) r3
            r10.setAlpha(r3)
            android.text.TextPaint r3 = r11.textPaint
            float r5 = r11.animationProgress
            float r5 = r5 - r8
            float r5 = r5 / r9
            float r5 = r7 - r5
            float r5 = r5 * r6
            int r5 = (int) r5
            r3.setAlpha(r5)
        L_0x00af:
            int r3 = r0.width()
            int r5 = r0.height()
            int r3 = java.lang.Math.max(r3, r5)
            int r3 = r3 / 4
            boolean r5 = r11.leftSide
            r8 = -1
            r9 = 1
            if (r5 == 0) goto L_0x00c5
            r5 = -1
            goto L_0x00c6
        L_0x00c5:
            r5 = 1
        L_0x00c6:
            int r3 = r3 * r5
            int r3 = r3 + r1
            float r3 = (float) r3
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r4 = r4 + r2
            float r4 = (float) r4
            int r5 = r0.width()
            int r0 = r0.height()
            int r0 = java.lang.Math.max(r5, r0)
            int r0 = r0 / 2
            float r0 = (float) r0
            android.graphics.Paint r5 = r11.paint
            r12.drawCircle(r3, r4, r0, r5)
            r12.restore()
            java.lang.String r0 = r11.timeStr
            if (r0 == 0) goto L_0x010b
            int r3 = r11.getIntrinsicWidth()
            boolean r4 = r11.leftSide
            if (r4 == 0) goto L_0x00f4
            goto L_0x00f5
        L_0x00f4:
            r8 = 1
        L_0x00f5:
            int r3 = r3 * r8
            int r3 = r3 + r1
            float r3 = (float) r3
            int r4 = r11.getIntrinsicHeight()
            int r4 = r4 + r2
            r5 = 1097859072(0x41700000, float:15.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r4 = r4 + r5
            float r4 = (float) r4
            android.text.TextPaint r5 = r11.textPaint
            r12.drawText(r0, r3, r4, r5)
        L_0x010b:
            r12.save()
            boolean r0 = r11.leftSide
            if (r0 == 0) goto L_0x0120
            r0 = 1127481344(0x43340000, float:180.0)
            float r3 = (float) r1
            int r4 = r11.getIntrinsicHeight()
            int r4 = r4 / 2
            int r4 = r4 + r2
            float r4 = (float) r4
            r12.rotate(r0, r3, r4)
        L_0x0120:
            float r0 = (float) r1
            float r1 = (float) r2
            r12.translate(r0, r1)
            float r0 = r11.animationProgress
            r1 = 1058642330(0x3var_a, float:0.6)
            r2 = 255(0xff, float:3.57E-43)
            r3 = 1053609165(0x3ecccccd, float:0.4)
            r4 = 1045220557(0x3e4ccccd, float:0.2)
            int r1 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r1 > 0) goto L_0x0160
            int r1 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r1 >= 0) goto L_0x0143
            float r0 = r0 * r6
            float r0 = r0 / r4
            int r0 = (int) r0
            int r0 = java.lang.Math.min(r2, r0)
            goto L_0x014a
        L_0x0143:
            float r0 = r0 - r3
            float r0 = r0 / r4
            float r0 = r7 - r0
            float r0 = r0 * r6
            int r0 = (int) r0
        L_0x014a:
            boolean r1 = r11.isOneShootAnimation
            if (r1 != 0) goto L_0x0154
            float r0 = (float) r0
            float r1 = r11.enterAnimationProgress
            float r0 = r0 * r1
            int r0 = (int) r0
        L_0x0154:
            android.graphics.Paint r1 = r11.paint
            r1.setAlpha(r0)
            android.graphics.Path r0 = r11.path1
            android.graphics.Paint r1 = r11.paint
            r12.drawPath(r0, r1)
        L_0x0160:
            r0 = 1099956224(0x41900000, float:18.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r0)
            float r1 = (float) r1
            r5 = 0
            r12.translate(r1, r5)
            float r1 = r11.animationProgress
            int r8 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r8 < 0) goto L_0x01a3
            r8 = 1061997773(0x3f4ccccd, float:0.8)
            int r8 = (r1 > r8 ? 1 : (r1 == r8 ? 0 : -1))
            if (r8 > 0) goto L_0x01a3
            float r1 = r1 - r4
            int r8 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r8 >= 0) goto L_0x0186
            float r1 = r1 * r6
            float r1 = r1 / r4
            int r1 = (int) r1
            int r1 = java.lang.Math.min(r2, r1)
            goto L_0x018d
        L_0x0186:
            float r1 = r1 - r3
            float r1 = r1 / r4
            float r1 = r7 - r1
            float r1 = r1 * r6
            int r1 = (int) r1
        L_0x018d:
            boolean r8 = r11.isOneShootAnimation
            if (r8 != 0) goto L_0x0197
            float r1 = (float) r1
            float r8 = r11.enterAnimationProgress
            float r1 = r1 * r8
            int r1 = (int) r1
        L_0x0197:
            android.graphics.Paint r8 = r11.paint
            r8.setAlpha(r1)
            android.graphics.Path r1 = r11.path1
            android.graphics.Paint r8 = r11.paint
            r12.drawPath(r1, r8)
        L_0x01a3:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            float r0 = (float) r0
            r12.translate(r0, r5)
            float r0 = r11.animationProgress
            int r1 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r1 < 0) goto L_0x01e0
            int r1 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1))
            if (r1 > 0) goto L_0x01e0
            float r0 = r0 - r3
            int r1 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r1 >= 0) goto L_0x01c3
            float r0 = r0 * r6
            float r0 = r0 / r4
            int r0 = (int) r0
            int r0 = java.lang.Math.min(r2, r0)
            goto L_0x01ca
        L_0x01c3:
            float r0 = r0 - r3
            float r0 = r0 / r4
            float r0 = r7 - r0
            float r0 = r0 * r6
            int r0 = (int) r0
        L_0x01ca:
            boolean r1 = r11.isOneShootAnimation
            if (r1 != 0) goto L_0x01d4
            float r0 = (float) r0
            float r1 = r11.enterAnimationProgress
            float r0 = r0 * r1
            int r0 = (int) r0
        L_0x01d4:
            android.graphics.Paint r1 = r11.paint
            r1.setAlpha(r0)
            android.graphics.Path r0 = r11.path1
            android.graphics.Paint r1 = r11.paint
            r12.drawPath(r0, r1)
        L_0x01e0:
            r12.restore()
            boolean r12 = r11.animating
            if (r12 == 0) goto L_0x0266
            long r0 = java.lang.System.currentTimeMillis()
            long r2 = r11.lastAnimationTime
            long r2 = r0 - r2
            r8 = 17
            int r12 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1))
            if (r12 <= 0) goto L_0x01f6
            r2 = r8
        L_0x01f6:
            r11.lastAnimationTime = r0
            float r12 = r11.animationProgress
            int r0 = (r12 > r7 ? 1 : (r12 == r7 ? 0 : -1))
            if (r0 >= 0) goto L_0x0231
            float r0 = (float) r2
            r1 = 1145569280(0x44480000, float:800.0)
            float r0 = r0 / r1
            float r12 = r12 + r0
            r11.animationProgress = r12
            boolean r0 = r11.isOneShootAnimation
            if (r0 != 0) goto L_0x0217
            int r12 = (r12 > r7 ? 1 : (r12 == r7 ? 0 : -1))
            if (r12 < 0) goto L_0x022e
            boolean r12 = r11.showing
            if (r12 == 0) goto L_0x0214
            r11.animationProgress = r5
            goto L_0x022e
        L_0x0214:
            r11.animationProgress = r7
            goto L_0x022e
        L_0x0217:
            int r12 = (r12 > r7 ? 1 : (r12 == r7 ? 0 : -1))
            if (r12 < 0) goto L_0x022e
            r11.animationProgress = r5
            r12 = 0
            r11.animating = r12
            r0 = 0
            r11.time = r0
            r12 = 0
            r11.timeStr = r12
            org.telegram.ui.Components.VideoForwardDrawable$VideoForwardDrawableDelegate r12 = r11.delegate
            if (r12 == 0) goto L_0x022e
            r12.onAnimationEnd()
        L_0x022e:
            r11.invalidate()
        L_0x0231:
            boolean r12 = r11.isOneShootAnimation
            if (r12 != 0) goto L_0x0266
            boolean r12 = r11.showing
            r0 = 1037726734(0x3dda740e, float:0.10666667)
            if (r12 == 0) goto L_0x0249
            float r1 = r11.enterAnimationProgress
            int r2 = (r1 > r7 ? 1 : (r1 == r7 ? 0 : -1))
            if (r2 == 0) goto L_0x0249
            float r1 = r1 + r0
            r11.enterAnimationProgress = r1
            r11.invalidate()
            goto L_0x0257
        L_0x0249:
            if (r12 != 0) goto L_0x0257
            float r12 = r11.enterAnimationProgress
            int r1 = (r12 > r5 ? 1 : (r12 == r5 ? 0 : -1))
            if (r1 == 0) goto L_0x0257
            float r12 = r12 - r0
            r11.enterAnimationProgress = r12
            r11.invalidate()
        L_0x0257:
            float r12 = r11.enterAnimationProgress
            int r0 = (r12 > r5 ? 1 : (r12 == r5 ? 0 : -1))
            if (r0 >= 0) goto L_0x0260
            r11.enterAnimationProgress = r5
            goto L_0x0266
        L_0x0260:
            int r12 = (r12 > r7 ? 1 : (r12 == r7 ? 0 : -1))
            if (r12 <= 0) goto L_0x0266
            r11.enterAnimationProgress = r7
        L_0x0266:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.VideoForwardDrawable.draw(android.graphics.Canvas):void");
    }

    public void setShowing(boolean z) {
        this.showing = z;
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

    public void addTime(long j) {
        long j2 = this.time + j;
        this.time = j2;
        this.timeStr = LocaleController.formatPluralString("Seconds", (int) (j2 / 1000));
    }
}
