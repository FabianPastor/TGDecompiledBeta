package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.view.View;
import java.util.ArrayList;

public class EllipsizeSpanAnimator {
    boolean attachedToWindow;
    /* access modifiers changed from: private */
    public final AnimatorSet ellAnimator;
    private final TextAlphaSpan[] ellSpans;
    public ArrayList<View> ellipsizedViews = new ArrayList<>();

    public EllipsizeSpanAnimator(final View parentView) {
        TextAlphaSpan[] textAlphaSpanArr = {new TextAlphaSpan(), new TextAlphaSpan(), new TextAlphaSpan()};
        this.ellSpans = textAlphaSpanArr;
        AnimatorSet animatorSet = new AnimatorSet();
        this.ellAnimator = animatorSet;
        animatorSet.playTogether(new Animator[]{createEllipsizeAnimator(textAlphaSpanArr[0], 0, 255, 0, 300), createEllipsizeAnimator(textAlphaSpanArr[1], 0, 255, 150, 300), createEllipsizeAnimator(textAlphaSpanArr[2], 0, 255, 300, 300), createEllipsizeAnimator(textAlphaSpanArr[0], 255, 0, 1000, 400), createEllipsizeAnimator(textAlphaSpanArr[1], 255, 0, 1000, 400), createEllipsizeAnimator(textAlphaSpanArr[2], 255, 0, 1000, 400)});
        animatorSet.addListener(new AnimatorListenerAdapter() {
            private Runnable restarter = new Runnable() {
                public void run() {
                    if (EllipsizeSpanAnimator.this.attachedToWindow && !EllipsizeSpanAnimator.this.ellipsizedViews.isEmpty() && !EllipsizeSpanAnimator.this.ellAnimator.isRunning()) {
                        try {
                            EllipsizeSpanAnimator.this.ellAnimator.start();
                        } catch (Exception e) {
                        }
                    }
                }
            };

            public void onAnimationEnd(Animator animation) {
                if (EllipsizeSpanAnimator.this.attachedToWindow) {
                    parentView.postDelayed(this.restarter, 300);
                }
            }
        });
    }

    public void wrap(SpannableString string, int start) {
        string.setSpan(this.ellSpans[0], start, start + 1, 0);
        string.setSpan(this.ellSpans[1], start + 1, start + 2, 0);
        string.setSpan(this.ellSpans[2], start + 2, start + 3, 0);
    }

    public void onAttachedToWindow() {
        this.attachedToWindow = true;
        if (!this.ellAnimator.isRunning()) {
            this.ellAnimator.start();
        }
    }

    public void onDetachedFromWindow() {
        this.attachedToWindow = false;
        this.ellAnimator.cancel();
    }

    public void reset() {
        for (TextAlphaSpan s : this.ellSpans) {
            s.setAlpha(0);
        }
    }

    private Animator createEllipsizeAnimator(TextAlphaSpan target, int startVal, int endVal, int startDelay, int duration) {
        ValueAnimator a = ValueAnimator.ofInt(new int[]{startVal, endVal});
        a.addUpdateListener(new EllipsizeSpanAnimator$$ExternalSyntheticLambda0(this, target));
        a.setDuration((long) duration);
        a.setStartDelay((long) startDelay);
        a.setInterpolator(CubicBezierInterpolator.DEFAULT);
        return a;
    }

    /* renamed from: lambda$createEllipsizeAnimator$0$org-telegram-ui-Components-EllipsizeSpanAnimator  reason: not valid java name */
    public /* synthetic */ void m920x871035ba(TextAlphaSpan target, ValueAnimator valueAnimator) {
        target.setAlpha(((Integer) valueAnimator.getAnimatedValue()).intValue());
        for (int i = 0; i < this.ellipsizedViews.size(); i++) {
            this.ellipsizedViews.get(i).invalidate();
        }
    }

    public void addView(View view) {
        if (this.ellipsizedViews.isEmpty()) {
            this.ellAnimator.start();
        }
        if (!this.ellipsizedViews.contains(view)) {
            this.ellipsizedViews.add(view);
        }
    }

    public void removeView(View view) {
        this.ellipsizedViews.remove(view);
        if (this.ellipsizedViews.isEmpty()) {
            this.ellAnimator.cancel();
        }
    }

    private static class TextAlphaSpan extends CharacterStyle {
        private int alpha = 0;

        public void setAlpha(int alpha2) {
            this.alpha = alpha2;
        }

        public void updateDrawState(TextPaint tp) {
            tp.setAlpha(this.alpha);
        }
    }
}
