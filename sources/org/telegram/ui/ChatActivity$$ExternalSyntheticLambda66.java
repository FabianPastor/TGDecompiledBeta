package org.telegram.ui;

import android.animation.ValueAnimator;
import android.view.View;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda66 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ View f$1;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda66(ChatActivity chatActivity, View view) {
        this.f$0 = chatActivity;
        this.f$1 = view;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.m3012lambda$hideInfoView$85$orgtelegramuiChatActivity(this.f$1, valueAnimator);
    }
}
