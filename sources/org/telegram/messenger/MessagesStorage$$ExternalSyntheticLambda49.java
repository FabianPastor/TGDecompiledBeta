package org.telegram.messenger;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda49 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ boolean f$3;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda49(MessagesStorage messagesStorage, int i, int i2, boolean z) {
        this.f$0 = messagesStorage;
        this.f$1 = i;
        this.f$2 = i2;
        this.f$3 = z;
    }

    public final void run() {
        this.f$0.lambda$markMessagesAsDeleted$161(this.f$1, this.f$2, this.f$3);
    }
}
