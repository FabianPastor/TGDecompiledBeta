package org.telegram.messenger;

public final /* synthetic */ class PushListenerController$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ int f$0;

    public /* synthetic */ PushListenerController$$ExternalSyntheticLambda0(int i) {
        this.f$0 = i;
    }

    public final void run() {
        LocationController.getInstance(this.f$0).setNewLocationEndWatchTime();
    }
}
