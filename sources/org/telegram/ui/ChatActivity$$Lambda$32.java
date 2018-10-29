package org.telegram.ui;

import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.PhonebookSelectActivity.PhonebookSelectActivityDelegate;

final /* synthetic */ class ChatActivity$$Lambda$32 implements PhonebookSelectActivityDelegate {
    private final ChatActivity arg$1;

    ChatActivity$$Lambda$32(ChatActivity chatActivity) {
        this.arg$1 = chatActivity;
    }

    public void didSelectContact(User user) {
        this.arg$1.lambda$processSelectedAttach$39$ChatActivity(user);
    }
}
