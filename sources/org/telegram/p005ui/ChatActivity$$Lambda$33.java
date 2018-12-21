package org.telegram.p005ui;

import org.telegram.p005ui.PhonebookSelectActivity.PhonebookSelectActivityDelegate;
import org.telegram.tgnet.TLRPC.User;

/* renamed from: org.telegram.ui.ChatActivity$$Lambda$33 */
final /* synthetic */ class ChatActivity$$Lambda$33 implements PhonebookSelectActivityDelegate {
    private final ChatActivity arg$1;

    ChatActivity$$Lambda$33(ChatActivity chatActivity) {
        this.arg$1 = chatActivity;
    }

    public void didSelectContact(User user) {
        this.arg$1.lambda$processSelectedAttach$40$ChatActivity(user);
    }
}
