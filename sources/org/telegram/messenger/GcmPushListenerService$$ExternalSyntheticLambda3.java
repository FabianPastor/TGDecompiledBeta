package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class GcmPushListenerService$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ int f$0;
    public final /* synthetic */ TLRPC.TL_updates f$1;

    public /* synthetic */ GcmPushListenerService$$ExternalSyntheticLambda3(int i, TLRPC.TL_updates tL_updates) {
        this.f$0 = i;
        this.f$1 = tL_updates;
    }

    public final void run() {
        MessagesController.getInstance(this.f$0).processUpdates(this.f$1, false);
    }
}
