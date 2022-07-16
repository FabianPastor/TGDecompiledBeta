package org.telegram.ui.Components;

import androidx.dynamicanimation.animation.DynamicAnimation;

public final /* synthetic */ class MentionsContainerView$$ExternalSyntheticLambda1 implements DynamicAnimation.OnAnimationEndListener {
    public static final /* synthetic */ MentionsContainerView$$ExternalSyntheticLambda1 INSTANCE = new MentionsContainerView$$ExternalSyntheticLambda1();

    private /* synthetic */ MentionsContainerView$$ExternalSyntheticLambda1() {
    }

    public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        MentionsContainerView.lambda$updateListViewTranslation$3(dynamicAnimation, z, f, f2);
    }
}
