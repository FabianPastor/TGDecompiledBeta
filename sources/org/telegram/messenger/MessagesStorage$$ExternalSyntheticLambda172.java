package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda172 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ TLRPC.TL_updates_channelDifferenceTooLong f$3;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda172(MessagesStorage messagesStorage, long j, int i, TLRPC.TL_updates_channelDifferenceTooLong tL_updates_channelDifferenceTooLong) {
        this.f$0 = messagesStorage;
        this.f$1 = j;
        this.f$2 = i;
        this.f$3 = tL_updates_channelDifferenceTooLong;
    }

    public final void run() {
        this.f$0.m955x427e2d50(this.f$1, this.f$2, this.f$3);
    }
}
