package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_updates_channelDifferenceTooLong;

final /* synthetic */ class MessagesStorage$$Lambda$75 implements Runnable {
    private final MessagesStorage arg$1;
    private final int arg$2;
    private final int arg$3;
    private final TL_updates_channelDifferenceTooLong arg$4;

    MessagesStorage$$Lambda$75(MessagesStorage messagesStorage, int i, int i2, TL_updates_channelDifferenceTooLong tL_updates_channelDifferenceTooLong) {
        this.arg$1 = messagesStorage;
        this.arg$2 = i;
        this.arg$3 = i2;
        this.arg$4 = tL_updates_channelDifferenceTooLong;
    }

    public void run() {
        this.arg$1.lambda$overwriteChannel$102$MessagesStorage(this.arg$2, this.arg$3, this.arg$4);
    }
}
