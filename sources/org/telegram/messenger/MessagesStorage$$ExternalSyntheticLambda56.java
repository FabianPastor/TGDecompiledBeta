package org.telegram.messenger;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda56 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ long f$3;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda56(MessagesStorage messagesStorage, int i, boolean z, long j) {
        this.f$0 = messagesStorage;
        this.f$1 = i;
        this.f$2 = z;
        this.f$3 = j;
    }

    public final void run() {
        this.f$0.lambda$loadPendingTasks$31(this.f$1, this.f$2, this.f$3);
    }
}
