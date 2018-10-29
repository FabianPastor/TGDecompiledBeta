package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_help_termsOfServiceUpdate;

final /* synthetic */ class MessagesController$$Lambda$223 implements Runnable {
    private final MessagesController arg$1;
    private final TL_help_termsOfServiceUpdate arg$2;

    MessagesController$$Lambda$223(MessagesController messagesController, TL_help_termsOfServiceUpdate tL_help_termsOfServiceUpdate) {
        this.arg$1 = messagesController;
        this.arg$2 = tL_help_termsOfServiceUpdate;
    }

    public void run() {
        this.arg$1.lambda$null$71$MessagesController(this.arg$2);
    }
}
