package org.telegram.messenger;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$exFD4MGKbWHHI2LvO4ZsOCDDjmg implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ long f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ int f$3;
    private final /* synthetic */ boolean f$4;
    private final /* synthetic */ boolean f$5;

    public /* synthetic */ -$$Lambda$MessagesController$exFD4MGKbWHHI2LvO4ZsOCDDjmg(MessagesController messagesController, long j, int i, int i2, boolean z, boolean z2) {
        this.f$0 = messagesController;
        this.f$1 = j;
        this.f$2 = i;
        this.f$3 = i2;
        this.f$4 = z;
        this.f$5 = z2;
    }

    public final void run() {
        this.f$0.lambda$markDialogAsRead$166$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
