package org.telegram.messenger;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda57 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ long f$1;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda57(MessagesStorage messagesStorage, long j) {
        this.f$0 = messagesStorage;
        this.f$1 = j;
    }

    public final void run() {
        this.f$0.lambda$removePendingTask$9(this.f$1);
    }
}
