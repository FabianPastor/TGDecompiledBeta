package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_updateLangPack;

final /* synthetic */ class MessagesController$$Lambda$140 implements Runnable {
    private final MessagesController arg$1;
    private final TL_updateLangPack arg$2;

    MessagesController$$Lambda$140(MessagesController messagesController, TL_updateLangPack tL_updateLangPack) {
        this.arg$1 = messagesController;
        this.arg$2 = tL_updateLangPack;
    }

    public void run() {
        this.arg$1.lambda$processUpdateArray$229$MessagesController(this.arg$2);
    }
}
