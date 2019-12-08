package org.telegram.messenger;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TonController$y5gHwqWFihVH-4vITInmi9Ihzbk implements Runnable {
    private final /* synthetic */ TonController f$0;

    public /* synthetic */ -$$Lambda$TonController$y5gHwqWFihVH-4vITInmi9Ihzbk(TonController tonController) {
        this.f$0 = tonController;
    }

    public final void run() {
        this.f$0.runShortPolling();
    }
}
