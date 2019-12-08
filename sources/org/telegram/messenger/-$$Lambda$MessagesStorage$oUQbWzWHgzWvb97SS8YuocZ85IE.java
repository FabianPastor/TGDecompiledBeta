package org.telegram.messenger;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$oUQbWzWHgzWvb97SS8YuocZ85IE implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ long f$3;

    public /* synthetic */ -$$Lambda$MessagesStorage$oUQbWzWHgzWvb97SS8YuocZ85IE(MessagesStorage messagesStorage, int i, int i2, long j) {
        this.f$0 = messagesStorage;
        this.f$1 = i;
        this.f$2 = i2;
        this.f$3 = j;
    }

    public final void run() {
        this.f$0.lambda$markMentionMessageAsRead$57$MessagesStorage(this.f$1, this.f$2, this.f$3);
    }
}
