package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda169 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ TLRPC.InputChannel f$3;
    public final /* synthetic */ int f$4;
    public final /* synthetic */ long f$5;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda169(MessagesStorage messagesStorage, long j, int i, TLRPC.InputChannel inputChannel, int i2, long j2) {
        this.f$0 = messagesStorage;
        this.f$1 = j;
        this.f$2 = i;
        this.f$3 = inputChannel;
        this.f$4 = i2;
        this.f$5 = j2;
    }

    public final void run() {
        this.f$0.m924xd2b302ee(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
