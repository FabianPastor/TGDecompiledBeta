package org.telegram.ui;

import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC$MessagePeerReaction;
import org.telegram.ui.Components.ReactedUsersListView;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda247 implements ReactedUsersListView.OnProfileSelectedListener {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ MessageObject f$1;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda247(ChatActivity chatActivity, MessageObject messageObject) {
        this.f$0 = chatActivity;
        this.f$1 = messageObject;
    }

    public final void onProfileSelected(ReactedUsersListView reactedUsersListView, long j, TLRPC$MessagePeerReaction tLRPC$MessagePeerReaction) {
        this.f$0.lambda$createMenu$159(this.f$1, reactedUsersListView, j, tLRPC$MessagePeerReaction);
    }
}
