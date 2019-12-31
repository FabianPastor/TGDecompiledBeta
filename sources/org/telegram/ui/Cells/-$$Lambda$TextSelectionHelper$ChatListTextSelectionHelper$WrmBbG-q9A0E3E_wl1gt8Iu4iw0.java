package org.telegram.ui.Cells;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import org.telegram.ui.Cells.TextSelectionHelper.ChatListTextSelectionHelper;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TextSelectionHelper$ChatListTextSelectionHelper$WrmBbG-q9A0E3E_wl1gt8Iu4iw0 implements AnimatorUpdateListener {
    private final /* synthetic */ ChatListTextSelectionHelper f$0;
    private final /* synthetic */ boolean f$1;

    public /* synthetic */ -$$Lambda$TextSelectionHelper$ChatListTextSelectionHelper$WrmBbG-q9A0E3E_wl1gt8Iu4iw0(ChatListTextSelectionHelper chatListTextSelectionHelper, boolean z) {
        this.f$0 = chatListTextSelectionHelper;
        this.f$1 = z;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$onTextSelected$0$TextSelectionHelper$ChatListTextSelectionHelper(this.f$1, valueAnimator);
    }
}
