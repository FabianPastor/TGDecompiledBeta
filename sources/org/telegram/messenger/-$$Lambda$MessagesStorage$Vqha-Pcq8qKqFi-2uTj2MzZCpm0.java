package org.telegram.messenger;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$Vqha-Pcq8qKqFi-2uTj2MzZCpm0 implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ long f$1;

    public /* synthetic */ -$$Lambda$MessagesStorage$Vqha-Pcq8qKqFi-2uTj2MzZCpm0(MessagesStorage messagesStorage, long j) {
        this.f$0 = messagesStorage;
        this.f$1 = j;
    }

    public final void run() {
        this.f$0.lambda$markMessageAsMention$54$MessagesStorage(this.f$1);
    }
}