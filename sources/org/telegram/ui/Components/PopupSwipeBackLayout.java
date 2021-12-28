package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.SparseIntArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.core.view.GestureDetectorCompat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.Theme;

public class PopupSwipeBackLayout extends FrameLayout {
    private int currentForegroundIndex = -1;
    private GestureDetectorCompat detector;
    private ValueAnimator foregroundAnimator;
    private Paint foregroundPaint = new Paint();
    private Rect hitRect = new Rect();
    /* access modifiers changed from: private */
    public boolean isAnimationInProgress;
    /* access modifiers changed from: private */
    public boolean isProcessingSwipe;
    /* access modifiers changed from: private */
    public boolean isSwipeBackDisallowed;
    /* access modifiers changed from: private */
    public boolean isSwipeDisallowed;
    private Path mPath = new Path();
    private RectF mRect = new RectF();
    /* access modifiers changed from: private */
    public int notificationIndex;
    private OnSwipeBackProgressListener onSwipeBackProgressListener;
    private Paint overlayPaint = new Paint(1);
    private float overrideForegroundHeight;
    SparseIntArray overrideHeightIndex = new SparseIntArray();
    Theme.ResourcesProvider resourcesProvider;
    /* access modifiers changed from: private */
    public float toProgress = -1.0f;
    /* access modifiers changed from: private */
    public float transitionProgress;

    public interface OnSwipeBackProgressListener {
        void onSwipeBackProgress(PopupSwipeBackLayout popupSwipeBackLayout, float f, float f2);
    }

    public PopupSwipeBackLayout(Context context, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.resourcesProvider = resourcesProvider2;
        final int scaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        this.detector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {
            public boolean onDown(MotionEvent motionEvent) {
                return true;
            }

            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
                if (PopupSwipeBackLayout.this.isProcessingSwipe || PopupSwipeBackLayout.this.isSwipeDisallowed) {
                    MotionEvent motionEvent3 = motionEvent2;
                } else {
                    if (PopupSwipeBackLayout.this.isSwipeBackDisallowed || PopupSwipeBackLayout.this.transitionProgress != 1.0f || f > ((float) (-scaledTouchSlop)) || Math.abs(f) < Math.abs(1.5f * f2)) {
                        MotionEvent motionEvent4 = motionEvent2;
                    } else {
                        PopupSwipeBackLayout popupSwipeBackLayout = PopupSwipeBackLayout.this;
                        if (!popupSwipeBackLayout.isDisallowedView(motionEvent2, popupSwipeBackLayout.getChildAt(popupSwipeBackLayout.transitionProgress > 0.5f ? 1 : 0))) {
                            boolean unused = PopupSwipeBackLayout.this.isProcessingSwipe = true;
                            MotionEvent obtain = MotionEvent.obtain(0, 0, 3, 0.0f, 0.0f, 0);
                            for (int i = 0; i < PopupSwipeBackLayout.this.getChildCount(); i++) {
                                PopupSwipeBackLayout.this.getChildAt(i).dispatchTouchEvent(obtain);
                            }
                            obtain.recycle();
                        }
                    }
                    boolean unused2 = PopupSwipeBackLayout.this.isSwipeDisallowed = true;
                }
                if (PopupSwipeBackLayout.this.isProcessingSwipe) {
                    float unused3 = PopupSwipeBackLayout.this.toProgress = -1.0f;
                    float unused4 = PopupSwipeBackLayout.this.transitionProgress = 1.0f - Math.max(0.0f, Math.min(1.0f, (motionEvent2.getX() - motionEvent.getX()) / ((float) PopupSwipeBackLayout.this.getWidth())));
                    PopupSwipeBackLayout.this.invalidateTransforms();
                }
                return PopupSwipeBackLayout.this.isProcessingSwipe;
            }

            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
                if (!PopupSwipeBackLayout.this.isAnimationInProgress && !PopupSwipeBackLayout.this.isSwipeDisallowed && f >= 600.0f) {
                    PopupSwipeBackLayout.this.clearFlags();
                    PopupSwipeBackLayout.this.animateToState(0.0f, f / 6000.0f);
                }
                return false;
            }
        });
        this.overlayPaint.setColor(-16777216);
    }

    public void setSwipeBackDisallowed(boolean z) {
        this.isSwipeBackDisallowed = z;
    }

    public void setOnSwipeBackProgressListener(OnSwipeBackProgressListener onSwipeBackProgressListener2) {
        this.onSwipeBackProgressListener = onSwipeBackProgressListener2;
    }

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View view, long j) {
        int indexOfChild = indexOfChild(view);
        int save = canvas.save();
        if (indexOfChild != 0) {
            this.foregroundPaint.setColor(Theme.getColor("actionBarDefaultSubmenuBackground", this.resourcesProvider));
            canvas.drawRect(view.getX(), 0.0f, view.getX() + ((float) view.getMeasuredWidth()), (float) getMeasuredHeight(), this.foregroundPaint);
        }
        boolean drawChild = super.drawChild(canvas, view, j);
        if (indexOfChild == 0) {
            this.overlayPaint.setAlpha((int) (this.transitionProgress * 64.0f));
            canvas.drawRect(0.0f, 0.0f, (float) getWidth(), (float) getHeight(), this.overlayPaint);
        }
        canvas.restoreToCount(save);
        return drawChild;
    }

    /* access modifiers changed from: private */
    public void invalidateTransforms() {
        float f;
        float f2;
        OnSwipeBackProgressListener onSwipeBackProgressListener2 = this.onSwipeBackProgressListener;
        if (onSwipeBackProgressListener2 != null) {
            onSwipeBackProgressListener2.onSwipeBackProgress(this, this.toProgress, this.transitionProgress);
        }
        View childAt = getChildAt(0);
        View view = null;
        int i = this.currentForegroundIndex;
        if (i >= 0 && i < getChildCount()) {
            view = getChildAt(this.currentForegroundIndex);
        }
        childAt.setTranslationX((-this.transitionProgress) * ((float) getWidth()) * 0.5f);
        float f3 = ((1.0f - this.transitionProgress) * 0.05f) + 0.95f;
        childAt.setScaleX(f3);
        childAt.setScaleY(f3);
        if (view != null) {
            view.setTranslationX((1.0f - this.transitionProgress) * ((float) getWidth()));
        }
        invalidateVisibility();
        float measuredWidth = (float) childAt.getMeasuredWidth();
        float measuredHeight = (float) childAt.getMeasuredHeight();
        if (view != null) {
            f2 = (float) view.getMeasuredWidth();
            f = this.overrideForegroundHeight;
            if (f == 0.0f) {
                f = (float) view.getMeasuredHeight();
            }
        } else {
            f2 = 0.0f;
            f = 0.0f;
        }
        if (childAt.getMeasuredWidth() != 0 && childAt.getMeasuredHeight() != 0) {
            ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = (ActionBarPopupWindow.ActionBarPopupWindowLayout) getParent();
            float f4 = this.transitionProgress;
            float paddingLeft = measuredWidth + ((f2 - measuredWidth) * f4) + ((float) (actionBarPopupWindowLayout.getPaddingLeft() + actionBarPopupWindowLayout.getPaddingRight()));
            float paddingTop = measuredHeight + ((f - measuredHeight) * f4) + ((float) (actionBarPopupWindowLayout.getPaddingTop() + actionBarPopupWindowLayout.getPaddingBottom()));
            actionBarPopupWindowLayout.setBackScaleX(paddingLeft / ((float) actionBarPopupWindowLayout.getMeasuredWidth()));
            actionBarPopupWindowLayout.setBackScaleY(paddingTop / ((float) actionBarPopupWindowLayout.getMeasuredHeight()));
            for (int i2 = 0; i2 < getChildCount(); i2++) {
                View childAt2 = getChildAt(i2);
                childAt2.setPivotX(0.0f);
                childAt2.setPivotY(0.0f);
            }
            invalidate();
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0064, code lost:
        if (r5 > (r6 + ((r7 - ((float) r2.getMeasuredHeight())) * r9.transitionProgress))) goto L_0x0066;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean dispatchTouchEvent(android.view.MotionEvent r10) {
        /*
            r9 = this;
            boolean r0 = r9.processTouchEvent(r10)
            r1 = 1
            if (r0 == 0) goto L_0x0008
            return r1
        L_0x0008:
            int r0 = r9.currentForegroundIndex
            if (r0 < 0) goto L_0x0087
            int r2 = r9.getChildCount()
            if (r0 < r2) goto L_0x0014
            goto L_0x0087
        L_0x0014:
            r0 = 0
            android.view.View r2 = r9.getChildAt(r0)
            int r3 = r9.currentForegroundIndex
            android.view.View r3 = r9.getChildAt(r3)
            int r4 = r10.getActionMasked()
            if (r4 != 0) goto L_0x006a
            float r5 = r10.getX()
            int r6 = r2.getMeasuredWidth()
            float r6 = (float) r6
            int r7 = r3.getMeasuredWidth()
            int r8 = r2.getMeasuredWidth()
            int r7 = r7 - r8
            float r7 = (float) r7
            float r8 = r9.transitionProgress
            float r7 = r7 * r8
            float r6 = r6 + r7
            int r5 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
            if (r5 > 0) goto L_0x0066
            float r5 = r10.getY()
            int r6 = r2.getMeasuredHeight()
            float r6 = (float) r6
            float r7 = r9.overrideForegroundHeight
            r8 = 0
            int r8 = (r7 > r8 ? 1 : (r7 == r8 ? 0 : -1))
            if (r8 == 0) goto L_0x0052
            goto L_0x0057
        L_0x0052:
            int r7 = r3.getMeasuredHeight()
            float r7 = (float) r7
        L_0x0057:
            int r8 = r2.getMeasuredHeight()
            float r8 = (float) r8
            float r7 = r7 - r8
            float r8 = r9.transitionProgress
            float r7 = r7 * r8
            float r6 = r6 + r7
            int r5 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
            if (r5 <= 0) goto L_0x006a
        L_0x0066:
            r9.callOnClick()
            return r1
        L_0x006a:
            float r5 = r9.transitionProgress
            r6 = 1056964608(0x3var_, float:0.5)
            int r5 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
            if (r5 <= 0) goto L_0x0073
            r2 = r3
        L_0x0073:
            boolean r2 = r2.dispatchTouchEvent(r10)
            if (r2 != 0) goto L_0x007c
            if (r4 != 0) goto L_0x007c
            return r1
        L_0x007c:
            if (r2 != 0) goto L_0x0086
            boolean r10 = r9.onTouchEvent(r10)
            if (r10 == 0) goto L_0x0085
            goto L_0x0086
        L_0x0085:
            r1 = 0
        L_0x0086:
            return r1
        L_0x0087:
            boolean r10 = super.dispatchTouchEvent(r10)
            return r10
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PopupSwipeBackLayout.dispatchTouchEvent(android.view.MotionEvent):boolean");
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        invalidateTransforms();
    }

    private boolean processTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction() & 255;
        if (this.isAnimationInProgress) {
            return true;
        }
        if (this.detector.onTouchEvent(motionEvent) || (action != 1 && action != 3)) {
            return this.isProcessingSwipe;
        }
        if (this.isProcessingSwipe) {
            clearFlags();
            animateToState(this.transitionProgress >= 0.5f ? 1.0f : 0.0f, 0.0f);
            return false;
        } else if (!this.isSwipeDisallowed) {
            return false;
        } else {
            clearFlags();
            return false;
        }
    }

    /* access modifiers changed from: private */
    public void animateToState(final float f, float f2) {
        ValueAnimator duration = ValueAnimator.ofFloat(new float[]{this.transitionProgress, f}).setDuration((long) (Math.max(0.5f, Math.abs(this.transitionProgress - f) - Math.min(0.2f, f2)) * 300.0f));
        duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
        final int i = UserConfig.selectedAccount;
        this.notificationIndex = NotificationCenter.getInstance(i).setAnimationInProgress(this.notificationIndex, (int[]) null);
        duration.addUpdateListener(new PopupSwipeBackLayout$$ExternalSyntheticLambda1(this));
        duration.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                boolean unused = PopupSwipeBackLayout.this.isAnimationInProgress = true;
                float unused2 = PopupSwipeBackLayout.this.toProgress = f;
            }

            public void onAnimationEnd(Animator animator) {
                NotificationCenter.getInstance(i).onAnimationFinish(PopupSwipeBackLayout.this.notificationIndex);
                float unused = PopupSwipeBackLayout.this.transitionProgress = f;
                PopupSwipeBackLayout.this.invalidateTransforms();
                boolean unused2 = PopupSwipeBackLayout.this.isAnimationInProgress = false;
            }
        });
        duration.start();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$animateToState$0(ValueAnimator valueAnimator) {
        this.transitionProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidateTransforms();
    }

    /* access modifiers changed from: private */
    public void clearFlags() {
        this.isProcessingSwipe = false;
        this.isSwipeDisallowed = false;
    }

    public void openForeground(int i) {
        if (!this.isAnimationInProgress) {
            this.currentForegroundIndex = i;
            this.overrideForegroundHeight = (float) this.overrideHeightIndex.get(i);
            animateToState(1.0f, 0.0f);
        }
    }

    public void closeForeground() {
        if (!this.isAnimationInProgress) {
            animateToState(0.0f, 0.0f);
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        for (int i5 = 0; i5 < getChildCount(); i5++) {
            View childAt = getChildAt(i5);
            childAt.layout(0, 0, childAt.getMeasuredWidth(), childAt.getMeasuredHeight());
        }
    }

    public void addView(View view, int i, ViewGroup.LayoutParams layoutParams) {
        super.addView(view, i, layoutParams);
        invalidateTransforms();
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        if (getChildCount() != 0) {
            View childAt = getChildAt(0);
            float measuredWidth = (float) childAt.getMeasuredWidth();
            float measuredHeight = (float) childAt.getMeasuredHeight();
            int i = this.currentForegroundIndex;
            if (i != -1 && i < getChildCount()) {
                View childAt2 = getChildAt(this.currentForegroundIndex);
                float measuredWidth2 = (float) childAt2.getMeasuredWidth();
                float f = this.overrideForegroundHeight;
                if (f == 0.0f) {
                    f = (float) childAt2.getMeasuredHeight();
                }
                if (!(childAt.getMeasuredWidth() == 0 || childAt.getMeasuredHeight() == 0 || childAt2.getMeasuredWidth() == 0 || childAt2.getMeasuredHeight() == 0)) {
                    float f2 = this.transitionProgress;
                    measuredWidth += (measuredWidth2 - measuredWidth) * f2;
                    measuredHeight += (f - measuredHeight) * f2;
                }
            }
            int save = canvas.save();
            this.mPath.rewind();
            int dp = AndroidUtilities.dp(6.0f);
            this.mRect.set(0.0f, 0.0f, measuredWidth, measuredHeight);
            float f3 = (float) dp;
            this.mPath.addRoundRect(this.mRect, f3, f3, Path.Direction.CW);
            canvas.clipPath(this.mPath);
            super.dispatchDraw(canvas);
            canvas.restoreToCount(save);
        }
    }

    /* access modifiers changed from: private */
    public boolean isDisallowedView(MotionEvent motionEvent, View view) {
        view.getHitRect(this.hitRect);
        if (this.hitRect.contains((int) motionEvent.getX(), (int) motionEvent.getY()) && view.canScrollHorizontally(-1)) {
            return true;
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                if (isDisallowedView(motionEvent, viewGroup.getChildAt(i))) {
                    return true;
                }
            }
        }
        return false;
    }

    private void invalidateVisibility() {
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            if (i == 0) {
                if (this.transitionProgress == 1.0f && childAt.getVisibility() != 4) {
                    childAt.setVisibility(4);
                }
                if (!(this.transitionProgress == 1.0f || childAt.getVisibility() == 0)) {
                    childAt.setVisibility(0);
                }
            } else if (i == this.currentForegroundIndex) {
                if (this.transitionProgress == 0.0f && childAt.getVisibility() != 4) {
                    childAt.setVisibility(4);
                }
                if (!(this.transitionProgress == 0.0f || childAt.getVisibility() == 0)) {
                    childAt.setVisibility(0);
                }
            } else {
                childAt.setVisibility(4);
            }
        }
    }

    public void setNewForegroundHeight(int i, int i2) {
        this.overrideHeightIndex.put(i, i2);
        int i3 = this.currentForegroundIndex;
        if (i == i3 && i3 >= 0 && i3 < getChildCount()) {
            ValueAnimator valueAnimator = this.foregroundAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            View childAt = getChildAt(this.currentForegroundIndex);
            float f = this.overrideForegroundHeight;
            if (f == 0.0f) {
                f = (float) childAt.getMeasuredHeight();
            }
            ValueAnimator duration = ValueAnimator.ofFloat(new float[]{f, (float) i2}).setDuration(240);
            duration.setInterpolator(Easings.easeInOutQuad);
            duration.addUpdateListener(new PopupSwipeBackLayout$$ExternalSyntheticLambda0(this));
            duration.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    boolean unused = PopupSwipeBackLayout.this.isAnimationInProgress = false;
                }

                public void onAnimationStart(Animator animator) {
                    boolean unused = PopupSwipeBackLayout.this.isAnimationInProgress = true;
                }
            });
            duration.start();
            this.foregroundAnimator = duration;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setNewForegroundHeight$1(ValueAnimator valueAnimator) {
        this.overrideForegroundHeight = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidateTransforms();
    }
}
