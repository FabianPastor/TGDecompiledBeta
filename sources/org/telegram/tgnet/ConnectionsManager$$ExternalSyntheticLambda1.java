package org.telegram.tgnet;

import org.telegram.messenger.AccountInstance;

public final /* synthetic */ class ConnectionsManager$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ int f$0;

    public /* synthetic */ ConnectionsManager$$ExternalSyntheticLambda1(int i) {
        this.f$0 = i;
    }

    public final void run() {
        AccountInstance.getInstance(this.f$0).getMessagesController().updateTimerProc();
    }
}