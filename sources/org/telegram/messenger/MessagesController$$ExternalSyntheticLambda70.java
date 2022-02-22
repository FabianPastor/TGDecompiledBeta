package org.telegram.messenger;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda70 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ boolean f$3;
    public final /* synthetic */ int f$4;
    public final /* synthetic */ int f$5;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda70(MessagesController messagesController, long j, int i, boolean z, int i2, int i3) {
        this.f$0 = messagesController;
        this.f$1 = j;
        this.f$2 = i;
        this.f$3 = z;
        this.f$4 = i2;
        this.f$5 = i3;
    }

    public final void run() {
        this.f$0.lambda$markDialogAsRead$200(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
