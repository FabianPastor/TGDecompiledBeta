package org.telegram.messenger;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda88 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ Runnable f$2;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda88(MessagesStorage messagesStorage, long j, Runnable runnable) {
        this.f$0 = messagesStorage;
        this.f$1 = j;
        this.f$2 = runnable;
    }

    public final void run() {
        this.f$0.lambda$isDialogHasTopMessage$134(this.f$1, this.f$2);
    }
}
