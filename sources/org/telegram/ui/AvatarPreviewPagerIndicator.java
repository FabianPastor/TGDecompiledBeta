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
    TextPaint textPaint;
    String title;
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
        ofFloat.addUpdateListener(new AvatarPreviewPagerIndicator$$ExternalSyntheticLambda0(this));
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
        TextPaint textPaint2 = new TextPaint(1);
        this.textPaint = textPaint2;
        textPaint2.setColor(-1);
        this.textPaint.setTypeface(Typeface.SANS_SERIF);
        this.textPaint.setTextAlign(Paint.Align.CENTER);
        this.textPaint.setTextSize(AndroidUtilities.dpf2(15.0f));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(ValueAnimator valueAnimator) {
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
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        this.path.reset();
        this.rectF.set(0.0f, 0.0f, (float) getMeasuredHeight(), (float) getMeasuredWidth());
        this.path.addRoundRect(this.rectF, new float[]{(float) AndroidUtilities.dp(13.0f), (float) AndroidUtilities.dp(13.0f), (float) AndroidUtilities.dp(13.0f), (float) AndroidUtilities.dp(13.0f), 0.0f, 0.0f, 0.0f, 0.0f}, Path.Direction.CCW);
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
    /* JADX WARNING: Removed duplicated region for block: B:116:0x02f3  */
    /* JADX WARNING: Removed duplicated region for block: B:117:0x02f6  */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x01c5  */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x01e9  */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x0201  */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x0204  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r24) {
        /*
            r23 = this;
            r0 = r23
            r1 = r24
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
            android.graphics.drawable.GradientDrawable r3 = r0.topOverlayGradient
            r3.draw(r1)
            android.graphics.Rect r3 = r0.topOverlayRect
            android.graphics.Paint r7 = r0.backgroundPaint
            r1.drawRect(r3, r7)
            org.telegram.ui.Components.ProfileGalleryView r3 = r0.profileGalleryView
            int r3 = r3.getRealCount()
            org.telegram.ui.Components.ProfileGalleryView r7 = r0.profileGalleryView
            int r7 = r7.getRealPosition()
            r0.selectedPosition = r7
            float[] r7 = r0.alphas
            if (r7 == 0) goto L_0x004a
            int r7 = r7.length
            if (r7 == r3) goto L_0x0051
        L_0x004a:
            float[] r7 = new float[r3]
            r0.alphas = r7
            java.util.Arrays.fill(r7, r6)
        L_0x0051:
            long r7 = android.os.SystemClock.elapsedRealtime()
            long r9 = r0.lastTime
            long r9 = r7 - r9
            r11 = 0
            int r13 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1))
            if (r13 < 0) goto L_0x0065
            r11 = 20
            int r13 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1))
            if (r13 <= 0) goto L_0x0067
        L_0x0065:
            r9 = 17
        L_0x0067:
            r0.lastTime = r7
            r8 = 1090519040(0x41000000, float:8.0)
            r11 = 20
            r12 = 1
            r13 = 1065353216(0x3var_, float:1.0)
            if (r3 <= r12) goto L_0x0271
            if (r3 > r11) goto L_0x0271
            int r14 = r0.overlayCountVisible
            r15 = 3
            if (r14 != 0) goto L_0x007e
            r0.alpha = r6
            r0.overlayCountVisible = r15
            goto L_0x0084
        L_0x007e:
            if (r14 != r12) goto L_0x0084
            r0.alpha = r6
            r0.overlayCountVisible = r5
        L_0x0084:
            int r14 = r0.overlayCountVisible
            r16 = 1118437376(0x42aa0000, float:85.0)
            if (r14 != r5) goto L_0x009e
            android.graphics.Paint r14 = r0.barPaint
            float r2 = r0.alpha
            float r2 = r2 * r16
            int r2 = (int) r2
            r14.setAlpha(r2)
            android.graphics.Paint r2 = r0.selectedBarPaint
            float r14 = r0.alpha
            float r14 = r14 * r4
            int r14 = (int) r14
            r2.setAlpha(r14)
        L_0x009e:
            int r2 = r23.getMeasuredWidth()
            r14 = 1092616192(0x41200000, float:10.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r2 = r2 - r14
            int r14 = r3 + -1
            int r14 = r14 * 2
            float r14 = (float) r14
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r2 = r2 - r14
            int r2 = r2 / r3
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r12 = 0
            r17 = 0
        L_0x00bb:
            r18 = 1140457472(0x43fa0000, float:500.0)
            if (r12 >= r3) goto L_0x0216
            int r19 = r12 * 2
            int r8 = r19 + 5
            float r8 = (float) r8
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r19 = r2 * r12
            int r8 = r8 + r19
            r19 = 85
            int r11 = r0.previousSelectedPotision
            r20 = 80
            r21 = 1073741824(0x40000000, float:2.0)
            if (r12 != r11) goto L_0x012b
            float r11 = r0.previousSelectedProgress
            float r11 = r11 - r13
            float r11 = java.lang.Math.abs(r11)
            r22 = 953267991(0x38d1b717, float:1.0E-4)
            int r11 = (r11 > r22 ? 1 : (r11 == r22 ? 0 : -1))
            if (r11 <= 0) goto L_0x012b
            float r11 = r0.previousSelectedProgress
            r24.save()
            float r7 = (float) r8
            float r5 = (float) r2
            float r5 = r5 * r11
            float r5 = r5 + r7
            float r4 = (float) r14
            int r15 = r8 + r2
            float r15 = (float) r15
            int r17 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r6 = r14 + r17
            float r6 = (float) r6
            r1.clipRect(r5, r4, r15, r6)
            android.graphics.RectF r5 = r0.rect
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r6 = r6 + r14
            float r6 = (float) r6
            r5.set(r7, r4, r15, r6)
            android.graphics.Paint r4 = r0.barPaint
            float r5 = r0.alpha
            float r5 = r5 * r16
            int r5 = (int) r5
            r4.setAlpha(r5)
            android.graphics.RectF r4 = r0.rect
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r5 = (float) r5
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r6 = (float) r6
            android.graphics.Paint r7 = r0.barPaint
            r1.drawRoundRect(r4, r5, r6, r7)
            r24.restore()
        L_0x0125:
            r4 = 80
            r17 = 1
            goto L_0x01b0
        L_0x012b:
            int r4 = r0.selectedPosition
            if (r12 != r4) goto L_0x01ac
            org.telegram.ui.Components.ProfileGalleryView r4 = r0.profileGalleryView
            boolean r4 = r4.isCurrentItemVideo()
            if (r4 == 0) goto L_0x01aa
            org.telegram.ui.Components.ProfileGalleryView r4 = r0.profileGalleryView
            float r11 = r4.getCurrentItemProgress()
            r0.currentProgress = r11
            r4 = 0
            int r5 = (r11 > r4 ? 1 : (r11 == r4 ? 0 : -1))
            if (r5 > 0) goto L_0x014c
            org.telegram.ui.Components.ProfileGalleryView r5 = r0.profileGalleryView
            boolean r5 = r5.isLoadingCurrentVideo()
            if (r5 != 0) goto L_0x0152
        L_0x014c:
            float r5 = r0.currentLoadingAnimationProgress
            int r5 = (r5 > r4 ? 1 : (r5 == r4 ? 0 : -1))
            if (r5 <= 0) goto L_0x0175
        L_0x0152:
            float r4 = r0.currentLoadingAnimationProgress
            int r5 = r0.currentLoadingAnimationDirection
            long r6 = (long) r5
            long r6 = r6 * r9
            float r6 = (float) r6
            float r6 = r6 / r18
            float r4 = r4 + r6
            r0.currentLoadingAnimationProgress = r4
            int r6 = (r4 > r13 ? 1 : (r4 == r13 ? 0 : -1))
            if (r6 <= 0) goto L_0x016a
            r0.currentLoadingAnimationProgress = r13
            int r5 = r5 * -1
            r0.currentLoadingAnimationDirection = r5
            goto L_0x0175
        L_0x016a:
            r6 = 0
            int r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r4 > 0) goto L_0x0175
            r0.currentLoadingAnimationProgress = r6
            int r5 = r5 * -1
            r0.currentLoadingAnimationDirection = r5
        L_0x0175:
            android.graphics.RectF r4 = r0.rect
            float r5 = (float) r8
            float r6 = (float) r14
            int r7 = r8 + r2
            float r7 = (float) r7
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r15 = r15 + r14
            float r15 = (float) r15
            r4.set(r5, r6, r7, r15)
            android.graphics.Paint r4 = r0.barPaint
            r5 = 1111490560(0x42400000, float:48.0)
            float r6 = r0.currentLoadingAnimationProgress
            float r6 = r6 * r5
            float r6 = r6 + r16
            float r5 = r0.alpha
            float r6 = r6 * r5
            int r5 = (int) r6
            r4.setAlpha(r5)
            android.graphics.RectF r4 = r0.rect
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r5 = (float) r5
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r6 = (float) r6
            android.graphics.Paint r7 = r0.barPaint
            r1.drawRoundRect(r4, r5, r6, r7)
            goto L_0x0125
        L_0x01aa:
            r0.currentProgress = r13
        L_0x01ac:
            r4 = 85
            r11 = 1065353216(0x3var_, float:1.0)
        L_0x01b0:
            android.graphics.RectF r5 = r0.rect
            float r6 = (float) r8
            float r7 = (float) r14
            float r8 = (float) r2
            float r8 = r8 * r11
            float r8 = r8 + r6
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r11 = r11 + r14
            float r11 = (float) r11
            r5.set(r6, r7, r8, r11)
            int r5 = r0.selectedPosition
            if (r12 == r5) goto L_0x01e9
            int r5 = r0.overlayCountVisible
            r6 = 3
            if (r5 != r6) goto L_0x01e6
            android.graphics.Paint r5 = r0.barPaint
            float r4 = (float) r4
            org.telegram.ui.Components.CubicBezierInterpolator r6 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_BOTH
            float[] r7 = r0.alphas
            r7 = r7[r12]
            float r6 = r6.getInterpolation(r7)
            r7 = 1132396544(0x437var_, float:255.0)
            float r4 = org.telegram.messenger.AndroidUtilities.lerp(r4, r7, r6)
            float r6 = r0.alpha
            float r4 = r4 * r6
            int r4 = (int) r4
            r5.setAlpha(r4)
            goto L_0x01f1
        L_0x01e6:
            r7 = 1132396544(0x437var_, float:255.0)
            goto L_0x01f1
        L_0x01e9:
            r7 = 1132396544(0x437var_, float:255.0)
            float[] r4 = r0.alphas
            r5 = 1061158912(0x3var_, float:0.75)
            r4[r12] = r5
        L_0x01f1:
            android.graphics.RectF r4 = r0.rect
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r5 = (float) r5
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r6 = (float) r6
            int r8 = r0.selectedPosition
            if (r12 != r8) goto L_0x0204
            android.graphics.Paint r8 = r0.selectedBarPaint
            goto L_0x0206
        L_0x0204:
            android.graphics.Paint r8 = r0.barPaint
        L_0x0206:
            r1.drawRoundRect(r4, r5, r6, r8)
            int r12 = r12 + 1
            r4 = 1132396544(0x437var_, float:255.0)
            r5 = 2
            r6 = 0
            r8 = 1090519040(0x41000000, float:8.0)
            r11 = 20
            r15 = 3
            goto L_0x00bb
        L_0x0216:
            int r2 = r0.overlayCountVisible
            r4 = 2
            if (r2 != r4) goto L_0x0237
            float r2 = r0.alpha
            int r4 = (r2 > r13 ? 1 : (r2 == r13 ? 0 : -1))
            if (r4 >= 0) goto L_0x0233
            float r4 = (float) r9
            r5 = 1127481344(0x43340000, float:180.0)
            float r4 = r4 / r5
            float r2 = r2 + r4
            r0.alpha = r2
            int r2 = (r2 > r13 ? 1 : (r2 == r13 ? 0 : -1))
            if (r2 <= 0) goto L_0x022e
            r0.alpha = r13
        L_0x022e:
            r2 = 20
            r17 = 1
            goto L_0x0275
        L_0x0233:
            r4 = 3
            r0.overlayCountVisible = r4
            goto L_0x026e
        L_0x0237:
            r4 = 3
            if (r2 != r4) goto L_0x026e
            r2 = 0
        L_0x023b:
            float[] r4 = r0.alphas
            int r5 = r4.length
            if (r2 >= r5) goto L_0x026e
            int r5 = r0.selectedPosition
            r6 = -1
            if (r2 == r5) goto L_0x0265
            r5 = r4[r2]
            r7 = 0
            int r5 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r5 <= 0) goto L_0x0265
            r5 = r4[r2]
            float r8 = (float) r9
            float r8 = r8 / r18
            float r5 = r5 - r8
            r4[r2] = r5
            r5 = r4[r2]
            int r5 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r5 > 0) goto L_0x0262
            r4[r2] = r7
            int r4 = r0.previousSelectedPotision
            if (r2 != r4) goto L_0x0262
            r0.previousSelectedPotision = r6
        L_0x0262:
            r17 = 1
            goto L_0x026b
        L_0x0265:
            int r4 = r0.previousSelectedPotision
            if (r2 != r4) goto L_0x026b
            r0.previousSelectedPotision = r6
        L_0x026b:
            int r2 = r2 + 1
            goto L_0x023b
        L_0x026e:
            r2 = 20
            goto L_0x0275
        L_0x0271:
            r2 = 20
            r17 = 0
        L_0x0275:
            if (r3 > r2) goto L_0x027e
            float r2 = r0.progressToCounter
            r4 = 0
            int r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r2 == 0) goto L_0x0336
        L_0x027e:
            android.text.TextPaint r2 = r0.textPaint
            java.lang.String r4 = r23.getCurrentTitle()
            float r2 = r2.measureText(r4)
            android.graphics.RectF r4 = r0.indicatorRect
            int r5 = r23.getMeasuredWidth()
            r6 = 1090519040(0x41000000, float:8.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r7
            float r5 = (float) r5
            r4.right = r5
            android.graphics.RectF r4 = r0.indicatorRect
            float r5 = r4.right
            r6 = 1098907648(0x41800000, float:16.0)
            float r6 = org.telegram.messenger.AndroidUtilities.dpf2(r6)
            float r2 = r2 + r6
            float r5 = r5 - r2
            r4.left = r5
            android.graphics.RectF r2 = r0.indicatorRect
            r4 = 1090519040(0x41000000, float:8.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            r2.top = r4
            android.graphics.RectF r2 = r0.indicatorRect
            float r4 = r2.top
            r5 = 1104150528(0x41d00000, float:26.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            float r4 = r4 + r5
            r2.bottom = r4
            r2 = 1094713344(0x41400000, float:12.0)
            float r2 = org.telegram.messenger.AndroidUtilities.dpf2(r2)
            r24.save()
            r4 = 20
            if (r3 <= r4) goto L_0x02ce
            r3 = 1
            goto L_0x02cf
        L_0x02ce:
            r3 = 0
        L_0x02cf:
            r4 = 1125515264(0x43160000, float:150.0)
            if (r3 == 0) goto L_0x02df
            float r5 = r0.progressToCounter
            int r6 = (r5 > r13 ? 1 : (r5 == r13 ? 0 : -1))
            if (r6 == 0) goto L_0x02df
            float r3 = (float) r9
            float r3 = r3 / r4
            float r5 = r5 + r3
            r0.progressToCounter = r5
            goto L_0x02ed
        L_0x02df:
            if (r3 != 0) goto L_0x02ed
            float r3 = r0.progressToCounter
            r5 = 0
            int r6 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r6 == 0) goto L_0x02ed
            float r5 = (float) r9
            float r5 = r5 / r4
            float r3 = r3 - r5
            r0.progressToCounter = r3
        L_0x02ed:
            float r3 = r0.progressToCounter
            int r4 = (r3 > r13 ? 1 : (r3 == r13 ? 0 : -1))
            if (r4 < 0) goto L_0x02f6
            r0.progressToCounter = r13
            goto L_0x0301
        L_0x02f6:
            r4 = 0
            int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r3 > 0) goto L_0x02fe
            r0.progressToCounter = r4
            goto L_0x0301
        L_0x02fe:
            r23.invalidate()
        L_0x0301:
            float r3 = r0.progressToCounter
            android.graphics.RectF r4 = r0.indicatorRect
            float r4 = r4.centerX()
            android.graphics.RectF r5 = r0.indicatorRect
            float r5 = r5.centerY()
            r1.scale(r3, r3, r4, r5)
            android.graphics.RectF r3 = r0.indicatorRect
            android.graphics.Paint r4 = r0.backgroundPaint
            r1.drawRoundRect(r3, r2, r2, r4)
            java.lang.String r2 = r23.getCurrentTitle()
            android.graphics.RectF r3 = r0.indicatorRect
            float r3 = r3.centerX()
            android.graphics.RectF r4 = r0.indicatorRect
            float r4 = r4.top
            r5 = 1100218368(0x41940000, float:18.5)
            float r5 = org.telegram.messenger.AndroidUtilities.dpf2(r5)
            float r4 = r4 + r5
            android.text.TextPaint r5 = r0.textPaint
            r1.drawText(r2, r3, r4, r5)
            r24.restore()
        L_0x0336:
            r1 = 2
            r2 = 0
        L_0x0338:
            if (r2 >= r1) goto L_0x0381
            boolean[] r3 = r0.pressedOverlayVisible
            boolean r3 = r3[r2]
            if (r3 == 0) goto L_0x035f
            float[] r3 = r0.pressedOverlayAlpha
            r4 = r3[r2]
            int r4 = (r4 > r13 ? 1 : (r4 == r13 ? 0 : -1))
            if (r4 >= 0) goto L_0x035d
            r4 = r3[r2]
            float r5 = (float) r9
            r6 = 1127481344(0x43340000, float:180.0)
            float r5 = r5 / r6
            float r4 = r4 + r5
            r3[r2] = r4
            r4 = r3[r2]
            int r4 = (r4 > r13 ? 1 : (r4 == r13 ? 0 : -1))
            if (r4 <= 0) goto L_0x0359
            r3[r2] = r13
        L_0x0359:
            r5 = 0
            r7 = 1127481344(0x43340000, float:180.0)
            goto L_0x0379
        L_0x035d:
            r5 = 0
            goto L_0x037c
        L_0x035f:
            float[] r3 = r0.pressedOverlayAlpha
            r4 = r3[r2]
            r5 = 0
            int r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            if (r4 <= 0) goto L_0x037c
            r4 = r3[r2]
            float r6 = (float) r9
            r7 = 1127481344(0x43340000, float:180.0)
            float r6 = r6 / r7
            float r4 = r4 - r6
            r3[r2] = r4
            r4 = r3[r2]
            int r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            if (r4 >= 0) goto L_0x0379
            r3[r2] = r5
        L_0x0379:
            r17 = 1
            goto L_0x037e
        L_0x037c:
            r7 = 1127481344(0x43340000, float:180.0)
        L_0x037e:
            int r2 = r2 + 1
            goto L_0x0338
        L_0x0381:
            if (r17 == 0) goto L_0x0386
            r23.postInvalidateOnAnimation()
        L_0x0386:
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

    public ProfileGalleryView getProfileGalleryView() {
        return this.profileGalleryView;
    }
}
