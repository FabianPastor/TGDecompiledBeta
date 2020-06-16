package org.telegram.ui.Components;

import android.graphics.ColorFilter;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.view.animation.DecelerateInterpolator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class MediaActionDrawable extends Drawable {
    private static final float[] pausePath1 = {16.0f, 17.0f, 32.0f, 17.0f, 32.0f, 22.0f, 16.0f, 22.0f, 16.0f, 19.5f};
    private static final float[] pausePath2 = {16.0f, 31.0f, 32.0f, 31.0f, 32.0f, 26.0f, 16.0f, 26.0f, 16.0f, 28.5f};
    private static final float[] playFinalPath = {18.0f, 15.0f, 34.0f, 24.0f, 18.0f, 33.0f};
    private static final float[] playPath1 = {18.0f, 15.0f, 34.0f, 24.0f, 34.0f, 24.0f, 18.0f, 24.0f, 18.0f, 24.0f};
    private static final float[] playPath2 = {18.0f, 33.0f, 34.0f, 24.0f, 34.0f, 24.0f, 18.0f, 24.0f, 18.0f, 24.0f};
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
    private Path path1 = new Path();
    private Path path2 = new Path();
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
        this.paint2.setPathEffect(new CornerPathEffect((float) AndroidUtilities.dp(2.0f)));
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
            if (i3 == 2 && (i == 3 || i == 14)) {
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

    /*  JADX ERROR: JadxRuntimeException in pass: IfRegionVisitor
        jadx.core.utils.exceptions.JadxRuntimeException: Don't wrap MOVE or CONST insns: 0x0bb1: MOVE  (r2v29 android.graphics.drawable.Drawable) = (r28v0 android.graphics.drawable.Drawable)
        	at jadx.core.dex.instructions.args.InsnArg.wrapArg(InsnArg.java:164)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.assignInline(CodeShrinkVisitor.java:133)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.checkInline(CodeShrinkVisitor.java:118)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.shrinkBlock(CodeShrinkVisitor.java:65)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.shrinkMethod(CodeShrinkVisitor.java:43)
        	at jadx.core.dex.visitors.regions.TernaryMod.makeTernaryInsn(TernaryMod.java:122)
        	at jadx.core.dex.visitors.regions.TernaryMod.visitRegion(TernaryMod.java:34)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseIterativeStepInternal(DepthRegionTraversal.java:73)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseIterativeStepInternal(DepthRegionTraversal.java:78)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseIterativeStepInternal(DepthRegionTraversal.java:78)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseIterativeStepInternal(DepthRegionTraversal.java:78)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseIterativeStepInternal(DepthRegionTraversal.java:78)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseIterativeStepInternal(DepthRegionTraversal.java:78)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseIterativeStepInternal(DepthRegionTraversal.java:78)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseIterativeStepInternal(DepthRegionTraversal.java:78)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseIterativeStepInternal(DepthRegionTraversal.java:78)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseIterativeStepInternal(DepthRegionTraversal.java:78)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseIterative(DepthRegionTraversal.java:27)
        	at jadx.core.dex.visitors.regions.IfRegionVisitor.visit(IfRegionVisitor.java:31)
        */
    /* JADX WARNING: Removed duplicated region for block: B:100:0x0366 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x03e5  */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x040b  */
    /* JADX WARNING: Removed duplicated region for block: B:170:0x04f1  */
    /* JADX WARNING: Removed duplicated region for block: B:173:0x04fc  */
    /* JADX WARNING: Removed duplicated region for block: B:175:0x0506  */
    /* JADX WARNING: Removed duplicated region for block: B:181:0x0582  */
    /* JADX WARNING: Removed duplicated region for block: B:184:0x058a  */
    /* JADX WARNING: Removed duplicated region for block: B:196:0x05ad  */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x05b0  */
    /* JADX WARNING: Removed duplicated region for block: B:210:0x060e  */
    /* JADX WARNING: Removed duplicated region for block: B:213:0x0617  */
    /* JADX WARNING: Removed duplicated region for block: B:214:0x061c  */
    /* JADX WARNING: Removed duplicated region for block: B:221:0x0641  */
    /* JADX WARNING: Removed duplicated region for block: B:222:0x0646  */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x0655  */
    /* JADX WARNING: Removed duplicated region for block: B:230:0x0658  */
    /* JADX WARNING: Removed duplicated region for block: B:237:0x066a  */
    /* JADX WARNING: Removed duplicated region for block: B:238:0x066d  */
    /* JADX WARNING: Removed duplicated region for block: B:245:0x0686  */
    /* JADX WARNING: Removed duplicated region for block: B:246:0x0690  */
    /* JADX WARNING: Removed duplicated region for block: B:262:0x071f  */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x0722  */
    /* JADX WARNING: Removed duplicated region for block: B:269:0x0737  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x00a6  */
    /* JADX WARNING: Removed duplicated region for block: B:270:0x073a  */
    /* JADX WARNING: Removed duplicated region for block: B:273:0x074e  */
    /* JADX WARNING: Removed duplicated region for block: B:276:0x0784  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x00ac  */
    /* JADX WARNING: Removed duplicated region for block: B:283:0x0797  */
    /* JADX WARNING: Removed duplicated region for block: B:284:0x079a  */
    /* JADX WARNING: Removed duplicated region for block: B:289:0x07c0  */
    /* JADX WARNING: Removed duplicated region for block: B:297:0x0808  */
    /* JADX WARNING: Removed duplicated region for block: B:300:0x0810 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:315:0x083a  */
    /* JADX WARNING: Removed duplicated region for block: B:316:0x0843  */
    /* JADX WARNING: Removed duplicated region for block: B:319:0x0852  */
    /* JADX WARNING: Removed duplicated region for block: B:323:0x0861  */
    /* JADX WARNING: Removed duplicated region for block: B:327:0x086c  */
    /* JADX WARNING: Removed duplicated region for block: B:331:0x0879  */
    /* JADX WARNING: Removed duplicated region for block: B:334:0x0880  */
    /* JADX WARNING: Removed duplicated region for block: B:335:0x0886  */
    /* JADX WARNING: Removed duplicated region for block: B:338:0x088d A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:350:0x0938  */
    /* JADX WARNING: Removed duplicated region for block: B:366:0x09e8  */
    /* JADX WARNING: Removed duplicated region for block: B:380:0x0af1  */
    /* JADX WARNING: Removed duplicated region for block: B:381:0x0b01  */
    public void draw(android.graphics.Canvas r39) {
        /*
            r38 = this;
            r0 = r38
            r7 = r39
            android.graphics.Rect r8 = r38.getBounds()
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
            r14 = 3
            r15 = 4
            r5 = 14
            r4 = 1065353216(0x3var_, float:1.0)
            if (r1 != r15) goto L_0x0073
            int r1 = r0.currentIcon
            if (r1 == r14) goto L_0x008a
            if (r1 == r5) goto L_0x008a
            int r1 = r39.save()
            float r2 = r0.transitionProgress
            float r2 = r4 - r2
            float r3 = (float) r11
            float r6 = (float) r12
            r7.scale(r2, r2, r3, r6)
            goto L_0x0088
        L_0x0073:
            if (r1 == r13) goto L_0x0079
            r2 = 10
            if (r1 != r2) goto L_0x008a
        L_0x0079:
            int r1 = r0.currentIcon
            if (r1 != r15) goto L_0x008a
            int r1 = r39.save()
            float r2 = r0.transitionProgress
            float r3 = (float) r11
            float r6 = (float) r12
            r7.scale(r2, r2, r3, r6)
        L_0x0088:
            r6 = r1
            goto L_0x008b
        L_0x008a:
            r6 = 0
        L_0x008b:
            r1 = 1077936128(0x40400000, float:3.0)
            org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = r0.currentIcon
            r16 = 1094713344(0x41400000, float:12.0)
            r17 = 1080033280(0x40600000, float:3.5)
            r18 = 1073741824(0x40000000, float:2.0)
            r19 = 1088421888(0x40e00000, float:7.0)
            r20 = 1132396544(0x437var_, float:255.0)
            r21 = 1056964608(0x3var_, float:0.5)
            r9 = 2
            if (r1 == r9) goto L_0x00ac
            int r1 = r0.nextIcon
            if (r1 != r9) goto L_0x00a6
            goto L_0x00ac
        L_0x00a6:
            r31 = r6
            r14 = 14
            goto L_0x035d
        L_0x00ac:
            float r1 = (float) r12
            r23 = 1091567616(0x41100000, float:9.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r23)
            float r3 = (float) r3
            float r13 = r0.scale
            float r3 = r3 * r13
            float r3 = r1 - r3
            r13 = 1091567616(0x41100000, float:9.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r13 = (float) r13
            float r15 = r0.scale
            float r13 = r13 * r15
            float r13 = r13 + r1
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r15 = (float) r15
            float r10 = r0.scale
            float r15 = r15 * r10
            float r15 = r15 + r1
            int r10 = r0.currentIcon
            if (r10 == r14) goto L_0x00d6
            if (r10 != r5) goto L_0x00fb
        L_0x00d6:
            int r10 = r0.nextIcon
            if (r10 != r9) goto L_0x00fb
            android.graphics.Paint r10 = r0.paint
            float r2 = r0.transitionProgress
            float r2 = r2 / r21
            float r2 = java.lang.Math.min(r4, r2)
            float r2 = r2 * r20
            int r2 = (int) r2
            r10.setAlpha(r2)
            float r2 = r0.transitionProgress
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r10 = (float) r10
            float r4 = r0.scale
            float r10 = r10 * r4
            float r10 = r10 + r1
            r25 = r10
            r10 = 1065353216(0x3var_, float:1.0)
            goto L_0x0138
        L_0x00fb:
            int r2 = r0.nextIcon
            if (r2 == r14) goto L_0x0120
            if (r2 == r5) goto L_0x0120
            if (r2 == r9) goto L_0x0120
            android.graphics.Paint r2 = r0.paint
            float r4 = r0.savedTransitionProgress
            float r4 = r4 / r21
            r10 = 1065353216(0x3var_, float:1.0)
            float r4 = java.lang.Math.min(r10, r4)
            float r4 = r4 * r20
            float r5 = r0.transitionProgress
            float r5 = r10 - r5
            float r4 = r4 * r5
            int r4 = (int) r4
            r2.setAlpha(r4)
            float r2 = r0.savedTransitionProgress
            r4 = 255(0xff, float:3.57E-43)
            goto L_0x0129
        L_0x0120:
            android.graphics.Paint r2 = r0.paint
            r4 = 255(0xff, float:3.57E-43)
            r2.setAlpha(r4)
            float r2 = r0.transitionProgress
        L_0x0129:
            r10 = 1065353216(0x3var_, float:1.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r5 = (float) r5
            float r4 = r0.scale
            float r5 = r5 * r4
            float r4 = r1 + r5
            r25 = r4
        L_0x0138:
            boolean r4 = r0.animatingTransition
            r5 = 1090519040(0x41000000, float:8.0)
            if (r4 == 0) goto L_0x030b
            int r4 = r0.nextIcon
            if (r4 == r9) goto L_0x02ba
            int r4 = (r2 > r21 ? 1 : (r2 == r21 ? 0 : -1))
            if (r4 > 0) goto L_0x0148
            goto L_0x02ba
        L_0x0148:
            r3 = 1095761920(0x41500000, float:13.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            float r4 = r0.scale
            float r3 = r3 * r4
            float r3 = r3 * r4
            boolean r4 = r0.isMini
            if (r4 == 0) goto L_0x015e
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r18)
            goto L_0x015f
        L_0x015e:
            r4 = 0
        L_0x015f:
            float r4 = (float) r4
            float r3 = r3 + r4
            float r2 = r2 - r21
            float r4 = r2 / r21
            r5 = 1045220557(0x3e4ccccd, float:0.2)
            int r5 = (r2 > r5 ? 1 : (r2 == r5 ? 0 : -1))
            if (r5 <= 0) goto L_0x0179
            r5 = 1045220557(0x3e4ccccd, float:0.2)
            float r2 = r2 - r5
            r5 = 1050253722(0x3e99999a, float:0.3)
            float r2 = r2 / r5
            r27 = r2
            r13 = 1065353216(0x3var_, float:1.0)
            goto L_0x0180
        L_0x0179:
            r5 = 1045220557(0x3e4ccccd, float:0.2)
            float r2 = r2 / r5
            r13 = r2
            r27 = 0
        L_0x0180:
            android.graphics.RectF r2 = r0.rect
            float r5 = (float) r11
            float r10 = r5 - r3
            float r3 = r3 / r18
            float r9 = r15 - r3
            float r3 = r3 + r15
            r2.set(r10, r9, r5, r3)
            r2 = 1120403456(0x42CLASSNAME, float:100.0)
            float r3 = r27 * r2
            android.graphics.RectF r2 = r0.rect
            r9 = 1120927744(0x42d00000, float:104.0)
            float r4 = r4 * r9
            float r4 = r4 - r3
            r9 = 0
            android.graphics.Paint r10 = r0.paint
            r28 = r1
            r1 = r39
            r14 = 1065353216(0x3var_, float:1.0)
            r30 = r5
            r14 = 14
            r5 = r9
            r31 = r6
            r9 = 0
            r6 = r10
            r1.drawArc(r2, r3, r4, r5, r6)
            float r1 = r15 - r25
            float r1 = r1 * r13
            float r25 = r25 + r1
            r1 = 0
            int r2 = (r27 > r1 ? 1 : (r27 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x02b3
            int r1 = r0.nextIcon
            if (r1 != r14) goto L_0x01be
            r10 = 0
            goto L_0x01c7
        L_0x01be:
            r1 = -1036779520(0xffffffffCLASSNAME, float:-45.0)
            r2 = 1065353216(0x3var_, float:1.0)
            float r4 = r2 - r27
            float r1 = r1 * r4
            r10 = r1
        L_0x01c7:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r19)
            float r1 = (float) r1
            float r1 = r1 * r27
            float r2 = r0.scale
            float r1 = r1 * r2
            float r2 = r27 * r20
            int r2 = (int) r2
            int r3 = r0.nextIcon
            r4 = 3
            if (r3 == r4) goto L_0x01ef
            if (r3 == r14) goto L_0x01ef
            r4 = 2
            if (r3 == r4) goto L_0x01ef
            float r3 = r0.transitionProgress
            float r3 = r3 / r21
            r4 = 1065353216(0x3var_, float:1.0)
            float r3 = java.lang.Math.min(r4, r3)
            float r3 = r4 - r3
            float r2 = (float) r2
            float r2 = r2 * r3
            int r2 = (int) r2
        L_0x01ef:
            r13 = r2
            r2 = 0
            int r3 = (r10 > r2 ? 1 : (r10 == r2 ? 0 : -1))
            if (r3 == 0) goto L_0x0200
            r39.save()
            r2 = r28
            r6 = r30
            r7.rotate(r10, r6, r2)
            goto L_0x0204
        L_0x0200:
            r2 = r28
            r6 = r30
        L_0x0204:
            if (r13 == 0) goto L_0x02a8
            android.graphics.Paint r3 = r0.paint
            r3.setAlpha(r13)
            int r3 = r0.nextIcon
            if (r3 != r14) goto L_0x0288
            android.graphics.Paint r1 = r0.paint3
            r1.setAlpha(r13)
            android.graphics.RectF r1 = r0.rect
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r2 = r11 - r2
            float r2 = (float) r2
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r3 = r12 - r3
            float r3 = (float) r3
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r4 = r4 + r11
            float r4 = (float) r4
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r5 = r5 + r12
            float r5 = (float) r5
            r1.set(r2, r3, r4, r5)
            android.graphics.RectF r1 = r0.rect
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r2 = (float) r2
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r3 = (float) r3
            android.graphics.Paint r4 = r0.paint3
            r7.drawRoundRect(r1, r2, r3, r4)
            android.graphics.Paint r1 = r0.paint
            float r2 = (float) r13
            r3 = 1041865114(0x3e19999a, float:0.15)
            float r2 = r2 * r3
            int r2 = (int) r2
            r1.setAlpha(r2)
            boolean r1 = r0.isMini
            if (r1 == 0) goto L_0x0257
            r3 = 1073741824(0x40000000, float:2.0)
            goto L_0x0259
        L_0x0257:
            r3 = 1082130432(0x40800000, float:4.0)
        L_0x0259:
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
            int r9 = r8.bottom
            int r9 = r9 - r1
            float r1 = (float) r9
            r2.set(r3, r4, r5, r1)
            android.graphics.RectF r2 = r0.rect
            r3 = 0
            r4 = 1135869952(0x43b40000, float:360.0)
            r5 = 0
            android.graphics.Paint r9 = r0.paint
            r1 = r39
            r24 = r6
            r6 = r9
            r1.drawArc(r2, r3, r4, r5, r6)
            android.graphics.Paint r1 = r0.paint
            r1.setAlpha(r13)
            goto L_0x02aa
        L_0x0288:
            r24 = r6
            float r9 = r24 - r1
            float r13 = r2 - r1
            float r26 = r24 + r1
            float r27 = r2 + r1
            android.graphics.Paint r6 = r0.paint
            r1 = r39
            r2 = r9
            r3 = r13
            r4 = r26
            r5 = r27
            r1.drawLine(r2, r3, r4, r5, r6)
            android.graphics.Paint r6 = r0.paint
            r2 = r26
            r4 = r9
            r1.drawLine(r2, r3, r4, r5, r6)
            goto L_0x02aa
        L_0x02a8:
            r24 = r6
        L_0x02aa:
            r1 = 0
            int r2 = (r10 > r1 ? 1 : (r10 == r1 ? 0 : -1))
            if (r2 == 0) goto L_0x02b5
            r39.restore()
            goto L_0x02b5
        L_0x02b3:
            r24 = r30
        L_0x02b5:
            r2 = r15
            r1 = r24
            r5 = r1
            goto L_0x0305
        L_0x02ba:
            r31 = r6
            r14 = 14
            int r1 = r0.nextIcon
            r4 = 2
            if (r1 != r4) goto L_0x02c8
            r1 = 1065353216(0x3var_, float:1.0)
            float r4 = r1 - r2
            goto L_0x02ce
        L_0x02c8:
            r1 = 1065353216(0x3var_, float:1.0)
            float r4 = r2 / r21
            float r2 = r1 - r4
        L_0x02ce:
            float r25 = r25 - r3
            float r25 = r25 * r4
            float r25 = r3 + r25
            float r15 = r15 - r13
            float r15 = r15 * r4
            float r15 = r15 + r13
            float r1 = (float) r11
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r3 = (float) r3
            float r3 = r3 * r2
            float r4 = r0.scale
            float r3 = r3 * r4
            float r3 = r1 - r3
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r4 = (float) r4
            float r4 = r4 * r2
            float r6 = r0.scale
            float r4 = r4 * r6
            float r1 = r1 + r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r4 = (float) r4
            float r4 = r4 * r2
            float r2 = r0.scale
            float r4 = r4 * r2
            float r2 = r15 - r4
            r5 = r3
            r37 = r15
            r15 = r2
            r2 = r37
        L_0x0305:
            r10 = r1
            r13 = r2
            r9 = r5
            r3 = r25
            goto L_0x0333
        L_0x030b:
            r31 = r6
            r14 = 14
            float r1 = (float) r11
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r2 = (float) r2
            float r4 = r0.scale
            float r2 = r2 * r4
            float r2 = r1 - r2
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r4 = (float) r4
            float r6 = r0.scale
            float r4 = r4 * r6
            float r1 = r1 + r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r4 = (float) r4
            float r5 = r0.scale
            float r4 = r4 * r5
            float r4 = r13 - r4
            r10 = r1
            r9 = r2
            r15 = r4
        L_0x0333:
            int r1 = (r3 > r13 ? 1 : (r3 == r13 ? 0 : -1))
            if (r1 == 0) goto L_0x0341
            float r4 = (float) r11
            android.graphics.Paint r6 = r0.paint
            r1 = r39
            r2 = r4
            r5 = r13
            r1.drawLine(r2, r3, r4, r5, r6)
        L_0x0341:
            float r6 = (float) r11
            int r1 = (r9 > r6 ? 1 : (r9 == r6 ? 0 : -1))
            if (r1 == 0) goto L_0x035d
            android.graphics.Paint r5 = r0.paint
            r1 = r39
            r2 = r9
            r3 = r15
            r4 = r6
            r9 = r5
            r5 = r13
            r24 = r6
            r6 = r9
            r1.drawLine(r2, r3, r4, r5, r6)
            android.graphics.Paint r6 = r0.paint
            r2 = r10
            r4 = r24
            r1.drawLine(r2, r3, r4, r5, r6)
        L_0x035d:
            int r1 = r0.currentIcon
            r9 = 5
            r10 = 13
            r13 = 1
            r2 = 3
            if (r1 == r2) goto L_0x03de
            if (r1 == r14) goto L_0x03de
            r3 = 4
            if (r1 != r3) goto L_0x0372
            int r1 = r0.nextIcon
            if (r1 == r14) goto L_0x03de
            if (r1 != r2) goto L_0x0372
            goto L_0x03de
        L_0x0372:
            int r1 = r0.currentIcon
            r2 = 10
            if (r1 == r2) goto L_0x0380
            int r2 = r0.nextIcon
            r3 = 10
            if (r2 == r3) goto L_0x0380
            if (r1 != r10) goto L_0x0611
        L_0x0380:
            int r1 = r0.nextIcon
            r2 = 4
            if (r1 == r2) goto L_0x038c
            r2 = 6
            if (r1 != r2) goto L_0x0389
            goto L_0x038c
        L_0x0389:
            r2 = 255(0xff, float:3.57E-43)
            goto L_0x0395
        L_0x038c:
            float r1 = r0.transitionProgress
            r2 = 1065353216(0x3var_, float:1.0)
            float r4 = r2 - r1
            float r4 = r4 * r20
            int r2 = (int) r4
        L_0x0395:
            if (r2 == 0) goto L_0x0611
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
            if (r1 == 0) goto L_0x03b5
            r3 = 1073741824(0x40000000, float:2.0)
            goto L_0x03b7
        L_0x03b5:
            r3 = 1082130432(0x40800000, float:4.0)
        L_0x03b7:
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
            r1 = r39
            r1.drawArc(r2, r3, r4, r5, r6)
            goto L_0x0611
        L_0x03de:
            r15 = 1082130432(0x40800000, float:4.0)
            int r1 = r0.nextIcon
            r2 = 2
            if (r1 != r2) goto L_0x040b
            float r1 = r0.transitionProgress
            int r2 = (r1 > r21 ? 1 : (r1 == r21 ? 0 : -1))
            if (r2 > 0) goto L_0x0400
            float r1 = r1 / r21
            r2 = 1065353216(0x3var_, float:1.0)
            float r4 = r2 - r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r19)
            float r1 = (float) r1
            float r1 = r1 * r4
            float r2 = r0.scale
            float r1 = r1 * r2
            float r4 = r4 * r20
            int r6 = (int) r4
            goto L_0x0402
        L_0x0400:
            r1 = 0
            r6 = 0
        L_0x0402:
            r2 = 1065353216(0x3var_, float:1.0)
            r3 = 0
            r4 = 0
            r5 = 1065353216(0x3var_, float:1.0)
        L_0x0408:
            r13 = 0
            goto L_0x04ed
        L_0x040b:
            if (r1 == 0) goto L_0x04b8
            if (r1 == r13) goto L_0x04b8
            if (r1 == r9) goto L_0x04b8
            r2 = 8
            if (r1 == r2) goto L_0x04b8
            r2 = 9
            if (r1 == r2) goto L_0x04b8
            r2 = 7
            if (r1 == r2) goto L_0x04b8
            r2 = 6
            if (r1 != r2) goto L_0x0421
            goto L_0x04b8
        L_0x0421:
            r2 = 4
            if (r1 != r2) goto L_0x0463
            float r1 = r0.transitionProgress
            r2 = 1065353216(0x3var_, float:1.0)
            float r4 = r2 - r1
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r19)
            float r2 = (float) r2
            float r3 = r0.scale
            float r2 = r2 * r3
            float r3 = r4 * r20
            int r3 = (int) r3
            int r5 = r0.currentIcon
            if (r5 != r14) goto L_0x0444
            int r1 = r8.left
            float r1 = (float) r1
            int r5 = r8.top
            float r5 = (float) r5
            r6 = r5
            r5 = r1
            r1 = 0
            goto L_0x0456
        L_0x0444:
            r4 = 1110704128(0x42340000, float:45.0)
            float r1 = r1 * r4
            int r4 = r8.centerX()
            float r4 = (float) r4
            int r5 = r8.centerY()
            float r5 = (float) r5
            r6 = r5
            r5 = r4
            r4 = 1065353216(0x3var_, float:1.0)
        L_0x0456:
            r13 = r1
            r1 = r2
            r2 = 1065353216(0x3var_, float:1.0)
            r37 = r6
            r6 = r3
        L_0x045d:
            r3 = r5
            r5 = r4
            r4 = r37
            goto L_0x04ed
        L_0x0463:
            if (r1 == r14) goto L_0x047b
            r2 = 3
            if (r1 != r2) goto L_0x0469
            goto L_0x047b
        L_0x0469:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r19)
            float r1 = (float) r1
            float r2 = r0.scale
            float r1 = r1 * r2
            r2 = 1065353216(0x3var_, float:1.0)
            r3 = 0
            r4 = 0
            r5 = 1065353216(0x3var_, float:1.0)
            r6 = 255(0xff, float:3.57E-43)
            goto L_0x0408
        L_0x047b:
            float r1 = r0.transitionProgress
            r2 = 1065353216(0x3var_, float:1.0)
            float r4 = r2 - r1
            int r2 = r0.currentIcon
            r3 = 4
            if (r2 != r3) goto L_0x0489
            r4 = r1
            r2 = 0
            goto L_0x0490
        L_0x0489:
            r2 = 1110704128(0x42340000, float:45.0)
            float r4 = r4 * r2
            r2 = r4
            r4 = 1065353216(0x3var_, float:1.0)
        L_0x0490:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r19)
            float r3 = (float) r3
            float r5 = r0.scale
            float r3 = r3 * r5
            float r1 = r1 * r20
            int r1 = (int) r1
            int r5 = r0.nextIcon
            if (r5 != r14) goto L_0x04a6
            int r5 = r8.left
            float r5 = (float) r5
            int r6 = r8.top
            goto L_0x04af
        L_0x04a6:
            int r5 = r8.centerX()
            float r5 = (float) r5
            int r6 = r8.centerY()
        L_0x04af:
            float r6 = (float) r6
            r13 = r2
            r2 = 1065353216(0x3var_, float:1.0)
            r37 = r6
            r6 = r1
            r1 = r3
            goto L_0x045d
        L_0x04b8:
            int r1 = r0.nextIcon
            r2 = 6
            if (r1 != r2) goto L_0x04c8
            float r1 = r0.transitionProgress
            float r1 = r1 / r21
            r2 = 1065353216(0x3var_, float:1.0)
            float r1 = java.lang.Math.min(r2, r1)
            goto L_0x04cc
        L_0x04c8:
            r2 = 1065353216(0x3var_, float:1.0)
            float r1 = r0.transitionProgress
        L_0x04cc:
            float r4 = r2 - r1
            r3 = 1110704128(0x42340000, float:45.0)
            float r1 = r1 * r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r19)
            float r3 = (float) r3
            float r3 = r3 * r4
            float r5 = r0.scale
            float r3 = r3 * r5
            float r4 = r4 * r18
            float r4 = java.lang.Math.min(r2, r4)
            float r4 = r4 * r20
            int r4 = (int) r4
            r13 = r1
            r1 = r3
            r6 = r4
            r3 = 0
            r4 = 0
            r5 = 1065353216(0x3var_, float:1.0)
        L_0x04ed:
            int r25 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1))
            if (r25 == 0) goto L_0x04f7
            r39.save()
            r7.scale(r5, r5, r3, r4)
        L_0x04f7:
            r2 = 0
            int r3 = (r13 > r2 ? 1 : (r13 == r2 ? 0 : -1))
            if (r3 == 0) goto L_0x0504
            r39.save()
            float r2 = (float) r11
            float r3 = (float) r12
            r7.rotate(r13, r2, r3)
        L_0x0504:
            if (r6 == 0) goto L_0x0582
            android.graphics.Paint r2 = r0.paint
            float r3 = (float) r6
            float r4 = r0.overrideAlpha
            float r4 = r4 * r3
            int r4 = (int) r4
            r2.setAlpha(r4)
            int r2 = r0.currentIcon
            if (r2 == r14) goto L_0x0544
            int r2 = r0.nextIcon
            if (r2 != r14) goto L_0x051a
            goto L_0x0544
        L_0x051a:
            float r2 = (float) r11
            float r17 = r2 - r1
            float r3 = (float) r12
            float r25 = r3 - r1
            float r26 = r2 + r1
            float r27 = r3 + r1
            android.graphics.Paint r4 = r0.paint
            r1 = r39
            r2 = r17
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
            r4 = r17
            r1.drawLine(r2, r3, r4, r5, r6)
            goto L_0x0585
        L_0x0544:
            r29 = r5
            r10 = r6
            android.graphics.Paint r1 = r0.paint3
            float r2 = r0.overrideAlpha
            float r3 = r3 * r2
            int r2 = (int) r3
            r1.setAlpha(r2)
            android.graphics.RectF r1 = r0.rect
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r2 = r11 - r2
            float r2 = (float) r2
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r3 = r12 - r3
            float r3 = (float) r3
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r4 = r4 + r11
            float r4 = (float) r4
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r5 = r5 + r12
            float r5 = (float) r5
            r1.set(r2, r3, r4, r5)
            android.graphics.RectF r1 = r0.rect
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r2 = (float) r2
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r3 = (float) r3
            android.graphics.Paint r4 = r0.paint3
            r7.drawRoundRect(r1, r2, r3, r4)
            goto L_0x0585
        L_0x0582:
            r29 = r5
            r10 = r6
        L_0x0585:
            r1 = 0
            int r2 = (r13 > r1 ? 1 : (r13 == r1 ? 0 : -1))
            if (r2 == 0) goto L_0x058d
            r39.restore()
        L_0x058d:
            int r1 = r0.currentIcon
            r2 = 3
            if (r1 == r2) goto L_0x059d
            if (r1 == r14) goto L_0x059d
            r3 = 4
            if (r1 != r3) goto L_0x0608
            int r1 = r0.nextIcon
            if (r1 == r14) goto L_0x059d
            if (r1 != r2) goto L_0x0608
        L_0x059d:
            if (r10 == 0) goto L_0x0608
            r1 = 1135869952(0x43b40000, float:360.0)
            float r2 = r0.animatedDownloadProgress
            float r2 = r2 * r1
            float r13 = java.lang.Math.max(r15, r2)
            boolean r1 = r0.isMini
            if (r1 == 0) goto L_0x05b0
            r3 = 1073741824(0x40000000, float:2.0)
            goto L_0x05b2
        L_0x05b0:
            r3 = 1082130432(0x40800000, float:4.0)
        L_0x05b2:
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
            if (r1 == r14) goto L_0x05d9
            r2 = 4
            if (r1 != r2) goto L_0x05fb
            int r1 = r0.nextIcon
            if (r1 == r14) goto L_0x05d9
            r2 = 3
            if (r1 != r2) goto L_0x05fb
        L_0x05d9:
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
            r1 = r39
            r1.drawArc(r2, r3, r4, r5, r6)
            android.graphics.Paint r1 = r0.paint
            r1.setAlpha(r10)
        L_0x05fb:
            android.graphics.RectF r2 = r0.rect
            float r3 = r0.downloadRadOffset
            r5 = 0
            android.graphics.Paint r6 = r0.paint
            r1 = r39
            r4 = r13
            r1.drawArc(r2, r3, r4, r5, r6)
        L_0x0608:
            r1 = 1065353216(0x3var_, float:1.0)
            int r2 = (r29 > r1 ? 1 : (r29 == r1 ? 0 : -1))
            if (r2 == 0) goto L_0x0611
            r39.restore()
        L_0x0611:
            int r1 = r0.currentIcon
            int r2 = r0.nextIcon
            if (r1 != r2) goto L_0x061c
            r10 = 1065353216(0x3var_, float:1.0)
            r13 = 1065353216(0x3var_, float:1.0)
            goto L_0x063d
        L_0x061c:
            r2 = 4
            if (r1 != r2) goto L_0x0626
            float r4 = r0.transitionProgress
            r1 = 1065353216(0x3var_, float:1.0)
            float r2 = r1 - r4
            goto L_0x063b
        L_0x0626:
            r1 = 1065353216(0x3var_, float:1.0)
            float r2 = r0.transitionProgress
            float r2 = r2 / r21
            float r4 = java.lang.Math.min(r1, r2)
            float r2 = r0.transitionProgress
            float r2 = r2 / r21
            float r2 = r1 - r2
            r1 = 0
            float r2 = java.lang.Math.max(r1, r2)
        L_0x063b:
            r13 = r2
            r10 = r4
        L_0x063d:
            int r1 = r0.nextIcon
            if (r1 != r9) goto L_0x0646
            android.graphics.Path[] r1 = org.telegram.ui.ActionBar.Theme.chat_filePath
            r15 = r1
            r6 = 0
            goto L_0x0650
        L_0x0646:
            int r1 = r0.currentIcon
            if (r1 != r9) goto L_0x064e
            android.graphics.Path[] r1 = org.telegram.ui.ActionBar.Theme.chat_filePath
            r6 = r1
            goto L_0x064f
        L_0x064e:
            r6 = 0
        L_0x064f:
            r15 = 0
        L_0x0650:
            int r1 = r0.nextIcon
            r2 = 7
            if (r1 != r2) goto L_0x0658
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.chat_flameIcon
            goto L_0x0663
        L_0x0658:
            int r1 = r0.currentIcon
            r2 = 7
            if (r1 != r2) goto L_0x0662
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.chat_flameIcon
            r2 = r1
            r1 = 0
            goto L_0x0664
        L_0x0662:
            r1 = 0
        L_0x0663:
            r2 = 0
        L_0x0664:
            int r3 = r0.nextIcon
            r4 = 8
            if (r3 != r4) goto L_0x066d
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.chat_gifIcon
            goto L_0x0675
        L_0x066d:
            int r3 = r0.currentIcon
            r4 = 8
            if (r3 != r4) goto L_0x0675
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.chat_gifIcon
        L_0x0675:
            r5 = r1
            r4 = r2
            int r1 = r0.currentIcon
            r2 = 9
            r17 = 1086324736(0x40CLASSNAME, float:6.0)
            if (r1 == r2) goto L_0x0690
            int r1 = r0.nextIcon
            r2 = 9
            if (r1 != r2) goto L_0x0686
            goto L_0x0690
        L_0x0686:
            r14 = r4
            r33 = r6
            r32 = r13
            r25 = r15
            r13 = r5
            goto L_0x070d
        L_0x0690:
            android.graphics.Paint r1 = r0.paint
            int r2 = r0.currentIcon
            int r3 = r0.nextIcon
            if (r2 != r3) goto L_0x069b
            r2 = 255(0xff, float:3.57E-43)
            goto L_0x06a0
        L_0x069b:
            float r2 = r0.transitionProgress
            float r2 = r2 * r20
            int r2 = (int) r2
        L_0x06a0:
            r1.setAlpha(r2)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r3 = r12 + r1
            r1 = 1077936128(0x40400000, float:3.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r2 = r11 - r1
            int r1 = r0.currentIcon
            int r14 = r0.nextIcon
            if (r1 == r14) goto L_0x06c1
            r39.save()
            float r1 = r0.transitionProgress
            float r14 = (float) r11
            float r9 = (float) r12
            r7.scale(r1, r1, r14, r9)
        L_0x06c1:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r1 = r2 - r1
            float r9 = (float) r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r1 = r3 - r1
            float r14 = (float) r1
            float r1 = (float) r2
            r25 = r15
            float r15 = (float) r3
            r27 = r6
            android.graphics.Paint r6 = r0.paint
            r28 = r1
            r1 = r39
            r29 = r2
            r2 = r9
            r9 = r3
            r3 = r14
            r14 = r4
            r4 = r28
            r32 = r13
            r13 = r5
            r5 = r15
            r33 = r27
            r1.drawLine(r2, r3, r4, r5, r6)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r2 = r29 + r1
            float r4 = (float) r2
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r3 = r9 - r1
            float r5 = (float) r3
            android.graphics.Paint r6 = r0.paint
            r1 = r39
            r2 = r28
            r3 = r15
            r1.drawLine(r2, r3, r4, r5, r6)
            int r1 = r0.currentIcon
            int r2 = r0.nextIcon
            if (r1 == r2) goto L_0x070d
            r39.restore()
        L_0x070d:
            int r1 = r0.currentIcon
            r2 = 12
            if (r1 == r2) goto L_0x0719
            int r1 = r0.nextIcon
            r2 = 12
            if (r1 != r2) goto L_0x0787
        L_0x0719:
            int r1 = r0.currentIcon
            int r2 = r0.nextIcon
            if (r1 != r2) goto L_0x0722
            r4 = 1065353216(0x3var_, float:1.0)
            goto L_0x072f
        L_0x0722:
            r1 = 13
            if (r2 != r1) goto L_0x0729
            float r4 = r0.transitionProgress
            goto L_0x072f
        L_0x0729:
            float r1 = r0.transitionProgress
            r2 = 1065353216(0x3var_, float:1.0)
            float r4 = r2 - r1
        L_0x072f:
            android.graphics.Paint r1 = r0.paint
            int r2 = r0.currentIcon
            int r3 = r0.nextIcon
            if (r2 != r3) goto L_0x073a
            r2 = 255(0xff, float:3.57E-43)
            goto L_0x073d
        L_0x073a:
            float r2 = r4 * r20
            int r2 = (int) r2
        L_0x073d:
            r1.setAlpha(r2)
            org.telegram.messenger.AndroidUtilities.dp(r19)
            r1 = 1077936128(0x40400000, float:3.0)
            org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = r0.currentIcon
            int r2 = r0.nextIcon
            if (r1 == r2) goto L_0x0756
            r39.save()
            float r1 = (float) r11
            float r2 = (float) r12
            r7.scale(r4, r4, r1, r2)
        L_0x0756:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r19)
            float r1 = (float) r1
            float r2 = r0.scale
            float r1 = r1 * r2
            float r2 = (float) r11
            float r9 = r2 - r1
            float r3 = (float) r12
            float r15 = r3 - r1
            float r27 = r2 + r1
            float r28 = r3 + r1
            android.graphics.Paint r6 = r0.paint
            r1 = r39
            r2 = r9
            r3 = r15
            r4 = r27
            r5 = r28
            r1.drawLine(r2, r3, r4, r5, r6)
            android.graphics.Paint r6 = r0.paint
            r2 = r27
            r4 = r9
            r1.drawLine(r2, r3, r4, r5, r6)
            int r1 = r0.currentIcon
            int r2 = r0.nextIcon
            if (r1 == r2) goto L_0x0787
            r39.restore()
        L_0x0787:
            int r1 = r0.currentIcon
            r2 = 13
            if (r1 == r2) goto L_0x0791
            int r1 = r0.nextIcon
            if (r1 != r2) goto L_0x080b
        L_0x0791:
            int r1 = r0.currentIcon
            int r3 = r0.nextIcon
            if (r1 != r3) goto L_0x079a
            r4 = 1065353216(0x3var_, float:1.0)
            goto L_0x07a5
        L_0x079a:
            if (r3 != r2) goto L_0x079f
            float r4 = r0.transitionProgress
            goto L_0x07a5
        L_0x079f:
            float r1 = r0.transitionProgress
            r2 = 1065353216(0x3var_, float:1.0)
            float r4 = r2 - r1
        L_0x07a5:
            android.text.TextPaint r1 = r0.textPaint
            float r2 = r4 * r20
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
            if (r3 == r5) goto L_0x07c8
            r39.save()
            float r3 = (float) r11
            float r5 = (float) r12
            r7.scale(r4, r4, r3, r5)
        L_0x07c8:
            float r3 = r0.animatedDownloadProgress
            r4 = 1120403456(0x42CLASSNAME, float:100.0)
            float r3 = r3 * r4
            int r3 = (int) r3
            java.lang.String r4 = r0.percentString
            if (r4 == 0) goto L_0x07d7
            int r4 = r0.lastPercent
            if (r3 == r4) goto L_0x07f9
        L_0x07d7:
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
        L_0x07f9:
            java.lang.String r3 = r0.percentString
            float r2 = (float) r2
            float r1 = (float) r1
            android.text.TextPaint r4 = r0.textPaint
            r7.drawText(r3, r2, r1, r4)
            int r1 = r0.currentIcon
            int r2 = r0.nextIcon
            if (r1 == r2) goto L_0x080b
            r39.restore()
        L_0x080b:
            int r1 = r0.currentIcon
            r2 = 1
            if (r1 == 0) goto L_0x0826
            if (r1 == r2) goto L_0x0826
            int r1 = r0.nextIcon
            if (r1 == 0) goto L_0x0826
            if (r1 != r2) goto L_0x0819
            goto L_0x0826
        L_0x0819:
            r8 = r10
            r18 = r11
            r34 = r12
            r27 = r13
            r28 = r14
            r9 = 255(0xff, float:3.57E-43)
            goto L_0x0b14
        L_0x0826:
            int r1 = r0.currentIcon
            if (r1 != 0) goto L_0x082e
            int r1 = r0.nextIcon
            if (r1 == r2) goto L_0x0836
        L_0x082e:
            int r1 = r0.currentIcon
            if (r1 != r2) goto L_0x0843
            int r1 = r0.nextIcon
            if (r1 != 0) goto L_0x0843
        L_0x0836:
            boolean r1 = r0.animatingTransition
            if (r1 == 0) goto L_0x0843
            android.view.animation.DecelerateInterpolator r1 = r0.interpolator
            float r2 = r0.transitionProgress
            float r1 = r1.getInterpolation(r2)
            goto L_0x0844
        L_0x0843:
            r1 = 0
        L_0x0844:
            android.graphics.Path r2 = r0.path1
            r2.reset()
            android.graphics.Path r2 = r0.path2
            r2.reset()
            int r2 = r0.currentIcon
            if (r2 == 0) goto L_0x0861
            r3 = 1
            if (r2 == r3) goto L_0x0859
            r2 = 0
            r3 = 0
            r4 = 0
            goto L_0x0867
        L_0x0859:
            float[] r2 = pausePath1
            float[] r3 = pausePath2
            r6 = 90
            r4 = 0
            goto L_0x0868
        L_0x0861:
            float[] r2 = playPath1
            float[] r3 = playPath2
            float[] r4 = playFinalPath
        L_0x0867:
            r6 = 0
        L_0x0868:
            int r5 = r0.nextIcon
            if (r5 == 0) goto L_0x0879
            r9 = 1
            if (r5 == r9) goto L_0x0872
            r5 = 0
            r9 = 0
            goto L_0x087d
        L_0x0872:
            float[] r5 = pausePath1
            float[] r9 = pausePath2
            r15 = 90
            goto L_0x087e
        L_0x0879:
            float[] r5 = playPath1
            float[] r9 = playPath2
        L_0x087d:
            r15 = 0
        L_0x087e:
            if (r2 != 0) goto L_0x0886
            r2 = r5
            r3 = r9
            r9 = 0
            r22 = 0
            goto L_0x0889
        L_0x0886:
            r22 = r9
            r9 = r5
        L_0x0889:
            boolean r5 = r0.animatingTransition
            if (r5 != 0) goto L_0x0930
            if (r4 == 0) goto L_0x0930
            r2 = 0
        L_0x0890:
            int r3 = r4.length
            r5 = 2
            int r3 = r3 / r5
            if (r2 >= r3) goto L_0x0922
            if (r2 != 0) goto L_0x08d8
            android.graphics.Path r3 = r0.path1
            int r5 = r2 * 2
            r9 = r4[r5]
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r9 = (float) r9
            r27 = r13
            float r13 = r0.scale
            float r9 = r9 * r13
            int r13 = r5 + 1
            r18 = r4[r13]
            r28 = r14
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r14 = (float) r14
            r29 = r10
            float r10 = r0.scale
            float r14 = r14 * r10
            r3.moveTo(r9, r14)
            android.graphics.Path r3 = r0.path2
            r5 = r4[r5]
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            float r9 = r0.scale
            float r5 = r5 * r9
            r9 = r4[r13]
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r9 = (float) r9
            float r10 = r0.scale
            float r9 = r9 * r10
            r3.moveTo(r5, r9)
            goto L_0x0918
        L_0x08d8:
            r29 = r10
            r27 = r13
            r28 = r14
            android.graphics.Path r3 = r0.path1
            int r5 = r2 * 2
            r9 = r4[r5]
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r9 = (float) r9
            float r10 = r0.scale
            float r9 = r9 * r10
            int r10 = r5 + 1
            r13 = r4[r10]
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r13 = (float) r13
            float r14 = r0.scale
            float r13 = r13 * r14
            r3.lineTo(r9, r13)
            android.graphics.Path r3 = r0.path2
            r5 = r4[r5]
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            float r9 = r0.scale
            float r5 = r5 * r9
            r9 = r4[r10]
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r9 = (float) r9
            float r10 = r0.scale
            float r9 = r9 * r10
            r3.lineTo(r5, r9)
        L_0x0918:
            int r2 = r2 + 1
            r13 = r27
            r14 = r28
            r10 = r29
            goto L_0x0890
        L_0x0922:
            r29 = r10
            r27 = r13
            r28 = r14
            r18 = r11
            r34 = r12
        L_0x092c:
            r9 = 255(0xff, float:3.57E-43)
            goto L_0x0abc
        L_0x0930:
            r29 = r10
            r27 = r13
            r28 = r14
            if (r9 != 0) goto L_0x09e8
            r4 = 0
        L_0x0939:
            r5 = 5
            if (r4 >= r5) goto L_0x09bd
            if (r4 != 0) goto L_0x097b
            android.graphics.Path r5 = r0.path1
            int r9 = r4 * 2
            r10 = r2[r9]
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r10 = (float) r10
            float r13 = r0.scale
            float r10 = r10 * r13
            int r13 = r9 + 1
            r14 = r2[r13]
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r14 = (float) r14
            r34 = r12
            float r12 = r0.scale
            float r14 = r14 * r12
            r5.moveTo(r10, r14)
            android.graphics.Path r5 = r0.path2
            r9 = r3[r9]
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r9 = (float) r9
            float r10 = r0.scale
            float r9 = r9 * r10
            r10 = r3[r13]
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r10 = (float) r10
            float r12 = r0.scale
            float r10 = r10 * r12
            r5.moveTo(r9, r10)
            goto L_0x09b7
        L_0x097b:
            r34 = r12
            android.graphics.Path r5 = r0.path1
            int r9 = r4 * 2
            r10 = r2[r9]
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r10 = (float) r10
            float r12 = r0.scale
            float r10 = r10 * r12
            int r12 = r9 + 1
            r13 = r2[r12]
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r13 = (float) r13
            float r14 = r0.scale
            float r13 = r13 * r14
            r5.lineTo(r10, r13)
            android.graphics.Path r5 = r0.path2
            r9 = r3[r9]
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r9 = (float) r9
            float r10 = r0.scale
            float r9 = r9 * r10
            r10 = r3[r12]
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r10 = (float) r10
            float r12 = r0.scale
            float r10 = r10 * r12
            r5.lineTo(r9, r10)
        L_0x09b7:
            int r4 = r4 + 1
            r12 = r34
            goto L_0x0939
        L_0x09bd:
            r34 = r12
            int r2 = r0.nextIcon
            r3 = 4
            if (r2 != r3) goto L_0x09d6
            android.graphics.Paint r2 = r0.paint2
            float r3 = r0.transitionProgress
            r4 = 1065353216(0x3var_, float:1.0)
            float r3 = r4 - r3
            float r3 = r3 * r20
            int r3 = (int) r3
            r2.setAlpha(r3)
        L_0x09d2:
            r18 = r11
            goto L_0x092c
        L_0x09d6:
            android.graphics.Paint r3 = r0.paint2
            int r4 = r0.currentIcon
            if (r4 != r2) goto L_0x09df
            r2 = 255(0xff, float:3.57E-43)
            goto L_0x09e4
        L_0x09df:
            float r2 = r0.transitionProgress
            float r2 = r2 * r20
            int r2 = (int) r2
        L_0x09e4:
            r3.setAlpha(r2)
            goto L_0x09d2
        L_0x09e8:
            r34 = r12
            r4 = 0
        L_0x09eb:
            r5 = 5
            if (r4 >= r5) goto L_0x0ab3
            if (r4 != 0) goto L_0x0a50
            android.graphics.Path r10 = r0.path1
            int r12 = r4 * 2
            r13 = r2[r12]
            r14 = r9[r12]
            r18 = r2[r12]
            float r14 = r14 - r18
            float r14 = r14 * r1
            float r13 = r13 + r14
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r13 = (float) r13
            float r14 = r0.scale
            float r13 = r13 * r14
            int r14 = r12 + 1
            r18 = r2[r14]
            r35 = r9[r14]
            r36 = r2[r14]
            float r35 = r35 - r36
            float r35 = r35 * r1
            float r18 = r18 + r35
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r5 = (float) r5
            r18 = r11
            float r11 = r0.scale
            float r5 = r5 * r11
            r10.moveTo(r13, r5)
            android.graphics.Path r5 = r0.path2
            r10 = r3[r12]
            r11 = r22[r12]
            r12 = r3[r12]
            float r11 = r11 - r12
            float r11 = r11 * r1
            float r10 = r10 + r11
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r10 = (float) r10
            float r11 = r0.scale
            float r10 = r10 * r11
            r11 = r3[r14]
            r12 = r22[r14]
            r13 = r3[r14]
            float r12 = r12 - r13
            float r12 = r12 * r1
            float r11 = r11 + r12
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r11 = (float) r11
            float r12 = r0.scale
            float r11 = r11 * r12
            r5.moveTo(r10, r11)
            goto L_0x0aad
        L_0x0a50:
            r18 = r11
            android.graphics.Path r5 = r0.path1
            int r10 = r4 * 2
            r11 = r2[r10]
            r12 = r9[r10]
            r13 = r2[r10]
            float r12 = r12 - r13
            float r12 = r12 * r1
            float r11 = r11 + r12
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r11 = (float) r11
            float r12 = r0.scale
            float r11 = r11 * r12
            int r12 = r10 + 1
            r13 = r2[r12]
            r14 = r9[r12]
            r36 = r2[r12]
            float r14 = r14 - r36
            float r14 = r14 * r1
            float r13 = r13 + r14
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r13 = (float) r13
            float r14 = r0.scale
            float r13 = r13 * r14
            r5.lineTo(r11, r13)
            android.graphics.Path r5 = r0.path2
            r11 = r3[r10]
            r13 = r22[r10]
            r10 = r3[r10]
            float r13 = r13 - r10
            float r13 = r13 * r1
            float r11 = r11 + r13
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r10 = (float) r10
            float r11 = r0.scale
            float r10 = r10 * r11
            r11 = r3[r12]
            r13 = r22[r12]
            r12 = r3[r12]
            float r13 = r13 - r12
            float r13 = r13 * r1
            float r11 = r11 + r13
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r11 = (float) r11
            float r12 = r0.scale
            float r11 = r11 * r12
            r5.lineTo(r10, r11)
        L_0x0aad:
            int r4 = r4 + 1
            r11 = r18
            goto L_0x09eb
        L_0x0ab3:
            r18 = r11
            android.graphics.Paint r2 = r0.paint2
            r9 = 255(0xff, float:3.57E-43)
            r2.setAlpha(r9)
        L_0x0abc:
            android.graphics.Path r2 = r0.path1
            r2.close()
            android.graphics.Path r2 = r0.path2
            r2.close()
            r39.save()
            int r2 = r8.left
            float r2 = (float) r2
            int r3 = r8.top
            float r3 = (float) r3
            r7.translate(r2, r3)
            float r2 = (float) r6
            int r15 = r15 - r6
            float r3 = (float) r15
            float r3 = r3 * r1
            float r2 = r2 + r3
            int r1 = r8.left
            int r11 = r18 - r1
            float r1 = (float) r11
            int r3 = r8.top
            int r12 = r34 - r3
            float r3 = (float) r12
            r7.rotate(r2, r1, r3)
            int r1 = r0.currentIcon
            if (r1 == 0) goto L_0x0aec
            r2 = 1
            if (r1 != r2) goto L_0x0af1
        L_0x0aec:
            int r1 = r0.currentIcon
            r2 = 4
            if (r1 != r2) goto L_0x0b01
        L_0x0af1:
            int r1 = r8.left
            int r11 = r18 - r1
            float r1 = (float) r11
            int r2 = r8.top
            int r12 = r34 - r2
            float r2 = (float) r12
            r8 = r29
            r7.scale(r8, r8, r1, r2)
            goto L_0x0b03
        L_0x0b01:
            r8 = r29
        L_0x0b03:
            android.graphics.Path r1 = r0.path1
            android.graphics.Paint r2 = r0.paint2
            r7.drawPath(r1, r2)
            android.graphics.Path r1 = r0.path2
            android.graphics.Paint r2 = r0.paint2
            r7.drawPath(r1, r2)
            r39.restore()
        L_0x0b14:
            int r1 = r0.currentIcon
            r2 = 6
            if (r1 == r2) goto L_0x0b1d
            int r1 = r0.nextIcon
            if (r1 != r2) goto L_0x0bad
        L_0x0b1d:
            int r1 = r0.currentIcon
            if (r1 == r2) goto L_0x0b45
            float r1 = r0.transitionProgress
            int r2 = (r1 > r21 ? 1 : (r1 == r21 ? 0 : -1))
            if (r2 <= 0) goto L_0x0b41
            float r1 = r1 - r21
            float r1 = r1 / r21
            float r2 = r1 / r21
            r3 = 1065353216(0x3var_, float:1.0)
            float r2 = java.lang.Math.min(r3, r2)
            float r4 = r3 - r2
            int r2 = (r1 > r21 ? 1 : (r1 == r21 ? 0 : -1))
            if (r2 <= 0) goto L_0x0b3e
            float r1 = r1 - r21
            float r1 = r1 / r21
            goto L_0x0b3f
        L_0x0b3e:
            r1 = 0
        L_0x0b3f:
            r10 = r1
            goto L_0x0b48
        L_0x0b41:
            r4 = 1065353216(0x3var_, float:1.0)
            r10 = 0
            goto L_0x0b48
        L_0x0b45:
            r4 = 0
            r10 = 1065353216(0x3var_, float:1.0)
        L_0x0b48:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r12 = r34 + r1
            r1 = 1077936128(0x40400000, float:3.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r11 = r18 - r1
            android.graphics.Paint r1 = r0.paint
            r1.setAlpha(r9)
            r1 = 1065353216(0x3var_, float:1.0)
            int r2 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r2 >= 0) goto L_0x0b8d
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r1 = r11 - r1
            float r2 = (float) r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r1 = r12 - r1
            float r3 = (float) r1
            float r1 = (float) r11
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r5 = (float) r5
            float r5 = r5 * r4
            float r5 = r1 - r5
            float r1 = (float) r12
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r6 = (float) r6
            float r6 = r6 * r4
            float r6 = r1 - r6
            android.graphics.Paint r13 = r0.paint
            r1 = r39
            r4 = r5
            r5 = r6
            r6 = r13
            r1.drawLine(r2, r3, r4, r5, r6)
        L_0x0b8d:
            r1 = 0
            int r2 = (r10 > r1 ? 1 : (r10 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x0bad
            float r2 = (float) r11
            float r3 = (float) r12
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r1 = (float) r1
            float r1 = r1 * r10
            float r4 = r2 + r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r1 = (float) r1
            float r1 = r1 * r10
            float r5 = r3 - r1
            android.graphics.Paint r6 = r0.paint
            r1 = r39
            r1.drawLine(r2, r3, r4, r5, r6)
        L_0x0bad:
            r1 = r27
            if (r28 == 0) goto L_0x0bf0
            r2 = r28
            if (r2 == r1) goto L_0x0bf0
            int r3 = r2.getIntrinsicWidth()
            float r3 = (float) r3
            float r3 = r3 * r32
            int r3 = (int) r3
            int r4 = r2.getIntrinsicHeight()
            float r4 = (float) r4
            float r4 = r4 * r32
            int r4 = (int) r4
            android.graphics.ColorFilter r5 = r0.colorFilter
            r2.setColorFilter(r5)
            int r5 = r0.currentIcon
            int r6 = r0.nextIcon
            if (r5 != r6) goto L_0x0bd3
            r5 = 255(0xff, float:3.57E-43)
            goto L_0x0bdc
        L_0x0bd3:
            float r5 = r0.transitionProgress
            r6 = 1065353216(0x3var_, float:1.0)
            float r5 = r6 - r5
            float r5 = r5 * r20
            int r5 = (int) r5
        L_0x0bdc:
            r2.setAlpha(r5)
            r5 = 2
            int r3 = r3 / r5
            int r11 = r18 - r3
            int r4 = r4 / r5
            int r12 = r34 - r4
            int r3 = r18 + r3
            int r4 = r34 + r4
            r2.setBounds(r11, r12, r3, r4)
            r2.draw(r7)
        L_0x0bf0:
            if (r1 == 0) goto L_0x0CLASSNAME
            int r2 = r1.getIntrinsicWidth()
            float r2 = (float) r2
            float r2 = r2 * r8
            int r2 = (int) r2
            int r3 = r1.getIntrinsicHeight()
            float r3 = (float) r3
            float r3 = r3 * r8
            int r3 = (int) r3
            android.graphics.ColorFilter r4 = r0.colorFilter
            r1.setColorFilter(r4)
            int r4 = r0.currentIcon
            int r5 = r0.nextIcon
            if (r4 != r5) goto L_0x0CLASSNAME
            r4 = 255(0xff, float:3.57E-43)
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            float r4 = r0.transitionProgress
            float r4 = r4 * r20
            int r4 = (int) r4
        L_0x0CLASSNAME:
            r1.setAlpha(r4)
            r4 = 2
            int r2 = r2 / r4
            int r11 = r18 - r2
            int r3 = r3 / r4
            int r12 = r34 - r3
            int r2 = r18 + r2
            int r3 = r34 + r3
            r1.setBounds(r11, r12, r2, r3)
            r1.draw(r7)
        L_0x0CLASSNAME:
            r1 = r33
            r2 = r25
            if (r1 == 0) goto L_0x0CLASSNAME
            if (r1 == r2) goto L_0x0CLASSNAME
            r3 = 1103101952(0x41CLASSNAME, float:24.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            float r3 = r3 * r32
            int r3 = (int) r3
            r4 = 1103101952(0x41CLASSNAME, float:24.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            float r4 = r4 * r32
            int r4 = (int) r4
            android.graphics.Paint r5 = r0.paint2
            android.graphics.Paint$Style r6 = android.graphics.Paint.Style.FILL_AND_STROKE
            r5.setStyle(r6)
            android.graphics.Paint r5 = r0.paint2
            int r6 = r0.currentIcon
            int r10 = r0.nextIcon
            if (r6 != r10) goto L_0x0CLASSNAME
            r6 = 255(0xff, float:3.57E-43)
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            float r6 = r0.transitionProgress
            r10 = 1065353216(0x3var_, float:1.0)
            float r6 = r10 - r6
            float r6 = r6 * r20
            int r6 = (int) r6
        L_0x0CLASSNAME:
            r5.setAlpha(r6)
            r39.save()
            r5 = 2
            int r3 = r3 / r5
            int r11 = r18 - r3
            float r3 = (float) r11
            int r4 = r4 / r5
            int r12 = r34 - r4
            float r4 = (float) r12
            r7.translate(r3, r4)
            r3 = 0
            r4 = r1[r3]
            android.graphics.Paint r3 = r0.paint2
            r7.drawPath(r4, r3)
            r3 = 1
            r4 = r1[r3]
            if (r4 == 0) goto L_0x0CLASSNAME
            r1 = r1[r3]
            android.graphics.Paint r3 = r0.backPaint
            r7.drawPath(r1, r3)
        L_0x0CLASSNAME:
            r39.restore()
        L_0x0CLASSNAME:
            if (r2 == 0) goto L_0x0cdf
            r1 = 1103101952(0x41CLASSNAME, float:24.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            float r1 = r1 * r8
            int r1 = (int) r1
            r3 = 1103101952(0x41CLASSNAME, float:24.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            float r3 = r3 * r8
            int r3 = (int) r3
            android.graphics.Paint r4 = r0.paint2
            android.graphics.Paint$Style r5 = android.graphics.Paint.Style.FILL_AND_STROKE
            r4.setStyle(r5)
            android.graphics.Paint r4 = r0.paint2
            int r5 = r0.currentIcon
            int r6 = r0.nextIcon
            if (r5 != r6) goto L_0x0cb1
            r5 = 255(0xff, float:3.57E-43)
            goto L_0x0cb6
        L_0x0cb1:
            float r5 = r0.transitionProgress
            float r5 = r5 * r20
            int r5 = (int) r5
        L_0x0cb6:
            r4.setAlpha(r5)
            r39.save()
            r4 = 2
            int r1 = r1 / r4
            int r11 = r18 - r1
            float r1 = (float) r11
            int r3 = r3 / r4
            int r12 = r34 - r3
            float r3 = (float) r12
            r7.translate(r1, r3)
            r1 = 0
            r3 = r2[r1]
            android.graphics.Paint r1 = r0.paint2
            r7.drawPath(r3, r1)
            r1 = 1
            r3 = r2[r1]
            if (r3 == 0) goto L_0x0cdc
            r2 = r2[r1]
            android.graphics.Paint r1 = r0.backPaint
            r7.drawPath(r2, r1)
        L_0x0cdc:
            r39.restore()
        L_0x0cdf:
            long r1 = java.lang.System.currentTimeMillis()
            long r3 = r0.lastAnimationTime
            long r3 = r1 - r3
            r5 = 17
            int r8 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r8 <= 0) goto L_0x0cef
            r3 = 17
        L_0x0cef:
            r0.lastAnimationTime = r1
            int r1 = r0.currentIcon
            r2 = 3
            if (r1 == r2) goto L_0x0d0b
            r2 = 14
            if (r1 == r2) goto L_0x0d0b
            r5 = 4
            if (r1 != r5) goto L_0x0d01
            int r1 = r0.nextIcon
            if (r1 == r2) goto L_0x0d0b
        L_0x0d01:
            int r1 = r0.currentIcon
            r2 = 10
            if (r1 == r2) goto L_0x0d0b
            r2 = 13
            if (r1 != r2) goto L_0x0d53
        L_0x0d0b:
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
            if (r1 == r2) goto L_0x0d50
            float r1 = r0.downloadProgress
            float r2 = r0.downloadProgressAnimationStart
            float r5 = r1 - r2
            r6 = 0
            int r8 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
            if (r8 <= 0) goto L_0x0d50
            float r8 = r0.downloadProgressTime
            float r9 = (float) r3
            float r8 = r8 + r9
            r0.downloadProgressTime = r8
            r9 = 1128792064(0x43480000, float:200.0)
            int r9 = (r8 > r9 ? 1 : (r8 == r9 ? 0 : -1))
            if (r9 < 0) goto L_0x0d42
            r0.animatedDownloadProgress = r1
            r0.downloadProgressAnimationStart = r1
            r0.downloadProgressTime = r6
            goto L_0x0d50
        L_0x0d42:
            android.view.animation.DecelerateInterpolator r1 = r0.interpolator
            r6 = 1128792064(0x43480000, float:200.0)
            float r8 = r8 / r6
            float r1 = r1.getInterpolation(r8)
            float r5 = r5 * r1
            float r2 = r2 + r5
            r0.animatedDownloadProgress = r2
        L_0x0d50:
            r38.invalidateSelf()
        L_0x0d53:
            boolean r1 = r0.animatingTransition
            if (r1 == 0) goto L_0x0d76
            float r1 = r0.transitionProgress
            r2 = 1065353216(0x3var_, float:1.0)
            int r5 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r5 >= 0) goto L_0x0d76
            float r3 = (float) r3
            float r4 = r0.transitionAnimationTime
            float r3 = r3 / r4
            float r1 = r1 + r3
            r0.transitionProgress = r1
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 < 0) goto L_0x0d73
            int r1 = r0.nextIcon
            r0.currentIcon = r1
            r0.transitionProgress = r2
            r1 = 0
            r0.animatingTransition = r1
        L_0x0d73:
            r38.invalidateSelf()
        L_0x0d76:
            r1 = r31
            r2 = 1
            if (r1 < r2) goto L_0x0d7e
            r7.restoreToCount(r1)
        L_0x0d7e:
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
