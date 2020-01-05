package org.telegram.ui.Cells;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import org.telegram.ui.Cells.TextSelectionHelper.ChatListTextSelectionHelper;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TextSelectionHelper$ChatListTextSelectionHelper$GhFjRffOqivFT1D6v-74EEa8DCc implements AnimatorUpdateListener {
    private final /* synthetic */ ChatMessageCell f$0;
    private final /* synthetic */ int f$1;

    public /* synthetic */ -$$Lambda$TextSelectionHelper$ChatListTextSelectionHelper$GhFjRffOqivFT1D6v-74EEa8DCc(ChatMessageCell chatMessageCell, int i) {
        this.f$0 = chatMessageCell;
        this.f$1 = i;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        ChatListTextSelectionHelper.lambda$onExitSelectionMode$1(this.f$0, this.f$1, valueAnimator);
    }
}
