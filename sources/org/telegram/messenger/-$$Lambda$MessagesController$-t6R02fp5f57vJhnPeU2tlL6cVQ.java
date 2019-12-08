package org.telegram.messenger;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$-t6R02fp5var_vJhnPeU2tlL6cVQ implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ long f$1;
    private final /* synthetic */ boolean f$2;
    private final /* synthetic */ int f$3;
    private final /* synthetic */ int f$4;

    public /* synthetic */ -$$Lambda$MessagesController$-t6R02fp5var_vJhnPeU2tlL6cVQ(MessagesController messagesController, long j, boolean z, int i, int i2) {
        this.f$0 = messagesController;
        this.f$1 = j;
        this.f$2 = z;
        this.f$3 = i;
        this.f$4 = i2;
    }

    public final void run() {
        this.f$0.lambda$markDialogAsRead$156$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
