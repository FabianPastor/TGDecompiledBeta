package org.telegram.messenger;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$GcmPushListenerService$aCTiHH_b4RhBMfcCkAHdgReKcg4 implements Runnable {
    private final /* synthetic */ int f$0;

    public /* synthetic */ -$$Lambda$GcmPushListenerService$aCTiHH_b4RhBMfcCkAHdgReKcg4(int i) {
        this.f$0 = i;
    }

    public final void run() {
        LocationController.getInstance(this.f$0).setNewLocationEndWatchTime();
    }
}
