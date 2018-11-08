package org.telegram.p005ui;

import org.telegram.messenger.MessagesStorage;
import org.telegram.p005ui.ChatActivity.C110313;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.ChatActivity$13$$Lambda$1 */
final /* synthetic */ class ChatActivity$13$$Lambda$1 implements RequestDelegate {
    private final C110313 arg$1;
    private final MessagesStorage arg$2;

    ChatActivity$13$$Lambda$1(C110313 c110313, MessagesStorage messagesStorage) {
        this.arg$1 = c110313;
        this.arg$2 = messagesStorage;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$loadLastUnreadMention$2$ChatActivity$13(this.arg$2, tLObject, tL_error);
    }
}
