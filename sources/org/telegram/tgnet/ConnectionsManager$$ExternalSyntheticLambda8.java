package org.telegram.tgnet;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.Utilities;

public final /* synthetic */ class ConnectionsManager$$ExternalSyntheticLambda8 implements Runnable {
    public final /* synthetic */ int f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ ConnectionsManager$$ExternalSyntheticLambda8(int i, int i2) {
        this.f$0 = i;
        this.f$1 = i2;
    }

    public final void run() {
        Utilities.stageQueue.postRunnable(new ConnectionsManager$$ExternalSyntheticLambda11(this.f$0, ApplicationLoader.isNetworkOnline(), this.f$1));
    }
}
