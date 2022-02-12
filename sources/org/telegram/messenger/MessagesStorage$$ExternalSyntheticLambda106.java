package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$InputPeer;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda106 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ int f$4;
    public final /* synthetic */ boolean f$5;
    public final /* synthetic */ TLRPC$InputPeer f$6;
    public final /* synthetic */ long f$7;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda106(MessagesStorage messagesStorage, long j, boolean z, int i, int i2, boolean z2, TLRPC$InputPeer tLRPC$InputPeer, long j2) {
        this.f$0 = messagesStorage;
        this.f$1 = j;
        this.f$2 = z;
        this.f$3 = i;
        this.f$4 = i2;
        this.f$5 = z2;
        this.f$6 = tLRPC$InputPeer;
        this.f$7 = j2;
    }

    public final void run() {
        this.f$0.lambda$loadPendingTasks$25(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
    }
}
