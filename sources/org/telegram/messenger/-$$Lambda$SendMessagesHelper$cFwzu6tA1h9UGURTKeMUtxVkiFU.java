package org.telegram.messenger;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$cFwzu6tA1h9UGURTKeMUtxVkiFU implements Runnable {
    private final /* synthetic */ String f$0;
    private final /* synthetic */ AccountInstance f$1;
    private final /* synthetic */ long f$2;
    private final /* synthetic */ boolean f$3;
    private final /* synthetic */ int f$4;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$cFwzu6tA1h9UGURTKeMUtxVkiFU(String str, AccountInstance accountInstance, long j, boolean z, int i) {
        this.f$0 = str;
        this.f$1 = accountInstance;
        this.f$2 = j;
        this.f$3 = z;
        this.f$4 = i;
    }

    public final void run() {
        AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$1r7G8fot9gV0CAIfftBjzBAyj0g(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4));
    }
}
