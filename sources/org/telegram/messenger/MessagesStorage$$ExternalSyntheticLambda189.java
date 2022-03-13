package org.telegram.messenger;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda189 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ long f$3;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda189(MessagesStorage messagesStorage, boolean z, int i, long j) {
        this.f$0 = messagesStorage;
        this.f$1 = z;
        this.f$2 = i;
        this.f$3 = j;
    }

    public final void run() {
        this.f$0.lambda$removeFromDownloadQueue$148(this.f$1, this.f$2, this.f$3);
    }
}
