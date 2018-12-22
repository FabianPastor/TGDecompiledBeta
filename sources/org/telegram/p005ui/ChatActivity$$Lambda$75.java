package org.telegram.p005ui;

import org.telegram.tgnet.TLObject;

/* renamed from: org.telegram.ui.ChatActivity$$Lambda$75 */
final /* synthetic */ class ChatActivity$$Lambda$75 implements Runnable {
    private final ChatActivity arg$1;
    private final TLObject arg$2;

    ChatActivity$$Lambda$75(ChatActivity chatActivity, TLObject tLObject) {
        this.arg$1 = chatActivity;
        this.arg$2 = tLObject;
    }

    public void run() {
        this.arg$1.lambda$null$69$ChatActivity(this.arg$2);
    }
}
