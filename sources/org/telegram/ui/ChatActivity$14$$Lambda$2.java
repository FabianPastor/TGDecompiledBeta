package org.telegram.ui;

import org.telegram.messenger.MessagesStorage;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ChatActivity.AnonymousClass14;

final /* synthetic */ class ChatActivity$14$$Lambda$2 implements Runnable {
    private final AnonymousClass14 arg$1;
    private final TLObject arg$2;
    private final TL_error arg$3;
    private final MessagesStorage arg$4;

    ChatActivity$14$$Lambda$2(AnonymousClass14 anonymousClass14, TLObject tLObject, TL_error tL_error, MessagesStorage messagesStorage) {
        this.arg$1 = anonymousClass14;
        this.arg$2 = tLObject;
        this.arg$3 = tL_error;
        this.arg$4 = messagesStorage;
    }

    public void run() {
        this.arg$1.lambda$null$1$ChatActivity$14(this.arg$2, this.arg$3, this.arg$4);
    }
}
