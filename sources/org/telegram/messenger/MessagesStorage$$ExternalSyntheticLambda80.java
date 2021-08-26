package org.telegram.messenger;

import org.telegram.tgnet.TLObject;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda80 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ long f$3;
    public final /* synthetic */ TLObject f$4;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda80(MessagesStorage messagesStorage, long j, int i, long j2, TLObject tLObject) {
        this.f$0 = messagesStorage;
        this.f$1 = j;
        this.f$2 = i;
        this.f$3 = j2;
        this.f$4 = tLObject;
    }

    public final void run() {
        this.f$0.lambda$loadPendingTasks$21(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
