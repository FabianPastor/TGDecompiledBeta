package org.telegram.messenger;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$PTdkE5WlMN7BYGypHn90wyIYfSU implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ boolean f$1;
    private final /* synthetic */ boolean f$2;
    private final /* synthetic */ long f$3;

    public /* synthetic */ -$$Lambda$MessagesController$PTdkE5WlMN7BYGypHn90wyIYfSU(MessagesController messagesController, boolean z, boolean z2, long j) {
        this.f$0 = messagesController;
        this.f$1 = z;
        this.f$2 = z2;
        this.f$3 = j;
    }

    public final void run() {
        this.f$0.lambda$setLastCreatedDialogId$19$MessagesController(this.f$1, this.f$2, this.f$3);
    }
}
