package org.telegram.messenger;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$anvUg9BPgXzy3G26SbyYlksatN4 implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ long f$1;
    private final /* synthetic */ Integer f$2;
    private final /* synthetic */ int f$3;
    private final /* synthetic */ int f$4;
    private final /* synthetic */ int f$5;
    private final /* synthetic */ int f$6;

    public /* synthetic */ -$$Lambda$MessagesStorage$anvUg9BPgXzy3G26SbyYlksatN4(MessagesStorage messagesStorage, long j, Integer num, int i, int i2, int i3, int i4) {
        this.f$0 = messagesStorage;
        this.f$1 = j;
        this.f$2 = num;
        this.f$3 = i;
        this.f$4 = i2;
        this.f$5 = i3;
        this.f$6 = i4;
    }

    public final void run() {
        this.f$0.lambda$updateMessageStateAndId$123$MessagesStorage(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
