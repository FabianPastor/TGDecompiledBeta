package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$Dialog;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda54 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ TLRPC$Dialog f$2;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda54(MessagesController messagesController, int i, TLRPC$Dialog tLRPC$Dialog) {
        this.f$0 = messagesController;
        this.f$1 = i;
        this.f$2 = tLRPC$Dialog;
    }

    public final void run() {
        this.f$0.lambda$checkLastDialogMessage$174(this.f$1, this.f$2);
    }
}
