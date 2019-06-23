package org.telegram.messenger;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AutoMessageHeardReceiver$R9C-O9TfhsmJTCeCbR268b8rTs8 implements Runnable {
    private final /* synthetic */ AccountInstance f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ long f$3;
    private final /* synthetic */ int f$4;

    public /* synthetic */ -$$Lambda$AutoMessageHeardReceiver$R9C-O9TfhsmJTCeCbR268b8rTs8(AccountInstance accountInstance, int i, int i2, long j, int i3) {
        this.f$0 = accountInstance;
        this.f$1 = i;
        this.f$2 = i2;
        this.f$3 = j;
        this.f$4 = i3;
    }

    public final void run() {
        AndroidUtilities.runOnUIThread(new -$$Lambda$AutoMessageHeardReceiver$5gHjgkav0Vsi6hdGsimdfmozlgg(this.f$0, this.f$0.getMessagesStorage().getChatSync(-this.f$1), this.f$2, this.f$3, this.f$4));
    }
}
