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
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.Theme;

public class PopupSwipeBackLayout extends FrameLayout {
    /* access modifiers changed from: private */
    public int currentForegroundIndex = -1;
    private GestureDetectorCompat detector;
    /* access modifiers changed from: private */
    public ValueAnimator foregroundAnimator;
    private int foregroundColor = 0;
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
    private int lastHeightReported = -1;
    private Path mPath = new Path();
    private RectF mRect = new RectF();
    /* access modifiers changed from: private */
    public int notificationIndex;
    private IntCallback onHeightUpdateListener;
    private ArrayList<OnSwipeBackProgressListener> onSwipeBackProgressListeners = new ArrayList<>();
    private Paint overlayPaint = new Paint(1);
    private float overrideForegroundHeight;
    SparseIntArray overrideHeightIndex = new SparseIntArray();
    Theme.ResourcesProvider resourcesProvider;
    /* access modifiers changed from: private */
    public float toProgress = -1.0f;
    public float transitionProgress;

    public interface IntCallback {
        void run(int i);
    }

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
                    PopupSwipeBackLayout.this.transitionProgress = 1.0f - Math.max(0.0f, Math.min(1.0f, (motionEvent2.getX() - motionEvent.getX()) / ((float) PopupSwipeBackLayout.this.getWidth())));
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

    public void addOnSwipeBackProgressListener(OnSwipeBackProgressListener onSwipeBackProgressListener) {
        this.onSwipeBackProgressListeners.add(onSwipeBackProgressListener);
    }

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View view, long j) {
        int indexOfChild = indexOfChild(view);
        int save = canvas.save();
        if (indexOfChild != 0) {
            int i = this.foregroundColor;
            if (i == 0) {
                this.foregroundPaint.setColor(Theme.getColor("actionBarDefaultSubmenuBackground", this.resourcesProvider));
            } else {
                this.foregroundPaint.setColor(i);
            }
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

    public void invalidateTransforms() {
        invalidateTransforms(true);
    }

    public void invalidateTransforms(boolean z) {
        float f;
        float f2;
        if (!this.onSwipeBackProgressListeners.isEmpty()) {
            for (int i = 0; i < this.onSwipeBackProgressListeners.size(); i++) {
                this.onSwipeBackProgressListeners.get(i).onSwipeBackProgress(this, this.toProgress, this.transitionProgress);
            }
        }
        View childAt = getChildAt(0);
        View view = null;
        int i2 = this.currentForegroundIndex;
        if (i2 >= 0 && i2 < getChildCount()) {
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
            actionBarPopupWindowLayout.updateAnimation = false;
            actionBarPopupWindowLayout.setBackScaleX(paddingLeft / ((float) actionBarPopupWindowLayout.getMeasuredWidth()));
            if (z) {
                actionBarPopupWindowLayout.setBackScaleY(paddingTop / ((float) actionBarPopupWindowLayout.getMeasuredHeight()));
            }
            actionBarPopupWindowLayout.updateAnimation = true;
            for (int i3 = 0; i3 < getChildCount(); i3++) {
                View childAt2 = getChildAt(i3);
                childAt2.setPivotX(0.0f);
                childAt2.setPivotY(0.0f);
            }
            invalidate();
        }
    }

    public boolean isForegroundOpen() {
        return this.transitionProgress > 0.0f;
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (processTouchEvent(motionEvent)) {
            return true;
        }
        int actionMasked = motionEvent.getActionMasked();
        RectF rectF = this.mRect;
        if (rectF != null) {
            rectF.contains(motionEvent.getX(), motionEvent.getY());
        }
        if (actionMasked != 0 || this.mRect.contains(motionEvent.getX(), motionEvent.getY())) {
            int i = this.currentForegroundIndex;
            if (i < 0 || i >= getChildCount()) {
                return super.dispatchTouchEvent(motionEvent);
            }
            View childAt = getChildAt(0);
            View childAt2 = getChildAt(this.currentForegroundIndex);
            if (this.transitionProgress > 0.5f) {
                childAt = childAt2;
            }
            boolean dispatchTouchEvent = childAt.dispatchTouchEvent(motionEvent);
            if ((dispatchTouchEvent || actionMasked != 0) && !dispatchTouchEvent && !onTouchEvent(motionEvent)) {
                return false;
            }
            return true;
        }
        callOnClick();
        return true;
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
                PopupSwipeBackLayout popupSwipeBackLayout = PopupSwipeBackLayout.this;
                float f = f;
                popupSwipeBackLayout.transitionProgress = f;
                if (f <= 0.0f) {
                    int unused = popupSwipeBackLayout.currentForegroundIndex = -1;
                }
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
        closeForeground(true);
    }

    public void closeForeground(boolean z) {
        if (!this.isAnimationInProgress) {
            if (!z) {
                this.currentForegroundIndex = -1;
                this.transitionProgress = 0.0f;
                invalidateTransforms();
                return;
            }
            animateToState(0.0f, 0.0f);
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        for (int i5 = 0; i5 < getChildCount(); i5++) {
            View childAt = getChildAt(i5);
            if ((childAt.getLayoutParams() instanceof FrameLayout.LayoutParams) && ((FrameLayout.LayoutParams) childAt.getLayoutParams()).gravity == 80) {
                int i6 = i4 - i2;
                childAt.layout(0, i6 - childAt.getMeasuredHeight(), childAt.getMeasuredWidth(), i6);
            } else {
                childAt.layout(0, 0, childAt.getMeasuredWidth(), childAt.getMeasuredHeight());
            }
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
            float top = (float) childAt.getTop();
            float measuredWidth = (float) childAt.getMeasuredWidth();
            float measuredHeight = (float) childAt.getMeasuredHeight();
            int i = this.currentForegroundIndex;
            if (i != -1 && i < getChildCount()) {
                View childAt2 = getChildAt(this.currentForegroundIndex);
                float top2 = (float) childAt2.getTop();
                float measuredWidth2 = (float) childAt2.getMeasuredWidth();
                float f = this.overrideForegroundHeight;
                if (f == 0.0f) {
                    f = (float) childAt2.getMeasuredHeight();
                }
                if (!(childAt.getMeasuredWidth() == 0 || childAt.getMeasuredHeight() == 0 || childAt2.getMeasuredWidth() == 0 || childAt2.getMeasuredHeight() == 0)) {
                    top = AndroidUtilities.lerp(top, top2, this.transitionProgress);
                    measuredWidth = AndroidUtilities.lerp(measuredWidth, measuredWidth2, this.transitionProgress);
                    measuredHeight = AndroidUtilities.lerp(measuredHeight, f, this.transitionProgress);
                }
            }
            int save = canvas.save();
            this.mPath.rewind();
            int dp = AndroidUtilities.dp(6.0f);
            this.mRect.set(0.0f, top, measuredWidth, measuredHeight + top);
            float f2 = (float) dp;
            this.mPath.addRoundRect(this.mRect, f2, f2, Path.Direction.CW);
            canvas.clipPath(this.mPath);
            super.dispatchDraw(canvas);
            canvas.restoreToCount(save);
            if (this.onHeightUpdateListener != null && ((float) this.lastHeightReported) != this.mRect.height()) {
                IntCallback intCallback = this.onHeightUpdateListener;
                int height = (int) this.mRect.height();
                this.lastHeightReported = height;
                intCallback.run(height);
            }
        }
    }

    public void setOnHeightUpdateListener(IntCallback intCallback) {
        this.onHeightUpdateListener = intCallback;
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

    public void setNewForegroundHeight(int i, int i2, boolean z) {
        this.overrideHeightIndex.put(i, i2);
        int i3 = this.currentForegroundIndex;
        if (i == i3 && i3 >= 0 && i3 < getChildCount()) {
            ValueAnimator valueAnimator = this.foregroundAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
                this.foregroundAnimator = null;
            }
            if (z) {
                View childAt = getChildAt(this.currentForegroundIndex);
                float f = this.overrideForegroundHeight;
                if (f == 0.0f) {
                    f = (float) childAt.getMeasuredHeight();
                }
                ValueAnimator duration = ValueAnimator.ofFloat(new float[]{f, (float) i2}).setDuration(240);
                duration.setInterpolator(Easings.easeInOutQuad);
                duration.addUpdateListener(new PopupSwipeBackLayout$$ExternalSyntheticLambda0(this));
                this.isAnimationInProgress = true;
                duration.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        boolean unused = PopupSwipeBackLayout.this.isAnimationInProgress = false;
                        ValueAnimator unused2 = PopupSwipeBackLayout.this.foregroundAnimator = null;
                    }
                });
                duration.start();
                this.foregroundAnimator = duration;
                return;
            }
            this.overrideForegroundHeight = (float) i2;
            invalidateTransforms();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setNewForegroundHeight$1(ValueAnimator valueAnimator) {
        this.overrideForegroundHeight = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidateTransforms();
    }

    public void setForegroundColor(int i) {
        this.foregroundColor = i;
    }
}
