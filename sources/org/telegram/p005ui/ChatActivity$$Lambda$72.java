package org.telegram.p005ui;

import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_editMessage;

/* renamed from: org.telegram.ui.ChatActivity$$Lambda$72 */
final /* synthetic */ class ChatActivity$$Lambda$72 implements Runnable {
    private final ChatActivity arg$1;
    private final TL_error arg$2;
    private final TL_messages_editMessage arg$3;

    ChatActivity$$Lambda$72(ChatActivity chatActivity, TL_error tL_error, TL_messages_editMessage tL_messages_editMessage) {
        this.arg$1 = chatActivity;
        this.arg$2 = tL_error;
        this.arg$3 = tL_messages_editMessage;
    }

    public void run() {
        this.arg$1.lambda$null$80$ChatActivity(this.arg$2, this.arg$3);
    }
}
