package org.telegram.messenger;

import org.telegram.tgnet.TLObject;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda84 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ TLObject f$3;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda84(MessagesStorage messagesStorage, long j, long j2, TLObject tLObject) {
        this.f$0 = messagesStorage;
        this.f$1 = j;
        this.f$2 = j2;
        this.f$3 = tLObject;
    }

    public final void run() {
        this.f$0.lambda$loadPendingTasks$29(this.f$1, this.f$2, this.f$3);
    }
}
