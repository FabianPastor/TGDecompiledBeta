package org.telegram.ui.Components;

import androidx.dynamicanimation.animation.DynamicAnimation;

public final /* synthetic */ class MentionsContainerView$$ExternalSyntheticLambda1 implements DynamicAnimation.OnAnimationEndListener {
    public final /* synthetic */ MentionsContainerView f$0;
    public final /* synthetic */ boolean f$1;

    public /* synthetic */ MentionsContainerView$$ExternalSyntheticLambda1(MentionsContainerView mentionsContainerView, boolean z) {
        this.f$0 = mentionsContainerView;
        this.f$1 = z;
    }

    public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        this.f$0.lambda$updateListViewTranslation$2(this.f$1, dynamicAnimation, z, f, f2);
    }
}
