package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.InputChannel;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$TqvcAvYPfh-Cio-d66SYceSFjMQ implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ InputChannel f$3;
    private final /* synthetic */ int f$4;
    private final /* synthetic */ long f$5;

    public /* synthetic */ -$$Lambda$MessagesStorage$TqvcAvYPfh-Cio-d66SYceSFjMQ(MessagesStorage messagesStorage, int i, int i2, InputChannel inputChannel, int i3, long j) {
        this.f$0 = messagesStorage;
        this.f$1 = i;
        this.f$2 = i2;
        this.f$3 = inputChannel;
        this.f$4 = i3;
        this.f$5 = j;
    }

    public final void run() {
        this.f$0.lambda$null$15$MessagesStorage(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
