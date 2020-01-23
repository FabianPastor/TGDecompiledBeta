package org.telegram.messenger;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$Qscy6zJwgYgJry4HRY-Be40Q4Ug implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ Long f$1;
    private final /* synthetic */ int f$2;

    public /* synthetic */ -$$Lambda$MessagesStorage$Qscy6zJwgYgJry4HRY-Be40Q4Ug(MessagesStorage messagesStorage, Long l, int i) {
        this.f$0 = messagesStorage;
        this.f$1 = l;
        this.f$2 = i;
    }

    public final void run() {
        this.f$0.lambda$broadcastScheduledMessagesChange$129$MessagesStorage(this.f$1, this.f$2);
    }
}
