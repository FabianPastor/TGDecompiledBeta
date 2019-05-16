package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.InputChannel;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$veltQ-QzWYSSmgAGGDUTY-jvHoM implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ long f$3;
    private final /* synthetic */ InputChannel f$4;

    public /* synthetic */ -$$Lambda$MessagesStorage$veltQ-QzWYSSmgAGGDUTY-jvHoM(MessagesStorage messagesStorage, int i, int i2, long j, InputChannel inputChannel) {
        this.f$0 = messagesStorage;
        this.f$1 = i;
        this.f$2 = i2;
        this.f$3 = j;
        this.f$4 = inputChannel;
    }

    public final void run() {
        this.f$0.lambda$null$12$MessagesStorage(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
