package org.telegram.messenger;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda154 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ MessageObject f$1;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda154(MessagesStorage messagesStorage, MessageObject messageObject) {
        this.f$0 = messagesStorage;
        this.f$1 = messageObject;
    }

    public final void run() {
        this.f$0.lambda$onDownloadComplete$41(this.f$1);
    }
}
