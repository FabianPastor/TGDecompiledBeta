package org.telegram.messenger;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$uZgJttXUWPBitEWEPJJQPgaQByA implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ long f$1;
    private final /* synthetic */ long f$2;

    public /* synthetic */ -$$Lambda$MessagesStorage$uZgJttXUWPBitEWEPJJQPgaQByA(MessagesStorage messagesStorage, long j, long j2) {
        this.f$0 = messagesStorage;
        this.f$1 = j;
        this.f$2 = j2;
    }

    public final void run() {
        this.f$0.lambda$setDialogFlags$24$MessagesStorage(this.f$1, this.f$2);
    }
}