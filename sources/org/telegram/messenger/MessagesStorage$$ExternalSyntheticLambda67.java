package org.telegram.messenger;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda67 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ long f$1;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda67(MessagesStorage messagesStorage, long j) {
        this.f$0 = messagesStorage;
        this.f$1 = j;
    }

    public final void run() {
        this.f$0.lambda$onDeleteQueryComplete$70(this.f$1);
    }
}
