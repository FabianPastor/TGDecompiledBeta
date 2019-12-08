package org.telegram.messenger;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$A7k4sc1a2ORoZ72BrFZX2UvSDew implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ long f$1;

    public /* synthetic */ -$$Lambda$MessagesStorage$A7k4sc1a2ORoZ72BrFZX2UvSDew(MessagesStorage messagesStorage, long j) {
        this.f$0 = messagesStorage;
        this.f$1 = j;
    }

    public final void run() {
        this.f$0.lambda$onDeleteQueryComplete$44$MessagesStorage(this.f$1);
    }
}
