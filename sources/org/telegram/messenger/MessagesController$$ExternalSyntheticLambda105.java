package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda105 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC.updates_Difference f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ int f$3;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda105(MessagesController messagesController, TLRPC.updates_Difference updates_difference, int i, int i2) {
        this.f$0 = messagesController;
        this.f$1 = updates_difference;
        this.f$2 = i;
        this.f$3 = i2;
    }

    public final void run() {
        this.f$0.m218x5e153785(this.f$1, this.f$2, this.f$3);
    }
}
