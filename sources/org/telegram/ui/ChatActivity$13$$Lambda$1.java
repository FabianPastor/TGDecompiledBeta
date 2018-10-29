package org.telegram.ui;

import org.telegram.messenger.MessagesStorage;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ChatActivity.AnonymousClass13;

final /* synthetic */ class ChatActivity$13$$Lambda$1 implements RequestDelegate {
    private final AnonymousClass13 arg$1;
    private final MessagesStorage arg$2;

    ChatActivity$13$$Lambda$1(AnonymousClass13 anonymousClass13, MessagesStorage messagesStorage) {
        this.arg$1 = anonymousClass13;
        this.arg$2 = messagesStorage;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$loadLastUnreadMention$2$ChatActivity$13(this.arg$2, tLObject, tL_error);
    }
}
