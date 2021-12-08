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

    /* JADX WARNING: Code restructure failed: missing block: B:322:0x08da, code lost:
        if (r0.nextIcon != 1) goto L_0x08de;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:344:0x090c, code lost:
        if (r2 == 1) goto L_0x0910;
     */
    /* JADX WARNING: Removed duplicated region for block: B:104:0x03b9  */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x0438  */
    /* JADX WARNING: Removed duplicated region for block: B:130:0x0447  */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x0470  */
    /* JADX WARNING: Removed duplicated region for block: B:177:0x0563  */
    /* JADX WARNING: Removed duplicated region for block: B:180:0x056e  */
    /* JADX WARNING: Removed duplicated region for block: B:182:0x0578  */
    /* JADX WARNING: Removed duplicated region for block: B:190:0x05ff  */
    /* JADX WARNING: Removed duplicated region for block: B:193:0x0607  */
    /* JADX WARNING: Removed duplicated region for block: B:206:0x062c  */
    /* JADX WARNING: Removed duplicated region for block: B:207:0x062f  */
    /* JADX WARNING: Removed duplicated region for block: B:220:0x0690  */
    /* JADX WARNING: Removed duplicated region for block: B:223:0x069f  */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x06a6  */
    /* JADX WARNING: Removed duplicated region for block: B:230:0x06cf  */
    /* JADX WARNING: Removed duplicated region for block: B:231:0x06d2  */
    /* JADX WARNING: Removed duplicated region for block: B:236:0x06df  */
    /* JADX WARNING: Removed duplicated region for block: B:237:0x06e4  */
    /* JADX WARNING: Removed duplicated region for block: B:243:0x06f5  */
    /* JADX WARNING: Removed duplicated region for block: B:244:0x06f8  */
    /* JADX WARNING: Removed duplicated region for block: B:249:0x0705  */
    /* JADX WARNING: Removed duplicated region for block: B:250:0x070a  */
    /* JADX WARNING: Removed duplicated region for block: B:258:0x0726  */
    /* JADX WARNING: Removed duplicated region for block: B:259:0x0731  */
    /* JADX WARNING: Removed duplicated region for block: B:275:0x07c7  */
    /* JADX WARNING: Removed duplicated region for block: B:276:0x07cb  */
    /* JADX WARNING: Removed duplicated region for block: B:282:0x07de  */
    /* JADX WARNING: Removed duplicated region for block: B:283:0x07e1  */
    /* JADX WARNING: Removed duplicated region for block: B:286:0x07fb  */
    /* JADX WARNING: Removed duplicated region for block: B:289:0x0838  */
    /* JADX WARNING: Removed duplicated region for block: B:296:0x084f  */
    /* JADX WARNING: Removed duplicated region for block: B:297:0x0852  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x00cf  */
    /* JADX WARNING: Removed duplicated region for block: B:303:0x087a  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x00d3  */
    /* JADX WARNING: Removed duplicated region for block: B:311:0x08c0  */
    /* JADX WARNING: Removed duplicated region for block: B:321:0x08d7  */
    /* JADX WARNING: Removed duplicated region for block: B:323:0x08dd  */
    /* JADX WARNING: Removed duplicated region for block: B:339:0x0903  */
    /* JADX WARNING: Removed duplicated region for block: B:340:0x0906  */
    /* JADX WARNING: Removed duplicated region for block: B:343:0x090b  */
    /* JADX WARNING: Removed duplicated region for block: B:345:0x090f  */
    /* JADX WARNING: Removed duplicated region for block: B:359:0x096a  */
    /* JADX WARNING: Removed duplicated region for block: B:360:0x096d  */
    /* JADX WARNING: Removed duplicated region for block: B:364:0x0975  */
    /* JADX WARNING: Removed duplicated region for block: B:372:0x09ac  */
    /* JADX WARNING: Removed duplicated region for block: B:395:0x0a12  */
    /* JADX WARNING: Removed duplicated region for block: B:396:0x0a16  */
    /* JADX WARNING: Removed duplicated region for block: B:420:0x0ad5  */
    /* JADX WARNING: Removed duplicated region for block: B:427:0x0b18  */
    /* JADX WARNING: Removed duplicated region for block: B:429:0x0b1c  */
    /* JADX WARNING: Removed duplicated region for block: B:435:0x0b58  */
    /* JADX WARNING: Removed duplicated region for block: B:448:0x0bc0  */
    /* JADX WARNING: Removed duplicated region for block: B:450:0x0bc6  */
    /* JADX WARNING: Removed duplicated region for block: B:471:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:484:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:485:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:498:0x0ce4  */
    /* JADX WARNING: Removed duplicated region for block: B:506:0x0d08  */
    /* JADX WARNING: Removed duplicated region for block: B:508:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void draw(android.graphics.Canvas r43) {
        /*
            r42 = this;
            r0 = r42
            r7 = r43
            android.graphics.Rect r8 = r42.getBounds()
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
            int r1 = r43.save()
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
            int r1 = r43.save()
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
            r1 = r43
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
            r43.save()
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
            r1 = r43
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
            r1 = r43
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
            r1 = r43
            r1.drawLine(r2, r3, r4, r5, r6)
            goto L_0x0306
        L_0x0303:
            r40 = r5
            r12 = r6
        L_0x0306:
            r1 = 0
            int r2 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1))
            if (r2 == 0) goto L_0x0357
            r43.restore()
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
            r1 = r43
            r3 = r25
            r5 = r36
            r1.drawLine(r2, r3, r4, r5, r6)
        L_0x0392:
            float r1 = (float) r10
            int r1 = (r38 > r1 ? 1 : (r38 == r1 ? 0 : -1))
            if (r1 == 0) goto L_0x03ad
            float r4 = (float) r10
            android.graphics.Paint r6 = r0.paint
            r1 = r43
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
            goto L_0x0695
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
            if (r13 == 0) goto L_0x0695
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
            r1 = r43
            r4 = r15
            r1.drawArc(r2, r3, r4, r5, r6)
            goto L_0x0695
        L_0x0438:
            r14 = 1082130432(0x40800000, float:4.0)
        L_0x043a:
            r1 = 0
            r0.applyShaderMatrix(r1)
            r1 = 1065353216(0x3var_, float:1.0)
            r2 = 0
            r3 = 0
            int r4 = r0.nextIcon
            r5 = 2
            if (r4 != r5) goto L_0x0470
            float r4 = r0.transitionProgress
            int r5 = (r4 > r22 ? 1 : (r4 == r22 ? 0 : -1))
            if (r5 > 0) goto L_0x0462
            float r4 = r4 / r22
            r5 = 1065353216(0x3var_, float:1.0)
            float r6 = r5 - r4
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r20)
            float r5 = (float) r5
            float r5 = r5 * r6
            float r13 = r0.scale
            float r5 = r5 * r13
            float r13 = r6 * r23
            int r4 = (int) r13
            goto L_0x0464
        L_0x0462:
            r5 = 0
            r4 = 0
        L_0x0464:
            r6 = 0
            r9 = r1
            r12 = r2
            r15 = r3
            r27 = r5
            r5 = r6
            r13 = 1065353216(0x3var_, float:1.0)
            r6 = r4
            goto L_0x055f
        L_0x0470:
            r5 = 15
            if (r4 == r5) goto L_0x052b
            if (r4 == 0) goto L_0x052b
            if (r4 == r12) goto L_0x052b
            r5 = 5
            if (r4 == r5) goto L_0x052b
            r5 = 8
            if (r4 == r5) goto L_0x052b
            r5 = 9
            if (r4 == r5) goto L_0x052b
            r5 = 7
            if (r4 == r5) goto L_0x052b
            r5 = 6
            if (r4 != r5) goto L_0x048b
            goto L_0x052b
        L_0x048b:
            r5 = 4
            if (r4 != r5) goto L_0x04cc
            float r4 = r0.transitionProgress
            r5 = 1065353216(0x3var_, float:1.0)
            float r6 = r5 - r4
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r20)
            float r5 = (float) r5
            float r13 = r0.scale
            float r5 = r5 * r13
            float r13 = r6 * r23
            int r13 = (int) r13
            int r15 = r0.currentIcon
            r12 = 14
            if (r15 != r12) goto L_0x04b0
            r12 = 0
            r1 = r6
            int r15 = r8.left
            float r2 = (float) r15
            int r15 = r8.top
            float r3 = (float) r15
            r6 = r12
            goto L_0x04c1
        L_0x04b0:
            r12 = 1110704128(0x42340000, float:45.0)
            float r12 = r12 * r4
            r1 = 1065353216(0x3var_, float:1.0)
            int r15 = r8.centerX()
            float r2 = (float) r15
            int r15 = r8.centerY()
            float r3 = (float) r15
            r6 = r12
        L_0x04c1:
            r9 = r1
            r12 = r2
            r15 = r3
            r27 = r5
            r5 = r6
            r6 = r13
            r13 = 1065353216(0x3var_, float:1.0)
            goto L_0x055f
        L_0x04cc:
            r5 = 14
            if (r4 == r5) goto L_0x04eb
            r5 = 3
            if (r4 != r5) goto L_0x04d4
            goto L_0x04eb
        L_0x04d4:
            r6 = 0
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r20)
            float r4 = (float) r4
            float r5 = r0.scale
            float r5 = r5 * r4
            r4 = 255(0xff, float:3.57E-43)
            r9 = r1
            r12 = r2
            r15 = r3
            r27 = r5
            r5 = r6
            r13 = 1065353216(0x3var_, float:1.0)
            r6 = r4
            goto L_0x055f
        L_0x04eb:
            float r4 = r0.transitionProgress
            r5 = 1065353216(0x3var_, float:1.0)
            float r6 = r5 - r4
            int r5 = r0.currentIcon
            r12 = 4
            if (r5 != r12) goto L_0x04f9
            r5 = 0
            r1 = r4
            goto L_0x04ff
        L_0x04f9:
            r5 = 1110704128(0x42340000, float:45.0)
            float r5 = r5 * r6
            r1 = 1065353216(0x3var_, float:1.0)
        L_0x04ff:
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r20)
            float r12 = (float) r12
            float r13 = r0.scale
            float r12 = r12 * r13
            float r13 = r4 * r23
            int r13 = (int) r13
            int r15 = r0.nextIcon
            r9 = 14
            if (r15 != r9) goto L_0x0518
            int r9 = r8.left
            float r2 = (float) r9
            int r9 = r8.top
            float r3 = (float) r9
            goto L_0x0522
        L_0x0518:
            int r9 = r8.centerX()
            float r2 = (float) r9
            int r9 = r8.centerY()
            float r3 = (float) r9
        L_0x0522:
            r9 = r1
            r15 = r3
            r27 = r12
            r6 = r13
            r13 = 1065353216(0x3var_, float:1.0)
            r12 = r2
            goto L_0x055f
        L_0x052b:
            r5 = 6
            if (r4 != r5) goto L_0x0539
            float r4 = r0.transitionProgress
            float r4 = r4 / r22
            r5 = 1065353216(0x3var_, float:1.0)
            float r4 = java.lang.Math.min(r5, r4)
            goto L_0x053d
        L_0x0539:
            r5 = 1065353216(0x3var_, float:1.0)
            float r4 = r0.transitionProgress
        L_0x053d:
            float r6 = r5 - r4
            r5 = 1110704128(0x42340000, float:45.0)
            float r5 = r5 * r4
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r20)
            float r9 = (float) r9
            float r9 = r9 * r6
            float r12 = r0.scale
            float r9 = r9 * r12
            float r12 = r6 * r21
            r13 = 1065353216(0x3var_, float:1.0)
            float r12 = java.lang.Math.min(r13, r12)
            float r12 = r12 * r23
            int r4 = (int) r12
            r12 = r2
            r15 = r3
            r6 = r4
            r27 = r9
            r9 = r1
        L_0x055f:
            int r1 = (r9 > r13 ? 1 : (r9 == r13 ? 0 : -1))
            if (r1 == 0) goto L_0x0569
            r43.save()
            r7.scale(r9, r9, r12, r15)
        L_0x0569:
            r1 = 0
            int r2 = (r5 > r1 ? 1 : (r5 == r1 ? 0 : -1))
            if (r2 == 0) goto L_0x0576
            r43.save()
            float r1 = (float) r10
            float r2 = (float) r11
            r7.rotate(r5, r1, r2)
        L_0x0576:
            if (r6 == 0) goto L_0x05ff
            android.graphics.Paint r1 = r0.paint
            float r2 = (float) r6
            float r3 = r0.overrideAlpha
            float r2 = r2 * r3
            int r2 = (int) r2
            r1.setAlpha(r2)
            int r1 = r0.currentIcon
            r2 = 14
            if (r1 == r2) goto L_0x05c0
            int r1 = r0.nextIcon
            if (r1 != r2) goto L_0x0591
            r28 = r5
            r13 = r6
            goto L_0x05c3
        L_0x0591:
            float r1 = (float) r10
            float r2 = r1 - r27
            float r1 = (float) r11
            float r3 = r1 - r27
            float r1 = (float) r10
            float r4 = r1 + r27
            float r1 = (float) r11
            float r13 = r1 + r27
            android.graphics.Paint r1 = r0.paint
            r19 = r1
            r1 = r43
            r28 = r5
            r5 = r13
            r13 = r6
            r6 = r19
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
            r1 = r43
            r1.drawLine(r2, r3, r4, r5, r6)
            goto L_0x0602
        L_0x05c0:
            r28 = r5
            r13 = r6
        L_0x05c3:
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
            goto L_0x0602
        L_0x05ff:
            r28 = r5
            r13 = r6
        L_0x0602:
            r1 = 0
            int r2 = (r28 > r1 ? 1 : (r28 == r1 ? 0 : -1))
            if (r2 == 0) goto L_0x060a
            r43.restore()
        L_0x060a:
            int r1 = r0.currentIcon
            r2 = 3
            if (r1 == r2) goto L_0x061c
            r3 = 14
            if (r1 == r3) goto L_0x061c
            r4 = 4
            if (r1 != r4) goto L_0x068a
            int r1 = r0.nextIcon
            if (r1 == r3) goto L_0x061c
            if (r1 != r2) goto L_0x068a
        L_0x061c:
            if (r13 == 0) goto L_0x068a
            r1 = 1135869952(0x43b40000, float:360.0)
            float r2 = r0.animatedDownloadProgress
            float r2 = r2 * r1
            float r19 = java.lang.Math.max(r14, r2)
            boolean r1 = r0.isMini
            if (r1 == 0) goto L_0x062f
            r3 = 1073741824(0x40000000, float:2.0)
            goto L_0x0631
        L_0x062f:
            r3 = 1082130432(0x40800000, float:4.0)
        L_0x0631:
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
            int r1 = r0.currentIcon
            r2 = 14
            if (r1 == r2) goto L_0x065a
            r3 = 4
            if (r1 != r3) goto L_0x067c
            int r1 = r0.nextIcon
            if (r1 == r2) goto L_0x065a
            r2 = 3
            if (r1 != r2) goto L_0x067c
        L_0x065a:
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
            r1 = r43
            r1.drawArc(r2, r3, r4, r5, r6)
            android.graphics.Paint r1 = r0.paint
            r1.setAlpha(r13)
        L_0x067c:
            android.graphics.RectF r2 = r0.rect
            float r3 = r0.downloadRadOffset
            r5 = 0
            android.graphics.Paint r6 = r0.paint
            r1 = r43
            r4 = r19
            r1.drawArc(r2, r3, r4, r5, r6)
        L_0x068a:
            r1 = 1065353216(0x3var_, float:1.0)
            int r2 = (r9 > r1 ? 1 : (r9 == r1 ? 0 : -1))
            if (r2 == 0) goto L_0x03d1
            r43.restore()
            goto L_0x03d1
        L_0x0695:
            r1 = 0
            r2 = 0
            r3 = 0
            r4 = 0
            int r5 = r0.currentIcon
            int r6 = r0.nextIcon
            if (r5 != r6) goto L_0x06a6
            r6 = 1065353216(0x3var_, float:1.0)
            r5 = r6
            r9 = r6
            r12 = r9
            r9 = r5
            goto L_0x06c9
        L_0x06a6:
            r6 = 1065353216(0x3var_, float:1.0)
            r9 = 4
            if (r5 != r9) goto L_0x06b4
            float r9 = r0.transitionProgress
            float r5 = r0.transitionProgress
            float r5 = r6 - r5
            r12 = r9
            r9 = r5
            goto L_0x06c9
        L_0x06b4:
            float r5 = r0.transitionProgress
            float r5 = r5 / r22
            float r9 = java.lang.Math.min(r6, r5)
            float r5 = r0.transitionProgress
            float r5 = r5 / r22
            float r5 = r6 - r5
            r6 = 0
            float r5 = java.lang.Math.max(r6, r5)
            r12 = r9
            r9 = r5
        L_0x06c9:
            int r5 = r0.nextIcon
            r6 = 15
            if (r5 != r6) goto L_0x06d2
            android.graphics.Path[] r3 = org.telegram.ui.ActionBar.Theme.chat_updatePath
            goto L_0x06da
        L_0x06d2:
            int r5 = r0.currentIcon
            r6 = 15
            if (r5 != r6) goto L_0x06da
            android.graphics.Path[] r4 = org.telegram.ui.ActionBar.Theme.chat_updatePath
        L_0x06da:
            int r5 = r0.nextIcon
            r6 = 5
            if (r5 != r6) goto L_0x06e4
            android.graphics.Path[] r3 = org.telegram.ui.ActionBar.Theme.chat_filePath
            r13 = r3
            r14 = r4
            goto L_0x06f0
        L_0x06e4:
            int r5 = r0.currentIcon
            r6 = 5
            if (r5 != r6) goto L_0x06ee
            android.graphics.Path[] r4 = org.telegram.ui.ActionBar.Theme.chat_filePath
            r13 = r3
            r14 = r4
            goto L_0x06f0
        L_0x06ee:
            r13 = r3
            r14 = r4
        L_0x06f0:
            int r3 = r0.nextIcon
            r4 = 7
            if (r3 != r4) goto L_0x06f8
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.chat_flameIcon
            goto L_0x06ff
        L_0x06f8:
            int r3 = r0.currentIcon
            r4 = 7
            if (r3 != r4) goto L_0x06ff
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.chat_flameIcon
        L_0x06ff:
            int r3 = r0.nextIcon
            r4 = 8
            if (r3 != r4) goto L_0x070a
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.chat_gifIcon
            r15 = r1
            r6 = r2
            goto L_0x0717
        L_0x070a:
            int r3 = r0.currentIcon
            r4 = 8
            if (r3 != r4) goto L_0x0715
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.chat_gifIcon
            r15 = r1
            r6 = r2
            goto L_0x0717
        L_0x0715:
            r15 = r1
            r6 = r2
        L_0x0717:
            int r1 = r0.currentIcon
            r2 = 9
            r19 = 1086324736(0x40CLASSNAME, float:6.0)
            if (r1 == r2) goto L_0x0731
            int r1 = r0.nextIcon
            r2 = 9
            if (r1 != r2) goto L_0x0726
            goto L_0x0731
        L_0x0726:
            r28 = r9
            r27 = r13
            r29 = r14
            r30 = r15
            r15 = r6
            goto L_0x07b1
        L_0x0731:
            r1 = 0
            r0.applyShaderMatrix(r1)
            android.graphics.Paint r1 = r0.paint
            int r2 = r0.currentIcon
            int r3 = r0.nextIcon
            if (r2 != r3) goto L_0x0740
            r2 = 255(0xff, float:3.57E-43)
            goto L_0x0745
        L_0x0740:
            float r2 = r0.transitionProgress
            float r2 = r2 * r23
            int r2 = (int) r2
        L_0x0745:
            r1.setAlpha(r2)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r5 = r11 + r1
            r1 = 1077936128(0x40400000, float:3.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r4 = r10 - r1
            int r1 = r0.currentIcon
            int r2 = r0.nextIcon
            if (r1 == r2) goto L_0x0766
            r43.save()
            float r1 = r0.transitionProgress
            float r2 = (float) r10
            float r3 = (float) r11
            r7.scale(r1, r1, r2, r3)
        L_0x0766:
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
            r1 = r43
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
            r1 = r43
            r1.drawLine(r2, r3, r4, r5, r6)
            int r1 = r0.currentIcon
            int r2 = r0.nextIcon
            if (r1 == r2) goto L_0x07b1
            r43.restore()
        L_0x07b1:
            int r1 = r0.currentIcon
            r2 = 12
            if (r1 == r2) goto L_0x07bd
            int r1 = r0.nextIcon
            r2 = 12
            if (r1 != r2) goto L_0x083b
        L_0x07bd:
            r1 = 0
            r0.applyShaderMatrix(r1)
            int r1 = r0.currentIcon
            int r2 = r0.nextIcon
            if (r1 != r2) goto L_0x07cb
            r3 = 1065353216(0x3var_, float:1.0)
            r9 = r3
            goto L_0x07da
        L_0x07cb:
            r3 = 13
            if (r2 != r3) goto L_0x07d3
            float r3 = r0.transitionProgress
            r9 = r3
            goto L_0x07da
        L_0x07d3:
            float r3 = r0.transitionProgress
            r4 = 1065353216(0x3var_, float:1.0)
            float r3 = r4 - r3
            r9 = r3
        L_0x07da:
            android.graphics.Paint r3 = r0.paint
            if (r1 != r2) goto L_0x07e1
            r2 = 255(0xff, float:3.57E-43)
            goto L_0x07e4
        L_0x07e1:
            float r1 = r9 * r23
            int r2 = (int) r1
        L_0x07e4:
            r3.setAlpha(r2)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r13 = r11 + r1
            r1 = 1077936128(0x40400000, float:3.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r14 = r10 - r1
            int r1 = r0.currentIcon
            int r2 = r0.nextIcon
            if (r1 == r2) goto L_0x0803
            r43.save()
            float r1 = (float) r10
            float r2 = (float) r11
            r7.scale(r9, r9, r1, r2)
        L_0x0803:
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
            r1 = r43
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
            r1 = r43
            r1.drawLine(r2, r3, r4, r5, r6)
            int r1 = r0.currentIcon
            int r2 = r0.nextIcon
            if (r1 == r2) goto L_0x083b
            r43.restore()
        L_0x083b:
            int r1 = r0.currentIcon
            r2 = 13
            if (r1 == r2) goto L_0x0845
            int r1 = r0.nextIcon
            if (r1 != r2) goto L_0x08c3
        L_0x0845:
            r1 = 0
            r0.applyShaderMatrix(r1)
            int r1 = r0.currentIcon
            int r2 = r0.nextIcon
            if (r1 != r2) goto L_0x0852
            r1 = 1065353216(0x3var_, float:1.0)
            goto L_0x085f
        L_0x0852:
            r1 = 13
            if (r2 != r1) goto L_0x0859
            float r1 = r0.transitionProgress
            goto L_0x085f
        L_0x0859:
            float r1 = r0.transitionProgress
            r2 = 1065353216(0x3var_, float:1.0)
            float r1 = r2 - r1
        L_0x085f:
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
            if (r4 == r5) goto L_0x0882
            r43.save()
            float r4 = (float) r10
            float r5 = (float) r11
            r7.scale(r1, r1, r4, r5)
        L_0x0882:
            float r4 = r0.animatedDownloadProgress
            float r4 = r4 * r18
            int r4 = (int) r4
            java.lang.String r5 = r0.percentString
            if (r5 == 0) goto L_0x088f
            int r5 = r0.lastPercent
            if (r4 == r5) goto L_0x08b1
        L_0x088f:
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
        L_0x08b1:
            java.lang.String r5 = r0.percentString
            float r6 = (float) r3
            float r9 = (float) r2
            android.text.TextPaint r13 = r0.textPaint
            r7.drawText(r5, r6, r9, r13)
            int r5 = r0.currentIcon
            int r6 = r0.nextIcon
            if (r5 == r6) goto L_0x08c3
            r43.restore()
        L_0x08c3:
            int r1 = r0.currentIcon
            if (r1 == 0) goto L_0x08d5
            r2 = 1
            if (r1 == r2) goto L_0x08d5
            int r3 = r0.nextIcon
            if (r3 == 0) goto L_0x08d5
            if (r3 != r2) goto L_0x08d1
            goto L_0x08d5
        L_0x08d1:
            r9 = 255(0xff, float:3.57E-43)
            goto L_0x0a08
        L_0x08d5:
            if (r1 != 0) goto L_0x08dd
            int r2 = r0.nextIcon
            r3 = 1
            if (r2 == r3) goto L_0x08e4
            goto L_0x08de
        L_0x08dd:
            r3 = 1
        L_0x08de:
            if (r1 != r3) goto L_0x0900
            int r2 = r0.nextIcon
            if (r2 != 0) goto L_0x0900
        L_0x08e4:
            boolean r2 = r0.animatingTransition
            if (r2 == 0) goto L_0x08f6
            int r2 = r0.nextIcon
            if (r2 != 0) goto L_0x08f3
            float r2 = r0.transitionProgress
            r3 = 1065353216(0x3var_, float:1.0)
            float r5 = r3 - r2
            goto L_0x0907
        L_0x08f3:
            float r5 = r0.transitionProgress
            goto L_0x0907
        L_0x08f6:
            int r2 = r0.nextIcon
            r3 = 1
            if (r2 != r3) goto L_0x08fe
            r5 = 1065353216(0x3var_, float:1.0)
            goto L_0x08ff
        L_0x08fe:
            r5 = 0
        L_0x08ff:
            goto L_0x0907
        L_0x0900:
            r2 = 1
            if (r1 != r2) goto L_0x0906
            r5 = 1065353216(0x3var_, float:1.0)
            goto L_0x0907
        L_0x0906:
            r5 = 0
        L_0x0907:
            int r2 = r0.nextIcon
            if (r2 == 0) goto L_0x090f
            r3 = 1
            if (r2 != r3) goto L_0x0914
            goto L_0x0910
        L_0x090f:
            r3 = 1
        L_0x0910:
            if (r1 == 0) goto L_0x093a
            if (r1 == r3) goto L_0x093a
        L_0x0914:
            r3 = 4
            if (r2 != r3) goto L_0x0928
            android.graphics.Paint r1 = r0.paint2
            float r2 = r0.transitionProgress
            r3 = 1065353216(0x3var_, float:1.0)
            float r2 = r3 - r2
            float r2 = r2 * r23
            int r2 = (int) r2
            r1.setAlpha(r2)
            r9 = 255(0xff, float:3.57E-43)
            goto L_0x0941
        L_0x0928:
            android.graphics.Paint r3 = r0.paint2
            if (r1 != r2) goto L_0x092f
            r2 = 255(0xff, float:3.57E-43)
            goto L_0x0934
        L_0x092f:
            float r1 = r0.transitionProgress
            float r1 = r1 * r23
            int r2 = (int) r1
        L_0x0934:
            r3.setAlpha(r2)
            r9 = 255(0xff, float:3.57E-43)
            goto L_0x0941
        L_0x093a:
            android.graphics.Paint r1 = r0.paint2
            r9 = 255(0xff, float:3.57E-43)
            r1.setAlpha(r9)
        L_0x0941:
            r1 = 1
            r0.applyShaderMatrix(r1)
            r43.save()
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
            if (r2 != r3) goto L_0x096d
            r3 = 1119092736(0x42b40000, float:90.0)
            goto L_0x096e
        L_0x096d:
            r3 = 0
        L_0x096e:
            if (r2 != 0) goto L_0x09ac
            int r4 = r0.nextIcon
            r6 = 1
            if (r4 != r6) goto L_0x09ac
            r2 = 1136656384(0x43CLASSNAME, float:384.0)
            int r2 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r2 >= 0) goto L_0x098b
            r2 = 1119748096(0x42be0000, float:95.0)
            org.telegram.ui.Components.CubicBezierInterpolator r4 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_BOTH
            r6 = 1136656384(0x43CLASSNAME, float:384.0)
            float r6 = r1 / r6
            float r4 = r4.getInterpolation(r6)
            float r4 = r4 * r2
            r3 = r4
            goto L_0x09a9
        L_0x098b:
            r2 = 1139933184(0x43var_, float:484.0)
            int r2 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r2 >= 0) goto L_0x09a6
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
            goto L_0x09a9
        L_0x09a6:
            r2 = 1119092736(0x42b40000, float:90.0)
            r3 = r2
        L_0x09a9:
            float r1 = r1 + r18
            goto L_0x09e0
        L_0x09ac:
            r4 = 1
            if (r2 != r4) goto L_0x09e0
            int r2 = r0.nextIcon
            if (r2 != 0) goto L_0x09e0
            int r2 = (r1 > r18 ? 1 : (r1 == r18 ? 0 : -1))
            if (r2 >= 0) goto L_0x09c4
            r2 = -1063256064(0xffffffffc0a00000, float:-5.0)
            org.telegram.ui.Components.CubicBezierInterpolator r4 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_BOTH
            float r6 = r1 / r18
            float r4 = r4.getInterpolation(r6)
            float r3 = r4 * r2
            goto L_0x09e0
        L_0x09c4:
            r2 = 1139933184(0x43var_, float:484.0)
            int r2 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r2 >= 0) goto L_0x09de
            r2 = -1063256064(0xffffffffc0a00000, float:-5.0)
            r4 = 1119748096(0x42be0000, float:95.0)
            org.telegram.ui.Components.CubicBezierInterpolator r6 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_BOTH
            float r13 = r1 - r18
            r14 = 1136656384(0x43CLASSNAME, float:384.0)
            float r13 = r13 / r14
            float r6 = r6.getInterpolation(r13)
            float r6 = r6 * r4
            float r3 = r6 + r2
            goto L_0x09e0
        L_0x09de:
            r3 = 1119092736(0x42b40000, float:90.0)
        L_0x09e0:
            r7.rotate(r3)
            int r2 = r0.currentIcon
            if (r2 == 0) goto L_0x09ea
            r4 = 1
            if (r2 != r4) goto L_0x09ed
        L_0x09ea:
            r4 = 4
            if (r2 != r4) goto L_0x09f0
        L_0x09ed:
            r7.scale(r12, r12)
        L_0x09f0:
            org.telegram.ui.Components.PathAnimator r2 = org.telegram.ui.ActionBar.Theme.playPauseAnimator
            android.graphics.Paint r4 = r0.paint2
            r2.draw(r7, r4, r1)
            r2 = -1082130432(0xffffffffbvar_, float:-1.0)
            r4 = 1065353216(0x3var_, float:1.0)
            r7.scale(r4, r2)
            org.telegram.ui.Components.PathAnimator r2 = org.telegram.ui.ActionBar.Theme.playPauseAnimator
            android.graphics.Paint r4 = r0.paint2
            r2.draw(r7, r4, r1)
            r43.restore()
        L_0x0a08:
            int r1 = r0.currentIcon
            r2 = 6
            if (r1 == r2) goto L_0x0a16
            int r1 = r0.nextIcon
            if (r1 != r2) goto L_0x0a12
            goto L_0x0a16
        L_0x0a12:
            r19 = r8
            goto L_0x0ad3
        L_0x0a16:
            r1 = 0
            r0.applyShaderMatrix(r1)
            int r1 = r0.currentIcon
            if (r1 == r2) goto L_0x0a49
            float r1 = r0.transitionProgress
            int r2 = (r1 > r22 ? 1 : (r1 == r22 ? 0 : -1))
            if (r2 <= 0) goto L_0x0a3e
            float r1 = r1 - r22
            float r1 = r1 / r22
            float r2 = r1 / r22
            r3 = 1065353216(0x3var_, float:1.0)
            float r2 = java.lang.Math.min(r3, r2)
            float r5 = r3 - r2
            int r2 = (r1 > r22 ? 1 : (r1 == r22 ? 0 : -1))
            if (r2 <= 0) goto L_0x0a3b
            float r2 = r1 - r22
            float r2 = r2 / r22
            goto L_0x0a3c
        L_0x0a3b:
            r2 = 0
        L_0x0a3c:
            r1 = r2
            goto L_0x0a41
        L_0x0a3e:
            r5 = 1065353216(0x3var_, float:1.0)
            r1 = 0
        L_0x0a41:
            android.graphics.Paint r2 = r0.paint
            r2.setAlpha(r9)
            r13 = r1
            r14 = r5
            goto L_0x0a67
        L_0x0a49:
            r5 = 0
            r1 = 1065353216(0x3var_, float:1.0)
            int r2 = r0.nextIcon
            r3 = 6
            if (r2 == r3) goto L_0x0a60
            android.graphics.Paint r2 = r0.paint
            float r3 = r0.transitionProgress
            r4 = 1065353216(0x3var_, float:1.0)
            float r3 = r4 - r3
            float r3 = r3 * r23
            int r3 = (int) r3
            r2.setAlpha(r3)
            goto L_0x0a65
        L_0x0a60:
            android.graphics.Paint r2 = r0.paint
            r2.setAlpha(r9)
        L_0x0a65:
            r13 = r1
            r14 = r5
        L_0x0a67:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r6 = r11 + r1
            r1 = 1077936128(0x40400000, float:3.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r5 = r10 - r1
            r1 = 1065353216(0x3var_, float:1.0)
            int r2 = (r14 > r1 ? 1 : (r14 == r1 ? 0 : -1))
            if (r2 >= 0) goto L_0x0aae
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
            r1 = r43
            r19 = r8
            r8 = r5
            r5 = r9
            r9 = r6
            r6 = r18
            r1.drawLine(r2, r3, r4, r5, r6)
            goto L_0x0ab2
        L_0x0aae:
            r9 = r6
            r19 = r8
            r8 = r5
        L_0x0ab2:
            r1 = 0
            int r2 = (r13 > r1 ? 1 : (r13 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x0ad3
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
            r1 = r43
            r1.drawLine(r2, r3, r4, r5, r6)
        L_0x0ad3:
            if (r15 == 0) goto L_0x0b18
            r1 = r30
            if (r15 == r1) goto L_0x0b1a
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
            if (r4 != r5) goto L_0x0af7
            r4 = 255(0xff, float:3.57E-43)
            goto L_0x0b00
        L_0x0af7:
            float r4 = r0.transitionProgress
            r5 = 1065353216(0x3var_, float:1.0)
            float r4 = r5 - r4
            float r4 = r4 * r23
            int r4 = (int) r4
        L_0x0b00:
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
            goto L_0x0b1a
        L_0x0b18:
            r1 = r30
        L_0x0b1a:
            if (r1 == 0) goto L_0x0b56
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
            if (r4 != r5) goto L_0x0b3a
            r4 = 255(0xff, float:3.57E-43)
            goto L_0x0b3f
        L_0x0b3a:
            float r4 = r0.transitionProgress
            float r4 = r4 * r23
            int r4 = (int) r4
        L_0x0b3f:
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
        L_0x0b56:
            if (r29 == 0) goto L_0x0bc0
            r3 = r27
            r4 = r29
            if (r4 == r3) goto L_0x0bc4
            r2 = 1103101952(0x41CLASSNAME, float:24.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            float r2 = r2 * r28
            int r2 = (int) r2
            r5 = 1103101952(0x41CLASSNAME, float:24.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            float r5 = r5 * r28
            int r5 = (int) r5
            android.graphics.Paint r6 = r0.paint2
            android.graphics.Paint$Style r8 = android.graphics.Paint.Style.FILL_AND_STROKE
            r6.setStyle(r8)
            android.graphics.Paint r6 = r0.paint2
            int r8 = r0.currentIcon
            int r9 = r0.nextIcon
            if (r8 != r9) goto L_0x0b84
            r8 = 255(0xff, float:3.57E-43)
            goto L_0x0b8d
        L_0x0b84:
            float r8 = r0.transitionProgress
            r9 = 1065353216(0x3var_, float:1.0)
            float r8 = r9 - r8
            float r8 = r8 * r23
            int r8 = (int) r8
        L_0x0b8d:
            r6.setAlpha(r8)
            r6 = 1
            r0.applyShaderMatrix(r6)
            r43.save()
            int r6 = r2 / 2
            int r6 = r10 - r6
            float r6 = (float) r6
            int r8 = r5 / 2
            int r8 = r11 - r8
            float r8 = (float) r8
            r7.translate(r6, r8)
            r6 = 0
            r8 = r4[r6]
            if (r8 == 0) goto L_0x0bb0
            r8 = r4[r6]
            android.graphics.Paint r6 = r0.paint2
            r7.drawPath(r8, r6)
        L_0x0bb0:
            r6 = 1
            r8 = r4[r6]
            if (r8 == 0) goto L_0x0bbc
            r8 = r4[r6]
            android.graphics.Paint r6 = r0.backPaint
            r7.drawPath(r8, r6)
        L_0x0bbc:
            r43.restore()
            goto L_0x0bc4
        L_0x0bc0:
            r3 = r27
            r4 = r29
        L_0x0bc4:
            if (r3 == 0) goto L_0x0CLASSNAME
            r2 = 1103101952(0x41CLASSNAME, float:24.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            float r2 = r2 * r12
            int r2 = (int) r2
            r5 = 1103101952(0x41CLASSNAME, float:24.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            float r5 = r5 * r12
            int r5 = (int) r5
            int r6 = r0.currentIcon
            int r8 = r0.nextIcon
            if (r6 != r8) goto L_0x0be3
            r6 = 255(0xff, float:3.57E-43)
            goto L_0x0be8
        L_0x0be3:
            float r6 = r0.transitionProgress
            float r6 = r6 * r23
            int r6 = (int) r6
        L_0x0be8:
            android.graphics.Paint r8 = r0.paint2
            android.graphics.Paint$Style r9 = android.graphics.Paint.Style.FILL_AND_STROKE
            r8.setStyle(r9)
            android.graphics.Paint r8 = r0.paint2
            r8.setAlpha(r6)
            r8 = 1
            r0.applyShaderMatrix(r8)
            r43.save()
            int r8 = r2 / 2
            int r8 = r10 - r8
            float r8 = (float) r8
            int r9 = r5 / 2
            int r9 = r11 - r9
            float r9 = (float) r9
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
            if (r9 == 0) goto L_0x0CLASSNAME
            r8 = 255(0xff, float:3.57E-43)
            if (r6 == r8) goto L_0x0c4d
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
            goto L_0x0CLASSNAME
        L_0x0c4d:
            r9 = 1
            r8 = r3[r9]
            android.graphics.Paint r9 = r0.backPaint
            r7.drawPath(r8, r9)
        L_0x0CLASSNAME:
            r43.restore()
        L_0x0CLASSNAME:
            long r5 = java.lang.System.currentTimeMillis()
            long r8 = r0.lastAnimationTime
            long r8 = r5 - r8
            r13 = 17
            int r2 = (r8 > r13 ? 1 : (r8 == r13 ? 0 : -1))
            if (r2 <= 0) goto L_0x0CLASSNAME
            r8 = 17
        L_0x0CLASSNAME:
            r0.lastAnimationTime = r5
            int r2 = r0.currentIcon
            r13 = 3
            if (r2 == r13) goto L_0x0CLASSNAME
            r13 = 14
            if (r2 == r13) goto L_0x0CLASSNAME
            r14 = 4
            if (r2 != r14) goto L_0x0c7a
            int r14 = r0.nextIcon
            if (r14 == r13) goto L_0x0CLASSNAME
        L_0x0c7a:
            r13 = 10
            if (r2 == r13) goto L_0x0CLASSNAME
            r13 = 13
            if (r2 != r13) goto L_0x0CLASSNAME
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r30 = r1
            r27 = r3
            goto L_0x0ce0
        L_0x0CLASSNAME:
            float r2 = r0.downloadRadOffset
            r13 = 360(0x168, double:1.78E-321)
            long r13 = r13 * r8
            float r13 = (float) r13
            r14 = 1159479296(0x451CLASSNAME, float:2500.0)
            float r13 = r13 / r14
            float r2 = r2 + r13
            r0.downloadRadOffset = r2
            float r2 = getCircleValue(r2)
            r0.downloadRadOffset = r2
            int r2 = r0.nextIcon
            r13 = 2
            if (r2 == r13) goto L_0x0cd9
            float r2 = r0.downloadProgress
            float r13 = r0.downloadProgressAnimationStart
            float r14 = r2 - r13
            r17 = 0
            int r18 = (r14 > r17 ? 1 : (r14 == r17 ? 0 : -1))
            if (r18 <= 0) goto L_0x0cd4
            r30 = r1
            float r1 = r0.downloadProgressTime
            r27 = r3
            float r3 = (float) r8
            float r1 = r1 + r3
            r0.downloadProgressTime = r1
            r3 = 1128792064(0x43480000, float:200.0)
            int r3 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r3 < 0) goto L_0x0cc5
            r0.animatedDownloadProgress = r2
            r0.downloadProgressAnimationStart = r2
            r1 = 0
            r0.downloadProgressTime = r1
            goto L_0x0cdd
        L_0x0cc5:
            android.view.animation.DecelerateInterpolator r2 = r0.interpolator
            r3 = 1128792064(0x43480000, float:200.0)
            float r1 = r1 / r3
            float r1 = r2.getInterpolation(r1)
            float r1 = r1 * r14
            float r13 = r13 + r1
            r0.animatedDownloadProgress = r13
            goto L_0x0cdd
        L_0x0cd4:
            r30 = r1
            r27 = r3
            goto L_0x0cdd
        L_0x0cd9:
            r30 = r1
            r27 = r3
        L_0x0cdd:
            r42.invalidateSelf()
        L_0x0ce0:
            boolean r1 = r0.animatingTransition
            if (r1 == 0) goto L_0x0d03
            float r1 = r0.transitionProgress
            r2 = 1065353216(0x3var_, float:1.0)
            int r3 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r3 >= 0) goto L_0x0d03
            float r3 = (float) r8
            float r13 = r0.transitionAnimationTime
            float r3 = r3 / r13
            float r1 = r1 + r3
            r0.transitionProgress = r1
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 < 0) goto L_0x0d00
            int r1 = r0.nextIcon
            r0.currentIcon = r1
            r0.transitionProgress = r2
            r1 = 0
            r0.animatingTransition = r1
        L_0x0d00:
            r42.invalidateSelf()
        L_0x0d03:
            r1 = r35
            r2 = 1
            if (r1 < r2) goto L_0x0d0b
            r7.restoreToCount(r1)
        L_0x0d0b:
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
