package org.telegram.ui.Components;

import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
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
    private LinearGradient gradientDrawable;
    private Matrix gradientMatrix;
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

    public static float getCircleValue(float f) {
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
        int i3;
        if (this.currentIcon == i && (i3 = this.nextIcon) != i) {
            this.currentIcon = i3;
            this.transitionProgress = 1.0f;
        }
        if (z) {
            int i4 = this.currentIcon;
            if (i4 == i || (i2 = this.nextIcon) == i) {
                return false;
            }
            if ((i4 == 0 && i == 1) || (i4 == 1 && i == 0)) {
                this.transitionAnimationTime = 300.0f;
            } else if (i4 == 2 && (i == 3 || i == 14)) {
                this.transitionAnimationTime = 400.0f;
            } else if (i4 != 4 && i == 6) {
                this.transitionAnimationTime = 360.0f;
            } else if ((i4 == 4 && i == 14) || (i4 == 14 && i == 4)) {
                this.transitionAnimationTime = 160.0f;
            } else {
                this.transitionAnimationTime = 220.0f;
            }
            if (this.animatingTransition) {
                this.currentIcon = i2;
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

    public void setBackgroundGradientDrawable(LinearGradient linearGradient) {
        this.gradientDrawable = linearGradient;
        this.gradientMatrix = new Matrix();
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

    /* JADX WARNING: Code restructure failed: missing block: B:332:0x0878, code lost:
        if (r0.nextIcon == 1) goto L_0x087a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:334:0x087d, code lost:
        r4 = 0.0f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:336:0x0880, code lost:
        if (r1 == 1) goto L_0x087a;
     */
    /* JADX WARNING: Removed duplicated region for block: B:101:0x036c  */
    /* JADX WARNING: Removed duplicated region for block: B:106:0x038f  */
    /* JADX WARNING: Removed duplicated region for block: B:128:0x0402  */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x040a  */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x0431  */
    /* JADX WARNING: Removed duplicated region for block: B:178:0x051d  */
    /* JADX WARNING: Removed duplicated region for block: B:181:0x0528  */
    /* JADX WARNING: Removed duplicated region for block: B:183:0x0532  */
    /* JADX WARNING: Removed duplicated region for block: B:189:0x05b0  */
    /* JADX WARNING: Removed duplicated region for block: B:192:0x05b8  */
    /* JADX WARNING: Removed duplicated region for block: B:204:0x05dc  */
    /* JADX WARNING: Removed duplicated region for block: B:205:0x05df  */
    /* JADX WARNING: Removed duplicated region for block: B:217:0x063e  */
    /* JADX WARNING: Removed duplicated region for block: B:220:0x0647  */
    /* JADX WARNING: Removed duplicated region for block: B:221:0x064c  */
    /* JADX WARNING: Removed duplicated region for block: B:227:0x0672  */
    /* JADX WARNING: Removed duplicated region for block: B:228:0x0675  */
    /* JADX WARNING: Removed duplicated region for block: B:235:0x0685  */
    /* JADX WARNING: Removed duplicated region for block: B:236:0x0688  */
    /* JADX WARNING: Removed duplicated region for block: B:241:0x0694  */
    /* JADX WARNING: Removed duplicated region for block: B:242:0x069a  */
    /* JADX WARNING: Removed duplicated region for block: B:249:0x06a9  */
    /* JADX WARNING: Removed duplicated region for block: B:250:0x06ac  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x06c4  */
    /* JADX WARNING: Removed duplicated region for block: B:258:0x06ce  */
    /* JADX WARNING: Removed duplicated region for block: B:275:0x075e  */
    /* JADX WARNING: Removed duplicated region for block: B:276:0x0761  */
    /* JADX WARNING: Removed duplicated region for block: B:282:0x0773  */
    /* JADX WARNING: Removed duplicated region for block: B:283:0x0776  */
    /* JADX WARNING: Removed duplicated region for block: B:286:0x078a  */
    /* JADX WARNING: Removed duplicated region for block: B:289:0x07c1  */
    /* JADX WARNING: Removed duplicated region for block: B:296:0x07d2  */
    /* JADX WARNING: Removed duplicated region for block: B:297:0x07d5  */
    /* JADX WARNING: Removed duplicated region for block: B:302:0x07fb  */
    /* JADX WARNING: Removed duplicated region for block: B:310:0x0841  */
    /* JADX WARNING: Removed duplicated region for block: B:313:0x0849 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:326:0x0866  */
    /* JADX WARNING: Removed duplicated region for block: B:331:0x0875  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x0109 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:354:0x08e0  */
    /* JADX WARNING: Removed duplicated region for block: B:355:0x08e3  */
    /* JADX WARNING: Removed duplicated region for block: B:359:0x08ea  */
    /* JADX WARNING: Removed duplicated region for block: B:367:0x0922 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x0173  */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x0334  */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x035d  */
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
            if (r1 == 0) goto L_0x0045
            boolean r1 = r1.hasGradient()
            if (r1 == 0) goto L_0x0045
            boolean r1 = r0.hasOverlayImage
            if (r1 != 0) goto L_0x0045
            org.telegram.ui.ActionBar.Theme$MessageDrawable r1 = r0.messageDrawable
            android.graphics.LinearGradient r1 = r1.getGradientShader()
            org.telegram.ui.ActionBar.Theme$MessageDrawable r2 = r0.messageDrawable
            android.graphics.Matrix r2 = r2.getMatrix()
            org.telegram.ui.ActionBar.Theme$MessageDrawable r3 = r0.messageDrawable
            int r3 = r3.getTopY()
            int r3 = -r3
            int r4 = r8.top
            int r3 = r3 + r4
            float r3 = (float) r3
            r2.setTranslate(r10, r3)
            r1.setLocalMatrix(r2)
            android.graphics.Paint r2 = r0.paint
            r2.setShader(r1)
            android.graphics.Paint r2 = r0.paint2
            r2.setShader(r1)
            android.graphics.Paint r2 = r0.paint3
            r2.setShader(r1)
            goto L_0x0086
        L_0x0045:
            android.graphics.LinearGradient r1 = r0.gradientDrawable
            if (r1 == 0) goto L_0x0077
            boolean r1 = r0.hasOverlayImage
            if (r1 != 0) goto L_0x0077
            android.graphics.Matrix r1 = r0.gradientMatrix
            r1.reset()
            android.graphics.Matrix r1 = r0.gradientMatrix
            int r2 = r8.top
            float r2 = (float) r2
            r1.setTranslate(r10, r2)
            android.graphics.LinearGradient r1 = r0.gradientDrawable
            android.graphics.Matrix r2 = r0.gradientMatrix
            r1.setLocalMatrix(r2)
            android.graphics.Paint r1 = r0.paint
            android.graphics.LinearGradient r2 = r0.gradientDrawable
            r1.setShader(r2)
            android.graphics.Paint r1 = r0.paint2
            android.graphics.LinearGradient r2 = r0.gradientDrawable
            r1.setShader(r2)
            android.graphics.Paint r1 = r0.paint3
            android.graphics.LinearGradient r2 = r0.gradientDrawable
            r1.setShader(r2)
            goto L_0x0086
        L_0x0077:
            android.graphics.Paint r1 = r0.paint
            r1.setShader(r9)
            android.graphics.Paint r1 = r0.paint2
            r1.setShader(r9)
            android.graphics.Paint r1 = r0.paint3
            r1.setShader(r9)
        L_0x0086:
            int r11 = r8.centerX()
            int r12 = r8.centerY()
            int r1 = r0.nextIcon
            r13 = 6
            r15 = 3
            r6 = 4
            r5 = 14
            r4 = 1065353216(0x3var_, float:1.0)
            if (r1 != r6) goto L_0x00ad
            int r1 = r0.currentIcon
            if (r1 == r15) goto L_0x00c4
            if (r1 == r5) goto L_0x00c4
            int r1 = r34.save()
            float r2 = r0.transitionProgress
            float r2 = r4 - r2
            float r3 = (float) r11
            float r9 = (float) r12
            r7.scale(r2, r2, r3, r9)
            goto L_0x00c2
        L_0x00ad:
            if (r1 == r13) goto L_0x00b3
            r2 = 10
            if (r1 != r2) goto L_0x00c4
        L_0x00b3:
            int r1 = r0.currentIcon
            if (r1 != r6) goto L_0x00c4
            int r1 = r34.save()
            float r2 = r0.transitionProgress
            float r3 = (float) r11
            float r9 = (float) r12
            r7.scale(r2, r2, r3, r9)
        L_0x00c2:
            r9 = r1
            goto L_0x00c5
        L_0x00c4:
            r9 = 0
        L_0x00c5:
            r1 = 1077936128(0x40400000, float:3.0)
            org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = r0.currentIcon
            r17 = 1094713344(0x41400000, float:12.0)
            r18 = 1120403456(0x42CLASSNAME, float:100.0)
            r19 = 1080033280(0x40600000, float:3.5)
            r20 = 1073741824(0x40000000, float:2.0)
            r21 = 1088421888(0x40e00000, float:7.0)
            r22 = 1056964608(0x3var_, float:0.5)
            r23 = 1132396544(0x437var_, float:255.0)
            r14 = 2
            if (r1 == r14) goto L_0x00e1
            int r1 = r0.nextIcon
            if (r1 != r14) goto L_0x0383
        L_0x00e1:
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
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r13 = (float) r13
            float r10 = r0.scale
            float r13 = r13 * r10
            float r13 = r13 + r1
            int r10 = r0.currentIcon
            if (r10 == r15) goto L_0x010b
            if (r10 != r5) goto L_0x0130
        L_0x010b:
            int r10 = r0.nextIcon
            if (r10 != r14) goto L_0x0130
            android.graphics.Paint r10 = r0.paint
            float r2 = r0.transitionProgress
            float r2 = r2 / r22
            float r2 = java.lang.Math.min(r4, r2)
            float r2 = r2 * r23
            int r2 = (int) r2
            r10.setAlpha(r2)
            float r2 = r0.transitionProgress
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r10 = (float) r10
            float r4 = r0.scale
            float r10 = r10 * r4
            float r10 = r10 + r1
            r25 = r10
            r10 = 1065353216(0x3var_, float:1.0)
            goto L_0x016d
        L_0x0130:
            int r2 = r0.nextIcon
            if (r2 == r15) goto L_0x0155
            if (r2 == r5) goto L_0x0155
            if (r2 == r14) goto L_0x0155
            android.graphics.Paint r2 = r0.paint
            float r4 = r0.savedTransitionProgress
            float r4 = r4 / r22
            r10 = 1065353216(0x3var_, float:1.0)
            float r4 = java.lang.Math.min(r10, r4)
            float r4 = r4 * r23
            float r5 = r0.transitionProgress
            float r5 = r10 - r5
            float r4 = r4 * r5
            int r4 = (int) r4
            r2.setAlpha(r4)
            float r2 = r0.savedTransitionProgress
            r4 = 255(0xff, float:3.57E-43)
            goto L_0x015e
        L_0x0155:
            android.graphics.Paint r2 = r0.paint
            r4 = 255(0xff, float:3.57E-43)
            r2.setAlpha(r4)
            float r2 = r0.transitionProgress
        L_0x015e:
            r10 = 1065353216(0x3var_, float:1.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r5 = (float) r5
            float r4 = r0.scale
            float r5 = r5 * r4
            float r4 = r1 + r5
            r25 = r4
        L_0x016d:
            boolean r4 = r0.animatingTransition
            r5 = 1090519040(0x41000000, float:8.0)
            if (r4 == 0) goto L_0x0334
            int r4 = r0.nextIcon
            if (r4 == r14) goto L_0x02e9
            int r26 = (r2 > r22 ? 1 : (r2 == r22 ? 0 : -1))
            if (r26 > 0) goto L_0x017d
            goto L_0x02e9
        L_0x017d:
            r3 = 1095761920(0x41500000, float:13.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            float r4 = r0.scale
            float r3 = r3 * r4
            float r3 = r3 * r4
            boolean r4 = r0.isMini
            if (r4 == 0) goto L_0x0193
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r20)
            goto L_0x0194
        L_0x0193:
            r4 = 0
        L_0x0194:
            float r4 = (float) r4
            float r3 = r3 + r4
            float r2 = r2 - r22
            float r4 = r2 / r22
            r5 = 1045220557(0x3e4ccccd, float:0.2)
            int r5 = (r2 > r5 ? 1 : (r2 == r5 ? 0 : -1))
            if (r5 <= 0) goto L_0x01ae
            r5 = 1045220557(0x3e4ccccd, float:0.2)
            float r2 = r2 - r5
            r5 = 1050253722(0x3e99999a, float:0.3)
            float r2 = r2 / r5
            r27 = r2
            r26 = 1065353216(0x3var_, float:1.0)
            goto L_0x01b6
        L_0x01ae:
            r5 = 1045220557(0x3e4ccccd, float:0.2)
            float r2 = r2 / r5
            r26 = r2
            r27 = 0
        L_0x01b6:
            android.graphics.RectF r2 = r0.rect
            float r6 = (float) r11
            float r5 = r6 - r3
            float r3 = r3 / r20
            float r10 = r13 - r3
            float r3 = r3 + r13
            r2.set(r5, r10, r6, r3)
            float r3 = r27 * r18
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
            if (r2 <= 0) goto L_0x02e2
            int r1 = r0.nextIcon
            if (r1 != r15) goto L_0x01ee
            r10 = 0
            goto L_0x01f5
        L_0x01ee:
            r1 = -1036779520(0xffffffffCLASSNAME, float:-45.0)
            float r4 = r14 - r27
            float r1 = r1 * r4
            r10 = r1
        L_0x01f5:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r1 = (float) r1
            float r1 = r1 * r27
            float r2 = r0.scale
            float r1 = r1 * r2
            float r2 = r27 * r23
            int r2 = (int) r2
            int r3 = r0.nextIcon
            r4 = 3
            if (r3 == r4) goto L_0x021b
            if (r3 == r15) goto L_0x021b
            r4 = 2
            if (r3 == r4) goto L_0x021b
            float r3 = r0.transitionProgress
            float r3 = r3 / r22
            float r3 = java.lang.Math.min(r14, r3)
            float r4 = r14 - r3
            float r2 = (float) r2
            float r2 = r2 * r4
            int r2 = (int) r2
        L_0x021b:
            r6 = r2
            r2 = 0
            int r3 = (r10 > r2 ? 1 : (r10 == r2 ? 0 : -1))
            if (r3 == 0) goto L_0x022c
            r34.save()
            r2 = r28
            r5 = r30
            r7.rotate(r10, r5, r2)
            goto L_0x0230
        L_0x022c:
            r2 = r28
            r5 = r30
        L_0x0230:
            if (r6 == 0) goto L_0x02d7
            android.graphics.Paint r3 = r0.paint
            r3.setAlpha(r6)
            int r3 = r0.nextIcon
            if (r3 != r15) goto L_0x02b7
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
            if (r1 == 0) goto L_0x0284
            r3 = 1073741824(0x40000000, float:2.0)
            goto L_0x0286
        L_0x0284:
            r3 = 1082130432(0x40800000, float:4.0)
        L_0x0286:
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
            goto L_0x02d9
        L_0x02b7:
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
            goto L_0x02d9
        L_0x02d7:
            r24 = r5
        L_0x02d9:
            r1 = 0
            int r2 = (r10 > r1 ? 1 : (r10 == r1 ? 0 : -1))
            if (r2 == 0) goto L_0x02e4
            r34.restore()
            goto L_0x02e4
        L_0x02e2:
            r24 = r30
        L_0x02e4:
            r2 = r13
            r1 = r24
            r6 = r1
            goto L_0x032d
        L_0x02e9:
            r1 = 2
            if (r4 != r1) goto L_0x02f1
            r1 = 1065353216(0x3var_, float:1.0)
            float r4 = r1 - r2
            goto L_0x02f7
        L_0x02f1:
            r1 = 1065353216(0x3var_, float:1.0)
            float r4 = r2 / r22
            float r2 = r1 - r4
        L_0x02f7:
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
        L_0x032d:
            r15 = r2
            r10 = r6
            r14 = r13
            r3 = r25
            r13 = r1
            goto L_0x0359
        L_0x0334:
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
        L_0x0359:
            int r1 = (r3 > r15 ? 1 : (r3 == r15 ? 0 : -1))
            if (r1 == 0) goto L_0x0367
            float r4 = (float) r11
            android.graphics.Paint r6 = r0.paint
            r1 = r34
            r2 = r4
            r5 = r15
            r1.drawLine(r2, r3, r4, r5, r6)
        L_0x0367:
            float r6 = (float) r11
            int r1 = (r10 > r6 ? 1 : (r10 == r6 ? 0 : -1))
            if (r1 == 0) goto L_0x0383
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
        L_0x0383:
            int r1 = r0.currentIcon
            r10 = 13
            r13 = 1
            r2 = 3
            if (r1 == r2) goto L_0x0402
            r3 = 14
            if (r1 == r3) goto L_0x0402
            r14 = 4
            if (r1 != r14) goto L_0x0399
            int r4 = r0.nextIcon
            if (r4 == r3) goto L_0x0403
            if (r4 != r2) goto L_0x0399
            goto L_0x0403
        L_0x0399:
            r2 = 10
            if (r1 == r2) goto L_0x03a5
            int r2 = r0.nextIcon
            r3 = 10
            if (r2 == r3) goto L_0x03a5
            if (r1 != r10) goto L_0x0641
        L_0x03a5:
            int r1 = r0.nextIcon
            if (r1 == r14) goto L_0x03b0
            r2 = 6
            if (r1 != r2) goto L_0x03ad
            goto L_0x03b0
        L_0x03ad:
            r2 = 255(0xff, float:3.57E-43)
            goto L_0x03b9
        L_0x03b0:
            float r1 = r0.transitionProgress
            r2 = 1065353216(0x3var_, float:1.0)
            float r4 = r2 - r1
            float r4 = r4 * r23
            int r2 = (int) r4
        L_0x03b9:
            if (r2 == 0) goto L_0x0641
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
            if (r1 == 0) goto L_0x03d9
            r3 = 1073741824(0x40000000, float:2.0)
            goto L_0x03db
        L_0x03d9:
            r3 = 1082130432(0x40800000, float:4.0)
        L_0x03db:
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
            goto L_0x0641
        L_0x0402:
            r14 = 4
        L_0x0403:
            r15 = 1082130432(0x40800000, float:4.0)
            int r2 = r0.nextIcon
            r3 = 2
            if (r2 != r3) goto L_0x0431
            float r1 = r0.transitionProgress
            int r2 = (r1 > r22 ? 1 : (r1 == r22 ? 0 : -1))
            if (r2 > 0) goto L_0x0425
            float r1 = r1 / r22
            r2 = 1065353216(0x3var_, float:1.0)
            float r4 = r2 - r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r1 = (float) r1
            float r1 = r1 * r4
            float r2 = r0.scale
            float r1 = r1 * r2
            float r4 = r4 * r23
            int r2 = (int) r4
            goto L_0x0427
        L_0x0425:
            r1 = 0
            r2 = 0
        L_0x0427:
            r6 = r2
            r2 = 1065353216(0x3var_, float:1.0)
            r3 = 0
            r4 = 0
            r5 = 1065353216(0x3var_, float:1.0)
        L_0x042e:
            r13 = 0
            goto L_0x0519
        L_0x0431:
            r3 = 15
            if (r2 == r3) goto L_0x04e6
            if (r2 == 0) goto L_0x04e6
            if (r2 == r13) goto L_0x04e6
            r3 = 5
            if (r2 == r3) goto L_0x04e6
            r3 = 8
            if (r2 == r3) goto L_0x04e6
            r3 = 9
            if (r2 == r3) goto L_0x04e6
            r3 = 7
            if (r2 == r3) goto L_0x04e6
            r3 = 6
            if (r2 != r3) goto L_0x044c
            goto L_0x04e6
        L_0x044c:
            if (r2 != r14) goto L_0x048f
            float r1 = r0.transitionProgress
            r2 = 1065353216(0x3var_, float:1.0)
            float r4 = r2 - r1
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r2 = (float) r2
            float r3 = r0.scale
            float r2 = r2 * r3
            float r3 = r4 * r23
            int r3 = (int) r3
            int r5 = r0.currentIcon
            r6 = 14
            if (r5 != r6) goto L_0x0470
            int r1 = r8.left
            float r1 = (float) r1
            int r5 = r8.top
            float r5 = (float) r5
            r6 = r5
            r5 = r1
            r1 = 0
            goto L_0x0482
        L_0x0470:
            r4 = 1110704128(0x42340000, float:45.0)
            float r1 = r1 * r4
            int r4 = r8.centerX()
            float r4 = (float) r4
            int r5 = r8.centerY()
            float r5 = (float) r5
            r6 = r5
            r5 = r4
            r4 = 1065353216(0x3var_, float:1.0)
        L_0x0482:
            r13 = r1
            r1 = r2
            r2 = 1065353216(0x3var_, float:1.0)
            r32 = r6
            r6 = r3
            r3 = r5
            r5 = r4
            r4 = r32
            goto L_0x0519
        L_0x048f:
            r3 = 14
            if (r2 == r3) goto L_0x04a9
            r3 = 3
            if (r2 != r3) goto L_0x0497
            goto L_0x04a9
        L_0x0497:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r1 = (float) r1
            float r2 = r0.scale
            float r1 = r1 * r2
            r2 = 1065353216(0x3var_, float:1.0)
            r3 = 0
            r4 = 0
            r5 = 1065353216(0x3var_, float:1.0)
            r6 = 255(0xff, float:3.57E-43)
            goto L_0x042e
        L_0x04a9:
            float r2 = r0.transitionProgress
            r3 = 1065353216(0x3var_, float:1.0)
            float r4 = r3 - r2
            if (r1 != r14) goto L_0x04b4
            r4 = r2
            r1 = 0
            goto L_0x04bb
        L_0x04b4:
            r1 = 1110704128(0x42340000, float:45.0)
            float r4 = r4 * r1
            r1 = r4
            r4 = 1065353216(0x3var_, float:1.0)
        L_0x04bb:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r3 = (float) r3
            float r5 = r0.scale
            float r3 = r3 * r5
            float r2 = r2 * r23
            int r2 = (int) r2
            int r5 = r0.nextIcon
            r6 = 14
            if (r5 != r6) goto L_0x04d3
            int r5 = r8.left
            float r5 = (float) r5
            int r6 = r8.top
            goto L_0x04dc
        L_0x04d3:
            int r5 = r8.centerX()
            float r5 = (float) r5
            int r6 = r8.centerY()
        L_0x04dc:
            float r6 = (float) r6
            r13 = r1
            r1 = r3
            r3 = r5
            r5 = r4
            r4 = r6
            r6 = r2
            r2 = 1065353216(0x3var_, float:1.0)
            goto L_0x0519
        L_0x04e6:
            r1 = 6
            if (r2 != r1) goto L_0x04f4
            float r1 = r0.transitionProgress
            float r1 = r1 / r22
            r2 = 1065353216(0x3var_, float:1.0)
            float r1 = java.lang.Math.min(r2, r1)
            goto L_0x04f8
        L_0x04f4:
            r2 = 1065353216(0x3var_, float:1.0)
            float r1 = r0.transitionProgress
        L_0x04f8:
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
            float r4 = r4 * r23
            int r4 = (int) r4
            r13 = r1
            r1 = r3
            r6 = r4
            r3 = 0
            r4 = 0
            r5 = 1065353216(0x3var_, float:1.0)
        L_0x0519:
            int r25 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1))
            if (r25 == 0) goto L_0x0523
            r34.save()
            r7.scale(r5, r5, r3, r4)
        L_0x0523:
            r2 = 0
            int r3 = (r13 > r2 ? 1 : (r13 == r2 ? 0 : -1))
            if (r3 == 0) goto L_0x0530
            r34.save()
            float r2 = (float) r11
            float r3 = (float) r12
            r7.rotate(r13, r2, r3)
        L_0x0530:
            if (r6 == 0) goto L_0x05b0
            android.graphics.Paint r2 = r0.paint
            float r3 = (float) r6
            float r4 = r0.overrideAlpha
            float r4 = r4 * r3
            int r4 = (int) r4
            r2.setAlpha(r4)
            int r2 = r0.currentIcon
            r4 = 14
            if (r2 == r4) goto L_0x0572
            int r2 = r0.nextIcon
            if (r2 != r4) goto L_0x0548
            goto L_0x0572
        L_0x0548:
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
            goto L_0x05b3
        L_0x0572:
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
            goto L_0x05b3
        L_0x05b0:
            r29 = r5
            r10 = r6
        L_0x05b3:
            r1 = 0
            int r2 = (r13 > r1 ? 1 : (r13 == r1 ? 0 : -1))
            if (r2 == 0) goto L_0x05bb
            r34.restore()
        L_0x05bb:
            int r1 = r0.currentIcon
            r2 = 3
            if (r1 == r2) goto L_0x05cc
            r3 = 14
            if (r1 == r3) goto L_0x05cc
            if (r1 != r14) goto L_0x0638
            int r1 = r0.nextIcon
            if (r1 == r3) goto L_0x05cc
            if (r1 != r2) goto L_0x0638
        L_0x05cc:
            if (r10 == 0) goto L_0x0638
            r1 = 1135869952(0x43b40000, float:360.0)
            float r2 = r0.animatedDownloadProgress
            float r2 = r2 * r1
            float r13 = java.lang.Math.max(r15, r2)
            boolean r1 = r0.isMini
            if (r1 == 0) goto L_0x05df
            r3 = 1073741824(0x40000000, float:2.0)
            goto L_0x05e1
        L_0x05df:
            r3 = 1082130432(0x40800000, float:4.0)
        L_0x05e1:
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
            if (r1 == r2) goto L_0x0609
            if (r1 != r14) goto L_0x062b
            int r1 = r0.nextIcon
            if (r1 == r2) goto L_0x0609
            r2 = 3
            if (r1 != r2) goto L_0x062b
        L_0x0609:
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
        L_0x062b:
            android.graphics.RectF r2 = r0.rect
            float r3 = r0.downloadRadOffset
            r5 = 0
            android.graphics.Paint r6 = r0.paint
            r1 = r34
            r4 = r13
            r1.drawArc(r2, r3, r4, r5, r6)
        L_0x0638:
            r1 = 1065353216(0x3var_, float:1.0)
            int r2 = (r29 > r1 ? 1 : (r29 == r1 ? 0 : -1))
            if (r2 == 0) goto L_0x0641
            r34.restore()
        L_0x0641:
            int r1 = r0.currentIcon
            int r2 = r0.nextIcon
            if (r1 != r2) goto L_0x064c
            r10 = 1065353216(0x3var_, float:1.0)
            r13 = 1065353216(0x3var_, float:1.0)
            goto L_0x066c
        L_0x064c:
            if (r1 != r14) goto L_0x0655
            float r4 = r0.transitionProgress
            r1 = 1065353216(0x3var_, float:1.0)
            float r2 = r1 - r4
            goto L_0x066a
        L_0x0655:
            r1 = 1065353216(0x3var_, float:1.0)
            float r2 = r0.transitionProgress
            float r2 = r2 / r22
            float r4 = java.lang.Math.min(r1, r2)
            float r2 = r0.transitionProgress
            float r2 = r2 / r22
            float r2 = r1 - r2
            r1 = 0
            float r2 = java.lang.Math.max(r1, r2)
        L_0x066a:
            r13 = r2
            r10 = r4
        L_0x066c:
            int r1 = r0.nextIcon
            r2 = 15
            if (r1 != r2) goto L_0x0675
            android.graphics.Path[] r2 = org.telegram.ui.ActionBar.Theme.chat_updatePath
            goto L_0x0681
        L_0x0675:
            int r2 = r0.currentIcon
            r3 = 15
            if (r2 != r3) goto L_0x0680
            android.graphics.Path[] r2 = org.telegram.ui.ActionBar.Theme.chat_updatePath
            r3 = r2
            r2 = 0
            goto L_0x0682
        L_0x0680:
            r2 = 0
        L_0x0681:
            r3 = 0
        L_0x0682:
            r4 = 5
            if (r1 != r4) goto L_0x0688
            android.graphics.Path[] r2 = org.telegram.ui.ActionBar.Theme.chat_filePath
            goto L_0x068f
        L_0x0688:
            int r4 = r0.currentIcon
            r5 = 5
            if (r4 != r5) goto L_0x068f
            android.graphics.Path[] r3 = org.telegram.ui.ActionBar.Theme.chat_filePath
        L_0x068f:
            r15 = r2
            r6 = r3
            r2 = 7
            if (r1 != r2) goto L_0x069a
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.chat_flameIcon
            r16 = r2
            r2 = 0
            goto L_0x06a5
        L_0x069a:
            int r2 = r0.currentIcon
            r3 = 7
            if (r2 != r3) goto L_0x06a2
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.chat_flameIcon
            goto L_0x06a3
        L_0x06a2:
            r2 = 0
        L_0x06a3:
            r16 = 0
        L_0x06a5:
            r3 = 8
            if (r1 != r3) goto L_0x06ac
            android.graphics.drawable.Drawable r16 = org.telegram.ui.ActionBar.Theme.chat_gifIcon
            goto L_0x06b4
        L_0x06ac:
            int r3 = r0.currentIcon
            r4 = 8
            if (r3 != r4) goto L_0x06b4
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.chat_gifIcon
        L_0x06b4:
            r5 = r2
            r4 = r16
            int r2 = r0.currentIcon
            r3 = 9
            r16 = 1086324736(0x40CLASSNAME, float:6.0)
            if (r2 == r3) goto L_0x06ce
            r3 = 9
            if (r1 != r3) goto L_0x06c4
            goto L_0x06ce
        L_0x06c4:
            r14 = r4
            r31 = r6
            r20 = r9
            r19 = r15
            r15 = r5
            goto L_0x074e
        L_0x06ce:
            android.graphics.Paint r3 = r0.paint
            if (r2 != r1) goto L_0x06d5
            r2 = 255(0xff, float:3.57E-43)
            goto L_0x06da
        L_0x06d5:
            float r1 = r0.transitionProgress
            float r1 = r1 * r23
            int r2 = (int) r1
        L_0x06da:
            r3.setAlpha(r2)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r3 = r12 + r1
            r1 = 1077936128(0x40400000, float:3.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r2 = r11 - r1
            int r1 = r0.currentIcon
            int r14 = r0.nextIcon
            if (r1 == r14) goto L_0x06fe
            r34.save()
            float r1 = r0.transitionProgress
            float r14 = (float) r11
            r19 = r4
            float r4 = (float) r12
            r7.scale(r1, r1, r14, r4)
            goto L_0x0700
        L_0x06fe:
            r19 = r4
        L_0x0700:
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
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r2 = r27 + r1
            float r4 = (float) r2
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r3 = r28 - r1
            float r5 = (float) r3
            android.graphics.Paint r6 = r0.paint
            r1 = r34
            r2 = r26
            r3 = r9
            r1.drawLine(r2, r3, r4, r5, r6)
            int r1 = r0.currentIcon
            int r2 = r0.nextIcon
            if (r1 == r2) goto L_0x074e
            r34.restore()
        L_0x074e:
            int r1 = r0.currentIcon
            r2 = 12
            if (r1 == r2) goto L_0x075a
            int r2 = r0.nextIcon
            r3 = 12
            if (r2 != r3) goto L_0x07c4
        L_0x075a:
            int r2 = r0.nextIcon
            if (r1 != r2) goto L_0x0761
            r4 = 1065353216(0x3var_, float:1.0)
            goto L_0x076f
        L_0x0761:
            r3 = 13
            if (r2 != r3) goto L_0x0768
            float r4 = r0.transitionProgress
            goto L_0x076f
        L_0x0768:
            float r3 = r0.transitionProgress
            r4 = 1065353216(0x3var_, float:1.0)
            float r3 = r4 - r3
            r4 = r3
        L_0x076f:
            android.graphics.Paint r3 = r0.paint
            if (r1 != r2) goto L_0x0776
            r2 = 255(0xff, float:3.57E-43)
            goto L_0x0779
        L_0x0776:
            float r1 = r4 * r23
            int r2 = (int) r1
        L_0x0779:
            r3.setAlpha(r2)
            org.telegram.messenger.AndroidUtilities.dp(r21)
            r1 = 1077936128(0x40400000, float:3.0)
            org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = r0.currentIcon
            int r2 = r0.nextIcon
            if (r1 == r2) goto L_0x0792
            r34.save()
            float r1 = (float) r11
            float r2 = (float) r12
            r7.scale(r4, r4, r1, r2)
        L_0x0792:
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
            if (r1 == r2) goto L_0x07c4
            r34.restore()
        L_0x07c4:
            int r1 = r0.currentIcon
            r2 = 13
            if (r1 == r2) goto L_0x07ce
            int r3 = r0.nextIcon
            if (r3 != r2) goto L_0x0844
        L_0x07ce:
            int r3 = r0.nextIcon
            if (r1 != r3) goto L_0x07d5
            r4 = 1065353216(0x3var_, float:1.0)
            goto L_0x07e0
        L_0x07d5:
            if (r3 != r2) goto L_0x07da
            float r4 = r0.transitionProgress
            goto L_0x07e0
        L_0x07da:
            float r1 = r0.transitionProgress
            r2 = 1065353216(0x3var_, float:1.0)
            float r4 = r2 - r1
        L_0x07e0:
            android.text.TextPaint r1 = r0.textPaint
            float r2 = r4 * r23
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
            if (r3 == r5) goto L_0x0803
            r34.save()
            float r3 = (float) r11
            float r5 = (float) r12
            r7.scale(r4, r4, r3, r5)
        L_0x0803:
            float r3 = r0.animatedDownloadProgress
            float r3 = r3 * r18
            int r3 = (int) r3
            java.lang.String r4 = r0.percentString
            if (r4 == 0) goto L_0x0810
            int r4 = r0.lastPercent
            if (r3 == r4) goto L_0x0832
        L_0x0810:
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
        L_0x0832:
            java.lang.String r3 = r0.percentString
            float r2 = (float) r2
            float r1 = (float) r1
            android.text.TextPaint r4 = r0.textPaint
            r7.drawText(r3, r2, r1, r4)
            int r1 = r0.currentIcon
            int r2 = r0.nextIcon
            if (r1 == r2) goto L_0x0844
            r34.restore()
        L_0x0844:
            int r1 = r0.currentIcon
            r2 = 1
            if (r1 == 0) goto L_0x0856
            if (r1 == r2) goto L_0x0856
            int r3 = r0.nextIcon
            if (r3 == 0) goto L_0x0856
            if (r3 != r2) goto L_0x0852
            goto L_0x0856
        L_0x0852:
            r9 = 255(0xff, float:3.57E-43)
            goto L_0x097c
        L_0x0856:
            if (r1 != 0) goto L_0x085c
            int r3 = r0.nextIcon
            if (r3 == r2) goto L_0x0862
        L_0x085c:
            if (r1 != r2) goto L_0x087f
            int r2 = r0.nextIcon
            if (r2 != 0) goto L_0x087f
        L_0x0862:
            boolean r2 = r0.animatingTransition
            if (r2 == 0) goto L_0x0875
            int r2 = r0.nextIcon
            if (r2 != 0) goto L_0x0871
            float r2 = r0.transitionProgress
            r3 = 1065353216(0x3var_, float:1.0)
            float r4 = r3 - r2
            goto L_0x0873
        L_0x0871:
            float r4 = r0.transitionProgress
        L_0x0873:
            r3 = 1
            goto L_0x0883
        L_0x0875:
            int r2 = r0.nextIcon
            r3 = 1
            if (r2 != r3) goto L_0x087d
        L_0x087a:
            r4 = 1065353216(0x3var_, float:1.0)
            goto L_0x0883
        L_0x087d:
            r4 = 0
            goto L_0x0883
        L_0x087f:
            r3 = 1
            if (r1 != r3) goto L_0x087d
            goto L_0x087a
        L_0x0883:
            int r2 = r0.nextIcon
            if (r2 == 0) goto L_0x088c
            if (r2 != r3) goto L_0x088a
            goto L_0x088c
        L_0x088a:
            r3 = 4
            goto L_0x0891
        L_0x088c:
            if (r1 == 0) goto L_0x08b4
            if (r1 == r3) goto L_0x08b4
            goto L_0x088a
        L_0x0891:
            if (r2 != r3) goto L_0x08a4
            android.graphics.Paint r1 = r0.paint2
            float r2 = r0.transitionProgress
            r3 = 1065353216(0x3var_, float:1.0)
            float r2 = r3 - r2
            float r2 = r2 * r23
            int r2 = (int) r2
            r1.setAlpha(r2)
        L_0x08a1:
            r9 = 255(0xff, float:3.57E-43)
            goto L_0x08bb
        L_0x08a4:
            android.graphics.Paint r3 = r0.paint2
            if (r1 != r2) goto L_0x08ab
            r2 = 255(0xff, float:3.57E-43)
            goto L_0x08b0
        L_0x08ab:
            float r1 = r0.transitionProgress
            float r1 = r1 * r23
            int r2 = (int) r1
        L_0x08b0:
            r3.setAlpha(r2)
            goto L_0x08a1
        L_0x08b4:
            android.graphics.Paint r1 = r0.paint2
            r9 = 255(0xff, float:3.57E-43)
            r1.setAlpha(r9)
        L_0x08bb:
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
            r1 = 1140457472(0x43fa0000, float:500.0)
            float r4 = r4 * r1
            int r1 = r0.currentIcon
            r2 = 1
            if (r1 != r2) goto L_0x08e3
            r3 = 1119092736(0x42b40000, float:90.0)
            goto L_0x08e4
        L_0x08e3:
            r3 = 0
        L_0x08e4:
            if (r1 != 0) goto L_0x0922
            int r5 = r0.nextIcon
            if (r5 != r2) goto L_0x0922
            r1 = 1136656384(0x43CLASSNAME, float:384.0)
            int r1 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r1 >= 0) goto L_0x0900
            r1 = 1119748096(0x42be0000, float:95.0)
            org.telegram.ui.Components.CubicBezierInterpolator r2 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_BOTH
            r3 = 1136656384(0x43CLASSNAME, float:384.0)
            float r3 = r4 / r3
            float r2 = r2.getInterpolation(r3)
            float r2 = r2 * r1
            r3 = r2
            goto L_0x091f
        L_0x0900:
            r1 = 1139933184(0x43var_, float:484.0)
            int r1 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r1 >= 0) goto L_0x091b
            r1 = 1119748096(0x42be0000, float:95.0)
            r2 = 1084227584(0x40a00000, float:5.0)
            org.telegram.ui.Components.CubicBezierInterpolator r3 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_BOTH
            r5 = 1136656384(0x43CLASSNAME, float:384.0)
            float r5 = r4 - r5
            float r5 = r5 / r18
            float r3 = r3.getInterpolation(r5)
            float r3 = r3 * r2
            float r1 = r1 - r3
            r3 = r1
            goto L_0x091f
        L_0x091b:
            r1 = 1119092736(0x42b40000, float:90.0)
            r3 = 1119092736(0x42b40000, float:90.0)
        L_0x091f:
            float r4 = r4 + r18
            goto L_0x0954
        L_0x0922:
            if (r1 != r2) goto L_0x0954
            int r1 = r0.nextIcon
            if (r1 != 0) goto L_0x0954
            int r1 = (r4 > r18 ? 1 : (r4 == r18 ? 0 : -1))
            if (r1 >= 0) goto L_0x0939
            r1 = -1063256064(0xffffffffc0a00000, float:-5.0)
            org.telegram.ui.Components.CubicBezierInterpolator r2 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_BOTH
            float r3 = r4 / r18
            float r2 = r2.getInterpolation(r3)
            float r3 = r2 * r1
            goto L_0x0954
        L_0x0939:
            r1 = 1139933184(0x43var_, float:484.0)
            int r1 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r1 >= 0) goto L_0x0952
            r1 = -1063256064(0xffffffffc0a00000, float:-5.0)
            r2 = 1119748096(0x42be0000, float:95.0)
            org.telegram.ui.Components.CubicBezierInterpolator r3 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_BOTH
            float r5 = r4 - r18
            r6 = 1136656384(0x43CLASSNAME, float:384.0)
            float r5 = r5 / r6
            float r3 = r3.getInterpolation(r5)
            float r3 = r3 * r2
            float r3 = r3 + r1
            goto L_0x0954
        L_0x0952:
            r3 = 1119092736(0x42b40000, float:90.0)
        L_0x0954:
            r7.rotate(r3)
            int r1 = r0.currentIcon
            if (r1 == 0) goto L_0x095e
            r2 = 1
            if (r1 != r2) goto L_0x0961
        L_0x095e:
            r2 = 4
            if (r1 != r2) goto L_0x0964
        L_0x0961:
            r7.scale(r10, r10)
        L_0x0964:
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
        L_0x097c:
            int r1 = r0.currentIcon
            r2 = 6
            if (r1 == r2) goto L_0x098a
            int r3 = r0.nextIcon
            if (r3 != r2) goto L_0x0986
            goto L_0x098a
        L_0x0986:
            r18 = r10
            goto L_0x0a3f
        L_0x098a:
            if (r1 == r2) goto L_0x09b5
            float r1 = r0.transitionProgress
            int r2 = (r1 > r22 ? 1 : (r1 == r22 ? 0 : -1))
            if (r2 <= 0) goto L_0x09ab
            float r1 = r1 - r22
            float r1 = r1 / r22
            float r2 = r1 / r22
            r3 = 1065353216(0x3var_, float:1.0)
            float r2 = java.lang.Math.min(r3, r2)
            float r4 = r3 - r2
            int r2 = (r1 > r22 ? 1 : (r1 == r22 ? 0 : -1))
            if (r2 <= 0) goto L_0x09a9
            float r1 = r1 - r22
            float r1 = r1 / r22
            goto L_0x09ae
        L_0x09a9:
            r1 = 0
            goto L_0x09ae
        L_0x09ab:
            r1 = 0
            r4 = 1065353216(0x3var_, float:1.0)
        L_0x09ae:
            android.graphics.Paint r2 = r0.paint
            r2.setAlpha(r9)
            r8 = r1
            goto L_0x09d1
        L_0x09b5:
            int r1 = r0.nextIcon
            r2 = 6
            if (r1 == r2) goto L_0x09c9
            android.graphics.Paint r1 = r0.paint
            float r2 = r0.transitionProgress
            r3 = 1065353216(0x3var_, float:1.0)
            float r4 = r3 - r2
            float r4 = r4 * r23
            int r2 = (int) r4
            r1.setAlpha(r2)
            goto L_0x09ce
        L_0x09c9:
            android.graphics.Paint r1 = r0.paint
            r1.setAlpha(r9)
        L_0x09ce:
            r4 = 0
            r8 = 1065353216(0x3var_, float:1.0)
        L_0x09d1:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r6 = r12 + r1
            r1 = 1077936128(0x40400000, float:3.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r5 = r11 - r1
            r1 = 1065353216(0x3var_, float:1.0)
            int r2 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r2 >= 0) goto L_0x0a1b
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
            r18 = r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r5 = (float) r5
            float r5 = r5 * r4
            float r5 = r1 - r5
            android.graphics.Paint r4 = r0.paint
            r1 = r34
            r16 = r4
            r4 = r9
            r9 = r18
            r18 = r10
            r10 = r6
            r6 = r16
            r1.drawLine(r2, r3, r4, r5, r6)
            goto L_0x0a1f
        L_0x0a1b:
            r9 = r5
            r18 = r10
            r10 = r6
        L_0x0a1f:
            r1 = 0
            int r2 = (r8 > r1 ? 1 : (r8 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x0a3f
            float r2 = (float) r9
            float r3 = (float) r10
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r1 = (float) r1
            float r1 = r1 * r8
            float r4 = r2 + r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r1 = (float) r1
            float r1 = r1 * r8
            float r5 = r3 - r1
            android.graphics.Paint r6 = r0.paint
            r1 = r34
            r1.drawLine(r2, r3, r4, r5, r6)
        L_0x0a3f:
            if (r15 == 0) goto L_0x0a7c
            if (r15 == r14) goto L_0x0a7c
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
            if (r3 != r4) goto L_0x0a61
            r3 = 255(0xff, float:3.57E-43)
            goto L_0x0a6a
        L_0x0a61:
            float r3 = r0.transitionProgress
            r4 = 1065353216(0x3var_, float:1.0)
            float r3 = r4 - r3
            float r3 = r3 * r23
            int r3 = (int) r3
        L_0x0a6a:
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
        L_0x0a7c:
            if (r14 == 0) goto L_0x0ab3
            int r1 = r14.getIntrinsicWidth()
            float r1 = (float) r1
            float r1 = r1 * r18
            int r1 = (int) r1
            int r2 = r14.getIntrinsicHeight()
            float r2 = (float) r2
            float r2 = r2 * r18
            int r2 = (int) r2
            android.graphics.ColorFilter r3 = r0.colorFilter
            r14.setColorFilter(r3)
            int r3 = r0.currentIcon
            int r4 = r0.nextIcon
            if (r3 != r4) goto L_0x0a9c
            r3 = 255(0xff, float:3.57E-43)
            goto L_0x0aa1
        L_0x0a9c:
            float r3 = r0.transitionProgress
            float r3 = r3 * r23
            int r3 = (int) r3
        L_0x0aa1:
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
        L_0x0ab3:
            r3 = r31
            r2 = r19
            if (r3 == 0) goto L_0x0b17
            if (r3 == r2) goto L_0x0b17
            r1 = 1103101952(0x41CLASSNAME, float:24.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            float r1 = r1 * r13
            int r1 = (int) r1
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
            if (r6 != r8) goto L_0x0ae1
            r6 = 255(0xff, float:3.57E-43)
            goto L_0x0aea
        L_0x0ae1:
            float r6 = r0.transitionProgress
            r8 = 1065353216(0x3var_, float:1.0)
            float r6 = r8 - r6
            float r6 = r6 * r23
            int r6 = (int) r6
        L_0x0aea:
            r5.setAlpha(r6)
            r34.save()
            r5 = 2
            int r1 = r1 / r5
            int r1 = r11 - r1
            float r1 = (float) r1
            int r4 = r4 / r5
            int r4 = r12 - r4
            float r4 = (float) r4
            r7.translate(r1, r4)
            r1 = 0
            r4 = r3[r1]
            if (r4 == 0) goto L_0x0b08
            r4 = r3[r1]
            android.graphics.Paint r1 = r0.paint2
            r7.drawPath(r4, r1)
        L_0x0b08:
            r1 = 1
            r4 = r3[r1]
            if (r4 == 0) goto L_0x0b14
            r3 = r3[r1]
            android.graphics.Paint r1 = r0.backPaint
            r7.drawPath(r3, r1)
        L_0x0b14:
            r34.restore()
        L_0x0b17:
            if (r2 == 0) goto L_0x0ba4
            r1 = 1103101952(0x41CLASSNAME, float:24.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            float r1 = r1 * r18
            int r1 = (int) r1
            r3 = 1103101952(0x41CLASSNAME, float:24.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            float r3 = r3 * r18
            int r3 = (int) r3
            int r4 = r0.currentIcon
            int r5 = r0.nextIcon
            if (r4 != r5) goto L_0x0b36
            r4 = 255(0xff, float:3.57E-43)
            goto L_0x0b3b
        L_0x0b36:
            float r4 = r0.transitionProgress
            float r4 = r4 * r23
            int r4 = (int) r4
        L_0x0b3b:
            android.graphics.Paint r5 = r0.paint2
            android.graphics.Paint$Style r6 = android.graphics.Paint.Style.FILL_AND_STROKE
            r5.setStyle(r6)
            android.graphics.Paint r5 = r0.paint2
            r5.setAlpha(r4)
            r34.save()
            r5 = 2
            int r1 = r1 / r5
            int r11 = r11 - r1
            float r1 = (float) r11
            int r3 = r3 / r5
            int r12 = r12 - r3
            float r3 = (float) r12
            r7.translate(r1, r3)
            r1 = 0
            r3 = r2[r1]
            if (r3 == 0) goto L_0x0b60
            r3 = r2[r1]
            android.graphics.Paint r1 = r0.paint2
            r7.drawPath(r3, r1)
        L_0x0b60:
            int r1 = r2.length
            r3 = 3
            if (r1 < r3) goto L_0x0b70
            r1 = 2
            r3 = r2[r1]
            if (r3 == 0) goto L_0x0b70
            r3 = r2[r1]
            android.graphics.Paint r1 = r0.paint
            r7.drawPath(r3, r1)
        L_0x0b70:
            r1 = 1
            r3 = r2[r1]
            if (r3 == 0) goto L_0x0ba1
            r1 = 255(0xff, float:3.57E-43)
            if (r4 == r1) goto L_0x0b99
            android.graphics.Paint r1 = r0.backPaint
            int r1 = r1.getAlpha()
            android.graphics.Paint r3 = r0.backPaint
            float r5 = (float) r1
            float r4 = (float) r4
            float r4 = r4 / r23
            float r5 = r5 * r4
            int r4 = (int) r5
            r3.setAlpha(r4)
            r3 = 1
            r2 = r2[r3]
            android.graphics.Paint r4 = r0.backPaint
            r7.drawPath(r2, r4)
            android.graphics.Paint r2 = r0.backPaint
            r2.setAlpha(r1)
            goto L_0x0ba1
        L_0x0b99:
            r3 = 1
            r1 = r2[r3]
            android.graphics.Paint r2 = r0.backPaint
            r7.drawPath(r1, r2)
        L_0x0ba1:
            r34.restore()
        L_0x0ba4:
            long r1 = java.lang.System.currentTimeMillis()
            long r3 = r0.lastAnimationTime
            long r3 = r1 - r3
            r5 = 17
            int r8 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r8 <= 0) goto L_0x0bb4
            r3 = 17
        L_0x0bb4:
            r0.lastAnimationTime = r1
            int r1 = r0.currentIcon
            r2 = 3
            if (r1 == r2) goto L_0x0bce
            r2 = 14
            if (r1 == r2) goto L_0x0bce
            r5 = 4
            if (r1 != r5) goto L_0x0bc6
            int r5 = r0.nextIcon
            if (r5 == r2) goto L_0x0bce
        L_0x0bc6:
            r2 = 10
            if (r1 == r2) goto L_0x0bce
            r2 = 13
            if (r1 != r2) goto L_0x0CLASSNAME
        L_0x0bce:
            float r1 = r0.downloadRadOffset
            r5 = 360(0x168, double:1.78E-321)
            long r5 = r5 * r3
            float r2 = (float) r5
            r5 = 1159479296(0x451CLASSNAME, float:2500.0)
            float r2 = r2 / r5
            float r1 = r1 + r2
            r0.downloadRadOffset = r1
            float r1 = getCircleValue(r1)
            r0.downloadRadOffset = r1
            int r1 = r0.nextIcon
            r2 = 2
            if (r1 == r2) goto L_0x0CLASSNAME
            float r1 = r0.downloadProgress
            float r2 = r0.downloadProgressAnimationStart
            float r5 = r1 - r2
            r6 = 0
            int r8 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
            if (r8 <= 0) goto L_0x0CLASSNAME
            float r8 = r0.downloadProgressTime
            float r9 = (float) r3
            float r8 = r8 + r9
            r0.downloadProgressTime = r8
            r9 = 1128792064(0x43480000, float:200.0)
            int r9 = (r8 > r9 ? 1 : (r8 == r9 ? 0 : -1))
            if (r9 < 0) goto L_0x0CLASSNAME
            r0.animatedDownloadProgress = r1
            r0.downloadProgressAnimationStart = r1
            r0.downloadProgressTime = r6
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            android.view.animation.DecelerateInterpolator r1 = r0.interpolator
            r6 = 1128792064(0x43480000, float:200.0)
            float r8 = r8 / r6
            float r1 = r1.getInterpolation(r8)
            float r5 = r5 * r1
            float r2 = r2 + r5
            r0.animatedDownloadProgress = r2
        L_0x0CLASSNAME:
            r33.invalidateSelf()
        L_0x0CLASSNAME:
            boolean r1 = r0.animatingTransition
            if (r1 == 0) goto L_0x0CLASSNAME
            float r1 = r0.transitionProgress
            r2 = 1065353216(0x3var_, float:1.0)
            int r5 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r5 >= 0) goto L_0x0CLASSNAME
            float r3 = (float) r3
            float r4 = r0.transitionAnimationTime
            float r3 = r3 / r4
            float r1 = r1 + r3
            r0.transitionProgress = r1
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 < 0) goto L_0x0CLASSNAME
            int r1 = r0.nextIcon
            r0.currentIcon = r1
            r0.transitionProgress = r2
            r1 = 0
            r0.animatingTransition = r1
        L_0x0CLASSNAME:
            r33.invalidateSelf()
        L_0x0CLASSNAME:
            r1 = r20
            r2 = 1
            if (r1 < r2) goto L_0x0CLASSNAME
            r7.restoreToCount(r1)
        L_0x0CLASSNAME:
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
