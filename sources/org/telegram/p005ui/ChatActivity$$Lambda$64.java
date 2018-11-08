package org.telegram.p005ui;

import org.telegram.tgnet.TLObject;

/* renamed from: org.telegram.ui.ChatActivity$$Lambda$64 */
final /* synthetic */ class ChatActivity$$Lambda$64 implements Runnable {
    private final TLObject arg$1;

    ChatActivity$$Lambda$64(TLObject tLObject) {
        this.arg$1 = tLObject;
    }

    public void run() {
        ChatActivity.lambda$null$73$ChatActivity(this.arg$1);
    }
}
