package org.telegram.messenger;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda75 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ boolean f$3;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda75(MessagesStorage messagesStorage, long j, int i, boolean z) {
        this.f$0 = messagesStorage;
        this.f$1 = j;
        this.f$2 = i;
        this.f$3 = z;
    }

    public final void run() {
        this.f$0.lambda$markMessagesAsDeleted$169(this.f$1, this.f$2, this.f$3);
    }
}
