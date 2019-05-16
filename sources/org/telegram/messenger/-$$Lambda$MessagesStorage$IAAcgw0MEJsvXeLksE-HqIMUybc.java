package org.telegram.messenger;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$IAAcgw0MEJsvXeLksE-HqIMUybc implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ int f$1;

    public /* synthetic */ -$$Lambda$MessagesStorage$IAAcgw0MEJsvXeLksE-HqIMUybc(MessagesStorage messagesStorage, int i) {
        this.f$0 = messagesStorage;
        this.f$1 = i;
    }

    public final void run() {
        this.f$0.lambda$clearWebRecent$36$MessagesStorage(this.f$1);
    }
}
