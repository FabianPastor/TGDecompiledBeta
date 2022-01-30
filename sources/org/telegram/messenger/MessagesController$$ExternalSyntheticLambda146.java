package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$Dialog;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda146 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC$Dialog f$1;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda146(MessagesController messagesController, TLRPC$Dialog tLRPC$Dialog) {
        this.f$0 = messagesController;
        this.f$1 = tLRPC$Dialog;
    }

    public final void run() {
        this.f$0.lambda$checkLastDialogMessage$180(this.f$1);
    }
}
