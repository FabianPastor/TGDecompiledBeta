package org.telegram.messenger;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda115 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ Long f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda115(MessagesStorage messagesStorage, Long l, int i) {
        this.f$0 = messagesStorage;
        this.f$1 = l;
        this.f$2 = i;
    }

    public final void run() {
        this.f$0.lambda$broadcastScheduledMessagesChange$161(this.f$1, this.f$2);
    }
}
