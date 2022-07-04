package org.telegram.ui.Components;

import androidx.dynamicanimation.animation.DynamicAnimation;

public final /* synthetic */ class MentionsContainerView$$ExternalSyntheticLambda0 implements DynamicAnimation.OnAnimationEndListener {
    public final /* synthetic */ MentionsContainerView f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ MentionsContainerView$$ExternalSyntheticLambda0(MentionsContainerView mentionsContainerView, int i) {
        this.f$0 = mentionsContainerView;
        this.f$1 = i;
    }

    public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        this.f$0.lambda$updateListViewTranslation$4(this.f$1, dynamicAnimation, z, f, f2);
    }
}
