package org.telegram.messenger;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda103 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ boolean f$2;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda103(MessagesStorage messagesStorage, long j, boolean z) {
        this.f$0 = messagesStorage;
        this.f$1 = j;
        this.f$2 = z;
    }

    public final void run() {
        this.f$0.lambda$setDialogUnread$179(this.f$1, this.f$2);
    }
}