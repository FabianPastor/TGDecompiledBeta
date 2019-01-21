package org.telegram.ui;

import org.telegram.messenger.MessagesStorage;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ChatActivity.AnonymousClass14;

final /* synthetic */ class ChatActivity$14$$Lambda$1 implements RequestDelegate {
    private final AnonymousClass14 arg$1;
    private final MessagesStorage arg$2;

    ChatActivity$14$$Lambda$1(AnonymousClass14 anonymousClass14, MessagesStorage messagesStorage) {
        this.arg$1 = anonymousClass14;
        this.arg$2 = messagesStorage;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$loadLastUnreadMention$2$ChatActivity$14(this.arg$2, tLObject, tL_error);
    }
}
