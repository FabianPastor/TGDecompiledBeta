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
    private static final float[] pausePath1 = {16.0f, 17.0f, 32.0f, 17.0f, 32.0f, 22.0f, 16.0f, 22.0f, 16.0f, 19.5f};
    private static final float[] pausePath2 = {16.0f, 31.0f, 32.0f, 31.0f, 32.0f, 26.0f, 16.0f, 26.0f, 16.0f, 28.5f};
    private static final int pauseRotation = 90;
    private static final float[] playFinalPath = {18.0f, 15.0f, 34.0f, 24.0f, 18.0f, 33.0f};
    private static final float[] playPath1 = {18.0f, 15.0f, 34.0f, 24.0f, 34.0f, 24.0f, 18.0f, 24.0f, 18.0f, 24.0f};
    private static final float[] playPath2 = {18.0f, 33.0f, 34.0f, 24.0f, 34.0f, 24.0f, 18.0f, 24.0f, 18.0f, 24.0f};
    private static final int playRotation = 0;
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

    public int getColor() {
        return this.paint.getColor();
    }

    public void setMini(boolean z) {
        this.isMini = z;
        this.paint.setStrokeWidth((float) AndroidUtilities.dp(this.isMini ? 2.0f : 3.0f));
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

    public float getProgressAlpha() {
        return 1.0f - this.transitionProgress;
    }

    public float getTransitionProgress() {
        if (this.animatingTransition) {
            return this.transitionProgress;
        }
        return 1.0f;
    }

    public void setBackgroundDrawable(Drawable drawable) {
        if (drawable instanceof Theme.MessageDrawable) {
            this.messageDrawable = (Theme.MessageDrawable) drawable;
        }
    }

    public void setHasOverlayImage(boolean z) {
        this.hasOverlayImage = z;
    }

    public void setBounds(int i, int i2, int i3, int i4) {
        super.setBounds(i, i2, i3, i4);
        this.scale = ((float) (i3 - i)) / ((float) getIntrinsicWidth());
        if (this.scale < 0.7f) {
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
        jadx.core.utils.exceptions.JadxRuntimeException: Don't wrap MOVE or CONST insns: 0x0bc3: MOVE  (r2v31 android.graphics.drawable.Drawable) = (r23v0 android.graphics.drawable.Drawable)
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
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseIterativeStepInternal(DepthRegionTraversal.java:78)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseIterativeStepInternal(DepthRegionTraversal.java:78)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseIterative(DepthRegionTraversal.java:27)
        	at jadx.core.dex.visitors.regions.IfRegionVisitor.visit(IfRegionVisitor.java:31)
        */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x03ea  */
    /* JADX WARNING: Removed duplicated region for block: B:130:0x0412  */
    /* JADX WARNING: Removed duplicated region for block: B:169:0x04ee  */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x04f9  */
    /* JADX WARNING: Removed duplicated region for block: B:174:0x0503  */
    /* JADX WARNING: Removed duplicated region for block: B:180:0x0577  */
    /* JADX WARNING: Removed duplicated region for block: B:183:0x057d  */
    /* JADX WARNING: Removed duplicated region for block: B:195:0x05a3  */
    /* JADX WARNING: Removed duplicated region for block: B:208:0x0601  */
    /* JADX WARNING: Removed duplicated region for block: B:211:0x060a  */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x060f  */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x0635  */
    /* JADX WARNING: Removed duplicated region for block: B:220:0x063a  */
    /* JADX WARNING: Removed duplicated region for block: B:227:0x064a  */
    /* JADX WARNING: Removed duplicated region for block: B:228:0x064d  */
    /* JADX WARNING: Removed duplicated region for block: B:235:0x065f  */
    /* JADX WARNING: Removed duplicated region for block: B:236:0x0662  */
    /* JADX WARNING: Removed duplicated region for block: B:243:0x0679  */
    /* JADX WARNING: Removed duplicated region for block: B:244:0x0683  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x071a  */
    /* JADX WARNING: Removed duplicated region for block: B:261:0x071d  */
    /* JADX WARNING: Removed duplicated region for block: B:267:0x0732  */
    /* JADX WARNING: Removed duplicated region for block: B:268:0x0735  */
    /* JADX WARNING: Removed duplicated region for block: B:271:0x0749  */
    /* JADX WARNING: Removed duplicated region for block: B:274:0x077f  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x00a4  */
    /* JADX WARNING: Removed duplicated region for block: B:281:0x0794  */
    /* JADX WARNING: Removed duplicated region for block: B:282:0x0797  */
    /* JADX WARNING: Removed duplicated region for block: B:288:0x07bf  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x00ad  */
    /* JADX WARNING: Removed duplicated region for block: B:296:0x080b  */
    /* JADX WARNING: Removed duplicated region for block: B:299:0x0813 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:314:0x083c  */
    /* JADX WARNING: Removed duplicated region for block: B:315:0x0845  */
    /* JADX WARNING: Removed duplicated region for block: B:318:0x0854  */
    /* JADX WARNING: Removed duplicated region for block: B:323:0x0864  */
    /* JADX WARNING: Removed duplicated region for block: B:326:0x086f  */
    /* JADX WARNING: Removed duplicated region for block: B:331:0x087d  */
    /* JADX WARNING: Removed duplicated region for block: B:333:0x0884  */
    /* JADX WARNING: Removed duplicated region for block: B:334:0x088b  */
    /* JADX WARNING: Removed duplicated region for block: B:337:0x0891 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:349:0x0934  */
    /* JADX WARNING: Removed duplicated region for block: B:365:0x09e6  */
    /* JADX WARNING: Removed duplicated region for block: B:379:0x0af8  */
    /* JADX WARNING: Removed duplicated region for block: B:380:0x0b08  */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x0364 A[ADDED_TO_REGION] */
    public void draw(android.graphics.Canvas r33) {
        /*
            r32 = this;
            r0 = r32
            r7 = r33
            android.graphics.Rect r8 = r32.getBounds()
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
            r13 = 3
            r14 = 4
            r6 = 14
            r5 = 1065353216(0x3var_, float:1.0)
            if (r1 != r14) goto L_0x0072
            int r1 = r0.currentIcon
            if (r1 == r13) goto L_0x008a
            if (r1 == r6) goto L_0x008a
            int r1 = r33.save()
            float r2 = r0.transitionProgress
            float r2 = r5 - r2
            float r3 = (float) r11
            float r4 = (float) r12
            r7.scale(r2, r2, r3, r4)
            goto L_0x0088
        L_0x0072:
            r2 = 6
            if (r1 == r2) goto L_0x0079
            r2 = 10
            if (r1 != r2) goto L_0x008a
        L_0x0079:
            int r1 = r0.currentIcon
            if (r1 != r14) goto L_0x008a
            int r1 = r33.save()
            float r2 = r0.transitionProgress
            float r3 = (float) r11
            float r4 = (float) r12
            r7.scale(r2, r2, r3, r4)
        L_0x0088:
            r4 = r1
            goto L_0x008b
        L_0x008a:
            r4 = 0
        L_0x008b:
            r1 = 1077936128(0x40400000, float:3.0)
            org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = r0.currentIcon
            r16 = 1080033280(0x40600000, float:3.5)
            r17 = 1073741824(0x40000000, float:2.0)
            r18 = 1088421888(0x40e00000, float:7.0)
            r19 = 1132396544(0x437var_, float:255.0)
            r20 = 1056964608(0x3var_, float:0.5)
            r2 = 2
            if (r1 == r2) goto L_0x00ad
            int r1 = r0.nextIcon
            if (r1 != r2) goto L_0x00a4
            goto L_0x00ad
        L_0x00a4:
            r27 = r4
            r28 = r8
            r8 = 14
            r10 = 2
            goto L_0x035e
        L_0x00ad:
            float r1 = (float) r12
            r21 = 1091567616(0x41100000, float:9.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r9 = (float) r9
            float r15 = r0.scale
            float r9 = r9 * r15
            float r9 = r1 - r9
            r15 = 1091567616(0x41100000, float:9.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            float r15 = (float) r15
            float r14 = r0.scale
            float r15 = r15 * r14
            float r15 = r15 + r1
            r14 = 1094713344(0x41400000, float:12.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r14 = (float) r14
            float r10 = r0.scale
            float r14 = r14 * r10
            float r14 = r14 + r1
            int r10 = r0.currentIcon
            if (r10 == r13) goto L_0x00d9
            if (r10 != r6) goto L_0x0100
        L_0x00d9:
            int r10 = r0.nextIcon
            if (r10 != r2) goto L_0x0100
            android.graphics.Paint r10 = r0.paint
            float r3 = r0.transitionProgress
            float r3 = r3 / r20
            float r3 = java.lang.Math.min(r5, r3)
            float r3 = r3 * r19
            int r3 = (int) r3
            r10.setAlpha(r3)
            float r3 = r0.transitionProgress
            r10 = 1094713344(0x41400000, float:12.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r10 = (float) r10
            float r5 = r0.scale
            float r10 = r10 * r5
            float r10 = r10 + r1
            r24 = r10
            r10 = 1065353216(0x3var_, float:1.0)
            goto L_0x013d
        L_0x0100:
            int r3 = r0.nextIcon
            if (r3 == r13) goto L_0x0125
            if (r3 == r6) goto L_0x0125
            if (r3 == r2) goto L_0x0125
            android.graphics.Paint r3 = r0.paint
            float r5 = r0.savedTransitionProgress
            float r5 = r5 / r20
            r10 = 1065353216(0x3var_, float:1.0)
            float r5 = java.lang.Math.min(r10, r5)
            float r5 = r5 * r19
            float r6 = r0.transitionProgress
            float r6 = r10 - r6
            float r5 = r5 * r6
            int r5 = (int) r5
            r3.setAlpha(r5)
            float r3 = r0.savedTransitionProgress
            r5 = 255(0xff, float:3.57E-43)
            goto L_0x012e
        L_0x0125:
            android.graphics.Paint r3 = r0.paint
            r5 = 255(0xff, float:3.57E-43)
            r3.setAlpha(r5)
            float r3 = r0.transitionProgress
        L_0x012e:
            r10 = 1065353216(0x3var_, float:1.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r6 = (float) r6
            float r5 = r0.scale
            float r6 = r6 * r5
            float r5 = r1 + r6
            r24 = r5
        L_0x013d:
            boolean r5 = r0.animatingTransition
            if (r5 == 0) goto L_0x0301
            int r5 = r0.nextIcon
            if (r5 == r2) goto L_0x02ae
            int r5 = (r3 > r20 ? 1 : (r3 == r20 ? 0 : -1))
            if (r5 > 0) goto L_0x014b
            goto L_0x02ae
        L_0x014b:
            r5 = 1095761920(0x41500000, float:13.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            float r6 = r0.scale
            float r5 = r5 * r6
            float r3 = r3 - r20
            float r6 = r3 / r20
            r9 = 1045220557(0x3e4ccccd, float:0.2)
            int r9 = (r3 > r9 ? 1 : (r3 == r9 ? 0 : -1))
            if (r9 <= 0) goto L_0x016d
            r9 = 1045220557(0x3e4ccccd, float:0.2)
            float r3 = r3 - r9
            r9 = 1050253722(0x3e99999a, float:0.3)
            float r3 = r3 / r9
            r9 = r3
            r15 = 1065353216(0x3var_, float:1.0)
            goto L_0x0173
        L_0x016d:
            r9 = 1045220557(0x3e4ccccd, float:0.2)
            float r3 = r3 / r9
            r15 = r3
            r9 = 0
        L_0x0173:
            android.graphics.RectF r3 = r0.rect
            float r10 = (float) r11
            float r2 = r10 - r5
            float r5 = r5 / r17
            float r13 = r14 - r5
            float r5 = r5 + r14
            r3.set(r2, r13, r10, r5)
            r2 = 1120403456(0x42CLASSNAME, float:100.0)
            float r3 = r9 * r2
            android.graphics.RectF r2 = r0.rect
            r5 = 1120927744(0x42d00000, float:104.0)
            float r6 = r6 * r5
            float r5 = r6 - r3
            r6 = 0
            android.graphics.Paint r13 = r0.paint
            r26 = r1
            r1 = r33
            r27 = r4
            r4 = r5
            r23 = r8
            r8 = 1065353216(0x3var_, float:1.0)
            r5 = r6
            r8 = 14
            r6 = r13
            r1.drawArc(r2, r3, r4, r5, r6)
            float r1 = r14 - r24
            float r1 = r1 * r15
            float r24 = r24 + r1
            r1 = 0
            int r2 = (r9 > r1 ? 1 : (r9 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x02a7
            int r1 = r0.nextIcon
            if (r1 != r8) goto L_0x01b2
            r13 = 0
            goto L_0x01bb
        L_0x01b2:
            r1 = -1036779520(0xffffffffCLASSNAME, float:-45.0)
            r2 = 1065353216(0x3var_, float:1.0)
            float r5 = r2 - r9
            float r1 = r1 * r5
            r13 = r1
        L_0x01bb:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r1 = (float) r1
            float r1 = r1 * r9
            float r2 = r0.scale
            float r1 = r1 * r2
            float r9 = r9 * r19
            int r2 = (int) r9
            int r3 = r0.nextIcon
            r4 = 3
            if (r3 == r4) goto L_0x01e4
            if (r3 == r8) goto L_0x01e4
            r9 = 2
            if (r3 == r9) goto L_0x01e5
            float r3 = r0.transitionProgress
            float r3 = r3 / r20
            r4 = 1065353216(0x3var_, float:1.0)
            float r3 = java.lang.Math.min(r4, r3)
            float r5 = r4 - r3
            float r2 = (float) r2
            float r2 = r2 * r5
            int r2 = (int) r2
            goto L_0x01e5
        L_0x01e4:
            r9 = 2
        L_0x01e5:
            r15 = r2
            r2 = 0
            int r3 = (r13 > r2 ? 1 : (r13 == r2 ? 0 : -1))
            if (r3 == 0) goto L_0x01f4
            r33.save()
            r2 = r26
            r7.rotate(r13, r10, r2)
            goto L_0x01f6
        L_0x01f4:
            r2 = r26
        L_0x01f6:
            if (r15 == 0) goto L_0x029c
            android.graphics.Paint r3 = r0.paint
            r3.setAlpha(r15)
            int r3 = r0.nextIcon
            if (r3 != r8) goto L_0x027c
            android.graphics.Paint r1 = r0.paint3
            r1.setAlpha(r15)
            android.graphics.RectF r1 = r0.rect
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r2 = r11 - r2
            float r2 = (float) r2
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r3 = r12 - r3
            float r3 = (float) r3
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r4 = r4 + r11
            float r4 = (float) r4
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r5 = r5 + r12
            float r5 = (float) r5
            r1.set(r2, r3, r4, r5)
            android.graphics.RectF r1 = r0.rect
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r2 = (float) r2
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r3 = (float) r3
            android.graphics.Paint r4 = r0.paint3
            r7.drawRoundRect(r1, r2, r3, r4)
            android.graphics.Paint r1 = r0.paint
            float r2 = (float) r15
            r3 = 1041865114(0x3e19999a, float:0.15)
            float r2 = r2 * r3
            int r2 = (int) r2
            r1.setAlpha(r2)
            boolean r1 = r0.isMini
            if (r1 == 0) goto L_0x0249
            r1 = 1073741824(0x40000000, float:2.0)
            goto L_0x024b
        L_0x0249:
            r1 = 1082130432(0x40800000, float:4.0)
        L_0x024b:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            android.graphics.RectF r2 = r0.rect
            r6 = r23
            int r3 = r6.left
            int r3 = r3 + r1
            float r3 = (float) r3
            int r4 = r6.top
            int r4 = r4 + r1
            float r4 = (float) r4
            int r5 = r6.right
            int r5 = r5 - r1
            float r5 = (float) r5
            int r9 = r6.bottom
            int r9 = r9 - r1
            float r1 = (float) r9
            r2.set(r3, r4, r5, r1)
            android.graphics.RectF r2 = r0.rect
            r3 = 0
            r4 = 1135869952(0x43b40000, float:360.0)
            r5 = 0
            android.graphics.Paint r9 = r0.paint
            r1 = r33
            r28 = r6
            r6 = r9
            r1.drawArc(r2, r3, r4, r5, r6)
            android.graphics.Paint r1 = r0.paint
            r1.setAlpha(r15)
            goto L_0x029e
        L_0x027c:
            r28 = r23
            float r9 = r10 - r1
            float r15 = r2 - r1
            float r23 = r10 + r1
            float r25 = r2 + r1
            android.graphics.Paint r6 = r0.paint
            r1 = r33
            r2 = r9
            r3 = r15
            r4 = r23
            r5 = r25
            r1.drawLine(r2, r3, r4, r5, r6)
            android.graphics.Paint r6 = r0.paint
            r2 = r23
            r4 = r9
            r1.drawLine(r2, r3, r4, r5, r6)
            goto L_0x029e
        L_0x029c:
            r28 = r23
        L_0x029e:
            r1 = 0
            int r2 = (r13 > r1 ? 1 : (r13 == r1 ? 0 : -1))
            if (r2 == 0) goto L_0x02a9
            r33.restore()
            goto L_0x02a9
        L_0x02a7:
            r28 = r23
        L_0x02a9:
            r1 = r10
            r2 = r1
            r3 = r14
            r10 = 2
            goto L_0x02fb
        L_0x02ae:
            r27 = r4
            r28 = r8
            r8 = 14
            r10 = 2
            int r1 = r0.nextIcon
            if (r1 != r10) goto L_0x02be
            r1 = 1065353216(0x3var_, float:1.0)
            float r5 = r1 - r3
            goto L_0x02c4
        L_0x02be:
            r1 = 1065353216(0x3var_, float:1.0)
            float r5 = r3 / r20
            float r3 = r1 - r5
        L_0x02c4:
            float r24 = r24 - r9
            float r24 = r24 * r5
            float r24 = r9 + r24
            float r14 = r14 - r15
            float r14 = r14 * r5
            float r14 = r14 + r15
            float r1 = (float) r11
            r2 = 1090519040(0x41000000, float:8.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            float r2 = r2 * r3
            float r4 = r0.scale
            float r2 = r2 * r4
            float r2 = r1 - r2
            r4 = 1090519040(0x41000000, float:8.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            float r4 = r4 * r3
            float r5 = r0.scale
            float r4 = r4 * r5
            float r1 = r1 + r4
            r4 = 1090519040(0x41000000, float:8.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            float r4 = r4 * r3
            float r3 = r0.scale
            float r4 = r4 * r3
            float r3 = r14 - r4
        L_0x02fb:
            r15 = r1
            r9 = r2
            r13 = r3
            r3 = r24
            goto L_0x0334
        L_0x0301:
            r27 = r4
            r28 = r8
            r8 = 14
            r10 = 2
            float r1 = (float) r11
            r2 = 1090519040(0x41000000, float:8.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            float r3 = r0.scale
            float r2 = r2 * r3
            float r2 = r1 - r2
            r3 = 1090519040(0x41000000, float:8.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            float r4 = r0.scale
            float r3 = r3 * r4
            float r1 = r1 + r3
            r3 = 1090519040(0x41000000, float:8.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            float r4 = r0.scale
            float r3 = r3 * r4
            float r3 = r15 - r3
            r13 = r3
            r3 = r9
            r14 = r15
            r15 = r1
            r9 = r2
        L_0x0334:
            int r1 = (r3 > r14 ? 1 : (r3 == r14 ? 0 : -1))
            if (r1 == 0) goto L_0x0342
            float r4 = (float) r11
            android.graphics.Paint r6 = r0.paint
            r1 = r33
            r2 = r4
            r5 = r14
            r1.drawLine(r2, r3, r4, r5, r6)
        L_0x0342:
            float r6 = (float) r11
            int r1 = (r9 > r6 ? 1 : (r9 == r6 ? 0 : -1))
            if (r1 == 0) goto L_0x035e
            android.graphics.Paint r5 = r0.paint
            r1 = r33
            r2 = r9
            r3 = r13
            r4 = r6
            r9 = r5
            r5 = r14
            r23 = r6
            r6 = r9
            r1.drawLine(r2, r3, r4, r5, r6)
            android.graphics.Paint r6 = r0.paint
            r2 = r15
            r4 = r23
            r1.drawLine(r2, r3, r4, r5, r6)
        L_0x035e:
            int r1 = r0.currentIcon
            r9 = 1
            r2 = 3
            if (r1 == r2) goto L_0x03e4
            if (r1 == r8) goto L_0x03e4
            r3 = 4
            if (r1 != r3) goto L_0x0371
            int r1 = r0.nextIcon
            if (r1 == r8) goto L_0x03e4
            if (r1 != r2) goto L_0x0371
            goto L_0x03e4
        L_0x0371:
            int r1 = r0.currentIcon
            r2 = 10
            if (r1 == r2) goto L_0x0386
            int r2 = r0.nextIcon
            r3 = 10
            if (r2 == r3) goto L_0x0386
            r2 = 13
            if (r1 != r2) goto L_0x0382
            goto L_0x0386
        L_0x0382:
            r13 = r28
            goto L_0x0604
        L_0x0386:
            int r1 = r0.nextIcon
            r2 = 4
            if (r1 == r2) goto L_0x0392
            r2 = 6
            if (r1 != r2) goto L_0x038f
            goto L_0x0392
        L_0x038f:
            r3 = 255(0xff, float:3.57E-43)
            goto L_0x039b
        L_0x0392:
            float r1 = r0.transitionProgress
            r2 = 1065353216(0x3var_, float:1.0)
            float r5 = r2 - r1
            float r5 = r5 * r19
            int r3 = (int) r5
        L_0x039b:
            if (r3 == 0) goto L_0x0382
            android.graphics.Paint r1 = r0.paint
            float r2 = (float) r3
            float r3 = r0.overrideAlpha
            float r2 = r2 * r3
            int r2 = (int) r2
            r1.setAlpha(r2)
            r1 = 1082130432(0x40800000, float:4.0)
            r2 = 1135869952(0x43b40000, float:360.0)
            float r3 = r0.animatedDownloadProgress
            float r3 = r3 * r2
            float r4 = java.lang.Math.max(r1, r3)
            boolean r1 = r0.isMini
            if (r1 == 0) goto L_0x03b9
            goto L_0x03bb
        L_0x03b9:
            r17 = 1082130432(0x40800000, float:4.0)
        L_0x03bb:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r17)
            android.graphics.RectF r2 = r0.rect
            r13 = r28
            int r3 = r13.left
            int r3 = r3 + r1
            float r3 = (float) r3
            int r5 = r13.top
            int r5 = r5 + r1
            float r5 = (float) r5
            int r6 = r13.right
            int r6 = r6 - r1
            float r6 = (float) r6
            int r14 = r13.bottom
            int r14 = r14 - r1
            float r1 = (float) r14
            r2.set(r3, r5, r6, r1)
            android.graphics.RectF r2 = r0.rect
            float r3 = r0.downloadRadOffset
            r5 = 0
            android.graphics.Paint r6 = r0.paint
            r1 = r33
            r1.drawArc(r2, r3, r4, r5, r6)
            goto L_0x0604
        L_0x03e4:
            r13 = r28
            int r1 = r0.nextIcon
            if (r1 != r10) goto L_0x0412
            float r1 = r0.transitionProgress
            int r2 = (r1 > r20 ? 1 : (r1 == r20 ? 0 : -1))
            if (r2 > 0) goto L_0x0406
            float r1 = r1 / r20
            r2 = 1065353216(0x3var_, float:1.0)
            float r5 = r2 - r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r1 = (float) r1
            float r1 = r1 * r5
            float r2 = r0.scale
            float r1 = r1 * r2
            float r5 = r5 * r19
            int r15 = (int) r5
            r3 = r15
            goto L_0x0408
        L_0x0406:
            r1 = 0
            r3 = 0
        L_0x0408:
            r5 = r3
            r2 = 1065353216(0x3var_, float:1.0)
            r4 = 0
        L_0x040c:
            r6 = 0
            r14 = 1065353216(0x3var_, float:1.0)
            r15 = 0
            goto L_0x04ea
        L_0x0412:
            if (r1 == 0) goto L_0x04b5
            if (r1 == r9) goto L_0x04b5
            r2 = 5
            if (r1 == r2) goto L_0x04b5
            r2 = 8
            if (r1 == r2) goto L_0x04b5
            r2 = 9
            if (r1 == r2) goto L_0x04b5
            r2 = 7
            if (r1 == r2) goto L_0x04b5
            r2 = 6
            if (r1 != r2) goto L_0x0429
            goto L_0x04b5
        L_0x0429:
            r2 = 4
            if (r1 != r2) goto L_0x0465
            float r1 = r0.transitionProgress
            r2 = 1065353216(0x3var_, float:1.0)
            float r5 = r2 - r1
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r2 = (float) r2
            float r3 = r0.scale
            float r2 = r2 * r3
            float r3 = r5 * r19
            int r3 = (int) r3
            int r4 = r0.currentIcon
            if (r4 != r8) goto L_0x044c
            int r1 = r13.left
            float r1 = (float) r1
            int r4 = r13.top
            float r4 = (float) r4
            r6 = r4
            r4 = r1
            r1 = 0
            goto L_0x045d
        L_0x044c:
            r4 = 1110704128(0x42340000, float:45.0)
            float r1 = r1 * r4
            int r4 = r13.centerX()
            float r4 = (float) r4
            int r5 = r13.centerY()
            float r5 = (float) r5
            r6 = r5
            r5 = 1065353216(0x3var_, float:1.0)
        L_0x045d:
            r15 = r1
            r1 = r2
            r14 = r5
            r2 = 1065353216(0x3var_, float:1.0)
            r5 = r3
            goto L_0x04ea
        L_0x0465:
            if (r1 == r8) goto L_0x047a
            r2 = 3
            if (r1 != r2) goto L_0x046b
            goto L_0x047a
        L_0x046b:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r1 = (float) r1
            float r2 = r0.scale
            float r1 = r1 * r2
            r2 = 1065353216(0x3var_, float:1.0)
            r4 = 0
            r5 = 255(0xff, float:3.57E-43)
            goto L_0x040c
        L_0x047a:
            float r1 = r0.transitionProgress
            r2 = 1065353216(0x3var_, float:1.0)
            float r5 = r2 - r1
            int r2 = r0.currentIcon
            r3 = 4
            if (r2 != r3) goto L_0x0488
            r2 = r1
            r5 = 0
            goto L_0x048e
        L_0x0488:
            r2 = 1110704128(0x42340000, float:45.0)
            float r5 = r5 * r2
            r2 = 1065353216(0x3var_, float:1.0)
        L_0x048e:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r3 = (float) r3
            float r4 = r0.scale
            float r3 = r3 * r4
            float r1 = r1 * r19
            int r1 = (int) r1
            int r4 = r0.nextIcon
            if (r4 != r8) goto L_0x04a4
            int r4 = r13.left
            float r4 = (float) r4
            int r6 = r13.top
            goto L_0x04ad
        L_0x04a4:
            int r4 = r13.centerX()
            float r4 = (float) r4
            int r6 = r13.centerY()
        L_0x04ad:
            float r6 = (float) r6
            r14 = r2
            r15 = r5
            r2 = 1065353216(0x3var_, float:1.0)
            r5 = r1
            r1 = r3
            goto L_0x04ea
        L_0x04b5:
            int r1 = r0.nextIcon
            r2 = 6
            if (r1 != r2) goto L_0x04c5
            float r1 = r0.transitionProgress
            float r1 = r1 / r20
            r2 = 1065353216(0x3var_, float:1.0)
            float r1 = java.lang.Math.min(r2, r1)
            goto L_0x04c9
        L_0x04c5:
            r2 = 1065353216(0x3var_, float:1.0)
            float r1 = r0.transitionProgress
        L_0x04c9:
            float r5 = r2 - r1
            r3 = 1110704128(0x42340000, float:45.0)
            float r1 = r1 * r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r3 = (float) r3
            float r3 = r3 * r5
            float r4 = r0.scale
            float r3 = r3 * r4
            float r5 = r5 * r17
            float r4 = java.lang.Math.min(r2, r5)
            float r4 = r4 * r19
            int r4 = (int) r4
            r15 = r1
            r1 = r3
            r5 = r4
            r4 = 0
            r6 = 0
            r14 = 1065353216(0x3var_, float:1.0)
        L_0x04ea:
            int r3 = (r14 > r2 ? 1 : (r14 == r2 ? 0 : -1))
            if (r3 == 0) goto L_0x04f4
            r33.save()
            r7.scale(r14, r14, r4, r6)
        L_0x04f4:
            r2 = 0
            int r3 = (r15 > r2 ? 1 : (r15 == r2 ? 0 : -1))
            if (r3 == 0) goto L_0x0501
            r33.save()
            float r2 = (float) r11
            float r3 = (float) r12
            r7.rotate(r15, r2, r3)
        L_0x0501:
            if (r5 == 0) goto L_0x0577
            android.graphics.Paint r2 = r0.paint
            float r3 = (float) r5
            float r4 = r0.overrideAlpha
            float r4 = r4 * r3
            int r4 = (int) r4
            r2.setAlpha(r4)
            int r2 = r0.currentIcon
            if (r2 == r8) goto L_0x053b
            int r2 = r0.nextIcon
            if (r2 != r8) goto L_0x0517
            goto L_0x053b
        L_0x0517:
            float r2 = (float) r11
            float r16 = r2 - r1
            float r3 = (float) r12
            float r23 = r3 - r1
            float r24 = r2 + r1
            float r25 = r3 + r1
            android.graphics.Paint r6 = r0.paint
            r1 = r33
            r2 = r16
            r3 = r23
            r4 = r24
            r9 = r5
            r5 = r25
            r1.drawLine(r2, r3, r4, r5, r6)
            android.graphics.Paint r6 = r0.paint
            r2 = r24
            r4 = r16
            r1.drawLine(r2, r3, r4, r5, r6)
            goto L_0x0578
        L_0x053b:
            r9 = r5
            android.graphics.Paint r1 = r0.paint3
            float r2 = r0.overrideAlpha
            float r3 = r3 * r2
            int r2 = (int) r3
            r1.setAlpha(r2)
            android.graphics.RectF r1 = r0.rect
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r2 = r11 - r2
            float r2 = (float) r2
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r3 = r12 - r3
            float r3 = (float) r3
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r4 = r4 + r11
            float r4 = (float) r4
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r5 = r5 + r12
            float r5 = (float) r5
            r1.set(r2, r3, r4, r5)
            android.graphics.RectF r1 = r0.rect
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r2 = (float) r2
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r3 = (float) r3
            android.graphics.Paint r4 = r0.paint3
            r7.drawRoundRect(r1, r2, r3, r4)
            goto L_0x0578
        L_0x0577:
            r9 = r5
        L_0x0578:
            r1 = 0
            int r2 = (r15 > r1 ? 1 : (r15 == r1 ? 0 : -1))
            if (r2 == 0) goto L_0x0580
            r33.restore()
        L_0x0580:
            int r1 = r0.currentIcon
            r2 = 3
            if (r1 == r2) goto L_0x0590
            if (r1 == r8) goto L_0x0590
            r3 = 4
            if (r1 != r3) goto L_0x05fb
            int r1 = r0.nextIcon
            if (r1 == r8) goto L_0x0590
            if (r1 != r2) goto L_0x05fb
        L_0x0590:
            if (r9 == 0) goto L_0x05fb
            r1 = 1082130432(0x40800000, float:4.0)
            r2 = 1135869952(0x43b40000, float:360.0)
            float r3 = r0.animatedDownloadProgress
            float r3 = r3 * r2
            float r15 = java.lang.Math.max(r1, r3)
            boolean r1 = r0.isMini
            if (r1 == 0) goto L_0x05a3
            goto L_0x05a5
        L_0x05a3:
            r17 = 1082130432(0x40800000, float:4.0)
        L_0x05a5:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r17)
            android.graphics.RectF r2 = r0.rect
            int r3 = r13.left
            int r3 = r3 + r1
            float r3 = (float) r3
            int r4 = r13.top
            int r4 = r4 + r1
            float r4 = (float) r4
            int r5 = r13.right
            int r5 = r5 - r1
            float r5 = (float) r5
            int r6 = r13.bottom
            int r6 = r6 - r1
            float r1 = (float) r6
            r2.set(r3, r4, r5, r1)
            int r1 = r0.currentIcon
            if (r1 == r8) goto L_0x05cc
            r2 = 4
            if (r1 != r2) goto L_0x05ee
            int r1 = r0.nextIcon
            if (r1 == r8) goto L_0x05cc
            r2 = 3
            if (r1 != r2) goto L_0x05ee
        L_0x05cc:
            android.graphics.Paint r1 = r0.paint
            float r2 = (float) r9
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
            r1 = r33
            r1.drawArc(r2, r3, r4, r5, r6)
            android.graphics.Paint r1 = r0.paint
            r1.setAlpha(r9)
        L_0x05ee:
            android.graphics.RectF r2 = r0.rect
            float r3 = r0.downloadRadOffset
            r5 = 0
            android.graphics.Paint r6 = r0.paint
            r1 = r33
            r4 = r15
            r1.drawArc(r2, r3, r4, r5, r6)
        L_0x05fb:
            r1 = 1065353216(0x3var_, float:1.0)
            int r2 = (r14 > r1 ? 1 : (r14 == r1 ? 0 : -1))
            if (r2 == 0) goto L_0x0604
            r33.restore()
        L_0x0604:
            int r1 = r0.currentIcon
            int r2 = r0.nextIcon
            if (r1 != r2) goto L_0x060f
            r9 = 1065353216(0x3var_, float:1.0)
            r14 = 1065353216(0x3var_, float:1.0)
            goto L_0x0630
        L_0x060f:
            r2 = 4
            if (r1 != r2) goto L_0x0619
            float r5 = r0.transitionProgress
            r1 = 1065353216(0x3var_, float:1.0)
            float r2 = r1 - r5
            goto L_0x062e
        L_0x0619:
            r1 = 1065353216(0x3var_, float:1.0)
            float r2 = r0.transitionProgress
            float r2 = r2 / r20
            float r5 = java.lang.Math.min(r1, r2)
            float r2 = r0.transitionProgress
            float r2 = r2 / r20
            float r2 = r1 - r2
            r1 = 0
            float r2 = java.lang.Math.max(r1, r2)
        L_0x062e:
            r14 = r2
            r9 = r5
        L_0x0630:
            int r1 = r0.nextIcon
            r2 = 5
            if (r1 != r2) goto L_0x063a
            android.graphics.Path[] r1 = org.telegram.ui.ActionBar.Theme.chat_filePath
            r15 = r1
            r6 = 0
            goto L_0x0645
        L_0x063a:
            int r1 = r0.currentIcon
            r2 = 5
            if (r1 != r2) goto L_0x0643
            android.graphics.Path[] r1 = org.telegram.ui.ActionBar.Theme.chat_filePath
            r6 = r1
            goto L_0x0644
        L_0x0643:
            r6 = 0
        L_0x0644:
            r15 = 0
        L_0x0645:
            int r1 = r0.nextIcon
            r2 = 7
            if (r1 != r2) goto L_0x064d
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.chat_flameIcon
            goto L_0x0658
        L_0x064d:
            int r1 = r0.currentIcon
            r2 = 7
            if (r1 != r2) goto L_0x0657
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.chat_flameIcon
            r2 = r1
            r1 = 0
            goto L_0x0659
        L_0x0657:
            r1 = 0
        L_0x0658:
            r2 = 0
        L_0x0659:
            int r3 = r0.nextIcon
            r4 = 8
            if (r3 != r4) goto L_0x0662
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.chat_gifIcon
            goto L_0x066a
        L_0x0662:
            int r3 = r0.currentIcon
            r4 = 8
            if (r3 != r4) goto L_0x066a
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.chat_gifIcon
        L_0x066a:
            r5 = r1
            r4 = r2
            int r1 = r0.currentIcon
            r2 = 9
            if (r1 == r2) goto L_0x0683
            int r1 = r0.nextIcon
            r2 = 9
            if (r1 != r2) goto L_0x0679
            goto L_0x0683
        L_0x0679:
            r10 = r4
            r29 = r6
            r26 = r14
            r16 = r15
            r14 = r5
            goto L_0x0708
        L_0x0683:
            android.graphics.Paint r1 = r0.paint
            int r2 = r0.currentIcon
            int r3 = r0.nextIcon
            if (r2 != r3) goto L_0x068e
            r3 = 255(0xff, float:3.57E-43)
            goto L_0x0693
        L_0x068e:
            float r2 = r0.transitionProgress
            float r2 = r2 * r19
            int r3 = (int) r2
        L_0x0693:
            r1.setAlpha(r3)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r18)
            int r3 = r12 + r1
            r1 = 1077936128(0x40400000, float:3.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r2 = r11 - r1
            int r1 = r0.currentIcon
            int r8 = r0.nextIcon
            if (r1 == r8) goto L_0x06b4
            r33.save()
            float r1 = r0.transitionProgress
            float r8 = (float) r11
            float r10 = (float) r12
            r7.scale(r1, r1, r8, r10)
        L_0x06b4:
            r1 = 1086324736(0x40CLASSNAME, float:6.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = r2 - r1
            float r8 = (float) r1
            r1 = 1086324736(0x40CLASSNAME, float:6.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = r3 - r1
            float r10 = (float) r1
            float r1 = (float) r2
            r16 = r15
            float r15 = (float) r3
            r17 = r6
            android.graphics.Paint r6 = r0.paint
            r23 = r1
            r1 = r33
            r24 = r2
            r2 = r8
            r8 = r3
            r3 = r10
            r10 = r4
            r4 = r23
            r26 = r14
            r14 = r5
            r5 = r15
            r29 = r17
            r1.drawLine(r2, r3, r4, r5, r6)
            r1 = 1094713344(0x41400000, float:12.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r2 = r24 + r1
            float r4 = (float) r2
            r1 = 1094713344(0x41400000, float:12.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r3 = r8 - r1
            float r5 = (float) r3
            android.graphics.Paint r6 = r0.paint
            r1 = r33
            r2 = r23
            r3 = r15
            r1.drawLine(r2, r3, r4, r5, r6)
            int r1 = r0.currentIcon
            int r2 = r0.nextIcon
            if (r1 == r2) goto L_0x0708
            r33.restore()
        L_0x0708:
            int r1 = r0.currentIcon
            r2 = 12
            if (r1 == r2) goto L_0x0714
            int r1 = r0.nextIcon
            r2 = 12
            if (r1 != r2) goto L_0x0782
        L_0x0714:
            int r1 = r0.currentIcon
            int r2 = r0.nextIcon
            if (r1 != r2) goto L_0x071d
            r5 = 1065353216(0x3var_, float:1.0)
            goto L_0x072a
        L_0x071d:
            r1 = 13
            if (r2 != r1) goto L_0x0724
            float r5 = r0.transitionProgress
            goto L_0x072a
        L_0x0724:
            float r1 = r0.transitionProgress
            r2 = 1065353216(0x3var_, float:1.0)
            float r5 = r2 - r1
        L_0x072a:
            android.graphics.Paint r1 = r0.paint
            int r2 = r0.currentIcon
            int r3 = r0.nextIcon
            if (r2 != r3) goto L_0x0735
            r3 = 255(0xff, float:3.57E-43)
            goto L_0x0738
        L_0x0735:
            float r2 = r5 * r19
            int r3 = (int) r2
        L_0x0738:
            r1.setAlpha(r3)
            org.telegram.messenger.AndroidUtilities.dp(r18)
            r1 = 1077936128(0x40400000, float:3.0)
            org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = r0.currentIcon
            int r2 = r0.nextIcon
            if (r1 == r2) goto L_0x0751
            r33.save()
            float r1 = (float) r11
            float r2 = (float) r12
            r7.scale(r5, r5, r1, r2)
        L_0x0751:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r1 = (float) r1
            float r2 = r0.scale
            float r1 = r1 * r2
            float r2 = (float) r11
            float r8 = r2 - r1
            float r3 = (float) r12
            float r15 = r3 - r1
            float r17 = r2 + r1
            float r23 = r3 + r1
            android.graphics.Paint r6 = r0.paint
            r1 = r33
            r2 = r8
            r3 = r15
            r4 = r17
            r5 = r23
            r1.drawLine(r2, r3, r4, r5, r6)
            android.graphics.Paint r6 = r0.paint
            r2 = r17
            r4 = r8
            r1.drawLine(r2, r3, r4, r5, r6)
            int r1 = r0.currentIcon
            int r2 = r0.nextIcon
            if (r1 == r2) goto L_0x0782
            r33.restore()
        L_0x0782:
            int r1 = r0.currentIcon
            r2 = 13
            if (r1 == r2) goto L_0x078e
            int r1 = r0.nextIcon
            r2 = 13
            if (r1 != r2) goto L_0x080e
        L_0x078e:
            int r1 = r0.currentIcon
            int r2 = r0.nextIcon
            if (r1 != r2) goto L_0x0797
            r5 = 1065353216(0x3var_, float:1.0)
            goto L_0x07a4
        L_0x0797:
            r1 = 13
            if (r2 != r1) goto L_0x079e
            float r5 = r0.transitionProgress
            goto L_0x07a4
        L_0x079e:
            float r1 = r0.transitionProgress
            r2 = 1065353216(0x3var_, float:1.0)
            float r5 = r2 - r1
        L_0x07a4:
            android.text.TextPaint r1 = r0.textPaint
            float r2 = r5 * r19
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
            int r4 = r0.nextIcon
            if (r3 == r4) goto L_0x07c7
            r33.save()
            float r3 = (float) r11
            float r4 = (float) r12
            r7.scale(r5, r5, r3, r4)
        L_0x07c7:
            float r3 = r0.animatedDownloadProgress
            r4 = 1120403456(0x42CLASSNAME, float:100.0)
            float r3 = r3 * r4
            int r3 = (int) r3
            java.lang.String r4 = r0.percentString
            if (r4 == 0) goto L_0x07d6
            int r4 = r0.lastPercent
            if (r3 == r4) goto L_0x07fc
        L_0x07d6:
            r0.lastPercent = r3
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]
            int r3 = r0.lastPercent
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r5 = 0
            r4[r5] = r3
            java.lang.String r3 = "%d%%"
            java.lang.String r3 = java.lang.String.format(r3, r4)
            r0.percentString = r3
            android.text.TextPaint r3 = r0.textPaint
            java.lang.String r4 = r0.percentString
            float r3 = r3.measureText(r4)
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
            r33.restore()
        L_0x080e:
            int r1 = r0.currentIcon
            r2 = 1
            if (r1 == 0) goto L_0x0828
            if (r1 == r2) goto L_0x0828
            int r1 = r0.nextIcon
            if (r1 == 0) goto L_0x0828
            if (r1 != r2) goto L_0x081c
            goto L_0x0828
        L_0x081c:
            r23 = r10
            r30 = r11
            r24 = r12
            r17 = r14
            r8 = 255(0xff, float:3.57E-43)
            goto L_0x0b1b
        L_0x0828:
            int r1 = r0.currentIcon
            if (r1 != 0) goto L_0x0830
            int r1 = r0.nextIcon
            if (r1 == r2) goto L_0x0838
        L_0x0830:
            int r1 = r0.currentIcon
            if (r1 != r2) goto L_0x0845
            int r1 = r0.nextIcon
            if (r1 != 0) goto L_0x0845
        L_0x0838:
            boolean r1 = r0.animatingTransition
            if (r1 == 0) goto L_0x0845
            android.view.animation.DecelerateInterpolator r1 = r0.interpolator
            float r2 = r0.transitionProgress
            float r1 = r1.getInterpolation(r2)
            goto L_0x0846
        L_0x0845:
            r1 = 0
        L_0x0846:
            android.graphics.Path r2 = r0.path1
            r2.reset()
            android.graphics.Path r2 = r0.path2
            r2.reset()
            int r2 = r0.currentIcon
            if (r2 == 0) goto L_0x0864
            r3 = 1
            if (r2 == r3) goto L_0x085c
            r2 = 0
            r3 = 0
            r4 = 0
        L_0x085a:
            r15 = 0
            goto L_0x086b
        L_0x085c:
            float[] r2 = pausePath1
            float[] r3 = pausePath2
            r15 = 90
            r4 = 0
            goto L_0x086b
        L_0x0864:
            float[] r2 = playPath1
            float[] r3 = playPath2
            float[] r4 = playFinalPath
            goto L_0x085a
        L_0x086b:
            int r5 = r0.nextIcon
            if (r5 == 0) goto L_0x087d
            r6 = 1
            if (r5 == r6) goto L_0x0876
            r5 = 0
            r6 = 0
        L_0x0874:
            r8 = 0
            goto L_0x0882
        L_0x0876:
            float[] r5 = pausePath1
            float[] r6 = pausePath2
            r8 = 90
            goto L_0x0882
        L_0x087d:
            float[] r5 = playPath1
            float[] r6 = playPath2
            goto L_0x0874
        L_0x0882:
            if (r2 != 0) goto L_0x088b
            r2 = r5
            r3 = r6
            r17 = r14
            r5 = 0
            r6 = 0
            goto L_0x088d
        L_0x088b:
            r17 = r14
        L_0x088d:
            boolean r14 = r0.animatingTransition
            if (r14 != 0) goto L_0x092e
            if (r4 == 0) goto L_0x092e
            r2 = 0
        L_0x0894:
            int r3 = r4.length
            r5 = 2
            int r3 = r3 / r5
            if (r2 >= r3) goto L_0x0920
            if (r2 != 0) goto L_0x08da
            android.graphics.Path r3 = r0.path1
            int r5 = r2 * 2
            r6 = r4[r5]
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r6 = (float) r6
            float r14 = r0.scale
            float r6 = r6 * r14
            int r14 = r5 + 1
            r22 = r4[r14]
            r23 = r10
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r22)
            float r10 = (float) r10
            r22 = r9
            float r9 = r0.scale
            float r10 = r10 * r9
            r3.moveTo(r6, r10)
            android.graphics.Path r3 = r0.path2
            r5 = r4[r5]
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            float r6 = r0.scale
            float r5 = r5 * r6
            r6 = r4[r14]
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r6 = (float) r6
            float r9 = r0.scale
            float r6 = r6 * r9
            r3.moveTo(r5, r6)
            goto L_0x0918
        L_0x08da:
            r22 = r9
            r23 = r10
            android.graphics.Path r3 = r0.path1
            int r5 = r2 * 2
            r6 = r4[r5]
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r6 = (float) r6
            float r9 = r0.scale
            float r6 = r6 * r9
            int r9 = r5 + 1
            r10 = r4[r9]
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r10 = (float) r10
            float r14 = r0.scale
            float r10 = r10 * r14
            r3.lineTo(r6, r10)
            android.graphics.Path r3 = r0.path2
            r5 = r4[r5]
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            float r6 = r0.scale
            float r5 = r5 * r6
            r6 = r4[r9]
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r6 = (float) r6
            float r9 = r0.scale
            float r6 = r6 * r9
            r3.lineTo(r5, r6)
        L_0x0918:
            int r2 = r2 + 1
            r9 = r22
            r10 = r23
            goto L_0x0894
        L_0x0920:
            r22 = r9
            r23 = r10
            r28 = r8
            r30 = r11
            r24 = r12
        L_0x092a:
            r8 = 255(0xff, float:3.57E-43)
            goto L_0x0ac2
        L_0x092e:
            r22 = r9
            r23 = r10
            if (r5 != 0) goto L_0x09e6
            r4 = 0
        L_0x0935:
            r5 = 5
            if (r4 >= r5) goto L_0x09b9
            if (r4 != 0) goto L_0x0977
            android.graphics.Path r5 = r0.path1
            int r6 = r4 * 2
            r9 = r2[r6]
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r9 = (float) r9
            float r10 = r0.scale
            float r9 = r9 * r10
            int r10 = r6 + 1
            r14 = r2[r10]
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r14 = (float) r14
            r24 = r12
            float r12 = r0.scale
            float r14 = r14 * r12
            r5.moveTo(r9, r14)
            android.graphics.Path r5 = r0.path2
            r6 = r3[r6]
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r6 = (float) r6
            float r9 = r0.scale
            float r6 = r6 * r9
            r9 = r3[r10]
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r9 = (float) r9
            float r10 = r0.scale
            float r9 = r9 * r10
            r5.moveTo(r6, r9)
            goto L_0x09b3
        L_0x0977:
            r24 = r12
            android.graphics.Path r5 = r0.path1
            int r6 = r4 * 2
            r9 = r2[r6]
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r9 = (float) r9
            float r10 = r0.scale
            float r9 = r9 * r10
            int r10 = r6 + 1
            r12 = r2[r10]
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r12 = (float) r12
            float r14 = r0.scale
            float r12 = r12 * r14
            r5.lineTo(r9, r12)
            android.graphics.Path r5 = r0.path2
            r6 = r3[r6]
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r6 = (float) r6
            float r9 = r0.scale
            float r6 = r6 * r9
            r9 = r3[r10]
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r9 = (float) r9
            float r10 = r0.scale
            float r9 = r9 * r10
            r5.lineTo(r6, r9)
        L_0x09b3:
            int r4 = r4 + 1
            r12 = r24
            goto L_0x0935
        L_0x09b9:
            r24 = r12
            int r2 = r0.nextIcon
            r3 = 4
            if (r2 != r3) goto L_0x09d4
            android.graphics.Paint r2 = r0.paint2
            float r3 = r0.transitionProgress
            r4 = 1065353216(0x3var_, float:1.0)
            float r5 = r4 - r3
            float r5 = r5 * r19
            int r3 = (int) r5
            r2.setAlpha(r3)
        L_0x09ce:
            r28 = r8
            r30 = r11
            goto L_0x092a
        L_0x09d4:
            android.graphics.Paint r3 = r0.paint2
            int r4 = r0.currentIcon
            if (r4 != r2) goto L_0x09dd
            r2 = 255(0xff, float:3.57E-43)
            goto L_0x09e2
        L_0x09dd:
            float r2 = r0.transitionProgress
            float r2 = r2 * r19
            int r2 = (int) r2
        L_0x09e2:
            r3.setAlpha(r2)
            goto L_0x09ce
        L_0x09e6:
            r24 = r12
            r4 = 0
        L_0x09e9:
            r9 = 5
            if (r4 >= r9) goto L_0x0ab7
            if (r4 != 0) goto L_0x0a50
            android.graphics.Path r9 = r0.path1
            int r10 = r4 * 2
            r12 = r2[r10]
            r14 = r5[r10]
            r28 = r2[r10]
            float r14 = r14 - r28
            float r14 = r14 * r1
            float r12 = r12 + r14
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r12 = (float) r12
            float r14 = r0.scale
            float r12 = r12 * r14
            int r14 = r10 + 1
            r28 = r2[r14]
            r30 = r5[r14]
            r31 = r2[r14]
            float r30 = r30 - r31
            float r30 = r30 * r1
            float r28 = r28 + r30
            r30 = r11
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r28)
            float r11 = (float) r11
            r28 = r8
            float r8 = r0.scale
            float r11 = r11 * r8
            r9.moveTo(r12, r11)
            android.graphics.Path r8 = r0.path2
            r9 = r3[r10]
            r11 = r6[r10]
            r10 = r3[r10]
            float r11 = r11 - r10
            float r11 = r11 * r1
            float r9 = r9 + r11
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r9 = (float) r9
            float r10 = r0.scale
            float r9 = r9 * r10
            r10 = r3[r14]
            r11 = r6[r14]
            r12 = r3[r14]
            float r11 = r11 - r12
            float r11 = r11 * r1
            float r10 = r10 + r11
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r10 = (float) r10
            float r11 = r0.scale
            float r10 = r10 * r11
            r8.moveTo(r9, r10)
            goto L_0x0aaf
        L_0x0a50:
            r28 = r8
            r30 = r11
            android.graphics.Path r8 = r0.path1
            int r9 = r4 * 2
            r10 = r2[r9]
            r11 = r5[r9]
            r12 = r2[r9]
            float r11 = r11 - r12
            float r11 = r11 * r1
            float r10 = r10 + r11
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r10 = (float) r10
            float r11 = r0.scale
            float r10 = r10 * r11
            int r11 = r9 + 1
            r12 = r2[r11]
            r14 = r5[r11]
            r31 = r2[r11]
            float r14 = r14 - r31
            float r14 = r14 * r1
            float r12 = r12 + r14
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r12 = (float) r12
            float r14 = r0.scale
            float r12 = r12 * r14
            r8.lineTo(r10, r12)
            android.graphics.Path r8 = r0.path2
            r10 = r3[r9]
            r12 = r6[r9]
            r9 = r3[r9]
            float r12 = r12 - r9
            float r12 = r12 * r1
            float r10 = r10 + r12
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r9 = (float) r9
            float r10 = r0.scale
            float r9 = r9 * r10
            r10 = r3[r11]
            r12 = r6[r11]
            r11 = r3[r11]
            float r12 = r12 - r11
            float r12 = r12 * r1
            float r10 = r10 + r12
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r10 = (float) r10
            float r11 = r0.scale
            float r10 = r10 * r11
            r8.lineTo(r9, r10)
        L_0x0aaf:
            int r4 = r4 + 1
            r8 = r28
            r11 = r30
            goto L_0x09e9
        L_0x0ab7:
            r28 = r8
            r30 = r11
            android.graphics.Paint r2 = r0.paint2
            r8 = 255(0xff, float:3.57E-43)
            r2.setAlpha(r8)
        L_0x0ac2:
            android.graphics.Path r2 = r0.path1
            r2.close()
            android.graphics.Path r2 = r0.path2
            r2.close()
            r33.save()
            int r2 = r13.left
            float r2 = (float) r2
            int r3 = r13.top
            float r3 = (float) r3
            r7.translate(r2, r3)
            float r2 = (float) r15
            int r3 = r28 - r15
            float r3 = (float) r3
            float r3 = r3 * r1
            float r2 = r2 + r3
            int r1 = r13.left
            int r11 = r30 - r1
            float r1 = (float) r11
            int r3 = r13.top
            int r12 = r24 - r3
            float r3 = (float) r12
            r7.rotate(r2, r1, r3)
            int r1 = r0.currentIcon
            if (r1 == 0) goto L_0x0af3
            r2 = 1
            if (r1 != r2) goto L_0x0af8
        L_0x0af3:
            int r1 = r0.currentIcon
            r2 = 4
            if (r1 != r2) goto L_0x0b08
        L_0x0af8:
            int r1 = r13.left
            int r11 = r30 - r1
            float r1 = (float) r11
            int r2 = r13.top
            int r12 = r24 - r2
            float r2 = (float) r12
            r9 = r22
            r7.scale(r9, r9, r1, r2)
            goto L_0x0b0a
        L_0x0b08:
            r9 = r22
        L_0x0b0a:
            android.graphics.Path r1 = r0.path1
            android.graphics.Paint r2 = r0.paint2
            r7.drawPath(r1, r2)
            android.graphics.Path r1 = r0.path2
            android.graphics.Paint r2 = r0.paint2
            r7.drawPath(r1, r2)
            r33.restore()
        L_0x0b1b:
            int r1 = r0.currentIcon
            r2 = 6
            if (r1 == r2) goto L_0x0b25
            int r1 = r0.nextIcon
            r2 = 6
            if (r1 != r2) goto L_0x0bbf
        L_0x0b25:
            int r1 = r0.currentIcon
            r2 = 6
            if (r1 == r2) goto L_0x0b4e
            float r1 = r0.transitionProgress
            int r2 = (r1 > r20 ? 1 : (r1 == r20 ? 0 : -1))
            if (r2 <= 0) goto L_0x0b4a
            float r1 = r1 - r20
            float r1 = r1 / r20
            float r2 = r1 / r20
            r3 = 1065353216(0x3var_, float:1.0)
            float r2 = java.lang.Math.min(r3, r2)
            float r10 = r3 - r2
            int r2 = (r1 > r20 ? 1 : (r1 == r20 ? 0 : -1))
            if (r2 <= 0) goto L_0x0b47
            float r1 = r1 - r20
            float r1 = r1 / r20
            goto L_0x0b48
        L_0x0b47:
            r1 = 0
        L_0x0b48:
            r11 = r1
            goto L_0x0b51
        L_0x0b4a:
            r10 = 1065353216(0x3var_, float:1.0)
            r11 = 0
            goto L_0x0b51
        L_0x0b4e:
            r10 = 0
            r11 = 1065353216(0x3var_, float:1.0)
        L_0x0b51:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r18)
            int r12 = r24 + r1
            r1 = 1077936128(0x40400000, float:3.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r13 = r30 - r1
            android.graphics.Paint r1 = r0.paint
            r1.setAlpha(r8)
            r1 = 1065353216(0x3var_, float:1.0)
            int r2 = (r10 > r1 ? 1 : (r10 == r1 ? 0 : -1))
            if (r2 >= 0) goto L_0x0b9b
            r1 = 1086324736(0x40CLASSNAME, float:6.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = r13 - r1
            float r2 = (float) r1
            r1 = 1086324736(0x40CLASSNAME, float:6.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = r12 - r1
            float r3 = (float) r1
            float r1 = (float) r13
            r4 = 1086324736(0x40CLASSNAME, float:6.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            float r4 = r4 * r10
            float r4 = r1 - r4
            float r1 = (float) r12
            r5 = 1086324736(0x40CLASSNAME, float:6.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            float r5 = r5 * r10
            float r5 = r1 - r5
            android.graphics.Paint r6 = r0.paint
            r1 = r33
            r1.drawLine(r2, r3, r4, r5, r6)
        L_0x0b9b:
            r1 = 0
            int r2 = (r11 > r1 ? 1 : (r11 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x0bbf
            float r2 = (float) r13
            float r3 = (float) r12
            r1 = 1094713344(0x41400000, float:12.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            float r1 = r1 * r11
            float r4 = r2 + r1
            r1 = 1094713344(0x41400000, float:12.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            float r1 = r1 * r11
            float r5 = r3 - r1
            android.graphics.Paint r6 = r0.paint
            r1 = r33
            r1.drawLine(r2, r3, r4, r5, r6)
        L_0x0bbf:
            r1 = r17
            if (r23 == 0) goto L_0x0CLASSNAME
            r2 = r23
            if (r2 == r1) goto L_0x0CLASSNAME
            int r3 = r2.getIntrinsicWidth()
            float r3 = (float) r3
            float r3 = r3 * r26
            int r3 = (int) r3
            int r4 = r2.getIntrinsicHeight()
            float r4 = (float) r4
            float r4 = r4 * r26
            int r4 = (int) r4
            android.graphics.ColorFilter r5 = r0.colorFilter
            r2.setColorFilter(r5)
            int r5 = r0.currentIcon
            int r6 = r0.nextIcon
            if (r5 != r6) goto L_0x0be5
            r5 = 255(0xff, float:3.57E-43)
            goto L_0x0bee
        L_0x0be5:
            float r5 = r0.transitionProgress
            r6 = 1065353216(0x3var_, float:1.0)
            float r5 = r6 - r5
            float r5 = r5 * r19
            int r5 = (int) r5
        L_0x0bee:
            r2.setAlpha(r5)
            r5 = 2
            int r3 = r3 / r5
            int r11 = r30 - r3
            int r4 = r4 / r5
            int r12 = r24 - r4
            int r3 = r30 + r3
            int r4 = r24 + r4
            r2.setBounds(r11, r12, r3, r4)
            r2.draw(r7)
        L_0x0CLASSNAME:
            if (r1 == 0) goto L_0x0c3b
            int r2 = r1.getIntrinsicWidth()
            float r2 = (float) r2
            float r2 = r2 * r9
            int r2 = (int) r2
            int r3 = r1.getIntrinsicHeight()
            float r3 = (float) r3
            float r3 = r3 * r9
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
            float r4 = r4 * r19
            int r4 = (int) r4
        L_0x0CLASSNAME:
            r1.setAlpha(r4)
            r4 = 2
            int r2 = r2 / r4
            int r11 = r30 - r2
            int r3 = r3 / r4
            int r12 = r24 - r3
            int r2 = r30 + r2
            int r3 = r24 + r3
            r1.setBounds(r11, r12, r2, r3)
            r1.draw(r7)
        L_0x0c3b:
            r1 = r29
            r2 = r16
            if (r1 == 0) goto L_0x0c9b
            if (r1 == r2) goto L_0x0c9b
            r3 = 1103101952(0x41CLASSNAME, float:24.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            float r3 = r3 * r26
            int r3 = (int) r3
            r4 = 1103101952(0x41CLASSNAME, float:24.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            float r4 = r4 * r26
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
            float r6 = r6 * r19
            int r6 = (int) r6
        L_0x0CLASSNAME:
            r5.setAlpha(r6)
            r33.save()
            r5 = 2
            int r3 = r3 / r5
            int r11 = r30 - r3
            float r3 = (float) r11
            int r4 = r4 / r5
            int r12 = r24 - r4
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
            r33.restore()
        L_0x0c9b:
            if (r2 == 0) goto L_0x0cf1
            r1 = 1103101952(0x41CLASSNAME, float:24.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            float r1 = r1 * r9
            int r1 = (int) r1
            r3 = 1103101952(0x41CLASSNAME, float:24.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            float r3 = r3 * r9
            int r3 = (int) r3
            android.graphics.Paint r4 = r0.paint2
            android.graphics.Paint$Style r5 = android.graphics.Paint.Style.FILL_AND_STROKE
            r4.setStyle(r5)
            android.graphics.Paint r4 = r0.paint2
            int r5 = r0.currentIcon
            int r6 = r0.nextIcon
            if (r5 != r6) goto L_0x0cc3
            r5 = 255(0xff, float:3.57E-43)
            goto L_0x0cc8
        L_0x0cc3:
            float r5 = r0.transitionProgress
            float r5 = r5 * r19
            int r5 = (int) r5
        L_0x0cc8:
            r4.setAlpha(r5)
            r33.save()
            r4 = 2
            int r1 = r1 / r4
            int r11 = r30 - r1
            float r1 = (float) r11
            int r3 = r3 / r4
            int r12 = r24 - r3
            float r3 = (float) r12
            r7.translate(r1, r3)
            r1 = 0
            r3 = r2[r1]
            android.graphics.Paint r1 = r0.paint2
            r7.drawPath(r3, r1)
            r1 = 1
            r3 = r2[r1]
            if (r3 == 0) goto L_0x0cee
            r2 = r2[r1]
            android.graphics.Paint r1 = r0.backPaint
            r7.drawPath(r2, r1)
        L_0x0cee:
            r33.restore()
        L_0x0cf1:
            long r1 = java.lang.System.currentTimeMillis()
            long r3 = r0.lastAnimationTime
            long r3 = r1 - r3
            r5 = 17
            int r8 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r8 <= 0) goto L_0x0d01
            r3 = 17
        L_0x0d01:
            r0.lastAnimationTime = r1
            int r1 = r0.currentIcon
            r2 = 3
            if (r1 == r2) goto L_0x0d1d
            r2 = 14
            if (r1 == r2) goto L_0x0d1d
            r5 = 4
            if (r1 != r5) goto L_0x0d13
            int r1 = r0.nextIcon
            if (r1 == r2) goto L_0x0d1d
        L_0x0d13:
            int r1 = r0.currentIcon
            r2 = 10
            if (r1 == r2) goto L_0x0d1d
            r2 = 13
            if (r1 != r2) goto L_0x0d6a
        L_0x0d1d:
            float r1 = r0.downloadRadOffset
            r5 = 360(0x168, double:1.78E-321)
            long r5 = r5 * r3
            float r2 = (float) r5
            r5 = 1159479296(0x451CLASSNAME, float:2500.0)
            float r2 = r2 / r5
            float r1 = r1 + r2
            r0.downloadRadOffset = r1
            float r1 = r0.downloadRadOffset
            float r1 = r0.getCircleValue(r1)
            r0.downloadRadOffset = r1
            int r1 = r0.nextIcon
            r2 = 2
            if (r1 == r2) goto L_0x0d67
            float r1 = r0.downloadProgress
            float r2 = r0.downloadProgressAnimationStart
            float r5 = r1 - r2
            r6 = 0
            int r8 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
            if (r8 <= 0) goto L_0x0d67
            float r6 = r0.downloadProgressTime
            float r8 = (float) r3
            float r6 = r6 + r8
            r0.downloadProgressTime = r6
            float r6 = r0.downloadProgressTime
            r8 = 1128792064(0x43480000, float:200.0)
            int r8 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r8 < 0) goto L_0x0d59
            r0.animatedDownloadProgress = r1
            r0.downloadProgressAnimationStart = r1
            r1 = 0
            r0.downloadProgressTime = r1
            goto L_0x0d67
        L_0x0d59:
            android.view.animation.DecelerateInterpolator r1 = r0.interpolator
            r8 = 1128792064(0x43480000, float:200.0)
            float r6 = r6 / r8
            float r1 = r1.getInterpolation(r6)
            float r5 = r5 * r1
            float r2 = r2 + r5
            r0.animatedDownloadProgress = r2
        L_0x0d67:
            r32.invalidateSelf()
        L_0x0d6a:
            boolean r1 = r0.animatingTransition
            if (r1 == 0) goto L_0x0d8f
            float r1 = r0.transitionProgress
            r2 = 1065353216(0x3var_, float:1.0)
            int r5 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r5 >= 0) goto L_0x0d8f
            float r3 = (float) r3
            float r4 = r0.transitionAnimationTime
            float r3 = r3 / r4
            float r1 = r1 + r3
            r0.transitionProgress = r1
            float r1 = r0.transitionProgress
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 < 0) goto L_0x0d8c
            int r1 = r0.nextIcon
            r0.currentIcon = r1
            r0.transitionProgress = r2
            r1 = 0
            r0.animatingTransition = r1
        L_0x0d8c:
            r32.invalidateSelf()
        L_0x0d8f:
            r1 = r27
            r2 = 1
            if (r1 < r2) goto L_0x0d97
            r7.restoreToCount(r1)
        L_0x0d97:
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
