package org.telegram.ui;

import org.telegram.ui.Components.RecyclerAnimationScrollHelper;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda187 implements RecyclerAnimationScrollHelper.ScrollListener {
    public final /* synthetic */ ChatActivity f$0;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda187(ChatActivity chatActivity) {
        this.f$0 = chatActivity;
    }

    public final void onScroll() {
        this.f$0.invalidateMessagesVisiblePart();
    }
}
