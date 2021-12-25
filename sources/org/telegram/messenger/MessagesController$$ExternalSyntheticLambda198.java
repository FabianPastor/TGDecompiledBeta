package org.telegram.messenger;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda198 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ long f$3;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda198(MessagesController messagesController, boolean z, boolean z2, long j) {
        this.f$0 = messagesController;
        this.f$1 = z;
        this.f$2 = z2;
        this.f$3 = j;
    }

    public final void run() {
        this.f$0.lambda$setLastCreatedDialogId$30(this.f$1, this.f$2, this.f$3);
    }
}
