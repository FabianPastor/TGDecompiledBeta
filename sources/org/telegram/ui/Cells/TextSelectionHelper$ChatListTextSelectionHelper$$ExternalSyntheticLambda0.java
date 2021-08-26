package org.telegram.ui.Cells;

import android.animation.ValueAnimator;
import org.telegram.ui.Cells.TextSelectionHelper;

public final /* synthetic */ class TextSelectionHelper$ChatListTextSelectionHelper$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ ChatMessageCell f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ TextSelectionHelper$ChatListTextSelectionHelper$$ExternalSyntheticLambda0(ChatMessageCell chatMessageCell, int i) {
        this.f$0 = chatMessageCell;
        this.f$1 = i;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        TextSelectionHelper.ChatListTextSelectionHelper.lambda$onExitSelectionMode$1(this.f$0, this.f$1, valueAnimator);
    }
}
