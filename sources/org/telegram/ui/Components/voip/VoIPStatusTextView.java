package org.telegram.ui.Components.voip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.voip.VoIPStatusTextView;

public class VoIPStatusTextView extends FrameLayout {
    boolean animationInProgress;
    ValueAnimator animator;
    /* access modifiers changed from: private */
    public boolean attachedToWindow;
    /* access modifiers changed from: private */
    public AnimatorSet ellAnimator;
    private TextAlphaSpan[] ellSpans;
    CharSequence nextTextToSet;
    TextView[] textView = new TextView[2];
    boolean timerShowing;
    VoIPTimerView timerView;

    public VoIPStatusTextView(Context context) {
        super(context);
        TextAlphaSpan[] textAlphaSpanArr = {new TextAlphaSpan(this), new TextAlphaSpan(this), new TextAlphaSpan(this)};
        this.ellSpans = textAlphaSpanArr;
        this.ellSpans = textAlphaSpanArr;
        for (int i = 0; i < 2; i++) {
            this.textView[i] = new TextView(context);
            this.textView[i].setTextSize(15.0f);
            this.textView[i].setShadowLayer((float) AndroidUtilities.dp(3.0f), 0.0f, (float) AndroidUtilities.dp(0.6666667f), NUM);
            this.textView[i].setTextColor(-1);
            this.textView[i].setGravity(1);
            addView(this.textView[i]);
        }
        VoIPTimerView voIPTimerView = new VoIPTimerView(context);
        this.timerView = voIPTimerView;
        addView(voIPTimerView, LayoutHelper.createFrame(-1, -2.0f));
        AnimatorSet animatorSet = new AnimatorSet();
        this.ellAnimator = animatorSet;
        animatorSet.playTogether(new Animator[]{createEllipsizeAnimator(this.ellSpans[0], 0, 255, 0, 300), createEllipsizeAnimator(this.ellSpans[1], 0, 255, 150, 300), createEllipsizeAnimator(this.ellSpans[2], 0, 255, 300, 300), createEllipsizeAnimator(this.ellSpans[0], 255, 0, 1000, 400), createEllipsizeAnimator(this.ellSpans[1], 255, 0, 1000, 400), createEllipsizeAnimator(this.ellSpans[2], 255, 0, 1000, 400)});
        this.ellAnimator.addListener(new AnimatorListenerAdapter() {
            private Runnable restarter = new Runnable() {
                public void run() {
                    if (VoIPStatusTextView.this.attachedToWindow) {
                        VoIPStatusTextView.this.ellAnimator.start();
                    }
                }
            };

            public void onAnimationEnd(Animator animator) {
                if (VoIPStatusTextView.this.attachedToWindow) {
                    VoIPStatusTextView.this.postDelayed(this.restarter, 300);
                }
            }
        });
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v22, resolved type: android.text.SpannableStringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v12, resolved type: java.lang.String} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setText(java.lang.String r6, boolean r7, boolean r8) {
        /*
            r5 = this;
            r0 = 1
            r1 = 0
            if (r7 == 0) goto L_0x0039
            android.text.SpannableStringBuilder r7 = new android.text.SpannableStringBuilder
            r7.<init>(r6)
            org.telegram.ui.Components.voip.VoIPStatusTextView$TextAlphaSpan[] r6 = r5.ellSpans
            int r2 = r6.length
            r3 = 0
        L_0x000d:
            if (r3 >= r2) goto L_0x0017
            r4 = r6[r3]
            r4.setAlpha(r1)
            int r3 = r3 + 1
            goto L_0x000d
        L_0x0017:
            android.text.SpannableString r6 = new android.text.SpannableString
            java.lang.String r2 = "..."
            r6.<init>(r2)
            org.telegram.ui.Components.voip.VoIPStatusTextView$TextAlphaSpan[] r2 = r5.ellSpans
            r2 = r2[r1]
            r6.setSpan(r2, r1, r0, r1)
            org.telegram.ui.Components.voip.VoIPStatusTextView$TextAlphaSpan[] r2 = r5.ellSpans
            r2 = r2[r0]
            r3 = 2
            r6.setSpan(r2, r0, r3, r1)
            org.telegram.ui.Components.voip.VoIPStatusTextView$TextAlphaSpan[] r2 = r5.ellSpans
            r2 = r2[r3]
            r4 = 3
            r6.setSpan(r2, r3, r4, r1)
            r7.append(r6)
            r6 = r7
        L_0x0039:
            android.widget.TextView[] r7 = r5.textView
            r7 = r7[r1]
            java.lang.CharSequence r7 = r7.getText()
            boolean r7 = android.text.TextUtils.isEmpty(r7)
            if (r7 == 0) goto L_0x0048
            r8 = 0
        L_0x0048:
            if (r8 != 0) goto L_0x0070
            android.animation.ValueAnimator r7 = r5.animator
            if (r7 == 0) goto L_0x0051
            r7.cancel()
        L_0x0051:
            r5.animationInProgress = r1
            android.widget.TextView[] r7 = r5.textView
            r7 = r7[r1]
            r7.setText(r6)
            android.widget.TextView[] r6 = r5.textView
            r6 = r6[r1]
            r6.setVisibility(r1)
            android.widget.TextView[] r6 = r5.textView
            r6 = r6[r0]
            r7 = 8
            r6.setVisibility(r7)
            org.telegram.ui.Components.voip.VoIPTimerView r6 = r5.timerView
            r6.setVisibility(r7)
            goto L_0x00b0
        L_0x0070:
            boolean r7 = r5.animationInProgress
            if (r7 == 0) goto L_0x0077
            r5.nextTextToSet = r6
            return
        L_0x0077:
            boolean r7 = r5.timerShowing
            if (r7 == 0) goto L_0x008d
            android.widget.TextView[] r7 = r5.textView
            r7 = r7[r1]
            r7.setText(r6)
            org.telegram.ui.Components.voip.VoIPTimerView r6 = r5.timerView
            android.widget.TextView[] r7 = r5.textView
            r7 = r7[r1]
            r8 = 0
            r5.replaceViews(r6, r7, r8)
            goto L_0x00b0
        L_0x008d:
            android.widget.TextView[] r7 = r5.textView
            r7 = r7[r1]
            java.lang.CharSequence r7 = r7.getText()
            boolean r7 = r7.equals(r6)
            if (r7 != 0) goto L_0x00b0
            android.widget.TextView[] r7 = r5.textView
            r7 = r7[r0]
            r7.setText(r6)
            android.widget.TextView[] r6 = r5.textView
            r7 = r6[r1]
            r6 = r6[r0]
            org.telegram.ui.Components.voip.-$$Lambda$VoIPStatusTextView$NnszBeG4p9PQxoQw7aG18fdhXFQ r8 = new org.telegram.ui.Components.voip.-$$Lambda$VoIPStatusTextView$NnszBeG4p9PQxoQw7aG18fdhXFQ
            r8.<init>()
            r5.replaceViews(r7, r6, r8)
        L_0x00b0:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.VoIPStatusTextView.setText(java.lang.String, boolean, boolean):void");
    }

    public /* synthetic */ void lambda$setText$0$VoIPStatusTextView() {
        TextView[] textViewArr = this.textView;
        TextView textView2 = textViewArr[0];
        textViewArr[0] = textViewArr[1];
        textViewArr[1] = textView2;
    }

    public void showTimer(boolean z) {
        if (TextUtils.isEmpty(this.textView[0].getText())) {
            z = false;
        }
        if (!this.timerShowing) {
            this.timerView.updateTimer();
            if (!z) {
                ValueAnimator valueAnimator = this.animator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                }
                this.timerShowing = true;
                this.animationInProgress = false;
                this.textView[0].setVisibility(8);
                this.textView[1].setVisibility(8);
                this.timerView.setVisibility(0);
            } else if (this.animationInProgress) {
                this.nextTextToSet = "timer";
            } else {
                this.timerShowing = true;
                replaceViews(this.textView[0], this.timerView, (Runnable) null);
            }
        }
    }

    /* access modifiers changed from: private */
    public void replaceViews(final View view, final View view2, final Runnable runnable) {
        view.setVisibility(0);
        view2.setVisibility(0);
        view2.setTranslationY((float) AndroidUtilities.dp(15.0f));
        view2.setAlpha(0.0f);
        this.animationInProgress = true;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        this.animator = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(view2, view) {
            public final /* synthetic */ View f$0;
            public final /* synthetic */ View f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                VoIPStatusTextView.lambda$replaceViews$1(this.f$0, this.f$1, valueAnimator);
            }
        });
        this.animator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                view.setVisibility(8);
                view.setAlpha(1.0f);
                view.setTranslationY(0.0f);
                view.setScaleY(1.0f);
                view.setScaleX(1.0f);
                view2.setAlpha(1.0f);
                view2.setTranslationY(0.0f);
                view2.setVisibility(0);
                view2.setScaleY(1.0f);
                view2.setScaleX(1.0f);
                Runnable runnable = runnable;
                if (runnable != null) {
                    runnable.run();
                }
                VoIPStatusTextView voIPStatusTextView = VoIPStatusTextView.this;
                voIPStatusTextView.animationInProgress = false;
                CharSequence charSequence = voIPStatusTextView.nextTextToSet;
                if (charSequence != null) {
                    if (charSequence.equals("timer")) {
                        VoIPStatusTextView.this.showTimer(true);
                    } else {
                        VoIPStatusTextView voIPStatusTextView2 = VoIPStatusTextView.this;
                        voIPStatusTextView2.textView[1].setText(voIPStatusTextView2.nextTextToSet);
                        VoIPStatusTextView voIPStatusTextView3 = VoIPStatusTextView.this;
                        TextView[] textViewArr = voIPStatusTextView3.textView;
                        voIPStatusTextView3.replaceViews(textViewArr[0], textViewArr[1], new Runnable() {
                            public final void run() {
                                VoIPStatusTextView.AnonymousClass2.this.lambda$onAnimationEnd$0$VoIPStatusTextView$2();
                            }
                        });
                    }
                    VoIPStatusTextView.this.nextTextToSet = null;
                }
            }

            public /* synthetic */ void lambda$onAnimationEnd$0$VoIPStatusTextView$2() {
                TextView[] textViewArr = VoIPStatusTextView.this.textView;
                TextView textView = textViewArr[0];
                textViewArr[0] = textViewArr[1];
                textViewArr[1] = textView;
            }
        });
        this.animator.setDuration(250).setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.animator.start();
    }

    static /* synthetic */ void lambda$replaceViews$1(View view, View view2, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        float f = (floatValue * 0.6f) + 0.4f;
        float f2 = 1.0f - floatValue;
        float f3 = (0.6f * f2) + 0.4f;
        view.setTranslationY(((float) AndroidUtilities.dp(10.0f)) * f2);
        view.setAlpha(floatValue);
        view.setScaleX(f);
        view.setScaleY(f);
        view2.setTranslationY(((float) (-AndroidUtilities.dp(10.0f))) * floatValue);
        view2.setAlpha(f2);
        view2.setScaleX(f3);
        view2.setScaleY(f3);
    }

    public void setSignalBarCount(int i) {
        this.timerView.setSignalBarCount(i);
    }

    private Animator createEllipsizeAnimator(TextAlphaSpan textAlphaSpan, int i, int i2, int i3, int i4) {
        ValueAnimator ofInt = ValueAnimator.ofInt(new int[]{i, i2});
        ofInt.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(textAlphaSpan) {
            public final /* synthetic */ VoIPStatusTextView.TextAlphaSpan f$1;

            {
                this.f$1 = r2;
            }

            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                VoIPStatusTextView.this.lambda$createEllipsizeAnimator$2$VoIPStatusTextView(this.f$1, valueAnimator);
            }
        });
        ofInt.setDuration((long) i4);
        ofInt.setStartDelay((long) i3);
        ofInt.setInterpolator(CubicBezierInterpolator.DEFAULT);
        return ofInt;
    }

    public /* synthetic */ void lambda$createEllipsizeAnimator$2$VoIPStatusTextView(TextAlphaSpan textAlphaSpan, ValueAnimator valueAnimator) {
        textAlphaSpan.setAlpha(((Integer) valueAnimator.getAnimatedValue()).intValue());
        if (!this.timerShowing || this.animationInProgress) {
            this.textView[0].invalidate();
            this.textView[1].invalidate();
        }
    }

    private class TextAlphaSpan extends CharacterStyle {
        private int alpha = 0;

        public TextAlphaSpan(VoIPStatusTextView voIPStatusTextView) {
        }

        public void setAlpha(int i) {
            this.alpha = i;
        }

        public void updateDrawState(TextPaint textPaint) {
            textPaint.setAlpha(this.alpha);
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.attachedToWindow = true;
        if (!this.ellAnimator.isRunning()) {
            this.ellAnimator.start();
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.attachedToWindow = false;
        this.ellAnimator.removeAllListeners();
        this.ellAnimator.cancel();
    }
}
