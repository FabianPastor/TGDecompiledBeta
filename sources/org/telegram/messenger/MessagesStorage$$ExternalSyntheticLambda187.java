package org.telegram.messenger;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda187 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ int f$3;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda187(MessagesStorage messagesStorage, boolean z, long j, int i) {
        this.f$0 = messagesStorage;
        this.f$1 = z;
        this.f$2 = j;
        this.f$3 = i;
    }

    public final void run() {
        this.f$0.lambda$updateDialogUnreadReactions$202(this.f$1, this.f$2, this.f$3);
    }
}
