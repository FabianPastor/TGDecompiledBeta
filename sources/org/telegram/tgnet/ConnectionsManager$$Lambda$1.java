package org.telegram.tgnet;

import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC.Updates;

final /* synthetic */ class ConnectionsManager$$Lambda$1 implements Runnable {
    private final int arg$1;
    private final TLObject arg$2;

    ConnectionsManager$$Lambda$1(int i, TLObject tLObject) {
        this.arg$1 = i;
        this.arg$2 = tLObject;
    }

    public void run() {
        MessagesController.getInstance(this.arg$1).processUpdates((Updates) this.arg$2, false);
    }
}
