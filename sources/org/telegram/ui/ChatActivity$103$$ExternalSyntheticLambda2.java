package org.telegram.ui;

import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC$MessagePeerReaction;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.ReactedUsersListView;

public final /* synthetic */ class ChatActivity$103$$ExternalSyntheticLambda2 implements ReactedUsersListView.OnProfileSelectedListener {
    public final /* synthetic */ ChatActivity.AnonymousClass103 f$0;
    public final /* synthetic */ MessageObject f$1;

    public /* synthetic */ ChatActivity$103$$ExternalSyntheticLambda2(ChatActivity.AnonymousClass103 r1, MessageObject messageObject) {
        this.f$0 = r1;
        this.f$1 = messageObject;
    }

    public final void onProfileSelected(ReactedUsersListView reactedUsersListView, long j, TLRPC$MessagePeerReaction tLRPC$MessagePeerReaction) {
        this.f$0.lambda$instantiateItem$1(this.f$1, reactedUsersListView, j, tLRPC$MessagePeerReaction);
    }
}
