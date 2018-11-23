package org.telegram.p005ui;

import org.telegram.messenger.MessagesStorage;
import org.telegram.p005ui.ChatActivity.C056914;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.ChatActivity$14$$Lambda$2 */
final /* synthetic */ class ChatActivity$14$$Lambda$2 implements Runnable {
    private final C056914 arg$1;
    private final TLObject arg$2;
    private final TL_error arg$3;
    private final MessagesStorage arg$4;

    ChatActivity$14$$Lambda$2(C056914 c056914, TLObject tLObject, TL_error tL_error, MessagesStorage messagesStorage) {
        this.arg$1 = c056914;
        this.arg$2 = tLObject;
        this.arg$3 = tL_error;
        this.arg$4 = messagesStorage;
    }

    public void run() {
        this.arg$1.lambda$null$1$ChatActivity$14(this.arg$2, this.arg$3, this.arg$4);
    }
}
