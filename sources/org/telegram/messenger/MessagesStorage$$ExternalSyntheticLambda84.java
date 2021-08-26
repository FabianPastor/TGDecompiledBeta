package org.telegram.messenger;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda84 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ Long f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ int f$4;
    public final /* synthetic */ int f$5;
    public final /* synthetic */ int f$6;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda84(MessagesStorage messagesStorage, long j, Long l, int i, int i2, int i3, int i4) {
        this.f$0 = messagesStorage;
        this.f$1 = j;
        this.f$2 = l;
        this.f$3 = i;
        this.f$4 = i2;
        this.f$5 = i3;
        this.f$6 = i4;
    }

    public final void run() {
        this.f$0.lambda$updateMessageStateAndId$150(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
