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

    public SubstringLayoutAnimator(View view) {
        this.parentView = view;
    }

    public void create(StaticLayout staticLayout, CharSequence charSequence, CharSequence charSequence2, TextPaint textPaint) {
        boolean z;
        String str;
        String str2;
        float f;
        if (staticLayout != null && !charSequence.equals(charSequence2)) {
            ValueAnimator valueAnimator2 = this.valueAnimator;
            if (valueAnimator2 != null) {
                valueAnimator2.cancel();
            }
            if (charSequence.length() > charSequence2.length()) {
                str2 = charSequence.toString();
                str = charSequence2.toString();
                z = true;
            } else {
                str2 = charSequence2.toString();
                str = charSequence.toString();
                z = false;
            }
            int indexOf = str2.indexOf(str);
            if (indexOf >= 0) {
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(str2);
                SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder(str2);
                if (indexOf != 0) {
                    spannableStringBuilder2.setSpan(new EmptyStubSpan(), 0, indexOf, 0);
                }
                if (str.length() + indexOf != str2.length()) {
                    spannableStringBuilder2.setSpan(new EmptyStubSpan(), str.length() + indexOf, str2.length(), 0);
                }
                spannableStringBuilder.setSpan(new EmptyStubSpan(), indexOf, str.length() + indexOf, 0);
                this.animateInLayout = new StaticLayout(spannableStringBuilder, textPaint, AndroidUtilities.dp(400.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                StaticLayout staticLayout2 = new StaticLayout(spannableStringBuilder2, textPaint, AndroidUtilities.dp(400.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                this.animateStableLayout = staticLayout2;
                this.animateTextChange = true;
                this.animateTextChangeOut = z;
                if (indexOf == 0) {
                    f = 0.0f;
                } else {
                    f = -staticLayout2.getPrimaryHorizontal(indexOf);
                }
                this.xOffset = f;
                this.animateOutLayout = null;
                this.replaceAnimation = false;
            } else {
                this.animateInLayout = new StaticLayout(charSequence2, textPaint, AndroidUtilities.dp(400.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                this.animateOutLayout = new StaticLayout(charSequence, textPaint, AndroidUtilities.dp(400.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                this.animateStableLayout = null;
                this.animateTextChange = true;
                this.replaceAnimation = true;
                this.xOffset = 0.0f;
            }
            this.hintProgress = 0.0f;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.valueAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    SubstringLayoutAnimator.this.lambda$create$0$SubstringLayoutAnimator(valueAnimator);
                }
            });
            this.valueAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    SubstringLayoutAnimator.this.animateTextChange = false;
                }
            });
            this.valueAnimator.setDuration(150);
            this.valueAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.valueAnimator.start();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$create$0 */
    public /* synthetic */ void lambda$create$0$SubstringLayoutAnimator(ValueAnimator valueAnimator2) {
        this.hintProgress = ((Float) valueAnimator2.getAnimatedValue()).floatValue();
        this.parentView.invalidate();
    }

    public void draw(Canvas canvas, TextPaint textPaint) {
        if (this.animateTextChange) {
            float f = this.xOffset * (this.animateTextChangeOut ? this.hintProgress : 1.0f - this.hintProgress);
            int alpha = textPaint.getAlpha();
            if (this.animateStableLayout != null) {
                canvas.save();
                canvas.translate(f, 0.0f);
                this.animateStableLayout.draw(canvas);
                canvas.restore();
            }
            if (this.animateInLayout != null) {
                float f2 = this.animateTextChangeOut ? 1.0f - this.hintProgress : this.hintProgress;
                canvas.save();
                textPaint.setAlpha((int) (((float) alpha) * f2));
                canvas.translate(f, 0.0f);
                if (this.replaceAnimation) {
                    float f3 = (f2 * 0.1f) + 0.9f;
                    canvas.scale(f3, f3, f, ((float) this.parentView.getMeasuredHeight()) / 2.0f);
                }
                this.animateInLayout.draw(canvas);
                canvas.restore();
                textPaint.setAlpha(alpha);
            }
            if (this.animateOutLayout != null) {
                float f4 = this.animateTextChangeOut ? this.hintProgress : 1.0f - this.hintProgress;
                canvas.save();
                textPaint.setAlpha((int) (((float) alpha) * (this.animateTextChangeOut ? this.hintProgress : 1.0f - this.hintProgress)));
                canvas.translate(f, 0.0f);
                if (this.replaceAnimation) {
                    float f5 = (f4 * 0.1f) + 0.9f;
                    canvas.scale(f5, f5, f, ((float) this.parentView.getMeasuredHeight()) / 2.0f);
                }
                this.animateOutLayout.draw(canvas);
                canvas.restore();
                textPaint.setAlpha(alpha);
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
