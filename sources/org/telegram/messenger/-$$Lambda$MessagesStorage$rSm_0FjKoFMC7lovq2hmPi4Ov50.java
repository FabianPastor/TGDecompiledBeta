package org.telegram.messenger;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$rSm_0FjKoFMC7lovq2hmPi4Ov50 implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ int f$2;

    public /* synthetic */ -$$Lambda$MessagesStorage$rSm_0FjKoFMC7lovq2hmPi4Ov50(MessagesStorage messagesStorage, int i, int i2) {
        this.f$0 = messagesStorage;
        this.f$1 = i;
        this.f$2 = i2;
    }

    public final void run() {
        this.f$0.lambda$deleteUserChannelHistory$41$MessagesStorage(this.f$1, this.f$2);
    }
}
