package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$updates_Difference;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda178 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC$updates_Difference f$1;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda178(MessagesController messagesController, TLRPC$updates_Difference tLRPC$updates_Difference) {
        this.f$0 = messagesController;
        this.f$1 = tLRPC$updates_Difference;
    }

    public final void run() {
        this.f$0.lambda$getDifference$255(this.f$1);
    }
}