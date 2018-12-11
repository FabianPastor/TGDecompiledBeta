package org.telegram.tgnet;

import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC.TL_config;

final /* synthetic */ class ConnectionsManager$$Lambda$8 implements Runnable {
    private final int arg$1;
    private final TL_config arg$2;

    ConnectionsManager$$Lambda$8(int i, TL_config tL_config) {
        this.arg$1 = i;
        this.arg$2 = tL_config;
    }

    public void run() {
        MessagesController.getInstance(this.arg$1).updateConfig(this.arg$2);
    }
}
