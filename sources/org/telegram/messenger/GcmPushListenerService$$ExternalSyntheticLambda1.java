package org.telegram.messenger;

public final /* synthetic */ class GcmPushListenerService$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ int f$0;

    public /* synthetic */ GcmPushListenerService$$ExternalSyntheticLambda1(int i) {
        this.f$0 = i;
    }

    public final void run() {
        LocationController.getInstance(this.f$0).setNewLocationEndWatchTime();
    }
}
