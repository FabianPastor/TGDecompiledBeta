package org.telegram.ui.Components;

import android.app.Dialog;
import androidx.dynamicanimation.animation.DynamicAnimation;

public final /* synthetic */ class ChatActivityEnterView$$ExternalSyntheticLambda28 implements DynamicAnimation.OnAnimationEndListener {
    public final /* synthetic */ ChatActivityEnterView f$0;
    public final /* synthetic */ Dialog f$1;

    public /* synthetic */ ChatActivityEnterView$$ExternalSyntheticLambda28(ChatActivityEnterView chatActivityEnterView, Dialog dialog) {
        this.f$0 = chatActivityEnterView;
        this.f$1 = dialog;
    }

    public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        this.f$0.lambda$new$9(this.f$1, dynamicAnimation, z, f, f2);
    }
}
