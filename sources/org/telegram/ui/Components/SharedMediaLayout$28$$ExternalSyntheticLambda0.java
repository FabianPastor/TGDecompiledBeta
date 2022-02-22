package org.telegram.ui.Components;

import android.animation.ValueAnimator;
import org.telegram.ui.Components.SharedMediaLayout;

public final /* synthetic */ class SharedMediaLayout$28$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ SharedMediaLayout.AnonymousClass28 f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ RecyclerListView f$2;

    public /* synthetic */ SharedMediaLayout$28$$ExternalSyntheticLambda0(SharedMediaLayout.AnonymousClass28 r1, int i, RecyclerListView recyclerListView) {
        this.f$0 = r1;
        this.f$1 = i;
        this.f$2 = recyclerListView;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$onPreDraw$0(this.f$1, this.f$2, valueAnimator);
    }
}
