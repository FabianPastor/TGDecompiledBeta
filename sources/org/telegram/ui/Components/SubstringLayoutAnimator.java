package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;

public class SubstringLayoutAnimator {
    private StaticLayout animateInLayout;
    private StaticLayout animateOutLayout;
    private StaticLayout animateStableLayout;
    public boolean animateTextChange;
    private boolean animateTextChangeOut;
    private float hintProgress;
    private final View parentView;
    private boolean replaceAnimation;
    ValueAnimator valueAnimator;
    private float xOffset;

    public SubstringLayoutAnimator(View parentView2) {
        this.parentView = parentView2;
    }

    public void create(StaticLayout hintLayout, CharSequence hint, CharSequence text, TextPaint paint) {
        String substring;
        String maxStr;
        boolean animateOut;
        float f;
        if (hintLayout != null && !hint.equals(text)) {
            ValueAnimator valueAnimator2 = this.valueAnimator;
            if (valueAnimator2 != null) {
                valueAnimator2.cancel();
            }
            if (hint.length() > text.length()) {
                animateOut = true;
                maxStr = hint.toString();
                substring = text.toString();
            } else {
                animateOut = false;
                maxStr = text.toString();
                substring = hint.toString();
            }
            int startFrom = maxStr.indexOf(substring);
            if (startFrom >= 0) {
                SpannableStringBuilder inStr = new SpannableStringBuilder(maxStr);
                SpannableStringBuilder stabeStr = new SpannableStringBuilder(maxStr);
                if (startFrom != 0) {
                    stabeStr.setSpan(new EmptyStubSpan(), 0, startFrom, 0);
                }
                if (substring.length() + startFrom != maxStr.length()) {
                    stabeStr.setSpan(new EmptyStubSpan(), substring.length() + startFrom, maxStr.length(), 0);
                }
                inStr.setSpan(new EmptyStubSpan(), startFrom, substring.length() + startFrom, 0);
                StaticLayout staticLayout = r10;
                StaticLayout staticLayout2 = new StaticLayout(inStr, paint, AndroidUtilities.dp(400.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                this.animateInLayout = staticLayout;
                StaticLayout staticLayout3 = new StaticLayout(stabeStr, paint, AndroidUtilities.dp(400.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                this.animateStableLayout = staticLayout3;
                this.animateTextChange = true;
                this.animateTextChangeOut = animateOut;
                this.xOffset = startFrom == 0 ? 0.0f : -staticLayout3.getPrimaryHorizontal(startFrom);
                this.animateOutLayout = null;
                this.replaceAnimation = false;
                f = 0.0f;
            } else {
                this.animateInLayout = new StaticLayout(text, paint, AndroidUtilities.dp(400.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                this.animateOutLayout = new StaticLayout(hint, paint, AndroidUtilities.dp(400.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                this.animateStableLayout = null;
                this.animateTextChange = true;
                this.replaceAnimation = true;
                f = 0.0f;
                this.xOffset = 0.0f;
            }
            this.hintProgress = f;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.valueAnimator = ofFloat;
            ofFloat.addUpdateListener(new SubstringLayoutAnimator$$ExternalSyntheticLambda0(this));
            this.valueAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    SubstringLayoutAnimator.this.animateTextChange = false;
                }
            });
            this.valueAnimator.setDuration(150);
            this.valueAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.valueAnimator.start();
        }
    }

    /* renamed from: lambda$create$0$org-telegram-ui-Components-SubstringLayoutAnimator  reason: not valid java name */
    public /* synthetic */ void m4449xebd39380(ValueAnimator valueAnimator1) {
        this.hintProgress = ((Float) valueAnimator1.getAnimatedValue()).floatValue();
        this.parentView.invalidate();
    }

    public void draw(Canvas canvas, TextPaint paint) {
        if (this.animateTextChange) {
            float titleOffsetX = this.xOffset * (this.animateTextChangeOut ? this.hintProgress : 1.0f - this.hintProgress);
            int alpha = paint.getAlpha();
            if (this.animateStableLayout != null) {
                canvas.save();
                canvas.translate(titleOffsetX, 0.0f);
                this.animateStableLayout.draw(canvas);
                canvas.restore();
            }
            if (this.animateInLayout != null) {
                float p = this.animateTextChangeOut ? 1.0f - this.hintProgress : this.hintProgress;
                canvas.save();
                paint.setAlpha((int) (((float) alpha) * p));
                canvas.translate(titleOffsetX, 0.0f);
                if (this.replaceAnimation) {
                    float s = (p * 0.1f) + 0.9f;
                    canvas.scale(s, s, titleOffsetX, ((float) this.parentView.getMeasuredHeight()) / 2.0f);
                }
                this.animateInLayout.draw(canvas);
                canvas.restore();
                paint.setAlpha(alpha);
            }
            if (this.animateOutLayout != null) {
                float p2 = this.animateTextChangeOut ? this.hintProgress : 1.0f - this.hintProgress;
                canvas.save();
                paint.setAlpha((int) (((float) alpha) * (this.animateTextChangeOut ? this.hintProgress : 1.0f - this.hintProgress)));
                canvas.translate(titleOffsetX, 0.0f);
                if (this.replaceAnimation) {
                    float s2 = (0.1f * p2) + 0.9f;
                    canvas.scale(s2, s2, titleOffsetX, ((float) this.parentView.getMeasuredHeight()) / 2.0f);
                }
                this.animateOutLayout.draw(canvas);
                canvas.restore();
                paint.setAlpha(alpha);
            }
        }
    }

    public void cancel() {
        ValueAnimator valueAnimator2 = this.valueAnimator;
        if (valueAnimator2 != null) {
            valueAnimator2.cancel();
        }
        this.animateTextChange = false;
    }
}
