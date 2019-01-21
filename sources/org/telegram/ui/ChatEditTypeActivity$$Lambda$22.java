package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class ChatEditTypeActivity$$Lambda$22 implements Runnable {
    private final ChatEditTypeActivity arg$1;
    private final TL_error arg$2;

    ChatEditTypeActivity$$Lambda$22(ChatEditTypeActivity chatEditTypeActivity, TL_error tL_error) {
        this.arg$1 = chatEditTypeActivity;
        this.arg$2 = tL_error;
    }

    public void run() {
        this.arg$1.lambda$null$1$ChatEditTypeActivity(this.arg$2);
    }
}
