package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.View;
import java.util.Arrays;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.ProfileGalleryView;

public class AvatarPreviewPagerIndicator extends View implements ProfileGalleryView.Callback {
    private float alpha = 0.0f;
    private float[] alphas = null;
    private final ValueAnimator animator;
    private final float[] animatorValues = {0.0f, 1.0f};
    private final Paint backgroundPaint;
    private final Paint barPaint;
    private final GradientDrawable bottomOverlayGradient;
    private final Rect bottomOverlayRect = new Rect();
    private int currentLoadingAnimationDirection = 1;
    private float currentLoadingAnimationProgress;
    private float currentProgress;
    /* access modifiers changed from: private */
    public boolean isOverlaysVisible;
    private long lastTime;
    private int overlayCountVisible = 1;
    private final float[] pressedOverlayAlpha = new float[2];
    private final GradientDrawable[] pressedOverlayGradient = new GradientDrawable[2];
    private final boolean[] pressedOverlayVisible = new boolean[2];
    private int previousSelectedPotision = -1;
    private float previousSelectedProgress;
    ProfileGalleryView profileGalleryView;
    private final RectF rect = new RectF();
    private final Paint selectedBarPaint;
    private int selectedPosition;
    private final GradientDrawable topOverlayGradient;
    private final Rect topOverlayRect = new Rect();

    public void onPhotosLoaded() {
    }

    public AvatarPreviewPagerIndicator(Context context) {
        super(context);
        Paint paint = new Paint(1);
        this.barPaint = paint;
        paint.setColor(NUM);
        Paint paint2 = new Paint(1);
        this.selectedBarPaint = paint2;
        paint2.setColor(-1);
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{NUM, 0});
        this.topOverlayGradient = gradientDrawable;
        gradientDrawable.setShape(0);
        GradientDrawable gradientDrawable2 = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{NUM, 0});
        this.bottomOverlayGradient = gradientDrawable2;
        gradientDrawable2.setShape(0);
        int i = 0;
        while (i < 2) {
            this.pressedOverlayGradient[i] = new GradientDrawable(i == 0 ? GradientDrawable.Orientation.LEFT_RIGHT : GradientDrawable.Orientation.RIGHT_LEFT, new int[]{NUM, 0});
            this.pressedOverlayGradient[i].setShape(0);
            i++;
        }
        Paint paint3 = new Paint(1);
        this.backgroundPaint = paint3;
        paint3.setColor(-16777216);
        paint3.setAlpha(66);
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        this.animator = ofFloat;
        ofFloat.setDuration(250);
        ofFloat.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                AvatarPreviewPagerIndicator.this.lambda$new$0$AvatarPreviewPagerIndicator(valueAnimator);
            }
        });
        ofFloat.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (!AvatarPreviewPagerIndicator.this.isOverlaysVisible) {
                    AvatarPreviewPagerIndicator.this.setVisibility(8);
                }
            }

            public void onAnimationStart(Animator animator) {
                AvatarPreviewPagerIndicator.this.setVisibility(0);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$AvatarPreviewPagerIndicator(ValueAnimator valueAnimator) {
        setAlphaValue(AndroidUtilities.lerp(this.animatorValues, valueAnimator.getAnimatedFraction()), true);
    }

    public void saveCurrentPageProgress() {
        this.previousSelectedProgress = this.currentProgress;
        this.previousSelectedPotision = this.selectedPosition;
        this.currentLoadingAnimationProgress = 0.0f;
        this.currentLoadingAnimationDirection = 1;
    }

    public void setAlphaValue(float f, boolean z) {
        if (Build.VERSION.SDK_INT > 18) {
            int i = (int) (255.0f * f);
            this.topOverlayGradient.setAlpha(i);
            this.bottomOverlayGradient.setAlpha(i);
            this.backgroundPaint.setAlpha((int) (66.0f * f));
            this.barPaint.setAlpha((int) (85.0f * f));
            this.selectedBarPaint.setAlpha(i);
            this.alpha = f;
        } else {
            setAlpha(f);
        }
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        int currentActionBarHeight = ActionBar.getCurrentActionBarHeight() + 0;
        this.topOverlayRect.set(0, 0, i, (int) (((float) currentActionBarHeight) * 0.5f));
        this.bottomOverlayRect.set(0, (int) (((float) i2) - (((float) AndroidUtilities.dp(72.0f)) * 0.5f)), i, i2);
        this.topOverlayGradient.setBounds(0, this.topOverlayRect.bottom, i, currentActionBarHeight + AndroidUtilities.dp(16.0f));
        this.bottomOverlayGradient.setBounds(0, (i2 - AndroidUtilities.dp(72.0f)) - AndroidUtilities.dp(24.0f), i, this.bottomOverlayRect.top);
        int i5 = i / 5;
        this.pressedOverlayGradient[0].setBounds(0, 0, i5, i2);
        this.pressedOverlayGradient[1].setBounds(i - i5, 0, i, i2);
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x01d2  */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x01f6  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x0210  */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x0213  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r23) {
        /*
            r22 = this;
            r0 = r22
            r1 = r23
            r3 = 0
        L_0x0005:
            r4 = 1132396544(0x437var_, float:255.0)
            r5 = 2
            r6 = 0
            if (r3 >= r5) goto L_0x0029
            float[] r5 = r0.pressedOverlayAlpha
            r7 = r5[r3]
            int r6 = (r7 > r6 ? 1 : (r7 == r6 ? 0 : -1))
            if (r6 <= 0) goto L_0x0026
            android.graphics.drawable.GradientDrawable[] r6 = r0.pressedOverlayGradient
            r6 = r6[r3]
            r5 = r5[r3]
            float r5 = r5 * r4
            int r4 = (int) r5
            r6.setAlpha(r4)
            android.graphics.drawable.GradientDrawable[] r4 = r0.pressedOverlayGradient
            r4 = r4[r3]
            r4.draw(r1)
        L_0x0026:
            int r3 = r3 + 1
            goto L_0x0005
        L_0x0029:
            org.telegram.ui.Components.ProfileGalleryView r3 = r0.profileGalleryView
            int r3 = r3.getRealCount()
            org.telegram.ui.Components.ProfileGalleryView r7 = r0.profileGalleryView
            int r7 = r7.getRealPosition()
            r0.selectedPosition = r7
            float[] r7 = r0.alphas
            if (r7 == 0) goto L_0x003e
            int r7 = r7.length
            if (r7 == r3) goto L_0x0045
        L_0x003e:
            float[] r7 = new float[r3]
            r0.alphas = r7
            java.util.Arrays.fill(r7, r6)
        L_0x0045:
            long r7 = android.os.SystemClock.elapsedRealtime()
            long r9 = r0.lastTime
            long r9 = r7 - r9
            r11 = 0
            int r13 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1))
            if (r13 < 0) goto L_0x0059
            r11 = 20
            int r13 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1))
            if (r13 <= 0) goto L_0x005b
        L_0x0059:
            r9 = 17
        L_0x005b:
            r0.lastTime = r7
            r8 = 1
            r11 = 1065353216(0x3var_, float:1.0)
            if (r3 <= r8) goto L_0x027f
            r12 = 20
            if (r3 > r12) goto L_0x027f
            int r12 = r0.overlayCountVisible
            r13 = 3
            if (r12 != 0) goto L_0x0070
            r0.alpha = r6
            r0.overlayCountVisible = r13
            goto L_0x0076
        L_0x0070:
            if (r12 != r8) goto L_0x0076
            r0.alpha = r6
            r0.overlayCountVisible = r5
        L_0x0076:
            int r12 = r0.overlayCountVisible
            r14 = 1118437376(0x42aa0000, float:85.0)
            if (r12 != r5) goto L_0x0090
            android.graphics.Paint r12 = r0.barPaint
            float r15 = r0.alpha
            float r15 = r15 * r14
            int r15 = (int) r15
            r12.setAlpha(r15)
            android.graphics.Paint r12 = r0.selectedBarPaint
            float r15 = r0.alpha
            float r15 = r15 * r4
            int r15 = (int) r15
            r12.setAlpha(r15)
        L_0x0090:
            int r12 = r22.getMeasuredWidth()
            r15 = 1092616192(0x41200000, float:10.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r12 = r12 - r15
            int r15 = r3 + -1
            int r15 = r15 * 2
            float r15 = (float) r15
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r12 = r12 - r15
            int r12 = r12 / r3
            r15 = 1082130432(0x40800000, float:4.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r2 = 0
            r16 = 0
        L_0x00af:
            r17 = 1140457472(0x43fa0000, float:500.0)
            if (r2 >= r3) goto L_0x0227
            int r18 = r2 * 2
            int r8 = r18 + 5
            float r8 = (float) r8
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r18 = r12 * r2
            int r8 = r8 + r18
            r18 = 85
            int r7 = r0.previousSelectedPotision
            r19 = 80
            r20 = 1073741824(0x40000000, float:2.0)
            if (r2 != r7) goto L_0x0124
            float r7 = r0.previousSelectedProgress
            float r7 = r7 - r11
            float r7 = java.lang.Math.abs(r7)
            r21 = 953267991(0x38d1b717, float:1.0E-4)
            int r7 = (r7 > r21 ? 1 : (r7 == r21 ? 0 : -1))
            if (r7 <= 0) goto L_0x0124
            float r7 = r0.previousSelectedProgress
            r23.save()
            float r5 = (float) r8
            float r4 = (float) r12
            float r4 = r4 * r7
            float r4 = r4 + r5
            float r13 = (float) r15
            int r6 = r8 + r12
            float r6 = (float) r6
            int r16 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r11 = r15 + r16
            float r11 = (float) r11
            r1.clipRect(r4, r13, r6, r11)
            android.graphics.RectF r4 = r0.rect
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r11 = r11 + r15
            float r11 = (float) r11
            r4.set(r5, r13, r6, r11)
            android.graphics.Paint r4 = r0.barPaint
            float r5 = r0.alpha
            float r5 = r5 * r14
            int r5 = (int) r5
            r4.setAlpha(r5)
            android.graphics.RectF r4 = r0.rect
            r5 = 1065353216(0x3var_, float:1.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r6 = (float) r6
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r11
            android.graphics.Paint r11 = r0.barPaint
            r1.drawRoundRect(r4, r6, r5, r11)
            r23.restore()
            r11 = r15
            r4 = 80
            r5 = 1118437376(0x42aa0000, float:85.0)
        L_0x0120:
            r16 = 1
            goto L_0x01bc
        L_0x0124:
            int r4 = r0.selectedPosition
            if (r2 != r4) goto L_0x01b5
            org.telegram.ui.Components.ProfileGalleryView r4 = r0.profileGalleryView
            boolean r4 = r4.isCurrentItemVideo()
            if (r4 == 0) goto L_0x01ad
            org.telegram.ui.Components.ProfileGalleryView r4 = r0.profileGalleryView
            float r7 = r4.getCurrentItemProgress()
            r0.currentProgress = r7
            r4 = 0
            int r5 = (r7 > r4 ? 1 : (r7 == r4 ? 0 : -1))
            if (r5 > 0) goto L_0x0145
            org.telegram.ui.Components.ProfileGalleryView r5 = r0.profileGalleryView
            boolean r5 = r5.isLoadingCurrentVideo()
            if (r5 != 0) goto L_0x014b
        L_0x0145:
            float r5 = r0.currentLoadingAnimationProgress
            int r5 = (r5 > r4 ? 1 : (r5 == r4 ? 0 : -1))
            if (r5 <= 0) goto L_0x0172
        L_0x014b:
            float r4 = r0.currentLoadingAnimationProgress
            int r5 = r0.currentLoadingAnimationDirection
            r11 = r15
            long r14 = (long) r5
            long r14 = r14 * r9
            float r13 = (float) r14
            float r13 = r13 / r17
            float r4 = r4 + r13
            r0.currentLoadingAnimationProgress = r4
            r13 = 1065353216(0x3var_, float:1.0)
            int r14 = (r4 > r13 ? 1 : (r4 == r13 ? 0 : -1))
            if (r14 <= 0) goto L_0x0166
            r0.currentLoadingAnimationProgress = r13
            int r5 = r5 * -1
            r0.currentLoadingAnimationDirection = r5
            goto L_0x0173
        L_0x0166:
            r13 = 0
            int r4 = (r4 > r13 ? 1 : (r4 == r13 ? 0 : -1))
            if (r4 > 0) goto L_0x0173
            r0.currentLoadingAnimationProgress = r13
            int r5 = r5 * -1
            r0.currentLoadingAnimationDirection = r5
            goto L_0x0173
        L_0x0172:
            r11 = r15
        L_0x0173:
            android.graphics.RectF r4 = r0.rect
            float r5 = (float) r8
            float r13 = (float) r11
            int r14 = r8 + r12
            float r14 = (float) r14
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r15 = r15 + r11
            float r15 = (float) r15
            r4.set(r5, r13, r14, r15)
            android.graphics.Paint r4 = r0.barPaint
            r5 = 1111490560(0x42400000, float:48.0)
            float r13 = r0.currentLoadingAnimationProgress
            float r13 = r13 * r5
            r5 = 1118437376(0x42aa0000, float:85.0)
            float r13 = r13 + r5
            float r6 = r0.alpha
            float r13 = r13 * r6
            int r6 = (int) r13
            r4.setAlpha(r6)
            android.graphics.RectF r4 = r0.rect
            r6 = 1065353216(0x3var_, float:1.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r13 = (float) r13
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r14 = (float) r14
            android.graphics.Paint r15 = r0.barPaint
            r1.drawRoundRect(r4, r13, r14, r15)
            r4 = 80
            goto L_0x0120
        L_0x01ad:
            r11 = r15
            r5 = 1118437376(0x42aa0000, float:85.0)
            r6 = 1065353216(0x3var_, float:1.0)
            r0.currentProgress = r6
            goto L_0x01b8
        L_0x01b5:
            r11 = r15
            r5 = 1118437376(0x42aa0000, float:85.0)
        L_0x01b8:
            r4 = 85
            r7 = 1065353216(0x3var_, float:1.0)
        L_0x01bc:
            android.graphics.RectF r6 = r0.rect
            float r8 = (float) r8
            float r13 = (float) r11
            float r14 = (float) r12
            float r14 = r14 * r7
            float r14 = r14 + r8
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r15 = r11 + r7
            float r7 = (float) r15
            r6.set(r8, r13, r14, r7)
            int r6 = r0.selectedPosition
            if (r2 == r6) goto L_0x01f6
            int r6 = r0.overlayCountVisible
            r7 = 3
            if (r6 != r7) goto L_0x01f3
            android.graphics.Paint r6 = r0.barPaint
            float r4 = (float) r4
            org.telegram.ui.Components.CubicBezierInterpolator r7 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_BOTH
            float[] r8 = r0.alphas
            r8 = r8[r2]
            float r7 = r7.getInterpolation(r8)
            r8 = 1132396544(0x437var_, float:255.0)
            float r4 = org.telegram.messenger.AndroidUtilities.lerp(r4, r8, r7)
            float r7 = r0.alpha
            float r4 = r4 * r7
            int r4 = (int) r4
            r6.setAlpha(r4)
            goto L_0x01fe
        L_0x01f3:
            r8 = 1132396544(0x437var_, float:255.0)
            goto L_0x01fe
        L_0x01f6:
            r8 = 1132396544(0x437var_, float:255.0)
            float[] r4 = r0.alphas
            r6 = 1061158912(0x3var_, float:0.75)
            r4[r2] = r6
        L_0x01fe:
            android.graphics.RectF r4 = r0.rect
            r6 = 1065353216(0x3var_, float:1.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r7 = (float) r7
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r6 = (float) r13
            int r13 = r0.selectedPosition
            if (r2 != r13) goto L_0x0213
            android.graphics.Paint r13 = r0.selectedBarPaint
            goto L_0x0215
        L_0x0213:
            android.graphics.Paint r13 = r0.barPaint
        L_0x0215:
            r1.drawRoundRect(r4, r7, r6, r13)
            int r2 = r2 + 1
            r15 = r11
            r4 = 1132396544(0x437var_, float:255.0)
            r5 = 2
            r6 = 0
            r8 = 1
            r11 = 1065353216(0x3var_, float:1.0)
            r13 = 3
            r14 = 1118437376(0x42aa0000, float:85.0)
            goto L_0x00af
        L_0x0227:
            int r1 = r0.overlayCountVisible
            r2 = 2
            if (r1 != r2) goto L_0x0248
            float r1 = r0.alpha
            r2 = 1065353216(0x3var_, float:1.0)
            int r3 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r3 >= 0) goto L_0x0244
            float r3 = (float) r9
            r4 = 1127481344(0x43340000, float:180.0)
            float r3 = r3 / r4
            float r1 = r1 + r3
            r0.alpha = r1
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 <= 0) goto L_0x0241
            r0.alpha = r2
        L_0x0241:
            r16 = 1
            goto L_0x0281
        L_0x0244:
            r2 = 3
            r0.overlayCountVisible = r2
            goto L_0x0281
        L_0x0248:
            r2 = 3
            if (r1 != r2) goto L_0x0281
            r1 = 0
        L_0x024c:
            float[] r2 = r0.alphas
            int r3 = r2.length
            if (r1 >= r3) goto L_0x0281
            int r3 = r0.selectedPosition
            r4 = -1
            if (r1 == r3) goto L_0x0276
            r3 = r2[r1]
            r5 = 0
            int r3 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r3 <= 0) goto L_0x0276
            r3 = r2[r1]
            float r6 = (float) r9
            float r6 = r6 / r17
            float r3 = r3 - r6
            r2[r1] = r3
            r3 = r2[r1]
            int r3 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r3 > 0) goto L_0x0273
            r2[r1] = r5
            int r2 = r0.previousSelectedPotision
            if (r1 != r2) goto L_0x0273
            r0.previousSelectedPotision = r4
        L_0x0273:
            r16 = 1
            goto L_0x027c
        L_0x0276:
            int r2 = r0.previousSelectedPotision
            if (r1 != r2) goto L_0x027c
            r0.previousSelectedPotision = r4
        L_0x027c:
            int r1 = r1 + 1
            goto L_0x024c
        L_0x027f:
            r16 = 0
        L_0x0281:
            r1 = 2
            r2 = 0
        L_0x0283:
            if (r2 >= r1) goto L_0x02d0
            boolean[] r3 = r0.pressedOverlayVisible
            boolean r3 = r3[r2]
            if (r3 == 0) goto L_0x02ac
            float[] r3 = r0.pressedOverlayAlpha
            r4 = r3[r2]
            r5 = 1065353216(0x3var_, float:1.0)
            int r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            if (r4 >= 0) goto L_0x02aa
            r4 = r3[r2]
            float r6 = (float) r9
            r7 = 1127481344(0x43340000, float:180.0)
            float r6 = r6 / r7
            float r4 = r4 + r6
            r3[r2] = r4
            r4 = r3[r2]
            int r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            if (r4 <= 0) goto L_0x02a6
            r3[r2] = r5
        L_0x02a6:
            r6 = 0
            r8 = 1127481344(0x43340000, float:180.0)
            goto L_0x02c8
        L_0x02aa:
            r6 = 0
            goto L_0x02cb
        L_0x02ac:
            r5 = 1065353216(0x3var_, float:1.0)
            float[] r3 = r0.pressedOverlayAlpha
            r4 = r3[r2]
            r6 = 0
            int r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r4 <= 0) goto L_0x02cb
            r4 = r3[r2]
            float r7 = (float) r9
            r8 = 1127481344(0x43340000, float:180.0)
            float r7 = r7 / r8
            float r4 = r4 - r7
            r3[r2] = r4
            r4 = r3[r2]
            int r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r4 >= 0) goto L_0x02c8
            r3[r2] = r6
        L_0x02c8:
            r16 = 1
            goto L_0x02cd
        L_0x02cb:
            r8 = 1127481344(0x43340000, float:180.0)
        L_0x02cd:
            int r2 = r2 + 1
            goto L_0x0283
        L_0x02d0:
            if (r16 == 0) goto L_0x02d5
            r22.postInvalidateOnAnimation()
        L_0x02d5:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.AvatarPreviewPagerIndicator.onDraw(android.graphics.Canvas):void");
    }

    public void onDown(boolean z) {
        this.pressedOverlayVisible[!z] = true;
        postInvalidateOnAnimation();
    }

    public void onRelease() {
        Arrays.fill(this.pressedOverlayVisible, false);
        postInvalidateOnAnimation();
    }

    public void onVideoSet() {
        invalidate();
    }

    public void setProfileGalleryView(ProfileGalleryView profileGalleryView2) {
        this.profileGalleryView = profileGalleryView2;
    }
}
