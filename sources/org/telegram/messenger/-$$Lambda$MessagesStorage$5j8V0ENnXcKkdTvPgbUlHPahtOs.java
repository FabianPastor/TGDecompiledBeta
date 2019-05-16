package org.telegram.messenger;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$5j8V0ENnXcKkdTvPgbUlHPahtOs implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ int f$1;

    public /* synthetic */ -$$Lambda$MessagesStorage$5j8V0ENnXcKkdTvPgbUlHPahtOs(MessagesStorage messagesStorage, int i) {
        this.f$0 = messagesStorage;
        this.f$1 = i;
    }

    public final void run() {
        this.f$0.lambda$deleteBlockedUser$37$MessagesStorage(this.f$1);
    }
}
