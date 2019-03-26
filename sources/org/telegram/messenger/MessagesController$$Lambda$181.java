package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.updates_ChannelDifference;

final /* synthetic */ class MessagesController$$Lambda$181 implements Runnable {
    private final MessagesController arg$1;
    private final updates_ChannelDifference arg$2;

    MessagesController$$Lambda$181(MessagesController messagesController, updates_ChannelDifference updates_channeldifference) {
        this.arg$1 = messagesController;
        this.arg$2 = updates_channeldifference;
    }

    public void run() {
        this.arg$1.lambda$null$195$MessagesController(this.arg$2);
    }
}
