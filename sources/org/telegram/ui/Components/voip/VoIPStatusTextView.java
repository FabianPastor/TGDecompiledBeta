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
import org.telegram.ui.Components.voip.VoIPStatusTextView;

public class VoIPStatusTextView extends FrameLayout {
    boolean animationInProgress;
    ValueAnimator animator;
    EllipsizeSpanAnimator ellipsizeAnimator;
    CharSequence nextTextToSet;
    TextView reconnectTextView;
    TextView[] textView = new TextView[2];
    boolean timerShowing;
    VoIPTimerView timerView;

    public VoIPStatusTextView(Context context) {
        super(context);
        for (int i = 0; i < 2; i++) {
            this.textView[i] = new TextView(context);
            this.textView[i].setTextSize(15.0f);
            this.textView[i].setShadowLayer((float) AndroidUtilities.dp(3.0f), 0.0f, (float) AndroidUtilities.dp(0.6666667f), NUM);
            this.textView[i].setTextColor(-1);
            this.textView[i].setGravity(1);
            addView(this.textView[i]);
        }
        TextView textView2 = new TextView(context);
        this.reconnectTextView = textView2;
        textView2.setTextSize(15.0f);
        this.reconnectTextView.setShadowLayer((float) AndroidUtilities.dp(3.0f), 0.0f, (float) AndroidUtilities.dp(0.6666667f), NUM);
        this.reconnectTextView.setTextColor(-1);
        this.reconnectTextView.setGravity(1);
        addView(this.reconnectTextView, LayoutHelper.createFrame(-1, -2.0f, 0, 0.0f, 22.0f, 0.0f, 0.0f));
        this.ellipsizeAnimator = new EllipsizeSpanAnimator(this);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(LocaleController.getString("VoipReconnecting", NUM));
        SpannableString spannableString = new SpannableString("...");
        this.ellipsizeAnimator.wrap(spannableString, 0);
        spannableStringBuilder.append(spannableString);
        this.reconnectTextView.setText(spannableStringBuilder);
        this.reconnectTextView.setVisibility(8);
        VoIPTimerView voIPTimerView = new VoIPTimerView(context);
        this.timerView = voIPTimerView;
        addView(voIPTimerView, LayoutHelper.createFrame(-1, -2.0f));
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v24, resolved type: android.text.SpannableStringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v14, resolved type: java.lang.String} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setText(java.lang.String r4, boolean r5, boolean r6) {
        /*
            r3 = this;
            r0 = 1
            r1 = 0
            if (r5 == 0) goto L_0x0031
            android.text.SpannableStringBuilder r5 = new android.text.SpannableStringBuilder
            r5.<init>(r4)
            org.telegram.ui.Components.EllipsizeSpanAnimator r4 = r3.ellipsizeAnimator
            r4.reset()
            android.text.SpannableString r4 = new android.text.SpannableString
            java.lang.String r2 = "..."
            r4.<init>(r2)
            org.telegram.ui.Components.EllipsizeSpanAnimator r2 = r3.ellipsizeAnimator
            r2.wrap(r4, r1)
            r5.append(r4)
            org.telegram.ui.Components.EllipsizeSpanAnimator r4 = r3.ellipsizeAnimator
            android.widget.TextView[] r2 = r3.textView
            r2 = r2[r1]
            r4.addView(r2)
            org.telegram.ui.Components.EllipsizeSpanAnimator r4 = r3.ellipsizeAnimator
            android.widget.TextView[] r2 = r3.textView
            r2 = r2[r0]
            r4.addView(r2)
            r4 = r5
            goto L_0x0043
        L_0x0031:
            org.telegram.ui.Components.EllipsizeSpanAnimator r5 = r3.ellipsizeAnimator
            android.widget.TextView[] r2 = r3.textView
            r2 = r2[r1]
            r5.removeView(r2)
            org.telegram.ui.Components.EllipsizeSpanAnimator r5 = r3.ellipsizeAnimator
            android.widget.TextView[] r2 = r3.textView
            r2 = r2[r0]
            r5.removeView(r2)
        L_0x0043:
            android.widget.TextView[] r5 = r3.textView
            r5 = r5[r1]
            java.lang.CharSequence r5 = r5.getText()
            boolean r5 = android.text.TextUtils.isEmpty(r5)
            if (r5 == 0) goto L_0x0052
            r6 = 0
        L_0x0052:
            if (r6 != 0) goto L_0x007a
            android.animation.ValueAnimator r5 = r3.animator
            if (r5 == 0) goto L_0x005b
            r5.cancel()
        L_0x005b:
            r3.animationInProgress = r1
            android.widget.TextView[] r5 = r3.textView
            r5 = r5[r1]
            r5.setText(r4)
            android.widget.TextView[] r4 = r3.textView
            r4 = r4[r1]
            r4.setVisibility(r1)
            android.widget.TextView[] r4 = r3.textView
            r4 = r4[r0]
            r5 = 8
            r4.setVisibility(r5)
            org.telegram.ui.Components.voip.VoIPTimerView r4 = r3.timerView
            r4.setVisibility(r5)
            goto L_0x00ba
        L_0x007a:
            boolean r5 = r3.animationInProgress
            if (r5 == 0) goto L_0x0081
            r3.nextTextToSet = r4
            return
        L_0x0081:
            boolean r5 = r3.timerShowing
            if (r5 == 0) goto L_0x0097
            android.widget.TextView[] r5 = r3.textView
            r5 = r5[r1]
            r5.setText(r4)
            org.telegram.ui.Components.voip.VoIPTimerView r4 = r3.timerView
            android.widget.TextView[] r5 = r3.textView
            r5 = r5[r1]
            r6 = 0
            r3.replaceViews(r4, r5, r6)
            goto L_0x00ba
        L_0x0097:
            android.widget.TextView[] r5 = r3.textView
            r5 = r5[r1]
            java.lang.CharSequence r5 = r5.getText()
            boolean r5 = r5.equals(r4)
            if (r5 != 0) goto L_0x00ba
            android.widget.TextView[] r5 = r3.textView
            r5 = r5[r0]
            r5.setText(r4)
            android.widget.TextView[] r4 = r3.textView
            r5 = r4[r1]
            r4 = r4[r0]
            org.telegram.ui.Components.voip.-$$Lambda$VoIPStatusTextView$NnszBeG4p9PQxoQw7aG18fdhXFQ r6 = new org.telegram.ui.Components.voip.-$$Lambda$VoIPStatusTextView$NnszBeG4p9PQxoQw7aG18fdhXFQ
            r6.<init>()
            r3.replaceViews(r5, r4, r6)
        L_0x00ba:
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
                                VoIPStatusTextView.AnonymousClass1.this.lambda$onAnimationEnd$0$VoIPStatusTextView$1();
                            }
                        });
                    }
                    VoIPStatusTextView.this.nextTextToSet = null;
                }
            }

            public /* synthetic */ void lambda$onAnimationEnd$0$VoIPStatusTextView$1() {
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

    public void showReconnect(boolean z, boolean z2) {
        int i = 0;
        if (!z2) {
            this.reconnectTextView.animate().setListener((Animator.AnimatorListener) null).cancel();
            TextView textView2 = this.reconnectTextView;
            if (!z) {
                i = 8;
            }
            textView2.setVisibility(i);
        } else if (z) {
            if (this.reconnectTextView.getVisibility() != 0) {
                this.reconnectTextView.setVisibility(0);
                this.reconnectTextView.setAlpha(0.0f);
            }
            this.reconnectTextView.animate().setListener((Animator.AnimatorListener) null).cancel();
            this.reconnectTextView.animate().alpha(1.0f).setDuration(150).start();
        } else {
            this.reconnectTextView.animate().alpha(0.0f).setListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    VoIPStatusTextView.this.reconnectTextView.setVisibility(8);
                }
            }).setDuration(150).start();
        }
        if (z) {
            this.ellipsizeAnimator.addView(this.reconnectTextView);
        } else {
            this.ellipsizeAnimator.removeView(this.reconnectTextView);
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.ellipsizeAnimator.onAttachedToWindow();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.ellipsizeAnimator.onDetachedFromWindow();
    }
}
