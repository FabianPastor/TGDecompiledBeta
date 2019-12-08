package org.telegram.messenger;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$N-HedNDC9ih6Ti-SnWELePO5WG8 implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ boolean f$1;
    private final /* synthetic */ long f$2;

    public /* synthetic */ -$$Lambda$MessagesController$N-HedNDC9ih6Ti-SnWELePO5WG8(MessagesController messagesController, boolean z, long j) {
        this.f$0 = messagesController;
        this.f$1 = z;
        this.f$2 = j;
    }

    public final void run() {
        this.f$0.lambda$setLastCreatedDialogId$13$MessagesController(this.f$1, this.f$2);
    }
}
