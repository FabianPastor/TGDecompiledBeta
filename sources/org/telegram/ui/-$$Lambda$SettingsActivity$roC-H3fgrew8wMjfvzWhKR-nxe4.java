package org.telegram.ui;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SettingsActivity$roC-H3fgrew8wMjfvzWhKR-nxe4 implements AnimatorUpdateListener {
    private final /* synthetic */ SettingsActivity f$0;
    private final /* synthetic */ ValueAnimator f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ boolean f$3;

    public /* synthetic */ -$$Lambda$SettingsActivity$roC-H3fgrew8wMjfvzWhKR-nxe4(SettingsActivity settingsActivity, ValueAnimator valueAnimator, int i, boolean z) {
        this.f$0 = settingsActivity;
        this.f$1 = valueAnimator;
        this.f$2 = i;
        this.f$3 = z;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$searchExpandTransition$7$SettingsActivity(this.f$1, this.f$2, this.f$3, valueAnimator);
    }
}
