package org.telegram.ui;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ProxySettingsActivity$53rzfdw6zTqW2BSy-byLXDlX17M implements AnimatorUpdateListener {
    private final /* synthetic */ ProxySettingsActivity f$0;

    public /* synthetic */ -$$Lambda$ProxySettingsActivity$53rzfdw6zTqW2BSy-byLXDlX17M(ProxySettingsActivity proxySettingsActivity) {
        this.f$0 = proxySettingsActivity;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$setShareDoneEnabled$5$ProxySettingsActivity(valueAnimator);
    }
}
