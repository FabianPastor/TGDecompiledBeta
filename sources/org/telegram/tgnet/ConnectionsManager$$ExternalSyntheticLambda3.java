package org.telegram.tgnet;

public final /* synthetic */ class ConnectionsManager$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ int f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ ConnectionsManager$$ExternalSyntheticLambda3(int i, int i2) {
        this.f$0 = i;
        this.f$1 = i2;
    }

    public final void run() {
        ConnectionsManager.lambda$onConnectionStateChanged$6(this.f$0, this.f$1);
    }
}
