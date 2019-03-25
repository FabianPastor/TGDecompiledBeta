package org.telegram.ui;

import org.telegram.tgnet.TLObject;

final /* synthetic */ class ChatActivity$$Lambda$69 implements Runnable {
    private final ChatActivity arg$1;
    private final TLObject arg$2;

    ChatActivity$$Lambda$69(ChatActivity chatActivity, TLObject tLObject) {
        this.arg$1 = chatActivity;
        this.arg$2 = tLObject;
    }

    public void run() {
        this.arg$1.lambda$null$59$ChatActivity(this.arg$2);
    }
}
