package org.telegram.messenger;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$tfio61Jg7EwU_lYu8vz8qjAm11M implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ int f$3;

    public /* synthetic */ -$$Lambda$MessagesStorage$tfio61Jg7EwU_lYu8vz8qjAm11M(MessagesStorage messagesStorage, int i, int i2, int i3) {
        this.f$0 = messagesStorage;
        this.f$1 = i;
        this.f$2 = i2;
        this.f$3 = i3;
    }

    public final void run() {
        this.f$0.lambda$setMessageSeq$121$MessagesStorage(this.f$1, this.f$2, this.f$3);
    }
}
