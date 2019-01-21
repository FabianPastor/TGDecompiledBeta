package org.telegram.ui;

import org.telegram.tgnet.TLObject;

final /* synthetic */ class ChatEditTypeActivity$$Lambda$16 implements Runnable {
    private final ChatEditTypeActivity arg$1;
    private final TLObject arg$2;

    ChatEditTypeActivity$$Lambda$16(ChatEditTypeActivity chatEditTypeActivity, TLObject tLObject) {
        this.arg$1 = chatEditTypeActivity;
        this.arg$2 = tLObject;
    }

    public void run() {
        this.arg$1.lambda$null$15$ChatEditTypeActivity(this.arg$2);
    }
}
