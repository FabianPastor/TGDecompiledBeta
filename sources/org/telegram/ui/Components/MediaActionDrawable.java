package org.telegram.ui.Components;

import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.view.animation.DecelerateInterpolator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class MediaActionDrawable extends Drawable {
    private float animatedDownloadProgress;
    private boolean animatingTransition;
    private Paint backPaint = new Paint(1);
    private ColorFilter colorFilter;
    private int currentIcon;
    private MediaActionDrawableDelegate delegate;
    private float downloadProgress;
    private float downloadProgressAnimationStart;
    private float downloadProgressTime;
    private float downloadRadOffset;
    private boolean hasOverlayImage;
    private DecelerateInterpolator interpolator = new DecelerateInterpolator();
    private boolean isMini;
    private long lastAnimationTime;
    private int lastPercent = -1;
    private Theme.MessageDrawable messageDrawable;
    private int nextIcon;
    private float overrideAlpha = 1.0f;
    private Paint paint = new Paint(1);
    private Paint paint2 = new Paint(1);
    private Paint paint3 = new Paint(1);
    private String percentString;
    private int percentStringWidth;
    private RectF rect = new RectF();
    private float savedTransitionProgress;
    private float scale = 1.0f;
    private TextPaint textPaint = new TextPaint(1);
    private float transitionAnimationTime = 400.0f;
    private float transitionProgress = 1.0f;

    public interface MediaActionDrawableDelegate {
        void invalidate();
    }

    private float getCircleValue(float f) {
        while (f > 360.0f) {
            f -= 360.0f;
        }
        return f;
    }

    public int getOpacity() {
        return -2;
    }

    public void setAlpha(int i) {
    }

    public MediaActionDrawable() {
        this.paint.setColor(-1);
        this.paint.setStrokeCap(Paint.Cap.ROUND);
        this.paint.setStrokeWidth((float) AndroidUtilities.dp(3.0f));
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint3.setColor(-1);
        this.textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textPaint.setTextSize((float) AndroidUtilities.dp(13.0f));
        this.textPaint.setColor(-1);
        this.paint2.setColor(-1);
    }

    public void setOverrideAlpha(float f) {
        this.overrideAlpha = f;
    }

    public void setColorFilter(ColorFilter colorFilter2) {
        this.paint.setColorFilter(colorFilter2);
        this.paint2.setColorFilter(colorFilter2);
        this.paint3.setColorFilter(colorFilter2);
        this.textPaint.setColorFilter(colorFilter2);
    }

    public void setColor(int i) {
        int i2 = -16777216 | i;
        this.paint.setColor(i2);
        this.paint2.setColor(i2);
        this.paint3.setColor(i2);
        this.textPaint.setColor(i2);
        this.colorFilter = new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY);
    }

    public void setBackColor(int i) {
        this.backPaint.setColor(i | -16777216);
    }

    public void setMini(boolean z) {
        this.isMini = z;
        this.paint.setStrokeWidth((float) AndroidUtilities.dp(z ? 2.0f : 3.0f));
    }

    public void setDelegate(MediaActionDrawableDelegate mediaActionDrawableDelegate) {
        this.delegate = mediaActionDrawableDelegate;
    }

    public boolean setIcon(int i, boolean z) {
        int i2;
        if (this.currentIcon == i && (i2 = this.nextIcon) != i) {
            this.currentIcon = i2;
            this.transitionProgress = 1.0f;
        }
        if (z) {
            int i3 = this.currentIcon;
            if (i3 == i || this.nextIcon == i) {
                return false;
            }
            if ((i3 == 0 && i == 1) || (this.currentIcon == 1 && i == 0)) {
                this.transitionAnimationTime = 666.0f;
            } else if (this.currentIcon == 2 && (i == 3 || i == 14)) {
                this.transitionAnimationTime = 400.0f;
            } else if (this.currentIcon != 4 && i == 6) {
                this.transitionAnimationTime = 360.0f;
            } else if ((this.currentIcon == 4 && i == 14) || (this.currentIcon == 14 && i == 4)) {
                this.transitionAnimationTime = 160.0f;
            } else {
                this.transitionAnimationTime = 220.0f;
            }
            if (this.animatingTransition) {
                this.currentIcon = this.nextIcon;
            }
            this.animatingTransition = true;
            this.nextIcon = i;
            this.savedTransitionProgress = this.transitionProgress;
            this.transitionProgress = 0.0f;
        } else if (this.currentIcon == i) {
            return false;
        } else {
            this.animatingTransition = false;
            this.nextIcon = i;
            this.currentIcon = i;
            this.savedTransitionProgress = this.transitionProgress;
            this.transitionProgress = 1.0f;
        }
        if (i == 3 || i == 14) {
            this.downloadRadOffset = 112.0f;
            this.animatedDownloadProgress = 0.0f;
            this.downloadProgressAnimationStart = 0.0f;
            this.downloadProgressTime = 0.0f;
        }
        invalidateSelf();
        return true;
    }

    public int getCurrentIcon() {
        return this.nextIcon;
    }

    public int getPreviousIcon() {
        return this.currentIcon;
    }

    public void setProgress(float f, boolean z) {
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

    public float getProgress() {
        return this.downloadProgress;
    }

    public float getTransitionProgress() {
        if (this.animatingTransition) {
            return this.transitionProgress;
        }
        return 1.0f;
    }

    public void setBackgroundDrawable(Theme.MessageDrawable messageDrawable2) {
        this.messageDrawable = messageDrawable2;
    }

    public void setHasOverlayImage(boolean z) {
        this.hasOverlayImage = z;
    }

    public void setBounds(int i, int i2, int i3, int i4) {
        super.setBounds(i, i2, i3, i4);
        float intrinsicWidth = ((float) (i3 - i)) / ((float) getIntrinsicWidth());
        this.scale = intrinsicWidth;
        if (intrinsicWidth < 0.7f) {
            this.paint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        }
    }

    public void invalidateSelf() {
        super.invalidateSelf();
        MediaActionDrawableDelegate mediaActionDrawableDelegate = this.delegate;
        if (mediaActionDrawableDelegate != null) {
            mediaActionDrawableDelegate.invalidate();
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:322:0x0846, code lost:
        if (r0.nextIcon == 1) goto L_0x0848;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:323:0x0848, code lost:
        r4 = 1.0f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:324:0x084b, code lost:
        r4 = 0.0f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:327:0x0850, code lost:
        if (r0.currentIcon != r2) goto L_0x084b;
     */
    /* JADX WARNING: Removed duplicated region for block: B:101:0x0357  */
    /* JADX WARNING: Removed duplicated region for block: B:123:0x03cc  */
    /* JADX WARNING: Removed duplicated region for block: B:126:0x03d4  */
    /* JADX WARNING: Removed duplicated region for block: B:132:0x03fb  */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x04e6  */
    /* JADX WARNING: Removed duplicated region for block: B:175:0x04f1  */
    /* JADX WARNING: Removed duplicated region for block: B:177:0x04fb  */
    /* JADX WARNING: Removed duplicated region for block: B:183:0x0579  */
    /* JADX WARNING: Removed duplicated region for block: B:186:0x0581  */
    /* JADX WARNING: Removed duplicated region for block: B:198:0x05a5  */
    /* JADX WARNING: Removed duplicated region for block: B:199:0x05a8  */
    /* JADX WARNING: Removed duplicated region for block: B:211:0x0607  */
    /* JADX WARNING: Removed duplicated region for block: B:214:0x0610  */
    /* JADX WARNING: Removed duplicated region for block: B:215:0x0615  */
    /* JADX WARNING: Removed duplicated region for block: B:221:0x063a  */
    /* JADX WARNING: Removed duplicated region for block: B:222:0x063f  */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x064f  */
    /* JADX WARNING: Removed duplicated region for block: B:230:0x0655  */
    /* JADX WARNING: Removed duplicated region for block: B:237:0x0666  */
    /* JADX WARNING: Removed duplicated region for block: B:238:0x0669  */
    /* JADX WARNING: Removed duplicated region for block: B:245:0x0683  */
    /* JADX WARNING: Removed duplicated region for block: B:246:0x068d  */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x0723  */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x0726  */
    /* JADX WARNING: Removed duplicated region for block: B:270:0x073b  */
    /* JADX WARNING: Removed duplicated region for block: B:271:0x073e  */
    /* JADX WARNING: Removed duplicated region for block: B:274:0x0752  */
    /* JADX WARNING: Removed duplicated region for block: B:277:0x0789  */
    /* JADX WARNING: Removed duplicated region for block: B:284:0x079c  */
    /* JADX WARNING: Removed duplicated region for block: B:285:0x079f  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x00cf A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:290:0x07c5  */
    /* JADX WARNING: Removed duplicated region for block: B:298:0x080b  */
    /* JADX WARNING: Removed duplicated region for block: B:301:0x0813 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:312:0x082c  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x0139  */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x02fc  */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x0325  */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x0334  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void draw(android.graphics.Canvas r34) {
        /*
            r33 = this;
            r0 = r33
            r7 = r34
            android.graphics.Rect r8 = r33.getBounds()
            org.telegram.ui.ActionBar.Theme$MessageDrawable r1 = r0.messageDrawable
            r9 = 0
            r10 = 0
            if (r1 == 0) goto L_0x003d
            boolean r1 = r1.hasGradient()
            if (r1 == 0) goto L_0x003d
            boolean r1 = r0.hasOverlayImage
            if (r1 != 0) goto L_0x003d
            org.telegram.ui.ActionBar.Theme$MessageDrawable r1 = r0.messageDrawable
            android.graphics.LinearGradient r1 = r1.getGradientShader()
            org.telegram.ui.ActionBar.Theme$MessageDrawable r2 = r0.messageDrawable
            android.graphics.Matrix r2 = r2.getMatrix()
            int r3 = r8.top
            float r3 = (float) r3
            r2.postTranslate(r10, r3)
            r1.setLocalMatrix(r2)
            android.graphics.Paint r2 = r0.paint
            r2.setShader(r1)
            android.graphics.Paint r2 = r0.paint2
            r2.setShader(r1)
            android.graphics.Paint r2 = r0.paint3
            r2.setShader(r1)
            goto L_0x004c
        L_0x003d:
            android.graphics.Paint r1 = r0.paint
            r1.setShader(r9)
            android.graphics.Paint r1 = r0.paint2
            r1.setShader(r9)
            android.graphics.Paint r1 = r0.paint3
            r1.setShader(r9)
        L_0x004c:
            int r11 = r8.centerX()
            int r12 = r8.centerY()
            int r1 = r0.nextIcon
            r13 = 6
            r15 = 3
            r6 = 4
            r5 = 14
            r4 = 1065353216(0x3var_, float:1.0)
            if (r1 != r6) goto L_0x0073
            int r1 = r0.currentIcon
            if (r1 == r15) goto L_0x008a
            if (r1 == r5) goto L_0x008a
            int r1 = r34.save()
            float r2 = r0.transitionProgress
            float r2 = r4 - r2
            float r3 = (float) r11
            float r9 = (float) r12
            r7.scale(r2, r2, r3, r9)
            goto L_0x0088
        L_0x0073:
            if (r1 == r13) goto L_0x0079
            r2 = 10
            if (r1 != r2) goto L_0x008a
        L_0x0079:
            int r1 = r0.currentIcon
            if (r1 != r6) goto L_0x008a
            int r1 = r34.save()
            float r2 = r0.transitionProgress
            float r3 = (float) r11
            float r9 = (float) r12
            r7.scale(r2, r2, r3, r9)
        L_0x0088:
            r9 = r1
            goto L_0x008b
        L_0x008a:
            r9 = 0
        L_0x008b:
            r1 = 1077936128(0x40400000, float:3.0)
            org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = r0.currentIcon
            r17 = 1120403456(0x42CLASSNAME, float:100.0)
            r18 = 1094713344(0x41400000, float:12.0)
            r19 = 1080033280(0x40600000, float:3.5)
            r20 = 1073741824(0x40000000, float:2.0)
            r21 = 1088421888(0x40e00000, float:7.0)
            r22 = 1132396544(0x437var_, float:255.0)
            r14 = 2
            r23 = 1056964608(0x3var_, float:0.5)
            if (r1 == r14) goto L_0x00a7
            int r1 = r0.nextIcon
            if (r1 != r14) goto L_0x034b
        L_0x00a7:
            float r1 = (float) r12
            r24 = 1091567616(0x41100000, float:9.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r24)
            float r3 = (float) r3
            float r6 = r0.scale
            float r3 = r3 * r6
            float r3 = r1 - r3
            r6 = 1091567616(0x41100000, float:9.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r6 = (float) r6
            float r13 = r0.scale
            float r6 = r6 * r13
            float r6 = r6 + r1
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r13 = (float) r13
            float r10 = r0.scale
            float r13 = r13 * r10
            float r13 = r13 + r1
            int r10 = r0.currentIcon
            if (r10 == r15) goto L_0x00d1
            if (r10 != r5) goto L_0x00f6
        L_0x00d1:
            int r10 = r0.nextIcon
            if (r10 != r14) goto L_0x00f6
            android.graphics.Paint r10 = r0.paint
            float r2 = r0.transitionProgress
            float r2 = r2 / r23
            float r2 = java.lang.Math.min(r4, r2)
            float r2 = r2 * r22
            int r2 = (int) r2
            r10.setAlpha(r2)
            float r2 = r0.transitionProgress
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r10 = (float) r10
            float r4 = r0.scale
            float r10 = r10 * r4
            float r10 = r10 + r1
            r25 = r10
            r10 = 1065353216(0x3var_, float:1.0)
            goto L_0x0133
        L_0x00f6:
            int r2 = r0.nextIcon
            if (r2 == r15) goto L_0x011b
            if (r2 == r5) goto L_0x011b
            if (r2 == r14) goto L_0x011b
            android.graphics.Paint r2 = r0.paint
            float r4 = r0.savedTransitionProgress
            float r4 = r4 / r23
            r10 = 1065353216(0x3var_, float:1.0)
            float r4 = java.lang.Math.min(r10, r4)
            float r4 = r4 * r22
            float r5 = r0.transitionProgress
            float r5 = r10 - r5
            float r4 = r4 * r5
            int r4 = (int) r4
            r2.setAlpha(r4)
            float r2 = r0.savedTransitionProgress
            r4 = 255(0xff, float:3.57E-43)
            goto L_0x0124
        L_0x011b:
            android.graphics.Paint r2 = r0.paint
            r4 = 255(0xff, float:3.57E-43)
            r2.setAlpha(r4)
            float r2 = r0.transitionProgress
        L_0x0124:
            r10 = 1065353216(0x3var_, float:1.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r5 = (float) r5
            float r4 = r0.scale
            float r5 = r5 * r4
            float r4 = r1 + r5
            r25 = r4
        L_0x0133:
            boolean r4 = r0.animatingTransition
            r5 = 1090519040(0x41000000, float:8.0)
            if (r4 == 0) goto L_0x02fc
            int r4 = r0.nextIcon
            if (r4 == r14) goto L_0x02af
            int r4 = (r2 > r23 ? 1 : (r2 == r23 ? 0 : -1))
            if (r4 > 0) goto L_0x0143
            goto L_0x02af
        L_0x0143:
            r3 = 1095761920(0x41500000, float:13.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            float r4 = r0.scale
            float r3 = r3 * r4
            float r3 = r3 * r4
            boolean r4 = r0.isMini
            if (r4 == 0) goto L_0x0159
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r20)
            goto L_0x015a
        L_0x0159:
            r4 = 0
        L_0x015a:
            float r4 = (float) r4
            float r3 = r3 + r4
            float r2 = r2 - r23
            float r4 = r2 / r23
            r5 = 1045220557(0x3e4ccccd, float:0.2)
            int r5 = (r2 > r5 ? 1 : (r2 == r5 ? 0 : -1))
            if (r5 <= 0) goto L_0x0174
            r5 = 1045220557(0x3e4ccccd, float:0.2)
            float r2 = r2 - r5
            r5 = 1050253722(0x3e99999a, float:0.3)
            float r2 = r2 / r5
            r27 = r2
            r26 = 1065353216(0x3var_, float:1.0)
            goto L_0x017c
        L_0x0174:
            r5 = 1045220557(0x3e4ccccd, float:0.2)
            float r2 = r2 / r5
            r26 = r2
            r27 = 0
        L_0x017c:
            android.graphics.RectF r2 = r0.rect
            float r6 = (float) r11
            float r5 = r6 - r3
            float r3 = r3 / r20
            float r10 = r13 - r3
            float r3 = r3 + r13
            r2.set(r5, r10, r6, r3)
            float r3 = r27 * r17
            android.graphics.RectF r2 = r0.rect
            r5 = 1120927744(0x42d00000, float:104.0)
            float r4 = r4 * r5
            float r4 = r4 - r3
            r5 = 0
            android.graphics.Paint r10 = r0.paint
            r28 = r1
            r1 = r34
            r14 = 1065353216(0x3var_, float:1.0)
            r15 = 14
            r30 = r6
            r6 = r10
            r1.drawArc(r2, r3, r4, r5, r6)
            float r1 = r13 - r25
            float r1 = r1 * r26
            float r25 = r25 + r1
            r1 = 0
            int r2 = (r27 > r1 ? 1 : (r27 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x02a8
            int r1 = r0.nextIcon
            if (r1 != r15) goto L_0x01b4
            r10 = 0
            goto L_0x01bb
        L_0x01b4:
            r1 = -1036779520(0xffffffffCLASSNAME, float:-45.0)
            float r4 = r14 - r27
            float r1 = r1 * r4
            r10 = r1
        L_0x01bb:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r1 = (float) r1
            float r1 = r1 * r27
            float r2 = r0.scale
            float r1 = r1 * r2
            float r2 = r27 * r22
            int r2 = (int) r2
            int r3 = r0.nextIcon
            r4 = 3
            if (r3 == r4) goto L_0x01e1
            if (r3 == r15) goto L_0x01e1
            r4 = 2
            if (r3 == r4) goto L_0x01e1
            float r3 = r0.transitionProgress
            float r3 = r3 / r23
            float r3 = java.lang.Math.min(r14, r3)
            float r4 = r14 - r3
            float r2 = (float) r2
            float r2 = r2 * r4
            int r2 = (int) r2
        L_0x01e1:
            r6 = r2
            r2 = 0
            int r3 = (r10 > r2 ? 1 : (r10 == r2 ? 0 : -1))
            if (r3 == 0) goto L_0x01f2
            r34.save()
            r2 = r28
            r5 = r30
            r7.rotate(r10, r5, r2)
            goto L_0x01f6
        L_0x01f2:
            r2 = r28
            r5 = r30
        L_0x01f6:
            if (r6 == 0) goto L_0x029d
            android.graphics.Paint r3 = r0.paint
            r3.setAlpha(r6)
            int r3 = r0.nextIcon
            if (r3 != r15) goto L_0x027d
            android.graphics.Paint r1 = r0.paint3
            r1.setAlpha(r6)
            android.graphics.RectF r1 = r0.rect
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r2 = r11 - r2
            float r2 = (float) r2
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r3 = r12 - r3
            float r3 = (float) r3
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r4 = r4 + r11
            float r4 = (float) r4
            int r24 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r15 = r12 + r24
            float r15 = (float) r15
            r1.set(r2, r3, r4, r15)
            android.graphics.RectF r1 = r0.rect
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r20)
            float r2 = (float) r2
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r20)
            float r3 = (float) r3
            android.graphics.Paint r4 = r0.paint3
            r7.drawRoundRect(r1, r2, r3, r4)
            android.graphics.Paint r1 = r0.paint
            float r2 = (float) r6
            r3 = 1041865114(0x3e19999a, float:0.15)
            float r2 = r2 * r3
            int r2 = (int) r2
            r1.setAlpha(r2)
            boolean r1 = r0.isMini
            if (r1 == 0) goto L_0x024a
            r3 = 1073741824(0x40000000, float:2.0)
            goto L_0x024c
        L_0x024a:
            r3 = 1082130432(0x40800000, float:4.0)
        L_0x024c:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r3)
            android.graphics.RectF r2 = r0.rect
            int r3 = r8.left
            int r3 = r3 + r1
            float r3 = (float) r3
            int r4 = r8.top
            int r4 = r4 + r1
            float r4 = (float) r4
            int r15 = r8.right
            int r15 = r15 - r1
            float r15 = (float) r15
            int r14 = r8.bottom
            int r14 = r14 - r1
            float r1 = (float) r14
            r2.set(r3, r4, r15, r1)
            android.graphics.RectF r2 = r0.rect
            r3 = 0
            r4 = 1135869952(0x43b40000, float:360.0)
            r14 = 0
            android.graphics.Paint r15 = r0.paint
            r1 = r34
            r24 = r5
            r5 = r14
            r14 = r6
            r6 = r15
            r1.drawArc(r2, r3, r4, r5, r6)
            android.graphics.Paint r1 = r0.paint
            r1.setAlpha(r14)
            goto L_0x029f
        L_0x027d:
            r24 = r5
            float r14 = r24 - r1
            float r15 = r2 - r1
            float r26 = r24 + r1
            float r27 = r2 + r1
            android.graphics.Paint r6 = r0.paint
            r1 = r34
            r2 = r14
            r3 = r15
            r4 = r26
            r5 = r27
            r1.drawLine(r2, r3, r4, r5, r6)
            android.graphics.Paint r6 = r0.paint
            r2 = r26
            r4 = r14
            r1.drawLine(r2, r3, r4, r5, r6)
            goto L_0x029f
        L_0x029d:
            r24 = r5
        L_0x029f:
            r1 = 0
            int r2 = (r10 > r1 ? 1 : (r10 == r1 ? 0 : -1))
            if (r2 == 0) goto L_0x02aa
            r34.restore()
            goto L_0x02aa
        L_0x02a8:
            r24 = r30
        L_0x02aa:
            r2 = r13
            r1 = r24
            r6 = r1
            goto L_0x02f5
        L_0x02af:
            int r1 = r0.nextIcon
            r4 = 2
            if (r1 != r4) goto L_0x02b9
            r1 = 1065353216(0x3var_, float:1.0)
            float r4 = r1 - r2
            goto L_0x02bf
        L_0x02b9:
            r1 = 1065353216(0x3var_, float:1.0)
            float r4 = r2 / r23
            float r2 = r1 - r4
        L_0x02bf:
            float r25 = r25 - r3
            float r25 = r25 * r4
            float r25 = r3 + r25
            float r13 = r13 - r6
            float r13 = r13 * r4
            float r13 = r13 + r6
            float r1 = (float) r11
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r3 = (float) r3
            float r3 = r3 * r2
            float r4 = r0.scale
            float r3 = r3 * r4
            float r6 = r1 - r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r3 = (float) r3
            float r3 = r3 * r2
            float r4 = r0.scale
            float r3 = r3 * r4
            float r1 = r1 + r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r3 = (float) r3
            float r3 = r3 * r2
            float r2 = r0.scale
            float r3 = r3 * r2
            float r2 = r13 - r3
            r32 = r13
            r13 = r2
            r2 = r32
        L_0x02f5:
            r15 = r2
            r10 = r6
            r14 = r13
            r3 = r25
            r13 = r1
            goto L_0x0321
        L_0x02fc:
            float r1 = (float) r11
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r2 = (float) r2
            float r4 = r0.scale
            float r2 = r2 * r4
            float r2 = r1 - r2
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r4 = (float) r4
            float r10 = r0.scale
            float r4 = r4 * r10
            float r1 = r1 + r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r4 = (float) r4
            float r5 = r0.scale
            float r4 = r4 * r5
            float r4 = r6 - r4
            r13 = r1
            r10 = r2
            r14 = r4
            r15 = r6
        L_0x0321:
            int r1 = (r3 > r15 ? 1 : (r3 == r15 ? 0 : -1))
            if (r1 == 0) goto L_0x032f
            float r4 = (float) r11
            android.graphics.Paint r6 = r0.paint
            r1 = r34
            r2 = r4
            r5 = r15
            r1.drawLine(r2, r3, r4, r5, r6)
        L_0x032f:
            float r6 = (float) r11
            int r1 = (r10 > r6 ? 1 : (r10 == r6 ? 0 : -1))
            if (r1 == 0) goto L_0x034b
            android.graphics.Paint r5 = r0.paint
            r1 = r34
            r2 = r10
            r3 = r14
            r4 = r6
            r10 = r5
            r5 = r15
            r24 = r6
            r6 = r10
            r1.drawLine(r2, r3, r4, r5, r6)
            android.graphics.Paint r6 = r0.paint
            r2 = r13
            r4 = r24
            r1.drawLine(r2, r3, r4, r5, r6)
        L_0x034b:
            int r1 = r0.currentIcon
            r10 = 13
            r13 = 1
            r2 = 3
            if (r1 == r2) goto L_0x03cc
            r3 = 14
            if (r1 == r3) goto L_0x03cc
            r14 = 4
            if (r1 != r14) goto L_0x0361
            int r1 = r0.nextIcon
            if (r1 == r3) goto L_0x03cd
            if (r1 != r2) goto L_0x0361
            goto L_0x03cd
        L_0x0361:
            int r1 = r0.currentIcon
            r2 = 10
            if (r1 == r2) goto L_0x036f
            int r2 = r0.nextIcon
            r3 = 10
            if (r2 == r3) goto L_0x036f
            if (r1 != r10) goto L_0x060a
        L_0x036f:
            int r1 = r0.nextIcon
            if (r1 == r14) goto L_0x037a
            r2 = 6
            if (r1 != r2) goto L_0x0377
            goto L_0x037a
        L_0x0377:
            r2 = 255(0xff, float:3.57E-43)
            goto L_0x0383
        L_0x037a:
            float r1 = r0.transitionProgress
            r2 = 1065353216(0x3var_, float:1.0)
            float r4 = r2 - r1
            float r4 = r4 * r22
            int r2 = (int) r4
        L_0x0383:
            if (r2 == 0) goto L_0x060a
            android.graphics.Paint r1 = r0.paint
            float r2 = (float) r2
            float r3 = r0.overrideAlpha
            float r2 = r2 * r3
            int r2 = (int) r2
            r1.setAlpha(r2)
            r1 = 1135869952(0x43b40000, float:360.0)
            float r2 = r0.animatedDownloadProgress
            float r2 = r2 * r1
            r15 = 1082130432(0x40800000, float:4.0)
            float r4 = java.lang.Math.max(r15, r2)
            boolean r1 = r0.isMini
            if (r1 == 0) goto L_0x03a3
            r3 = 1073741824(0x40000000, float:2.0)
            goto L_0x03a5
        L_0x03a3:
            r3 = 1082130432(0x40800000, float:4.0)
        L_0x03a5:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r3)
            android.graphics.RectF r2 = r0.rect
            int r3 = r8.left
            int r3 = r3 + r1
            float r3 = (float) r3
            int r5 = r8.top
            int r5 = r5 + r1
            float r5 = (float) r5
            int r6 = r8.right
            int r6 = r6 - r1
            float r6 = (float) r6
            int r15 = r8.bottom
            int r15 = r15 - r1
            float r1 = (float) r15
            r2.set(r3, r5, r6, r1)
            android.graphics.RectF r2 = r0.rect
            float r3 = r0.downloadRadOffset
            r5 = 0
            android.graphics.Paint r6 = r0.paint
            r1 = r34
            r1.drawArc(r2, r3, r4, r5, r6)
            goto L_0x060a
        L_0x03cc:
            r14 = 4
        L_0x03cd:
            r15 = 1082130432(0x40800000, float:4.0)
            int r1 = r0.nextIcon
            r2 = 2
            if (r1 != r2) goto L_0x03fb
            float r1 = r0.transitionProgress
            int r2 = (r1 > r23 ? 1 : (r1 == r23 ? 0 : -1))
            if (r2 > 0) goto L_0x03ef
            float r1 = r1 / r23
            r2 = 1065353216(0x3var_, float:1.0)
            float r4 = r2 - r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r1 = (float) r1
            float r1 = r1 * r4
            float r2 = r0.scale
            float r1 = r1 * r2
            float r4 = r4 * r22
            int r2 = (int) r4
            goto L_0x03f1
        L_0x03ef:
            r1 = 0
            r2 = 0
        L_0x03f1:
            r6 = r2
            r2 = 1065353216(0x3var_, float:1.0)
            r3 = 0
            r4 = 0
            r5 = 1065353216(0x3var_, float:1.0)
        L_0x03f8:
            r13 = 0
            goto L_0x04e2
        L_0x03fb:
            if (r1 == 0) goto L_0x04ad
            if (r1 == r13) goto L_0x04ad
            r2 = 5
            if (r1 == r2) goto L_0x04ad
            r2 = 8
            if (r1 == r2) goto L_0x04ad
            r2 = 9
            if (r1 == r2) goto L_0x04ad
            r2 = 7
            if (r1 == r2) goto L_0x04ad
            r2 = 6
            if (r1 != r2) goto L_0x0412
            goto L_0x04ad
        L_0x0412:
            if (r1 != r14) goto L_0x0455
            float r1 = r0.transitionProgress
            r2 = 1065353216(0x3var_, float:1.0)
            float r4 = r2 - r1
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r2 = (float) r2
            float r3 = r0.scale
            float r2 = r2 * r3
            float r3 = r4 * r22
            int r3 = (int) r3
            int r5 = r0.currentIcon
            r6 = 14
            if (r5 != r6) goto L_0x0436
            int r1 = r8.left
            float r1 = (float) r1
            int r5 = r8.top
            float r5 = (float) r5
            r6 = r5
            r5 = r1
            r1 = 0
            goto L_0x0448
        L_0x0436:
            r4 = 1110704128(0x42340000, float:45.0)
            float r1 = r1 * r4
            int r4 = r8.centerX()
            float r4 = (float) r4
            int r5 = r8.centerY()
            float r5 = (float) r5
            r6 = r5
            r5 = r4
            r4 = 1065353216(0x3var_, float:1.0)
        L_0x0448:
            r13 = r1
            r1 = r2
            r2 = 1065353216(0x3var_, float:1.0)
            r32 = r6
            r6 = r3
        L_0x044f:
            r3 = r5
            r5 = r4
            r4 = r32
            goto L_0x04e2
        L_0x0455:
            r2 = 14
            if (r1 == r2) goto L_0x046f
            r2 = 3
            if (r1 != r2) goto L_0x045d
            goto L_0x046f
        L_0x045d:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r1 = (float) r1
            float r2 = r0.scale
            float r1 = r1 * r2
            r2 = 1065353216(0x3var_, float:1.0)
            r3 = 0
            r4 = 0
            r5 = 1065353216(0x3var_, float:1.0)
            r6 = 255(0xff, float:3.57E-43)
            goto L_0x03f8
        L_0x046f:
            float r1 = r0.transitionProgress
            r2 = 1065353216(0x3var_, float:1.0)
            float r4 = r2 - r1
            int r2 = r0.currentIcon
            if (r2 != r14) goto L_0x047c
            r4 = r1
            r2 = 0
            goto L_0x0483
        L_0x047c:
            r2 = 1110704128(0x42340000, float:45.0)
            float r4 = r4 * r2
            r2 = r4
            r4 = 1065353216(0x3var_, float:1.0)
        L_0x0483:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r3 = (float) r3
            float r5 = r0.scale
            float r3 = r3 * r5
            float r1 = r1 * r22
            int r1 = (int) r1
            int r5 = r0.nextIcon
            r6 = 14
            if (r5 != r6) goto L_0x049b
            int r5 = r8.left
            float r5 = (float) r5
            int r6 = r8.top
            goto L_0x04a4
        L_0x049b:
            int r5 = r8.centerX()
            float r5 = (float) r5
            int r6 = r8.centerY()
        L_0x04a4:
            float r6 = (float) r6
            r13 = r2
            r2 = 1065353216(0x3var_, float:1.0)
            r32 = r6
            r6 = r1
            r1 = r3
            goto L_0x044f
        L_0x04ad:
            int r1 = r0.nextIcon
            r2 = 6
            if (r1 != r2) goto L_0x04bd
            float r1 = r0.transitionProgress
            float r1 = r1 / r23
            r2 = 1065353216(0x3var_, float:1.0)
            float r1 = java.lang.Math.min(r2, r1)
            goto L_0x04c1
        L_0x04bd:
            r2 = 1065353216(0x3var_, float:1.0)
            float r1 = r0.transitionProgress
        L_0x04c1:
            float r4 = r2 - r1
            r3 = 1110704128(0x42340000, float:45.0)
            float r1 = r1 * r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r3 = (float) r3
            float r3 = r3 * r4
            float r5 = r0.scale
            float r3 = r3 * r5
            float r4 = r4 * r20
            float r4 = java.lang.Math.min(r2, r4)
            float r4 = r4 * r22
            int r4 = (int) r4
            r13 = r1
            r1 = r3
            r6 = r4
            r3 = 0
            r4 = 0
            r5 = 1065353216(0x3var_, float:1.0)
        L_0x04e2:
            int r25 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1))
            if (r25 == 0) goto L_0x04ec
            r34.save()
            r7.scale(r5, r5, r3, r4)
        L_0x04ec:
            r2 = 0
            int r3 = (r13 > r2 ? 1 : (r13 == r2 ? 0 : -1))
            if (r3 == 0) goto L_0x04f9
            r34.save()
            float r2 = (float) r11
            float r3 = (float) r12
            r7.rotate(r13, r2, r3)
        L_0x04f9:
            if (r6 == 0) goto L_0x0579
            android.graphics.Paint r2 = r0.paint
            float r3 = (float) r6
            float r4 = r0.overrideAlpha
            float r4 = r4 * r3
            int r4 = (int) r4
            r2.setAlpha(r4)
            int r2 = r0.currentIcon
            r4 = 14
            if (r2 == r4) goto L_0x053b
            int r2 = r0.nextIcon
            if (r2 != r4) goto L_0x0511
            goto L_0x053b
        L_0x0511:
            float r2 = (float) r11
            float r19 = r2 - r1
            float r3 = (float) r12
            float r25 = r3 - r1
            float r26 = r2 + r1
            float r27 = r3 + r1
            android.graphics.Paint r4 = r0.paint
            r1 = r34
            r2 = r19
            r3 = r25
            r28 = r4
            r4 = r26
            r29 = r5
            r5 = r27
            r10 = r6
            r6 = r28
            r1.drawLine(r2, r3, r4, r5, r6)
            android.graphics.Paint r6 = r0.paint
            r2 = r26
            r4 = r19
            r1.drawLine(r2, r3, r4, r5, r6)
            goto L_0x057c
        L_0x053b:
            r29 = r5
            r10 = r6
            android.graphics.Paint r1 = r0.paint3
            float r2 = r0.overrideAlpha
            float r3 = r3 * r2
            int r2 = (int) r3
            r1.setAlpha(r2)
            android.graphics.RectF r1 = r0.rect
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r2 = r11 - r2
            float r2 = (float) r2
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r3 = r12 - r3
            float r3 = (float) r3
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r4 = r4 + r11
            float r4 = (float) r4
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r5 = r5 + r12
            float r5 = (float) r5
            r1.set(r2, r3, r4, r5)
            android.graphics.RectF r1 = r0.rect
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r20)
            float r2 = (float) r2
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r20)
            float r3 = (float) r3
            android.graphics.Paint r4 = r0.paint3
            r7.drawRoundRect(r1, r2, r3, r4)
            goto L_0x057c
        L_0x0579:
            r29 = r5
            r10 = r6
        L_0x057c:
            r1 = 0
            int r2 = (r13 > r1 ? 1 : (r13 == r1 ? 0 : -1))
            if (r2 == 0) goto L_0x0584
            r34.restore()
        L_0x0584:
            int r1 = r0.currentIcon
            r2 = 3
            if (r1 == r2) goto L_0x0595
            r3 = 14
            if (r1 == r3) goto L_0x0595
            if (r1 != r14) goto L_0x0601
            int r1 = r0.nextIcon
            if (r1 == r3) goto L_0x0595
            if (r1 != r2) goto L_0x0601
        L_0x0595:
            if (r10 == 0) goto L_0x0601
            r1 = 1135869952(0x43b40000, float:360.0)
            float r2 = r0.animatedDownloadProgress
            float r2 = r2 * r1
            float r13 = java.lang.Math.max(r15, r2)
            boolean r1 = r0.isMini
            if (r1 == 0) goto L_0x05a8
            r3 = 1073741824(0x40000000, float:2.0)
            goto L_0x05aa
        L_0x05a8:
            r3 = 1082130432(0x40800000, float:4.0)
        L_0x05aa:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r3)
            android.graphics.RectF r2 = r0.rect
            int r3 = r8.left
            int r3 = r3 + r1
            float r3 = (float) r3
            int r4 = r8.top
            int r4 = r4 + r1
            float r4 = (float) r4
            int r5 = r8.right
            int r5 = r5 - r1
            float r5 = (float) r5
            int r6 = r8.bottom
            int r6 = r6 - r1
            float r1 = (float) r6
            r2.set(r3, r4, r5, r1)
            int r1 = r0.currentIcon
            r2 = 14
            if (r1 == r2) goto L_0x05d2
            if (r1 != r14) goto L_0x05f4
            int r1 = r0.nextIcon
            if (r1 == r2) goto L_0x05d2
            r2 = 3
            if (r1 != r2) goto L_0x05f4
        L_0x05d2:
            android.graphics.Paint r1 = r0.paint
            float r2 = (float) r10
            r3 = 1041865114(0x3e19999a, float:0.15)
            float r2 = r2 * r3
            float r3 = r0.overrideAlpha
            float r2 = r2 * r3
            int r2 = (int) r2
            r1.setAlpha(r2)
            android.graphics.RectF r2 = r0.rect
            r3 = 0
            r4 = 1135869952(0x43b40000, float:360.0)
            r5 = 0
            android.graphics.Paint r6 = r0.paint
            r1 = r34
            r1.drawArc(r2, r3, r4, r5, r6)
            android.graphics.Paint r1 = r0.paint
            r1.setAlpha(r10)
        L_0x05f4:
            android.graphics.RectF r2 = r0.rect
            float r3 = r0.downloadRadOffset
            r5 = 0
            android.graphics.Paint r6 = r0.paint
            r1 = r34
            r4 = r13
            r1.drawArc(r2, r3, r4, r5, r6)
        L_0x0601:
            r1 = 1065353216(0x3var_, float:1.0)
            int r2 = (r29 > r1 ? 1 : (r29 == r1 ? 0 : -1))
            if (r2 == 0) goto L_0x060a
            r34.restore()
        L_0x060a:
            int r1 = r0.currentIcon
            int r2 = r0.nextIcon
            if (r1 != r2) goto L_0x0615
            r10 = 1065353216(0x3var_, float:1.0)
            r13 = 1065353216(0x3var_, float:1.0)
            goto L_0x0635
        L_0x0615:
            if (r1 != r14) goto L_0x061e
            float r4 = r0.transitionProgress
            r1 = 1065353216(0x3var_, float:1.0)
            float r2 = r1 - r4
            goto L_0x0633
        L_0x061e:
            r1 = 1065353216(0x3var_, float:1.0)
            float r2 = r0.transitionProgress
            float r2 = r2 / r23
            float r4 = java.lang.Math.min(r1, r2)
            float r2 = r0.transitionProgress
            float r2 = r2 / r23
            float r2 = r1 - r2
            r1 = 0
            float r2 = java.lang.Math.max(r1, r2)
        L_0x0633:
            r13 = r2
            r10 = r4
        L_0x0635:
            int r1 = r0.nextIcon
            r2 = 5
            if (r1 != r2) goto L_0x063f
            android.graphics.Path[] r1 = org.telegram.ui.ActionBar.Theme.chat_filePath
            r15 = r1
            r6 = 0
            goto L_0x064a
        L_0x063f:
            int r1 = r0.currentIcon
            r2 = 5
            if (r1 != r2) goto L_0x0648
            android.graphics.Path[] r1 = org.telegram.ui.ActionBar.Theme.chat_filePath
            r6 = r1
            goto L_0x0649
        L_0x0648:
            r6 = 0
        L_0x0649:
            r15 = 0
        L_0x064a:
            int r1 = r0.nextIcon
            r2 = 7
            if (r1 != r2) goto L_0x0655
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.chat_flameIcon
            r16 = r1
            r1 = 0
            goto L_0x0660
        L_0x0655:
            int r1 = r0.currentIcon
            r2 = 7
            if (r1 != r2) goto L_0x065d
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.chat_flameIcon
            goto L_0x065e
        L_0x065d:
            r1 = 0
        L_0x065e:
            r16 = 0
        L_0x0660:
            int r2 = r0.nextIcon
            r3 = 8
            if (r2 != r3) goto L_0x0669
            android.graphics.drawable.Drawable r16 = org.telegram.ui.ActionBar.Theme.chat_gifIcon
            goto L_0x0671
        L_0x0669:
            int r2 = r0.currentIcon
            r3 = 8
            if (r2 != r3) goto L_0x0671
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.chat_gifIcon
        L_0x0671:
            r5 = r1
            r4 = r16
            int r1 = r0.currentIcon
            r2 = 9
            r16 = 1086324736(0x40CLASSNAME, float:6.0)
            if (r1 == r2) goto L_0x068d
            int r1 = r0.nextIcon
            r2 = 9
            if (r1 != r2) goto L_0x0683
            goto L_0x068d
        L_0x0683:
            r14 = r4
            r31 = r6
            r20 = r9
            r19 = r15
            r15 = r5
            goto L_0x0711
        L_0x068d:
            android.graphics.Paint r1 = r0.paint
            int r2 = r0.currentIcon
            int r3 = r0.nextIcon
            if (r2 != r3) goto L_0x0698
            r2 = 255(0xff, float:3.57E-43)
            goto L_0x069d
        L_0x0698:
            float r2 = r0.transitionProgress
            float r2 = r2 * r22
            int r2 = (int) r2
        L_0x069d:
            r1.setAlpha(r2)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r3 = r12 + r1
            r1 = 1077936128(0x40400000, float:3.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r2 = r11 - r1
            int r1 = r0.currentIcon
            int r14 = r0.nextIcon
            if (r1 == r14) goto L_0x06c1
            r34.save()
            float r1 = r0.transitionProgress
            float r14 = (float) r11
            r19 = r4
            float r4 = (float) r12
            r7.scale(r1, r1, r14, r4)
            goto L_0x06c3
        L_0x06c1:
            r19 = r4
        L_0x06c3:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r1 = r2 - r1
            float r4 = (float) r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r1 = r3 - r1
            float r14 = (float) r1
            float r1 = (float) r2
            r20 = r9
            float r9 = (float) r3
            r25 = r6
            android.graphics.Paint r6 = r0.paint
            r26 = r1
            r1 = r34
            r27 = r2
            r2 = r4
            r28 = r3
            r3 = r14
            r14 = r19
            r4 = r26
            r19 = r15
            r15 = r5
            r5 = r9
            r31 = r25
            r1.drawLine(r2, r3, r4, r5, r6)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r18)
            int r2 = r27 + r1
            float r4 = (float) r2
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r18)
            int r3 = r28 - r1
            float r5 = (float) r3
            android.graphics.Paint r6 = r0.paint
            r1 = r34
            r2 = r26
            r3 = r9
            r1.drawLine(r2, r3, r4, r5, r6)
            int r1 = r0.currentIcon
            int r2 = r0.nextIcon
            if (r1 == r2) goto L_0x0711
            r34.restore()
        L_0x0711:
            int r1 = r0.currentIcon
            r2 = 12
            if (r1 == r2) goto L_0x071d
            int r1 = r0.nextIcon
            r2 = 12
            if (r1 != r2) goto L_0x078c
        L_0x071d:
            int r1 = r0.currentIcon
            int r2 = r0.nextIcon
            if (r1 != r2) goto L_0x0726
            r4 = 1065353216(0x3var_, float:1.0)
            goto L_0x0733
        L_0x0726:
            r1 = 13
            if (r2 != r1) goto L_0x072d
            float r4 = r0.transitionProgress
            goto L_0x0733
        L_0x072d:
            float r1 = r0.transitionProgress
            r2 = 1065353216(0x3var_, float:1.0)
            float r4 = r2 - r1
        L_0x0733:
            android.graphics.Paint r1 = r0.paint
            int r2 = r0.currentIcon
            int r3 = r0.nextIcon
            if (r2 != r3) goto L_0x073e
            r2 = 255(0xff, float:3.57E-43)
            goto L_0x0741
        L_0x073e:
            float r2 = r4 * r22
            int r2 = (int) r2
        L_0x0741:
            r1.setAlpha(r2)
            org.telegram.messenger.AndroidUtilities.dp(r21)
            r1 = 1077936128(0x40400000, float:3.0)
            org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = r0.currentIcon
            int r2 = r0.nextIcon
            if (r1 == r2) goto L_0x075a
            r34.save()
            float r1 = (float) r11
            float r2 = (float) r12
            r7.scale(r4, r4, r1, r2)
        L_0x075a:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r1 = (float) r1
            float r2 = r0.scale
            float r1 = r1 * r2
            float r2 = (float) r11
            float r9 = r2 - r1
            float r3 = (float) r12
            float r25 = r3 - r1
            float r26 = r2 + r1
            float r27 = r3 + r1
            android.graphics.Paint r6 = r0.paint
            r1 = r34
            r2 = r9
            r3 = r25
            r4 = r26
            r5 = r27
            r1.drawLine(r2, r3, r4, r5, r6)
            android.graphics.Paint r6 = r0.paint
            r2 = r26
            r4 = r9
            r1.drawLine(r2, r3, r4, r5, r6)
            int r1 = r0.currentIcon
            int r2 = r0.nextIcon
            if (r1 == r2) goto L_0x078c
            r34.restore()
        L_0x078c:
            int r1 = r0.currentIcon
            r2 = 13
            if (r1 == r2) goto L_0x0796
            int r1 = r0.nextIcon
            if (r1 != r2) goto L_0x080e
        L_0x0796:
            int r1 = r0.currentIcon
            int r3 = r0.nextIcon
            if (r1 != r3) goto L_0x079f
            r4 = 1065353216(0x3var_, float:1.0)
            goto L_0x07aa
        L_0x079f:
            if (r3 != r2) goto L_0x07a4
            float r4 = r0.transitionProgress
            goto L_0x07aa
        L_0x07a4:
            float r1 = r0.transitionProgress
            r2 = 1065353216(0x3var_, float:1.0)
            float r4 = r2 - r1
        L_0x07aa:
            android.text.TextPaint r1 = r0.textPaint
            float r2 = r4 * r22
            int r2 = (int) r2
            r1.setAlpha(r2)
            r1 = 1084227584(0x40a00000, float:5.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = r1 + r12
            int r2 = r0.percentStringWidth
            r3 = 2
            int r2 = r2 / r3
            int r2 = r11 - r2
            int r3 = r0.currentIcon
            int r5 = r0.nextIcon
            if (r3 == r5) goto L_0x07cd
            r34.save()
            float r3 = (float) r11
            float r5 = (float) r12
            r7.scale(r4, r4, r3, r5)
        L_0x07cd:
            float r3 = r0.animatedDownloadProgress
            float r3 = r3 * r17
            int r3 = (int) r3
            java.lang.String r4 = r0.percentString
            if (r4 == 0) goto L_0x07da
            int r4 = r0.lastPercent
            if (r3 == r4) goto L_0x07fc
        L_0x07da:
            r0.lastPercent = r3
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r4 = 0
            r5[r4] = r3
            java.lang.String r3 = "%d%%"
            java.lang.String r3 = java.lang.String.format(r3, r5)
            r0.percentString = r3
            android.text.TextPaint r4 = r0.textPaint
            float r3 = r4.measureText(r3)
            double r3 = (double) r3
            double r3 = java.lang.Math.ceil(r3)
            int r3 = (int) r3
            r0.percentStringWidth = r3
        L_0x07fc:
            java.lang.String r3 = r0.percentString
            float r2 = (float) r2
            float r1 = (float) r1
            android.text.TextPaint r4 = r0.textPaint
            r7.drawText(r3, r2, r1, r4)
            int r1 = r0.currentIcon
            int r2 = r0.nextIcon
            if (r1 == r2) goto L_0x080e
            r34.restore()
        L_0x080e:
            int r1 = r0.currentIcon
            r2 = 1
            if (r1 == 0) goto L_0x0820
            if (r1 == r2) goto L_0x0820
            int r1 = r0.nextIcon
            if (r1 == 0) goto L_0x0820
            if (r1 != r2) goto L_0x081c
            goto L_0x0820
        L_0x081c:
            r9 = 255(0xff, float:3.57E-43)
            goto L_0x097e
        L_0x0820:
            int r1 = r0.currentIcon
            if (r1 != 0) goto L_0x0828
            int r1 = r0.nextIcon
            if (r1 == r2) goto L_0x0830
        L_0x0828:
            int r1 = r0.currentIcon
            if (r1 != r2) goto L_0x084e
            int r1 = r0.nextIcon
            if (r1 != 0) goto L_0x084d
        L_0x0830:
            boolean r1 = r0.animatingTransition
            if (r1 == 0) goto L_0x0843
            int r1 = r0.nextIcon
            if (r1 != 0) goto L_0x083f
            float r1 = r0.transitionProgress
            r2 = 1065353216(0x3var_, float:1.0)
            float r4 = r2 - r1
            goto L_0x0841
        L_0x083f:
            float r4 = r0.transitionProgress
        L_0x0841:
            r2 = 1
            goto L_0x0853
        L_0x0843:
            int r1 = r0.nextIcon
            r2 = 1
            if (r1 != r2) goto L_0x084b
        L_0x0848:
            r4 = 1065353216(0x3var_, float:1.0)
            goto L_0x0853
        L_0x084b:
            r4 = 0
            goto L_0x0853
        L_0x084d:
            r2 = 1
        L_0x084e:
            int r1 = r0.currentIcon
            if (r1 != r2) goto L_0x084b
            goto L_0x0848
        L_0x0853:
            int r1 = r0.nextIcon
            if (r1 == 0) goto L_0x0859
            if (r1 != r2) goto L_0x085f
        L_0x0859:
            int r1 = r0.currentIcon
            if (r1 == 0) goto L_0x0887
            if (r1 == r2) goto L_0x0887
        L_0x085f:
            int r1 = r0.nextIcon
            r2 = 4
            if (r1 != r2) goto L_0x0875
            android.graphics.Paint r1 = r0.paint2
            float r2 = r0.transitionProgress
            r3 = 1065353216(0x3var_, float:1.0)
            float r2 = r3 - r2
            float r2 = r2 * r22
            int r2 = (int) r2
            r1.setAlpha(r2)
        L_0x0872:
            r9 = 255(0xff, float:3.57E-43)
            goto L_0x088e
        L_0x0875:
            android.graphics.Paint r2 = r0.paint2
            int r3 = r0.currentIcon
            if (r3 != r1) goto L_0x087e
            r1 = 255(0xff, float:3.57E-43)
            goto L_0x0883
        L_0x087e:
            float r1 = r0.transitionProgress
            float r1 = r1 * r22
            int r1 = (int) r1
        L_0x0883:
            r2.setAlpha(r1)
            goto L_0x0872
        L_0x0887:
            android.graphics.Paint r1 = r0.paint2
            r9 = 255(0xff, float:3.57E-43)
            r1.setAlpha(r9)
        L_0x088e:
            r34.save()
            int r1 = r8.centerX()
            float r1 = (float) r1
            r2 = 1065353216(0x3var_, float:1.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r3 = (float) r3
            float r5 = r2 - r4
            float r3 = r3 * r5
            float r1 = r1 + r3
            int r2 = r8.centerY()
            float r2 = (float) r2
            r7.translate(r1, r2)
            r1 = 1143373824(0x44268000, float:666.0)
            float r4 = r4 * r1
            int r1 = r0.currentIcon
            r2 = 1
            if (r1 != r2) goto L_0x08b7
            r1 = 1119092736(0x42b40000, float:90.0)
            goto L_0x08b8
        L_0x08b7:
            r1 = 0
        L_0x08b8:
            int r3 = r0.currentIcon
            if (r3 != 0) goto L_0x08c0
            int r3 = r0.nextIcon
            if (r3 == r2) goto L_0x08c8
        L_0x08c0:
            int r3 = r0.currentIcon
            if (r3 != r2) goto L_0x094f
            int r2 = r0.nextIcon
            if (r2 != 0) goto L_0x094f
        L_0x08c8:
            int r1 = (r4 > r17 ? 1 : (r4 == r17 ? 0 : -1))
            if (r1 >= 0) goto L_0x08da
            r1 = -1063256064(0xffffffffc0a00000, float:-5.0)
            org.telegram.ui.Components.CubicBezierInterpolator r2 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_BOTH
            float r3 = r4 / r17
            float r2 = r2.getInterpolation(r3)
            float r2 = r2 * r1
            r1 = r2
            goto L_0x08f6
        L_0x08da:
            r1 = 1139933184(0x43var_, float:484.0)
            int r1 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r1 >= 0) goto L_0x08f4
            r1 = -1063256064(0xffffffffc0a00000, float:-5.0)
            r2 = 1119748096(0x42be0000, float:95.0)
            org.telegram.ui.Components.CubicBezierInterpolator r3 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_BOTH
            float r5 = r4 - r17
            r6 = 1136656384(0x43CLASSNAME, float:384.0)
            float r5 = r5 / r6
            float r3 = r3.getInterpolation(r5)
            float r3 = r3 * r2
            float r3 = r3 + r1
            r1 = r3
            goto L_0x08f6
        L_0x08f4:
            r1 = 1119092736(0x42b40000, float:90.0)
        L_0x08f6:
            r2 = 1128792064(0x43480000, float:200.0)
            int r2 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r2 >= 0) goto L_0x08fd
            goto L_0x094f
        L_0x08fd:
            r2 = 1137704960(0x43d00000, float:416.0)
            int r2 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r2 >= 0) goto L_0x091a
            r2 = 1036831949(0x3dcccccd, float:0.1)
            org.telegram.ui.Components.CubicBezierInterpolator r3 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_BOTH
            r5 = 1128792064(0x43480000, float:200.0)
            float r5 = r4 - r5
            r6 = 1129840640(0x43580000, float:216.0)
            float r5 = r5 / r6
            float r3 = r3.getInterpolation(r5)
            float r3 = r3 * r2
            r2 = 1065353216(0x3var_, float:1.0)
            float r3 = r3 + r2
            r2 = r3
            goto L_0x0951
        L_0x091a:
            r2 = 1141735424(0x440d8000, float:566.0)
            int r2 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r2 >= 0) goto L_0x0938
            r2 = 1066192077(0x3f8ccccd, float:1.1)
            r3 = 1040522936(0x3e051eb8, float:0.13)
            org.telegram.ui.Components.CubicBezierInterpolator r5 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_BOTH
            r6 = 1137704960(0x43d00000, float:416.0)
            float r6 = r4 - r6
            r8 = 1125515264(0x43160000, float:150.0)
            float r6 = r6 / r8
            float r5 = r5.getInterpolation(r6)
            float r5 = r5 * r3
            float r2 = r2 - r5
            goto L_0x0951
        L_0x0938:
            r2 = 1064849900(0x3var_ec, float:0.97)
            r3 = 1022739087(0x3cf5CLASSNAMEf, float:0.03)
            android.view.animation.DecelerateInterpolator r5 = r0.interpolator
            r6 = 1141735424(0x440d8000, float:566.0)
            float r6 = r4 - r6
            float r6 = r6 / r17
            float r5 = r5.getInterpolation(r6)
            float r5 = r5 * r3
            float r2 = r2 + r5
            goto L_0x0951
        L_0x094f:
            r2 = 1065353216(0x3var_, float:1.0)
        L_0x0951:
            r7.rotate(r1)
            r7.scale(r2, r2)
            int r1 = r0.currentIcon
            if (r1 == 0) goto L_0x095e
            r2 = 1
            if (r1 != r2) goto L_0x0963
        L_0x095e:
            int r1 = r0.currentIcon
            r2 = 4
            if (r1 != r2) goto L_0x0966
        L_0x0963:
            r7.scale(r10, r10)
        L_0x0966:
            org.telegram.ui.Components.PathAnimator r1 = org.telegram.ui.ActionBar.Theme.playPauseAnimator
            android.graphics.Paint r2 = r0.paint2
            r1.draw(r7, r2, r4)
            r1 = -1082130432(0xffffffffbvar_, float:-1.0)
            r2 = 1065353216(0x3var_, float:1.0)
            r7.scale(r2, r1)
            org.telegram.ui.Components.PathAnimator r1 = org.telegram.ui.ActionBar.Theme.playPauseAnimator
            android.graphics.Paint r2 = r0.paint2
            r1.draw(r7, r2, r4)
            r34.restore()
        L_0x097e:
            int r1 = r0.currentIcon
            r2 = 6
            if (r1 == r2) goto L_0x098c
            int r1 = r0.nextIcon
            if (r1 != r2) goto L_0x0988
            goto L_0x098c
        L_0x0988:
            r17 = r10
            goto L_0x0a2a
        L_0x098c:
            int r1 = r0.currentIcon
            if (r1 == r2) goto L_0x09b4
            float r1 = r0.transitionProgress
            int r2 = (r1 > r23 ? 1 : (r1 == r23 ? 0 : -1))
            if (r2 <= 0) goto L_0x09b0
            float r1 = r1 - r23
            float r1 = r1 / r23
            float r2 = r1 / r23
            r3 = 1065353216(0x3var_, float:1.0)
            float r2 = java.lang.Math.min(r3, r2)
            float r4 = r3 - r2
            int r2 = (r1 > r23 ? 1 : (r1 == r23 ? 0 : -1))
            if (r2 <= 0) goto L_0x09ad
            float r1 = r1 - r23
            float r1 = r1 / r23
            goto L_0x09ae
        L_0x09ad:
            r1 = 0
        L_0x09ae:
            r8 = r1
            goto L_0x09b7
        L_0x09b0:
            r4 = 1065353216(0x3var_, float:1.0)
            r8 = 0
            goto L_0x09b7
        L_0x09b4:
            r4 = 0
            r8 = 1065353216(0x3var_, float:1.0)
        L_0x09b7:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r6 = r12 + r1
            r1 = 1077936128(0x40400000, float:3.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r5 = r11 - r1
            android.graphics.Paint r1 = r0.paint
            r1.setAlpha(r9)
            r1 = 1065353216(0x3var_, float:1.0)
            int r2 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r2 >= 0) goto L_0x0a06
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r1 = r5 - r1
            float r2 = (float) r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r1 = r6 - r1
            float r3 = (float) r1
            float r1 = (float) r5
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r9 = (float) r9
            float r9 = r9 * r4
            float r9 = r1 - r9
            float r1 = (float) r6
            r17 = r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r5 = (float) r5
            float r5 = r5 * r4
            float r5 = r1 - r5
            android.graphics.Paint r4 = r0.paint
            r1 = r34
            r16 = r4
            r4 = r9
            r9 = r17
            r17 = r10
            r10 = r6
            r6 = r16
            r1.drawLine(r2, r3, r4, r5, r6)
            goto L_0x0a0a
        L_0x0a06:
            r9 = r5
            r17 = r10
            r10 = r6
        L_0x0a0a:
            r1 = 0
            int r2 = (r8 > r1 ? 1 : (r8 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x0a2a
            float r2 = (float) r9
            float r3 = (float) r10
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r1 = (float) r1
            float r1 = r1 * r8
            float r4 = r2 + r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r1 = (float) r1
            float r1 = r1 * r8
            float r5 = r3 - r1
            android.graphics.Paint r6 = r0.paint
            r1 = r34
            r1.drawLine(r2, r3, r4, r5, r6)
        L_0x0a2a:
            if (r15 == 0) goto L_0x0a67
            if (r15 == r14) goto L_0x0a67
            int r1 = r15.getIntrinsicWidth()
            float r1 = (float) r1
            float r1 = r1 * r13
            int r1 = (int) r1
            int r2 = r15.getIntrinsicHeight()
            float r2 = (float) r2
            float r2 = r2 * r13
            int r2 = (int) r2
            android.graphics.ColorFilter r3 = r0.colorFilter
            r15.setColorFilter(r3)
            int r3 = r0.currentIcon
            int r4 = r0.nextIcon
            if (r3 != r4) goto L_0x0a4c
            r3 = 255(0xff, float:3.57E-43)
            goto L_0x0a55
        L_0x0a4c:
            float r3 = r0.transitionProgress
            r4 = 1065353216(0x3var_, float:1.0)
            float r3 = r4 - r3
            float r3 = r3 * r22
            int r3 = (int) r3
        L_0x0a55:
            r15.setAlpha(r3)
            r3 = 2
            int r1 = r1 / r3
            int r4 = r11 - r1
            int r2 = r2 / r3
            int r3 = r12 - r2
            int r1 = r1 + r11
            int r2 = r2 + r12
            r15.setBounds(r4, r3, r1, r2)
            r15.draw(r7)
        L_0x0a67:
            if (r14 == 0) goto L_0x0a9e
            int r1 = r14.getIntrinsicWidth()
            float r1 = (float) r1
            float r1 = r1 * r17
            int r1 = (int) r1
            int r2 = r14.getIntrinsicHeight()
            float r2 = (float) r2
            float r2 = r2 * r17
            int r2 = (int) r2
            android.graphics.ColorFilter r3 = r0.colorFilter
            r14.setColorFilter(r3)
            int r3 = r0.currentIcon
            int r4 = r0.nextIcon
            if (r3 != r4) goto L_0x0a87
            r3 = 255(0xff, float:3.57E-43)
            goto L_0x0a8c
        L_0x0a87:
            float r3 = r0.transitionProgress
            float r3 = r3 * r22
            int r3 = (int) r3
        L_0x0a8c:
            r14.setAlpha(r3)
            r3 = 2
            int r1 = r1 / r3
            int r4 = r11 - r1
            int r2 = r2 / r3
            int r3 = r12 - r2
            int r1 = r1 + r11
            int r2 = r2 + r12
            r14.setBounds(r4, r3, r1, r2)
            r14.draw(r7)
        L_0x0a9e:
            r1 = r31
            r2 = r19
            if (r1 == 0) goto L_0x0afe
            if (r1 == r2) goto L_0x0afe
            r3 = 1103101952(0x41CLASSNAME, float:24.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            float r3 = r3 * r13
            int r3 = (int) r3
            r4 = 1103101952(0x41CLASSNAME, float:24.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            float r4 = r4 * r13
            int r4 = (int) r4
            android.graphics.Paint r5 = r0.paint2
            android.graphics.Paint$Style r6 = android.graphics.Paint.Style.FILL_AND_STROKE
            r5.setStyle(r6)
            android.graphics.Paint r5 = r0.paint2
            int r6 = r0.currentIcon
            int r8 = r0.nextIcon
            if (r6 != r8) goto L_0x0acc
            r6 = 255(0xff, float:3.57E-43)
            goto L_0x0ad5
        L_0x0acc:
            float r6 = r0.transitionProgress
            r8 = 1065353216(0x3var_, float:1.0)
            float r6 = r8 - r6
            float r6 = r6 * r22
            int r6 = (int) r6
        L_0x0ad5:
            r5.setAlpha(r6)
            r34.save()
            r5 = 2
            int r3 = r3 / r5
            int r3 = r11 - r3
            float r3 = (float) r3
            int r4 = r4 / r5
            int r4 = r12 - r4
            float r4 = (float) r4
            r7.translate(r3, r4)
            r3 = 0
            r4 = r1[r3]
            android.graphics.Paint r3 = r0.paint2
            r7.drawPath(r4, r3)
            r3 = 1
            r4 = r1[r3]
            if (r4 == 0) goto L_0x0afb
            r1 = r1[r3]
            android.graphics.Paint r3 = r0.backPaint
            r7.drawPath(r1, r3)
        L_0x0afb:
            r34.restore()
        L_0x0afe:
            if (r2 == 0) goto L_0x0b52
            r1 = 1103101952(0x41CLASSNAME, float:24.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            float r1 = r1 * r17
            int r1 = (int) r1
            r3 = 1103101952(0x41CLASSNAME, float:24.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            float r3 = r3 * r17
            int r3 = (int) r3
            android.graphics.Paint r4 = r0.paint2
            android.graphics.Paint$Style r5 = android.graphics.Paint.Style.FILL_AND_STROKE
            r4.setStyle(r5)
            android.graphics.Paint r4 = r0.paint2
            int r5 = r0.currentIcon
            int r6 = r0.nextIcon
            if (r5 != r6) goto L_0x0b26
            r5 = 255(0xff, float:3.57E-43)
            goto L_0x0b2b
        L_0x0b26:
            float r5 = r0.transitionProgress
            float r5 = r5 * r22
            int r5 = (int) r5
        L_0x0b2b:
            r4.setAlpha(r5)
            r34.save()
            r4 = 2
            int r1 = r1 / r4
            int r11 = r11 - r1
            float r1 = (float) r11
            int r3 = r3 / r4
            int r12 = r12 - r3
            float r3 = (float) r12
            r7.translate(r1, r3)
            r1 = 0
            r3 = r2[r1]
            android.graphics.Paint r1 = r0.paint2
            r7.drawPath(r3, r1)
            r1 = 1
            r3 = r2[r1]
            if (r3 == 0) goto L_0x0b4f
            r2 = r2[r1]
            android.graphics.Paint r1 = r0.backPaint
            r7.drawPath(r2, r1)
        L_0x0b4f:
            r34.restore()
        L_0x0b52:
            long r1 = java.lang.System.currentTimeMillis()
            long r3 = r0.lastAnimationTime
            long r3 = r1 - r3
            r5 = 17
            int r8 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r8 <= 0) goto L_0x0b62
            r3 = 17
        L_0x0b62:
            r0.lastAnimationTime = r1
            int r1 = r0.currentIcon
            r2 = 3
            if (r1 == r2) goto L_0x0b7e
            r2 = 14
            if (r1 == r2) goto L_0x0b7e
            r5 = 4
            if (r1 != r5) goto L_0x0b74
            int r1 = r0.nextIcon
            if (r1 == r2) goto L_0x0b7e
        L_0x0b74:
            int r1 = r0.currentIcon
            r2 = 10
            if (r1 == r2) goto L_0x0b7e
            r2 = 13
            if (r1 != r2) goto L_0x0bc6
        L_0x0b7e:
            float r1 = r0.downloadRadOffset
            r5 = 360(0x168, double:1.78E-321)
            long r5 = r5 * r3
            float r2 = (float) r5
            r5 = 1159479296(0x451CLASSNAME, float:2500.0)
            float r2 = r2 / r5
            float r1 = r1 + r2
            r0.downloadRadOffset = r1
            float r1 = r0.getCircleValue(r1)
            r0.downloadRadOffset = r1
            int r1 = r0.nextIcon
            r2 = 2
            if (r1 == r2) goto L_0x0bc3
            float r1 = r0.downloadProgress
            float r2 = r0.downloadProgressAnimationStart
            float r5 = r1 - r2
            r6 = 0
            int r8 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
            if (r8 <= 0) goto L_0x0bc3
            float r8 = r0.downloadProgressTime
            float r9 = (float) r3
            float r8 = r8 + r9
            r0.downloadProgressTime = r8
            r9 = 1128792064(0x43480000, float:200.0)
            int r9 = (r8 > r9 ? 1 : (r8 == r9 ? 0 : -1))
            if (r9 < 0) goto L_0x0bb5
            r0.animatedDownloadProgress = r1
            r0.downloadProgressAnimationStart = r1
            r0.downloadProgressTime = r6
            goto L_0x0bc3
        L_0x0bb5:
            android.view.animation.DecelerateInterpolator r1 = r0.interpolator
            r6 = 1128792064(0x43480000, float:200.0)
            float r8 = r8 / r6
            float r1 = r1.getInterpolation(r8)
            float r5 = r5 * r1
            float r2 = r2 + r5
            r0.animatedDownloadProgress = r2
        L_0x0bc3:
            r33.invalidateSelf()
        L_0x0bc6:
            boolean r1 = r0.animatingTransition
            if (r1 == 0) goto L_0x0be9
            float r1 = r0.transitionProgress
            r2 = 1065353216(0x3var_, float:1.0)
            int r5 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r5 >= 0) goto L_0x0be9
            float r3 = (float) r3
            float r4 = r0.transitionAnimationTime
            float r3 = r3 / r4
            float r1 = r1 + r3
            r0.transitionProgress = r1
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 < 0) goto L_0x0be6
            int r1 = r0.nextIcon
            r0.currentIcon = r1
            r0.transitionProgress = r2
            r1 = 0
            r0.animatingTransition = r1
        L_0x0be6:
            r33.invalidateSelf()
        L_0x0be9:
            r1 = r20
            r2 = 1
            if (r1 < r2) goto L_0x0bf1
            r7.restoreToCount(r1)
        L_0x0bf1:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.MediaActionDrawable.draw(android.graphics.Canvas):void");
    }

    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(48.0f);
    }

    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(48.0f);
    }

    public int getMinimumWidth() {
        return AndroidUtilities.dp(48.0f);
    }

    public int getMinimumHeight() {
        return AndroidUtilities.dp(48.0f);
    }
}
