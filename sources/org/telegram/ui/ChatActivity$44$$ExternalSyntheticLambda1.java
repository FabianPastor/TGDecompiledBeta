package org.telegram.ui;

import android.animation.ValueAnimator;
import org.telegram.ui.ChatActivity;

public final /* synthetic */ class ChatActivity$44$$ExternalSyntheticLambda1 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ ChatActivity.AnonymousClass44 f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ ChatActivity$44$$ExternalSyntheticLambda1(ChatActivity.AnonymousClass44 r1, int i) {
        this.f$0 = r1;
        this.f$1 = i;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$checkAnimation$0(this.f$1, valueAnimator);
    }
}
