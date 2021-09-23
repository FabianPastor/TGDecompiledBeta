package org.telegram.messenger;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda28 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda28(MessagesStorage messagesStorage, int i) {
        this.f$0 = messagesStorage;
        this.f$1 = i;
    }

    public final void run() {
        this.f$0.lambda$clearDownloadQueue$139(this.f$1);
    }
}
