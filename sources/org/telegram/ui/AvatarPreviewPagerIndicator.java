package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.TextPaint;
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
    private float currentAnimationValue;
    private int currentLoadingAnimationDirection = 1;
    private float currentLoadingAnimationProgress;
    private float currentProgress;
    private final RectF indicatorRect = new RectF();
    /* access modifiers changed from: private */
    public boolean isOverlaysVisible;
    int lastCurrentItem = -1;
    private long lastTime;
    private int overlayCountVisible = 1;
    Path path = new Path();
    private final float[] pressedOverlayAlpha = new float[2];
    private final GradientDrawable[] pressedOverlayGradient = new GradientDrawable[2];
    private final boolean[] pressedOverlayVisible = new boolean[2];
    private int previousSelectedPotision = -1;
    private float previousSelectedProgress;
    protected ProfileGalleryView profileGalleryView;
    private float progressToCounter;
    private final RectF rect = new RectF();
    RectF rectF = new RectF();
    private final Paint selectedBarPaint;
    private int selectedPosition;
    private final int statusBarHeight = 0;
    TextPaint textPaint;
    String title;
    private final GradientDrawable topOverlayGradient;
    private final Rect topOverlayRect = new Rect();

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
        ofFloat.addUpdateListener(new AvatarPreviewPagerIndicator$$ExternalSyntheticLambda0(this));
        ofFloat.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (!AvatarPreviewPagerIndicator.this.isOverlaysVisible) {
                    AvatarPreviewPagerIndicator.this.setVisibility(8);
                }
            }

            public void onAnimationStart(Animator animation) {
                AvatarPreviewPagerIndicator.this.setVisibility(0);
            }
        });
        TextPaint textPaint2 = new TextPaint(1);
        this.textPaint = textPaint2;
        textPaint2.setColor(-1);
        this.textPaint.setTypeface(Typeface.SANS_SERIF);
        this.textPaint.setTextAlign(Paint.Align.CENTER);
        this.textPaint.setTextSize(AndroidUtilities.dpf2(15.0f));
    }

    /* renamed from: lambda$new$0$org-telegram-ui-AvatarPreviewPagerIndicator  reason: not valid java name */
    public /* synthetic */ void m1457lambda$new$0$orgtelegramuiAvatarPreviewPagerIndicator(ValueAnimator anim) {
        float[] fArr = this.animatorValues;
        float animatedFraction = anim.getAnimatedFraction();
        this.currentAnimationValue = animatedFraction;
        setAlphaValue(AndroidUtilities.lerp(fArr, animatedFraction), true);
    }

    public void saveCurrentPageProgress() {
        this.previousSelectedProgress = this.currentProgress;
        this.previousSelectedPotision = this.selectedPosition;
        this.currentLoadingAnimationProgress = 0.0f;
        this.currentLoadingAnimationDirection = 1;
    }

    public void setAlphaValue(float value, boolean self) {
        if (Build.VERSION.SDK_INT > 18) {
            int alpha2 = (int) (255.0f * value);
            this.topOverlayGradient.setAlpha(alpha2);
            this.bottomOverlayGradient.setAlpha(alpha2);
            this.backgroundPaint.setAlpha((int) (66.0f * value));
            this.barPaint.setAlpha((int) (85.0f * value));
            this.selectedBarPaint.setAlpha(alpha2);
            this.alpha = value;
        } else {
            setAlpha(value);
        }
        if (!self) {
            this.currentAnimationValue = value;
        }
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.path.reset();
        this.rectF.set(0.0f, 0.0f, (float) getMeasuredHeight(), (float) getMeasuredWidth());
        this.path.addRoundRect(this.rectF, new float[]{(float) AndroidUtilities.dp(13.0f), (float) AndroidUtilities.dp(13.0f), (float) AndroidUtilities.dp(13.0f), (float) AndroidUtilities.dp(13.0f), 0.0f, 0.0f, 0.0f, 0.0f}, Path.Direction.CCW);
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        int actionBarHeight = ActionBar.getCurrentActionBarHeight() + 0;
        this.topOverlayRect.set(0, 0, w, (int) (((float) actionBarHeight) * 0.5f));
        this.bottomOverlayRect.set(0, (int) (((float) h) - (((float) AndroidUtilities.dp(72.0f)) * 0.5f)), w, h);
        this.topOverlayGradient.setBounds(0, this.topOverlayRect.bottom, w, AndroidUtilities.dp(16.0f) + actionBarHeight);
        this.bottomOverlayGradient.setBounds(0, (h - AndroidUtilities.dp(72.0f)) - AndroidUtilities.dp(24.0f), w, this.bottomOverlayRect.top);
        this.pressedOverlayGradient[0].setBounds(0, 0, w / 5, h);
        this.pressedOverlayGradient[1].setBounds(w - (w / 5), 0, w, h);
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:117:0x0331  */
    /* JADX WARNING: Removed duplicated region for block: B:118:0x0334  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r27) {
        /*
            r26 = this;
            r0 = r26
            r1 = r27
            r2 = 0
        L_0x0005:
            r3 = 1132396544(0x437var_, float:255.0)
            r4 = 2
            r5 = 0
            if (r2 >= r4) goto L_0x0029
            float[] r4 = r0.pressedOverlayAlpha
            r6 = r4[r2]
            int r5 = (r6 > r5 ? 1 : (r6 == r5 ? 0 : -1))
            if (r5 <= 0) goto L_0x0026
            android.graphics.drawable.GradientDrawable[] r5 = r0.pressedOverlayGradient
            r5 = r5[r2]
            r4 = r4[r2]
            float r4 = r4 * r3
            int r3 = (int) r4
            r5.setAlpha(r3)
            android.graphics.drawable.GradientDrawable[] r3 = r0.pressedOverlayGradient
            r3 = r3[r2]
            r3.draw(r1)
        L_0x0026:
            int r2 = r2 + 1
            goto L_0x0005
        L_0x0029:
            android.graphics.drawable.GradientDrawable r2 = r0.topOverlayGradient
            r2.draw(r1)
            android.graphics.Rect r2 = r0.topOverlayRect
            android.graphics.Paint r6 = r0.backgroundPaint
            r1.drawRect(r2, r6)
            org.telegram.ui.Components.ProfileGalleryView r2 = r0.profileGalleryView
            int r2 = r2.getRealCount()
            org.telegram.ui.Components.ProfileGalleryView r6 = r0.profileGalleryView
            int r6 = r6.getRealPosition()
            r0.selectedPosition = r6
            float[] r6 = r0.alphas
            if (r6 == 0) goto L_0x004a
            int r6 = r6.length
            if (r6 == r2) goto L_0x0051
        L_0x004a:
            float[] r6 = new float[r2]
            r0.alphas = r6
            java.util.Arrays.fill(r6, r5)
        L_0x0051:
            r6 = 0
            long r7 = android.os.SystemClock.elapsedRealtime()
            long r9 = r0.lastTime
            long r9 = r7 - r9
            r11 = 0
            int r13 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1))
            if (r13 < 0) goto L_0x0066
            r11 = 20
            int r13 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1))
            if (r13 <= 0) goto L_0x0068
        L_0x0066:
            r9 = 17
        L_0x0068:
            r0.lastTime = r7
            r12 = 1090519040(0x41000000, float:8.0)
            r13 = 20
            r14 = 1
            r15 = 1065353216(0x3var_, float:1.0)
            if (r2 <= r14) goto L_0x02aa
            if (r2 > r13) goto L_0x02aa
            int r13 = r0.overlayCountVisible
            r11 = 3
            if (r13 != 0) goto L_0x007f
            r0.alpha = r5
            r0.overlayCountVisible = r11
            goto L_0x0085
        L_0x007f:
            if (r13 != r14) goto L_0x0085
            r0.alpha = r5
            r0.overlayCountVisible = r4
        L_0x0085:
            int r13 = r0.overlayCountVisible
            r16 = 1118437376(0x42aa0000, float:85.0)
            if (r13 != r4) goto L_0x009f
            android.graphics.Paint r13 = r0.barPaint
            float r14 = r0.alpha
            float r14 = r14 * r16
            int r14 = (int) r14
            r13.setAlpha(r14)
            android.graphics.Paint r13 = r0.selectedBarPaint
            float r14 = r0.alpha
            float r14 = r14 * r3
            int r14 = (int) r14
            r13.setAlpha(r14)
        L_0x009f:
            int r13 = r26.getMeasuredWidth()
            r14 = 1092616192(0x41200000, float:10.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r13 = r13 - r14
            int r14 = r2 + -1
            int r14 = r14 * 2
            float r14 = (float) r14
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r13 = r13 - r14
            int r13 = r13 / r2
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r17 = 0
            r12 = r17
        L_0x00bd:
            r18 = 1140457472(0x43fa0000, float:500.0)
            if (r12 >= r2) goto L_0x024b
            int r19 = r12 * 2
            int r4 = r19 + 5
            float r4 = (float) r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r19 = r13 * r12
            int r4 = r4 + r19
            r19 = 85
            int r3 = r0.previousSelectedPotision
            r20 = 1073741824(0x40000000, float:2.0)
            if (r12 != r3) goto L_0x013c
            float r3 = r0.previousSelectedProgress
            float r3 = r3 - r15
            float r3 = java.lang.Math.abs(r3)
            r21 = 953267991(0x38d1b717, float:1.0E-4)
            int r3 = (r3 > r21 ? 1 : (r3 == r21 ? 0 : -1))
            if (r3 <= 0) goto L_0x013c
            float r3 = r0.previousSelectedProgress
            r27.save()
            float r11 = (float) r4
            float r5 = (float) r13
            float r5 = r5 * r3
            float r11 = r11 + r5
            float r5 = (float) r14
            int r15 = r4 + r13
            float r15 = (float) r15
            int r18 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r22 = r3
            int r3 = r14 + r18
            float r3 = (float) r3
            r1.clipRect(r11, r5, r15, r3)
            android.graphics.RectF r3 = r0.rect
            float r5 = (float) r4
            float r11 = (float) r14
            int r15 = r4 + r13
            float r15 = (float) r15
            int r18 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r23 = r6
            int r6 = r14 + r18
            float r6 = (float) r6
            r3.set(r5, r11, r15, r6)
            android.graphics.Paint r3 = r0.barPaint
            float r5 = r0.alpha
            float r5 = r5 * r16
            int r5 = (int) r5
            r3.setAlpha(r5)
            android.graphics.RectF r3 = r0.rect
            r5 = 1065353216(0x3var_, float:1.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r6 = (float) r6
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r11
            android.graphics.Paint r11 = r0.barPaint
            r1.drawRoundRect(r3, r6, r5, r11)
            r19 = 80
            r27.restore()
            r6 = 1
            r24 = r7
            r5 = r19
            r3 = r22
            goto L_0x01e0
        L_0x013c:
            r23 = r6
            int r3 = r0.selectedPosition
            if (r12 != r3) goto L_0x01d8
            org.telegram.ui.Components.ProfileGalleryView r3 = r0.profileGalleryView
            boolean r3 = r3.isCurrentItemVideo()
            if (r3 == 0) goto L_0x01cc
            org.telegram.ui.Components.ProfileGalleryView r3 = r0.profileGalleryView
            float r3 = r3.getCurrentItemProgress()
            r0.currentProgress = r3
            r5 = 0
            int r6 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r6 > 0) goto L_0x015f
            org.telegram.ui.Components.ProfileGalleryView r5 = r0.profileGalleryView
            boolean r5 = r5.isLoadingCurrentVideo()
            if (r5 != 0) goto L_0x0166
        L_0x015f:
            float r5 = r0.currentLoadingAnimationProgress
            r6 = 0
            int r5 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
            if (r5 <= 0) goto L_0x018e
        L_0x0166:
            float r5 = r0.currentLoadingAnimationProgress
            int r6 = r0.currentLoadingAnimationDirection
            r24 = r7
            long r7 = (long) r6
            long r7 = r7 * r9
            float r7 = (float) r7
            float r7 = r7 / r18
            float r5 = r5 + r7
            r0.currentLoadingAnimationProgress = r5
            r7 = 1065353216(0x3var_, float:1.0)
            int r8 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r8 <= 0) goto L_0x0182
            r0.currentLoadingAnimationProgress = r7
            int r6 = r6 * -1
            r0.currentLoadingAnimationDirection = r6
            goto L_0x0190
        L_0x0182:
            r7 = 0
            int r5 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r5 > 0) goto L_0x0190
            r0.currentLoadingAnimationProgress = r7
            int r6 = r6 * -1
            r0.currentLoadingAnimationDirection = r6
            goto L_0x0190
        L_0x018e:
            r24 = r7
        L_0x0190:
            android.graphics.RectF r5 = r0.rect
            float r6 = (float) r4
            float r7 = (float) r14
            int r8 = r4 + r13
            float r8 = (float) r8
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r11 = r11 + r14
            float r11 = (float) r11
            r5.set(r6, r7, r8, r11)
            android.graphics.Paint r5 = r0.barPaint
            r6 = 1111490560(0x42400000, float:48.0)
            float r7 = r0.currentLoadingAnimationProgress
            float r7 = r7 * r6
            float r7 = r7 + r16
            float r6 = r0.alpha
            float r7 = r7 * r6
            int r6 = (int) r7
            r5.setAlpha(r6)
            android.graphics.RectF r5 = r0.rect
            r6 = 1065353216(0x3var_, float:1.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r7 = (float) r7
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r8 = (float) r8
            android.graphics.Paint r11 = r0.barPaint
            r1.drawRoundRect(r5, r7, r8, r11)
            r5 = 1
            r19 = 80
            r6 = r5
            r5 = r19
            goto L_0x01e0
        L_0x01cc:
            r24 = r7
            r6 = 1065353216(0x3var_, float:1.0)
            r0.currentProgress = r6
            r3 = r6
            r5 = r19
            r6 = r23
            goto L_0x01e0
        L_0x01d8:
            r24 = r7
            r3 = 1065353216(0x3var_, float:1.0)
            r5 = r19
            r6 = r23
        L_0x01e0:
            android.graphics.RectF r7 = r0.rect
            float r8 = (float) r4
            float r11 = (float) r14
            float r15 = (float) r4
            r19 = r4
            float r4 = (float) r13
            float r4 = r4 * r3
            float r15 = r15 + r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r4 = r4 + r14
            float r4 = (float) r4
            r7.set(r8, r11, r15, r4)
            int r4 = r0.selectedPosition
            if (r12 == r4) goto L_0x021c
            int r4 = r0.overlayCountVisible
            r7 = 3
            if (r4 != r7) goto L_0x0219
            android.graphics.Paint r4 = r0.barPaint
            float r7 = (float) r5
            org.telegram.ui.Components.CubicBezierInterpolator r8 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_BOTH
            float[] r11 = r0.alphas
            r11 = r11[r12]
            float r8 = r8.getInterpolation(r11)
            r11 = 1132396544(0x437var_, float:255.0)
            float r7 = org.telegram.messenger.AndroidUtilities.lerp(r7, r11, r8)
            float r8 = r0.alpha
            float r7 = r7 * r8
            int r7 = (int) r7
            r4.setAlpha(r7)
            goto L_0x0224
        L_0x0219:
            r11 = 1132396544(0x437var_, float:255.0)
            goto L_0x0224
        L_0x021c:
            r11 = 1132396544(0x437var_, float:255.0)
            float[] r4 = r0.alphas
            r7 = 1061158912(0x3var_, float:0.75)
            r4[r12] = r7
        L_0x0224:
            android.graphics.RectF r4 = r0.rect
            r7 = 1065353216(0x3var_, float:1.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r8 = (float) r8
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r7 = (float) r15
            int r15 = r0.selectedPosition
            if (r12 != r15) goto L_0x0239
            android.graphics.Paint r15 = r0.selectedBarPaint
            goto L_0x023b
        L_0x0239:
            android.graphics.Paint r15 = r0.barPaint
        L_0x023b:
            r1.drawRoundRect(r4, r8, r7, r15)
            int r12 = r12 + 1
            r7 = r24
            r3 = 1132396544(0x437var_, float:255.0)
            r4 = 2
            r5 = 0
            r11 = 3
            r15 = 1065353216(0x3var_, float:1.0)
            goto L_0x00bd
        L_0x024b:
            r23 = r6
            r24 = r7
            int r3 = r0.overlayCountVisible
            r4 = 2
            if (r3 != r4) goto L_0x026f
            float r3 = r0.alpha
            r4 = 1065353216(0x3var_, float:1.0)
            int r5 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r5 >= 0) goto L_0x026b
            float r5 = (float) r9
            r6 = 1127481344(0x43340000, float:180.0)
            float r5 = r5 / r6
            float r3 = r3 + r5
            r0.alpha = r3
            int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r3 <= 0) goto L_0x0269
            r0.alpha = r4
        L_0x0269:
            r6 = 1
            goto L_0x02ac
        L_0x026b:
            r4 = 3
            r0.overlayCountVisible = r4
            goto L_0x02a7
        L_0x026f:
            r4 = 3
            if (r3 != r4) goto L_0x02a7
            r3 = 0
            r6 = r23
        L_0x0275:
            float[] r4 = r0.alphas
            int r5 = r4.length
            if (r3 >= r5) goto L_0x02ac
            int r5 = r0.selectedPosition
            r7 = -1
            if (r3 == r5) goto L_0x029e
            r5 = r4[r3]
            r8 = 0
            int r5 = (r5 > r8 ? 1 : (r5 == r8 ? 0 : -1))
            if (r5 <= 0) goto L_0x029e
            r5 = r4[r3]
            float r11 = (float) r9
            float r11 = r11 / r18
            float r5 = r5 - r11
            r4[r3] = r5
            r5 = r4[r3]
            int r5 = (r5 > r8 ? 1 : (r5 == r8 ? 0 : -1))
            if (r5 > 0) goto L_0x029c
            r4[r3] = r8
            int r4 = r0.previousSelectedPotision
            if (r3 != r4) goto L_0x029c
            r0.previousSelectedPotision = r7
        L_0x029c:
            r6 = 1
            goto L_0x02a4
        L_0x029e:
            int r4 = r0.previousSelectedPotision
            if (r3 != r4) goto L_0x02a4
            r0.previousSelectedPotision = r7
        L_0x02a4:
            int r3 = r3 + 1
            goto L_0x0275
        L_0x02a7:
            r6 = r23
            goto L_0x02ac
        L_0x02aa:
            r24 = r7
        L_0x02ac:
            r3 = 20
            if (r2 > r3) goto L_0x02b7
            float r3 = r0.progressToCounter
            r4 = 0
            int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r3 == 0) goto L_0x0374
        L_0x02b7:
            android.text.TextPaint r3 = r0.textPaint
            java.lang.String r4 = r26.getCurrentTitle()
            float r3 = r3.measureText(r4)
            android.graphics.RectF r4 = r0.indicatorRect
            int r5 = r26.getMeasuredWidth()
            r7 = 1090519040(0x41000000, float:8.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r5 = r5 - r8
            float r5 = (float) r5
            r4.right = r5
            android.graphics.RectF r4 = r0.indicatorRect
            float r5 = r4.right
            r7 = 1098907648(0x41800000, float:16.0)
            float r7 = org.telegram.messenger.AndroidUtilities.dpf2(r7)
            float r7 = r7 + r3
            float r5 = r5 - r7
            r4.left = r5
            android.graphics.RectF r4 = r0.indicatorRect
            r5 = 1090519040(0x41000000, float:8.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            r4.top = r5
            android.graphics.RectF r4 = r0.indicatorRect
            float r5 = r4.top
            r7 = 1104150528(0x41d00000, float:26.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r7 = (float) r7
            float r5 = r5 + r7
            r4.bottom = r5
            r4 = 1094713344(0x41400000, float:12.0)
            float r4 = org.telegram.messenger.AndroidUtilities.dpf2(r4)
            r27.save()
            r5 = 20
            if (r2 <= r5) goto L_0x0307
            r14 = 1
            goto L_0x0308
        L_0x0307:
            r14 = 0
        L_0x0308:
            r5 = r14
            r7 = 1125515264(0x43160000, float:150.0)
            if (r5 == 0) goto L_0x031b
            float r8 = r0.progressToCounter
            r11 = 1065353216(0x3var_, float:1.0)
            int r12 = (r8 > r11 ? 1 : (r8 == r11 ? 0 : -1))
            if (r12 == 0) goto L_0x031b
            float r11 = (float) r9
            float r11 = r11 / r7
            float r8 = r8 + r11
            r0.progressToCounter = r8
            goto L_0x0329
        L_0x031b:
            if (r5 != 0) goto L_0x0329
            float r8 = r0.progressToCounter
            r11 = 0
            int r12 = (r8 > r11 ? 1 : (r8 == r11 ? 0 : -1))
            if (r12 == 0) goto L_0x0329
            float r11 = (float) r9
            float r11 = r11 / r7
            float r8 = r8 - r11
            r0.progressToCounter = r8
        L_0x0329:
            float r7 = r0.progressToCounter
            r8 = 1065353216(0x3var_, float:1.0)
            int r11 = (r7 > r8 ? 1 : (r7 == r8 ? 0 : -1))
            if (r11 < 0) goto L_0x0334
            r0.progressToCounter = r8
            goto L_0x033f
        L_0x0334:
            r8 = 0
            int r7 = (r7 > r8 ? 1 : (r7 == r8 ? 0 : -1))
            if (r7 > 0) goto L_0x033c
            r0.progressToCounter = r8
            goto L_0x033f
        L_0x033c:
            r26.invalidate()
        L_0x033f:
            float r7 = r0.progressToCounter
            android.graphics.RectF r8 = r0.indicatorRect
            float r8 = r8.centerX()
            android.graphics.RectF r11 = r0.indicatorRect
            float r11 = r11.centerY()
            r1.scale(r7, r7, r8, r11)
            android.graphics.RectF r7 = r0.indicatorRect
            android.graphics.Paint r8 = r0.backgroundPaint
            r1.drawRoundRect(r7, r4, r4, r8)
            java.lang.String r7 = r26.getCurrentTitle()
            android.graphics.RectF r8 = r0.indicatorRect
            float r8 = r8.centerX()
            android.graphics.RectF r11 = r0.indicatorRect
            float r11 = r11.top
            r12 = 1100218368(0x41940000, float:18.5)
            float r12 = org.telegram.messenger.AndroidUtilities.dpf2(r12)
            float r11 = r11 + r12
            android.text.TextPaint r12 = r0.textPaint
            r1.drawText(r7, r8, r11, r12)
            r27.restore()
        L_0x0374:
            r3 = 0
        L_0x0375:
            r4 = 2
            if (r3 >= r4) goto L_0x03c7
            boolean[] r5 = r0.pressedOverlayVisible
            boolean r5 = r5[r3]
            if (r5 == 0) goto L_0x03a3
            float[] r5 = r0.pressedOverlayAlpha
            r7 = r5[r3]
            r8 = 1065353216(0x3var_, float:1.0)
            int r7 = (r7 > r8 ? 1 : (r7 == r8 ? 0 : -1))
            if (r7 >= 0) goto L_0x039f
            r7 = r5[r3]
            float r11 = (float) r9
            r12 = 1127481344(0x43340000, float:180.0)
            float r11 = r11 / r12
            float r7 = r7 + r11
            r5[r3] = r7
            r7 = r5[r3]
            int r7 = (r7 > r8 ? 1 : (r7 == r8 ? 0 : -1))
            if (r7 <= 0) goto L_0x0399
            r5[r3] = r8
        L_0x0399:
            r5 = 1
            r6 = r5
            r11 = 0
            r13 = 1127481344(0x43340000, float:180.0)
            goto L_0x03c4
        L_0x039f:
            r11 = 0
            r13 = 1127481344(0x43340000, float:180.0)
            goto L_0x03c4
        L_0x03a3:
            r8 = 1065353216(0x3var_, float:1.0)
            float[] r5 = r0.pressedOverlayAlpha
            r7 = r5[r3]
            r11 = 0
            int r7 = (r7 > r11 ? 1 : (r7 == r11 ? 0 : -1))
            if (r7 <= 0) goto L_0x03c2
            r7 = r5[r3]
            float r12 = (float) r9
            r13 = 1127481344(0x43340000, float:180.0)
            float r12 = r12 / r13
            float r7 = r7 - r12
            r5[r3] = r7
            r7 = r5[r3]
            int r7 = (r7 > r11 ? 1 : (r7 == r11 ? 0 : -1))
            if (r7 >= 0) goto L_0x03bf
            r5[r3] = r11
        L_0x03bf:
            r5 = 1
            r6 = r5
            goto L_0x03c4
        L_0x03c2:
            r13 = 1127481344(0x43340000, float:180.0)
        L_0x03c4:
            int r3 = r3 + 1
            goto L_0x0375
        L_0x03c7:
            if (r6 == 0) goto L_0x03cc
            r26.postInvalidateOnAnimation()
        L_0x03cc:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.AvatarPreviewPagerIndicator.onDraw(android.graphics.Canvas):void");
    }

    private String getCurrentTitle() {
        if (this.lastCurrentItem != this.profileGalleryView.getCurrentItem()) {
            this.title = this.profileGalleryView.getAdapter().getPageTitle(this.profileGalleryView.getCurrentItem()).toString();
            this.lastCurrentItem = this.profileGalleryView.getCurrentItem();
        }
        return this.title;
    }

    public void onDown(boolean left) {
        this.pressedOverlayVisible[!left] = true;
        postInvalidateOnAnimation();
    }

    public void onRelease() {
        Arrays.fill(this.pressedOverlayVisible, false);
        postInvalidateOnAnimation();
    }

    public void onPhotosLoaded() {
    }

    public void onVideoSet() {
        invalidate();
    }

    public void setProfileGalleryView(ProfileGalleryView profileGalleryView2) {
        this.profileGalleryView = profileGalleryView2;
    }

    public ProfileGalleryView getProfileGalleryView() {
        return this.profileGalleryView;
    }
}
