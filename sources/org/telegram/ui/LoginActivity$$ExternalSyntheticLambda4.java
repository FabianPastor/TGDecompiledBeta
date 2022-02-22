package org.telegram.ui;

import android.animation.ValueAnimator;
import android.view.ViewGroup;
import org.telegram.ui.Components.TransformableLoginButtonView;

public final /* synthetic */ class LoginActivity$$ExternalSyntheticLambda4 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ LoginActivity f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ float f$10;
    public final /* synthetic */ int f$11;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ ViewGroup.MarginLayoutParams f$3;
    public final /* synthetic */ int f$4;
    public final /* synthetic */ int f$5;
    public final /* synthetic */ int f$6;
    public final /* synthetic */ TransformableLoginButtonView f$7;
    public final /* synthetic */ float f$8;
    public final /* synthetic */ int f$9;

    public /* synthetic */ LoginActivity$$ExternalSyntheticLambda4(LoginActivity loginActivity, int i, int i2, ViewGroup.MarginLayoutParams marginLayoutParams, int i3, int i4, int i5, TransformableLoginButtonView transformableLoginButtonView, float f, int i6, float f2, int i7) {
        this.f$0 = loginActivity;
        this.f$1 = i;
        this.f$2 = i2;
        this.f$3 = marginLayoutParams;
        this.f$4 = i3;
        this.f$5 = i4;
        this.f$6 = i5;
        this.f$7 = transformableLoginButtonView;
        this.f$8 = f;
        this.f$9 = i6;
        this.f$10 = f2;
        this.f$11 = i7;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$onCustomTransitionAnimation$17(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, valueAnimator);
    }
}
