package org.telegram.messenger;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$DdUqoRuNHd8tYSzSROwgs2f7oFw implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ boolean f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ long f$3;

    public /* synthetic */ -$$Lambda$MessagesStorage$DdUqoRuNHd8tYSzSROwgs2f7oFw(MessagesStorage messagesStorage, boolean z, int i, long j) {
        this.f$0 = messagesStorage;
        this.f$1 = z;
        this.f$2 = i;
        this.f$3 = j;
    }

    public final void run() {
        this.f$0.lambda$removeFromDownloadQueue$112$MessagesStorage(this.f$1, this.f$2, this.f$3);
    }
}