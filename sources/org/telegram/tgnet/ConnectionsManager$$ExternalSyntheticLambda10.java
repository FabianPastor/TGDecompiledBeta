package org.telegram.tgnet;

import org.telegram.messenger.AccountInstance;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ConnectionsManager$$ExternalSyntheticLambda10 implements Runnable {
    public final /* synthetic */ int f$0;
    public final /* synthetic */ TLRPC.TL_config f$1;

    public /* synthetic */ ConnectionsManager$$ExternalSyntheticLambda10(int i, TLRPC.TL_config tL_config) {
        this.f$0 = i;
        this.f$1 = tL_config;
    }

    public final void run() {
        AccountInstance.getInstance(this.f$0).getMessagesController().updateConfig(this.f$1);
    }
}
