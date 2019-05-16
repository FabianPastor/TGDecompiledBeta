package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.InputPeer;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$gZU7uqq34_u60ckwe65AH41ydVA implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ long f$1;
    private final /* synthetic */ boolean f$2;
    private final /* synthetic */ int f$3;
    private final /* synthetic */ int f$4;
    private final /* synthetic */ boolean f$5;
    private final /* synthetic */ InputPeer f$6;
    private final /* synthetic */ long f$7;

    public /* synthetic */ -$$Lambda$MessagesStorage$gZU7uqq34_u60ckwe65AH41ydVA(MessagesStorage messagesStorage, long j, boolean z, int i, int i2, boolean z2, InputPeer inputPeer, long j2) {
        this.f$0 = messagesStorage;
        this.f$1 = j;
        this.f$2 = z;
        this.f$3 = i;
        this.f$4 = i2;
        this.f$5 = z2;
        this.f$6 = inputPeer;
        this.f$7 = j2;
    }

    public final void run() {
        this.f$0.lambda$null$17$MessagesStorage(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
    }
}
