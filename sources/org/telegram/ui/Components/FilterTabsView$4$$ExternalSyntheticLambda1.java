package org.telegram.ui.Components;

import android.animation.ValueAnimator;
import org.telegram.ui.Components.FilterTabsView;

public final /* synthetic */ class FilterTabsView$4$$ExternalSyntheticLambda1 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ FilterTabsView.TabView f$0;

    public /* synthetic */ FilterTabsView$4$$ExternalSyntheticLambda1(FilterTabsView.TabView tabView) {
        this.f$0 = tabView;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        FilterTabsView.AnonymousClass4.lambda$animateMoveImpl$1(this.f$0, valueAnimator);
    }
}
