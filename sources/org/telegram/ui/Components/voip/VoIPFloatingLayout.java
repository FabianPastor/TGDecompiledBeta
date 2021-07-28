package org.telegram.ui.Components.voip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewOutlineProvider;
import android.view.ViewParent;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.CubicBezierInterpolator;

public class VoIPFloatingLayout extends FrameLayout {
    private final float FLOATING_MODE_SCALE = 0.23f;
    private boolean active = true;
    public boolean alwaysFloating;
    public int bottomOffset;
    float bottomPadding;
    /* access modifiers changed from: private */
    public VoIPFloatingLayoutDelegate delegate;
    /* access modifiers changed from: private */
    public boolean floatingMode;
    int lastH;
    WindowInsets lastInsets;
    int lastW;
    float leftPadding;
    /* access modifiers changed from: private */
    public boolean measuredAsFloatingMode;
    boolean moving;
    ValueAnimator mutedAnimator;
    Drawable mutedDrawable;
    Paint mutedPaint = new Paint(1);
    float mutedProgress = 0.0f;
    private ValueAnimator.AnimatorUpdateListener mutedUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
            VoIPFloatingLayout.this.lambda$new$0$VoIPFloatingLayout(valueAnimator);
        }
    };
    /* access modifiers changed from: private */
    public float overrideCornerRadius = -1.0f;
    final Path path = new Path();
    /* access modifiers changed from: private */
    public ValueAnimator.AnimatorUpdateListener progressUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            VoIPFloatingLayout.this.toFloatingModeProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            if (VoIPFloatingLayout.this.delegate != null) {
                VoIPFloatingLayoutDelegate access$000 = VoIPFloatingLayout.this.delegate;
                VoIPFloatingLayout voIPFloatingLayout = VoIPFloatingLayout.this;
                access$000.onChange(voIPFloatingLayout.toFloatingModeProgress, voIPFloatingLayout.measuredAsFloatingMode);
            }
            VoIPFloatingLayout.this.invalidate();
        }
    };
    final RectF rectF = new RectF();
    public float relativePositionToSetX = -1.0f;
    float relativePositionToSetY = -1.0f;
    float rightPadding;
    public float savedRelativePositionX;
    public float savedRelativePositionY;
    private boolean setedFloatingMode;
    float starX;
    float starY;
    float startMovingFromX;
    float startMovingFromY;
    long startTime;
    ValueAnimator switchToFloatingModeAnimator;
    /* access modifiers changed from: private */
    public boolean switchingToFloatingMode;
    public boolean switchingToPip;
    View.OnClickListener tapListener;
    float toFloatingModeProgress = 0.0f;
    float topPadding;
    float touchSlop;
    private boolean uiVisible;
    public float updatePositionFromX;
    public float updatePositionFromY;
    final Paint xRefPaint = new Paint(1);

    public interface VoIPFloatingLayoutDelegate {
        void onChange(float f, boolean z);
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$VoIPFloatingLayout(ValueAnimator valueAnimator) {
        this.mutedProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    public VoIPFloatingLayout(Context context) {
        super(context);
        this.touchSlop = (float) ViewConfiguration.get(context).getScaledTouchSlop();
        if (Build.VERSION.SDK_INT >= 21) {
            setOutlineProvider(new ViewOutlineProvider() {
                @TargetApi(21)
                public void getOutline(View view, Outline outline) {
                    if (VoIPFloatingLayout.this.overrideCornerRadius >= 0.0f) {
                        if (VoIPFloatingLayout.this.overrideCornerRadius < 1.0f) {
                            outline.setRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
                        } else {
                            outline.setRoundRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight(), VoIPFloatingLayout.this.overrideCornerRadius);
                        }
                    } else if (!VoIPFloatingLayout.this.floatingMode) {
                        outline.setRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
                    } else {
                        outline.setRoundRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight(), VoIPFloatingLayout.this.floatingMode ? (float) AndroidUtilities.dp(4.0f) : 0.0f);
                    }
                }
            });
            setClipToOutline(true);
        }
        this.mutedPaint.setColor(ColorUtils.setAlphaComponent(-16777216, 102));
        this.mutedDrawable = ContextCompat.getDrawable(context, NUM);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int size = View.MeasureSpec.getSize(i);
        int size2 = View.MeasureSpec.getSize(i2);
        this.measuredAsFloatingMode = false;
        if (this.floatingMode) {
            size = (int) (((float) size) * 0.23f);
            size2 = (int) (((float) size2) * 0.23f);
            this.measuredAsFloatingMode = true;
        } else if (!this.switchingToPip) {
            setTranslationX(0.0f);
            setTranslationY(0.0f);
        }
        VoIPFloatingLayoutDelegate voIPFloatingLayoutDelegate = this.delegate;
        if (voIPFloatingLayoutDelegate != null) {
            voIPFloatingLayoutDelegate.onChange(this.toFloatingModeProgress, this.measuredAsFloatingMode);
        }
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(size2, NUM));
        if (!(getMeasuredHeight() == this.lastH || getMeasuredWidth() == this.lastW)) {
            this.path.reset();
            this.rectF.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
            this.path.addRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), Path.Direction.CW);
            this.path.toggleInverseFillType();
        }
        this.lastH = getMeasuredHeight();
        this.lastW = getMeasuredWidth();
        updatePadding();
    }

    private void updatePadding() {
        float f = 16.0f;
        this.leftPadding = (float) AndroidUtilities.dp(16.0f);
        this.rightPadding = (float) AndroidUtilities.dp(16.0f);
        this.topPadding = (float) (this.uiVisible ? AndroidUtilities.dp(60.0f) : AndroidUtilities.dp(16.0f));
        if (this.uiVisible) {
            f = 100.0f;
        }
        this.bottomPadding = (float) (AndroidUtilities.dp(f) + this.bottomOffset);
    }

    public void setDelegate(VoIPFloatingLayoutDelegate voIPFloatingLayoutDelegate) {
        this.delegate = voIPFloatingLayoutDelegate;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0025, code lost:
        if (r1 != 3) goto L_0x01b3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r10) {
        /*
            r9 = this;
            android.view.ViewParent r0 = r9.getParent()
            boolean r1 = r9.floatingMode
            r2 = 0
            if (r1 == 0) goto L_0x01b4
            boolean r1 = r9.switchingToFloatingMode
            if (r1 != 0) goto L_0x01b4
            boolean r1 = r9.active
            if (r1 != 0) goto L_0x0013
            goto L_0x01b4
        L_0x0013:
            int r1 = r10.getAction()
            r3 = 0
            r5 = 0
            r6 = 1065353216(0x3var_, float:1.0)
            r7 = 1
            if (r1 == 0) goto L_0x016a
            if (r1 == r7) goto L_0x0090
            r8 = 2
            if (r1 == r8) goto L_0x0029
            r10 = 3
            if (r1 == r10) goto L_0x0090
            goto L_0x01b3
        L_0x0029:
            float r1 = r10.getX()
            float r2 = r9.getX()
            float r1 = r1 + r2
            float r2 = r9.starX
            float r1 = r1 - r2
            float r2 = r10.getY()
            float r3 = r9.getY()
            float r2 = r2 + r3
            float r3 = r9.starY
            float r2 = r2 - r3
            boolean r3 = r9.moving
            r4 = 0
            if (r3 != 0) goto L_0x007e
            float r3 = r1 * r1
            float r5 = r2 * r2
            float r3 = r3 + r5
            float r5 = r9.touchSlop
            float r5 = r5 * r5
            int r3 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r3 <= 0) goto L_0x007e
            if (r0 == 0) goto L_0x0058
            r0.requestDisallowInterceptTouchEvent(r7)
        L_0x0058:
            r9.moving = r7
            float r0 = r10.getX()
            float r1 = r9.getX()
            float r0 = r0 + r1
            r9.starX = r0
            float r10 = r10.getY()
            float r0 = r9.getY()
            float r10 = r10 + r0
            r9.starY = r10
            float r10 = r9.getTranslationX()
            r9.startMovingFromX = r10
            float r10 = r9.getTranslationY()
            r9.startMovingFromY = r10
            r1 = 0
            r2 = 0
        L_0x007e:
            boolean r10 = r9.moving
            if (r10 == 0) goto L_0x01b3
            float r10 = r9.startMovingFromX
            float r10 = r10 + r1
            r9.setTranslationX(r10)
            float r10 = r9.startMovingFromY
            float r10 = r10 + r2
            r9.setTranslationY(r10)
            goto L_0x01b3
        L_0x0090:
            if (r0 == 0) goto L_0x0167
            boolean r10 = r9.floatingMode
            if (r10 == 0) goto L_0x0167
            boolean r10 = r9.switchingToFloatingMode
            if (r10 != 0) goto L_0x0167
            r0.requestDisallowInterceptTouchEvent(r2)
            android.view.ViewPropertyAnimator r10 = r9.animate()
            android.view.ViewPropertyAnimator r10 = r10.setListener(r5)
            r10.cancel()
            android.view.ViewPropertyAnimator r10 = r9.animate()
            android.view.ViewPropertyAnimator r10 = r10.scaleX(r6)
            android.view.ViewPropertyAnimator r10 = r10.scaleY(r6)
            android.view.ViewPropertyAnimator r10 = r10.alpha(r6)
            android.view.ViewPropertyAnimator r10 = r10.setStartDelay(r3)
            android.view.View$OnClickListener r0 = r9.tapListener
            if (r0 == 0) goto L_0x00d6
            boolean r0 = r9.moving
            if (r0 != 0) goto L_0x00d6
            long r0 = java.lang.System.currentTimeMillis()
            long r3 = r9.startTime
            long r0 = r0 - r3
            r3 = 200(0xc8, double:9.9E-322)
            int r5 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r5 >= 0) goto L_0x00d6
            android.view.View$OnClickListener r0 = r9.tapListener
            r0.onClick(r9)
        L_0x00d6:
            android.view.ViewParent r0 = r9.getParent()
            android.view.View r0 = (android.view.View) r0
            int r0 = r0.getMeasuredWidth()
            android.view.ViewParent r1 = r9.getParent()
            android.view.View r1 = (android.view.View) r1
            int r1 = r1.getMeasuredHeight()
            float r3 = r9.topPadding
            float r4 = r9.bottomPadding
            int r5 = android.os.Build.VERSION.SDK_INT
            r6 = 20
            if (r5 <= r6) goto L_0x0106
            android.view.WindowInsets r5 = r9.lastInsets
            if (r5 == 0) goto L_0x0106
            int r5 = r5.getSystemWindowInsetTop()
            float r5 = (float) r5
            float r3 = r3 + r5
            android.view.WindowInsets r5 = r9.lastInsets
            int r5 = r5.getSystemWindowInsetBottom()
            float r5 = (float) r5
            float r4 = r4 + r5
        L_0x0106:
            float r5 = r9.getX()
            float r6 = r9.leftPadding
            int r5 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
            if (r5 >= 0) goto L_0x0114
            r10.translationX(r6)
            goto L_0x0132
        L_0x0114:
            float r5 = r9.getX()
            int r6 = r9.getMeasuredWidth()
            float r6 = (float) r6
            float r5 = r5 + r6
            float r6 = (float) r0
            float r8 = r9.rightPadding
            float r6 = r6 - r8
            int r5 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
            if (r5 <= 0) goto L_0x0132
            int r5 = r9.getMeasuredWidth()
            int r0 = r0 - r5
            float r0 = (float) r0
            float r5 = r9.rightPadding
            float r0 = r0 - r5
            r10.translationX(r0)
        L_0x0132:
            float r0 = r9.getY()
            int r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r0 >= 0) goto L_0x013e
            r10.translationY(r3)
            goto L_0x0158
        L_0x013e:
            float r0 = r9.getY()
            int r3 = r9.getMeasuredHeight()
            float r3 = (float) r3
            float r0 = r0 + r3
            float r3 = (float) r1
            float r3 = r3 - r4
            int r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r0 <= 0) goto L_0x0158
            int r0 = r9.getMeasuredHeight()
            int r1 = r1 - r0
            float r0 = (float) r1
            float r0 = r0 - r4
            r10.translationY(r0)
        L_0x0158:
            r0 = 150(0x96, double:7.4E-322)
            android.view.ViewPropertyAnimator r10 = r10.setDuration(r0)
            org.telegram.ui.Components.CubicBezierInterpolator r0 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            android.view.ViewPropertyAnimator r10 = r10.setInterpolator(r0)
            r10.start()
        L_0x0167:
            r9.moving = r2
            goto L_0x01b3
        L_0x016a:
            boolean r0 = r9.floatingMode
            if (r0 == 0) goto L_0x01b3
            boolean r0 = r9.switchingToFloatingMode
            if (r0 != 0) goto L_0x01b3
            long r0 = java.lang.System.currentTimeMillis()
            r9.startTime = r0
            float r0 = r10.getX()
            float r1 = r9.getX()
            float r0 = r0 + r1
            r9.starX = r0
            float r10 = r10.getY()
            float r0 = r9.getY()
            float r10 = r10 + r0
            r9.starY = r10
            android.view.ViewPropertyAnimator r10 = r9.animate()
            android.view.ViewPropertyAnimator r10 = r10.setListener(r5)
            r10.cancel()
            android.view.ViewPropertyAnimator r10 = r9.animate()
            r0 = 1065772646(0x3var_, float:1.05)
            android.view.ViewPropertyAnimator r10 = r10.scaleY(r0)
            android.view.ViewPropertyAnimator r10 = r10.scaleX(r0)
            android.view.ViewPropertyAnimator r10 = r10.alpha(r6)
            android.view.ViewPropertyAnimator r10 = r10.setStartDelay(r3)
            r10.start()
        L_0x01b3:
            return r7
        L_0x01b4:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.VoIPFloatingLayout.onTouchEvent(android.view.MotionEvent):boolean");
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        boolean z;
        if (this.updatePositionFromX >= 0.0f) {
            animate().setListener((Animator.AnimatorListener) null).cancel();
            setTranslationX(this.updatePositionFromX);
            setTranslationY(this.updatePositionFromY);
            setScaleX(1.0f);
            setScaleY(1.0f);
            setAlpha(1.0f);
            this.updatePositionFromX = -1.0f;
            this.updatePositionFromY = -1.0f;
        }
        if (this.relativePositionToSetX >= 0.0f && this.floatingMode && getMeasuredWidth() > 0) {
            setRelativePositionInternal(this.relativePositionToSetX, this.relativePositionToSetY, getMeasuredWidth(), getMeasuredHeight(), false);
            this.relativePositionToSetX = -1.0f;
            this.relativePositionToSetY = -1.0f;
        }
        super.dispatchDraw(canvas);
        if (!this.switchingToFloatingMode && this.floatingMode != (z = this.setedFloatingMode)) {
            setFloatingMode(z, true);
        }
        int measuredWidth = getMeasuredWidth() >> 1;
        int measuredHeight = getMeasuredHeight() - ((int) ((((float) AndroidUtilities.dp(18.0f)) * 1.0f) / getScaleY()));
        canvas.save();
        float f = (float) measuredWidth;
        float f2 = (float) measuredHeight;
        canvas.scale((1.0f / getScaleX()) * this.toFloatingModeProgress * this.mutedProgress, (1.0f / getScaleY()) * this.toFloatingModeProgress * this.mutedProgress, f, f2);
        canvas.drawCircle(f, f2, (float) AndroidUtilities.dp(14.0f), this.mutedPaint);
        Drawable drawable = this.mutedDrawable;
        drawable.setBounds(measuredWidth - (drawable.getIntrinsicWidth() / 2), measuredHeight - (this.mutedDrawable.getIntrinsicHeight() / 2), measuredWidth + (this.mutedDrawable.getIntrinsicWidth() / 2), measuredHeight + (this.mutedDrawable.getIntrinsicHeight() / 2));
        this.mutedDrawable.draw(canvas);
        canvas.restore();
        if (this.switchingToFloatingMode) {
            invalidate();
        }
    }

    public void setInsets(WindowInsets windowInsets) {
        this.lastInsets = windowInsets;
    }

    public void setRelativePosition(float f, float f2) {
        ViewParent parent = getParent();
        if (!this.floatingMode || parent == null || ((View) parent).getMeasuredWidth() > 0 || getMeasuredWidth() == 0 || getMeasuredHeight() == 0) {
            this.relativePositionToSetX = f;
            this.relativePositionToSetY = f2;
            return;
        }
        setRelativePositionInternal(f, f2, getMeasuredWidth(), getMeasuredHeight(), true);
    }

    public void setUiVisible(boolean z) {
        if (getParent() == null) {
            this.uiVisible = z;
        } else {
            this.uiVisible = z;
        }
    }

    public void setBottomOffset(int i, boolean z) {
        if (getParent() == null || !z) {
            this.bottomOffset = i;
        } else {
            this.bottomOffset = i;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x001b, code lost:
        r4 = r6.lastInsets;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setRelativePositionInternal(float r7, float r8, int r9, int r10, boolean r11) {
        /*
            r6 = this;
            android.view.ViewParent r0 = r6.getParent()
            if (r0 == 0) goto L_0x00c1
            boolean r1 = r6.floatingMode
            if (r1 == 0) goto L_0x00c1
            boolean r1 = r6.switchingToFloatingMode
            if (r1 != 0) goto L_0x00c1
            boolean r1 = r6.active
            if (r1 != 0) goto L_0x0014
            goto L_0x00c1
        L_0x0014:
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 0
            r3 = 20
            if (r1 < r3) goto L_0x0029
            android.view.WindowInsets r4 = r6.lastInsets
            if (r4 != 0) goto L_0x0020
            goto L_0x0029
        L_0x0020:
            int r4 = r4.getSystemWindowInsetTop()
            float r4 = (float) r4
            float r5 = r6.topPadding
            float r4 = r4 + r5
            goto L_0x002a
        L_0x0029:
            r4 = 0
        L_0x002a:
            if (r1 < r3) goto L_0x0039
            android.view.WindowInsets r1 = r6.lastInsets
            if (r1 != 0) goto L_0x0031
            goto L_0x0039
        L_0x0031:
            int r1 = r1.getSystemWindowInsetBottom()
            float r1 = (float) r1
            float r2 = r6.bottomPadding
            float r2 = r2 + r1
        L_0x0039:
            float r1 = r6.leftPadding
            android.view.View r0 = (android.view.View) r0
            int r3 = r0.getMeasuredWidth()
            float r3 = (float) r3
            float r5 = r6.leftPadding
            float r3 = r3 - r5
            float r5 = r6.rightPadding
            float r3 = r3 - r5
            float r9 = (float) r9
            float r3 = r3 - r9
            float r3 = r3 * r7
            float r1 = r1 + r3
            int r7 = r0.getMeasuredHeight()
            float r7 = (float) r7
            float r7 = r7 - r2
            float r7 = r7 - r4
            float r9 = (float) r10
            float r7 = r7 - r9
            float r7 = r7 * r8
            float r4 = r4 + r7
            r7 = 150(0x96, double:7.4E-322)
            r9 = 0
            r10 = 1065353216(0x3var_, float:1.0)
            if (r11 == 0) goto L_0x0097
            android.view.ViewPropertyAnimator r11 = r6.animate()
            android.view.ViewPropertyAnimator r9 = r11.setListener(r9)
            r9.cancel()
            android.view.ViewPropertyAnimator r9 = r6.animate()
            android.view.ViewPropertyAnimator r9 = r9.scaleX(r10)
            android.view.ViewPropertyAnimator r9 = r9.scaleY(r10)
            android.view.ViewPropertyAnimator r9 = r9.translationX(r1)
            android.view.ViewPropertyAnimator r9 = r9.translationY(r4)
            android.view.ViewPropertyAnimator r9 = r9.alpha(r10)
            r10 = 0
            android.view.ViewPropertyAnimator r9 = r9.setStartDelay(r10)
            android.view.ViewPropertyAnimator r7 = r9.setDuration(r7)
            org.telegram.ui.Components.CubicBezierInterpolator r8 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            android.view.ViewPropertyAnimator r7 = r7.setInterpolator(r8)
            r7.start()
            goto L_0x00c1
        L_0x0097:
            boolean r11 = r6.alwaysFloating
            if (r11 != 0) goto L_0x00bb
            android.view.ViewPropertyAnimator r11 = r6.animate()
            android.view.ViewPropertyAnimator r9 = r11.setListener(r9)
            r9.cancel()
            r6.setScaleX(r10)
            r6.setScaleY(r10)
            android.view.ViewPropertyAnimator r9 = r6.animate()
            android.view.ViewPropertyAnimator r9 = r9.alpha(r10)
            android.view.ViewPropertyAnimator r7 = r9.setDuration(r7)
            r7.start()
        L_0x00bb:
            r6.setTranslationX(r1)
            r6.setTranslationY(r4)
        L_0x00c1:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.VoIPFloatingLayout.setRelativePositionInternal(float, float, int, int, boolean):void");
    }

    public void setFloatingMode(boolean z, boolean z2) {
        if (getMeasuredWidth() <= 0 || getVisibility() != 0) {
            z2 = false;
        }
        float f = 1.0f;
        if (!z2) {
            if (this.floatingMode != z) {
                this.floatingMode = z;
                this.setedFloatingMode = z;
                if (!z) {
                    f = 0.0f;
                }
                this.toFloatingModeProgress = f;
                requestLayout();
                if (Build.VERSION.SDK_INT >= 21) {
                    invalidateOutline();
                }
            }
        } else if (this.switchingToFloatingMode) {
            this.setedFloatingMode = z;
        } else if (z && !this.floatingMode) {
            this.floatingMode = true;
            this.setedFloatingMode = z;
            updatePadding();
            float f2 = this.relativePositionToSetX;
            if (f2 >= 0.0f) {
                setRelativePositionInternal(f2, this.relativePositionToSetY, (int) (((float) getMeasuredWidth()) * 0.23f), (int) (((float) getMeasuredHeight()) * 0.23f), false);
            }
            this.floatingMode = false;
            this.switchingToFloatingMode = true;
            final float translationX = getTranslationX();
            final float translationY = getTranslationY();
            setTranslationX(0.0f);
            setTranslationY(0.0f);
            invalidate();
            ValueAnimator valueAnimator = this.switchToFloatingModeAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.toFloatingModeProgress, 1.0f});
            this.switchToFloatingModeAnimator = ofFloat;
            ofFloat.addUpdateListener(this.progressUpdateListener);
            this.switchToFloatingModeAnimator.setDuration(300);
            this.switchToFloatingModeAnimator.start();
            animate().setListener((Animator.AnimatorListener) null).cancel();
            ViewPropertyAnimator duration = animate().scaleX(0.23f).scaleY(0.23f).translationX(translationX - ((((float) getMeasuredWidth()) - (((float) getMeasuredWidth()) * 0.23f)) / 2.0f)).translationY(translationY - ((((float) getMeasuredHeight()) - (((float) getMeasuredHeight()) * 0.23f)) / 2.0f)).alpha(1.0f).setStartDelay(0).setDuration(300);
            CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.DEFAULT;
            duration.setInterpolator(cubicBezierInterpolator).setListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    boolean unused = VoIPFloatingLayout.this.switchingToFloatingMode = false;
                    boolean unused2 = VoIPFloatingLayout.this.floatingMode = true;
                    VoIPFloatingLayout voIPFloatingLayout = VoIPFloatingLayout.this;
                    voIPFloatingLayout.updatePositionFromX = translationX;
                    voIPFloatingLayout.updatePositionFromY = translationY;
                    voIPFloatingLayout.requestLayout();
                }
            }).setInterpolator(cubicBezierInterpolator).start();
        } else if (z || !this.floatingMode) {
            if (!this.floatingMode) {
                f = 0.0f;
            }
            this.toFloatingModeProgress = f;
            this.floatingMode = z;
            this.setedFloatingMode = z;
            requestLayout();
        } else {
            this.setedFloatingMode = z;
            final float translationX2 = getTranslationX();
            final float translationY2 = getTranslationY();
            updatePadding();
            this.floatingMode = false;
            this.switchingToFloatingMode = true;
            requestLayout();
            animate().setListener((Animator.AnimatorListener) null).cancel();
            getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    if (!VoIPFloatingLayout.this.measuredAsFloatingMode) {
                        ValueAnimator valueAnimator = VoIPFloatingLayout.this.switchToFloatingModeAnimator;
                        if (valueAnimator != null) {
                            valueAnimator.cancel();
                        }
                        VoIPFloatingLayout voIPFloatingLayout = VoIPFloatingLayout.this;
                        voIPFloatingLayout.switchToFloatingModeAnimator = ValueAnimator.ofFloat(new float[]{voIPFloatingLayout.toFloatingModeProgress, 0.0f});
                        VoIPFloatingLayout voIPFloatingLayout2 = VoIPFloatingLayout.this;
                        voIPFloatingLayout2.switchToFloatingModeAnimator.addUpdateListener(voIPFloatingLayout2.progressUpdateListener);
                        VoIPFloatingLayout.this.switchToFloatingModeAnimator.setDuration(300);
                        VoIPFloatingLayout.this.switchToFloatingModeAnimator.start();
                        float measuredWidth = translationX2 - ((((float) VoIPFloatingLayout.this.getMeasuredWidth()) - (((float) VoIPFloatingLayout.this.getMeasuredWidth()) * 0.23f)) / 2.0f);
                        VoIPFloatingLayout.this.getViewTreeObserver().removeOnPreDrawListener(this);
                        VoIPFloatingLayout.this.setTranslationX(measuredWidth);
                        VoIPFloatingLayout.this.setTranslationY(translationY2 - ((((float) VoIPFloatingLayout.this.getMeasuredHeight()) - (((float) VoIPFloatingLayout.this.getMeasuredHeight()) * 0.23f)) / 2.0f));
                        VoIPFloatingLayout.this.setScaleX(0.23f);
                        VoIPFloatingLayout.this.setScaleY(0.23f);
                        VoIPFloatingLayout.this.animate().setListener((Animator.AnimatorListener) null).cancel();
                        VoIPFloatingLayout.this.animate().setListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                boolean unused = VoIPFloatingLayout.this.switchingToFloatingMode = false;
                                VoIPFloatingLayout.this.requestLayout();
                            }
                        }).scaleX(1.0f).scaleY(1.0f).translationX(0.0f).translationY(0.0f).alpha(1.0f).setDuration(300).setStartDelay(0).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                    } else {
                        boolean unused = VoIPFloatingLayout.this.floatingMode = false;
                        VoIPFloatingLayout.this.requestLayout();
                    }
                    return false;
                }
            });
        }
    }

    public void setMuted(boolean z, boolean z2) {
        float f = 1.0f;
        if (!z2) {
            ValueAnimator valueAnimator = this.mutedAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            if (!z) {
                f = 0.0f;
            }
            this.mutedProgress = f;
            invalidate();
            return;
        }
        ValueAnimator valueAnimator2 = this.mutedAnimator;
        if (valueAnimator2 != null) {
            valueAnimator2.cancel();
        }
        float[] fArr = new float[2];
        fArr[0] = this.mutedProgress;
        if (!z) {
            f = 0.0f;
        }
        fArr[1] = f;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
        this.mutedAnimator = ofFloat;
        ofFloat.addUpdateListener(this.mutedUpdateListener);
        this.mutedAnimator.setDuration(150);
        this.mutedAnimator.start();
    }

    public void setCornerRadius(float f) {
        this.overrideCornerRadius = f;
        if (Build.VERSION.SDK_INT >= 21) {
            invalidateOutline();
        }
    }

    public void setOnTapListener(View.OnClickListener onClickListener) {
        this.tapListener = onClickListener;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:5:0x000e, code lost:
        r4 = r7.lastInsets;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setRelativePosition(org.telegram.ui.Components.voip.VoIPFloatingLayout r8) {
        /*
            r7 = this;
            android.view.ViewParent r0 = r7.getParent()
            if (r0 != 0) goto L_0x0007
            return
        L_0x0007:
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 20
            r3 = 0
            if (r1 < r2) goto L_0x001c
            android.view.WindowInsets r4 = r7.lastInsets
            if (r4 != 0) goto L_0x0013
            goto L_0x001c
        L_0x0013:
            int r4 = r4.getSystemWindowInsetTop()
            float r4 = (float) r4
            float r5 = r7.topPadding
            float r4 = r4 + r5
            goto L_0x001d
        L_0x001c:
            r4 = 0
        L_0x001d:
            if (r1 < r2) goto L_0x002d
            android.view.WindowInsets r1 = r7.lastInsets
            if (r1 != 0) goto L_0x0024
            goto L_0x002d
        L_0x0024:
            int r1 = r1.getSystemWindowInsetBottom()
            float r1 = (float) r1
            float r2 = r7.bottomPadding
            float r1 = r1 + r2
            goto L_0x002e
        L_0x002d:
            r1 = 0
        L_0x002e:
            float r2 = r8.getTranslationX()
            float r5 = r7.leftPadding
            float r2 = r2 - r5
            android.view.View r0 = (android.view.View) r0
            int r5 = r0.getMeasuredWidth()
            float r5 = (float) r5
            float r6 = r7.leftPadding
            float r5 = r5 - r6
            float r6 = r7.rightPadding
            float r5 = r5 - r6
            int r6 = r8.getMeasuredWidth()
            float r6 = (float) r6
            float r5 = r5 - r6
            float r2 = r2 / r5
            float r5 = r8.getTranslationY()
            float r5 = r5 - r4
            int r0 = r0.getMeasuredHeight()
            float r0 = (float) r0
            float r0 = r0 - r1
            float r0 = r0 - r4
            int r8 = r8.getMeasuredHeight()
            float r8 = (float) r8
            float r0 = r0 - r8
            float r5 = r5 / r0
            float r8 = java.lang.Math.max(r3, r2)
            r0 = 1065353216(0x3var_, float:1.0)
            float r8 = java.lang.Math.min(r0, r8)
            float r1 = java.lang.Math.max(r3, r5)
            float r0 = java.lang.Math.min(r0, r1)
            r7.setRelativePosition(r8, r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.VoIPFloatingLayout.setRelativePosition(org.telegram.ui.Components.voip.VoIPFloatingLayout):void");
    }

    public void setIsActive(boolean z) {
        this.active = z;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:9:0x001a, code lost:
        r4 = r7.lastInsets;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void saveRelatedPosition() {
        /*
            r7 = this;
            int r0 = r7.getMeasuredWidth()
            if (r0 <= 0) goto L_0x0087
            float r0 = r7.relativePositionToSetX
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x0087
            android.view.ViewParent r0 = r7.getParent()
            if (r0 != 0) goto L_0x0014
            return
        L_0x0014:
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 20
            if (r2 < r3) goto L_0x0028
            android.view.WindowInsets r4 = r7.lastInsets
            if (r4 != 0) goto L_0x001f
            goto L_0x0028
        L_0x001f:
            int r4 = r4.getSystemWindowInsetTop()
            float r4 = (float) r4
            float r5 = r7.topPadding
            float r4 = r4 + r5
            goto L_0x0029
        L_0x0028:
            r4 = 0
        L_0x0029:
            if (r2 < r3) goto L_0x0039
            android.view.WindowInsets r2 = r7.lastInsets
            if (r2 != 0) goto L_0x0030
            goto L_0x0039
        L_0x0030:
            int r2 = r2.getSystemWindowInsetBottom()
            float r2 = (float) r2
            float r3 = r7.bottomPadding
            float r2 = r2 + r3
            goto L_0x003a
        L_0x0039:
            r2 = 0
        L_0x003a:
            float r3 = r7.getTranslationX()
            float r5 = r7.leftPadding
            float r3 = r3 - r5
            android.view.View r0 = (android.view.View) r0
            int r5 = r0.getMeasuredWidth()
            float r5 = (float) r5
            float r6 = r7.leftPadding
            float r5 = r5 - r6
            float r6 = r7.rightPadding
            float r5 = r5 - r6
            int r6 = r7.getMeasuredWidth()
            float r6 = (float) r6
            float r5 = r5 - r6
            float r3 = r3 / r5
            r7.savedRelativePositionX = r3
            float r3 = r7.getTranslationY()
            float r3 = r3 - r4
            int r0 = r0.getMeasuredHeight()
            float r0 = (float) r0
            float r0 = r0 - r2
            float r0 = r0 - r4
            int r2 = r7.getMeasuredHeight()
            float r2 = (float) r2
            float r0 = r0 - r2
            float r3 = r3 / r0
            r7.savedRelativePositionY = r3
            float r0 = r7.savedRelativePositionX
            r2 = 1065353216(0x3var_, float:1.0)
            float r0 = java.lang.Math.min(r2, r0)
            float r0 = java.lang.Math.max(r1, r0)
            r7.savedRelativePositionX = r0
            float r0 = r7.savedRelativePositionY
            float r0 = java.lang.Math.min(r2, r0)
            float r0 = java.lang.Math.max(r1, r0)
            r7.savedRelativePositionY = r0
            goto L_0x008d
        L_0x0087:
            r0 = -1082130432(0xffffffffbvar_, float:-1.0)
            r7.savedRelativePositionX = r0
            r7.savedRelativePositionY = r0
        L_0x008d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.VoIPFloatingLayout.saveRelatedPosition():void");
    }

    public void restoreRelativePosition() {
        updatePadding();
        float f = this.savedRelativePositionX;
        if (f >= 0.0f && !this.switchingToFloatingMode) {
            setRelativePositionInternal(f, this.savedRelativePositionY, getMeasuredWidth(), getMeasuredHeight(), true);
            this.savedRelativePositionX = -1.0f;
            this.savedRelativePositionY = -1.0f;
        }
    }
}
