package org.telegram.messenger;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$bLsQ9Zez-Ef0tQJtoMR54iM-bHA implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ long f$1;

    public /* synthetic */ -$$Lambda$MessagesStorage$bLsQ9Zez-Ef0tQJtoMR54iM-bHA(MessagesStorage messagesStorage, long j) {
        this.f$0 = messagesStorage;
        this.f$1 = j;
    }

    public final void run() {
        this.f$0.lambda$removePendingTask$7$MessagesStorage(this.f$1);
    }
}
