package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.widget.HorizontalScrollView;
import androidx.core.math.MathUtils;
import org.telegram.messenger.AndroidUtilities;

/* compiled from: EmojiTabsStrip */
class ScrollableHorizontalScrollView extends HorizontalScrollView {
    private ValueAnimator scrollAnimator;
    protected boolean scrollingAnimation;
    private int scrollingTo = -1;

    public ScrollableHorizontalScrollView(Context context) {
        super(context);
    }

    public boolean scrollToVisible(int i, int i2) {
        int i3;
        if (getChildCount() <= 0) {
            return false;
        }
        int dp = AndroidUtilities.dp(50.0f);
        if (i < getScrollX() + dp) {
            i3 = i - dp;
        } else if (i2 <= getScrollX() + (getMeasuredWidth() - dp)) {
            return false;
        } else {
            i3 = (i2 - getMeasuredWidth()) + dp;
        }
        scrollTo(MathUtils.clamp(i3, 0, getChildAt(0).getMeasuredWidth() - getMeasuredWidth()));
        return true;
    }

    public void scrollTo(int i) {
        if (this.scrollingTo != i) {
            this.scrollingTo = i;
            ValueAnimator valueAnimator = this.scrollAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            if (getScrollX() != i) {
                ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{(float) getScrollX(), (float) i});
                this.scrollAnimator = ofFloat;
                ofFloat.addUpdateListener(new ScrollableHorizontalScrollView$$ExternalSyntheticLambda0(this));
                this.scrollAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                this.scrollAnimator.setDuration(250);
                this.scrollAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        ScrollableHorizontalScrollView.this.scrollingAnimation = false;
                    }

                    public void onAnimationStart(Animator animator) {
                        ScrollableHorizontalScrollView scrollableHorizontalScrollView = ScrollableHorizontalScrollView.this;
                        scrollableHorizontalScrollView.scrollingAnimation = true;
                        if (scrollableHorizontalScrollView.getParent() instanceof HorizontalScrollView) {
                            ((HorizontalScrollView) ScrollableHorizontalScrollView.this.getParent()).requestDisallowInterceptTouchEvent(false);
                        }
                    }
                });
                this.scrollAnimator.start();
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$scrollTo$0(ValueAnimator valueAnimator) {
        setScrollX((int) ((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    public void resetScrollTo() {
        this.scrollingTo = -1;
    }
}
