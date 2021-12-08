package org.telegram.ui;

import org.telegram.ui.Delegates.ChatActivityMemberRequestsDelegate;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda179 implements ChatActivityMemberRequestsDelegate.Callback {
    public final /* synthetic */ ChatActivity f$0;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda179(ChatActivity chatActivity) {
        this.f$0 = chatActivity;
    }

    public final void onEnterOffsetChanged() {
        this.f$0.invalidateChatListViewTopPadding();
    }
}
