package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.ui.ChatUsersActivity.ChatUsersActivityDelegate;

final /* synthetic */ class ChatUsersActivity$$Lambda$18 implements ChatUsersActivityDelegate {
    private final ChatUsersActivity arg$1;

    ChatUsersActivity$$Lambda$18(ChatUsersActivity chatUsersActivity) {
        this.arg$1 = chatUsersActivity;
    }

    public void didAddParticipantToList(int i, TLObject tLObject) {
        this.arg$1.lambda$null$1$ChatUsersActivity(i, tLObject);
    }
}
