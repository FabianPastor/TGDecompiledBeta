package org.telegram.ui.Components;

import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.view.animation.DecelerateInterpolator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class MediaActionDrawable extends Drawable {
    private static final float CANCEL_TO_CHECK_STAGE1 = 0.5f;
    private static final float CANCEL_TO_CHECK_STAGE2 = 0.5f;
    private static final float DOWNLOAD_TO_CANCEL_STAGE1 = 0.5f;
    private static final float DOWNLOAD_TO_CANCEL_STAGE2 = 0.2f;
    private static final float DOWNLOAD_TO_CANCEL_STAGE3 = 0.3f;
    private static final float EPS = 0.001f;
    public static final int ICON_CANCEL = 3;
    public static final int ICON_CANCEL_FILL = 14;
    public static final int ICON_CANCEL_NOPROFRESS = 12;
    public static final int ICON_CANCEL_PERCENT = 13;
    public static final int ICON_CHECK = 6;
    public static final int ICON_DOWNLOAD = 2;
    public static final int ICON_EMPTY = 10;
    public static final int ICON_EMPTY_NOPROGRESS = 11;
    public static final int ICON_FILE = 5;
    public static final int ICON_FIRE = 7;
    public static final int ICON_GIF = 8;
    public static final int ICON_NONE = 4;
    public static final int ICON_PAUSE = 1;
    public static final int ICON_PLAY = 0;
    public static final int ICON_SECRETCHECK = 9;
    public static final int ICON_UPDATE = 15;
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

    public void setAlpha(int alpha) {
    }

    public void setOverrideAlpha(float alpha) {
        this.overrideAlpha = alpha;
    }

    public void setColorFilter(ColorFilter colorFilter2) {
        this.paint.setColorFilter(colorFilter2);
        this.paint2.setColorFilter(colorFilter2);
        this.paint3.setColorFilter(colorFilter2);
        this.textPaint.setColorFilter(colorFilter2);
    }

    public void setColor(int value) {
        this.paint.setColor(value | -16777216);
        this.paint2.setColor(value | -16777216);
        this.paint3.setColor(value | -16777216);
        this.textPaint.setColor(-16777216 | value);
        this.colorFilter = new PorterDuffColorFilter(value, PorterDuff.Mode.MULTIPLY);
    }

    public void setBackColor(int value) {
        this.backPaint.setColor(-16777216 | value);
    }

    public int getColor() {
        return this.paint.getColor();
    }

    public void setMini(boolean value) {
        this.isMini = value;
        this.paint.setStrokeWidth((float) AndroidUtilities.dp(value ? 2.0f : 3.0f));
    }

    public int getOpacity() {
        return -2;
    }

    public void setDelegate(MediaActionDrawableDelegate mediaActionDrawableDelegate) {
        this.delegate = mediaActionDrawableDelegate;
    }

    public boolean setIcon(int icon, boolean animated) {
        int i;
        int i2;
        if (this.currentIcon == icon && (i2 = this.nextIcon) != icon) {
            this.currentIcon = i2;
            this.transitionProgress = 1.0f;
        }
        if (animated) {
            int i3 = this.currentIcon;
            if (i3 == icon || (i = this.nextIcon) == icon) {
                return false;
            }
            if ((i3 == 0 && icon == 1) || (i3 == 1 && icon == 0)) {
                this.transitionAnimationTime = 300.0f;
            } else if (i3 == 2 && (icon == 3 || icon == 14)) {
                this.transitionAnimationTime = 400.0f;
            } else if (i3 != 4 && icon == 6) {
                this.transitionAnimationTime = 360.0f;
            } else if ((i3 == 4 && icon == 14) || (i3 == 14 && icon == 4)) {
                this.transitionAnimationTime = 160.0f;
            } else {
                this.transitionAnimationTime = 220.0f;
            }
            if (this.animatingTransition) {
                this.currentIcon = i;
            }
            this.animatingTransition = true;
            this.nextIcon = icon;
            this.savedTransitionProgress = this.transitionProgress;
            this.transitionProgress = 0.0f;
        } else if (this.currentIcon == icon) {
            return false;
        } else {
            this.animatingTransition = false;
            this.nextIcon = icon;
            this.currentIcon = icon;
            this.savedTransitionProgress = this.transitionProgress;
            this.transitionProgress = 1.0f;
        }
        if (icon == 3 || icon == 14) {
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

    public void setProgress(float value, boolean animated) {
        if (!animated) {
            this.animatedDownloadProgress = value;
            this.downloadProgressAnimationStart = value;
        } else {
            if (this.animatedDownloadProgress > value) {
                this.animatedDownloadProgress = value;
            }
            this.downloadProgressAnimationStart = this.animatedDownloadProgress;
        }
        this.downloadProgress = value;
        this.downloadProgressTime = 0.0f;
        invalidateSelf();
    }

    public float getProgress() {
        return this.downloadProgress;
    }

    public static float getCircleValue(float value) {
        while (value > 360.0f) {
            value -= 360.0f;
        }
        return value;
    }

    public float getProgressAlpha() {
        return 1.0f - this.transitionProgress;
    }

    public float getTransitionProgress() {
        if (this.animatingTransition) {
            return this.transitionProgress;
        }
        return 1.0f;
    }

    public void setBackgroundDrawable(Theme.MessageDrawable drawable) {
        this.messageDrawable = drawable;
    }

    public void setBackgroundGradientDrawable(LinearGradient drawable) {
        this.gradientDrawable = drawable;
        this.gradientMatrix = new Matrix();
    }

    public void setHasOverlayImage(boolean value) {
        this.hasOverlayImage = value;
    }

    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        float intrinsicWidth = ((float) (right - left)) / ((float) getIntrinsicWidth());
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

    private void applyShaderMatrix(boolean path) {
        Theme.MessageDrawable messageDrawable2 = this.messageDrawable;
        if (messageDrawable2 != null && messageDrawable2.hasGradient() && !this.hasOverlayImage) {
            Rect bounds = getBounds();
            Shader shader = this.messageDrawable.getGradientShader();
            Matrix matrix = this.messageDrawable.getMatrix();
            matrix.reset();
            this.messageDrawable.applyMatrixScale();
            if (path) {
                matrix.postTranslate((float) (-bounds.centerX()), (float) ((-this.messageDrawable.getTopY()) + bounds.top));
            } else {
                matrix.postTranslate(0.0f, (float) (-this.messageDrawable.getTopY()));
            }
            shader.setLocalMatrix(matrix);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:332:0x0957, code lost:
        if (r0.nextIcon != 1) goto L_0x095b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:354:0x0989, code lost:
        if (r2 == 1) goto L_0x098d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:490:0x0cee, code lost:
        if (r0.nextIcon != 14) goto L_0x0cf3;
     */
    /* JADX WARNING: Removed duplicated region for block: B:104:0x03b9  */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x0438  */
    /* JADX WARNING: Removed duplicated region for block: B:130:0x044b  */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x0476  */
    /* JADX WARNING: Removed duplicated region for block: B:177:0x05a7  */
    /* JADX WARNING: Removed duplicated region for block: B:180:0x05b2  */
    /* JADX WARNING: Removed duplicated region for block: B:182:0x05bc  */
    /* JADX WARNING: Removed duplicated region for block: B:190:0x0655  */
    /* JADX WARNING: Removed duplicated region for block: B:193:0x0662  */
    /* JADX WARNING: Removed duplicated region for block: B:196:0x066b  */
    /* JADX WARNING: Removed duplicated region for block: B:199:0x0672  */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x069c  */
    /* JADX WARNING: Removed duplicated region for block: B:213:0x069f  */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x0701  */
    /* JADX WARNING: Removed duplicated region for block: B:227:0x0710  */
    /* JADX WARNING: Removed duplicated region for block: B:228:0x0717  */
    /* JADX WARNING: Removed duplicated region for block: B:240:0x074c  */
    /* JADX WARNING: Removed duplicated region for block: B:241:0x074f  */
    /* JADX WARNING: Removed duplicated region for block: B:246:0x075c  */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x0761  */
    /* JADX WARNING: Removed duplicated region for block: B:253:0x0772  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x0775  */
    /* JADX WARNING: Removed duplicated region for block: B:259:0x0782  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x0787  */
    /* JADX WARNING: Removed duplicated region for block: B:268:0x07a3  */
    /* JADX WARNING: Removed duplicated region for block: B:269:0x07ae  */
    /* JADX WARNING: Removed duplicated region for block: B:285:0x0844  */
    /* JADX WARNING: Removed duplicated region for block: B:286:0x0848  */
    /* JADX WARNING: Removed duplicated region for block: B:292:0x085b  */
    /* JADX WARNING: Removed duplicated region for block: B:293:0x085e  */
    /* JADX WARNING: Removed duplicated region for block: B:296:0x0878  */
    /* JADX WARNING: Removed duplicated region for block: B:299:0x08b5  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x00cf  */
    /* JADX WARNING: Removed duplicated region for block: B:306:0x08cc  */
    /* JADX WARNING: Removed duplicated region for block: B:307:0x08cf  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x00d3  */
    /* JADX WARNING: Removed duplicated region for block: B:313:0x08f7  */
    /* JADX WARNING: Removed duplicated region for block: B:321:0x093d  */
    /* JADX WARNING: Removed duplicated region for block: B:331:0x0954  */
    /* JADX WARNING: Removed duplicated region for block: B:333:0x095a  */
    /* JADX WARNING: Removed duplicated region for block: B:349:0x0980  */
    /* JADX WARNING: Removed duplicated region for block: B:350:0x0983  */
    /* JADX WARNING: Removed duplicated region for block: B:353:0x0988  */
    /* JADX WARNING: Removed duplicated region for block: B:355:0x098c  */
    /* JADX WARNING: Removed duplicated region for block: B:369:0x09e7  */
    /* JADX WARNING: Removed duplicated region for block: B:370:0x09ea  */
    /* JADX WARNING: Removed duplicated region for block: B:374:0x09f2  */
    /* JADX WARNING: Removed duplicated region for block: B:382:0x0a29  */
    /* JADX WARNING: Removed duplicated region for block: B:405:0x0a8f  */
    /* JADX WARNING: Removed duplicated region for block: B:406:0x0a93  */
    /* JADX WARNING: Removed duplicated region for block: B:430:0x0b52  */
    /* JADX WARNING: Removed duplicated region for block: B:437:0x0b95  */
    /* JADX WARNING: Removed duplicated region for block: B:439:0x0b99  */
    /* JADX WARNING: Removed duplicated region for block: B:445:0x0bd5  */
    /* JADX WARNING: Removed duplicated region for block: B:459:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:461:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:482:0x0cd8  */
    /* JADX WARNING: Removed duplicated region for block: B:487:0x0ce5  */
    /* JADX WARNING: Removed duplicated region for block: B:497:0x0d01  */
    /* JADX WARNING: Removed duplicated region for block: B:500:0x0d1e  */
    /* JADX WARNING: Removed duplicated region for block: B:507:0x0d51  */
    /* JADX WARNING: Removed duplicated region for block: B:511:0x0d5a  */
    /* JADX WARNING: Removed duplicated region for block: B:519:0x0d7e  */
    /* JADX WARNING: Removed duplicated region for block: B:521:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void draw(android.graphics.Canvas r45) {
        /*
            r44 = this;
            r0 = r44
            r7 = r45
            android.graphics.Rect r8 = r44.getBounds()
            org.telegram.ui.ActionBar.Theme$MessageDrawable r1 = r0.messageDrawable
            r9 = 0
            if (r1 == 0) goto L_0x002d
            boolean r1 = r1.hasGradient()
            if (r1 == 0) goto L_0x002d
            boolean r1 = r0.hasOverlayImage
            if (r1 != 0) goto L_0x002d
            org.telegram.ui.ActionBar.Theme$MessageDrawable r1 = r0.messageDrawable
            android.graphics.Shader r1 = r1.getGradientShader()
            android.graphics.Paint r2 = r0.paint
            r2.setShader(r1)
            android.graphics.Paint r2 = r0.paint2
            r2.setShader(r1)
            android.graphics.Paint r2 = r0.paint3
            r2.setShader(r1)
            goto L_0x006f
        L_0x002d:
            android.graphics.LinearGradient r1 = r0.gradientDrawable
            if (r1 == 0) goto L_0x005f
            boolean r1 = r0.hasOverlayImage
            if (r1 != 0) goto L_0x005f
            android.graphics.Matrix r1 = r0.gradientMatrix
            r1.reset()
            android.graphics.Matrix r1 = r0.gradientMatrix
            int r2 = r8.top
            float r2 = (float) r2
            r1.setTranslate(r9, r2)
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
            goto L_0x006f
        L_0x005f:
            android.graphics.Paint r1 = r0.paint
            r2 = 0
            r1.setShader(r2)
            android.graphics.Paint r1 = r0.paint2
            r1.setShader(r2)
            android.graphics.Paint r1 = r0.paint3
            r1.setShader(r2)
        L_0x006f:
            int r10 = r8.centerX()
            int r11 = r8.centerY()
            r1 = 0
            int r2 = r0.nextIcon
            r12 = 10
            r13 = 6
            r14 = 3
            r15 = 4
            r6 = 14
            r5 = 1065353216(0x3var_, float:1.0)
            if (r2 != r15) goto L_0x009a
            int r2 = r0.currentIcon
            if (r2 == r14) goto L_0x00af
            if (r2 == r6) goto L_0x00af
            int r1 = r45.save()
            float r2 = r0.transitionProgress
            float r2 = r5 - r2
            float r3 = (float) r10
            float r4 = (float) r11
            r7.scale(r2, r2, r3, r4)
            r4 = r1
            goto L_0x00b0
        L_0x009a:
            if (r2 == r13) goto L_0x009e
            if (r2 != r12) goto L_0x00af
        L_0x009e:
            int r2 = r0.currentIcon
            if (r2 != r15) goto L_0x00af
            int r1 = r45.save()
            float r2 = r0.transitionProgress
            float r3 = (float) r10
            float r4 = (float) r11
            r7.scale(r2, r2, r3, r4)
            r4 = r1
            goto L_0x00b0
        L_0x00af:
            r4 = r1
        L_0x00b0:
            r1 = 1077936128(0x40400000, float:3.0)
            int r16 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = r0.currentIcon
            r17 = 1094713344(0x41400000, float:12.0)
            r18 = 1120403456(0x42CLASSNAME, float:100.0)
            r19 = 1080033280(0x40600000, float:3.5)
            r20 = 1088421888(0x40e00000, float:7.0)
            r21 = 1073741824(0x40000000, float:2.0)
            r13 = 0
            r22 = 1056964608(0x3var_, float:0.5)
            r23 = 1132396544(0x437var_, float:255.0)
            r12 = 2
            if (r1 == r12) goto L_0x00d3
            int r1 = r0.nextIcon
            if (r1 != r12) goto L_0x00cf
            goto L_0x00d3
        L_0x00cf:
            r35 = r4
            goto L_0x03ad
        L_0x00d3:
            r0.applyShaderMatrix(r13)
            float r1 = (float) r11
            r24 = 1091567616(0x41100000, float:9.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r24)
            float r3 = (float) r3
            float r13 = r0.scale
            float r3 = r3 * r13
            float r13 = r1 - r3
            float r1 = (float) r11
            r3 = 1091567616(0x41100000, float:9.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            float r15 = r0.scale
            float r3 = r3 * r15
            float r15 = r1 + r3
            float r1 = (float) r11
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r3 = (float) r3
            float r9 = r0.scale
            float r3 = r3 * r9
            float r9 = r1 + r3
            int r1 = r0.currentIcon
            if (r1 == r14) goto L_0x0104
            if (r1 != r6) goto L_0x012a
        L_0x0104:
            int r1 = r0.nextIcon
            if (r1 != r12) goto L_0x012a
            android.graphics.Paint r1 = r0.paint
            float r3 = r0.transitionProgress
            float r3 = r3 / r22
            float r3 = java.lang.Math.min(r5, r3)
            float r3 = r3 * r23
            int r3 = (int) r3
            r1.setAlpha(r3)
            float r1 = r0.transitionProgress
            float r3 = (float) r11
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r2 = (float) r2
            float r5 = r0.scale
            float r2 = r2 * r5
            float r3 = r3 + r2
            r26 = r1
            r27 = r3
            goto L_0x0169
        L_0x012a:
            int r1 = r0.nextIcon
            if (r1 == r14) goto L_0x014f
            if (r1 == r6) goto L_0x014f
            if (r1 == r12) goto L_0x014f
            android.graphics.Paint r1 = r0.paint
            float r2 = r0.savedTransitionProgress
            float r2 = r2 / r22
            r3 = 1065353216(0x3var_, float:1.0)
            float r2 = java.lang.Math.min(r3, r2)
            float r2 = r2 * r23
            float r5 = r0.transitionProgress
            float r5 = r3 - r5
            float r2 = r2 * r5
            int r2 = (int) r2
            r1.setAlpha(r2)
            float r1 = r0.savedTransitionProgress
            r2 = 255(0xff, float:3.57E-43)
            goto L_0x0158
        L_0x014f:
            android.graphics.Paint r1 = r0.paint
            r2 = 255(0xff, float:3.57E-43)
            r1.setAlpha(r2)
            float r1 = r0.transitionProgress
        L_0x0158:
            float r3 = (float) r11
            r5 = 1065353216(0x3var_, float:1.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r2 = (float) r2
            float r5 = r0.scale
            float r2 = r2 * r5
            float r3 = r3 + r2
            r26 = r1
            r27 = r3
        L_0x0169:
            boolean r1 = r0.animatingTransition
            r2 = 1090519040(0x41000000, float:8.0)
            if (r1 == 0) goto L_0x0358
            r1 = r26
            int r3 = r0.nextIcon
            if (r3 == r12) goto L_0x030f
            int r5 = (r1 > r22 ? 1 : (r1 == r22 ? 0 : -1))
            if (r5 > 0) goto L_0x017d
            r35 = r4
            goto L_0x0311
        L_0x017d:
            r2 = 1095761920(0x41500000, float:13.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            float r3 = r0.scale
            float r2 = r2 * r3
            float r2 = r2 * r3
            boolean r3 = r0.isMini
            if (r3 == 0) goto L_0x0193
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r21)
            goto L_0x0194
        L_0x0193:
            r3 = 0
        L_0x0194:
            float r3 = (float) r3
            float r28 = r2 + r3
            float r1 = r1 - r22
            float r29 = r1 / r22
            r2 = 1045220557(0x3e4ccccd, float:0.2)
            int r2 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r2 <= 0) goto L_0x01b4
            r2 = 1045220557(0x3e4ccccd, float:0.2)
            float r1 = r1 - r2
            r2 = 1065353216(0x3var_, float:1.0)
            r3 = 1050253722(0x3e99999a, float:0.3)
            float r3 = r1 / r3
            r30 = r1
            r31 = r2
            r32 = r3
            goto L_0x01c0
        L_0x01b4:
            r2 = 1045220557(0x3e4ccccd, float:0.2)
            float r2 = r1 / r2
            r3 = 0
            r30 = r1
            r31 = r2
            r32 = r3
        L_0x01c0:
            android.graphics.RectF r1 = r0.rect
            float r2 = (float) r10
            float r2 = r2 - r28
            float r3 = r28 / r21
            float r3 = r9 - r3
            float r5 = (float) r10
            float r33 = r28 / r21
            float r6 = r9 + r33
            r1.set(r2, r3, r5, r6)
            float r33 = r32 * r18
            android.graphics.RectF r2 = r0.rect
            r1 = 1120927744(0x42d00000, float:104.0)
            float r1 = r1 * r29
            float r5 = r1 - r33
            r6 = 0
            android.graphics.Paint r3 = r0.paint
            r1 = r45
            r25 = r3
            r3 = r33
            r35 = r4
            r4 = r5
            r12 = 1065353216(0x3var_, float:1.0)
            r5 = r6
            r14 = 14
            r6 = r25
            r1.drawArc(r2, r3, r4, r5, r6)
            float r1 = r9 - r27
            float r1 = r1 * r31
            float r25 = r27 + r1
            r34 = r9
            r36 = r9
            float r1 = (float) r10
            r37 = r1
            r38 = r1
            r1 = 0
            int r2 = (r32 > r1 ? 1 : (r32 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x0357
            int r1 = r0.nextIcon
            if (r1 != r14) goto L_0x020c
            r1 = 0
            r6 = r1
            goto L_0x0213
        L_0x020c:
            r1 = -1036779520(0xffffffffCLASSNAME, float:-45.0)
            float r5 = r12 - r32
            float r1 = r1 * r5
            r6 = r1
        L_0x0213:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r20)
            float r1 = (float) r1
            float r1 = r1 * r32
            float r2 = r0.scale
            float r28 = r1 * r2
            float r1 = r32 * r23
            int r1 = (int) r1
            int r2 = r0.nextIcon
            r3 = 3
            if (r2 == r3) goto L_0x023b
            if (r2 == r14) goto L_0x023b
            r3 = 2
            if (r2 == r3) goto L_0x023b
            float r2 = r0.transitionProgress
            float r2 = r2 / r22
            float r2 = java.lang.Math.min(r12, r2)
            float r5 = r12 - r2
            float r2 = (float) r1
            float r2 = r2 * r5
            int r1 = (int) r2
            r5 = r1
            goto L_0x023c
        L_0x023b:
            r5 = r1
        L_0x023c:
            r1 = 0
            int r2 = (r6 > r1 ? 1 : (r6 == r1 ? 0 : -1))
            if (r2 == 0) goto L_0x0249
            r45.save()
            float r1 = (float) r10
            float r2 = (float) r11
            r7.rotate(r6, r1, r2)
        L_0x0249:
            if (r5 == 0) goto L_0x0303
            android.graphics.Paint r1 = r0.paint
            r1.setAlpha(r5)
            int r1 = r0.nextIcon
            if (r1 != r14) goto L_0x02d7
            android.graphics.Paint r1 = r0.paint3
            r1.setAlpha(r5)
            android.graphics.RectF r1 = r0.rect
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r2 = r10 - r2
            float r2 = (float) r2
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r3 = r11 - r3
            float r3 = (float) r3
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r4 = r4 + r10
            float r4 = (float) r4
            int r39 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r14 = r11 + r39
            float r14 = (float) r14
            r1.set(r2, r3, r4, r14)
            android.graphics.RectF r1 = r0.rect
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r2 = (float) r2
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r3 = (float) r3
            android.graphics.Paint r4 = r0.paint3
            r7.drawRoundRect(r1, r2, r3, r4)
            android.graphics.Paint r1 = r0.paint
            float r2 = (float) r5
            r3 = 1041865114(0x3e19999a, float:0.15)
            float r2 = r2 * r3
            int r2 = (int) r2
            r1.setAlpha(r2)
            boolean r1 = r0.isMini
            if (r1 == 0) goto L_0x029d
            r3 = 1073741824(0x40000000, float:2.0)
            goto L_0x029f
        L_0x029d:
            r3 = 1082130432(0x40800000, float:4.0)
        L_0x029f:
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r3)
            android.graphics.RectF r1 = r0.rect
            int r2 = r8.left
            int r2 = r2 + r14
            float r2 = (float) r2
            int r3 = r8.top
            int r3 = r3 + r14
            float r3 = (float) r3
            int r4 = r8.right
            int r4 = r4 - r14
            float r4 = (float) r4
            int r12 = r8.bottom
            int r12 = r12 - r14
            float r12 = (float) r12
            r1.set(r2, r3, r4, r12)
            android.graphics.RectF r2 = r0.rect
            r3 = 0
            r4 = 1135869952(0x43b40000, float:360.0)
            r12 = 0
            android.graphics.Paint r1 = r0.paint
            r40 = r1
            r1 = r45
            r41 = r5
            r5 = r12
            r12 = r6
            r6 = r40
            r1.drawArc(r2, r3, r4, r5, r6)
            android.graphics.Paint r1 = r0.paint
            r6 = r41
            r1.setAlpha(r6)
            r40 = r6
            goto L_0x0306
        L_0x02d7:
            r12 = r6
            r6 = r5
            float r1 = (float) r10
            float r2 = r1 - r28
            float r1 = (float) r11
            float r3 = r1 - r28
            float r1 = (float) r10
            float r4 = r1 + r28
            float r1 = (float) r11
            float r5 = r1 + r28
            android.graphics.Paint r14 = r0.paint
            r1 = r45
            r40 = r6
            r6 = r14
            r1.drawLine(r2, r3, r4, r5, r6)
            float r1 = (float) r10
            float r2 = r1 + r28
            float r1 = (float) r11
            float r3 = r1 - r28
            float r1 = (float) r10
            float r4 = r1 - r28
            float r1 = (float) r11
            float r5 = r1 + r28
            android.graphics.Paint r6 = r0.paint
            r1 = r45
            r1.drawLine(r2, r3, r4, r5, r6)
            goto L_0x0306
        L_0x0303:
            r40 = r5
            r12 = r6
        L_0x0306:
            r1 = 0
            int r2 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1))
            if (r2 == 0) goto L_0x0357
            r45.restore()
            goto L_0x0357
        L_0x030f:
            r35 = r4
        L_0x0311:
            r4 = 2
            if (r3 != r4) goto L_0x031b
            r3 = r26
            r4 = 1065353216(0x3var_, float:1.0)
            float r5 = r4 - r3
            goto L_0x0321
        L_0x031b:
            r4 = 1065353216(0x3var_, float:1.0)
            float r5 = r26 / r22
            float r3 = r4 - r5
        L_0x0321:
            float r4 = r27 - r13
            float r4 = r4 * r5
            float r25 = r13 + r4
            float r4 = r9 - r15
            float r4 = r4 * r5
            float r36 = r15 + r4
            float r4 = (float) r10
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r6 = (float) r6
            float r6 = r6 * r3
            float r12 = r0.scale
            float r6 = r6 * r12
            float r38 = r4 - r6
            float r4 = (float) r10
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r6 = (float) r6
            float r6 = r6 * r3
            float r12 = r0.scale
            float r6 = r6 * r12
            float r37 = r4 + r6
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            float r2 = r2 * r3
            float r4 = r0.scale
            float r2 = r2 * r4
            float r34 = r36 - r2
        L_0x0357:
            goto L_0x0381
        L_0x0358:
            r35 = r4
            r25 = r13
            r36 = r15
            float r1 = (float) r10
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r3 = (float) r3
            float r4 = r0.scale
            float r3 = r3 * r4
            float r38 = r1 - r3
            float r1 = (float) r10
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r3 = (float) r3
            float r4 = r0.scale
            float r3 = r3 * r4
            float r37 = r1 + r3
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r1 = (float) r1
            float r2 = r0.scale
            float r1 = r1 * r2
            float r34 = r36 - r1
        L_0x0381:
            int r1 = (r25 > r36 ? 1 : (r25 == r36 ? 0 : -1))
            if (r1 == 0) goto L_0x0392
            float r2 = (float) r10
            float r4 = (float) r10
            android.graphics.Paint r6 = r0.paint
            r1 = r45
            r3 = r25
            r5 = r36
            r1.drawLine(r2, r3, r4, r5, r6)
        L_0x0392:
            float r1 = (float) r10
            int r1 = (r38 > r1 ? 1 : (r38 == r1 ? 0 : -1))
            if (r1 == 0) goto L_0x03ad
            float r4 = (float) r10
            android.graphics.Paint r6 = r0.paint
            r1 = r45
            r2 = r38
            r3 = r34
            r5 = r36
            r1.drawLine(r2, r3, r4, r5, r6)
            float r4 = (float) r10
            android.graphics.Paint r6 = r0.paint
            r2 = r37
            r1.drawLine(r2, r3, r4, r5, r6)
        L_0x03ad:
            int r1 = r0.currentIcon
            r9 = 13
            r12 = 1
            r2 = 3
            if (r1 == r2) goto L_0x0438
            r3 = 14
            if (r1 == r3) goto L_0x0438
            r4 = 4
            if (r1 != r4) goto L_0x03c6
            int r4 = r0.nextIcon
            if (r4 == r3) goto L_0x03c2
            if (r4 != r2) goto L_0x03c6
        L_0x03c2:
            r14 = 1082130432(0x40800000, float:4.0)
            goto L_0x043a
        L_0x03c6:
            r2 = 10
            if (r1 == r2) goto L_0x03d3
            int r3 = r0.nextIcon
            if (r3 == r2) goto L_0x03d3
            if (r1 != r9) goto L_0x03d1
            goto L_0x03d3
        L_0x03d1:
            goto L_0x0706
        L_0x03d3:
            int r1 = r0.nextIcon
            r2 = 4
            if (r1 == r2) goto L_0x03e0
            r2 = 6
            if (r1 != r2) goto L_0x03dc
            goto L_0x03e0
        L_0x03dc:
            r1 = 255(0xff, float:3.57E-43)
            r13 = r1
            goto L_0x03ea
        L_0x03e0:
            float r1 = r0.transitionProgress
            r2 = 1065353216(0x3var_, float:1.0)
            float r5 = r2 - r1
            float r2 = r5 * r23
            int r1 = (int) r2
            r13 = r1
        L_0x03ea:
            if (r13 == 0) goto L_0x0706
            r1 = 0
            r0.applyShaderMatrix(r1)
            android.graphics.Paint r1 = r0.paint
            float r2 = (float) r13
            float r3 = r0.overrideAlpha
            float r2 = r2 * r3
            int r2 = (int) r2
            r1.setAlpha(r2)
            r1 = 1135869952(0x43b40000, float:360.0)
            float r2 = r0.animatedDownloadProgress
            float r2 = r2 * r1
            r14 = 1082130432(0x40800000, float:4.0)
            float r15 = java.lang.Math.max(r14, r2)
            boolean r1 = r0.isMini
            if (r1 == 0) goto L_0x040e
            r3 = 1073741824(0x40000000, float:2.0)
            goto L_0x0410
        L_0x040e:
            r3 = 1082130432(0x40800000, float:4.0)
        L_0x0410:
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r3)
            android.graphics.RectF r1 = r0.rect
            int r2 = r8.left
            int r2 = r2 + r14
            float r2 = (float) r2
            int r3 = r8.top
            int r3 = r3 + r14
            float r3 = (float) r3
            int r4 = r8.right
            int r4 = r4 - r14
            float r4 = (float) r4
            int r5 = r8.bottom
            int r5 = r5 - r14
            float r5 = (float) r5
            r1.set(r2, r3, r4, r5)
            android.graphics.RectF r2 = r0.rect
            float r3 = r0.downloadRadOffset
            r5 = 0
            android.graphics.Paint r6 = r0.paint
            r1 = r45
            r4 = r15
            r1.drawArc(r2, r3, r4, r5, r6)
            goto L_0x0706
        L_0x0438:
            r14 = 1082130432(0x40800000, float:4.0)
        L_0x043a:
            r1 = 0
            r0.applyShaderMatrix(r1)
            r1 = 1065353216(0x3var_, float:1.0)
            r2 = 1065353216(0x3var_, float:1.0)
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            int r13 = r0.nextIcon
            r15 = 2
            if (r13 != r15) goto L_0x0476
            float r13 = r0.transitionProgress
            int r15 = (r13 > r22 ? 1 : (r13 == r22 ? 0 : -1))
            if (r15 > 0) goto L_0x0466
            float r13 = r13 / r22
            r15 = 1065353216(0x3var_, float:1.0)
            float r25 = r15 - r13
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r20)
            float r15 = (float) r15
            float r15 = r15 * r25
            float r9 = r0.scale
            float r15 = r15 * r9
            float r9 = r25 * r23
            int r9 = (int) r9
            goto L_0x0468
        L_0x0466:
            r15 = 0
            r9 = 0
        L_0x0468:
            r13 = 0
            r12 = r2
            r14 = r4
            r4 = r13
            r27 = r15
            r13 = r3
            r15 = r5
            r5 = r9
            r9 = r1
            r1 = 1065353216(0x3var_, float:1.0)
            goto L_0x05a3
        L_0x0476:
            r9 = 15
            if (r13 == r9) goto L_0x0555
            if (r13 == 0) goto L_0x0555
            if (r13 == r12) goto L_0x0555
            r9 = 5
            if (r13 == r9) goto L_0x0555
            r9 = 8
            if (r13 == r9) goto L_0x0555
            r9 = 9
            if (r13 == r9) goto L_0x0555
            r9 = 7
            if (r13 == r9) goto L_0x0555
            r9 = 6
            if (r13 != r9) goto L_0x0491
            goto L_0x0555
        L_0x0491:
            r9 = 4
            if (r13 != r9) goto L_0x04db
            float r9 = r0.transitionProgress
            r13 = 1065353216(0x3var_, float:1.0)
            float r15 = r13 - r9
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r20)
            float r13 = (float) r13
            float r12 = r0.scale
            float r12 = r12 * r13
            float r13 = r15 * r23
            int r13 = (int) r13
            r27 = 0
            r1 = r15
            r2 = 1065353216(0x3var_, float:1.0)
            int r14 = r0.currentIcon
            r28 = r1
            r1 = 14
            if (r14 != r1) goto L_0x04be
            int r1 = r8.left
            float r1 = (float) r1
            r4 = r1
            int r3 = r8.top
            float r3 = (float) r3
            r6 = r3
            r5 = r3
            r3 = r1
            goto L_0x04cc
        L_0x04be:
            int r1 = r8.centerX()
            float r1 = (float) r1
            r4 = r1
            int r3 = r8.centerY()
            float r3 = (float) r3
            r6 = r3
            r5 = r3
            r3 = r1
        L_0x04cc:
            r14 = r4
            r15 = r5
            r5 = r13
            r4 = r27
            r9 = r28
            r1 = 1065353216(0x3var_, float:1.0)
            r13 = r3
            r27 = r12
            r12 = r2
            goto L_0x05a3
        L_0x04db:
            r9 = 14
            if (r13 == r9) goto L_0x04fc
            r9 = 3
            if (r13 != r9) goto L_0x04e3
            goto L_0x04fc
        L_0x04e3:
            r13 = 0
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r20)
            float r9 = (float) r9
            float r12 = r0.scale
            float r15 = r9 * r12
            r9 = 255(0xff, float:3.57E-43)
            r12 = r2
            r14 = r4
            r4 = r13
            r27 = r15
            r13 = r3
            r15 = r5
            r5 = r9
            r9 = r1
            r1 = 1065353216(0x3var_, float:1.0)
            goto L_0x05a3
        L_0x04fc:
            float r9 = r0.transitionProgress
            r12 = 1065353216(0x3var_, float:1.0)
            float r13 = r12 - r9
            int r12 = r0.currentIcon
            r14 = 4
            if (r12 != r14) goto L_0x050c
            r12 = 0
            r2 = 1065353216(0x3var_, float:1.0)
            r1 = r9
            goto L_0x0514
        L_0x050c:
            r12 = 1110704128(0x42340000, float:45.0)
            float r12 = r12 * r13
            r14 = 1065353216(0x3var_, float:1.0)
            r2 = r14
            r1 = r14
        L_0x0514:
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r20)
            float r14 = (float) r14
            float r15 = r0.scale
            float r15 = r15 * r14
            float r14 = r9 * r23
            int r14 = (int) r14
            r27 = r1
            int r1 = r0.nextIcon
            r28 = r2
            r2 = 14
            if (r1 != r2) goto L_0x0537
            int r1 = r8.left
            float r1 = (float) r1
            r2 = r1
            int r3 = r8.top
            float r3 = (float) r3
            r4 = r3
            r5 = r3
            r6 = r4
            r3 = r1
            r4 = r2
            goto L_0x0547
        L_0x0537:
            int r1 = r8.centerX()
            float r1 = (float) r1
            r2 = r1
            int r3 = r8.centerY()
            float r3 = (float) r3
            r4 = r3
            r5 = r3
            r6 = r4
            r3 = r1
            r4 = r2
        L_0x0547:
            r13 = r3
            r9 = r27
            r1 = 1065353216(0x3var_, float:1.0)
            r27 = r15
            r15 = r5
            r5 = r14
            r14 = r4
            r4 = r12
            r12 = r28
            goto L_0x05a3
        L_0x0555:
            r9 = 6
            if (r13 != r9) goto L_0x0563
            float r9 = r0.transitionProgress
            float r9 = r9 / r22
            r12 = 1065353216(0x3var_, float:1.0)
            float r9 = java.lang.Math.min(r12, r9)
            goto L_0x0567
        L_0x0563:
            r12 = 1065353216(0x3var_, float:1.0)
            float r9 = r0.transitionProgress
        L_0x0567:
            float r13 = r12 - r9
            r14 = 0
            r2 = 1065353216(0x3var_, float:1.0)
            float r1 = r12 - r9
            int r12 = r8.centerX()
            float r12 = (float) r12
            r4 = r12
            r3 = r12
            int r12 = r8.centerY()
            float r12 = (float) r12
            r6 = r12
            r5 = r12
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r20)
            float r12 = (float) r12
            float r12 = r12 * r13
            float r15 = r0.scale
            float r15 = r15 * r12
            float r12 = r13 * r21
            r27 = r1
            r1 = 1065353216(0x3var_, float:1.0)
            float r12 = java.lang.Math.min(r1, r12)
            float r12 = r12 * r23
            int r9 = (int) r12
            r12 = r2
            r13 = r3
            r42 = r14
            r14 = r4
            r4 = r42
            r43 = r15
            r15 = r5
            r5 = r9
            r9 = r27
            r27 = r43
        L_0x05a3:
            int r2 = (r9 > r1 ? 1 : (r9 == r1 ? 0 : -1))
            if (r2 == 0) goto L_0x05ad
            r45.save()
            r7.scale(r9, r9, r13, r15)
        L_0x05ad:
            r1 = 0
            int r2 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r2 == 0) goto L_0x05ba
            r45.save()
            float r1 = (float) r10
            float r2 = (float) r11
            r7.rotate(r4, r1, r2)
        L_0x05ba:
            if (r5 == 0) goto L_0x0655
            android.graphics.Paint r1 = r0.paint
            float r2 = (float) r5
            float r3 = r0.overrideAlpha
            float r2 = r2 * r3
            int r2 = (int) r2
            r1.setAlpha(r2)
            int r1 = r0.currentIcon
            r2 = 14
            if (r1 == r2) goto L_0x0611
            int r1 = r0.nextIcon
            if (r1 != r2) goto L_0x05da
            r30 = r4
            r31 = r13
            r28 = r15
            r13 = r5
            r15 = r6
            goto L_0x0619
        L_0x05da:
            float r1 = (float) r10
            float r2 = r1 - r27
            float r1 = (float) r11
            float r3 = r1 - r27
            float r1 = (float) r10
            float r19 = r1 + r27
            float r1 = (float) r11
            float r28 = r1 + r27
            android.graphics.Paint r1 = r0.paint
            r29 = r1
            r1 = r45
            r30 = r4
            r4 = r19
            r31 = r13
            r13 = r5
            r5 = r28
            r28 = r15
            r15 = r6
            r6 = r29
            r1.drawLine(r2, r3, r4, r5, r6)
            float r1 = (float) r10
            float r2 = r1 + r27
            float r1 = (float) r11
            float r3 = r1 - r27
            float r1 = (float) r10
            float r4 = r1 - r27
            float r1 = (float) r11
            float r5 = r1 + r27
            android.graphics.Paint r6 = r0.paint
            r1 = r45
            r1.drawLine(r2, r3, r4, r5, r6)
            goto L_0x065d
        L_0x0611:
            r30 = r4
            r31 = r13
            r28 = r15
            r13 = r5
            r15 = r6
        L_0x0619:
            android.graphics.Paint r1 = r0.paint3
            float r2 = (float) r13
            float r3 = r0.overrideAlpha
            float r2 = r2 * r3
            int r2 = (int) r2
            r1.setAlpha(r2)
            android.graphics.RectF r1 = r0.rect
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r2 = r10 - r2
            float r2 = (float) r2
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r3 = r11 - r3
            float r3 = (float) r3
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r4 = r4 + r10
            float r4 = (float) r4
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r5 = r5 + r11
            float r5 = (float) r5
            r1.set(r2, r3, r4, r5)
            android.graphics.RectF r1 = r0.rect
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r2 = (float) r2
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r3 = (float) r3
            android.graphics.Paint r4 = r0.paint3
            r7.drawRoundRect(r1, r2, r3, r4)
            goto L_0x065d
        L_0x0655:
            r30 = r4
            r31 = r13
            r28 = r15
            r13 = r5
            r15 = r6
        L_0x065d:
            r1 = 0
            int r2 = (r30 > r1 ? 1 : (r30 == r1 ? 0 : -1))
            if (r2 == 0) goto L_0x0665
            r45.restore()
        L_0x0665:
            r1 = 1065353216(0x3var_, float:1.0)
            int r2 = (r9 > r1 ? 1 : (r9 == r1 ? 0 : -1))
            if (r2 == 0) goto L_0x066e
            r45.restore()
        L_0x066e:
            int r2 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1))
            if (r2 == 0) goto L_0x0678
            r45.save()
            r7.scale(r12, r12, r14, r15)
        L_0x0678:
            int r1 = r0.currentIcon
            r2 = 3
            if (r1 == r2) goto L_0x068a
            r3 = 14
            if (r1 == r3) goto L_0x068a
            r4 = 4
            if (r1 != r4) goto L_0x06fb
            int r1 = r0.nextIcon
            if (r1 == r3) goto L_0x068a
            if (r1 != r2) goto L_0x06fb
        L_0x068a:
            if (r13 == 0) goto L_0x06fb
            r1 = 1135869952(0x43b40000, float:360.0)
            float r2 = r0.animatedDownloadProgress
            float r2 = r2 * r1
            r1 = 1082130432(0x40800000, float:4.0)
            float r19 = java.lang.Math.max(r1, r2)
            boolean r2 = r0.isMini
            if (r2 == 0) goto L_0x069f
            r3 = 1073741824(0x40000000, float:2.0)
            goto L_0x06a1
        L_0x069f:
            r3 = 1082130432(0x40800000, float:4.0)
        L_0x06a1:
            int r21 = org.telegram.messenger.AndroidUtilities.dp(r3)
            android.graphics.RectF r1 = r0.rect
            int r2 = r8.left
            int r2 = r2 + r21
            float r2 = (float) r2
            int r3 = r8.top
            int r3 = r3 + r21
            float r3 = (float) r3
            int r4 = r8.right
            int r4 = r4 - r21
            float r4 = (float) r4
            int r5 = r8.bottom
            int r5 = r5 - r21
            float r5 = (float) r5
            r1.set(r2, r3, r4, r5)
            int r1 = r0.currentIcon
            r2 = 14
            if (r1 == r2) goto L_0x06cb
            r3 = 4
            if (r1 != r3) goto L_0x06ed
            int r1 = r0.nextIcon
            if (r1 != r2) goto L_0x06ed
        L_0x06cb:
            android.graphics.Paint r1 = r0.paint
            float r2 = (float) r13
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
            r1 = r45
            r1.drawArc(r2, r3, r4, r5, r6)
            android.graphics.Paint r1 = r0.paint
            r1.setAlpha(r13)
        L_0x06ed:
            android.graphics.RectF r2 = r0.rect
            float r3 = r0.downloadRadOffset
            r5 = 0
            android.graphics.Paint r6 = r0.paint
            r1 = r45
            r4 = r19
            r1.drawArc(r2, r3, r4, r5, r6)
        L_0x06fb:
            r1 = 1065353216(0x3var_, float:1.0)
            int r2 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1))
            if (r2 == 0) goto L_0x03d1
            r45.restore()
            goto L_0x03d1
        L_0x0706:
            r1 = 0
            r2 = 0
            r3 = 0
            r4 = 0
            int r5 = r0.currentIcon
            int r6 = r0.nextIcon
            if (r5 != r6) goto L_0x0717
            r5 = 1065353216(0x3var_, float:1.0)
            r6 = r5
            r9 = r5
            r12 = r9
            r9 = r6
            goto L_0x0746
        L_0x0717:
            r6 = 4
            if (r5 == r6) goto L_0x073c
            r6 = 3
            if (r5 == r6) goto L_0x073c
            r6 = 14
            if (r5 != r6) goto L_0x0724
            r6 = 1065353216(0x3var_, float:1.0)
            goto L_0x073e
        L_0x0724:
            float r5 = r0.transitionProgress
            float r5 = r5 / r22
            r6 = 1065353216(0x3var_, float:1.0)
            float r9 = java.lang.Math.min(r6, r5)
            float r5 = r0.transitionProgress
            float r5 = r5 / r22
            float r5 = r6 - r5
            r12 = 0
            float r5 = java.lang.Math.max(r12, r5)
            r12 = r9
            r9 = r5
            goto L_0x0746
        L_0x073c:
            r6 = 1065353216(0x3var_, float:1.0)
        L_0x073e:
            float r9 = r0.transitionProgress
            float r5 = r0.transitionProgress
            float r5 = r6 - r5
            r12 = r9
            r9 = r5
        L_0x0746:
            int r5 = r0.nextIcon
            r6 = 15
            if (r5 != r6) goto L_0x074f
            android.graphics.Path[] r3 = org.telegram.ui.ActionBar.Theme.chat_updatePath
            goto L_0x0757
        L_0x074f:
            int r5 = r0.currentIcon
            r6 = 15
            if (r5 != r6) goto L_0x0757
            android.graphics.Path[] r4 = org.telegram.ui.ActionBar.Theme.chat_updatePath
        L_0x0757:
            int r5 = r0.nextIcon
            r6 = 5
            if (r5 != r6) goto L_0x0761
            android.graphics.Path[] r3 = org.telegram.ui.ActionBar.Theme.chat_filePath
            r13 = r3
            r14 = r4
            goto L_0x076d
        L_0x0761:
            int r5 = r0.currentIcon
            r6 = 5
            if (r5 != r6) goto L_0x076b
            android.graphics.Path[] r4 = org.telegram.ui.ActionBar.Theme.chat_filePath
            r13 = r3
            r14 = r4
            goto L_0x076d
        L_0x076b:
            r13 = r3
            r14 = r4
        L_0x076d:
            int r3 = r0.nextIcon
            r4 = 7
            if (r3 != r4) goto L_0x0775
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.chat_flameIcon
            goto L_0x077c
        L_0x0775:
            int r3 = r0.currentIcon
            r4 = 7
            if (r3 != r4) goto L_0x077c
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.chat_flameIcon
        L_0x077c:
            int r3 = r0.nextIcon
            r4 = 8
            if (r3 != r4) goto L_0x0787
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.chat_gifIcon
            r15 = r1
            r6 = r2
            goto L_0x0794
        L_0x0787:
            int r3 = r0.currentIcon
            r4 = 8
            if (r3 != r4) goto L_0x0792
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.chat_gifIcon
            r15 = r1
            r6 = r2
            goto L_0x0794
        L_0x0792:
            r15 = r1
            r6 = r2
        L_0x0794:
            int r1 = r0.currentIcon
            r2 = 9
            r19 = 1086324736(0x40CLASSNAME, float:6.0)
            if (r1 == r2) goto L_0x07ae
            int r1 = r0.nextIcon
            r2 = 9
            if (r1 != r2) goto L_0x07a3
            goto L_0x07ae
        L_0x07a3:
            r28 = r9
            r27 = r13
            r29 = r14
            r30 = r15
            r15 = r6
            goto L_0x082e
        L_0x07ae:
            r1 = 0
            r0.applyShaderMatrix(r1)
            android.graphics.Paint r1 = r0.paint
            int r2 = r0.currentIcon
            int r3 = r0.nextIcon
            if (r2 != r3) goto L_0x07bd
            r2 = 255(0xff, float:3.57E-43)
            goto L_0x07c2
        L_0x07bd:
            float r2 = r0.transitionProgress
            float r2 = r2 * r23
            int r2 = (int) r2
        L_0x07c2:
            r1.setAlpha(r2)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r5 = r11 + r1
            r1 = 1077936128(0x40400000, float:3.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r4 = r10 - r1
            int r1 = r0.currentIcon
            int r2 = r0.nextIcon
            if (r1 == r2) goto L_0x07e3
            r45.save()
            float r1 = r0.transitionProgress
            float r2 = (float) r10
            float r3 = (float) r11
            r7.scale(r1, r1, r2, r3)
        L_0x07e3:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r1 = r4 - r1
            float r2 = (float) r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r1 = r5 - r1
            float r3 = (float) r1
            float r1 = (float) r4
            r21 = r6
            float r6 = (float) r5
            r27 = r13
            android.graphics.Paint r13 = r0.paint
            r28 = r1
            r1 = r45
            r29 = r14
            r14 = r4
            r4 = r28
            r28 = r9
            r9 = r5
            r5 = r6
            r30 = r15
            r15 = r21
            r6 = r13
            r1.drawLine(r2, r3, r4, r5, r6)
            float r2 = (float) r14
            float r3 = (float) r9
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r4 = r14 + r1
            float r4 = (float) r4
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r5 = r9 - r1
            float r5 = (float) r5
            android.graphics.Paint r6 = r0.paint
            r1 = r45
            r1.drawLine(r2, r3, r4, r5, r6)
            int r1 = r0.currentIcon
            int r2 = r0.nextIcon
            if (r1 == r2) goto L_0x082e
            r45.restore()
        L_0x082e:
            int r1 = r0.currentIcon
            r2 = 12
            if (r1 == r2) goto L_0x083a
            int r1 = r0.nextIcon
            r2 = 12
            if (r1 != r2) goto L_0x08b8
        L_0x083a:
            r1 = 0
            r0.applyShaderMatrix(r1)
            int r1 = r0.currentIcon
            int r2 = r0.nextIcon
            if (r1 != r2) goto L_0x0848
            r3 = 1065353216(0x3var_, float:1.0)
            r9 = r3
            goto L_0x0857
        L_0x0848:
            r3 = 13
            if (r2 != r3) goto L_0x0850
            float r3 = r0.transitionProgress
            r9 = r3
            goto L_0x0857
        L_0x0850:
            float r3 = r0.transitionProgress
            r4 = 1065353216(0x3var_, float:1.0)
            float r3 = r4 - r3
            r9 = r3
        L_0x0857:
            android.graphics.Paint r3 = r0.paint
            if (r1 != r2) goto L_0x085e
            r2 = 255(0xff, float:3.57E-43)
            goto L_0x0861
        L_0x085e:
            float r1 = r9 * r23
            int r2 = (int) r1
        L_0x0861:
            r3.setAlpha(r2)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r13 = r11 + r1
            r1 = 1077936128(0x40400000, float:3.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r14 = r10 - r1
            int r1 = r0.currentIcon
            int r2 = r0.nextIcon
            if (r1 == r2) goto L_0x0880
            r45.save()
            float r1 = (float) r10
            float r2 = (float) r11
            r7.scale(r9, r9, r1, r2)
        L_0x0880:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r20)
            float r1 = (float) r1
            float r2 = r0.scale
            float r21 = r1 * r2
            float r1 = (float) r10
            float r2 = r1 - r21
            float r1 = (float) r11
            float r3 = r1 - r21
            float r1 = (float) r10
            float r4 = r1 + r21
            float r1 = (float) r11
            float r5 = r1 + r21
            android.graphics.Paint r6 = r0.paint
            r1 = r45
            r1.drawLine(r2, r3, r4, r5, r6)
            float r1 = (float) r10
            float r2 = r1 + r21
            float r1 = (float) r11
            float r3 = r1 - r21
            float r1 = (float) r10
            float r4 = r1 - r21
            float r1 = (float) r11
            float r5 = r1 + r21
            android.graphics.Paint r6 = r0.paint
            r1 = r45
            r1.drawLine(r2, r3, r4, r5, r6)
            int r1 = r0.currentIcon
            int r2 = r0.nextIcon
            if (r1 == r2) goto L_0x08b8
            r45.restore()
        L_0x08b8:
            int r1 = r0.currentIcon
            r2 = 13
            if (r1 == r2) goto L_0x08c2
            int r1 = r0.nextIcon
            if (r1 != r2) goto L_0x0940
        L_0x08c2:
            r1 = 0
            r0.applyShaderMatrix(r1)
            int r1 = r0.currentIcon
            int r2 = r0.nextIcon
            if (r1 != r2) goto L_0x08cf
            r1 = 1065353216(0x3var_, float:1.0)
            goto L_0x08dc
        L_0x08cf:
            r1 = 13
            if (r2 != r1) goto L_0x08d6
            float r1 = r0.transitionProgress
            goto L_0x08dc
        L_0x08d6:
            float r1 = r0.transitionProgress
            r2 = 1065353216(0x3var_, float:1.0)
            float r1 = r2 - r1
        L_0x08dc:
            android.text.TextPaint r2 = r0.textPaint
            float r3 = r1 * r23
            int r3 = (int) r3
            r2.setAlpha(r3)
            r2 = 1084227584(0x40a00000, float:5.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r2 = r2 + r11
            int r3 = r0.percentStringWidth
            r4 = 2
            int r3 = r3 / r4
            int r3 = r10 - r3
            int r4 = r0.currentIcon
            int r5 = r0.nextIcon
            if (r4 == r5) goto L_0x08ff
            r45.save()
            float r4 = (float) r10
            float r5 = (float) r11
            r7.scale(r1, r1, r4, r5)
        L_0x08ff:
            float r4 = r0.animatedDownloadProgress
            float r4 = r4 * r18
            int r4 = (int) r4
            java.lang.String r5 = r0.percentString
            if (r5 == 0) goto L_0x090c
            int r5 = r0.lastPercent
            if (r4 == r5) goto L_0x092e
        L_0x090c:
            r0.lastPercent = r4
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]
            java.lang.Integer r5 = java.lang.Integer.valueOf(r4)
            r9 = 0
            r6[r9] = r5
            java.lang.String r5 = "%d%%"
            java.lang.String r5 = java.lang.String.format(r5, r6)
            r0.percentString = r5
            android.text.TextPaint r6 = r0.textPaint
            float r5 = r6.measureText(r5)
            double r5 = (double) r5
            double r5 = java.lang.Math.ceil(r5)
            int r5 = (int) r5
            r0.percentStringWidth = r5
        L_0x092e:
            java.lang.String r5 = r0.percentString
            float r6 = (float) r3
            float r9 = (float) r2
            android.text.TextPaint r13 = r0.textPaint
            r7.drawText(r5, r6, r9, r13)
            int r5 = r0.currentIcon
            int r6 = r0.nextIcon
            if (r5 == r6) goto L_0x0940
            r45.restore()
        L_0x0940:
            int r1 = r0.currentIcon
            if (r1 == 0) goto L_0x0952
            r2 = 1
            if (r1 == r2) goto L_0x0952
            int r3 = r0.nextIcon
            if (r3 == 0) goto L_0x0952
            if (r3 != r2) goto L_0x094e
            goto L_0x0952
        L_0x094e:
            r9 = 255(0xff, float:3.57E-43)
            goto L_0x0a85
        L_0x0952:
            if (r1 != 0) goto L_0x095a
            int r2 = r0.nextIcon
            r3 = 1
            if (r2 == r3) goto L_0x0961
            goto L_0x095b
        L_0x095a:
            r3 = 1
        L_0x095b:
            if (r1 != r3) goto L_0x097d
            int r2 = r0.nextIcon
            if (r2 != 0) goto L_0x097d
        L_0x0961:
            boolean r2 = r0.animatingTransition
            if (r2 == 0) goto L_0x0973
            int r2 = r0.nextIcon
            if (r2 != 0) goto L_0x0970
            float r2 = r0.transitionProgress
            r3 = 1065353216(0x3var_, float:1.0)
            float r5 = r3 - r2
            goto L_0x0984
        L_0x0970:
            float r5 = r0.transitionProgress
            goto L_0x0984
        L_0x0973:
            int r2 = r0.nextIcon
            r3 = 1
            if (r2 != r3) goto L_0x097b
            r5 = 1065353216(0x3var_, float:1.0)
            goto L_0x097c
        L_0x097b:
            r5 = 0
        L_0x097c:
            goto L_0x0984
        L_0x097d:
            r2 = 1
            if (r1 != r2) goto L_0x0983
            r5 = 1065353216(0x3var_, float:1.0)
            goto L_0x0984
        L_0x0983:
            r5 = 0
        L_0x0984:
            int r2 = r0.nextIcon
            if (r2 == 0) goto L_0x098c
            r3 = 1
            if (r2 != r3) goto L_0x0991
            goto L_0x098d
        L_0x098c:
            r3 = 1
        L_0x098d:
            if (r1 == 0) goto L_0x09b7
            if (r1 == r3) goto L_0x09b7
        L_0x0991:
            r3 = 4
            if (r2 != r3) goto L_0x09a5
            android.graphics.Paint r1 = r0.paint2
            float r2 = r0.transitionProgress
            r3 = 1065353216(0x3var_, float:1.0)
            float r2 = r3 - r2
            float r2 = r2 * r23
            int r2 = (int) r2
            r1.setAlpha(r2)
            r9 = 255(0xff, float:3.57E-43)
            goto L_0x09be
        L_0x09a5:
            android.graphics.Paint r3 = r0.paint2
            if (r1 != r2) goto L_0x09ac
            r2 = 255(0xff, float:3.57E-43)
            goto L_0x09b1
        L_0x09ac:
            float r1 = r0.transitionProgress
            float r1 = r1 * r23
            int r2 = (int) r1
        L_0x09b1:
            r3.setAlpha(r2)
            r9 = 255(0xff, float:3.57E-43)
            goto L_0x09be
        L_0x09b7:
            android.graphics.Paint r1 = r0.paint2
            r9 = 255(0xff, float:3.57E-43)
            r1.setAlpha(r9)
        L_0x09be:
            r1 = 1
            r0.applyShaderMatrix(r1)
            r45.save()
            int r1 = r8.centerX()
            float r1 = (float) r1
            r2 = 1065353216(0x3var_, float:1.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r3 = (float) r3
            float r4 = r2 - r5
            float r3 = r3 * r4
            float r1 = r1 + r3
            int r2 = r8.centerY()
            float r2 = (float) r2
            r7.translate(r1, r2)
            r1 = 1140457472(0x43fa0000, float:500.0)
            float r1 = r1 * r5
            int r2 = r0.currentIcon
            r3 = 1
            if (r2 != r3) goto L_0x09ea
            r3 = 1119092736(0x42b40000, float:90.0)
            goto L_0x09eb
        L_0x09ea:
            r3 = 0
        L_0x09eb:
            if (r2 != 0) goto L_0x0a29
            int r4 = r0.nextIcon
            r6 = 1
            if (r4 != r6) goto L_0x0a29
            r2 = 1136656384(0x43CLASSNAME, float:384.0)
            int r2 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r2 >= 0) goto L_0x0a08
            r2 = 1119748096(0x42be0000, float:95.0)
            org.telegram.ui.Components.CubicBezierInterpolator r4 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_BOTH
            r6 = 1136656384(0x43CLASSNAME, float:384.0)
            float r6 = r1 / r6
            float r4 = r4.getInterpolation(r6)
            float r4 = r4 * r2
            r3 = r4
            goto L_0x0a26
        L_0x0a08:
            r2 = 1139933184(0x43var_, float:484.0)
            int r2 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r2 >= 0) goto L_0x0a23
            r2 = 1119748096(0x42be0000, float:95.0)
            r4 = 1084227584(0x40a00000, float:5.0)
            org.telegram.ui.Components.CubicBezierInterpolator r6 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_BOTH
            r13 = 1136656384(0x43CLASSNAME, float:384.0)
            float r13 = r1 - r13
            float r13 = r13 / r18
            float r6 = r6.getInterpolation(r13)
            float r6 = r6 * r4
            float r2 = r2 - r6
            r3 = r2
            goto L_0x0a26
        L_0x0a23:
            r2 = 1119092736(0x42b40000, float:90.0)
            r3 = r2
        L_0x0a26:
            float r1 = r1 + r18
            goto L_0x0a5d
        L_0x0a29:
            r4 = 1
            if (r2 != r4) goto L_0x0a5d
            int r2 = r0.nextIcon
            if (r2 != 0) goto L_0x0a5d
            int r2 = (r1 > r18 ? 1 : (r1 == r18 ? 0 : -1))
            if (r2 >= 0) goto L_0x0a41
            r2 = -1063256064(0xffffffffc0a00000, float:-5.0)
            org.telegram.ui.Components.CubicBezierInterpolator r4 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_BOTH
            float r6 = r1 / r18
            float r4 = r4.getInterpolation(r6)
            float r3 = r4 * r2
            goto L_0x0a5d
        L_0x0a41:
            r2 = 1139933184(0x43var_, float:484.0)
            int r2 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r2 >= 0) goto L_0x0a5b
            r2 = -1063256064(0xffffffffc0a00000, float:-5.0)
            r4 = 1119748096(0x42be0000, float:95.0)
            org.telegram.ui.Components.CubicBezierInterpolator r6 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_BOTH
            float r13 = r1 - r18
            r14 = 1136656384(0x43CLASSNAME, float:384.0)
            float r13 = r13 / r14
            float r6 = r6.getInterpolation(r13)
            float r6 = r6 * r4
            float r3 = r6 + r2
            goto L_0x0a5d
        L_0x0a5b:
            r3 = 1119092736(0x42b40000, float:90.0)
        L_0x0a5d:
            r7.rotate(r3)
            int r2 = r0.currentIcon
            if (r2 == 0) goto L_0x0a67
            r4 = 1
            if (r2 != r4) goto L_0x0a6a
        L_0x0a67:
            r4 = 4
            if (r2 != r4) goto L_0x0a6d
        L_0x0a6a:
            r7.scale(r12, r12)
        L_0x0a6d:
            org.telegram.ui.Components.PathAnimator r2 = org.telegram.ui.ActionBar.Theme.playPauseAnimator
            android.graphics.Paint r4 = r0.paint2
            r2.draw(r7, r4, r1)
            r2 = -1082130432(0xffffffffbvar_, float:-1.0)
            r4 = 1065353216(0x3var_, float:1.0)
            r7.scale(r4, r2)
            org.telegram.ui.Components.PathAnimator r2 = org.telegram.ui.ActionBar.Theme.playPauseAnimator
            android.graphics.Paint r4 = r0.paint2
            r2.draw(r7, r4, r1)
            r45.restore()
        L_0x0a85:
            int r1 = r0.currentIcon
            r2 = 6
            if (r1 == r2) goto L_0x0a93
            int r1 = r0.nextIcon
            if (r1 != r2) goto L_0x0a8f
            goto L_0x0a93
        L_0x0a8f:
            r19 = r8
            goto L_0x0b50
        L_0x0a93:
            r1 = 0
            r0.applyShaderMatrix(r1)
            int r1 = r0.currentIcon
            if (r1 == r2) goto L_0x0ac6
            float r1 = r0.transitionProgress
            int r2 = (r1 > r22 ? 1 : (r1 == r22 ? 0 : -1))
            if (r2 <= 0) goto L_0x0abb
            float r1 = r1 - r22
            float r1 = r1 / r22
            float r2 = r1 / r22
            r3 = 1065353216(0x3var_, float:1.0)
            float r2 = java.lang.Math.min(r3, r2)
            float r5 = r3 - r2
            int r2 = (r1 > r22 ? 1 : (r1 == r22 ? 0 : -1))
            if (r2 <= 0) goto L_0x0ab8
            float r2 = r1 - r22
            float r2 = r2 / r22
            goto L_0x0ab9
        L_0x0ab8:
            r2 = 0
        L_0x0ab9:
            r1 = r2
            goto L_0x0abe
        L_0x0abb:
            r5 = 1065353216(0x3var_, float:1.0)
            r1 = 0
        L_0x0abe:
            android.graphics.Paint r2 = r0.paint
            r2.setAlpha(r9)
            r13 = r1
            r14 = r5
            goto L_0x0ae4
        L_0x0ac6:
            r5 = 0
            r1 = 1065353216(0x3var_, float:1.0)
            int r2 = r0.nextIcon
            r3 = 6
            if (r2 == r3) goto L_0x0add
            android.graphics.Paint r2 = r0.paint
            float r3 = r0.transitionProgress
            r4 = 1065353216(0x3var_, float:1.0)
            float r3 = r4 - r3
            float r3 = r3 * r23
            int r3 = (int) r3
            r2.setAlpha(r3)
            goto L_0x0ae2
        L_0x0add:
            android.graphics.Paint r2 = r0.paint
            r2.setAlpha(r9)
        L_0x0ae2:
            r13 = r1
            r14 = r5
        L_0x0ae4:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r6 = r11 + r1
            r1 = 1077936128(0x40400000, float:3.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r5 = r10 - r1
            r1 = 1065353216(0x3var_, float:1.0)
            int r2 = (r14 > r1 ? 1 : (r14 == r1 ? 0 : -1))
            if (r2 >= 0) goto L_0x0b2b
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r1 = r5 - r1
            float r2 = (float) r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r1 = r6 - r1
            float r3 = (float) r1
            float r1 = (float) r5
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r19)
            float r4 = (float) r4
            float r4 = r4 * r14
            float r4 = r1 - r4
            float r1 = (float) r6
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r19)
            float r9 = (float) r9
            float r9 = r9 * r14
            float r9 = r1 - r9
            android.graphics.Paint r1 = r0.paint
            r18 = r1
            r1 = r45
            r19 = r8
            r8 = r5
            r5 = r9
            r9 = r6
            r6 = r18
            r1.drawLine(r2, r3, r4, r5, r6)
            goto L_0x0b2f
        L_0x0b2b:
            r9 = r6
            r19 = r8
            r8 = r5
        L_0x0b2f:
            r1 = 0
            int r2 = (r13 > r1 ? 1 : (r13 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x0b50
            float r2 = (float) r8
            float r3 = (float) r9
            float r1 = (float) r8
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r4 = (float) r4
            float r4 = r4 * r13
            float r4 = r4 + r1
            float r1 = (float) r9
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r5 = (float) r5
            float r5 = r5 * r13
            float r5 = r1 - r5
            android.graphics.Paint r6 = r0.paint
            r1 = r45
            r1.drawLine(r2, r3, r4, r5, r6)
        L_0x0b50:
            if (r15 == 0) goto L_0x0b95
            r1 = r30
            if (r15 == r1) goto L_0x0b97
            int r2 = r15.getIntrinsicWidth()
            float r2 = (float) r2
            float r2 = r2 * r28
            int r2 = (int) r2
            int r3 = r15.getIntrinsicHeight()
            float r3 = (float) r3
            float r3 = r3 * r28
            int r3 = (int) r3
            android.graphics.ColorFilter r4 = r0.colorFilter
            r15.setColorFilter(r4)
            int r4 = r0.currentIcon
            int r5 = r0.nextIcon
            if (r4 != r5) goto L_0x0b74
            r4 = 255(0xff, float:3.57E-43)
            goto L_0x0b7d
        L_0x0b74:
            float r4 = r0.transitionProgress
            r5 = 1065353216(0x3var_, float:1.0)
            float r4 = r5 - r4
            float r4 = r4 * r23
            int r4 = (int) r4
        L_0x0b7d:
            r15.setAlpha(r4)
            int r4 = r2 / 2
            int r4 = r10 - r4
            int r5 = r3 / 2
            int r5 = r11 - r5
            int r6 = r2 / 2
            int r6 = r6 + r10
            int r8 = r3 / 2
            int r8 = r8 + r11
            r15.setBounds(r4, r5, r6, r8)
            r15.draw(r7)
            goto L_0x0b97
        L_0x0b95:
            r1 = r30
        L_0x0b97:
            if (r1 == 0) goto L_0x0bd3
            int r2 = r1.getIntrinsicWidth()
            float r2 = (float) r2
            float r2 = r2 * r12
            int r2 = (int) r2
            int r3 = r1.getIntrinsicHeight()
            float r3 = (float) r3
            float r3 = r3 * r12
            int r3 = (int) r3
            android.graphics.ColorFilter r4 = r0.colorFilter
            r1.setColorFilter(r4)
            int r4 = r0.currentIcon
            int r5 = r0.nextIcon
            if (r4 != r5) goto L_0x0bb7
            r4 = 255(0xff, float:3.57E-43)
            goto L_0x0bbc
        L_0x0bb7:
            float r4 = r0.transitionProgress
            float r4 = r4 * r23
            int r4 = (int) r4
        L_0x0bbc:
            r1.setAlpha(r4)
            int r4 = r2 / 2
            int r4 = r10 - r4
            int r5 = r3 / 2
            int r5 = r11 - r5
            int r6 = r2 / 2
            int r6 = r6 + r10
            int r8 = r3 / 2
            int r8 = r8 + r11
            r1.setBounds(r4, r5, r6, r8)
            r1.draw(r7)
        L_0x0bd3:
            if (r29 == 0) goto L_0x0CLASSNAME
            r3 = r27
            r4 = r29
            if (r4 == r3) goto L_0x0CLASSNAME
            r2 = 1103101952(0x41CLASSNAME, float:24.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            android.graphics.Paint r5 = r0.paint2
            android.graphics.Paint$Style r6 = android.graphics.Paint.Style.FILL_AND_STROKE
            r5.setStyle(r6)
            android.graphics.Paint r5 = r0.paint2
            int r6 = r0.currentIcon
            int r8 = r0.nextIcon
            if (r6 != r8) goto L_0x0bf3
            r6 = 255(0xff, float:3.57E-43)
            goto L_0x0bfc
        L_0x0bf3:
            float r6 = r0.transitionProgress
            r8 = 1065353216(0x3var_, float:1.0)
            float r6 = r8 - r6
            float r6 = r6 * r23
            int r6 = (int) r6
        L_0x0bfc:
            r5.setAlpha(r6)
            r5 = 1
            r0.applyShaderMatrix(r5)
            r45.save()
            float r5 = (float) r10
            float r6 = (float) r11
            r7.translate(r5, r6)
            r5 = r28
            r7.scale(r5, r5)
            int r6 = -r2
            r8 = 2
            int r6 = r6 / r8
            float r6 = (float) r6
            int r9 = -r2
            int r9 = r9 / r8
            float r8 = (float) r9
            r7.translate(r6, r8)
            r6 = 0
            r8 = r4[r6]
            if (r8 == 0) goto L_0x0CLASSNAME
            r8 = r4[r6]
            android.graphics.Paint r6 = r0.paint2
            r7.drawPath(r8, r6)
        L_0x0CLASSNAME:
            r6 = 1
            r8 = r4[r6]
            if (r8 == 0) goto L_0x0CLASSNAME
            r8 = r4[r6]
            android.graphics.Paint r6 = r0.backPaint
            r7.drawPath(r8, r6)
        L_0x0CLASSNAME:
            r45.restore()
            goto L_0x0c3f
        L_0x0CLASSNAME:
            r5 = r28
            goto L_0x0c3f
        L_0x0CLASSNAME:
            r3 = r27
            r5 = r28
            r4 = r29
        L_0x0c3f:
            if (r3 == 0) goto L_0x0cca
            r2 = 1103101952(0x41CLASSNAME, float:24.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r6 = r0.currentIcon
            int r8 = r0.nextIcon
            if (r6 != r8) goto L_0x0CLASSNAME
            r6 = 255(0xff, float:3.57E-43)
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            float r6 = r0.transitionProgress
            float r6 = r6 * r23
            int r6 = (int) r6
        L_0x0CLASSNAME:
            android.graphics.Paint r8 = r0.paint2
            android.graphics.Paint$Style r9 = android.graphics.Paint.Style.FILL_AND_STROKE
            r8.setStyle(r9)
            android.graphics.Paint r8 = r0.paint2
            r8.setAlpha(r6)
            r8 = 1
            r0.applyShaderMatrix(r8)
            r45.save()
            float r8 = (float) r10
            float r9 = (float) r11
            r7.translate(r8, r9)
            r7.scale(r12, r12)
            int r8 = -r2
            r9 = 2
            int r8 = r8 / r9
            float r8 = (float) r8
            int r13 = -r2
            int r13 = r13 / r9
            float r9 = (float) r13
            r7.translate(r8, r9)
            r8 = 0
            r9 = r3[r8]
            if (r9 == 0) goto L_0x0CLASSNAME
            r9 = r3[r8]
            android.graphics.Paint r8 = r0.paint2
            r7.drawPath(r9, r8)
        L_0x0CLASSNAME:
            int r8 = r3.length
            r9 = 3
            if (r8 < r9) goto L_0x0CLASSNAME
            r8 = 2
            r9 = r3[r8]
            if (r9 == 0) goto L_0x0CLASSNAME
            r9 = r3[r8]
            android.graphics.Paint r8 = r0.paint
            r7.drawPath(r9, r8)
        L_0x0CLASSNAME:
            r8 = 1
            r9 = r3[r8]
            if (r9 == 0) goto L_0x0cc7
            r8 = 255(0xff, float:3.57E-43)
            if (r6 == r8) goto L_0x0cbf
            android.graphics.Paint r8 = r0.backPaint
            int r8 = r8.getAlpha()
            android.graphics.Paint r9 = r0.backPaint
            float r13 = (float) r8
            float r14 = (float) r6
            float r14 = r14 / r23
            float r13 = r13 * r14
            int r13 = (int) r13
            r9.setAlpha(r13)
            r9 = 1
            r13 = r3[r9]
            android.graphics.Paint r14 = r0.backPaint
            r7.drawPath(r13, r14)
            android.graphics.Paint r13 = r0.backPaint
            r13.setAlpha(r8)
            goto L_0x0cc7
        L_0x0cbf:
            r9 = 1
            r8 = r3[r9]
            android.graphics.Paint r9 = r0.backPaint
            r7.drawPath(r8, r9)
        L_0x0cc7:
            r45.restore()
        L_0x0cca:
            long r8 = java.lang.System.currentTimeMillis()
            long r13 = r0.lastAnimationTime
            long r13 = r8 - r13
            r17 = 17
            int r2 = (r13 > r17 ? 1 : (r13 == r17 ? 0 : -1))
            if (r2 <= 0) goto L_0x0cda
            r13 = 17
        L_0x0cda:
            r0.lastAnimationTime = r8
            int r2 = r0.currentIcon
            r6 = 3
            if (r2 == r6) goto L_0x0d01
            r6 = 14
            if (r2 == r6) goto L_0x0d01
            r6 = 4
            if (r2 != r6) goto L_0x0cf1
            int r6 = r0.nextIcon
            r30 = r1
            r1 = 14
            if (r6 == r1) goto L_0x0d03
            goto L_0x0cf3
        L_0x0cf1:
            r30 = r1
        L_0x0cf3:
            r1 = 10
            if (r2 == r1) goto L_0x0d03
            r1 = 13
            if (r2 != r1) goto L_0x0cfc
            goto L_0x0d03
        L_0x0cfc:
            r27 = r3
            r29 = r4
            goto L_0x0d56
        L_0x0d01:
            r30 = r1
        L_0x0d03:
            float r1 = r0.downloadRadOffset
            r17 = 360(0x168, double:1.78E-321)
            r27 = r3
            long r2 = r13 * r17
            float r2 = (float) r2
            r3 = 1159479296(0x451CLASSNAME, float:2500.0)
            float r2 = r2 / r3
            float r1 = r1 + r2
            r0.downloadRadOffset = r1
            float r1 = getCircleValue(r1)
            r0.downloadRadOffset = r1
            int r1 = r0.nextIcon
            r2 = 2
            if (r1 == r2) goto L_0x0d51
            float r1 = r0.downloadProgress
            float r2 = r0.downloadProgressAnimationStart
            float r3 = r1 - r2
            r6 = 0
            int r17 = (r3 > r6 ? 1 : (r3 == r6 ? 0 : -1))
            if (r17 <= 0) goto L_0x0d4e
            float r6 = r0.downloadProgressTime
            r29 = r4
            float r4 = (float) r13
            float r6 = r6 + r4
            r0.downloadProgressTime = r6
            r4 = 1128792064(0x43480000, float:200.0)
            int r4 = (r6 > r4 ? 1 : (r6 == r4 ? 0 : -1))
            if (r4 < 0) goto L_0x0d3f
            r0.animatedDownloadProgress = r1
            r0.downloadProgressAnimationStart = r1
            r1 = 0
            r0.downloadProgressTime = r1
            goto L_0x0d53
        L_0x0d3f:
            android.view.animation.DecelerateInterpolator r1 = r0.interpolator
            r4 = 1128792064(0x43480000, float:200.0)
            float r6 = r6 / r4
            float r1 = r1.getInterpolation(r6)
            float r1 = r1 * r3
            float r2 = r2 + r1
            r0.animatedDownloadProgress = r2
            goto L_0x0d53
        L_0x0d4e:
            r29 = r4
            goto L_0x0d53
        L_0x0d51:
            r29 = r4
        L_0x0d53:
            r44.invalidateSelf()
        L_0x0d56:
            boolean r1 = r0.animatingTransition
            if (r1 == 0) goto L_0x0d79
            float r1 = r0.transitionProgress
            r2 = 1065353216(0x3var_, float:1.0)
            int r3 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r3 >= 0) goto L_0x0d79
            float r3 = (float) r13
            float r4 = r0.transitionAnimationTime
            float r3 = r3 / r4
            float r1 = r1 + r3
            r0.transitionProgress = r1
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 < 0) goto L_0x0d76
            int r1 = r0.nextIcon
            r0.currentIcon = r1
            r0.transitionProgress = r2
            r1 = 0
            r0.animatingTransition = r1
        L_0x0d76:
            r44.invalidateSelf()
        L_0x0d79:
            r1 = r35
            r2 = 1
            if (r1 < r2) goto L_0x0d81
            r7.restoreToCount(r1)
        L_0x0d81:
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
