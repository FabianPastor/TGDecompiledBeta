package org.telegram.tgnet;

import org.telegram.messenger.AccountInstance;

public final /* synthetic */ class ConnectionsManager$$ExternalSyntheticLambda6 implements Runnable {
    public final /* synthetic */ int f$0;
    public final /* synthetic */ TLRPC$TL_config f$1;

    public /* synthetic */ ConnectionsManager$$ExternalSyntheticLambda6(int i, TLRPC$TL_config tLRPC$TL_config) {
        this.f$0 = i;
        this.f$1 = tLRPC$TL_config;
    }

    public final void run() {
        AccountInstance.getInstance(this.f$0).getMessagesController().updateConfig(this.f$1);
    }
}
