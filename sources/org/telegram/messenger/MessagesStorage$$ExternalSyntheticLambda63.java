package org.telegram.messenger;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda63 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda63(MessagesStorage messagesStorage, long j, int i) {
        this.f$0 = messagesStorage;
        this.f$1 = j;
        this.f$2 = i;
    }

    public final void run() {
        this.f$0.lambda$resetMentionsCount$78(this.f$1, this.f$2);
    }
}
