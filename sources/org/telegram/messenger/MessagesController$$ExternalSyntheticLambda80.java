package org.telegram.messenger;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda80 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ String f$2;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda80(MessagesController messagesController, long j, String str) {
        this.f$0 = messagesController;
        this.f$1 = j;
        this.f$2 = str;
    }

    public final void run() {
        this.f$0.lambda$updateChannelUserName$239(this.f$1, this.f$2);
    }
}
