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

    /* JADX WARNING: Code restructure failed: missing block: B:319:0x0831, code lost:
        if (r0.nextIcon == 1) goto L_0x0833;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:321:0x0836, code lost:
        r4 = 0.0f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:323:0x0839, code lost:
        if (r1 != 1) goto L_0x0836;
     */
    /* JADX WARNING: Removed duplicated region for block: B:101:0x035c  */
    /* JADX WARNING: Removed duplicated region for block: B:123:0x03cf  */
    /* JADX WARNING: Removed duplicated region for block: B:126:0x03d7  */
    /* JADX WARNING: Removed duplicated region for block: B:132:0x03fe  */
    /* JADX WARNING: Removed duplicated region for block: B:171:0x04e6  */
    /* JADX WARNING: Removed duplicated region for block: B:174:0x04f1  */
    /* JADX WARNING: Removed duplicated region for block: B:176:0x04fb  */
    /* JADX WARNING: Removed duplicated region for block: B:182:0x0579  */
    /* JADX WARNING: Removed duplicated region for block: B:185:0x0581  */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x05a5  */
    /* JADX WARNING: Removed duplicated region for block: B:198:0x05a8  */
    /* JADX WARNING: Removed duplicated region for block: B:210:0x0607  */
    /* JADX WARNING: Removed duplicated region for block: B:213:0x0610  */
    /* JADX WARNING: Removed duplicated region for block: B:214:0x0615  */
    /* JADX WARNING: Removed duplicated region for block: B:220:0x063a  */
    /* JADX WARNING: Removed duplicated region for block: B:221:0x063f  */
    /* JADX WARNING: Removed duplicated region for block: B:228:0x064d  */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x0653  */
    /* JADX WARNING: Removed duplicated region for block: B:236:0x0662  */
    /* JADX WARNING: Removed duplicated region for block: B:237:0x0665  */
    /* JADX WARNING: Removed duplicated region for block: B:244:0x067d  */
    /* JADX WARNING: Removed duplicated region for block: B:245:0x0687  */
    /* JADX WARNING: Removed duplicated region for block: B:262:0x0717  */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x071a  */
    /* JADX WARNING: Removed duplicated region for block: B:269:0x072c  */
    /* JADX WARNING: Removed duplicated region for block: B:270:0x072f  */
    /* JADX WARNING: Removed duplicated region for block: B:273:0x0743  */
    /* JADX WARNING: Removed duplicated region for block: B:276:0x077a  */
    /* JADX WARNING: Removed duplicated region for block: B:283:0x078b  */
    /* JADX WARNING: Removed duplicated region for block: B:284:0x078e  */
    /* JADX WARNING: Removed duplicated region for block: B:289:0x07b4  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x00d7 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:297:0x07fa  */
    /* JADX WARNING: Removed duplicated region for block: B:300:0x0802 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:313:0x081f  */
    /* JADX WARNING: Removed duplicated region for block: B:318:0x082e  */
    /* JADX WARNING: Removed duplicated region for block: B:341:0x0899  */
    /* JADX WARNING: Removed duplicated region for block: B:342:0x089c  */
    /* JADX WARNING: Removed duplicated region for block: B:346:0x08a3  */
    /* JADX WARNING: Removed duplicated region for block: B:354:0x08db A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x0141  */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x0301  */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x032a  */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x0339  */
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
            goto L_0x0054
        L_0x0045:
            android.graphics.Paint r1 = r0.paint
            r1.setShader(r9)
            android.graphics.Paint r1 = r0.paint2
            r1.setShader(r9)
            android.graphics.Paint r1 = r0.paint3
            r1.setShader(r9)
        L_0x0054:
            int r11 = r8.centerX()
            int r12 = r8.centerY()
            int r1 = r0.nextIcon
            r14 = 6
            r15 = 3
            r6 = 4
            r5 = 14
            r4 = 1065353216(0x3var_, float:1.0)
            if (r1 != r6) goto L_0x007b
            int r1 = r0.currentIcon
            if (r1 == r15) goto L_0x0092
            if (r1 == r5) goto L_0x0092
            int r1 = r34.save()
            float r2 = r0.transitionProgress
            float r2 = r4 - r2
            float r3 = (float) r11
            float r9 = (float) r12
            r7.scale(r2, r2, r3, r9)
            goto L_0x0090
        L_0x007b:
            if (r1 == r14) goto L_0x0081
            r2 = 10
            if (r1 != r2) goto L_0x0092
        L_0x0081:
            int r1 = r0.currentIcon
            if (r1 != r6) goto L_0x0092
            int r1 = r34.save()
            float r2 = r0.transitionProgress
            float r3 = (float) r11
            float r9 = (float) r12
            r7.scale(r2, r2, r3, r9)
        L_0x0090:
            r9 = r1
            goto L_0x0093
        L_0x0092:
            r9 = 0
        L_0x0093:
            r1 = 1077936128(0x40400000, float:3.0)
            org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = r0.currentIcon
            r17 = 1094713344(0x41400000, float:12.0)
            r18 = 1120403456(0x42CLASSNAME, float:100.0)
            r19 = 1080033280(0x40600000, float:3.5)
            r20 = 1073741824(0x40000000, float:2.0)
            r21 = 1088421888(0x40e00000, float:7.0)
            r22 = 1132396544(0x437var_, float:255.0)
            r13 = 2
            r23 = 1056964608(0x3var_, float:0.5)
            if (r1 == r13) goto L_0x00af
            int r1 = r0.nextIcon
            if (r1 != r13) goto L_0x0350
        L_0x00af:
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
            float r14 = r0.scale
            float r6 = r6 * r14
            float r6 = r6 + r1
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r14 = (float) r14
            float r10 = r0.scale
            float r14 = r14 * r10
            float r14 = r14 + r1
            int r10 = r0.currentIcon
            if (r10 == r15) goto L_0x00d9
            if (r10 != r5) goto L_0x00fe
        L_0x00d9:
            int r10 = r0.nextIcon
            if (r10 != r13) goto L_0x00fe
            android.graphics.Paint r10 = r0.paint
            float r2 = r0.transitionProgress
            float r2 = r2 / r23
            float r2 = java.lang.Math.min(r4, r2)
            float r2 = r2 * r22
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
            goto L_0x013b
        L_0x00fe:
            int r2 = r0.nextIcon
            if (r2 == r15) goto L_0x0123
            if (r2 == r5) goto L_0x0123
            if (r2 == r13) goto L_0x0123
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
            goto L_0x012c
        L_0x0123:
            android.graphics.Paint r2 = r0.paint
            r4 = 255(0xff, float:3.57E-43)
            r2.setAlpha(r4)
            float r2 = r0.transitionProgress
        L_0x012c:
            r10 = 1065353216(0x3var_, float:1.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r5 = (float) r5
            float r4 = r0.scale
            float r5 = r5 * r4
            float r4 = r1 + r5
            r25 = r4
        L_0x013b:
            boolean r4 = r0.animatingTransition
            r5 = 1090519040(0x41000000, float:8.0)
            if (r4 == 0) goto L_0x0301
            int r4 = r0.nextIcon
            if (r4 == r13) goto L_0x02b7
            int r26 = (r2 > r23 ? 1 : (r2 == r23 ? 0 : -1))
            if (r26 > 0) goto L_0x014b
            goto L_0x02b7
        L_0x014b:
            r3 = 1095761920(0x41500000, float:13.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            float r4 = r0.scale
            float r3 = r3 * r4
            float r3 = r3 * r4
            boolean r4 = r0.isMini
            if (r4 == 0) goto L_0x0161
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r20)
            goto L_0x0162
        L_0x0161:
            r4 = 0
        L_0x0162:
            float r4 = (float) r4
            float r3 = r3 + r4
            float r2 = r2 - r23
            float r4 = r2 / r23
            r5 = 1045220557(0x3e4ccccd, float:0.2)
            int r5 = (r2 > r5 ? 1 : (r2 == r5 ? 0 : -1))
            if (r5 <= 0) goto L_0x017c
            r5 = 1045220557(0x3e4ccccd, float:0.2)
            float r2 = r2 - r5
            r5 = 1050253722(0x3e99999a, float:0.3)
            float r2 = r2 / r5
            r27 = r2
            r26 = 1065353216(0x3var_, float:1.0)
            goto L_0x0184
        L_0x017c:
            r5 = 1045220557(0x3e4ccccd, float:0.2)
            float r2 = r2 / r5
            r26 = r2
            r27 = 0
        L_0x0184:
            android.graphics.RectF r2 = r0.rect
            float r6 = (float) r11
            float r5 = r6 - r3
            float r3 = r3 / r20
            float r10 = r14 - r3
            float r3 = r3 + r14
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
            r13 = 1065353216(0x3var_, float:1.0)
            r15 = 14
            r30 = r6
            r6 = r10
            r1.drawArc(r2, r3, r4, r5, r6)
            float r1 = r14 - r25
            float r1 = r1 * r26
            float r25 = r25 + r1
            r1 = 0
            int r2 = (r27 > r1 ? 1 : (r27 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x02b0
            int r1 = r0.nextIcon
            if (r1 != r15) goto L_0x01bc
            r10 = 0
            goto L_0x01c3
        L_0x01bc:
            r1 = -1036779520(0xffffffffCLASSNAME, float:-45.0)
            float r4 = r13 - r27
            float r1 = r1 * r4
            r10 = r1
        L_0x01c3:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r1 = (float) r1
            float r1 = r1 * r27
            float r2 = r0.scale
            float r1 = r1 * r2
            float r2 = r27 * r22
            int r2 = (int) r2
            int r3 = r0.nextIcon
            r4 = 3
            if (r3 == r4) goto L_0x01e9
            if (r3 == r15) goto L_0x01e9
            r4 = 2
            if (r3 == r4) goto L_0x01e9
            float r3 = r0.transitionProgress
            float r3 = r3 / r23
            float r3 = java.lang.Math.min(r13, r3)
            float r4 = r13 - r3
            float r2 = (float) r2
            float r2 = r2 * r4
            int r2 = (int) r2
        L_0x01e9:
            r6 = r2
            r2 = 0
            int r3 = (r10 > r2 ? 1 : (r10 == r2 ? 0 : -1))
            if (r3 == 0) goto L_0x01fa
            r34.save()
            r2 = r28
            r5 = r30
            r7.rotate(r10, r5, r2)
            goto L_0x01fe
        L_0x01fa:
            r2 = r28
            r5 = r30
        L_0x01fe:
            if (r6 == 0) goto L_0x02a5
            android.graphics.Paint r3 = r0.paint
            r3.setAlpha(r6)
            int r3 = r0.nextIcon
            if (r3 != r15) goto L_0x0285
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
            if (r1 == 0) goto L_0x0252
            r3 = 1073741824(0x40000000, float:2.0)
            goto L_0x0254
        L_0x0252:
            r3 = 1082130432(0x40800000, float:4.0)
        L_0x0254:
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
            int r13 = r8.bottom
            int r13 = r13 - r1
            float r1 = (float) r13
            r2.set(r3, r4, r15, r1)
            android.graphics.RectF r2 = r0.rect
            r3 = 0
            r4 = 1135869952(0x43b40000, float:360.0)
            r13 = 0
            android.graphics.Paint r15 = r0.paint
            r1 = r34
            r24 = r5
            r5 = r13
            r13 = r6
            r6 = r15
            r1.drawArc(r2, r3, r4, r5, r6)
            android.graphics.Paint r1 = r0.paint
            r1.setAlpha(r13)
            goto L_0x02a7
        L_0x0285:
            r24 = r5
            float r13 = r24 - r1
            float r15 = r2 - r1
            float r26 = r24 + r1
            float r27 = r2 + r1
            android.graphics.Paint r6 = r0.paint
            r1 = r34
            r2 = r13
            r3 = r15
            r4 = r26
            r5 = r27
            r1.drawLine(r2, r3, r4, r5, r6)
            android.graphics.Paint r6 = r0.paint
            r2 = r26
            r4 = r13
            r1.drawLine(r2, r3, r4, r5, r6)
            goto L_0x02a7
        L_0x02a5:
            r24 = r5
        L_0x02a7:
            r1 = 0
            int r2 = (r10 > r1 ? 1 : (r10 == r1 ? 0 : -1))
            if (r2 == 0) goto L_0x02b2
            r34.restore()
            goto L_0x02b2
        L_0x02b0:
            r24 = r30
        L_0x02b2:
            r2 = r14
            r1 = r24
            r6 = r1
            goto L_0x02fb
        L_0x02b7:
            r1 = 2
            if (r4 != r1) goto L_0x02bf
            r1 = 1065353216(0x3var_, float:1.0)
            float r4 = r1 - r2
            goto L_0x02c5
        L_0x02bf:
            r1 = 1065353216(0x3var_, float:1.0)
            float r4 = r2 / r23
            float r2 = r1 - r4
        L_0x02c5:
            float r25 = r25 - r3
            float r25 = r25 * r4
            float r25 = r3 + r25
            float r14 = r14 - r6
            float r14 = r14 * r4
            float r14 = r14 + r6
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
            float r2 = r14 - r3
            r32 = r14
            r14 = r2
            r2 = r32
        L_0x02fb:
            r13 = r1
            r15 = r2
            r10 = r6
            r3 = r25
            goto L_0x0326
        L_0x0301:
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
        L_0x0326:
            int r1 = (r3 > r15 ? 1 : (r3 == r15 ? 0 : -1))
            if (r1 == 0) goto L_0x0334
            float r4 = (float) r11
            android.graphics.Paint r6 = r0.paint
            r1 = r34
            r2 = r4
            r5 = r15
            r1.drawLine(r2, r3, r4, r5, r6)
        L_0x0334:
            float r6 = (float) r11
            int r1 = (r10 > r6 ? 1 : (r10 == r6 ? 0 : -1))
            if (r1 == 0) goto L_0x0350
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
        L_0x0350:
            int r1 = r0.currentIcon
            r10 = 13
            r13 = 1
            r2 = 3
            if (r1 == r2) goto L_0x03cf
            r3 = 14
            if (r1 == r3) goto L_0x03cf
            r14 = 4
            if (r1 != r14) goto L_0x0366
            int r4 = r0.nextIcon
            if (r4 == r3) goto L_0x03d0
            if (r4 != r2) goto L_0x0366
            goto L_0x03d0
        L_0x0366:
            r2 = 10
            if (r1 == r2) goto L_0x0372
            int r2 = r0.nextIcon
            r3 = 10
            if (r2 == r3) goto L_0x0372
            if (r1 != r10) goto L_0x060a
        L_0x0372:
            int r1 = r0.nextIcon
            if (r1 == r14) goto L_0x037d
            r2 = 6
            if (r1 != r2) goto L_0x037a
            goto L_0x037d
        L_0x037a:
            r2 = 255(0xff, float:3.57E-43)
            goto L_0x0386
        L_0x037d:
            float r1 = r0.transitionProgress
            r2 = 1065353216(0x3var_, float:1.0)
            float r4 = r2 - r1
            float r4 = r4 * r22
            int r2 = (int) r4
        L_0x0386:
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
            if (r1 == 0) goto L_0x03a6
            r3 = 1073741824(0x40000000, float:2.0)
            goto L_0x03a8
        L_0x03a6:
            r3 = 1082130432(0x40800000, float:4.0)
        L_0x03a8:
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
        L_0x03cf:
            r14 = 4
        L_0x03d0:
            r15 = 1082130432(0x40800000, float:4.0)
            int r2 = r0.nextIcon
            r3 = 2
            if (r2 != r3) goto L_0x03fe
            float r1 = r0.transitionProgress
            int r2 = (r1 > r23 ? 1 : (r1 == r23 ? 0 : -1))
            if (r2 > 0) goto L_0x03f2
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
            goto L_0x03f4
        L_0x03f2:
            r1 = 0
            r2 = 0
        L_0x03f4:
            r6 = r2
            r2 = 1065353216(0x3var_, float:1.0)
            r3 = 0
            r4 = 0
            r5 = 1065353216(0x3var_, float:1.0)
        L_0x03fb:
            r13 = 0
            goto L_0x04e2
        L_0x03fe:
            if (r2 == 0) goto L_0x04af
            if (r2 == r13) goto L_0x04af
            r3 = 5
            if (r2 == r3) goto L_0x04af
            r3 = 8
            if (r2 == r3) goto L_0x04af
            r3 = 9
            if (r2 == r3) goto L_0x04af
            r3 = 7
            if (r2 == r3) goto L_0x04af
            r3 = 6
            if (r2 != r3) goto L_0x0415
            goto L_0x04af
        L_0x0415:
            if (r2 != r14) goto L_0x0458
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
            if (r5 != r6) goto L_0x0439
            int r1 = r8.left
            float r1 = (float) r1
            int r5 = r8.top
            float r5 = (float) r5
            r6 = r5
            r5 = r1
            r1 = 0
            goto L_0x044b
        L_0x0439:
            r4 = 1110704128(0x42340000, float:45.0)
            float r1 = r1 * r4
            int r4 = r8.centerX()
            float r4 = (float) r4
            int r5 = r8.centerY()
            float r5 = (float) r5
            r6 = r5
            r5 = r4
            r4 = 1065353216(0x3var_, float:1.0)
        L_0x044b:
            r13 = r1
            r1 = r2
            r2 = 1065353216(0x3var_, float:1.0)
            r32 = r6
            r6 = r3
            r3 = r5
            r5 = r4
            r4 = r32
            goto L_0x04e2
        L_0x0458:
            r3 = 14
            if (r2 == r3) goto L_0x0472
            r3 = 3
            if (r2 != r3) goto L_0x0460
            goto L_0x0472
        L_0x0460:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r1 = (float) r1
            float r2 = r0.scale
            float r1 = r1 * r2
            r2 = 1065353216(0x3var_, float:1.0)
            r3 = 0
            r4 = 0
            r5 = 1065353216(0x3var_, float:1.0)
            r6 = 255(0xff, float:3.57E-43)
            goto L_0x03fb
        L_0x0472:
            float r2 = r0.transitionProgress
            r3 = 1065353216(0x3var_, float:1.0)
            float r4 = r3 - r2
            if (r1 != r14) goto L_0x047d
            r4 = r2
            r1 = 0
            goto L_0x0484
        L_0x047d:
            r1 = 1110704128(0x42340000, float:45.0)
            float r4 = r4 * r1
            r1 = r4
            r4 = 1065353216(0x3var_, float:1.0)
        L_0x0484:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r3 = (float) r3
            float r5 = r0.scale
            float r3 = r3 * r5
            float r2 = r2 * r22
            int r2 = (int) r2
            int r5 = r0.nextIcon
            r6 = 14
            if (r5 != r6) goto L_0x049c
            int r5 = r8.left
            float r5 = (float) r5
            int r6 = r8.top
            goto L_0x04a5
        L_0x049c:
            int r5 = r8.centerX()
            float r5 = (float) r5
            int r6 = r8.centerY()
        L_0x04a5:
            float r6 = (float) r6
            r13 = r1
            r1 = r3
            r3 = r5
            r5 = r4
            r4 = r6
            r6 = r2
            r2 = 1065353216(0x3var_, float:1.0)
            goto L_0x04e2
        L_0x04af:
            r1 = 6
            if (r2 != r1) goto L_0x04bd
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
            android.graphics.Path[] r2 = org.telegram.ui.ActionBar.Theme.chat_filePath
            r15 = r2
            r6 = 0
            goto L_0x064a
        L_0x063f:
            int r2 = r0.currentIcon
            r3 = 5
            if (r2 != r3) goto L_0x0648
            android.graphics.Path[] r2 = org.telegram.ui.ActionBar.Theme.chat_filePath
            r6 = r2
            goto L_0x0649
        L_0x0648:
            r6 = 0
        L_0x0649:
            r15 = 0
        L_0x064a:
            r2 = 7
            if (r1 != r2) goto L_0x0653
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.chat_flameIcon
            r16 = r2
            r2 = 0
            goto L_0x065e
        L_0x0653:
            int r2 = r0.currentIcon
            r3 = 7
            if (r2 != r3) goto L_0x065b
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.chat_flameIcon
            goto L_0x065c
        L_0x065b:
            r2 = 0
        L_0x065c:
            r16 = 0
        L_0x065e:
            r3 = 8
            if (r1 != r3) goto L_0x0665
            android.graphics.drawable.Drawable r16 = org.telegram.ui.ActionBar.Theme.chat_gifIcon
            goto L_0x066d
        L_0x0665:
            int r3 = r0.currentIcon
            r4 = 8
            if (r3 != r4) goto L_0x066d
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.chat_gifIcon
        L_0x066d:
            r5 = r2
            r4 = r16
            int r2 = r0.currentIcon
            r3 = 9
            r16 = 1086324736(0x40CLASSNAME, float:6.0)
            if (r2 == r3) goto L_0x0687
            r3 = 9
            if (r1 != r3) goto L_0x067d
            goto L_0x0687
        L_0x067d:
            r14 = r4
            r31 = r6
            r20 = r9
            r19 = r15
            r15 = r5
            goto L_0x0707
        L_0x0687:
            android.graphics.Paint r3 = r0.paint
            if (r2 != r1) goto L_0x068e
            r2 = 255(0xff, float:3.57E-43)
            goto L_0x0693
        L_0x068e:
            float r1 = r0.transitionProgress
            float r1 = r1 * r22
            int r2 = (int) r1
        L_0x0693:
            r3.setAlpha(r2)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r3 = r12 + r1
            r1 = 1077936128(0x40400000, float:3.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r2 = r11 - r1
            int r1 = r0.currentIcon
            int r14 = r0.nextIcon
            if (r1 == r14) goto L_0x06b7
            r34.save()
            float r1 = r0.transitionProgress
            float r14 = (float) r11
            r19 = r4
            float r4 = (float) r12
            r7.scale(r1, r1, r14, r4)
            goto L_0x06b9
        L_0x06b7:
            r19 = r4
        L_0x06b9:
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
            if (r1 == r2) goto L_0x0707
            r34.restore()
        L_0x0707:
            int r1 = r0.currentIcon
            r2 = 12
            if (r1 == r2) goto L_0x0713
            int r2 = r0.nextIcon
            r3 = 12
            if (r2 != r3) goto L_0x077d
        L_0x0713:
            int r2 = r0.nextIcon
            if (r1 != r2) goto L_0x071a
            r4 = 1065353216(0x3var_, float:1.0)
            goto L_0x0728
        L_0x071a:
            r3 = 13
            if (r2 != r3) goto L_0x0721
            float r4 = r0.transitionProgress
            goto L_0x0728
        L_0x0721:
            float r3 = r0.transitionProgress
            r4 = 1065353216(0x3var_, float:1.0)
            float r3 = r4 - r3
            r4 = r3
        L_0x0728:
            android.graphics.Paint r3 = r0.paint
            if (r1 != r2) goto L_0x072f
            r2 = 255(0xff, float:3.57E-43)
            goto L_0x0732
        L_0x072f:
            float r1 = r4 * r22
            int r2 = (int) r1
        L_0x0732:
            r3.setAlpha(r2)
            org.telegram.messenger.AndroidUtilities.dp(r21)
            r1 = 1077936128(0x40400000, float:3.0)
            org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = r0.currentIcon
            int r2 = r0.nextIcon
            if (r1 == r2) goto L_0x074b
            r34.save()
            float r1 = (float) r11
            float r2 = (float) r12
            r7.scale(r4, r4, r1, r2)
        L_0x074b:
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
            if (r1 == r2) goto L_0x077d
            r34.restore()
        L_0x077d:
            int r1 = r0.currentIcon
            r2 = 13
            if (r1 == r2) goto L_0x0787
            int r3 = r0.nextIcon
            if (r3 != r2) goto L_0x07fd
        L_0x0787:
            int r3 = r0.nextIcon
            if (r1 != r3) goto L_0x078e
            r4 = 1065353216(0x3var_, float:1.0)
            goto L_0x0799
        L_0x078e:
            if (r3 != r2) goto L_0x0793
            float r4 = r0.transitionProgress
            goto L_0x0799
        L_0x0793:
            float r1 = r0.transitionProgress
            r2 = 1065353216(0x3var_, float:1.0)
            float r4 = r2 - r1
        L_0x0799:
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
            if (r3 == r5) goto L_0x07bc
            r34.save()
            float r3 = (float) r11
            float r5 = (float) r12
            r7.scale(r4, r4, r3, r5)
        L_0x07bc:
            float r3 = r0.animatedDownloadProgress
            float r3 = r3 * r18
            int r3 = (int) r3
            java.lang.String r4 = r0.percentString
            if (r4 == 0) goto L_0x07c9
            int r4 = r0.lastPercent
            if (r3 == r4) goto L_0x07eb
        L_0x07c9:
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
        L_0x07eb:
            java.lang.String r3 = r0.percentString
            float r2 = (float) r2
            float r1 = (float) r1
            android.text.TextPaint r4 = r0.textPaint
            r7.drawText(r3, r2, r1, r4)
            int r1 = r0.currentIcon
            int r2 = r0.nextIcon
            if (r1 == r2) goto L_0x07fd
            r34.restore()
        L_0x07fd:
            int r1 = r0.currentIcon
            r2 = 1
            if (r1 == 0) goto L_0x080f
            if (r1 == r2) goto L_0x080f
            int r3 = r0.nextIcon
            if (r3 == 0) goto L_0x080f
            if (r3 != r2) goto L_0x080b
            goto L_0x080f
        L_0x080b:
            r9 = 255(0xff, float:3.57E-43)
            goto L_0x0935
        L_0x080f:
            if (r1 != 0) goto L_0x0815
            int r3 = r0.nextIcon
            if (r3 == r2) goto L_0x081b
        L_0x0815:
            if (r1 != r2) goto L_0x0838
            int r2 = r0.nextIcon
            if (r2 != 0) goto L_0x0838
        L_0x081b:
            boolean r2 = r0.animatingTransition
            if (r2 == 0) goto L_0x082e
            int r2 = r0.nextIcon
            if (r2 != 0) goto L_0x082a
            float r2 = r0.transitionProgress
            r3 = 1065353216(0x3var_, float:1.0)
            float r4 = r3 - r2
            goto L_0x082c
        L_0x082a:
            float r4 = r0.transitionProgress
        L_0x082c:
            r3 = 1
            goto L_0x083c
        L_0x082e:
            int r2 = r0.nextIcon
            r3 = 1
            if (r2 != r3) goto L_0x0836
        L_0x0833:
            r4 = 1065353216(0x3var_, float:1.0)
            goto L_0x083c
        L_0x0836:
            r4 = 0
            goto L_0x083c
        L_0x0838:
            r3 = 1
            if (r1 != r3) goto L_0x0836
            goto L_0x0833
        L_0x083c:
            int r2 = r0.nextIcon
            if (r2 == 0) goto L_0x0845
            if (r2 != r3) goto L_0x0843
            goto L_0x0845
        L_0x0843:
            r3 = 4
            goto L_0x084a
        L_0x0845:
            if (r1 == 0) goto L_0x086d
            if (r1 == r3) goto L_0x086d
            goto L_0x0843
        L_0x084a:
            if (r2 != r3) goto L_0x085d
            android.graphics.Paint r1 = r0.paint2
            float r2 = r0.transitionProgress
            r3 = 1065353216(0x3var_, float:1.0)
            float r2 = r3 - r2
            float r2 = r2 * r22
            int r2 = (int) r2
            r1.setAlpha(r2)
        L_0x085a:
            r9 = 255(0xff, float:3.57E-43)
            goto L_0x0874
        L_0x085d:
            android.graphics.Paint r3 = r0.paint2
            if (r1 != r2) goto L_0x0864
            r2 = 255(0xff, float:3.57E-43)
            goto L_0x0869
        L_0x0864:
            float r1 = r0.transitionProgress
            float r1 = r1 * r22
            int r2 = (int) r1
        L_0x0869:
            r3.setAlpha(r2)
            goto L_0x085a
        L_0x086d:
            android.graphics.Paint r1 = r0.paint2
            r9 = 255(0xff, float:3.57E-43)
            r1.setAlpha(r9)
        L_0x0874:
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
            if (r1 != r2) goto L_0x089c
            r3 = 1119092736(0x42b40000, float:90.0)
            goto L_0x089d
        L_0x089c:
            r3 = 0
        L_0x089d:
            if (r1 != 0) goto L_0x08db
            int r5 = r0.nextIcon
            if (r5 != r2) goto L_0x08db
            r1 = 1136656384(0x43CLASSNAME, float:384.0)
            int r1 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r1 >= 0) goto L_0x08b9
            r1 = 1119748096(0x42be0000, float:95.0)
            org.telegram.ui.Components.CubicBezierInterpolator r2 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_BOTH
            r3 = 1136656384(0x43CLASSNAME, float:384.0)
            float r3 = r4 / r3
            float r2 = r2.getInterpolation(r3)
            float r2 = r2 * r1
            r3 = r2
            goto L_0x08d8
        L_0x08b9:
            r1 = 1139933184(0x43var_, float:484.0)
            int r1 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r1 >= 0) goto L_0x08d4
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
            goto L_0x08d8
        L_0x08d4:
            r1 = 1119092736(0x42b40000, float:90.0)
            r3 = 1119092736(0x42b40000, float:90.0)
        L_0x08d8:
            float r4 = r4 + r18
            goto L_0x090d
        L_0x08db:
            if (r1 != r2) goto L_0x090d
            int r1 = r0.nextIcon
            if (r1 != 0) goto L_0x090d
            int r1 = (r4 > r18 ? 1 : (r4 == r18 ? 0 : -1))
            if (r1 >= 0) goto L_0x08f2
            r1 = -1063256064(0xffffffffc0a00000, float:-5.0)
            org.telegram.ui.Components.CubicBezierInterpolator r2 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_BOTH
            float r3 = r4 / r18
            float r2 = r2.getInterpolation(r3)
            float r3 = r2 * r1
            goto L_0x090d
        L_0x08f2:
            r1 = 1139933184(0x43var_, float:484.0)
            int r1 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r1 >= 0) goto L_0x090b
            r1 = -1063256064(0xffffffffc0a00000, float:-5.0)
            r2 = 1119748096(0x42be0000, float:95.0)
            org.telegram.ui.Components.CubicBezierInterpolator r3 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_BOTH
            float r5 = r4 - r18
            r6 = 1136656384(0x43CLASSNAME, float:384.0)
            float r5 = r5 / r6
            float r3 = r3.getInterpolation(r5)
            float r3 = r3 * r2
            float r3 = r3 + r1
            goto L_0x090d
        L_0x090b:
            r3 = 1119092736(0x42b40000, float:90.0)
        L_0x090d:
            r7.rotate(r3)
            int r1 = r0.currentIcon
            if (r1 == 0) goto L_0x0917
            r2 = 1
            if (r1 != r2) goto L_0x091a
        L_0x0917:
            r2 = 4
            if (r1 != r2) goto L_0x091d
        L_0x091a:
            r7.scale(r10, r10)
        L_0x091d:
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
        L_0x0935:
            int r1 = r0.currentIcon
            r2 = 6
            if (r1 == r2) goto L_0x0943
            int r3 = r0.nextIcon
            if (r3 != r2) goto L_0x093f
            goto L_0x0943
        L_0x093f:
            r18 = r10
            goto L_0x09f8
        L_0x0943:
            if (r1 == r2) goto L_0x096e
            float r1 = r0.transitionProgress
            int r2 = (r1 > r23 ? 1 : (r1 == r23 ? 0 : -1))
            if (r2 <= 0) goto L_0x0964
            float r1 = r1 - r23
            float r1 = r1 / r23
            float r2 = r1 / r23
            r3 = 1065353216(0x3var_, float:1.0)
            float r2 = java.lang.Math.min(r3, r2)
            float r4 = r3 - r2
            int r2 = (r1 > r23 ? 1 : (r1 == r23 ? 0 : -1))
            if (r2 <= 0) goto L_0x0962
            float r1 = r1 - r23
            float r1 = r1 / r23
            goto L_0x0967
        L_0x0962:
            r1 = 0
            goto L_0x0967
        L_0x0964:
            r1 = 0
            r4 = 1065353216(0x3var_, float:1.0)
        L_0x0967:
            android.graphics.Paint r2 = r0.paint
            r2.setAlpha(r9)
            r8 = r1
            goto L_0x098a
        L_0x096e:
            int r1 = r0.nextIcon
            r2 = 6
            if (r1 == r2) goto L_0x0982
            android.graphics.Paint r1 = r0.paint
            float r2 = r0.transitionProgress
            r3 = 1065353216(0x3var_, float:1.0)
            float r4 = r3 - r2
            float r4 = r4 * r22
            int r2 = (int) r4
            r1.setAlpha(r2)
            goto L_0x0987
        L_0x0982:
            android.graphics.Paint r1 = r0.paint
            r1.setAlpha(r9)
        L_0x0987:
            r4 = 0
            r8 = 1065353216(0x3var_, float:1.0)
        L_0x098a:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r6 = r12 + r1
            r1 = 1077936128(0x40400000, float:3.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r5 = r11 - r1
            r1 = 1065353216(0x3var_, float:1.0)
            int r2 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r2 >= 0) goto L_0x09d4
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
            goto L_0x09d8
        L_0x09d4:
            r9 = r5
            r18 = r10
            r10 = r6
        L_0x09d8:
            r1 = 0
            int r2 = (r8 > r1 ? 1 : (r8 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x09f8
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
        L_0x09f8:
            if (r15 == 0) goto L_0x0a35
            if (r15 == r14) goto L_0x0a35
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
            if (r3 != r4) goto L_0x0a1a
            r3 = 255(0xff, float:3.57E-43)
            goto L_0x0a23
        L_0x0a1a:
            float r3 = r0.transitionProgress
            r4 = 1065353216(0x3var_, float:1.0)
            float r3 = r4 - r3
            float r3 = r3 * r22
            int r3 = (int) r3
        L_0x0a23:
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
        L_0x0a35:
            if (r14 == 0) goto L_0x0a6c
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
            if (r3 != r4) goto L_0x0a55
            r3 = 255(0xff, float:3.57E-43)
            goto L_0x0a5a
        L_0x0a55:
            float r3 = r0.transitionProgress
            float r3 = r3 * r22
            int r3 = (int) r3
        L_0x0a5a:
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
        L_0x0a6c:
            r2 = r31
            r1 = r19
            if (r2 == 0) goto L_0x0acc
            if (r2 == r1) goto L_0x0acc
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
            if (r6 != r8) goto L_0x0a9a
            r6 = 255(0xff, float:3.57E-43)
            goto L_0x0aa3
        L_0x0a9a:
            float r6 = r0.transitionProgress
            r8 = 1065353216(0x3var_, float:1.0)
            float r6 = r8 - r6
            float r6 = r6 * r22
            int r6 = (int) r6
        L_0x0aa3:
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
            r4 = r2[r3]
            android.graphics.Paint r3 = r0.paint2
            r7.drawPath(r4, r3)
            r3 = 1
            r4 = r2[r3]
            if (r4 == 0) goto L_0x0ac9
            r2 = r2[r3]
            android.graphics.Paint r3 = r0.backPaint
            r7.drawPath(r2, r3)
        L_0x0ac9:
            r34.restore()
        L_0x0acc:
            if (r1 == 0) goto L_0x0b20
            r2 = 1103101952(0x41CLASSNAME, float:24.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            float r2 = r2 * r18
            int r2 = (int) r2
            r3 = 1103101952(0x41CLASSNAME, float:24.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            float r3 = r3 * r18
            int r3 = (int) r3
            android.graphics.Paint r4 = r0.paint2
            android.graphics.Paint$Style r5 = android.graphics.Paint.Style.FILL_AND_STROKE
            r4.setStyle(r5)
            android.graphics.Paint r4 = r0.paint2
            int r5 = r0.currentIcon
            int r6 = r0.nextIcon
            if (r5 != r6) goto L_0x0af4
            r5 = 255(0xff, float:3.57E-43)
            goto L_0x0af9
        L_0x0af4:
            float r5 = r0.transitionProgress
            float r5 = r5 * r22
            int r5 = (int) r5
        L_0x0af9:
            r4.setAlpha(r5)
            r34.save()
            r4 = 2
            int r2 = r2 / r4
            int r11 = r11 - r2
            float r2 = (float) r11
            int r3 = r3 / r4
            int r12 = r12 - r3
            float r3 = (float) r12
            r7.translate(r2, r3)
            r2 = 0
            r3 = r1[r2]
            android.graphics.Paint r2 = r0.paint2
            r7.drawPath(r3, r2)
            r2 = 1
            r3 = r1[r2]
            if (r3 == 0) goto L_0x0b1d
            r1 = r1[r2]
            android.graphics.Paint r2 = r0.backPaint
            r7.drawPath(r1, r2)
        L_0x0b1d:
            r34.restore()
        L_0x0b20:
            long r1 = java.lang.System.currentTimeMillis()
            long r3 = r0.lastAnimationTime
            long r3 = r1 - r3
            r5 = 17
            int r8 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r8 <= 0) goto L_0x0b30
            r3 = 17
        L_0x0b30:
            r0.lastAnimationTime = r1
            int r1 = r0.currentIcon
            r2 = 3
            if (r1 == r2) goto L_0x0b4a
            r2 = 14
            if (r1 == r2) goto L_0x0b4a
            r5 = 4
            if (r1 != r5) goto L_0x0b42
            int r5 = r0.nextIcon
            if (r5 == r2) goto L_0x0b4a
        L_0x0b42:
            r2 = 10
            if (r1 == r2) goto L_0x0b4a
            r2 = 13
            if (r1 != r2) goto L_0x0b92
        L_0x0b4a:
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
            if (r1 == r2) goto L_0x0b8f
            float r1 = r0.downloadProgress
            float r2 = r0.downloadProgressAnimationStart
            float r5 = r1 - r2
            r6 = 0
            int r8 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
            if (r8 <= 0) goto L_0x0b8f
            float r8 = r0.downloadProgressTime
            float r9 = (float) r3
            float r8 = r8 + r9
            r0.downloadProgressTime = r8
            r9 = 1128792064(0x43480000, float:200.0)
            int r9 = (r8 > r9 ? 1 : (r8 == r9 ? 0 : -1))
            if (r9 < 0) goto L_0x0b81
            r0.animatedDownloadProgress = r1
            r0.downloadProgressAnimationStart = r1
            r0.downloadProgressTime = r6
            goto L_0x0b8f
        L_0x0b81:
            android.view.animation.DecelerateInterpolator r1 = r0.interpolator
            r6 = 1128792064(0x43480000, float:200.0)
            float r8 = r8 / r6
            float r1 = r1.getInterpolation(r8)
            float r5 = r5 * r1
            float r2 = r2 + r5
            r0.animatedDownloadProgress = r2
        L_0x0b8f:
            r33.invalidateSelf()
        L_0x0b92:
            boolean r1 = r0.animatingTransition
            if (r1 == 0) goto L_0x0bb5
            float r1 = r0.transitionProgress
            r2 = 1065353216(0x3var_, float:1.0)
            int r5 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r5 >= 0) goto L_0x0bb5
            float r3 = (float) r3
            float r4 = r0.transitionAnimationTime
            float r3 = r3 / r4
            float r1 = r1 + r3
            r0.transitionProgress = r1
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 < 0) goto L_0x0bb2
            int r1 = r0.nextIcon
            r0.currentIcon = r1
            r0.transitionProgress = r2
            r1 = 0
            r0.animatingTransition = r1
        L_0x0bb2:
            r33.invalidateSelf()
        L_0x0bb5:
            r1 = r20
            r2 = 1
            if (r1 < r2) goto L_0x0bbd
            r7.restoreToCount(r1)
        L_0x0bbd:
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
