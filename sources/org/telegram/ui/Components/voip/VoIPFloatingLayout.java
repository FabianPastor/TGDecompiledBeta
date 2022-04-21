package org.telegram.ui.Components.voip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
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
    public boolean measuredAsFloatingMode;
    boolean moving;
    ValueAnimator mutedAnimator;
    Drawable mutedDrawable;
    Paint mutedPaint = new Paint(1);
    float mutedProgress = 0.0f;
    private ValueAnimator.AnimatorUpdateListener mutedUpdateListener = new VoIPFloatingLayout$$ExternalSyntheticLambda0(this);
    Drawable outerShadow;
    /* access modifiers changed from: private */
    public float overrideCornerRadius = -1.0f;
    final Path path = new Path();
    /* access modifiers changed from: private */
    public ValueAnimator.AnimatorUpdateListener progressUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            VoIPFloatingLayout.this.toFloatingModeProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            if (VoIPFloatingLayout.this.delegate != null) {
                VoIPFloatingLayout.this.delegate.onChange(VoIPFloatingLayout.this.toFloatingModeProgress, VoIPFloatingLayout.this.measuredAsFloatingMode);
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

    /* renamed from: lambda$new$0$org-telegram-ui-Components-voip-VoIPFloatingLayout  reason: not valid java name */
    public /* synthetic */ void m4576lambda$new$0$orgtelegramuiComponentsvoipVoIPFloatingLayout(ValueAnimator valueAnimator) {
        this.mutedProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    public VoIPFloatingLayout(Context context) {
        super(context);
        this.touchSlop = (float) ViewConfiguration.get(context).getScaledTouchSlop();
        if (Build.VERSION.SDK_INT >= 21) {
            setOutlineProvider(new ViewOutlineProvider() {
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
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);
        this.measuredAsFloatingMode = false;
        if (this.floatingMode) {
            width = (int) (((float) width) * 0.23f);
            height = (int) (((float) height) * 0.23f);
            this.measuredAsFloatingMode = true;
        } else if (!this.switchingToPip) {
            setTranslationX(0.0f);
            setTranslationY(0.0f);
        }
        VoIPFloatingLayoutDelegate voIPFloatingLayoutDelegate = this.delegate;
        if (voIPFloatingLayoutDelegate != null) {
            voIPFloatingLayoutDelegate.onChange(this.toFloatingModeProgress, this.measuredAsFloatingMode);
        }
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(width, NUM), View.MeasureSpec.makeMeasureSpec(height, NUM));
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

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    public void setDelegate(VoIPFloatingLayoutDelegate voIPFloatingLayoutDelegate) {
        this.delegate = voIPFloatingLayoutDelegate;
    }

    public boolean onTouchEvent(MotionEvent event) {
        WindowInsets windowInsets;
        ViewParent parent = getParent();
        if (!this.floatingMode || this.switchingToFloatingMode || !this.active) {
            return false;
        }
        switch (event.getAction()) {
            case 0:
                if (this.floatingMode && !this.switchingToFloatingMode) {
                    this.startTime = System.currentTimeMillis();
                    this.starX = event.getX() + getX();
                    this.starY = event.getY() + getY();
                    animate().setListener((Animator.AnimatorListener) null).cancel();
                    animate().scaleY(1.05f).scaleX(1.05f).alpha(1.0f).setStartDelay(0).start();
                    break;
                }
            case 1:
            case 3:
                if (parent != null && this.floatingMode && !this.switchingToFloatingMode) {
                    parent.requestDisallowInterceptTouchEvent(false);
                    animate().setListener((Animator.AnimatorListener) null).cancel();
                    ViewPropertyAnimator animator = animate().scaleX(1.0f).scaleY(1.0f).alpha(1.0f).setStartDelay(0);
                    if (this.tapListener != null && !this.moving && System.currentTimeMillis() - this.startTime < 200) {
                        this.tapListener.onClick(this);
                    }
                    int parentWidth = ((View) getParent()).getMeasuredWidth();
                    int parentHeight = ((View) getParent()).getMeasuredHeight();
                    float maxTop = this.topPadding;
                    float maxBottom = this.bottomPadding;
                    if (Build.VERSION.SDK_INT > 20 && (windowInsets = this.lastInsets) != null) {
                        maxTop += (float) windowInsets.getSystemWindowInsetTop();
                        maxBottom += (float) this.lastInsets.getSystemWindowInsetBottom();
                    }
                    float x = getX();
                    float f = this.leftPadding;
                    if (x < f) {
                        animator.translationX(f);
                    } else if (getX() + ((float) getMeasuredWidth()) > ((float) parentWidth) - this.rightPadding) {
                        animator.translationX(((float) (parentWidth - getMeasuredWidth())) - this.rightPadding);
                    }
                    if (getY() < maxTop) {
                        animator.translationY(maxTop);
                    } else if (getY() + ((float) getMeasuredHeight()) > ((float) parentHeight) - maxBottom) {
                        animator.translationY(((float) (parentHeight - getMeasuredHeight())) - maxBottom);
                    }
                    animator.setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                }
                this.moving = false;
                break;
            case 2:
                float dx = (event.getX() + getX()) - this.starX;
                float dy = (event.getY() + getY()) - this.starY;
                if (!this.moving) {
                    float f2 = (dx * dx) + (dy * dy);
                    float f3 = this.touchSlop;
                    if (f2 > f3 * f3) {
                        if (parent != null) {
                            parent.requestDisallowInterceptTouchEvent(true);
                        }
                        this.moving = true;
                        this.starX = event.getX() + getX();
                        this.starY = event.getY() + getY();
                        this.startMovingFromX = getTranslationX();
                        this.startMovingFromY = getTranslationY();
                        dx = 0.0f;
                        dy = 0.0f;
                    }
                }
                if (this.moving) {
                    setTranslationX(this.startMovingFromX + dx);
                    setTranslationY(this.startMovingFromY + dy);
                    break;
                }
                break;
        }
        return true;
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
        int cX = getMeasuredWidth() >> 1;
        int cY = getMeasuredHeight() - ((int) ((((float) AndroidUtilities.dp(18.0f)) * 1.0f) / getScaleY()));
        canvas.save();
        canvas.scale((1.0f / getScaleX()) * this.toFloatingModeProgress * this.mutedProgress, (1.0f / getScaleY()) * this.toFloatingModeProgress * this.mutedProgress, (float) cX, (float) cY);
        canvas.drawCircle((float) cX, (float) cY, (float) AndroidUtilities.dp(14.0f), this.mutedPaint);
        Drawable drawable = this.mutedDrawable;
        drawable.setBounds(cX - (drawable.getIntrinsicWidth() / 2), cY - (this.mutedDrawable.getIntrinsicHeight() / 2), (this.mutedDrawable.getIntrinsicWidth() / 2) + cX, (this.mutedDrawable.getIntrinsicHeight() / 2) + cY);
        this.mutedDrawable.draw(canvas);
        canvas.restore();
        if (this.switchingToFloatingMode) {
            invalidate();
        }
    }

    public void setInsets(WindowInsets lastInsets2) {
        this.lastInsets = lastInsets2;
    }

    public void setRelativePosition(float x, float y) {
        ViewParent parent = getParent();
        if (!this.floatingMode || parent == null || ((View) parent).getMeasuredWidth() > 0 || getMeasuredWidth() == 0 || getMeasuredHeight() == 0) {
            this.relativePositionToSetX = x;
            this.relativePositionToSetY = y;
            return;
        }
        setRelativePositionInternal(x, y, getMeasuredWidth(), getMeasuredHeight(), true);
    }

    public void setUiVisible(boolean uiVisible2) {
        if (getParent() == null) {
            this.uiVisible = uiVisible2;
        } else {
            this.uiVisible = uiVisible2;
        }
    }

    public void setBottomOffset(int bottomOffset2, boolean animated) {
        if (getParent() == null || !animated) {
            this.bottomOffset = bottomOffset2;
        } else {
            this.bottomOffset = bottomOffset2;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x001b, code lost:
        r1 = r10.lastInsets;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setRelativePositionInternal(float r11, float r12, int r13, int r14, boolean r15) {
        /*
            r10 = this;
            android.view.ViewParent r0 = r10.getParent()
            if (r0 == 0) goto L_0x00c8
            boolean r1 = r10.floatingMode
            if (r1 == 0) goto L_0x00c8
            boolean r1 = r10.switchingToFloatingMode
            if (r1 != 0) goto L_0x00c8
            boolean r1 = r10.active
            if (r1 != 0) goto L_0x0014
            goto L_0x00c8
        L_0x0014:
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 0
            r3 = 20
            if (r1 < r3) goto L_0x0029
            android.view.WindowInsets r1 = r10.lastInsets
            if (r1 != 0) goto L_0x0020
            goto L_0x0029
        L_0x0020:
            int r1 = r1.getSystemWindowInsetTop()
            float r1 = (float) r1
            float r4 = r10.topPadding
            float r1 = r1 + r4
            goto L_0x002a
        L_0x0029:
            r1 = 0
        L_0x002a:
            int r4 = android.os.Build.VERSION.SDK_INT
            if (r4 < r3) goto L_0x003b
            android.view.WindowInsets r3 = r10.lastInsets
            if (r3 != 0) goto L_0x0033
            goto L_0x003b
        L_0x0033:
            int r2 = r3.getSystemWindowInsetBottom()
            float r2 = (float) r2
            float r3 = r10.bottomPadding
            float r2 = r2 + r3
        L_0x003b:
            float r3 = r10.leftPadding
            r4 = r0
            android.view.View r4 = (android.view.View) r4
            int r4 = r4.getMeasuredWidth()
            float r4 = (float) r4
            float r5 = r10.leftPadding
            float r4 = r4 - r5
            float r5 = r10.rightPadding
            float r4 = r4 - r5
            float r5 = (float) r13
            float r4 = r4 - r5
            float r4 = r4 * r11
            float r3 = r3 + r4
            r4 = r0
            android.view.View r4 = (android.view.View) r4
            int r4 = r4.getMeasuredHeight()
            float r4 = (float) r4
            float r4 = r4 - r2
            float r4 = r4 - r1
            float r5 = (float) r14
            float r4 = r4 - r5
            float r4 = r4 * r12
            float r4 = r4 + r1
            r5 = 150(0x96, double:7.4E-322)
            r7 = 0
            r8 = 1065353216(0x3var_, float:1.0)
            if (r15 == 0) goto L_0x009d
            android.view.ViewPropertyAnimator r9 = r10.animate()
            android.view.ViewPropertyAnimator r7 = r9.setListener(r7)
            r7.cancel()
            android.view.ViewPropertyAnimator r7 = r10.animate()
            android.view.ViewPropertyAnimator r7 = r7.scaleX(r8)
            android.view.ViewPropertyAnimator r7 = r7.scaleY(r8)
            android.view.ViewPropertyAnimator r7 = r7.translationX(r3)
            android.view.ViewPropertyAnimator r7 = r7.translationY(r4)
            android.view.ViewPropertyAnimator r7 = r7.alpha(r8)
            r8 = 0
            android.view.ViewPropertyAnimator r7 = r7.setStartDelay(r8)
            android.view.ViewPropertyAnimator r5 = r7.setDuration(r5)
            org.telegram.ui.Components.CubicBezierInterpolator r6 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            android.view.ViewPropertyAnimator r5 = r5.setInterpolator(r6)
            r5.start()
            goto L_0x00c7
        L_0x009d:
            boolean r9 = r10.alwaysFloating
            if (r9 != 0) goto L_0x00c1
            android.view.ViewPropertyAnimator r9 = r10.animate()
            android.view.ViewPropertyAnimator r7 = r9.setListener(r7)
            r7.cancel()
            r10.setScaleX(r8)
            r10.setScaleY(r8)
            android.view.ViewPropertyAnimator r7 = r10.animate()
            android.view.ViewPropertyAnimator r7 = r7.alpha(r8)
            android.view.ViewPropertyAnimator r5 = r7.setDuration(r5)
            r5.start()
        L_0x00c1:
            r10.setTranslationX(r3)
            r10.setTranslationY(r4)
        L_0x00c7:
            return
        L_0x00c8:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.VoIPFloatingLayout.setRelativePositionInternal(float, float, int, int, boolean):void");
    }

    public void setFloatingMode(boolean show, boolean animated) {
        if (getMeasuredWidth() <= 0 || getVisibility() != 0) {
            animated = false;
        }
        float f = 1.0f;
        if (!animated) {
            if (this.floatingMode != show) {
                this.floatingMode = show;
                this.setedFloatingMode = show;
                if (!show) {
                    f = 0.0f;
                }
                this.toFloatingModeProgress = f;
                requestLayout();
                if (Build.VERSION.SDK_INT >= 21) {
                    invalidateOutline();
                }
            }
        } else if (this.switchingToFloatingMode) {
            this.setedFloatingMode = show;
        } else if (show && !this.floatingMode) {
            this.floatingMode = true;
            this.setedFloatingMode = show;
            updatePadding();
            float f2 = this.relativePositionToSetX;
            if (f2 >= 0.0f) {
                setRelativePositionInternal(f2, this.relativePositionToSetY, (int) (((float) getMeasuredWidth()) * 0.23f), (int) (((float) getMeasuredHeight()) * 0.23f), false);
            }
            this.floatingMode = false;
            this.switchingToFloatingMode = true;
            final float toX = getTranslationX();
            final float toY = getTranslationY();
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
            animate().scaleX(0.23f).scaleY(0.23f).translationX(toX - ((((float) getMeasuredWidth()) - (((float) getMeasuredWidth()) * 0.23f)) / 2.0f)).translationY(toY - ((((float) getMeasuredHeight()) - (((float) getMeasuredHeight()) * 0.23f)) / 2.0f)).alpha(1.0f).setStartDelay(0).setDuration(300).setInterpolator(CubicBezierInterpolator.DEFAULT).setListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    boolean unused = VoIPFloatingLayout.this.switchingToFloatingMode = false;
                    boolean unused2 = VoIPFloatingLayout.this.floatingMode = true;
                    VoIPFloatingLayout.this.updatePositionFromX = toX;
                    VoIPFloatingLayout.this.updatePositionFromY = toY;
                    VoIPFloatingLayout.this.requestLayout();
                }
            }).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        } else if (show || !this.floatingMode) {
            if (!this.floatingMode) {
                f = 0.0f;
            }
            this.toFloatingModeProgress = f;
            this.floatingMode = show;
            this.setedFloatingMode = show;
            requestLayout();
        } else {
            this.setedFloatingMode = show;
            final float fromX = getTranslationX();
            final float fromY = getTranslationY();
            updatePadding();
            this.floatingMode = false;
            this.switchingToFloatingMode = true;
            requestLayout();
            animate().setListener((Animator.AnimatorListener) null).cancel();
            getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    if (!VoIPFloatingLayout.this.measuredAsFloatingMode) {
                        if (VoIPFloatingLayout.this.switchToFloatingModeAnimator != null) {
                            VoIPFloatingLayout.this.switchToFloatingModeAnimator.cancel();
                        }
                        VoIPFloatingLayout voIPFloatingLayout = VoIPFloatingLayout.this;
                        voIPFloatingLayout.switchToFloatingModeAnimator = ValueAnimator.ofFloat(new float[]{voIPFloatingLayout.toFloatingModeProgress, 0.0f});
                        VoIPFloatingLayout.this.switchToFloatingModeAnimator.addUpdateListener(VoIPFloatingLayout.this.progressUpdateListener);
                        VoIPFloatingLayout.this.switchToFloatingModeAnimator.setDuration(300);
                        VoIPFloatingLayout.this.switchToFloatingModeAnimator.start();
                        float fromXfinal = fromX - ((((float) VoIPFloatingLayout.this.getMeasuredWidth()) - (((float) VoIPFloatingLayout.this.getMeasuredWidth()) * 0.23f)) / 2.0f);
                        VoIPFloatingLayout.this.getViewTreeObserver().removeOnPreDrawListener(this);
                        VoIPFloatingLayout.this.setTranslationX(fromXfinal);
                        VoIPFloatingLayout.this.setTranslationY(fromY - ((((float) VoIPFloatingLayout.this.getMeasuredHeight()) - (((float) VoIPFloatingLayout.this.getMeasuredHeight()) * 0.23f)) / 2.0f));
                        VoIPFloatingLayout.this.setScaleX(0.23f);
                        VoIPFloatingLayout.this.setScaleY(0.23f);
                        VoIPFloatingLayout.this.animate().setListener((Animator.AnimatorListener) null).cancel();
                        VoIPFloatingLayout.this.animate().setListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animation) {
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

    public void setMuted(boolean muted, boolean animated) {
        float f = 1.0f;
        if (!animated) {
            ValueAnimator valueAnimator = this.mutedAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            if (!muted) {
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
        if (!muted) {
            f = 0.0f;
        }
        fArr[1] = f;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
        this.mutedAnimator = ofFloat;
        ofFloat.addUpdateListener(this.mutedUpdateListener);
        this.mutedAnimator.setDuration(150);
        this.mutedAnimator.start();
    }

    public void setCornerRadius(float cornerRadius) {
        this.overrideCornerRadius = cornerRadius;
        if (Build.VERSION.SDK_INT >= 21) {
            invalidateOutline();
        }
    }

    public void setOnTapListener(View.OnClickListener tapListener2) {
        this.tapListener = tapListener2;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:5:0x000e, code lost:
        r1 = r8.lastInsets;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setRelativePosition(org.telegram.ui.Components.voip.VoIPFloatingLayout r9) {
        /*
            r8 = this;
            android.view.ViewParent r0 = r8.getParent()
            if (r0 != 0) goto L_0x0007
            return
        L_0x0007:
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 20
            r3 = 0
            if (r1 < r2) goto L_0x001c
            android.view.WindowInsets r1 = r8.lastInsets
            if (r1 != 0) goto L_0x0013
            goto L_0x001c
        L_0x0013:
            int r1 = r1.getSystemWindowInsetTop()
            float r1 = (float) r1
            float r4 = r8.topPadding
            float r1 = r1 + r4
            goto L_0x001d
        L_0x001c:
            r1 = 0
        L_0x001d:
            int r4 = android.os.Build.VERSION.SDK_INT
            if (r4 < r2) goto L_0x002f
            android.view.WindowInsets r2 = r8.lastInsets
            if (r2 != 0) goto L_0x0026
            goto L_0x002f
        L_0x0026:
            int r2 = r2.getSystemWindowInsetBottom()
            float r2 = (float) r2
            float r4 = r8.bottomPadding
            float r2 = r2 + r4
            goto L_0x0030
        L_0x002f:
            r2 = 0
        L_0x0030:
            float r4 = r9.getTranslationX()
            float r5 = r8.leftPadding
            float r4 = r4 - r5
            r5 = r0
            android.view.View r5 = (android.view.View) r5
            int r5 = r5.getMeasuredWidth()
            float r5 = (float) r5
            float r6 = r8.leftPadding
            float r5 = r5 - r6
            float r6 = r8.rightPadding
            float r5 = r5 - r6
            int r6 = r9.getMeasuredWidth()
            float r6 = (float) r6
            float r5 = r5 - r6
            float r4 = r4 / r5
            float r5 = r9.getTranslationY()
            float r5 = r5 - r1
            r6 = r0
            android.view.View r6 = (android.view.View) r6
            int r6 = r6.getMeasuredHeight()
            float r6 = (float) r6
            float r6 = r6 - r2
            float r6 = r6 - r1
            int r7 = r9.getMeasuredHeight()
            float r7 = (float) r7
            float r6 = r6 - r7
            float r5 = r5 / r6
            float r6 = java.lang.Math.max(r3, r4)
            r7 = 1065353216(0x3var_, float:1.0)
            float r4 = java.lang.Math.min(r7, r6)
            float r3 = java.lang.Math.max(r3, r5)
            float r3 = java.lang.Math.min(r7, r3)
            r8.setRelativePosition(r4, r3)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.VoIPFloatingLayout.setRelativePosition(org.telegram.ui.Components.voip.VoIPFloatingLayout):void");
    }

    public void setIsActive(boolean b) {
        this.active = b;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:9:0x001a, code lost:
        r2 = r7.lastInsets;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void saveRelativePosition() {
        /*
            r7 = this;
            int r0 = r7.getMeasuredWidth()
            if (r0 <= 0) goto L_0x008d
            float r0 = r7.relativePositionToSetX
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x008d
            android.view.ViewParent r0 = r7.getParent()
            if (r0 != 0) goto L_0x0014
            return
        L_0x0014:
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 20
            if (r2 < r3) goto L_0x0028
            android.view.WindowInsets r2 = r7.lastInsets
            if (r2 != 0) goto L_0x001f
            goto L_0x0028
        L_0x001f:
            int r2 = r2.getSystemWindowInsetTop()
            float r2 = (float) r2
            float r4 = r7.topPadding
            float r2 = r2 + r4
            goto L_0x0029
        L_0x0028:
            r2 = 0
        L_0x0029:
            int r4 = android.os.Build.VERSION.SDK_INT
            if (r4 < r3) goto L_0x003b
            android.view.WindowInsets r3 = r7.lastInsets
            if (r3 != 0) goto L_0x0032
            goto L_0x003b
        L_0x0032:
            int r3 = r3.getSystemWindowInsetBottom()
            float r3 = (float) r3
            float r4 = r7.bottomPadding
            float r3 = r3 + r4
            goto L_0x003c
        L_0x003b:
            r3 = 0
        L_0x003c:
            float r4 = r7.getTranslationX()
            float r5 = r7.leftPadding
            float r4 = r4 - r5
            r5 = r0
            android.view.View r5 = (android.view.View) r5
            int r5 = r5.getMeasuredWidth()
            float r5 = (float) r5
            float r6 = r7.leftPadding
            float r5 = r5 - r6
            float r6 = r7.rightPadding
            float r5 = r5 - r6
            int r6 = r7.getMeasuredWidth()
            float r6 = (float) r6
            float r5 = r5 - r6
            float r4 = r4 / r5
            r7.savedRelativePositionX = r4
            float r4 = r7.getTranslationY()
            float r4 = r4 - r2
            r5 = r0
            android.view.View r5 = (android.view.View) r5
            int r5 = r5.getMeasuredHeight()
            float r5 = (float) r5
            float r5 = r5 - r3
            float r5 = r5 - r2
            int r6 = r7.getMeasuredHeight()
            float r6 = (float) r6
            float r5 = r5 - r6
            float r4 = r4 / r5
            r7.savedRelativePositionY = r4
            float r4 = r7.savedRelativePositionX
            r5 = 1065353216(0x3var_, float:1.0)
            float r4 = java.lang.Math.min(r5, r4)
            float r4 = java.lang.Math.max(r1, r4)
            r7.savedRelativePositionX = r4
            float r4 = r7.savedRelativePositionY
            float r4 = java.lang.Math.min(r5, r4)
            float r1 = java.lang.Math.max(r1, r4)
            r7.savedRelativePositionY = r1
            goto L_0x0093
        L_0x008d:
            r0 = -1082130432(0xffffffffbvar_, float:-1.0)
            r7.savedRelativePositionX = r0
            r7.savedRelativePositionY = r0
        L_0x0093:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.VoIPFloatingLayout.saveRelativePosition():void");
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
