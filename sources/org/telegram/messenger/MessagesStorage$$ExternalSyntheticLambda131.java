package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda131 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ TLRPC.TL_messageReactions f$3;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda131(MessagesStorage messagesStorage, int i, long j, TLRPC.TL_messageReactions tL_messageReactions) {
        this.f$0 = messagesStorage;
        this.f$1 = i;
        this.f$2 = j;
        this.f$3 = tL_messageReactions;
    }

    public final void run() {
        this.f$0.m1078xaCLASSNAMECLASSNAME(this.f$1, this.f$2, this.f$3);
    }
}
