package org.telegram.messenger;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda37 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ long f$2;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda37(MessagesStorage messagesStorage, int i, long j) {
        this.f$0 = messagesStorage;
        this.f$1 = i;
        this.f$2 = j;
    }

    public final void run() {
        this.f$0.lambda$saveChannelPts$30(this.f$1, this.f$2);
    }
}