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
    public /* synthetic */ void m2713lambda$new$0$orgtelegramuiAvatarPreviewPagerIndicator(ValueAnimator anim) {
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
    /* JADX WARNING: Removed duplicated region for block: B:116:0x032b  */
    /* JADX WARNING: Removed duplicated region for block: B:117:0x032e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r26) {
        /*
            r25 = this;
            r0 = r25
            r1 = r26
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
            if (r2 <= r14) goto L_0x02a4
            if (r2 > r13) goto L_0x02a4
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
            int r3 = (int) r14
            r13.setAlpha(r3)
        L_0x009f:
            int r3 = r25.getMeasuredWidth()
            r13 = 1092616192(0x41200000, float:10.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r3 = r3 - r13
            int r13 = r2 + -1
            int r13 = r13 * 2
            float r13 = (float) r13
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r3 = r3 - r13
            int r3 = r3 / r2
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r14 = 0
        L_0x00ba:
            r17 = 1140457472(0x43fa0000, float:500.0)
            if (r14 >= r2) goto L_0x0245
            int r18 = r14 * 2
            int r12 = r18 + 5
            float r12 = (float) r12
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r18 = r3 * r14
            int r12 = r12 + r18
            r18 = 85
            int r4 = r0.previousSelectedPotision
            r19 = 1073741824(0x40000000, float:2.0)
            if (r14 != r4) goto L_0x0139
            float r4 = r0.previousSelectedProgress
            float r4 = r4 - r15
            float r4 = java.lang.Math.abs(r4)
            r20 = 953267991(0x38d1b717, float:1.0E-4)
            int r4 = (r4 > r20 ? 1 : (r4 == r20 ? 0 : -1))
            if (r4 <= 0) goto L_0x0139
            float r4 = r0.previousSelectedProgress
            r26.save()
            float r11 = (float) r12
            float r5 = (float) r3
            float r5 = r5 * r4
            float r11 = r11 + r5
            float r5 = (float) r13
            int r15 = r12 + r3
            float r15 = (float) r15
            int r17 = org.telegram.messenger.AndroidUtilities.dp(r19)
            r21 = r4
            int r4 = r13 + r17
            float r4 = (float) r4
            r1.clipRect(r11, r5, r15, r4)
            android.graphics.RectF r4 = r0.rect
            float r5 = (float) r12
            float r11 = (float) r13
            int r15 = r12 + r3
            float r15 = (float) r15
            int r17 = org.telegram.messenger.AndroidUtilities.dp(r19)
            r22 = r6
            int r6 = r13 + r17
            float r6 = (float) r6
            r4.set(r5, r11, r15, r6)
            android.graphics.Paint r4 = r0.barPaint
            float r5 = r0.alpha
            float r5 = r5 * r16
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
            r18 = 80
            r26.restore()
            r6 = 1
            r23 = r7
            r5 = r18
            r4 = r21
            goto L_0x01dd
        L_0x0139:
            r22 = r6
            int r4 = r0.selectedPosition
            if (r14 != r4) goto L_0x01d5
            org.telegram.ui.Components.ProfileGalleryView r4 = r0.profileGalleryView
            boolean r4 = r4.isCurrentItemVideo()
            if (r4 == 0) goto L_0x01c9
            org.telegram.ui.Components.ProfileGalleryView r4 = r0.profileGalleryView
            float r4 = r4.getCurrentItemProgress()
            r0.currentProgress = r4
            r5 = 0
            int r6 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            if (r6 > 0) goto L_0x015c
            org.telegram.ui.Components.ProfileGalleryView r5 = r0.profileGalleryView
            boolean r5 = r5.isLoadingCurrentVideo()
            if (r5 != 0) goto L_0x0163
        L_0x015c:
            float r5 = r0.currentLoadingAnimationProgress
            r6 = 0
            int r5 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
            if (r5 <= 0) goto L_0x018b
        L_0x0163:
            float r5 = r0.currentLoadingAnimationProgress
            int r6 = r0.currentLoadingAnimationDirection
            r23 = r7
            long r7 = (long) r6
            long r7 = r7 * r9
            float r7 = (float) r7
            float r7 = r7 / r17
            float r5 = r5 + r7
            r0.currentLoadingAnimationProgress = r5
            r7 = 1065353216(0x3var_, float:1.0)
            int r8 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r8 <= 0) goto L_0x017f
            r0.currentLoadingAnimationProgress = r7
            int r6 = r6 * -1
            r0.currentLoadingAnimationDirection = r6
            goto L_0x018d
        L_0x017f:
            r7 = 0
            int r5 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r5 > 0) goto L_0x018d
            r0.currentLoadingAnimationProgress = r7
            int r6 = r6 * -1
            r0.currentLoadingAnimationDirection = r6
            goto L_0x018d
        L_0x018b:
            r23 = r7
        L_0x018d:
            android.graphics.RectF r5 = r0.rect
            float r6 = (float) r12
            float r7 = (float) r13
            int r8 = r12 + r3
            float r8 = (float) r8
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r11 = r11 + r13
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
            r18 = 80
            r6 = r5
            r5 = r18
            goto L_0x01dd
        L_0x01c9:
            r23 = r7
            r6 = 1065353216(0x3var_, float:1.0)
            r0.currentProgress = r6
            r4 = r6
            r5 = r18
            r6 = r22
            goto L_0x01dd
        L_0x01d5:
            r23 = r7
            r4 = 1065353216(0x3var_, float:1.0)
            r5 = r18
            r6 = r22
        L_0x01dd:
            android.graphics.RectF r7 = r0.rect
            float r8 = (float) r12
            float r11 = (float) r13
            float r15 = (float) r12
            r17 = r6
            float r6 = (float) r3
            float r6 = r6 * r4
            float r15 = r15 + r6
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r6 = r6 + r13
            float r6 = (float) r6
            r7.set(r8, r11, r15, r6)
            int r6 = r0.selectedPosition
            if (r14 == r6) goto L_0x0216
            int r6 = r0.overlayCountVisible
            r7 = 3
            if (r6 != r7) goto L_0x021c
            android.graphics.Paint r6 = r0.barPaint
            r7 = 255(0xff, float:3.57E-43)
            org.telegram.ui.Components.CubicBezierInterpolator r8 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_BOTH
            float[] r11 = r0.alphas
            r11 = r11[r14]
            float r8 = r8.getInterpolation(r11)
            int r7 = org.telegram.messenger.AndroidUtilities.lerp((int) r5, (int) r7, (float) r8)
            float r7 = (float) r7
            float r8 = r0.alpha
            float r7 = r7 * r8
            int r7 = (int) r7
            r6.setAlpha(r7)
            goto L_0x021c
        L_0x0216:
            float[] r6 = r0.alphas
            r7 = 1061158912(0x3var_, float:0.75)
            r6[r14] = r7
        L_0x021c:
            android.graphics.RectF r6 = r0.rect
            r7 = 1065353216(0x3var_, float:1.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r8 = (float) r8
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r7 = (float) r11
            int r11 = r0.selectedPosition
            if (r14 != r11) goto L_0x0231
            android.graphics.Paint r11 = r0.selectedBarPaint
            goto L_0x0233
        L_0x0231:
            android.graphics.Paint r11 = r0.barPaint
        L_0x0233:
            r1.drawRoundRect(r6, r8, r7, r11)
            int r14 = r14 + 1
            r6 = r17
            r7 = r23
            r4 = 2
            r5 = 0
            r11 = 3
            r12 = 1090519040(0x41000000, float:8.0)
            r15 = 1065353216(0x3var_, float:1.0)
            goto L_0x00ba
        L_0x0245:
            r22 = r6
            r23 = r7
            int r4 = r0.overlayCountVisible
            r5 = 2
            if (r4 != r5) goto L_0x0269
            float r4 = r0.alpha
            r5 = 1065353216(0x3var_, float:1.0)
            int r6 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            if (r6 >= 0) goto L_0x0265
            float r6 = (float) r9
            r7 = 1127481344(0x43340000, float:180.0)
            float r6 = r6 / r7
            float r4 = r4 + r6
            r0.alpha = r4
            int r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            if (r4 <= 0) goto L_0x0263
            r0.alpha = r5
        L_0x0263:
            r6 = 1
            goto L_0x02a6
        L_0x0265:
            r5 = 3
            r0.overlayCountVisible = r5
            goto L_0x02a1
        L_0x0269:
            r5 = 3
            if (r4 != r5) goto L_0x02a1
            r4 = 0
            r6 = r22
        L_0x026f:
            float[] r5 = r0.alphas
            int r7 = r5.length
            if (r4 >= r7) goto L_0x02a6
            int r7 = r0.selectedPosition
            r8 = -1
            if (r4 == r7) goto L_0x0298
            r7 = r5[r4]
            r11 = 0
            int r7 = (r7 > r11 ? 1 : (r7 == r11 ? 0 : -1))
            if (r7 <= 0) goto L_0x0298
            r7 = r5[r4]
            float r12 = (float) r9
            float r12 = r12 / r17
            float r7 = r7 - r12
            r5[r4] = r7
            r7 = r5[r4]
            int r7 = (r7 > r11 ? 1 : (r7 == r11 ? 0 : -1))
            if (r7 > 0) goto L_0x0296
            r5[r4] = r11
            int r5 = r0.previousSelectedPotision
            if (r4 != r5) goto L_0x0296
            r0.previousSelectedPotision = r8
        L_0x0296:
            r6 = 1
            goto L_0x029e
        L_0x0298:
            int r5 = r0.previousSelectedPotision
            if (r4 != r5) goto L_0x029e
            r0.previousSelectedPotision = r8
        L_0x029e:
            int r4 = r4 + 1
            goto L_0x026f
        L_0x02a1:
            r6 = r22
            goto L_0x02a6
        L_0x02a4:
            r23 = r7
        L_0x02a6:
            r3 = 20
            if (r2 > r3) goto L_0x02b1
            float r3 = r0.progressToCounter
            r4 = 0
            int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r3 == 0) goto L_0x036e
        L_0x02b1:
            android.text.TextPaint r3 = r0.textPaint
            java.lang.String r4 = r25.getCurrentTitle()
            float r3 = r3.measureText(r4)
            android.graphics.RectF r4 = r0.indicatorRect
            int r5 = r25.getMeasuredWidth()
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
            r26.save()
            r5 = 20
            if (r2 <= r5) goto L_0x0301
            r14 = 1
            goto L_0x0302
        L_0x0301:
            r14 = 0
        L_0x0302:
            r5 = r14
            r7 = 1125515264(0x43160000, float:150.0)
            if (r5 == 0) goto L_0x0315
            float r8 = r0.progressToCounter
            r11 = 1065353216(0x3var_, float:1.0)
            int r12 = (r8 > r11 ? 1 : (r8 == r11 ? 0 : -1))
            if (r12 == 0) goto L_0x0315
            float r11 = (float) r9
            float r11 = r11 / r7
            float r8 = r8 + r11
            r0.progressToCounter = r8
            goto L_0x0323
        L_0x0315:
            if (r5 != 0) goto L_0x0323
            float r8 = r0.progressToCounter
            r11 = 0
            int r12 = (r8 > r11 ? 1 : (r8 == r11 ? 0 : -1))
            if (r12 == 0) goto L_0x0323
            float r11 = (float) r9
            float r11 = r11 / r7
            float r8 = r8 - r11
            r0.progressToCounter = r8
        L_0x0323:
            float r7 = r0.progressToCounter
            r8 = 1065353216(0x3var_, float:1.0)
            int r11 = (r7 > r8 ? 1 : (r7 == r8 ? 0 : -1))
            if (r11 < 0) goto L_0x032e
            r0.progressToCounter = r8
            goto L_0x0339
        L_0x032e:
            r8 = 0
            int r7 = (r7 > r8 ? 1 : (r7 == r8 ? 0 : -1))
            if (r7 > 0) goto L_0x0336
            r0.progressToCounter = r8
            goto L_0x0339
        L_0x0336:
            r25.invalidate()
        L_0x0339:
            float r7 = r0.progressToCounter
            android.graphics.RectF r8 = r0.indicatorRect
            float r8 = r8.centerX()
            android.graphics.RectF r11 = r0.indicatorRect
            float r11 = r11.centerY()
            r1.scale(r7, r7, r8, r11)
            android.graphics.RectF r7 = r0.indicatorRect
            android.graphics.Paint r8 = r0.backgroundPaint
            r1.drawRoundRect(r7, r4, r4, r8)
            java.lang.String r7 = r25.getCurrentTitle()
            android.graphics.RectF r8 = r0.indicatorRect
            float r8 = r8.centerX()
            android.graphics.RectF r11 = r0.indicatorRect
            float r11 = r11.top
            r12 = 1100218368(0x41940000, float:18.5)
            float r12 = org.telegram.messenger.AndroidUtilities.dpf2(r12)
            float r11 = r11 + r12
            android.text.TextPaint r12 = r0.textPaint
            r1.drawText(r7, r8, r11, r12)
            r26.restore()
        L_0x036e:
            r3 = 0
        L_0x036f:
            r4 = 2
            if (r3 >= r4) goto L_0x03c1
            boolean[] r5 = r0.pressedOverlayVisible
            boolean r5 = r5[r3]
            if (r5 == 0) goto L_0x039d
            float[] r5 = r0.pressedOverlayAlpha
            r7 = r5[r3]
            r8 = 1065353216(0x3var_, float:1.0)
            int r7 = (r7 > r8 ? 1 : (r7 == r8 ? 0 : -1))
            if (r7 >= 0) goto L_0x0399
            r7 = r5[r3]
            float r11 = (float) r9
            r12 = 1127481344(0x43340000, float:180.0)
            float r11 = r11 / r12
            float r7 = r7 + r11
            r5[r3] = r7
            r7 = r5[r3]
            int r7 = (r7 > r8 ? 1 : (r7 == r8 ? 0 : -1))
            if (r7 <= 0) goto L_0x0393
            r5[r3] = r8
        L_0x0393:
            r5 = 1
            r6 = r5
            r11 = 0
            r13 = 1127481344(0x43340000, float:180.0)
            goto L_0x03be
        L_0x0399:
            r11 = 0
            r13 = 1127481344(0x43340000, float:180.0)
            goto L_0x03be
        L_0x039d:
            r8 = 1065353216(0x3var_, float:1.0)
            float[] r5 = r0.pressedOverlayAlpha
            r7 = r5[r3]
            r11 = 0
            int r7 = (r7 > r11 ? 1 : (r7 == r11 ? 0 : -1))
            if (r7 <= 0) goto L_0x03bc
            r7 = r5[r3]
            float r12 = (float) r9
            r13 = 1127481344(0x43340000, float:180.0)
            float r12 = r12 / r13
            float r7 = r7 - r12
            r5[r3] = r7
            r7 = r5[r3]
            int r7 = (r7 > r11 ? 1 : (r7 == r11 ? 0 : -1))
            if (r7 >= 0) goto L_0x03b9
            r5[r3] = r11
        L_0x03b9:
            r5 = 1
            r6 = r5
            goto L_0x03be
        L_0x03bc:
            r13 = 1127481344(0x43340000, float:180.0)
        L_0x03be:
            int r3 = r3 + 1
            goto L_0x036f
        L_0x03c1:
            if (r6 == 0) goto L_0x03c6
            r25.postInvalidateOnAnimation()
        L_0x03c6:
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
