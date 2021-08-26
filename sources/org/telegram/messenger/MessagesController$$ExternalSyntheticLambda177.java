package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$updates_Difference;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda177 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC$updates_Difference f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ int f$3;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda177(MessagesController messagesController, TLRPC$updates_Difference tLRPC$updates_Difference, int i, int i2) {
        this.f$0 = messagesController;
        this.f$1 = tLRPC$updates_Difference;
        this.f$2 = i;
        this.f$3 = i2;
    }

    public final void run() {
        this.f$0.lambda$getDifference$254(this.f$1, this.f$2, this.f$3);
    }
}
