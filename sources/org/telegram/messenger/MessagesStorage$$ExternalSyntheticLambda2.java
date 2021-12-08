package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ TLRPC.TL_updates_channelDifferenceTooLong f$2;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda2(MessagesStorage messagesStorage, long j, TLRPC.TL_updates_channelDifferenceTooLong tL_updates_channelDifferenceTooLong) {
        this.f$0 = messagesStorage;
        this.f$1 = j;
        this.f$2 = tL_updates_channelDifferenceTooLong;
    }

    public final void run() {
        this.f$0.m1014xe60669d4(this.f$1, this.f$2);
    }
}
