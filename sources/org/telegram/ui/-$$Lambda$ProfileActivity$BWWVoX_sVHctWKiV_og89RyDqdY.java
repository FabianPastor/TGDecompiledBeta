package org.telegram.ui;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ProfileActivity$BWWVoX_sVHctWKiV_og89RyDqdY implements AnimatorUpdateListener {
    private final /* synthetic */ ProfileActivity f$0;
    private final /* synthetic */ int f$1;

    public /* synthetic */ -$$Lambda$ProfileActivity$BWWVoX_sVHctWKiV_og89RyDqdY(ProfileActivity profileActivity, int i) {
        this.f$0 = profileActivity;
        this.f$1 = i;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$needLayout$19$ProfileActivity(this.f$1, valueAnimator);
    }
}
