package org.telegram.ui;

import org.telegram.tgnet.TLRPC$MessagePeerReaction;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.ReactedUsersListView;

public final /* synthetic */ class ChatActivity$ChatActivityAdapter$1$$ExternalSyntheticLambda6 implements ReactedUsersListView.OnProfileSelectedListener {
    public final /* synthetic */ ChatActivity.ChatActivityAdapter.AnonymousClass1 f$0;
    public final /* synthetic */ ChatMessageCell f$1;

    public /* synthetic */ ChatActivity$ChatActivityAdapter$1$$ExternalSyntheticLambda6(ChatActivity.ChatActivityAdapter.AnonymousClass1 r1, ChatMessageCell chatMessageCell) {
        this.f$0 = r1;
        this.f$1 = chatMessageCell;
    }

    public final void onProfileSelected(ReactedUsersListView reactedUsersListView, long j, TLRPC$MessagePeerReaction tLRPC$MessagePeerReaction) {
        this.f$0.lambda$didPressReaction$4(this.f$1, reactedUsersListView, j, tLRPC$MessagePeerReaction);
    }
}
