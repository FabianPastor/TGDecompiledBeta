package org.telegram.ui;

import android.view.View;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.ReactionsContainerLayout;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda146 implements ReactionsContainerLayout.ReactionsContainerDelegate {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ MessageObject f$1;
    public final /* synthetic */ ReactionsContainerLayout f$2;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda146(ChatActivity chatActivity, MessageObject messageObject, ReactionsContainerLayout reactionsContainerLayout) {
        this.f$0 = chatActivity;
        this.f$1 = messageObject;
        this.f$2 = reactionsContainerLayout;
    }

    public final void onReactionClicked(View view, TLRPC.TL_availableReaction tL_availableReaction, boolean z) {
        this.f$0.m1654lambda$createMenu$168$orgtelegramuiChatActivity(this.f$1, this.f$2, view, tL_availableReaction, z);
    }
}
