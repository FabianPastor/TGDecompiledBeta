package org.telegram.messenger;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$7B7zYr_TXDhl6iWXwpWKnHe2Ihk implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ long f$1;

    public /* synthetic */ -$$Lambda$MessagesStorage$7B7zYr_TXDhl6iWXwpWKnHe2Ihk(MessagesStorage messagesStorage, long j) {
        this.f$0 = messagesStorage;
        this.f$1 = j;
    }

    public final void run() {
        this.f$0.lambda$onDeleteQueryComplete$40$MessagesStorage(this.f$1);
    }
}
