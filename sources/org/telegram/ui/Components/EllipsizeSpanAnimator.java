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
import org.telegram.ui.Components.EllipsizeSpanAnimator;

public class EllipsizeSpanAnimator {
    boolean attachedToWindow;
    /* access modifiers changed from: private */
    public final AnimatorSet ellAnimator;
    private final TextAlphaSpan[] ellSpans;
    public ArrayList<View> ellipsizedViews = new ArrayList<>();

    public EllipsizeSpanAnimator(final View view) {
        TextAlphaSpan[] textAlphaSpanArr = {new TextAlphaSpan(), new TextAlphaSpan(), new TextAlphaSpan()};
        this.ellSpans = textAlphaSpanArr;
        AnimatorSet animatorSet = new AnimatorSet();
        this.ellAnimator = animatorSet;
        animatorSet.playTogether(new Animator[]{createEllipsizeAnimator(textAlphaSpanArr[0], 0, 255, 0, 300), createEllipsizeAnimator(textAlphaSpanArr[1], 0, 255, 150, 300), createEllipsizeAnimator(textAlphaSpanArr[2], 0, 255, 300, 300), createEllipsizeAnimator(textAlphaSpanArr[0], 255, 0, 1000, 400), createEllipsizeAnimator(textAlphaSpanArr[1], 255, 0, 1000, 400), createEllipsizeAnimator(textAlphaSpanArr[2], 255, 0, 1000, 400)});
        animatorSet.addListener(new AnimatorListenerAdapter() {
            private Runnable restarter = new Runnable() {
                public void run() {
                    EllipsizeSpanAnimator ellipsizeSpanAnimator = EllipsizeSpanAnimator.this;
                    if (ellipsizeSpanAnimator.attachedToWindow && !ellipsizeSpanAnimator.ellipsizedViews.isEmpty() && !EllipsizeSpanAnimator.this.ellAnimator.isRunning()) {
                        try {
                            EllipsizeSpanAnimator.this.ellAnimator.start();
                        } catch (Exception unused) {
                        }
                    }
                }
            };

            public void onAnimationEnd(Animator animator) {
                if (EllipsizeSpanAnimator.this.attachedToWindow) {
                    view.postDelayed(this.restarter, 300);
                }
            }
        });
    }

    public void wrap(SpannableString spannableString, int i) {
        int i2 = i + 1;
        spannableString.setSpan(this.ellSpans[0], i, i2, 0);
        int i3 = i + 2;
        spannableString.setSpan(this.ellSpans[1], i2, i3, 0);
        spannableString.setSpan(this.ellSpans[2], i3, i + 3, 0);
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
        for (TextAlphaSpan alpha : this.ellSpans) {
            alpha.setAlpha(0);
        }
    }

    private Animator createEllipsizeAnimator(TextAlphaSpan textAlphaSpan, int i, int i2, int i3, int i4) {
        ValueAnimator ofInt = ValueAnimator.ofInt(new int[]{i, i2});
        ofInt.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(textAlphaSpan) {
            public final /* synthetic */ EllipsizeSpanAnimator.TextAlphaSpan f$1;

            {
                this.f$1 = r2;
            }

            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                EllipsizeSpanAnimator.this.lambda$createEllipsizeAnimator$0$EllipsizeSpanAnimator(this.f$1, valueAnimator);
            }
        });
        ofInt.setDuration((long) i4);
        ofInt.setStartDelay((long) i3);
        ofInt.setInterpolator(CubicBezierInterpolator.DEFAULT);
        return ofInt;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createEllipsizeAnimator$0 */
    public /* synthetic */ void lambda$createEllipsizeAnimator$0$EllipsizeSpanAnimator(TextAlphaSpan textAlphaSpan, ValueAnimator valueAnimator) {
        textAlphaSpan.setAlpha(((Integer) valueAnimator.getAnimatedValue()).intValue());
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

        public void setAlpha(int i) {
            this.alpha = i;
        }

        public void updateDrawState(TextPaint textPaint) {
            textPaint.setAlpha(this.alpha);
        }
    }
}
