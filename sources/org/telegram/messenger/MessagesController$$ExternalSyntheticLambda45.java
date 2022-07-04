package org.telegram.messenger;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda45 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ int f$3;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda45(MessagesController messagesController, int i, long j, int i2) {
        this.f$0 = messagesController;
        this.f$1 = i;
        this.f$2 = j;
        this.f$3 = i2;
    }

    public final void run() {
        this.f$0.lambda$sendTyping$143(this.f$1, this.f$2, this.f$3);
    }
}
