package org.telegram.ui.Components.voip;

import android.animation.ValueAnimator;

public final /* synthetic */ class GroupCallRenderersContainer$$ExternalSyntheticLambda3 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ GroupCallRenderersContainer f$0;
    public final /* synthetic */ GroupCallMiniTextureView f$1;

    public /* synthetic */ GroupCallRenderersContainer$$ExternalSyntheticLambda3(GroupCallRenderersContainer groupCallRenderersContainer, GroupCallMiniTextureView groupCallMiniTextureView) {
        this.f$0 = groupCallRenderersContainer;
        this.f$1 = groupCallMiniTextureView;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$requestFullscreen$5(this.f$1, valueAnimator);
    }
}
