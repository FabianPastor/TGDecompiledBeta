package org.telegram.messenger;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$M6hqucUGJrTL18vYRO931fjYJmM implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ long f$2;

    public /* synthetic */ -$$Lambda$MessagesStorage$M6hqucUGJrTL18vYRO931fjYJmM(MessagesStorage messagesStorage, int i, long j) {
        this.f$0 = messagesStorage;
        this.f$1 = i;
        this.f$2 = j;
    }

    public final void run() {
        this.f$0.lambda$resetMentionsCount$58$MessagesStorage(this.f$1, this.f$2);
    }
}