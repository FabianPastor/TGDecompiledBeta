package org.telegram.messenger;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$WearReplyReceiver$k_jOC9_QMpSfUO4oGXuf4RKbrYI implements Runnable {
    private final /* synthetic */ WearReplyReceiver f$0;
    private final /* synthetic */ AccountInstance f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ CharSequence f$3;
    private final /* synthetic */ long f$4;
    private final /* synthetic */ int f$5;

    public /* synthetic */ -$$Lambda$WearReplyReceiver$k_jOC9_QMpSfUO4oGXuf4RKbrYI(WearReplyReceiver wearReplyReceiver, AccountInstance accountInstance, int i, CharSequence charSequence, long j, int i2) {
        this.f$0 = wearReplyReceiver;
        this.f$1 = accountInstance;
        this.f$2 = i;
        this.f$3 = charSequence;
        this.f$4 = j;
        this.f$5 = i2;
    }

    public final void run() {
        this.f$0.lambda$onReceive$3$WearReplyReceiver(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
