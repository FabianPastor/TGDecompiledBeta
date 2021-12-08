package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda160 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ TLRPC.TL_chatBannedRights f$3;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda160(MessagesStorage messagesStorage, long j, int i, TLRPC.TL_chatBannedRights tL_chatBannedRights) {
        this.f$0 = messagesStorage;
        this.f$1 = j;
        this.f$2 = i;
        this.f$3 = tL_chatBannedRights;
    }

    public final void run() {
        this.f$0.m1061x3f3de42f(this.f$1, this.f$2, this.f$3);
    }
}
