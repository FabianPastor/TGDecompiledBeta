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

    private void applyShaderMatrix(boolean z) {
        Theme.MessageDrawable messageDrawable2 = this.messageDrawable;
        if (messageDrawable2 != null && messageDrawable2.hasGradient() && !this.hasOverlayImage) {
            Rect bounds = getBounds();
            Shader gradientShader = this.messageDrawable.getGradientShader();
            Matrix matrix = this.messageDrawable.getMatrix();
            matrix.reset();
            this.messageDrawable.applyMatrixScale();
            if (z) {
                matrix.postTranslate((float) (-bounds.centerX()), (float) ((-this.messageDrawable.getTopY()) + bounds.top));
            } else {
                matrix.postTranslate(0.0f, (float) (-this.messageDrawable.getTopY()));
            }
            gradientShader.setLocalMatrix(matrix);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:339:0x0885, code lost:
        if (r0.nextIcon == 1) goto L_0x0887;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:341:0x088a, code lost:
        r4 = 0.0f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:343:0x088d, code lost:
        if (r1 != 1) goto L_0x088a;
     */
    /* JADX WARNING: Removed duplicated region for block: B:101:0x0358  */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x0403  */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x0429  */
    /* JADX WARNING: Removed duplicated region for block: B:180:0x050f  */
    /* JADX WARNING: Removed duplicated region for block: B:183:0x051a  */
    /* JADX WARNING: Removed duplicated region for block: B:185:0x0524  */
    /* JADX WARNING: Removed duplicated region for block: B:191:0x05a2  */
    /* JADX WARNING: Removed duplicated region for block: B:194:0x05aa  */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x05b3  */
    /* JADX WARNING: Removed duplicated region for block: B:210:0x05d8  */
    /* JADX WARNING: Removed duplicated region for block: B:211:0x05db  */
    /* JADX WARNING: Removed duplicated region for block: B:222:0x0638  */
    /* JADX WARNING: Removed duplicated region for block: B:223:0x063d  */
    /* JADX WARNING: Removed duplicated region for block: B:234:0x066c  */
    /* JADX WARNING: Removed duplicated region for block: B:235:0x066f  */
    /* JADX WARNING: Removed duplicated region for block: B:242:0x067f  */
    /* JADX WARNING: Removed duplicated region for block: B:243:0x0682  */
    /* JADX WARNING: Removed duplicated region for block: B:248:0x068e  */
    /* JADX WARNING: Removed duplicated region for block: B:249:0x0694  */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x06a3  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x06a6  */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x06be  */
    /* JADX WARNING: Removed duplicated region for block: B:265:0x06c8  */
    /* JADX WARNING: Removed duplicated region for block: B:282:0x0765  */
    /* JADX WARNING: Removed duplicated region for block: B:283:0x0768  */
    /* JADX WARNING: Removed duplicated region for block: B:289:0x077a  */
    /* JADX WARNING: Removed duplicated region for block: B:290:0x077d  */
    /* JADX WARNING: Removed duplicated region for block: B:293:0x0791  */
    /* JADX WARNING: Removed duplicated region for block: B:296:0x07c8  */
    /* JADX WARNING: Removed duplicated region for block: B:303:0x07df  */
    /* JADX WARNING: Removed duplicated region for block: B:304:0x07e2  */
    /* JADX WARNING: Removed duplicated region for block: B:309:0x0808  */
    /* JADX WARNING: Removed duplicated region for block: B:317:0x084e  */
    /* JADX WARNING: Removed duplicated region for block: B:320:0x0856 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:333:0x0873  */
    /* JADX WARNING: Removed duplicated region for block: B:338:0x0882  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00f6 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:361:0x08f2  */
    /* JADX WARNING: Removed duplicated region for block: B:362:0x08f5  */
    /* JADX WARNING: Removed duplicated region for block: B:366:0x08fc  */
    /* JADX WARNING: Removed duplicated region for block: B:374:0x0934 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x0160  */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x0320  */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x0349  */
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
            if (r1 == 0) goto L_0x002e
            boolean r1 = r1.hasGradient()
            if (r1 == 0) goto L_0x002e
            boolean r1 = r0.hasOverlayImage
            if (r1 != 0) goto L_0x002e
            org.telegram.ui.ActionBar.Theme$MessageDrawable r1 = r0.messageDrawable
            android.graphics.Shader r1 = r1.getGradientShader()
            android.graphics.Paint r2 = r0.paint
            r2.setShader(r1)
            android.graphics.Paint r2 = r0.paint2
            r2.setShader(r1)
            android.graphics.Paint r2 = r0.paint3
            r2.setShader(r1)
            goto L_0x006f
        L_0x002e:
            android.graphics.LinearGradient r1 = r0.gradientDrawable
            if (r1 == 0) goto L_0x0060
            boolean r1 = r0.hasOverlayImage
            if (r1 != 0) goto L_0x0060
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
            goto L_0x006f
        L_0x0060:
            android.graphics.Paint r1 = r0.paint
            r1.setShader(r9)
            android.graphics.Paint r1 = r0.paint2
            r1.setShader(r9)
            android.graphics.Paint r1 = r0.paint3
            r1.setShader(r9)
        L_0x006f:
            int r11 = r8.centerX()
            int r12 = r8.centerY()
            int r1 = r0.nextIcon
            r13 = 6
            r14 = 3
            r15 = 4
            r6 = 0
            r5 = 14
            r4 = 1065353216(0x3var_, float:1.0)
            if (r1 != r15) goto L_0x0097
            int r1 = r0.currentIcon
            if (r1 == r14) goto L_0x00ae
            if (r1 == r5) goto L_0x00ae
            int r1 = r34.save()
            float r2 = r0.transitionProgress
            float r2 = r4 - r2
            float r3 = (float) r11
            float r9 = (float) r12
            r7.scale(r2, r2, r3, r9)
            goto L_0x00ac
        L_0x0097:
            if (r1 == r13) goto L_0x009d
            r2 = 10
            if (r1 != r2) goto L_0x00ae
        L_0x009d:
            int r1 = r0.currentIcon
            if (r1 != r15) goto L_0x00ae
            int r1 = r34.save()
            float r2 = r0.transitionProgress
            float r3 = (float) r11
            float r9 = (float) r12
            r7.scale(r2, r2, r3, r9)
        L_0x00ac:
            r9 = r1
            goto L_0x00af
        L_0x00ae:
            r9 = 0
        L_0x00af:
            r1 = 1077936128(0x40400000, float:3.0)
            org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = r0.currentIcon
            r17 = 1094713344(0x41400000, float:12.0)
            r18 = 1120403456(0x42CLASSNAME, float:100.0)
            r19 = 1080033280(0x40600000, float:3.5)
            r20 = 1073741824(0x40000000, float:2.0)
            r21 = 1088421888(0x40e00000, float:7.0)
            r13 = 2
            r22 = 1056964608(0x3var_, float:0.5)
            r23 = 1132396544(0x437var_, float:255.0)
            if (r1 == r13) goto L_0x00cb
            int r1 = r0.nextIcon
            if (r1 != r13) goto L_0x036f
        L_0x00cb:
            r0.applyShaderMatrix(r6)
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
            float r15 = r0.scale
            float r6 = r6 * r15
            float r6 = r6 + r1
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r15 = (float) r15
            float r10 = r0.scale
            float r15 = r15 * r10
            float r15 = r15 + r1
            int r10 = r0.currentIcon
            if (r10 == r14) goto L_0x00f8
            if (r10 != r5) goto L_0x011d
        L_0x00f8:
            int r10 = r0.nextIcon
            if (r10 != r13) goto L_0x011d
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
            goto L_0x015a
        L_0x011d:
            int r2 = r0.nextIcon
            if (r2 == r14) goto L_0x0142
            if (r2 == r5) goto L_0x0142
            if (r2 == r13) goto L_0x0142
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
            goto L_0x014b
        L_0x0142:
            android.graphics.Paint r2 = r0.paint
            r4 = 255(0xff, float:3.57E-43)
            r2.setAlpha(r4)
            float r2 = r0.transitionProgress
        L_0x014b:
            r10 = 1065353216(0x3var_, float:1.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r5 = (float) r5
            float r4 = r0.scale
            float r5 = r5 * r4
            float r4 = r1 + r5
            r25 = r4
        L_0x015a:
            boolean r4 = r0.animatingTransition
            r5 = 1090519040(0x41000000, float:8.0)
            if (r4 == 0) goto L_0x0320
            int r4 = r0.nextIcon
            if (r4 == r13) goto L_0x02d6
            int r26 = (r2 > r22 ? 1 : (r2 == r22 ? 0 : -1))
            if (r26 > 0) goto L_0x016a
            goto L_0x02d6
        L_0x016a:
            r3 = 1095761920(0x41500000, float:13.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            float r4 = r0.scale
            float r3 = r3 * r4
            float r3 = r3 * r4
            boolean r4 = r0.isMini
            if (r4 == 0) goto L_0x0180
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r20)
            goto L_0x0181
        L_0x0180:
            r4 = 0
        L_0x0181:
            float r4 = (float) r4
            float r3 = r3 + r4
            float r2 = r2 - r22
            float r4 = r2 / r22
            r5 = 1045220557(0x3e4ccccd, float:0.2)
            int r5 = (r2 > r5 ? 1 : (r2 == r5 ? 0 : -1))
            if (r5 <= 0) goto L_0x019b
            r5 = 1045220557(0x3e4ccccd, float:0.2)
            float r2 = r2 - r5
            r5 = 1050253722(0x3e99999a, float:0.3)
            float r2 = r2 / r5
            r27 = r2
            r26 = 1065353216(0x3var_, float:1.0)
            goto L_0x01a3
        L_0x019b:
            r5 = 1045220557(0x3e4ccccd, float:0.2)
            float r2 = r2 / r5
            r26 = r2
            r27 = 0
        L_0x01a3:
            android.graphics.RectF r2 = r0.rect
            float r6 = (float) r11
            float r5 = r6 - r3
            float r3 = r3 / r20
            float r10 = r15 - r3
            float r3 = r3 + r15
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
            r14 = 14
            r30 = r6
            r6 = r10
            r1.drawArc(r2, r3, r4, r5, r6)
            float r1 = r15 - r25
            float r1 = r1 * r26
            float r25 = r25 + r1
            r1 = 0
            int r2 = (r27 > r1 ? 1 : (r27 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x02cf
            int r1 = r0.nextIcon
            if (r1 != r14) goto L_0x01db
            r10 = 0
            goto L_0x01e2
        L_0x01db:
            r1 = -1036779520(0xffffffffCLASSNAME, float:-45.0)
            float r4 = r13 - r27
            float r1 = r1 * r4
            r10 = r1
        L_0x01e2:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r1 = (float) r1
            float r1 = r1 * r27
            float r2 = r0.scale
            float r1 = r1 * r2
            float r2 = r27 * r23
            int r2 = (int) r2
            int r3 = r0.nextIcon
            r4 = 3
            if (r3 == r4) goto L_0x0208
            if (r3 == r14) goto L_0x0208
            r4 = 2
            if (r3 == r4) goto L_0x0208
            float r3 = r0.transitionProgress
            float r3 = r3 / r22
            float r3 = java.lang.Math.min(r13, r3)
            float r4 = r13 - r3
            float r2 = (float) r2
            float r2 = r2 * r4
            int r2 = (int) r2
        L_0x0208:
            r6 = r2
            r2 = 0
            int r3 = (r10 > r2 ? 1 : (r10 == r2 ? 0 : -1))
            if (r3 == 0) goto L_0x0219
            r34.save()
            r2 = r28
            r5 = r30
            r7.rotate(r10, r5, r2)
            goto L_0x021d
        L_0x0219:
            r2 = r28
            r5 = r30
        L_0x021d:
            if (r6 == 0) goto L_0x02c4
            android.graphics.Paint r3 = r0.paint
            r3.setAlpha(r6)
            int r3 = r0.nextIcon
            if (r3 != r14) goto L_0x02a4
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
            int r14 = r12 + r24
            float r14 = (float) r14
            r1.set(r2, r3, r4, r14)
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
            if (r1 == 0) goto L_0x0271
            r3 = 1073741824(0x40000000, float:2.0)
            goto L_0x0273
        L_0x0271:
            r3 = 1082130432(0x40800000, float:4.0)
        L_0x0273:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r3)
            android.graphics.RectF r2 = r0.rect
            int r3 = r8.left
            int r3 = r3 + r1
            float r3 = (float) r3
            int r4 = r8.top
            int r4 = r4 + r1
            float r4 = (float) r4
            int r14 = r8.right
            int r14 = r14 - r1
            float r14 = (float) r14
            int r13 = r8.bottom
            int r13 = r13 - r1
            float r1 = (float) r13
            r2.set(r3, r4, r14, r1)
            android.graphics.RectF r2 = r0.rect
            r3 = 0
            r4 = 1135869952(0x43b40000, float:360.0)
            r13 = 0
            android.graphics.Paint r14 = r0.paint
            r1 = r34
            r24 = r5
            r5 = r13
            r13 = r6
            r6 = r14
            r1.drawArc(r2, r3, r4, r5, r6)
            android.graphics.Paint r1 = r0.paint
            r1.setAlpha(r13)
            goto L_0x02c6
        L_0x02a4:
            r24 = r5
            float r13 = r24 - r1
            float r14 = r2 - r1
            float r26 = r24 + r1
            float r27 = r2 + r1
            android.graphics.Paint r6 = r0.paint
            r1 = r34
            r2 = r13
            r3 = r14
            r4 = r26
            r5 = r27
            r1.drawLine(r2, r3, r4, r5, r6)
            android.graphics.Paint r6 = r0.paint
            r2 = r26
            r4 = r13
            r1.drawLine(r2, r3, r4, r5, r6)
            goto L_0x02c6
        L_0x02c4:
            r24 = r5
        L_0x02c6:
            r1 = 0
            int r2 = (r10 > r1 ? 1 : (r10 == r1 ? 0 : -1))
            if (r2 == 0) goto L_0x02d1
            r34.restore()
            goto L_0x02d1
        L_0x02cf:
            r24 = r30
        L_0x02d1:
            r2 = r15
            r1 = r24
            r6 = r1
            goto L_0x031a
        L_0x02d6:
            r1 = 2
            if (r4 != r1) goto L_0x02de
            r1 = 1065353216(0x3var_, float:1.0)
            float r4 = r1 - r2
            goto L_0x02e4
        L_0x02de:
            r1 = 1065353216(0x3var_, float:1.0)
            float r4 = r2 / r22
            float r2 = r1 - r4
        L_0x02e4:
            float r25 = r25 - r3
            float r25 = r25 * r4
            float r25 = r3 + r25
            float r15 = r15 - r6
            float r15 = r15 * r4
            float r15 = r15 + r6
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
            float r2 = r15 - r3
            r32 = r15
            r15 = r2
            r2 = r32
        L_0x031a:
            r13 = r1
            r14 = r2
            r10 = r6
            r3 = r25
            goto L_0x0345
        L_0x0320:
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
            r15 = r4
            r14 = r6
        L_0x0345:
            int r1 = (r3 > r14 ? 1 : (r3 == r14 ? 0 : -1))
            if (r1 == 0) goto L_0x0353
            float r4 = (float) r11
            android.graphics.Paint r6 = r0.paint
            r1 = r34
            r2 = r4
            r5 = r14
            r1.drawLine(r2, r3, r4, r5, r6)
        L_0x0353:
            float r6 = (float) r11
            int r1 = (r10 > r6 ? 1 : (r10 == r6 ? 0 : -1))
            if (r1 == 0) goto L_0x036f
            android.graphics.Paint r5 = r0.paint
            r1 = r34
            r2 = r10
            r3 = r15
            r4 = r6
            r10 = r5
            r5 = r14
            r24 = r6
            r6 = r10
            r1.drawLine(r2, r3, r4, r5, r6)
            android.graphics.Paint r6 = r0.paint
            r2 = r13
            r4 = r24
            r1.drawLine(r2, r3, r4, r5, r6)
        L_0x036f:
            int r1 = r0.currentIcon
            r10 = 13
            r13 = 1
            r2 = 3
            if (r1 == r2) goto L_0x03f8
            r3 = 14
            if (r1 == r3) goto L_0x03f8
            r4 = 4
            if (r1 != r4) goto L_0x0386
            int r4 = r0.nextIcon
            if (r4 == r3) goto L_0x03f8
            if (r4 != r2) goto L_0x0386
            goto L_0x03f8
        L_0x0386:
            r2 = 10
            if (r1 == r2) goto L_0x0396
            int r2 = r0.nextIcon
            r3 = 10
            if (r2 == r3) goto L_0x0396
            if (r1 != r10) goto L_0x0393
            goto L_0x0396
        L_0x0393:
            r14 = 0
            goto L_0x0632
        L_0x0396:
            int r1 = r0.nextIcon
            r2 = 4
            if (r1 == r2) goto L_0x03a2
            r2 = 6
            if (r1 != r2) goto L_0x039f
            goto L_0x03a2
        L_0x039f:
            r2 = 255(0xff, float:3.57E-43)
            goto L_0x03ab
        L_0x03a2:
            float r1 = r0.transitionProgress
            r2 = 1065353216(0x3var_, float:1.0)
            float r4 = r2 - r1
            float r4 = r4 * r23
            int r2 = (int) r4
        L_0x03ab:
            if (r2 == 0) goto L_0x0393
            r14 = 0
            r0.applyShaderMatrix(r14)
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
            if (r1 == 0) goto L_0x03cf
            r3 = 1073741824(0x40000000, float:2.0)
            goto L_0x03d1
        L_0x03cf:
            r3 = 1082130432(0x40800000, float:4.0)
        L_0x03d1:
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
            goto L_0x0632
        L_0x03f8:
            r14 = 0
            r15 = 1082130432(0x40800000, float:4.0)
            r0.applyShaderMatrix(r14)
            int r1 = r0.nextIcon
            r2 = 2
            if (r1 != r2) goto L_0x0429
            float r1 = r0.transitionProgress
            int r2 = (r1 > r22 ? 1 : (r1 == r22 ? 0 : -1))
            if (r2 > 0) goto L_0x041e
            float r1 = r1 / r22
            r2 = 1065353216(0x3var_, float:1.0)
            float r4 = r2 - r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r1 = (float) r1
            float r1 = r1 * r4
            float r2 = r0.scale
            float r1 = r1 * r2
            float r4 = r4 * r23
            int r6 = (int) r4
            goto L_0x0420
        L_0x041e:
            r1 = 0
            r6 = 0
        L_0x0420:
            r2 = 1065353216(0x3var_, float:1.0)
            r3 = 0
            r4 = 0
            r5 = 1065353216(0x3var_, float:1.0)
        L_0x0426:
            r13 = 0
            goto L_0x050b
        L_0x0429:
            r2 = 15
            if (r1 == r2) goto L_0x04d1
            if (r1 == 0) goto L_0x04d1
            if (r1 == r13) goto L_0x04d1
            r2 = 5
            if (r1 == r2) goto L_0x04d1
            r2 = 8
            if (r1 == r2) goto L_0x04d1
            r2 = 9
            if (r1 == r2) goto L_0x04d1
            r2 = 7
            if (r1 == r2) goto L_0x04d1
            r2 = 6
            if (r1 != r2) goto L_0x0444
            goto L_0x04d2
        L_0x0444:
            r2 = 4
            if (r1 != r2) goto L_0x0477
            float r1 = r0.transitionProgress
            r2 = 1065353216(0x3var_, float:1.0)
            float r4 = r2 - r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r1 = (float) r1
            float r2 = r0.scale
            float r1 = r1 * r2
            float r2 = r4 * r23
            int r2 = (int) r2
            int r3 = r0.currentIcon
            r5 = 14
            if (r3 != r5) goto L_0x0465
            int r3 = r8.left
            float r3 = (float) r3
            int r5 = r8.top
            goto L_0x046e
        L_0x0465:
            int r3 = r8.centerX()
            float r3 = (float) r3
            int r5 = r8.centerY()
        L_0x046e:
            float r5 = (float) r5
            r6 = r2
            r2 = 1065353216(0x3var_, float:1.0)
            r13 = 0
            r32 = r5
            goto L_0x0508
        L_0x0477:
            r2 = 14
            if (r1 == r2) goto L_0x0491
            r2 = 3
            if (r1 != r2) goto L_0x047f
            goto L_0x0491
        L_0x047f:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r1 = (float) r1
            float r2 = r0.scale
            float r1 = r1 * r2
            r2 = 1065353216(0x3var_, float:1.0)
            r3 = 0
            r4 = 0
            r5 = 1065353216(0x3var_, float:1.0)
            r6 = 255(0xff, float:3.57E-43)
            goto L_0x0426
        L_0x0491:
            float r1 = r0.transitionProgress
            r2 = 1065353216(0x3var_, float:1.0)
            float r4 = r2 - r1
            int r2 = r0.currentIcon
            r3 = 4
            if (r2 != r3) goto L_0x049f
            r4 = r1
            r2 = 0
            goto L_0x04a6
        L_0x049f:
            r2 = 1110704128(0x42340000, float:45.0)
            float r4 = r4 * r2
            r2 = r4
            r4 = 1065353216(0x3var_, float:1.0)
        L_0x04a6:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r3 = (float) r3
            float r5 = r0.scale
            float r3 = r3 * r5
            float r1 = r1 * r23
            int r1 = (int) r1
            int r5 = r0.nextIcon
            r6 = 14
            if (r5 != r6) goto L_0x04be
            int r5 = r8.left
            float r5 = (float) r5
            int r6 = r8.top
            goto L_0x04c7
        L_0x04be:
            int r5 = r8.centerX()
            float r5 = (float) r5
            int r6 = r8.centerY()
        L_0x04c7:
            float r6 = (float) r6
            r13 = r2
            r2 = 1065353216(0x3var_, float:1.0)
            r32 = r6
            r6 = r1
            r1 = r3
            r3 = r5
            goto L_0x0508
        L_0x04d1:
            r2 = 6
        L_0x04d2:
            if (r1 != r2) goto L_0x04df
            float r1 = r0.transitionProgress
            float r1 = r1 / r22
            r2 = 1065353216(0x3var_, float:1.0)
            float r1 = java.lang.Math.min(r2, r1)
            goto L_0x04e3
        L_0x04df:
            r2 = 1065353216(0x3var_, float:1.0)
            float r1 = r0.transitionProgress
        L_0x04e3:
            float r4 = r2 - r1
            int r1 = r8.centerX()
            float r1 = (float) r1
            int r3 = r8.centerY()
            float r3 = (float) r3
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r5 = (float) r5
            float r5 = r5 * r4
            float r6 = r0.scale
            float r5 = r5 * r6
            float r6 = r4 * r20
            float r6 = java.lang.Math.min(r2, r6)
            float r6 = r6 * r23
            int r6 = (int) r6
            r13 = 0
            r32 = r3
            r3 = r1
            r1 = r5
        L_0x0508:
            r5 = r4
            r4 = r32
        L_0x050b:
            int r25 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1))
            if (r25 == 0) goto L_0x0515
            r34.save()
            r7.scale(r5, r5, r3, r4)
        L_0x0515:
            r2 = 0
            int r3 = (r13 > r2 ? 1 : (r13 == r2 ? 0 : -1))
            if (r3 == 0) goto L_0x0522
            r34.save()
            float r2 = (float) r11
            float r3 = (float) r12
            r7.rotate(r13, r2, r3)
        L_0x0522:
            if (r6 == 0) goto L_0x05a2
            android.graphics.Paint r2 = r0.paint
            float r3 = (float) r6
            float r4 = r0.overrideAlpha
            float r4 = r4 * r3
            int r4 = (int) r4
            r2.setAlpha(r4)
            int r2 = r0.currentIcon
            r4 = 14
            if (r2 == r4) goto L_0x0564
            int r2 = r0.nextIcon
            if (r2 != r4) goto L_0x053a
            goto L_0x0564
        L_0x053a:
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
            goto L_0x05a5
        L_0x0564:
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
            goto L_0x05a5
        L_0x05a2:
            r29 = r5
            r10 = r6
        L_0x05a5:
            r1 = 0
            int r2 = (r13 > r1 ? 1 : (r13 == r1 ? 0 : -1))
            if (r2 == 0) goto L_0x05ad
            r34.restore()
        L_0x05ad:
            r1 = 1065353216(0x3var_, float:1.0)
            int r2 = (r29 > r1 ? 1 : (r29 == r1 ? 0 : -1))
            if (r2 == 0) goto L_0x05b6
            r34.restore()
        L_0x05b6:
            int r1 = r0.currentIcon
            r2 = 3
            if (r1 == r2) goto L_0x05c8
            r3 = 14
            if (r1 == r3) goto L_0x05c8
            r4 = 4
            if (r1 != r4) goto L_0x0632
            int r1 = r0.nextIcon
            if (r1 == r3) goto L_0x05c8
            if (r1 != r2) goto L_0x0632
        L_0x05c8:
            if (r10 == 0) goto L_0x0632
            r1 = 1135869952(0x43b40000, float:360.0)
            float r2 = r0.animatedDownloadProgress
            float r2 = r2 * r1
            float r13 = java.lang.Math.max(r15, r2)
            boolean r1 = r0.isMini
            if (r1 == 0) goto L_0x05db
            r3 = 1073741824(0x40000000, float:2.0)
            goto L_0x05dd
        L_0x05db:
            r3 = 1082130432(0x40800000, float:4.0)
        L_0x05dd:
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
            if (r1 == r2) goto L_0x0603
            r3 = 4
            if (r1 != r3) goto L_0x0625
            int r1 = r0.nextIcon
            if (r1 != r2) goto L_0x0625
        L_0x0603:
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
        L_0x0625:
            android.graphics.RectF r2 = r0.rect
            float r3 = r0.downloadRadOffset
            r5 = 0
            android.graphics.Paint r6 = r0.paint
            r1 = r34
            r4 = r13
            r1.drawArc(r2, r3, r4, r5, r6)
        L_0x0632:
            int r1 = r0.currentIcon
            int r2 = r0.nextIcon
            if (r1 != r2) goto L_0x063d
            r10 = 1065353216(0x3var_, float:1.0)
            r13 = 1065353216(0x3var_, float:1.0)
            goto L_0x0666
        L_0x063d:
            r2 = 4
            if (r1 == r2) goto L_0x065e
            r2 = 3
            if (r1 == r2) goto L_0x065e
            r2 = 14
            if (r1 != r2) goto L_0x0648
            goto L_0x065e
        L_0x0648:
            float r1 = r0.transitionProgress
            float r1 = r1 / r22
            r2 = 1065353216(0x3var_, float:1.0)
            float r4 = java.lang.Math.min(r2, r1)
            float r1 = r0.transitionProgress
            float r1 = r1 / r22
            float r1 = r2 - r1
            r3 = 0
            float r1 = java.lang.Math.max(r3, r1)
            goto L_0x0664
        L_0x065e:
            r2 = 1065353216(0x3var_, float:1.0)
            float r4 = r0.transitionProgress
            float r1 = r2 - r4
        L_0x0664:
            r13 = r1
            r10 = r4
        L_0x0666:
            int r1 = r0.nextIcon
            r2 = 15
            if (r1 != r2) goto L_0x066f
            android.graphics.Path[] r2 = org.telegram.ui.ActionBar.Theme.chat_updatePath
            goto L_0x067b
        L_0x066f:
            int r2 = r0.currentIcon
            r3 = 15
            if (r2 != r3) goto L_0x067a
            android.graphics.Path[] r2 = org.telegram.ui.ActionBar.Theme.chat_updatePath
            r3 = r2
            r2 = 0
            goto L_0x067c
        L_0x067a:
            r2 = 0
        L_0x067b:
            r3 = 0
        L_0x067c:
            r4 = 5
            if (r1 != r4) goto L_0x0682
            android.graphics.Path[] r2 = org.telegram.ui.ActionBar.Theme.chat_filePath
            goto L_0x0689
        L_0x0682:
            int r4 = r0.currentIcon
            r5 = 5
            if (r4 != r5) goto L_0x0689
            android.graphics.Path[] r3 = org.telegram.ui.ActionBar.Theme.chat_filePath
        L_0x0689:
            r15 = r2
            r6 = r3
            r2 = 7
            if (r1 != r2) goto L_0x0694
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.chat_flameIcon
            r16 = r2
            r2 = 0
            goto L_0x069f
        L_0x0694:
            int r2 = r0.currentIcon
            r3 = 7
            if (r2 != r3) goto L_0x069c
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.chat_flameIcon
            goto L_0x069d
        L_0x069c:
            r2 = 0
        L_0x069d:
            r16 = 0
        L_0x069f:
            r3 = 8
            if (r1 != r3) goto L_0x06a6
            android.graphics.drawable.Drawable r16 = org.telegram.ui.ActionBar.Theme.chat_gifIcon
            goto L_0x06ae
        L_0x06a6:
            int r3 = r0.currentIcon
            r4 = 8
            if (r3 != r4) goto L_0x06ae
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.chat_gifIcon
        L_0x06ae:
            r5 = r2
            r4 = r16
            int r2 = r0.currentIcon
            r3 = 9
            r16 = 1086324736(0x40CLASSNAME, float:6.0)
            if (r2 == r3) goto L_0x06c8
            r2 = 9
            if (r1 != r2) goto L_0x06be
            goto L_0x06c8
        L_0x06be:
            r14 = r4
            r31 = r6
            r20 = r9
            r19 = r15
            r15 = r5
            goto L_0x074f
        L_0x06c8:
            r0.applyShaderMatrix(r14)
            android.graphics.Paint r1 = r0.paint
            int r2 = r0.currentIcon
            int r3 = r0.nextIcon
            if (r2 != r3) goto L_0x06d6
            r2 = 255(0xff, float:3.57E-43)
            goto L_0x06db
        L_0x06d6:
            float r2 = r0.transitionProgress
            float r2 = r2 * r23
            int r2 = (int) r2
        L_0x06db:
            r1.setAlpha(r2)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r3 = r12 + r1
            r1 = 1077936128(0x40400000, float:3.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r2 = r11 - r1
            int r1 = r0.currentIcon
            int r14 = r0.nextIcon
            if (r1 == r14) goto L_0x06ff
            r34.save()
            float r1 = r0.transitionProgress
            float r14 = (float) r11
            r19 = r4
            float r4 = (float) r12
            r7.scale(r1, r1, r14, r4)
            goto L_0x0701
        L_0x06ff:
            r19 = r4
        L_0x0701:
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
            if (r1 == r2) goto L_0x074f
            r34.restore()
        L_0x074f:
            int r1 = r0.currentIcon
            r2 = 12
            if (r1 == r2) goto L_0x075b
            int r1 = r0.nextIcon
            r2 = 12
            if (r1 != r2) goto L_0x07cb
        L_0x075b:
            r1 = 0
            r0.applyShaderMatrix(r1)
            int r1 = r0.currentIcon
            int r2 = r0.nextIcon
            if (r1 != r2) goto L_0x0768
            r4 = 1065353216(0x3var_, float:1.0)
            goto L_0x0776
        L_0x0768:
            r3 = 13
            if (r2 != r3) goto L_0x076f
            float r4 = r0.transitionProgress
            goto L_0x0776
        L_0x076f:
            float r3 = r0.transitionProgress
            r4 = 1065353216(0x3var_, float:1.0)
            float r3 = r4 - r3
            r4 = r3
        L_0x0776:
            android.graphics.Paint r3 = r0.paint
            if (r1 != r2) goto L_0x077d
            r2 = 255(0xff, float:3.57E-43)
            goto L_0x0780
        L_0x077d:
            float r1 = r4 * r23
            int r2 = (int) r1
        L_0x0780:
            r3.setAlpha(r2)
            org.telegram.messenger.AndroidUtilities.dp(r21)
            r1 = 1077936128(0x40400000, float:3.0)
            org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = r0.currentIcon
            int r2 = r0.nextIcon
            if (r1 == r2) goto L_0x0799
            r34.save()
            float r1 = (float) r11
            float r2 = (float) r12
            r7.scale(r4, r4, r1, r2)
        L_0x0799:
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
            if (r1 == r2) goto L_0x07cb
            r34.restore()
        L_0x07cb:
            int r1 = r0.currentIcon
            r2 = 13
            if (r1 == r2) goto L_0x07d5
            int r1 = r0.nextIcon
            if (r1 != r2) goto L_0x0851
        L_0x07d5:
            r1 = 0
            r0.applyShaderMatrix(r1)
            int r1 = r0.currentIcon
            int r3 = r0.nextIcon
            if (r1 != r3) goto L_0x07e2
            r4 = 1065353216(0x3var_, float:1.0)
            goto L_0x07ed
        L_0x07e2:
            if (r3 != r2) goto L_0x07e7
            float r4 = r0.transitionProgress
            goto L_0x07ed
        L_0x07e7:
            float r1 = r0.transitionProgress
            r2 = 1065353216(0x3var_, float:1.0)
            float r4 = r2 - r1
        L_0x07ed:
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
            if (r3 == r5) goto L_0x0810
            r34.save()
            float r3 = (float) r11
            float r5 = (float) r12
            r7.scale(r4, r4, r3, r5)
        L_0x0810:
            float r3 = r0.animatedDownloadProgress
            float r3 = r3 * r18
            int r3 = (int) r3
            java.lang.String r4 = r0.percentString
            if (r4 == 0) goto L_0x081d
            int r4 = r0.lastPercent
            if (r3 == r4) goto L_0x083f
        L_0x081d:
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
        L_0x083f:
            java.lang.String r3 = r0.percentString
            float r2 = (float) r2
            float r1 = (float) r1
            android.text.TextPaint r4 = r0.textPaint
            r7.drawText(r3, r2, r1, r4)
            int r1 = r0.currentIcon
            int r2 = r0.nextIcon
            if (r1 == r2) goto L_0x0851
            r34.restore()
        L_0x0851:
            int r1 = r0.currentIcon
            r2 = 1
            if (r1 == 0) goto L_0x0863
            if (r1 == r2) goto L_0x0863
            int r3 = r0.nextIcon
            if (r3 == 0) goto L_0x0863
            if (r3 != r2) goto L_0x085f
            goto L_0x0863
        L_0x085f:
            r9 = 255(0xff, float:3.57E-43)
            goto L_0x098e
        L_0x0863:
            if (r1 != 0) goto L_0x0869
            int r3 = r0.nextIcon
            if (r3 == r2) goto L_0x086f
        L_0x0869:
            if (r1 != r2) goto L_0x088c
            int r2 = r0.nextIcon
            if (r2 != 0) goto L_0x088c
        L_0x086f:
            boolean r2 = r0.animatingTransition
            if (r2 == 0) goto L_0x0882
            int r2 = r0.nextIcon
            if (r2 != 0) goto L_0x087e
            float r2 = r0.transitionProgress
            r3 = 1065353216(0x3var_, float:1.0)
            float r4 = r3 - r2
            goto L_0x0880
        L_0x087e:
            float r4 = r0.transitionProgress
        L_0x0880:
            r3 = 1
            goto L_0x0890
        L_0x0882:
            int r2 = r0.nextIcon
            r3 = 1
            if (r2 != r3) goto L_0x088a
        L_0x0887:
            r4 = 1065353216(0x3var_, float:1.0)
            goto L_0x0890
        L_0x088a:
            r4 = 0
            goto L_0x0890
        L_0x088c:
            r3 = 1
            if (r1 != r3) goto L_0x088a
            goto L_0x0887
        L_0x0890:
            int r2 = r0.nextIcon
            if (r2 == 0) goto L_0x0899
            if (r2 != r3) goto L_0x0897
            goto L_0x0899
        L_0x0897:
            r3 = 4
            goto L_0x089e
        L_0x0899:
            if (r1 == 0) goto L_0x08c2
            if (r1 == r3) goto L_0x08c2
            goto L_0x0897
        L_0x089e:
            if (r2 != r3) goto L_0x08b2
            android.graphics.Paint r1 = r0.paint2
            float r2 = r0.transitionProgress
            r3 = 1065353216(0x3var_, float:1.0)
            float r2 = r3 - r2
            float r2 = r2 * r23
            int r2 = (int) r2
            r1.setAlpha(r2)
        L_0x08ae:
            r1 = 1
            r9 = 255(0xff, float:3.57E-43)
            goto L_0x08ca
        L_0x08b2:
            android.graphics.Paint r3 = r0.paint2
            if (r1 != r2) goto L_0x08b9
            r2 = 255(0xff, float:3.57E-43)
            goto L_0x08be
        L_0x08b9:
            float r1 = r0.transitionProgress
            float r1 = r1 * r23
            int r2 = (int) r1
        L_0x08be:
            r3.setAlpha(r2)
            goto L_0x08ae
        L_0x08c2:
            android.graphics.Paint r1 = r0.paint2
            r9 = 255(0xff, float:3.57E-43)
            r1.setAlpha(r9)
            r1 = 1
        L_0x08ca:
            r0.applyShaderMatrix(r1)
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
            if (r1 != r2) goto L_0x08f5
            r3 = 1119092736(0x42b40000, float:90.0)
            goto L_0x08f6
        L_0x08f5:
            r3 = 0
        L_0x08f6:
            if (r1 != 0) goto L_0x0934
            int r5 = r0.nextIcon
            if (r5 != r2) goto L_0x0934
            r1 = 1136656384(0x43CLASSNAME, float:384.0)
            int r1 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r1 >= 0) goto L_0x0912
            r1 = 1119748096(0x42be0000, float:95.0)
            org.telegram.ui.Components.CubicBezierInterpolator r2 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_BOTH
            r3 = 1136656384(0x43CLASSNAME, float:384.0)
            float r3 = r4 / r3
            float r2 = r2.getInterpolation(r3)
            float r2 = r2 * r1
            r3 = r2
            goto L_0x0931
        L_0x0912:
            r1 = 1139933184(0x43var_, float:484.0)
            int r1 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r1 >= 0) goto L_0x092d
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
            goto L_0x0931
        L_0x092d:
            r1 = 1119092736(0x42b40000, float:90.0)
            r3 = 1119092736(0x42b40000, float:90.0)
        L_0x0931:
            float r4 = r4 + r18
            goto L_0x0966
        L_0x0934:
            if (r1 != r2) goto L_0x0966
            int r1 = r0.nextIcon
            if (r1 != 0) goto L_0x0966
            int r1 = (r4 > r18 ? 1 : (r4 == r18 ? 0 : -1))
            if (r1 >= 0) goto L_0x094b
            r1 = -1063256064(0xffffffffc0a00000, float:-5.0)
            org.telegram.ui.Components.CubicBezierInterpolator r2 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_BOTH
            float r3 = r4 / r18
            float r2 = r2.getInterpolation(r3)
            float r3 = r2 * r1
            goto L_0x0966
        L_0x094b:
            r1 = 1139933184(0x43var_, float:484.0)
            int r1 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r1 >= 0) goto L_0x0964
            r1 = -1063256064(0xffffffffc0a00000, float:-5.0)
            r2 = 1119748096(0x42be0000, float:95.0)
            org.telegram.ui.Components.CubicBezierInterpolator r3 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_BOTH
            float r5 = r4 - r18
            r6 = 1136656384(0x43CLASSNAME, float:384.0)
            float r5 = r5 / r6
            float r3 = r3.getInterpolation(r5)
            float r3 = r3 * r2
            float r3 = r3 + r1
            goto L_0x0966
        L_0x0964:
            r3 = 1119092736(0x42b40000, float:90.0)
        L_0x0966:
            r7.rotate(r3)
            int r1 = r0.currentIcon
            if (r1 == 0) goto L_0x0970
            r2 = 1
            if (r1 != r2) goto L_0x0973
        L_0x0970:
            r2 = 4
            if (r1 != r2) goto L_0x0976
        L_0x0973:
            r7.scale(r10, r10)
        L_0x0976:
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
        L_0x098e:
            int r1 = r0.currentIcon
            r2 = 6
            if (r1 == r2) goto L_0x099c
            int r1 = r0.nextIcon
            if (r1 != r2) goto L_0x0998
            goto L_0x099c
        L_0x0998:
            r18 = r10
            goto L_0x0a57
        L_0x099c:
            r1 = 0
            r0.applyShaderMatrix(r1)
            int r1 = r0.currentIcon
            if (r1 == r2) goto L_0x09cd
            float r1 = r0.transitionProgress
            int r2 = (r1 > r22 ? 1 : (r1 == r22 ? 0 : -1))
            if (r2 <= 0) goto L_0x09c3
            float r1 = r1 - r22
            float r1 = r1 / r22
            float r2 = r1 / r22
            r3 = 1065353216(0x3var_, float:1.0)
            float r2 = java.lang.Math.min(r3, r2)
            float r4 = r3 - r2
            int r2 = (r1 > r22 ? 1 : (r1 == r22 ? 0 : -1))
            if (r2 <= 0) goto L_0x09c1
            float r1 = r1 - r22
            float r1 = r1 / r22
            goto L_0x09c6
        L_0x09c1:
            r1 = 0
            goto L_0x09c6
        L_0x09c3:
            r1 = 0
            r4 = 1065353216(0x3var_, float:1.0)
        L_0x09c6:
            android.graphics.Paint r2 = r0.paint
            r2.setAlpha(r9)
            r8 = r1
            goto L_0x09e9
        L_0x09cd:
            int r1 = r0.nextIcon
            r2 = 6
            if (r1 == r2) goto L_0x09e1
            android.graphics.Paint r1 = r0.paint
            float r2 = r0.transitionProgress
            r3 = 1065353216(0x3var_, float:1.0)
            float r4 = r3 - r2
            float r4 = r4 * r23
            int r2 = (int) r4
            r1.setAlpha(r2)
            goto L_0x09e6
        L_0x09e1:
            android.graphics.Paint r1 = r0.paint
            r1.setAlpha(r9)
        L_0x09e6:
            r4 = 0
            r8 = 1065353216(0x3var_, float:1.0)
        L_0x09e9:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r6 = r12 + r1
            r1 = 1077936128(0x40400000, float:3.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r5 = r11 - r1
            r1 = 1065353216(0x3var_, float:1.0)
            int r2 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r2 >= 0) goto L_0x0a33
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
            goto L_0x0a37
        L_0x0a33:
            r9 = r5
            r18 = r10
            r10 = r6
        L_0x0a37:
            r1 = 0
            int r2 = (r8 > r1 ? 1 : (r8 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x0a57
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
        L_0x0a57:
            if (r15 == 0) goto L_0x0a94
            if (r15 == r14) goto L_0x0a94
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
            if (r3 != r4) goto L_0x0a79
            r3 = 255(0xff, float:3.57E-43)
            goto L_0x0a82
        L_0x0a79:
            float r3 = r0.transitionProgress
            r4 = 1065353216(0x3var_, float:1.0)
            float r3 = r4 - r3
            float r3 = r3 * r23
            int r3 = (int) r3
        L_0x0a82:
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
        L_0x0a94:
            if (r14 == 0) goto L_0x0acb
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
            if (r3 != r4) goto L_0x0ab4
            r3 = 255(0xff, float:3.57E-43)
            goto L_0x0ab9
        L_0x0ab4:
            float r3 = r0.transitionProgress
            float r3 = r3 * r23
            int r3 = (int) r3
        L_0x0ab9:
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
        L_0x0acb:
            r3 = r31
            r2 = r19
            if (r3 == 0) goto L_0x0b28
            if (r3 == r2) goto L_0x0b28
            r1 = 1103101952(0x41CLASSNAME, float:24.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            android.graphics.Paint r4 = r0.paint2
            android.graphics.Paint$Style r5 = android.graphics.Paint.Style.FILL_AND_STROKE
            r4.setStyle(r5)
            android.graphics.Paint r4 = r0.paint2
            int r5 = r0.currentIcon
            int r6 = r0.nextIcon
            if (r5 != r6) goto L_0x0aeb
            r5 = 255(0xff, float:3.57E-43)
            goto L_0x0af4
        L_0x0aeb:
            float r5 = r0.transitionProgress
            r6 = 1065353216(0x3var_, float:1.0)
            float r5 = r6 - r5
            float r5 = r5 * r23
            int r5 = (int) r5
        L_0x0af4:
            r4.setAlpha(r5)
            r4 = 1
            r0.applyShaderMatrix(r4)
            r34.save()
            float r4 = (float) r11
            float r5 = (float) r12
            r7.translate(r4, r5)
            r7.scale(r13, r13)
            int r1 = -r1
            r4 = 2
            int r1 = r1 / r4
            float r1 = (float) r1
            r7.translate(r1, r1)
            r1 = 0
            r4 = r3[r1]
            if (r4 == 0) goto L_0x0b19
            r4 = r3[r1]
            android.graphics.Paint r1 = r0.paint2
            r7.drawPath(r4, r1)
        L_0x0b19:
            r1 = 1
            r4 = r3[r1]
            if (r4 == 0) goto L_0x0b25
            r3 = r3[r1]
            android.graphics.Paint r1 = r0.backPaint
            r7.drawPath(r3, r1)
        L_0x0b25:
            r34.restore()
        L_0x0b28:
            if (r2 == 0) goto L_0x0bb2
            r1 = 1103101952(0x41CLASSNAME, float:24.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r3 = r0.currentIcon
            int r4 = r0.nextIcon
            if (r3 != r4) goto L_0x0b39
            r3 = 255(0xff, float:3.57E-43)
            goto L_0x0b3e
        L_0x0b39:
            float r3 = r0.transitionProgress
            float r3 = r3 * r23
            int r3 = (int) r3
        L_0x0b3e:
            android.graphics.Paint r4 = r0.paint2
            android.graphics.Paint$Style r5 = android.graphics.Paint.Style.FILL_AND_STROKE
            r4.setStyle(r5)
            android.graphics.Paint r4 = r0.paint2
            r4.setAlpha(r3)
            r4 = 1
            r0.applyShaderMatrix(r4)
            r34.save()
            float r4 = (float) r11
            float r5 = (float) r12
            r7.translate(r4, r5)
            r4 = r18
            r7.scale(r4, r4)
            int r1 = -r1
            r4 = 2
            int r1 = r1 / r4
            float r1 = (float) r1
            r7.translate(r1, r1)
            r1 = 0
            r4 = r2[r1]
            if (r4 == 0) goto L_0x0b6e
            r4 = r2[r1]
            android.graphics.Paint r1 = r0.paint2
            r7.drawPath(r4, r1)
        L_0x0b6e:
            int r1 = r2.length
            r4 = 3
            if (r1 < r4) goto L_0x0b7e
            r1 = 2
            r4 = r2[r1]
            if (r4 == 0) goto L_0x0b7e
            r4 = r2[r1]
            android.graphics.Paint r1 = r0.paint
            r7.drawPath(r4, r1)
        L_0x0b7e:
            r1 = 1
            r4 = r2[r1]
            if (r4 == 0) goto L_0x0baf
            r1 = 255(0xff, float:3.57E-43)
            if (r3 == r1) goto L_0x0ba7
            android.graphics.Paint r1 = r0.backPaint
            int r1 = r1.getAlpha()
            android.graphics.Paint r4 = r0.backPaint
            float r5 = (float) r1
            float r3 = (float) r3
            float r3 = r3 / r23
            float r5 = r5 * r3
            int r3 = (int) r5
            r4.setAlpha(r3)
            r3 = 1
            r2 = r2[r3]
            android.graphics.Paint r4 = r0.backPaint
            r7.drawPath(r2, r4)
            android.graphics.Paint r2 = r0.backPaint
            r2.setAlpha(r1)
            goto L_0x0baf
        L_0x0ba7:
            r3 = 1
            r1 = r2[r3]
            android.graphics.Paint r2 = r0.backPaint
            r7.drawPath(r1, r2)
        L_0x0baf:
            r34.restore()
        L_0x0bb2:
            long r1 = java.lang.System.currentTimeMillis()
            long r3 = r0.lastAnimationTime
            long r3 = r1 - r3
            r5 = 17
            int r8 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r8 <= 0) goto L_0x0bc2
            r3 = 17
        L_0x0bc2:
            r0.lastAnimationTime = r1
            int r1 = r0.currentIcon
            r2 = 3
            if (r1 == r2) goto L_0x0bdc
            r2 = 14
            if (r1 == r2) goto L_0x0bdc
            r5 = 4
            if (r1 != r5) goto L_0x0bd4
            int r5 = r0.nextIcon
            if (r5 == r2) goto L_0x0bdc
        L_0x0bd4:
            r2 = 10
            if (r1 == r2) goto L_0x0bdc
            r2 = 13
            if (r1 != r2) goto L_0x0CLASSNAME
        L_0x0bdc:
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
            if (r1 < r2) goto L_0x0c4f
            r7.restoreToCount(r1)
        L_0x0c4f:
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
