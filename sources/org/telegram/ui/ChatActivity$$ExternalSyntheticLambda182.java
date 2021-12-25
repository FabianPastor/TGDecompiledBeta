package org.telegram.ui;

import android.view.View;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC$TL_availableReaction;
import org.telegram.ui.Components.ReactionsContainerLayout;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda182 implements ReactionsContainerLayout.ReactionsContainerDelegate {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ MessageObject f$1;
    public final /* synthetic */ ReactionsContainerLayout f$2;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda182(ChatActivity chatActivity, MessageObject messageObject, ReactionsContainerLayout reactionsContainerLayout) {
        this.f$0 = chatActivity;
        this.f$1 = messageObject;
        this.f$2 = reactionsContainerLayout;
    }

    public final void onReactionClicked(View view, TLRPC$TL_availableReaction tLRPC$TL_availableReaction) {
        this.f$0.lambda$createMenu$130(this.f$1, this.f$2, view, tLRPC$TL_availableReaction);
    }
}
