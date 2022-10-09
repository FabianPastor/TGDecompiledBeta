package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.widget.HorizontalScrollView;
import androidx.core.math.MathUtils;
import org.telegram.messenger.AndroidUtilities;
/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: EmojiTabsStrip.java */
/* loaded from: classes3.dex */
public class ScrollableHorizontalScrollView extends HorizontalScrollView {
    private ValueAnimator scrollAnimator;
    protected boolean scrollingAnimation;
    private int scrollingTo;

    public ScrollableHorizontalScrollView(Context context) {
        super(context);
        this.scrollingTo = -1;
    }

    public boolean scrollToVisible(int i, int i2) {
        int measuredWidth;
        if (getChildCount() <= 0) {
            return false;
        }
        int dp = AndroidUtilities.dp(50.0f);
        if (i < getScrollX() + dp) {
            measuredWidth = i - dp;
        } else if (i2 <= getScrollX() + (getMeasuredWidth() - dp)) {
            return false;
        } else {
            measuredWidth = (i2 - getMeasuredWidth()) + dp;
        }
        scrollTo(MathUtils.clamp(measuredWidth, 0, getChildAt(0).getMeasuredWidth() - getMeasuredWidth()));
        return true;
    }

    public void scrollTo(int i) {
        if (this.scrollingTo == i) {
            return;
        }
        this.scrollingTo = i;
        ValueAnimator valueAnimator = this.scrollAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        if (getScrollX() == i) {
            return;
        }
        ValueAnimator ofFloat = ValueAnimator.ofFloat(getScrollX(), i);
        this.scrollAnimator = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ScrollableHorizontalScrollView$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                ScrollableHorizontalScrollView.this.lambda$scrollTo$0(valueAnimator2);
            }
        });
        this.scrollAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        this.scrollAnimator.setDuration(250L);
        this.scrollAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ScrollableHorizontalScrollView.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                ScrollableHorizontalScrollView.this.scrollingAnimation = false;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
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

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$scrollTo$0(ValueAnimator valueAnimator) {
        setScrollX((int) ((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    public void resetScrollTo() {
        this.scrollingTo = -1;
    }
}
