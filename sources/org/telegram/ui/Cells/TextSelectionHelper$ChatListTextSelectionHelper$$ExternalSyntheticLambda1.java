package org.telegram.ui.Cells;

import android.animation.ValueAnimator;
import org.telegram.ui.Cells.TextSelectionHelper;

public final /* synthetic */ class TextSelectionHelper$ChatListTextSelectionHelper$$ExternalSyntheticLambda1 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ TextSelectionHelper.ChatListTextSelectionHelper f$0;
    public final /* synthetic */ boolean f$1;

    public /* synthetic */ TextSelectionHelper$ChatListTextSelectionHelper$$ExternalSyntheticLambda1(TextSelectionHelper.ChatListTextSelectionHelper chatListTextSelectionHelper, boolean z) {
        this.f$0 = chatListTextSelectionHelper;
        this.f$1 = z;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$onTextSelected$0(this.f$1, valueAnimator);
    }
}
