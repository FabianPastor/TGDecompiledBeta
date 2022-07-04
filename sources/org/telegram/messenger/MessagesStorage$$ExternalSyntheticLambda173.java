package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda173 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ long f$3;
    public final /* synthetic */ TLRPC.InputChannel f$4;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda173(MessagesStorage messagesStorage, long j, int i, long j2, TLRPC.InputChannel inputChannel) {
        this.f$0 = messagesStorage;
        this.f$1 = j;
        this.f$2 = i;
        this.f$3 = j2;
        this.f$4 = inputChannel;
    }

    public final void run() {
        this.f$0.m2200xf9a88CLASSNAME(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
