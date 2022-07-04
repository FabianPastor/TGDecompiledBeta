package org.telegram.ui.Components;

import android.app.Dialog;
import androidx.dynamicanimation.animation.DynamicAnimation;

public final /* synthetic */ class ChatActivityEnterView$$ExternalSyntheticLambda31 implements DynamicAnimation.OnAnimationEndListener {
    public final /* synthetic */ ChatActivityEnterView f$0;
    public final /* synthetic */ Dialog f$1;
    public final /* synthetic */ SimpleAvatarView f$2;
    public final /* synthetic */ float f$3;
    public final /* synthetic */ float f$4;

    public /* synthetic */ ChatActivityEnterView$$ExternalSyntheticLambda31(ChatActivityEnterView chatActivityEnterView, Dialog dialog, SimpleAvatarView simpleAvatarView, float f, float f2) {
        this.f$0 = chatActivityEnterView;
        this.f$1 = dialog;
        this.f$2 = simpleAvatarView;
        this.f$3 = f;
        this.f$4 = f2;
    }

    public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        this.f$0.lambda$new$11(this.f$1, this.f$2, this.f$3, this.f$4, dynamicAnimation, z, f, f2);
    }
}
