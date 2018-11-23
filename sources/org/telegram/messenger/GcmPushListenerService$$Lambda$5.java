package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_updates;

final /* synthetic */ class GcmPushListenerService$$Lambda$5 implements Runnable {
    private final int arg$1;
    private final TL_updates arg$2;

    GcmPushListenerService$$Lambda$5(int i, TL_updates tL_updates) {
        this.arg$1 = i;
        this.arg$2 = tL_updates;
    }

    public void run() {
        MessagesController.getInstance(this.arg$1).processUpdates(this.arg$2, false);
    }
}
