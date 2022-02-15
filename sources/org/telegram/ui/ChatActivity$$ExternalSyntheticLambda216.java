package org.telegram.ui;

import android.view.View;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC$TL_availableReaction;
import org.telegram.ui.Components.ReactionsContainerLayout;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda216 implements ReactionsContainerLayout.ReactionsContainerDelegate {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ MessageObject f$1;
    public final /* synthetic */ ReactionsContainerLayout f$2;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda216(ChatActivity chatActivity, MessageObject messageObject, ReactionsContainerLayout reactionsContainerLayout) {
        this.f$0 = chatActivity;
        this.f$1 = messageObject;
        this.f$2 = reactionsContainerLayout;
    }

    public final void onReactionClicked(View view, TLRPC$TL_availableReaction tLRPC$TL_availableReaction, boolean z) {
        this.f$0.lambda$createMenu$156(this.f$1, this.f$2, view, tLRPC$TL_availableReaction, z);
    }
}
