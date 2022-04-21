package org.telegram.ui.Components.voip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EllipsizeSpanAnimator;
import org.telegram.ui.Components.LayoutHelper;

public class VoIPStatusTextView extends FrameLayout {
    boolean animationInProgress;
    ValueAnimator animator;
    private boolean attachedToWindow;
    EllipsizeSpanAnimator ellipsizeAnimator;
    CharSequence nextTextToSet;
    TextView reconnectTextView;
    TextView[] textView = new TextView[2];
    boolean timerShowing;
    VoIPTimerView timerView;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public VoIPStatusTextView(Context context) {
        super(context);
        Context context2 = context;
        for (int i = 0; i < 2; i++) {
            this.textView[i] = new TextView(context2);
            this.textView[i].setTextSize(1, 15.0f);
            this.textView[i].setShadowLayer((float) AndroidUtilities.dp(3.0f), 0.0f, (float) AndroidUtilities.dp(0.6666667f), NUM);
            this.textView[i].setTextColor(-1);
            this.textView[i].setGravity(1);
            addView(this.textView[i]);
        }
        TextView textView2 = new TextView(context2);
        this.reconnectTextView = textView2;
        textView2.setTextSize(1, 15.0f);
        this.reconnectTextView.setShadowLayer((float) AndroidUtilities.dp(3.0f), 0.0f, (float) AndroidUtilities.dp(0.6666667f), NUM);
        this.reconnectTextView.setTextColor(-1);
        this.reconnectTextView.setGravity(1);
        addView(this.reconnectTextView, LayoutHelper.createFrame(-1, -2.0f, 0, 0.0f, 22.0f, 0.0f, 0.0f));
        this.ellipsizeAnimator = new EllipsizeSpanAnimator(this);
        SpannableStringBuilder ssb = new SpannableStringBuilder(LocaleController.getString("VoipReconnecting", NUM));
        SpannableString ell = new SpannableString("...");
        this.ellipsizeAnimator.wrap(ell, 0);
        ssb.append(ell);
        this.reconnectTextView.setText(ssb);
        this.reconnectTextView.setVisibility(8);
        VoIPTimerView voIPTimerView = new VoIPTimerView(context2);
        this.timerView = voIPTimerView;
        addView(voIPTimerView, LayoutHelper.createFrame(-1, -2.0f));
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v23, resolved type: android.text.SpannableStringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v2, resolved type: java.lang.String} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setText(java.lang.String r8, boolean r9, boolean r10) {
        /*
            r7 = this;
            r0 = r8
            r1 = 1
            r2 = 0
            if (r9 == 0) goto L_0x0032
            android.text.SpannableStringBuilder r3 = new android.text.SpannableStringBuilder
            r3.<init>(r8)
            org.telegram.ui.Components.EllipsizeSpanAnimator r4 = r7.ellipsizeAnimator
            r4.reset()
            android.text.SpannableString r4 = new android.text.SpannableString
            java.lang.String r5 = "..."
            r4.<init>(r5)
            org.telegram.ui.Components.EllipsizeSpanAnimator r5 = r7.ellipsizeAnimator
            r5.wrap(r4, r2)
            r3.append(r4)
            r0 = r3
            org.telegram.ui.Components.EllipsizeSpanAnimator r5 = r7.ellipsizeAnimator
            android.widget.TextView[] r6 = r7.textView
            r6 = r6[r2]
            r5.addView(r6)
            org.telegram.ui.Components.EllipsizeSpanAnimator r5 = r7.ellipsizeAnimator
            android.widget.TextView[] r6 = r7.textView
            r6 = r6[r1]
            r5.addView(r6)
            goto L_0x0044
        L_0x0032:
            org.telegram.ui.Components.EllipsizeSpanAnimator r3 = r7.ellipsizeAnimator
            android.widget.TextView[] r4 = r7.textView
            r4 = r4[r2]
            r3.removeView(r4)
            org.telegram.ui.Components.EllipsizeSpanAnimator r3 = r7.ellipsizeAnimator
            android.widget.TextView[] r4 = r7.textView
            r4 = r4[r1]
            r3.removeView(r4)
        L_0x0044:
            android.widget.TextView[] r3 = r7.textView
            r3 = r3[r2]
            java.lang.CharSequence r3 = r3.getText()
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 == 0) goto L_0x0053
            r10 = 0
        L_0x0053:
            if (r10 != 0) goto L_0x007b
            android.animation.ValueAnimator r3 = r7.animator
            if (r3 == 0) goto L_0x005c
            r3.cancel()
        L_0x005c:
            r7.animationInProgress = r2
            android.widget.TextView[] r3 = r7.textView
            r3 = r3[r2]
            r3.setText(r0)
            android.widget.TextView[] r3 = r7.textView
            r3 = r3[r2]
            r3.setVisibility(r2)
            android.widget.TextView[] r2 = r7.textView
            r1 = r2[r1]
            r2 = 8
            r1.setVisibility(r2)
            org.telegram.ui.Components.voip.VoIPTimerView r1 = r7.timerView
            r1.setVisibility(r2)
            goto L_0x00bb
        L_0x007b:
            boolean r3 = r7.animationInProgress
            if (r3 == 0) goto L_0x0082
            r7.nextTextToSet = r0
            return
        L_0x0082:
            boolean r3 = r7.timerShowing
            if (r3 == 0) goto L_0x0098
            android.widget.TextView[] r1 = r7.textView
            r1 = r1[r2]
            r1.setText(r0)
            org.telegram.ui.Components.voip.VoIPTimerView r1 = r7.timerView
            android.widget.TextView[] r3 = r7.textView
            r2 = r3[r2]
            r3 = 0
            r7.replaceViews(r1, r2, r3)
            goto L_0x00bb
        L_0x0098:
            android.widget.TextView[] r3 = r7.textView
            r3 = r3[r2]
            java.lang.CharSequence r3 = r3.getText()
            boolean r3 = r3.equals(r0)
            if (r3 != 0) goto L_0x00bb
            android.widget.TextView[] r3 = r7.textView
            r3 = r3[r1]
            r3.setText(r0)
            android.widget.TextView[] r3 = r7.textView
            r2 = r3[r2]
            r1 = r3[r1]
            org.telegram.ui.Components.voip.VoIPStatusTextView$$ExternalSyntheticLambda1 r3 = new org.telegram.ui.Components.voip.VoIPStatusTextView$$ExternalSyntheticLambda1
            r3.<init>(r7)
            r7.replaceViews(r2, r1, r3)
        L_0x00bb:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.VoIPStatusTextView.setText(java.lang.String, boolean, boolean):void");
    }

    /* renamed from: lambda$setText$0$org-telegram-ui-Components-voip-VoIPStatusTextView  reason: not valid java name */
    public /* synthetic */ void m4586x89ca5ab6() {
        TextView[] textViewArr = this.textView;
        TextView v = textViewArr[0];
        textViewArr[0] = textViewArr[1];
        textViewArr[1] = v;
    }

    public void showTimer(boolean animated) {
        if (TextUtils.isEmpty(this.textView[0].getText())) {
            animated = false;
        }
        if (!this.timerShowing) {
            this.timerView.updateTimer();
            if (!animated) {
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
                return;
            } else {
                this.timerShowing = true;
                replaceViews(this.textView[0], this.timerView, (Runnable) null);
            }
            this.ellipsizeAnimator.removeView(this.textView[0]);
            this.ellipsizeAnimator.removeView(this.textView[1]);
        }
    }

    /* access modifiers changed from: private */
    public void replaceViews(final View out, final View in, final Runnable onEnd) {
        out.setVisibility(0);
        in.setVisibility(0);
        in.setTranslationY((float) AndroidUtilities.dp(15.0f));
        in.setAlpha(0.0f);
        this.animationInProgress = true;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        this.animator = ofFloat;
        ofFloat.addUpdateListener(new VoIPStatusTextView$$ExternalSyntheticLambda0(in, out));
        this.animator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                out.setVisibility(8);
                out.setAlpha(1.0f);
                out.setTranslationY(0.0f);
                out.setScaleY(1.0f);
                out.setScaleX(1.0f);
                in.setAlpha(1.0f);
                in.setTranslationY(0.0f);
                in.setVisibility(0);
                in.setScaleY(1.0f);
                in.setScaleX(1.0f);
                Runnable runnable = onEnd;
                if (runnable != null) {
                    runnable.run();
                }
                VoIPStatusTextView.this.animationInProgress = false;
                if (VoIPStatusTextView.this.nextTextToSet != null) {
                    if (VoIPStatusTextView.this.nextTextToSet.equals("timer")) {
                        VoIPStatusTextView.this.showTimer(true);
                    } else {
                        VoIPStatusTextView.this.textView[1].setText(VoIPStatusTextView.this.nextTextToSet);
                        VoIPStatusTextView voIPStatusTextView = VoIPStatusTextView.this;
                        voIPStatusTextView.replaceViews(voIPStatusTextView.textView[0], VoIPStatusTextView.this.textView[1], new VoIPStatusTextView$1$$ExternalSyntheticLambda0(this));
                    }
                    VoIPStatusTextView.this.nextTextToSet = null;
                }
            }

            /* renamed from: lambda$onAnimationEnd$0$org-telegram-ui-Components-voip-VoIPStatusTextView$1  reason: not valid java name */
            public /* synthetic */ void m4587x82fvar_c2() {
                TextView v = VoIPStatusTextView.this.textView[0];
                VoIPStatusTextView.this.textView[0] = VoIPStatusTextView.this.textView[1];
                VoIPStatusTextView.this.textView[1] = v;
            }
        });
        this.animator.setDuration(250).setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.animator.start();
    }

    static /* synthetic */ void lambda$replaceViews$1(View in, View out, ValueAnimator valueAnimator) {
        float v = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        float inScale = (v * 0.6f) + 0.4f;
        float outScale = ((1.0f - v) * 0.6f) + 0.4f;
        in.setTranslationY(((float) AndroidUtilities.dp(10.0f)) * (1.0f - v));
        in.setAlpha(v);
        in.setScaleX(inScale);
        in.setScaleY(inScale);
        out.setTranslationY(((float) (-AndroidUtilities.dp(10.0f))) * v);
        out.setAlpha(1.0f - v);
        out.setScaleX(outScale);
        out.setScaleY(outScale);
    }

    public void setSignalBarCount(int count) {
        this.timerView.setSignalBarCount(count);
    }

    public void showReconnect(boolean showReconnecting, boolean animated) {
        int i = 0;
        if (!animated) {
            this.reconnectTextView.animate().setListener((Animator.AnimatorListener) null).cancel();
            TextView textView2 = this.reconnectTextView;
            if (!showReconnecting) {
                i = 8;
            }
            textView2.setVisibility(i);
        } else if (showReconnecting) {
            if (this.reconnectTextView.getVisibility() != 0) {
                this.reconnectTextView.setVisibility(0);
                this.reconnectTextView.setAlpha(0.0f);
            }
            this.reconnectTextView.animate().setListener((Animator.AnimatorListener) null).cancel();
            this.reconnectTextView.animate().alpha(1.0f).setDuration(150).start();
        } else {
            this.reconnectTextView.animate().alpha(0.0f).setListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    VoIPStatusTextView.this.reconnectTextView.setVisibility(8);
                }
            }).setDuration(150).start();
        }
        if (showReconnecting) {
            this.ellipsizeAnimator.addView(this.reconnectTextView);
        } else {
            this.ellipsizeAnimator.removeView(this.reconnectTextView);
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.attachedToWindow = true;
        this.ellipsizeAnimator.onAttachedToWindow();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.attachedToWindow = false;
        this.ellipsizeAnimator.onDetachedFromWindow();
    }
}
